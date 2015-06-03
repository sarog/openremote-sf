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
package org.openremote.controller.protocol.ictprotege;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.ArrayUtils;
import org.openremote.controller.command.CommandType;
import org.openremote.controller.exception.InvalidCommandTypeException;
import org.openremote.controller.model.sensor.Sensor;
import static org.openremote.controller.protocol.ictprotege.ProtegeDataType.AREA_STATUS;
import static org.openremote.controller.protocol.ictprotege.ProtegeDataType.DOOR_STATUS;
import static org.openremote.controller.protocol.ictprotege.ProtegeDataType.INPUT_STATUS;
import static org.openremote.controller.protocol.ictprotege.ProtegeDataType.OUTPUT_STATUS;
import static org.openremote.controller.protocol.ictprotege.ProtegeDataType.SYSTEM_EVENT_ASCII;
import static org.openremote.controller.protocol.ictprotege.ProtegeDataType.VARIABLE_STATUS;
import org.openremote.controller.protocol.ictprotege.network.ProtegeConnectionManager;
import org.openremote.controller.utils.Logger;

/**
 * Thread class to process a single received byte[].
 *
 * @author Tomas Morton
 */
public class ProtegePacketHandler implements Runnable
{

    private static final Logger log = ProtegeUtils.log;
    private ProtegeConnectionManager connectionManager;
    private Map<ProtegeDataType, Map<Integer, Sensor>> sensors;
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
        processPacket(packet);
    }

    /**
     * Process a received packet and forward it to the sensors.
     *
     */
    private void processPacket(byte[] packet)
    {
        //check the checksum first
        if (removeChecksum(packet) != null)
        {
            packet = decryptPacket(packet);
            log.debug("Packet after decryption: " + ProtegeUtils.byteArrayToHex(packet));
            //Decide what packet it is
            switch (packet[4])
            {
                case ProtegeUtils.PACKET_COMMAND:
                    log.debug("Reading command packet.");
                    readCommandPacket(packet);
                    break;

                case ProtegeUtils.PACKET_DATA:
                    log.debug("Reading data packet.");
                    readDataPacket(packet);
                    break;

                case (byte) ProtegeUtils.PACKET_SYSTEM:
                    log.debug("Reading system packet.");
                    readSystemPacket(packet);
                    break;

                default:
                    log.error("Unknown packet type received: '" + Byte.toString(packet[4]) + "'.");
            }
        }
        else
        {
            log.error("Received packet with an invalid checksum.");
        }
    }

    /**
     * Removes the checksum from the packet and then checks that the packet data
     * is correct. Returns null if the checksum did not match.
     *
     * @return
     */
    private byte[] removeChecksum(byte[] packet)
    {
        boolean packetValid;
        byte[] validatedPacket = packet.clone();
        switch (connectionManager.getChecksumType())
        {
            case CHECKSUM_8:
                byte checksum = packet[packet.length - 1];
                validatedPacket = Arrays.copyOf(packet, packet.length - 1);
                packetValid = validateChecksum8Bit(validatedPacket, checksum);
                break;

            case CHECKSUM_16_CRC:
                byte checksumLow = packet[packet.length - 2];
                byte checksumHigh = packet[packet.length - 1];
                packetValid = validateChecksum16Bit(checksumLow, checksumHigh);
                validatedPacket = Arrays.copyOf(packet, packet.length - 2);
                break;

            case CHECKSUM_NONE:
            default:
                packetValid = true;
        }
        if (packetValid)
        {
            return validatedPacket;
        }
        else
        {
            return null;
        }
    }

    /**
     * Calculates the 8 bit checksum on the received packet and compares it to
     * the checksum that was sent with the packet.
     *
     * @return
     */
    private boolean validateChecksum8Bit(byte[] packet, byte checksum)
    {
        return ProtegeConnectionManager.calcChecksum8Bit(packet)[0] == checksum;
    }

    /**
     * Calculates the 16 bit checksum on the received packet and compares it to
     * the checksum that was sent with the packet.
     *
     * @return
     */
    private boolean validateChecksum16Bit(byte checksumLow, byte checksumHigh)
    {
        byte[] checksum = ProtegeConnectionManager.calcChecksum8Bit(packet);
        return checksum[0] == checksumLow
                && checksum[1] == checksumHigh;
    }

    /**
     * Decrypts a received packet using AESCrypt.
     */
    private byte[] decryptPacket(byte[] packet)
    {
        if (connectionManager.getEncryptionType() != EncryptionType.ENCRYPTION_NONE)
        {
            byte[] header = Arrays.copyOf(packet, 6);
            byte[] data = Arrays.copyOfRange(packet, 6, packet.length);
            log.debug("Header: " + ProtegeUtils.byteArrayToHex(header));
            log.debug("Encrypted data: " + ProtegeUtils.byteArrayToHex(data));
            byte[] decrypted = connectionManager.getCrypto().decrypt(data);
            return ArrayUtils.addAll(header, decrypted);
        }
        return packet;
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
        //This method should never be called.
        log.error("WARNING: Received a Command Packet: " + 
                ProtegeUtils.byteArrayToHex(packet));
    }

    /**
     * Process a response from the Protege Controller. It is possible to receive
     * multiple data packets within a single message, so the sub method
     * <code>processDataPacket</code> handles each of these data packets. Note
     * that this will read and attempt to process the message terminator.
     */
    private void readDataPacket(byte[] message)
    {
        int currentByte = 6; //skip the headers
        while (currentByte < message.length)
        {
            //read each, increment after
            byte dataTypeLow = message[currentByte++];
            byte dataTypeHigh = message[currentByte++];
            int dataLength = message[currentByte++];
            if (dataLength < 0)
            {
                dataLength += 256;
            }
            byte[] dataPacket = new byte[dataLength];
            for (int i = 0; i < dataLength; i++)
            {
                try
                {
                    dataPacket[i] = message[i + currentByte];
                } catch (ArrayIndexOutOfBoundsException e)
                {
                    log.error("Failed to read data packet.  Please check the encryption settings.");
                    return;
                }
            }
            if (dataPacket.length > 0)
            {
                log.debug("Data section of packet: " + ProtegeUtils.byteArrayToHex(dataPacket));
            }
            if (!processDataPacket(ProtegeDataType.getDataType(dataTypeLow, dataTypeHigh), dataPacket))
            {
                break; //end of packet reached
            }
            currentByte += dataLength;
        }
    }

//***************************
//*                         *
//*   Data Packet Methods   *
//*                         *
//***************************
    private boolean processDataPacket(ProtegeDataType dataType, byte[] dataPacket)
    {
        boolean packetHasDataRemaining = true;
        try
        {
            log.debug("Reading packet type: " + dataType.name());
            List<ProtegeDataState> dataStates = new ArrayList();
            Sensor sensor;
            int index;
            HashMap<Integer, Sensor> eventSensorMap;
            //analyze packet
            switch (dataType)
            {
                case DOOR_STATUS:
                    //4 bytes for record index
                    index = ProtegeUtils.byteArrayToInt(new byte[]
                    {
                        dataPacket[0],
                        dataPacket[1], dataPacket[2], dataPacket[3]
                    });
                    //1 byte door lock state
                    ProtegeDataState lockState = ProtegeDataState.getDataState(dataType,
                            ProtegeDataState.INDEX_LOCK_STATE, dataPacket[4]);
                    //1 byte door state
                    ProtegeDataState doorState = ProtegeDataState.getDataState(dataType,
                            ProtegeDataState.INDEX_DOOR_STATE, dataPacket[5]);
                    //2 bytes reserved
                    eventSensorMap = (HashMap) sensors.get(DOOR_STATUS);
                    sensor = eventSensorMap.get(index);
                    log.debug("Updating door sensor " + index + " " + sensor.getName() + 
                            " with value " + lockState.getStatusText());
                    sensor.update(lockState.getStatusText());
                    dataStates.add(lockState);
                    dataStates.add(doorState);
                    break;

                case AREA_STATUS:
                    //4 bytes for record index
                    index = ProtegeUtils.byteArrayToInt(new byte[]
                    {
                        dataPacket[0], dataPacket[1], dataPacket[2], dataPacket[3]
                    });
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
                    eventSensorMap = (HashMap) sensors.get(AREA_STATUS);
                    sensor = eventSensorMap.get(index);
                    sensor.update(areaState.getStatusText());
                    log.debug("Updating area sensor " + index + " " + sensor.getName() + 
                            " with value " + areaState.getStatusText());
                    dataStates.add(areaState);
                    dataStates.add(areaTamperState);
                    dataStates.add(areaStateFlags);
                    break;

                case OUTPUT_STATUS:
                    //4 bytes for record index 
                    index = ProtegeUtils.byteArrayToInt(new byte[]
                    {
                        dataPacket[0],
                        dataPacket[1], dataPacket[2], dataPacket[3]
                    });
                    //8 bytes for ASCII value
                    //1 byte output state
                    ProtegeDataState outputState = ProtegeDataState.getDataState(dataType,
                            ProtegeDataState.INDEX_OUTPUT_STATE, dataPacket[12]);
                    //3 bytes reserved            
                    eventSensorMap = (HashMap) sensors.get(OUTPUT_STATUS);
                    sensor = eventSensorMap.get(index);
                    sensor.update(outputState.getStatusText());
                    log.debug("Updating output sensor " + index + " " + sensor.getName() + 
                            " with value " + outputState.getStatusText());
                    dataStates.add(outputState);
                    break;

                case INPUT_STATUS:
                    //4 bytes for record index
                    index = ProtegeUtils.byteArrayToInt(new byte[]
                    {
                        dataPacket[0],
                        dataPacket[1], dataPacket[2], dataPacket[3]
                    });
                    //8 bytes for ASCII value
                    //1 byte input state
                    ProtegeDataState inputState = ProtegeDataState.getDataState(dataType,
                            ProtegeDataState.INDEX_INPUT_STATE, dataPacket[12]);
                    //1 byte input bypass state
                    ProtegeDataState inputBypassState = ProtegeDataState.getDataState(dataType,
                            ProtegeDataState.INDEX_INPUT_BYPASS_STATE, dataPacket[13]);
                    //2 bytes reserved            
                    eventSensorMap = (HashMap) sensors.get(INPUT_STATUS);
                    sensor = eventSensorMap.get(index);
                    log.debug("Updating input sensor " + index + " " + sensor.getName() + 
                            " with value " + inputState.getStatusText());
                    sensor.update(inputState.getStatusText());
                    dataStates.add(inputState);
                    dataStates.add(inputBypassState);
                    break;

                case VARIABLE_STATUS:
                    //4 bytes for record index
                    index = ProtegeUtils.byteArrayToInt(new byte[]
                    {
                        dataPacket[0],
                        dataPacket[1], dataPacket[2], dataPacket[3]
                    });
                    //2 bytes variable value                
                    int variableValue = getVariableValue(dataPacket);
                    //2 bytes reserved
                    eventSensorMap = (HashMap) sensors.get(VARIABLE_STATUS);
                    sensor = eventSensorMap.get(index);
                    log.debug("Updating variable sensor " + index + " " + sensor.getName() + 
                            " with value " + variableValue);
                    sensor.update(Integer.toString(variableValue));
                    break;

                case PANEL_SERIAL_NUMBER:
                    String serialNumber = getPanelProperty(dataPacket); //4 Bytes
                    log.info("Panel serial number: " + serialNumber + ".");
                    break;

                case PANEL_HARDWARE_VERSION:
                    String hardwareVersion = getPanelProperty(dataPacket); //1 Byte
                    log.info("Panel hardware version: " + hardwareVersion + ".");
                    break;

                case FIRMWARE_TYPE:
                    String firmwareType = getPanelProperty(dataPacket); //2 Bytes
                    log.info("Panel firmware type: " + firmwareType + ".");
                    break;

                case FIRMWARE_VERSION:
                    String firmwareVersion = getPanelProperty(dataPacket); //2 Bytes
                    log.info("Panel firmware version: " + firmwareVersion + ".");
                    break;

                case FIRMWARE_BUILD:
                    String firmwareBuild = getPanelProperty(dataPacket); //2 Bytes
                    log.info("Panel firmware build: " + firmwareBuild + ".");
                    break;

                case SYSTEM_EVENT_NUMERICAL:
                    //2 bytes event code
                    //6 bytes event data               
                    throw new UnsupportedOperationException("Numerical events not supported.");
                    //break;          

                case SYSTEM_EVENT_ASCII:
                    String event = getASCIIEvent(dataPacket);
                    log.debug("New event: " + event);
                    ProtegeEventHandler.getInstance().logEvent(event);
                    break;

                case END_OF_DATA:
                    packetHasDataRemaining = false;
                default:
            }
        }
        catch (NullPointerException e)
        {
            log.error("Error at ProtegePacketHandler.processDataPacket: " + e);
        }
        return packetHasDataRemaining;
    }

    /**
     * Gets a hardware / firmware property of the panel.
     * 4 Bytes
     *
     * @param dataPacket
     * @return
     */
    private String getPanelProperty(byte[] dataPacket)
    {
        String serialNumber = "";
        try
        {
            serialNumber = new String(dataPacket, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            log.error("Failed to read event: " + ex);
        }
        return serialNumber;
    }

    /**
     *   Gets the value of a variable
     * 
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
     * Gets an event and processes as ASCII.
     * 
     * @param dataPacket
     * @return
     */
    private String getASCIIEvent(byte[] dataPacket)
    {
        String result = "";
        try
        {
            dataPacket[dataPacket.length - 1] = '.';
            result = new String(dataPacket, "US-ASCII");
        }
        catch (UnsupportedEncodingException ex)
        {
            java.util.logging.Logger.getLogger(ProtegeConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
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
            log.debug("ACK");
            connectionManager.notifyAck();
        }
        else //NACK 
        {
            log.debug("NACK");
            //add bytes [8] and [9] and then call ProtegeSystemType.getSystemType()
            try
            {
                log.debug("Converting [" + message[8] + ", " + message[9] + "]");
            }
            catch (IndexOutOfBoundsException e)
            {
                //For example, sent a command without being logged in
                connectionManager.notifyAck();
                log.debug("No data attached to NACK. Dropping packet.");
                return; //No data, stop processing
            }
            ProtegeSystemType systemType;
            try
            {
                systemType = ProtegeSystemType.getSystemType(message[8], message[9]);
                log.debug("Reading a " + systemType.name() + " SystemType packet.");
            }
            catch (NullPointerException e)
            {
                log.info("Could not translate packet to a SystemType ([" + message[8] + ", " + message[9] + "].");
                systemType = ProtegeSystemType.SYSTEM_NACK;
            }
            switch (systemType)
            {
                //ignore these
                case AREA_NO_CHANGE:
                case DOOR_ALREADY_IN_STATE:
                case ZONE_COMMAND_FAILED:
                {
                    connectionManager.notifyAck();
                    break;
                }
                //Should never happen!
                case SERVICE_COMMAND_NOT_VALID:
                {
                    connectionManager.notifyAck();
                    log.error("WARNING: Command not valid (Please check your encryption settings): " +
                            connectionManager.getCurrentPacket().toString());
                    log.debug("Packet data for invalid command: " + ProtegeUtils.byteArrayToHex(connectionManager.getCurrentPacket().getPacket()));                    
                    break;
                }
                case DOOR_INTERLOCK_ACTIVE:
                case DOOR_SVC_DENIED_LOCKDOWN:
                {
                    connectionManager.notifyAck();
                    log.error("Door (" + connectionManager.getCurrentPacket().getRecordID() + 
                            ") cannot be controlled from current state (Interlock active or in Lockdown).");
                    //Display on sensor that the command has been denied
                    break;
                }
                case SERVICE_INDEX_NOT_VALID:
                {
                    connectionManager.notifyAck();
                    log.error("Record does not exist on controller: " + connectionManager.getCurrentPacket().toString());
                    break;
                }
                case USER_ACCESS_RIGHTS:
                case USER_AXS_DOOR_AXS_LVL:
                case USER_DOOR_GROUP:
                case USER_INVALID:
                {
                    connectionManager.notifyAck();
                    log.error("Invalid PIN entered or user does not have sufficient priveledges in access level.");
                    connectionManager.notifyInvalidPin();
                    break;
                }
                case USER_LOGIN:
                {
                    connectionManager.notifyAck();
                    break;
                }
                case USER_LOGOUT:
                {
                    //If the sent packet was a logout packet, acknowledge
                    switch(connectionManager.getCurrentPacket().getCommandType())
                    {
                        case SEND_LOGOUT:
                            connectionManager.notifyAck();
                            break;
                        case SEND_LOGIN:
                            connectionManager.notifyInvalidPin();
                            break;
                        default:
                            //Packet denied due to login state: do not ack, as packet should be resent after logging in.
                            connectionManager.notifyLoggedOut();
                    }
                    break;
                }
                default:
                {
                    connectionManager.notifyAck();
                    log.error("Received an unhandled NACK packet (Please check your encryption settings).");
                }
            }
        }
    }
}
