/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.openremote.controller.model.sensor.Sensor;
import static org.openremote.controller.protocol.ictprotege.ProtegeConnectionManager.byteArrayToInt;
import org.openremote.controller.utils.Logger;

/**
 *
 * @author Tomas
 */
public class ProtegePacketHandler implements Runnable
{
    private static Logger log = ProtegeSystemConstants.log;
    private ProtegeConnectionManager connectionManager;
    private Map<String, Sensor> sensors;
    private byte[] packet;

    public ProtegePacketHandler(ProtegeConnectionManager connectionManager, byte[] packet)
    {
        this.connectionManager = connectionManager;
        this.packet = packet;
        this.sensors = connectionManager.getSensors();
    }
    
    @Override
    public void run()
    {
        processPacket();
    }
    
     /**
     * Process a received packet and forward it to the sensors.
     * 
     */
    private void processPacket()
    {
        //check the checksum first
        boolean packetValid;
        switch (connectionManager.getChecksumType())
        {
            case ProtegeSystemConstants.CHECKSUM_8 :
                packetValid = validateChecksum8Bit(packet);
                break;
                
            case ProtegeSystemConstants.CHECKSUM_16 :
                packetValid = validateChecksum16Bit(packet);
                break;
            
            case ProtegeSystemConstants.CHECKSUM_NONE :
            default :
                packetValid = true;
        }
        if (packetValid)
        {
            switch (connectionManager.getEncryptionType())
            {
                case ProtegeSystemConstants.ENCRYPTION_AES_128 :
                    packet = decryptAES128(packet);
                    break;

                case ProtegeSystemConstants.ENCRYPTION_AES_192 :
                    packet = decryptAES192(packet);
                    break;

                case ProtegeSystemConstants.ENCRYPTION_AES_256 :
                    packet = decryptAES256(packet);
                    break;

                case ProtegeSystemConstants.ENCRYPTION_NONE :
                default :
                    //no decryption required
            }
            //Decide what packet it is
            //TODO update these values with constants / enums 
            switch (packet[4])
            {
                case ProtegeSystemConstants.PACKET_COMMAND :
                    log.error("Reading command packet.");
                        readCommandPacket(packet);
                    break;

                case ProtegeSystemConstants.PACKET_DATA :
                        log.error("Reading data packet.");
                        readDataPacket(packet);
                    break;

                case (byte) ProtegeSystemConstants.PACKET_SYSTEM :
                        log.error("Reading system packet.");
                        readSystemPacket(packet);
                    break;

                default :
                    log.error("Unknown packet type received: '" + Byte.toString(packet[4]) + "'.");
            }
        }
    }
    
    private boolean validateChecksum8Bit(byte[] packet)
    {
        //sum all bytes before the checksum bytes
        byte sum = 0;
        for (int i = 0; i < packet.length - 1; i++) //or -2?
        {
            sum += packet[i];
        }
        return packet[packet.length - 1] == sum;
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

    
//******************************
//*                            *
//*   Command Packet Methods   *
//*                            *
//******************************
    
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
        int currentByte = 6; //skip the headers
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
            currentByte += dataLength;
        }
    }

    
//***************************
//*                         *
//*   Data Packet Methods   *
//*                         *
//***************************
    
    private void processDataPacket(ProtegeDataType dataType, byte[] dataPacket)
    {        
        log.error("Reading packet type: " + dataType.name());
        List<ProtegeDataState> dataStates = new ArrayList<>();
        Sensor sensor = null;
        //analyze packet
        switch (dataType)
        {
                 
            case DOOR_STATUS :
                //4 bytes for record index
                int index = byteArrayToInt(new byte[] {dataPacket[0], 
                        dataPacket[1], dataPacket[2], dataPacket[3]});
                //1 byte door lock state
                ProtegeDataState lockState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_LOCK_STATE, dataPacket[4]);
                //1 byte door state
                ProtegeDataState doorState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_DOOR_STATE, dataPacket[5]);
                //2 bytes reserved
                sensor = sensors.get(dataType.name() + index);                
                dataStates.add(lockState);
                dataStates.add(doorState);
                break;
                
            case AREA_STATUS :
                //4 bytes for record index
                index = byteArrayToInt(new byte[] {dataPacket[0], 
                        dataPacket[1], dataPacket[2], dataPacket[3]});
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
                sensor = sensors.get(dataType.name() + index);                
                dataStates.add(areaState);
                dataStates.add(areaTamperState);
                dataStates.add(areaStateFlags);
                break;
                
            case OUTPUT_STATUS :
                //4 bytes for record index 
                index = byteArrayToInt(new byte[] {dataPacket[0], 
                        dataPacket[1], dataPacket[2], dataPacket[3]});
                //8 bytes for ASCII value
                //1 byte output state
                ProtegeDataState outputState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_OUTPUT_STATE, dataPacket[12]);               
                //3 bytes reserved                
                sensor = sensors.get(dataType.name() + index);                
                dataStates.add(outputState);
                break;
                
            case INPUT_STATUS :
                //4 bytes for record index
                index = byteArrayToInt(new byte[] {dataPacket[0], 
                        dataPacket[1], dataPacket[2], dataPacket[3]});
                //8 bytes for ASCII value
                //1 byte input state
                ProtegeDataState inputState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_INPUT_STATE, dataPacket[12]); 
                //1 byte input bypass state
                ProtegeDataState inputBypassState = ProtegeDataState.getDataState(dataType, 
                        ProtegeDataState.INDEX_INPUT_BYPASS_STATE, dataPacket[13]);               
                //2 bytes reserved                
                sensor = sensors.get(dataType.name() + index);                
                dataStates.add(inputState);
                dataStates.add(inputBypassState);
                break;                
                
            case VARIABLE_STATUS :
                //4 bytes for record index
                index = byteArrayToInt(new byte[] {dataPacket[0], 
                        dataPacket[1], dataPacket[2], dataPacket[3]});
                //2 bytes variable value                
                int variableValue = getVariableValue(dataPacket);
                //2 bytes reserved
                sensor = sensors.get(dataType.name() + index);     
                break;              
                
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
           
            case SYSTEM_EVENT_NUMERICAL :
                //2 bytes event code
                //6 bytes event data               
                //TODO implement numerical events
                throw new UnsupportedOperationException("Numerical events not yet implemented");                
                //break;          
                
            case SYSTEM_EVENT_ASCII :
                //TODO implement numerical events
                String event = getASCIIEvent(dataPacket);              
                //break;
            
            case END_OF_DATA :
            default:
                //TODO nothing at this stage. Ack?
        }
        //create event
        if (sensor != null)
        {
//            sensor.notify(dataStates);
        }
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
    
//*****************************
//*                           *
//*   System Packet Methods   *
//*                           *
//*****************************
    
    private void readSystemPacket(byte[] message)
    { 
        //read each, increment after
        ProtegeSystemType ackType = ProtegeSystemType.getSystemType(message[6], message[7]);
        if (ackType == ProtegeSystemType.SYSTEM_ACK) 
        {
            log.error("ACK");
        } else //NACK 
        {
            log.error("NACK");
            //add bytes [8] and [9] and then call ProtegeSystemType.getSystemType()
            log.error("Converting [" + message[8] + ", " + message[9] + "]");
            ProtegeSystemType systemType = ProtegeSystemType.getSystemType(message[8], message[9]);
            log.error("Reading a " + systemType.name() + " SystemType packet.");
            switch(systemType)
            {
                case AREA_NO_CHANGE :
                {
                    
                    break;
                }
                case DOOR_ALREADY_IN_STATE :
                {
                    break;
                }
                case DOOR_INTERLOCK_ACTIVE :
                {
                    break;
                }
                case DOOR_SVC_DENIED_LOCKDOWN :
                {
                    break;
                }
                case SERVICE_COMMAND_NOT_VALID :
                {
                    break;
                }
                case SERVICE_INDEX_NOT_VALID :
                {
                    break;
                }
                case USER_ACCESS_RIGHTS :
                {
                    break;
                }
                case USER_AXS_DOOR_AXS_LVL :
                {
                    break;
                }
                case USER_DOOR_GROUP :
                {
                    break;
                }
                case USER_INVALID :
                {
                    break;
                }
                case USER_LOGIN :
                {
                    break;
                }
                case USER_LOGOUT :
                {
                    break;
                }
                case ZONE_COMMAND_FAILED :
                {
                    break;
                }
            }
        }       
    }
}
