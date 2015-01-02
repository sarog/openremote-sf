/* 
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.ictprotege.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.ictprotege.ChecksumType;
import org.openremote.controller.protocol.ictprotege.EncryptionType;
import org.openremote.controller.protocol.ictprotege.ProtegeCommandBuilder;
import org.openremote.controller.protocol.ictprotege.ProtegeCommandType;
import org.openremote.controller.protocol.ictprotege.ProtegeDataType;
import org.openremote.controller.protocol.ictprotege.ProtegeEventHandler;
import org.openremote.controller.protocol.ictprotege.ProtegePacket;
import org.openremote.controller.protocol.ictprotege.ProtegeRecordType;
import org.openremote.controller.protocol.ictprotege.ProtegeUtils;
import org.openremote.controller.utils.Logger;

/**
 * Handles network communication with the ICT Protege controller.
 *
 * @author Tomas Morton
 * @author Adam Mcnabb
 * @author Damon Williams
 */
public class ProtegeConnectionManager implements Runnable
{

    private static final Logger log = ProtegeUtils.log;
    //Constructor initialized values
    private final String address;
    private final int port;
    private final EncryptionType encryptionType;
    private final String encryptionKey;
    private final ChecksumType checksumType;
    private final String monitoringPIN;
    private String userPIN;
    private final ProtegePacket pollPacket;
    private final ProtegePacket logoutPacket;
    private final int pollTime;
    private final ProtegeTCPWriter tcpWriter;
    private final ICTAES crypto;

    //Flags    
    private boolean stopRequested;
    private boolean loggedIn;
    private boolean isConnected;
    private boolean pollAcknowledged;
    private boolean loginSent;

    private Socket socket;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;
    private final Map<ProtegeDataType, Map<Integer, Sensor>> sensors;
    private List<ProtegePacket> monitoringPackets;
public boolean superDebugMode = false;

    public ProtegeConnectionManager(String address, int port, EncryptionType encryptionType, String encryptionKey, ChecksumType checksumType, String PIN)
    {
        this.pollTime = 30000;
        this.address = address;
        this.port = port;
        this.encryptionType = encryptionType;
        this.encryptionKey = encryptionKey;
        this.checksumType = checksumType;
        this.sensors = new HashMap<ProtegeDataType, Map<Integer, Sensor>>();
        createDataTypeMaps();
        this.monitoringPackets = new ArrayList<ProtegePacket>();
        this.monitoringPIN = PIN;
        this.userPIN = "";
        //Set up and run threads.
        this.pollPacket = createPollPacket();
        this.logoutPacket = createLogoutPacket();
        this.tcpWriter = new ProtegeTCPWriter(this);
        this.crypto = new ICTAES(encryptionKey, encryptionType);
    }

    public void sendSuperDebugPoll()
    {
//        tcpWriter.sendPacketNowUnencrypted(createPollPacket());
    }
    
    public boolean superDebugMode()
    {
//        return superDebugMode;
        return false;
    }
    
    private void createDataTypeMaps()
    {
        for (ProtegeDataType type : ProtegeDataType.values())
        {
            Map<Integer, Sensor> map = new HashMap<Integer, Sensor>();
            sensors.put(type, map);
        }
    }

    @Override
    public void run()
    {
        //Start reader and writer threads
        new Thread(tcpWriter, "ProtegeTCPWriter").start();
        new Thread(new ProtegeTCPReader(this), "ProtegeTCPReader").start();
        while (!stopRequested)
        {
            //If polls are not answered (or start of thread), reconnect
            if (!pollAcknowledged)
            {
                isConnected = false;
                connect();
                login();
                synchronized (this)
                {
                    log.debug("Waking other threads after reconnect.");
                    this.notifyAll(); //tell threads that connection is established
                }
            }
            //While receiving packets wait
            while (pollAcknowledged && isConnected)
            {
                updateConnectionSensor("Connected");
                pollAcknowledged = false;
                waitForPoll();
            }
            //If no reply has been received, send a poll
            int missedPolls = 1;
            do
            {
                send(pollPacket);
                waitForPoll();
            }
            while (!pollAcknowledged && missedPolls < 5 && isConnected);
        }
        requestStop();
        disconnect();
    }

    /**
     * Pauses the thread for the specified poll time.
     *
     */
    private void waitForPoll()
    {
        synchronized (this)
        {
            try
            {
                this.wait(pollTime);
            }
            catch (InterruptedException ex)
            {
            }
        }
    }

    private void login()
    {
        if (!userPIN.isEmpty())
        {
            login(userPIN);
        }
        else if (!monitoringPIN.isEmpty())
        {
            login(monitoringPIN);
        }
    }

    /**
     * Stops the ConnectionManager and releases resources.
     *
     */
    private void disconnect()
    {
        try
        {
            try
            {
                outputStream.close();
                inputStream.close();
            }
            catch (IOException ex)
            {
            }
            try
            {
                socket.close();
            }
            catch (IOException ex)
            {
            }
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Sends a packet to the Protege Controller. Note: all login packets will
     * first send a logout.
     *
     * @param packet
     */
    public void send(ProtegePacket packet)
    {
        if (loggedIn && packet.getCommandType() == ProtegeCommandType.SEND_LOGIN)
        {
            ProtegePacket logoutPacket = createLogoutPacket(); //Do not use the cached packet as we need high priority here
            logoutPacket.getCommandType().setPriority(ProtegeCommandType.PRIORITY_HIGHEST);
            tcpWriter.sendPacket(logoutPacket);
            loginSent = true;
            updatePINSensor("Sent Login");
            
        }
        tcpWriter.sendPacket(packet);
    }
    
    /**
     * Sets up a connection with the Protege Controller.
     *
     */
    private void connect()
    {
        disconnect();
        updateConnectionSensor("Disconnected");
        log.debug("Connecting to Protege controller.");
        while (!connectToController())
        {
            try
            {
                sleep(300);
            }
            catch (InterruptedException ex)
            {
            }
        }
        log.info("Established connection with Protege controller.");
        synchronized (this)
        {
            log.debug("Waking other threads after reconnect.");
            this.notifyAll();
        }
        isConnected = true;
        updateConnectionSensor("Connected");

    }

    /**
     * Sub-method of connect() which makes a single attempt to open a socket to
     * the Protege controller.
     *
     * @return
     */
    private boolean connectToController()
    {
        boolean result = true;
        try
        {
            socket = new Socket(address, port);
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());
        }
        catch (IOException ex)
        {
            result = false;
        }
        return result && socket != null;
    }

    /**
     * Sends a login packet to the Protege controller using the monitoring PIN
     * set in the XML file.
     *
     */
    public void loginMonitoring()
    {
        login(monitoringPIN);
    }

    /**
     * Sends a login packet to the controller using the specified PIN.
     *
     * @param PIN
     */
    private void login(String PIN)
    {
        if (!stopRequested)
        {
            Map<String, String> loginMap = new HashMap<String, String>();
            loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "SEND_LOGIN");
            loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE, PIN);
            ProtegePacket loginPacket = new ProtegePacket(loginMap, encryptionType, checksumType);

            send(loginPacket);
        }

    }

    /**
     * Sends monitoring request packets to the Protege controller.
     *
     */
    private void requestStatusUpdates()
    {
        log.debug("Requesting status updates:");
        for (int i = 0; i < monitoringPackets.size(); i++)
        {
            send(monitoringPackets.get(i));
        }
    }

    public void notifyLoggedOut()
    {
        setLoggedOut();
        login();
    }

    /**
     * Requests the listener thread to stop.
     *
     */
    public void requestStop()
    {
        stopRequested = true;
    }

    public EncryptionType getEncryptionType()
    {
        return encryptionType;
    }

    public ChecksumType getChecksumType()
    {
        return checksumType;
    }

    public String getEncryptionKey()
    {
        return encryptionKey;
    }

    /**
     * Returns all currently registered sensors.
     *
     * @return
     */
    public Map<ProtegeDataType, Map<Integer, Sensor>> getSensors()
    {
        return sensors;
    }

    /**
     * Adds a sensor for asynchronous monitoring.
     *
     * @param sensor
     * @param packet
     */
    public void setSensor(Sensor sensor, ProtegePacket packet)
    {
        String recordType = packet.getRecordValue();
        if (recordType == null)
        {
            recordType = packet.getRecordType().toString();
        }
        byte recordTypeByte = ProtegeRecordType.valueOf(recordType).getValue();
        ProtegeDataType dataType = ProtegeDataType.getDataType((byte) 0x00, recordTypeByte);
        HashMap<Integer, Sensor> map = (HashMap) sensors.get(dataType);
        //Check if the command has an index or if is a OR controller status monitor
        int index = packet.getRecordID();
        if (index == -1)
        {
            if (packet.getCommandType() == ProtegeCommandType.SYSTEM_REQUEST_EVENTS)
            {
                index = map.size();
            } else
            {
                index = packet.getCommandType().getValue();
            }
        }
        map.put(index, sensor);
        log.debug("Adding sensor: " + packet.getRecordType()
                + " with RecordID: " + index + " to " + dataType + ".");
        if (packet.getRecordType() != ProtegeRecordType.CONFIG)
        {
            monitoringPackets.add(packet);
            send(packet);            
            if (packet.getCommandType() == ProtegeCommandType.SYSTEM_REQUEST_EVENTS)
            {
                Map<Integer, Sensor> eventSensors = sensors.get(ProtegeDataType.PANEL_SERIAL_NUMBER); //As there are no parameters with the event packet, it has been stored here.  
                ProtegeEventHandler.getInstance().setSensors(eventSensors);  
            }
        } 
    }

    /**
     * Removes a sensor from asynchronous monitoring. Monitoring will not stop
     * until the next logout or login.
     *
     * @param index
     * @param packet
     */
    public void removeSensor(ProtegePacket packet)
    {
        HashMap<Integer, Sensor> sensorMap = (HashMap) sensors.get(
                ProtegeDataType.getDataType((byte) 0x00, packet.getRecordType().getValue()));
        try
        {
            log.debug("Removing sensor: " + sensorMap.get(packet.getRecordID()).getName() + " from bin " + sensorMap.toString());
            sensorMap.remove(packet.getRecordID());
            if (packet.getCommandType() == ProtegeCommandType.SYSTEM_REQUEST_EVENTS)
            {
                Map<Integer, Sensor> eventSensors = sensors.get(ProtegeDataType.PANEL_SERIAL_NUMBER); //As there are no parameters with the event packet, it has been stored here.  
                if (eventSensors == null || eventSensors.isEmpty())
                {
                    ProtegeEventHandler.requestStop();
                }
            }
        } catch (NullPointerException e) {}
        try
        {
            monitoringPackets.remove(packet);
        } catch (NullPointerException e){}
    }

    /**
     * Logout the user.
     *
     */
    private void logout()
    {
        send(logoutPacket);
    }

    private ProtegePacket createLogoutPacket()
    {
        Map<String, String> logoutMap = new HashMap<String, String>();
        logoutMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "SEND_LOGOUT");
        return new ProtegePacket(logoutMap, encryptionType, checksumType);
    }

    private ProtegePacket createPollPacket()
    {
        Map<String, String> pollMap = new HashMap<String, String>();
        pollMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "SYSTEM_POLL");
        return new ProtegePacket(pollMap, encryptionType, checksumType);
    }

    /**
     * The check sum is a single byte which is the sum of all preceding bytes,
     * modulo 256.
     *
     */
    public static byte[] calcChecksum8Bit(byte[] packet)
    {
        int sum = 0;
        for (int b : packet)
        {
            sum += b;
        }
        sum %= 256;
        return new byte[]
        {
            (byte) sum
        };
    }

    public static byte[] calcChecksum16Bit(byte[] packet)
    {        
        throw new UnsupportedOperationException("16 bit checksum has not been enabled.  Please use 8 bit.");
    }
    
    public Socket getSocket()
    {
        return socket;
    }

    public void notifyConnectionLost()
    {
        isConnected = false;
        updateConnectionSensor("Disconnected");
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void notifyAck()
    {
        pollAcknowledged = true;
        ProtegePacket ackedPacket = tcpWriter.getLastPacketSent();
        processAckedPacket(ackedPacket);
        tcpWriter.notifyPacketReceived();
        synchronized (this)
        {
            log.debug("Waking other threads after Ack.");
            notifyAll(); //Tell the TCP Writer that a packet has been acknowledged.
        }
    }

    public void notifyInvalidPin()
    {
        userPIN = "";
        updatePINSensor("Invalid PIN");
    }
    
    /**
     * Controls any tasks that need to be executed after successfully sending a
     * type of packet.
     *
     * @param ackedPacket ProtegePacket that has been acknowledged by the
     *                    Protege controller.
     */
    private void processAckedPacket(ProtegePacket ackedPacket)
    {
        switch (ackedPacket.getCommandType())
        {
            case SEND_LOGIN:
                if (ackedPacket.getRecordValue().equals(monitoringPIN))
                {
                    setLoggedIn(false);
                } else
                {
                    setLoggedIn(true);
                    updatePINSensor("Welcome");                    
                }
                requestStatusUpdates();
                break;
            case SEND_LOGOUT:
                setLoggedOut();
                userPIN = "";
                requestStatusUpdates();
                break;
        }
    }

    public BufferedInputStream getInputStream()
    {
        return inputStream;
    }

    public BufferedOutputStream getOutputStream()
    {
        return outputStream;
    }

    public void processControllerCommand(ProtegePacket packet)
    {
        switch (packet.getCommandType())
        {
            case PIN_DIGIT:
                //check for re-entering of PIN
                if (loginSent)
                {
                    this.userPIN = "";                    
                    loginSent = false;
                }
                this.userPIN += packet.getRecordValue();
                //Update users PIN sensor with another *
                updatePINSensor();
                break;
            case CLEAR_LOGIN:
                this.userPIN = "";
                updatePINSensor();
                logout();
                break;
            case MONITOR_LOGIN_STATUS:
                setLoggedIn(false); //Update sensor's status
                break;
            case MONITOR_CONNECTION_STATUS:
                updateConnectionSensor("Connected");
                break;
            case MONITOR_QUEUED_COMMANDS:
                updateQueueSensor("0");
            case MONITOR_PIN_DISPLAY:
                updatePINSensor();
            default:
        }
    }

    public String getUserPin()
    {
        return userPIN;
    }

    public ProtegePacket getCurrentPacket()
    {
        return tcpWriter.getLastPacketSent();
    }

    private void setLoggedOut()
    {        
        this.loggedIn = false;
    
        HashMap<Integer, Sensor> map = (HashMap) sensors.get(ProtegeDataType.CONTROLLER_STATUS);
        Sensor s = map.get((int) ProtegeCommandType.MONITOR_LOGIN_STATUS.getValue());
        if (s != null)
        {
            s.update("Logged out");
        }
    }
    
    private void setLoggedIn(boolean isUser)
    {
        this.loggedIn = true;

        HashMap<Integer, Sensor> map = (HashMap) sensors.get(ProtegeDataType.CONTROLLER_STATUS);
        Sensor s = map.get((int) ProtegeCommandType.MONITOR_LOGIN_STATUS.getValue());
        if (s != null)
        {
            if (isUser)
            {
                s.update("User");
            } else
            {
                s.update("Monitoring");
            }
        }
    }

    private void updateConnectionSensor(String status)
    {
        HashMap<Integer, Sensor> map = (HashMap) sensors.get(ProtegeDataType.CONTROLLER_STATUS);
        Sensor s = map.get((int) ProtegeCommandType.MONITOR_CONNECTION_STATUS.getValue());
        if (s != null)
        {
            s.update(status);
        }
    }

    public void updateQueueSensor(String status)
    {
        HashMap<Integer, Sensor> map = (HashMap) sensors.get(ProtegeDataType.CONTROLLER_STATUS);
        Sensor s = map.get((int) ProtegeCommandType.MONITOR_QUEUED_COMMANDS.getValue());
        if (s != null)
        {
            s.update(status);
        }
    }
    
    //NOTE that " " is NOT a space, it is an Em Quad character.  Empty/Space/Tab/New Line are crashing the android app.
    private static final String[] PIN_STATUSES = {" ", "*", "**", "***", "****", "*****", "******"};
    
    private void updatePINSensor()
    {
        if (userPIN.length() < 6)
        {
            updatePINSensor(PIN_STATUSES[userPIN.length()]);
        } else
        {
            updatePINSensor(PIN_STATUSES[6]);
        }
    }
    
    private void updatePINSensor(String status)
    {     
        HashMap<Integer, Sensor> map = (HashMap) sensors.get(ProtegeDataType.CONTROLLER_STATUS);
        Sensor s = map.get((int) ProtegeCommandType.MONITOR_PIN_DISPLAY.getValue());
        if (s != null)
        {
            s.update(status);
        }  
    }
    
    public ICTAES getCrypto()
    {
        return crypto;
    }
}
