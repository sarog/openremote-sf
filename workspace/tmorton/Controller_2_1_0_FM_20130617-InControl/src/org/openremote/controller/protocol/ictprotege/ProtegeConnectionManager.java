/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import org.openremote.controller.exception.ConnectionException;
import org.openremote.controller.utils.Logger;

/**
 *
 * @author Tomas
 */
public class ProtegeConnectionManager implements Runnable
{
    private final static Logger log = Logger.getLogger(ProtegeCommandBuilder.PROTEGE_LOG_CATEGORY);
    
    private String address;
    private int port;
    private int encryptionType;
    private int checksumType;
    private Socket socket;
    private BufferedInputStream inputStream; 
    private BufferedOutputStream outputStream;//Possibly wrap with DataOutputStream?  Seems unnecesary for this.
    private boolean stopRequested;
    
    public ProtegeConnectionManager(String address, int port, int encryptionType, int checksumType)
    {
        this.address = address;
        this.port = port;
        this.encryptionType = encryptionType;
        this.checksumType = checksumType;
        initiate();
    }
    
    private void initiate()
    {
        //set up encryption and other settings
        connectToController();
    }
    
    @Override
    public void run()
    {
        listenForInput();
        disconnect();
    }    
    
    private void disconnect()
    {
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
    
    public void send(ProtegePacket packet) 
            throws ConnectionException
    {       
        //parse the params and create a command String
        //open connection to the controller (or use existing?)
        connectToController();
        //login if not already logged in
        checkAuthorization();
        try {
            //send packet
            outputStream.write(packet.getPacket());
        } catch (IOException ex) {
                log.error("Error sending command '" + packet.getCommandType().name() +
                        "' to controller.");
        }
            //wait for acknowledge this will be caught on the other thread?
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
        }
        
        try {
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            log.error("Cannot connect to the Protege Controller at '" +
                    address + ":" + port);
            
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
                while (!packetInitiated)
                {
                    byte received = (byte) inputStream.read();
                    if (received == ProtegePacket.HEADER_LOW)
                    {
                        received = (byte) inputStream.read();
                        if (received == ProtegePacket.HEADER_HIGH)
                        {
                            packetInitiated = true;
                        }
                    }
                }
                //Have received a valid packet header, now read              
                byte packetLengthLow = (byte) inputStream.read();
                byte packetLengthHigh = (byte) inputStream.read();
                int packetLength = packetLengthLow + packetLengthHigh; //TODO this is probably wrong.. HEX convert
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
                processPacket(input);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ProtegeConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * TODO Should ***NOT*** be executed in the read thread!!!
     * 
     * @param input 
     */
    private void processPacket(byte[] input)
    {
        //check the checksum first
        boolean packetValid = false;
        switch (checksumType)
        {
            case ProtegeSystemConstants.CHECKSUM_8 :
                packetValid = validateChecksum8Bit(input);
                break;
                
            case ProtegeSystemConstants.CHECKSUM_16 :
                packetValid = validateChecksum16Bit(input);
                break;
            
            case ProtegeSystemConstants.CHECKSUM_NONE :
            default :
                packetValid = true;
        }
        switch (encryptionType)
        {
            case ProtegeSystemConstants.ENCRYPTION_AES_128 :
                input = decryptAES128(input);
                break;
                
            case ProtegeSystemConstants.ENCRYPTION_AES_192 :
                input = decryptAES192(input);
                break;
                
            case ProtegeSystemConstants.ENCRYPTION_AES_256 :
                input = decryptAES256(input);
                break;
                
            case ProtegeSystemConstants.ENCRYPTION_NONE :
            default :
                //no decryption required
        }
        //Decide what packet it is
        //TODO update these values with constants / enums 
        switch (input[0])
        {
            case 0x00 :
                    readCommandPacket(input);
                break;
                
            case 0x01 :
                    readDataPacket(input);
                break;
                
            case 0x02 :
                    readSystemPacket(input);
                break;
            
            default :
                log.warn("Unknown packet type received: '" + Byte.toString(input[0]) + "'.");
        }
    }
    
    private boolean validateChecksum8Bit(byte[] packet)
    {
        //sum all bytes before the checksum bytes
        byte sum = 0;
        for (int i = 0; i < packet.length - 2; i++)
        {
            sum += packet[i];
        }
        return packet[packet.length - 2] == sum;
    }
    
    /**
     * TODO implement
     * @param packet
     * @return 
     */
    private boolean validateChecksum16Bit(byte[] packet)
    {
        return true;
    }
    
    private byte[] decryptAES128(byte[] packet)
    {
        return packet;
    }
    
    private byte[] decryptAES192(byte[] packet)
    {
        return packet;
    }
    
    private byte[] decryptAES256(byte[] packet)
    {
        return packet;
    }
    
    private void readCommandPacket(byte[] packet)
    {
        //Doesn't make sense to receive one of these?
    }
    
    /**
     * Process a response from the Protege Controller.
     * It is possible to receive multiple data packets within
     * a single message, so the sub method 
     * <code>processDataPacket</code> handles each
     * of these data packets.
     * Note that this will read and attempt to process the message terminator.
     */
    private void readDataPacket(byte[] message)
    {
        int currentByte = 4; //skip the headers
        while (currentByte < message.length)
        {
            //read each, increment after
            byte dataTypeLow = message[currentByte++];
            byte dataTypeHigh = message[currentByte++];
            byte dataLength = message[currentByte++];            
            byte[] dataPacket = new byte[dataLength];
            for (int i = 0; i < dataLength; i++)
            {
                dataPacket[i] = message[i + currentByte];
            }
            //Could use an if statement to ignore the packet terminator
            processDataPacket(ProtegeDataType.getDataType(dataTypeLow, dataTypeHigh), dataPacket);
        }
    }
    
    private void processDataPacket(ProtegeDataType dataType, byte[] dataPacket)
    {
        //analyze packet
        switch (dataType)
        {
            case PANEL_SERIAL_NUMBER :
                String serialNumber = getSerialNumber(dataPacket); //4 Bytes
                break;
                
            case PANEL_HARDWARE_VERSION :
                String hardwareVersion = getSerialNumber(dataPacket); //1 Byte
                break;
                
            case FIRMWARE_TYPE :
                String firmwareType = getSerialNumber(dataPacket); //2 Bytes
                break;
                
            case FIRMWARE_VERSION :
                String firmwareVersion = getSerialNumber(dataPacket); //2 Bytes
                break;
                
            case FIRMWARE_BUILD :
                String firmwareBuild = getSerialNumber(dataPacket); //2 Bytes
                break;
                
            case DOOR_STATUS :
                //4 bytes for record index
                //1 byte door lock state
                ProtegeDataState lockState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_LOCK_STATE, dataPacket[4]);
                //1 byte door state
                ProtegeDataState doorState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_DOOR_STATE, dataPacket[5]);
                //2 bytes reserved
                break;
                
            case AREA_STATUS :
                //4 bytes for record index
                //1 byte area state
                ProtegeDataState areaState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_AREA_STATE, dataPacket[4]);
                //1 byte area tamper state
                ProtegeDataState areaTamperState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_AREA_TAMPER_STATE, dataPacket[5]);
                //1 byte for area state flag(s)                
                ProtegeDataState areaStateFlags = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_AREA_STATE_FLAG, dataPacket[6]);
                //1 byte reserved
                break;
                
            case OUTPUT_STATUS :
                //4 bytes for record index
                //8 bytes for ASCII value
                //1 byte output state
                ProtegeDataState outputState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_OUTPUT_STATE, dataPacket[12]);               
                //3 bytes reserved
                break;
                
            case INPUT_STATUS :
                //4 bytes for record index
                //8 bytes for ASCII value
                //1 byte input state
                ProtegeDataState inputState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_INPUT_STATE, dataPacket[12]); 
                //1 byte input bypass state
                ProtegeDataState inputBypassState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_INPUT_BYPASS_STATE, dataPacket[13]);               
                //2 bytes reserved
                break;                
                
            case VARIABLE_STATUS :
                //4 bytes for record index
                //2 bytes variable value                
                int variableValue = getVariableValue(dataPacket);
                //2 bytes reserved
                break;              
                
            case SYSTEM_EVENT_NUMERICAL :
                //2 bytes event code
                //6 bytes event data               
                //TODO implement numerical events
                throw new UnsupportedOperationException("Numerical events not yet implemented");                
                //break;          
                
            case SYSTEM_EVENT_ASCII :
                //TODO implement numerical events
                String event = getASCIIEvent(dataPacket);
                throw new UnsupportedOperationException("Numerical events not yet implemented");                
                //break;
                
        }
        //create event
        
        //send ACK to Protege Controller
        
    }
    
    /**
     * 4 Bytes
     * @param dataPacket
     * @return 
     */
    private String getSerialNumber(byte[] dataPacket)
    {
        String serialNumber = "";
        try {
            serialNumber = new String(dataPacket, "UTF-8"); //TODO I don't think this will work
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(ProtegeConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return serialNumber;
    }
    
    /**
     * TODO make this work properly.
     * @param dataPacket
     * @return 
     */
    private int getVariableValue(byte[] dataPacket)
    {
        byte valueLow = dataPacket[4];
        byte valueHigh = dataPacket[5];
        
        return ((int) valueLow) + ((int) valueHigh);
    }
    
    /**
     * TODO test this works.
     * @param dataPacket
     * @return 
     */
    private String getASCIIEvent(byte[] dataPacket)
    {
        String result = ""; 
        try {
            result = new String(dataPacket, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(ProtegeConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.substring(2, result.length() - 1);
    }
    
    //http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    //inefficent
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
           sb.append(String.format("%02x", b&0xff));
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
    private void readSystemPacket(byte[] packet)
    {
        
    }
    
    public void requestStop()
    {
        stopRequested = true;
    }
}
