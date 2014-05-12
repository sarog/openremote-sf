/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * NOTE THAT ALL MONITORING COMMANDS MUST BE SENT EACH TIME YOU LOGIN
 * 
 * @author Tomas
 */
public class ProtegePacket {
    
    public static final byte HEADER_LOW = 0x49;
    public static final byte HEADER_HIGH = 0x43;
    
    private byte[] packet;
    private Map<String, String> paramMap;
    private ProtegeRecordType recordType;
    private ProtegeCommandType commandType;
    private int encryptionType;
    private int checksumType;
    
    public ProtegePacket(Map<String, String> paramMap, int encryptionType, int checksumType)
    {
        this.paramMap = paramMap;   
        this.encryptionType = encryptionType;
        this.checksumType = checksumType;
        this.recordType = ProtegeRecordType.valueOf(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_TYPE));
        this.commandType = ProtegeCommandType.valueOf(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND));
        setupCommandPacket();        
    }
    
    /**
     * Initialises as a Command Packet
     * TODO clean up all the variables and add directly into the list.
     * Example: 0x49, 0x43, 0x09, 0x00,  0x00   , 0x00       , 0x00   , 0x00          , 0x95
     *          header    | length    | type    | encryption | group  | command       | checksum 
     *          constant  | variable  | command | none       | system | Are you there | sum of other bytes.
     */
    private void setupCommandPacket()
    {
        //Get packet length - 2 bytes (fixed)
        int packetLength = 8 + commandType.getDataLength();
        packetLength += checksumType; //TODO Will break if checksum values ever change
        List<Byte> byteList = new ArrayList<>(packetLength);
        byteList.addAll(convertIntToByteArray(packetLength, 2));   
        //Setup packet preamble
        byteList.add( (byte) ProtegeSystemConstants.PACKET_COMMAND); //Command packet
        byteList.add( (byte) encryptionType); //encryption must be lower two BITS of the byte, TODO check
        //Packet Data
        byteList.add( (byte) recordType.getValue()); //TODO check the getValues are not too large for a byte
        byteList.add( (byte) commandType.getValue());
        addData(byteList);        
        //checksum - 0-2 bytes (variable)
        switch (checksumType)
        {
            case ProtegeSystemConstants.CHECKSUM_8 :
                byteList.addAll(getChecksum8Bit());
                break;
                
            case ProtegeSystemConstants.CHECKSUM_16 :
                byteList.addAll(getChecksum16Bit());
                break;
                
            case ProtegeSystemConstants.CHECKSUM_NONE :
            default:
                //nothing required
        }       
        packet = listToArray(byteList);
//        packet = new byte[packetSize];
//        packet[0] = HEADER_LOW;
//        packet[1] = HEADER_HIGH;
//        packet[2] = lengthLow;
//        packet[3] = lengthHigh;
//        packet[4] = packetType;
//        packet[5] = encryption;
//        packet[6] = group;
//        packet[7] = command;
//        packet[8] = record1;
//        packet[9] = record2;
//        packet[10] = record3;
//        packet[11] = record4;
        //packet[12] = activationLengthLow;
        //packet[13] = activationLengthLow;
//        if (checksumType != ProtegeSystemConstants.CHECKSUM_NONE)
//        {
//            byteList.add(checksumLow);
////            packet[12] = checksumLow;
//            if (checksumType == ProtegeSystemConstants.CHECKSUM_16)
//            {
//                byteList.add(checksumHigh);
////                packet[13] = checksumHigh;
//            }
//        }
//        packet = byteList.toArray(new Byte[byteList.size()]);
        //TODO optionally if packet MUST be primitive, use ArrayUtils.toPrimitive()
    }
    
    /**
     * Checks the CommandType and updates the packet accordingly.
     * Note: UPDATES the list provided, ensure it is modifiable.
     */
    private void addData(List byteList) 
            throws NoSuchCommandException
    {
        switch(commandType)
        {
            case SYSTEM_POLL :
            case SYSTEM_LOGOUT :
            case SYSTEM_DESCRIPTION :
                //no data
                break;
                
            case SYSTEM_LOGIN :
                
                byteList.addAll(getLoginData()); //6 bytes
                
                break;
            
            case SYSTEM_LOGIN_TIME :
               
                byteList.addAll(getLoginTimeoutData()); //2 bytes
                
                break;
                
            case SYSTEM_REQUEST_MONITOR :
            case REQUEST_STATUS : //don't want to send a single request, should be asynchronous
                
                byteList.addAll(getMonitoringData());
                break;
                
            case OUTPUT_TIMED :
               
                byteList.addAll(getActivationTimeData()); //2 bytes
                
                break;
                
            case VARIABLE_SET :
               
                byteList.addAll(getVariableSetData());
                break;
            
            //All generic change of state commands
            default :
               
                byteList.addAll(getRecordIndexData());
        }    
    }
    
    private List<Byte> getLoginData()
    {
        int PIN;
        try
        {
            PIN = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN));
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN + "' has an invalid value: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN)
            );
        }                
        List<Byte> PINList = convertIntToByteArray(PIN, commandType.getDataLength());
        return PINList;
    }
    
    private List<Byte> getLoginTimeoutData()
    {
        int loginTime;
        try
        {
            loginTime = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT));

        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT + "' has an invalid value: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_TIMEOUT)
            );
        }                
        List<Byte> loginTimeList = convertIntToByteArray(loginTime, commandType.getDataLength());
        return loginTimeList;
    }
    
    /**
     * TODO Cannot actually use the inputs in an area option at this stage.
     * Dependant on UI implementation.
     * 
     * @return 
     */
    private List<Byte> getMonitoringData()
    {
        List<Byte> monitoringList = new ArrayList<>();
        //Item Type - 2 bytes
            //0,0 All items (for stop monitoring)
            //0, 1 Door
            //0, 2 Area
            //1, 2 Inputs in an Area
            //0, 3 Output
            //0, 4 Input
            //0, 5 Variable
        monitoringList.add( (byte) recordType.getValue());
        monitoringList.add( (byte) 0x00); //until area inputs implemented, use 0.
        //Item Index - 4 bytes
            //Reuse the method in Default
        monitoringList.addAll(getRecordIndexData());
        //Flags - 2 Bytes
            // 0 to stop
            // 1 to start
            // 2 to force an update
        monitoringList.add( (byte) 0x01); //until area inputs implemented, use 0.
        monitoringList.add( (byte) 0x00); //until area inputs implemented, use 0.
        
        return monitoringList;
    }
    
    private List<Byte> getActivationTimeData()
    {
        int activationTime;
        try
        {
            activationTime = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_OUTPUT_TIMED));
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_OUTPUT_TIMED + "' has an invalid value: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_OUTPUT_TIMED)
            );
        }                
        List<Byte> activationTimeList = convertIntToByteArray(activationTime, commandType.getDataLength());
        return activationTimeList;
    }
    
    private List<Byte> getVariableSetData()
    {
        int variableValue;
        try
        {
            variableValue = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_VARIABLE_SET_VALUE));
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_VARIABLE_SET_VALUE + "' has an invalid value: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_VARIABLE_SET_VALUE)
            );
        }                
        List<Byte> variableValueList = convertIntToByteArray(variableValue, commandType.getDataLength());
        return variableValueList;
        
    }
    
    private List<Byte> getRecordIndexData()
    {
        int recordIndex;
        try
        {
            recordIndex = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_INDEX));
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' has an invalid index: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_INDEX)
            );
        }
        List<Byte> recordIndexList = convertIntToByteArray(recordIndex, 4); //Due to reuse with login, no guarantee that commandType will be the right type.
        return recordIndexList; //4 bytes
//                byte record1 = recordIDArray[0];
//                byte record2 = (recordIDArray.length > 1) ? recordIDArray[1] : 0x00; //TODO could be improved with a for loop
//                byte record3 = (recordIDArray.length > 2) ? recordIDArray[2] : 0x00;
//                byte record4 = (recordIDArray.length > 3) ? recordIDArray[3] : 0x00;
    }
    
    /**
     * The check sum is a single byte which is the sum of all preceding bytes, modulo 256.
     */
    private List<Byte> getChecksum8Bit()
    {
        List<Byte> checksum = new ArrayList<>();
        return checksum;
    }
    
    /**
     * 16 bit CRC. The check sum is a 16 bit CRC based on the CRC‐16‐CCITT polynomial.   
     */
    private List<Byte> getChecksum16Bit()
    {     
        List<Byte> checksum = new ArrayList<>();
        return checksum;
    }
    
    //Taken from StackOverflow
    //http://stackoverflow.com/questions/2183240/java-integer-to-byte-array
    //TODO test
    //TODO write own method
    public static List<Byte> convertIntToByteArray(int number, int length)
    {
        byte[] bytes = ByteBuffer.allocate(Integer.SIZE / 8).putInt(number).array();
        if (bytes.length > length)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' has a value that is too large."
            );
        }
//        for (byte b : bytes) {
//           System.out.format("0x%02X", b);
//        }
        //convert to little endian
//        List<Byte> byteList = new ArrayList<>(Arrays.asList(bytes));
        for (int i = 0; i < bytes.length / 2; i++)
        {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
        //Transfer to a correctly sized list with padded 0x00 values.
        List<Byte> paddedResult = new ArrayList(length);
        for (int i = 0; i < length; i++)
        {
            if (i < bytes.length) 
            {
                paddedResult.add(bytes[i]); 
            } else
            {
                paddedResult.add( (byte) 0x00);
            }
        }
        return paddedResult;
    }

    public static byte[] listToArray(List<Byte> bytes)
    {
        byte[] bytesArray = new byte[bytes.size()];
        for (int i=0; i < bytesArray.length; i++)
        {
            bytesArray[i] = bytes.get(i);
        }
        return bytesArray;
    }
    
    
//**************
//   GETTERS   *
//**************
    
    
    public int getSize() {
        return packet.length; 
    }
    
    public ProtegeRecordType getRecordType() {
        return recordType;
    }

    public ProtegeCommandType getCommandType() {
        return commandType;
    }
    public int getEncryptionType() {
        return encryptionType;
    }

    public int getChecksumType() {
        return checksumType;
    }

    public byte[] getPacket() {
        return packet;
    }
    
}
