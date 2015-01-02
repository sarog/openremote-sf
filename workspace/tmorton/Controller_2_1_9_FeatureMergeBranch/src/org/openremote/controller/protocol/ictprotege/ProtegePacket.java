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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.openremote.controller.utils.Logger;

/**
 * Class that builds a network packet to be sent to the Protege
 * Controller.
 *
 * @author Tomas Morton
 */
public class ProtegePacket implements Comparable<ProtegePacket>
{

    private static final Logger log = ProtegeUtils.log;
    private static final AtomicLong sequence = new AtomicLong(0);
    
    //Constructor variables
    private final Map<String, String> paramMap;
    private final ProtegeRecordType recordType;
    private final ProtegeCommandType commandType;
    private final EncryptionType encryptionType;
    private final ChecksumType checksumType;

    private byte[] packet;
    private long sequenceNumber;

    public ProtegePacket(Map<String, String> paramMap, EncryptionType encryptionType, ChecksumType checksumType)
    {
        this.paramMap = paramMap;
        this.encryptionType = encryptionType;
        this.checksumType = checksumType;
        this.commandType = ProtegeCommandType.valueOf(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND));
        this.recordType = commandType.getRecordType();
        
        if (commandType.getPriority() != ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG)
        {
            setupCommandPacket();
        }
        this.sequenceNumber = sequence.getAndIncrement();
    }

    /**
     * Initialises as a Command Packet
     *
     * Example: 0x49, 0x43, 0x09, 0x00, 0x00 , 0x00 , 0x00 , 0x00 , 0x95 
     * header | length | type | encryption | group | command | checksum constant |
     * variable | command | none | system | Are you there | sum of other bytes.
     */
    private void setupCommandPacket()
    {
        List<Byte> byteList = new ArrayList<Byte>();
        //Setup packet preamble
        byteList.add(ProtegeUtils.HEADER_LOW);
        byteList.add(ProtegeUtils.HEADER_HIGH);
        //Add length place holders (must be calculated after encryption
        byteList.add((byte) -1);
        byteList.add((byte) -1);
        byteList.add((byte) ProtegeUtils.PACKET_COMMAND);
        byteList.add(encryptionType.getCommandValue());
        //Packet Data
        byteList.add(recordType.getValue());
        byteList.add(commandType.getValue());
        byteList.addAll(getPacketData());
        packet = ProtegeUtils.byteListToArray(byteList);
    }

    /**
     * Checks the CommandType and produces the corresponding list of bytes.
     *
     */
    private List<Byte> getPacketData()
    {
        List<Byte> data;
        switch (commandType)
        {
            case CLEAR_LOGIN:
            case PIN_DIGIT:
            case SYSTEM_POLL:
            case SEND_LOGOUT:
            case SYSTEM_DESCRIPTION:
                //no data required
                data = new ArrayList(0);
                break;
            case SEND_LOGIN:
                data = getLoginData();
                break;
//            case SYSTEM_LOGIN_TIME :
//                data = getLoginTimeoutData();
//                break;                
            case SYSTEM_MONITOR_RECORD:
                data = getMonitoringData();
                break;
            case SYSTEM_REQUEST_EVENTS:
                data = getEventRequestData();
                break;
            case OUTPUT_ON_TIMED:
                data = getRecordIndexData();
                data.addAll(getIntRecordValueData());
                break;
            case VARIABLE_SET:
                data = getRecordIndexData();
                data.addAll(getIntRecordValueData());
                data = data.subList(0, data.size() - 4); //Due to command size being 6, but only need 2 here
                break;
            //All generic change of state commands
            default:
                data = getRecordIndexData();
        }
        return data;
    }

    //Single digit PIN entry.  Not currently used.
    
//    /**
//     * Encapsulates a single digit of a PIN into a byte array. If the PIN is
//     * longer than 1 digit, the entire PIN is sent as one by redirecting to
//     * getLoginDataFullPin().
//     *
//     * @return
//     */
//    private List<Byte> getLoginDataSingleDigit()
//    {
//        String recordValue = getRecordValue();
//        if (recordValue == null)
//        {
//            throw new InvalidParameterException(
//                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE
//                    + "' must be either " + ProtegeUtils.LOGIN_SEND + ", " + ProtegeUtils.LOGIN_CLEAR
//                    + " or a single digit number when used for logging in.");
//        }
//        List<Byte> data = new ArrayList<Byte>();
//        if (recordValue.equals(ProtegeUtils.LOGIN_SEND))
//        {
//            data.add(ProtegeUtils.LOGIN_SUFFIX);
//        }
//        else if (recordValue.equals(ProtegeUtils.LOGIN_INITIATE))
//        {
//            data.add(ProtegeUtils.LOGIN_PREFIX);
//        }
//        else if (recordValue.equals(ProtegeUtils.LOGIN_CLEAR))
//        {
//            //No data required
//        }
//        else //PIN digit
//        {
//            int digit;
//            try
//            {
//                digit = Integer.parseInt(recordValue);
//            }
//            catch (NumberFormatException e)
//            {
//                throw new InvalidParameterException(
//                        "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE
//                        + "' must be either " + ProtegeUtils.LOGIN_SEND + ", " + ProtegeUtils.LOGIN_CLEAR
//                        + " or a single digit number when used for logging in.");
//            }
//            if (digit > 9)
//            {
//                data = getLoginDataFullPin();
//            }
//            else
//            {
//                data.add((byte) digit);
//            }
//        }
//        return data;
//    }
    
    
    /**
     * Creates a list of bytes containing the users PIN.
     *
     * @return
     */
    private List<Byte> getLoginData()
    {
        String PIN = paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE);
        //Split PIN into individual bytes
        List<Byte> PINList = new ArrayList<Byte>();
        for (int i = 0; i < PIN.length(); i++)
        {
            PINList.add((byte) (PIN.charAt(i) - '0'));
        }
        if (PINList.size() < ProtegeCommandType.PIN_MAX_LENGTH)
        {
            PINList.add((byte) 0xFF);
        }

        return PINList;
    }

    /**
     * Creates the data for a Monitor Record packet.
     * 
     * @return
     */
    private List<Byte> getMonitoringData()
    {
        List<Byte> monitoringList = new ArrayList<Byte>();
        ProtegeRecordType recordValue;
        try
        {
            recordValue = ProtegeRecordType.valueOf(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE));
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE + "' has an invalid value: '"
                    + paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE)
            );
        }
        //Item type - 2 bytes
        monitoringList.add(recordValue.getValue());
        monitoringList.add((byte) 0x00);
        //Item Index - 4 bytes
        monitoringList.addAll(getRecordIndexData());
        //Flags - 2 Bytes
        monitoringList.add((byte) 0x01); //start monitoring (0 is stop, 2/3 is force update)
        monitoringList.add((byte) 0x00); //end of packet

        return monitoringList;
    }

    /**
     * Returns the packet data required for an event monitoring packet.
     *
     */
    private List<Byte> getEventRequestData()
    {
        List<Byte> eventList = new ArrayList<Byte>();
        //Flags - 1 Byte
        eventList.add((byte) 0x01); //start monitoring (0 is stop)
        
        //Format - 1 Byte
        //bit 0 = 0 for numeric
        //bit 0 = 1 for human readable          
        //bit 1 = 0 send events immediately
        //bit 1 = 1 send after reportable event
        eventList.add((byte) 0x01); //Human readable, immediate
        
        return eventList;

    }

    /**
     * Returns a list of bytes containing the record index.
     *
     * @return
     */
    private List<Byte> getRecordIndexData()
    {
        int recordIndex;
        try
        {
            recordIndex = getRecordID();
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' has an invalid index: '"
                    + paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_INDEX)
            );
        }
        List<Byte> recordIndexList = ProtegeUtils.intToByteList(recordIndex, 4); //Due to reuse with login, no guarantee that commandType will be the right type.
        return recordIndexList; //4 bytes
    }

    /**
     * Returns a list of bytes containing the integer value of the record. Used
     * for Variables and Timed Outputs.
     *
     * @return
     */
    private List<Byte> getIntRecordValueData()
    {
        int recordValue;
        try
        {
            recordValue = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE));
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE + "' has an invalid value: '"
                    + paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE)
            );
        }
        List<Byte> recordValueList = ProtegeUtils.intToByteList(recordValue, commandType.getDataLength());
        return recordValueList;
    }

//**************
//   GETTERS   *
//**************
    public int getSize()
    {
        return packet.length;
    }

    public ProtegeRecordType getRecordType()
    {
        return recordType;
    }

    public ProtegeCommandType getCommandType()
    {
        return commandType;
    }

    public EncryptionType getEncryptionType()
    {
        return encryptionType;
    }

    public ChecksumType getChecksumType()
    {
        return checksumType;
    }

    public byte[] getPacket()
    {
        return packet;
    }
    
    @Override
    public String toString()
    {
        String result = commandType + " (" + recordType;
        if (getRecordID() != -1)
        {
            result += ":" + getRecordID();
        }
        if (getRecordValue() != null)
        {
            result += " [" + getRecordValue() + "]";
        }
        result += ")";
        
        return result;
    }

    /**
     * Gets the record ID of this packet, or null if it does not exist.
     *
     * @return
     */
    public int getRecordID()
    {
        String str = paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_INDEX);
        int tmp = -1;
        if (str != null)
        {
            tmp = Integer.parseInt(str);
        }
        return tmp;
    }

    public Map<String, String> getParamMap()
    {
        return paramMap;
    }

    /**
     * Gets the value assigned to the ProtegeRecordType from the XML document.
     * e.g. <property-name='record-value' value='Area'/>
     * @return Value of property from record-value
     */
    public String getRecordValue()
    {
        return paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE);
    }
    
//    private List<Byte> getLoginTimeoutData()
//    {
//        int loginTime;
//        try
//        {
//            loginTime = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT));
//
//        } catch (NumberFormatException e)
//        {
//            throw new InvalidParameterException(
//                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT + "' has an invalid value: '" + 
//                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT)
//            );
//        }                
//        List<Byte> loginTimeList = ProtegeSystemConstants.intToByteList(loginTime, commandType.getDataLength());
//        return loginTimeList;
//    }
    
    @Override
    public int compareTo(ProtegePacket o)
    {
        int result = this.getCommandType().getPriority() - o.getCommandType().getPriority();
        if (result == 0) //Enforce FIFO ordering
        {
            result = (this.getSequenceNumber() < o.getSequenceNumber()) ? -1 : 1;
        }
        return result;
    }
    
    public long getSequenceNumber()
    {
        return sequenceNumber;
    }
    
    public void updateSequenceNumber()
    {
        this.sequenceNumber = sequence.getAndIncrement();
    }
}
