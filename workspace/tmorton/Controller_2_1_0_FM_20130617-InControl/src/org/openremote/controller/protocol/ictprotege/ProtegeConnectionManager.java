/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.openremote.controller.exception.ConnectionException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;

/**
 *
 * @author Tomas
 */
public class ProtegeConnectionManager implements Runnable
{
    private static Logger log = ProtegeSystemConstants.log;
    
    private String address;
    private int port;
    private int encryptionType;
    private int checksumType;
    private String PIN;
    private Socket socket;
    private BufferedInputStream inputStream; 
    private BufferedOutputStream outputStream;//Possibly wrap with DataOutputStream?  Seems unnecesary for this.
    private boolean stopRequested;
    private Map<String, Sensor> sensors;
    private boolean loggedIn;
    
    public ProtegeConnectionManager(String address, int port, int encryptionType, int checksumType, String PIN)
    {
        log.setLevel(Level.ALL);
        this.address = address;
        this.port = port;
        this.encryptionType = encryptionType;
        this.checksumType = checksumType;
        this.sensors = new HashMap<>();
        this.PIN = PIN;
        initiate();
    }
    
    private void initiate()
    {
        //set up encryption and other settings
        connectToController();
        
    }
    
    private void registerListener(String ID, Sensor sensor)
    {
        sensors.put(ID, sensor);
    }
    
    @Override
    public void run()
    {
        log.error("Starting listener thread");
        listenForInput();
        disconnect();
    }    
    
    /**
     * Stops the ConnectionManager and releases resources.
     */
    private void disconnect()
    {
        requestStop();
        try {
            outputStream.close();
        } catch (IOException ex) {}
        try {
            inputStream.close();
        } catch (IOException ex) {}
        try {
            socket.close();
        } catch (IOException ex) {}
    }
    
    /**
     * Sends a packet to the Protege Controller.
     * 
     * @param packet
     * @throws ConnectionException 
     */
    public void send(ProtegePacket packet) 
            throws ConnectionException
    {       
        log.error("Attempting to send Protege packet " + packet.getCommandType().name());
        //open connection to the controller (or use existing?)
        connectToController();
        if (!loggedIn)
        {
            login();
        }
        log.error("Socket / In / Out:" + (socket == null) + (inputStream == null)
                + (outputStream == null));
        //login if not already logged in
        if (checkAuthorization())
        {
            try {
                //send packet
                log.error("Writing packet to stream: " + packet.toString());
                outputStream.write(packet.getPacket());
                outputStream.flush();
            } catch (IOException ex) {
                    log.error("Error sending command '" + packet.getCommandType().name() +
                            "' to controller.");
            }
            //wait for acknowledge (butthis will be caught on the other thread?)
        }
        loggedIn = false; //TODO remove; for test
    }    
    
    /**
     * Sets up a connection with the Protege Controller.
     */
    private void connectToController()
    {
        if (socket == null || socket.isClosed())
        {
            try {
                socket = new Socket(address, port);
            } catch (IOException e) {
                log.error("Cannot connect to the Protege Controller at '" +
                        address + ":" + port);
            }
            try {
                inputStream = new BufferedInputStream(socket.getInputStream());
                outputStream = new BufferedOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                log.error("Cannot connect to the Protege Controller at '" +
                        address + ":" + port);

            }
        }        
    }    
    private void login()
    {
        Map loginMap = new HashMap<>();
        loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_TYPE, "SYSTEM");
        loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "SYSTEM_LOGIN");
        loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN, PIN);
        ProtegePacket packet = new ProtegePacket(loginMap, encryptionType, checksumType);
        try {
            loggedIn = true;
            send(packet);
        } catch (ConnectionException e) {
            log.error("Failed to login: " + e);
        }
    }
    /**
     * Checks if logged in.
     * @return 
     */
    private boolean checkAuthorization()
    {
        return true;
    }
    
    private void listenForInput()
    {
        while (!stopRequested)
        {
            connectToController();
            try {
                //TODO check for a better way to do this
                boolean packetInitiated = false;
                do
                {
                    byte received = (byte) inputStream.read();
                    log.error("Received byte: " + received);
                    if (received == ProtegePacket.HEADER_LOW)
                    {
                        received = (byte) inputStream.read();
                        if (received == ProtegePacket.HEADER_HIGH)
                        {
                            packetInitiated = true;
                        }
                    }
                } while (!packetInitiated);
                log.error("ICT Packet found");
                //Have received a valid packet header, now read              
                byte packetLengthLow = (byte) inputStream.read();
                byte packetLengthHigh = (byte) inputStream.read();
                int packetLength = packetLengthLow + packetLengthHigh;
                //Set up the array to store the packet and put in received values for checksums
                byte[] input = new byte[packetLength];
                input[0] = ProtegePacket.HEADER_LOW;
                input[1] = ProtegePacket.HEADER_HIGH;
                input[2] = packetLengthLow;
                input[3] = packetLengthHigh;
                int bytesRead = 4;
                int received;
                do 
                {
                    received = inputStream.read(input, bytesRead, packetLength - bytesRead);
                    bytesRead += received;
                } while (socket != null && socket.isConnected() && bytesRead < packetLength && received != -1); //TODO confirm the -1 check won't mess up on slow connection
                //TODO move this into an event broadcast so that we don't lock up the listener thread
                log.error("Received packet:");
                for(byte b : input)
                {
                    log.error(Byte.toString(b));
                }
                log.error("Received packet printed");
                ProtegePacketHandler handler = new ProtegePacketHandler(this, input);
                new Thread(handler, "ProtegePacketHandler").start();
            } catch (IOException e) {
                log.error(("Protege network error: " + e));
            }
        }
    }
    
   
    
    //http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    //inefficent
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
           sb.append(String.format("%02X", b&0xff));
        return sb.toString();
     }
    //efficient
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }    
    
    public void requestStop()
    {
        stopRequested = true;
    }
    
    public static int byteArrayToInt(byte[] input)
    {
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }

    public int getEncryptionType()
    {
        return encryptionType;
    }

    public int getChecksumType()
    {
        return checksumType;
    }

    public Map<String, Sensor> getSensors()
    {
        return sensors;
    }
    
    
}
