/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * NOTE THAT ALL MONITORING COMMANDS MUST BE SENT EACH TIME YOU LOGIN
 * 
 * @author Tomas
 */
public class ProtegePacket {
     
    private static Logger log = ProtegeSystemConstants.log;
    
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
//        int packetLength = 8 + commandType.getDataLength();
//        packetLength += checksumType; //TODO Will break if checksum values ever change
        List<Byte> byteList = new ArrayList<>();
        byteList.add(HEADER_LOW);
        byteList.add(HEADER_HIGH);   
        //Setup packet preamble
        byteList.add( (byte) ProtegeSystemConstants.PACKET_COMMAND); //Command packet
        byteList.add( (byte) encryptionType); //encryption must be lower two BITS of the byte, TODO check
        //Packet Data
        byteList.add( (byte) recordType.getValue()); //TODO check the getValues are not too large for a byte
        byteList.add( (byte) commandType.getValue());
        byteList.addAll(getPacketData());        
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
        byteList.addAll(2, ProtegeSystemConstants.intToByteList(
                byteList.size() + 2, 2));
        packet = ProtegeSystemConstants.byteListToArray(byteList);
    }
    
    /**
     * Checks the CommandType and produces the corresponding
     * list of bytes.
     * TODO look at making request for status into a monitoring request.
     * Note that the packet data is different for these, so may wish
     * to update the commandType instead.
     */
    private List<Byte> getPacketData() throws NoSuchCommandException
    {
        List<Byte> data = null;
        switch(commandType)
        {
            case SYSTEM_POLL :
            case SYSTEM_LOGOUT :
            case SYSTEM_DESCRIPTION :
                //no data
                break;
                
            case SYSTEM_LOGIN :
                
                data = getLoginData(); //6 bytes
                
                break;
            
            case SYSTEM_LOGIN_TIME :
               
                data = getLoginTimeoutData(); //2 bytes
                
                break;
                
            case SYSTEM_REQUEST_MONITOR :
                
                data = getMonitoringData();
                break;
                
            case OUTPUT_TIMED :
            case VARIABLE_SET :
               
                data = getRecordValueData(); //2 bytes
                
                break;
            
            //All generic change of state commands
            default :
               
                data = getRecordIndexData();
        }    
        return data;
    }
    
    /**
     * Creates a list of bytes containing the users PIN.
     * //TODO move this to a one of call, depending on final 
     * decision on PIN entry and storage.
     * 
     * @return 
     */
    private List<Byte> getLoginData()
    {
        String PIN;
        try
        {
            PIN = paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN);
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN + "' has an invalid value: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_LOGIN_PIN)
            );
        }        
        //Split PIN into individual bytes
        List<Byte> PINList = new ArrayList<>();
        for (int i = 0; i < PIN.length(); i++)
        {
            PINList.add((byte) (PIN.charAt(i) - '0') );
        }
        if (PINList.size() < 6)
        {
            PINList.add((byte) 0xFF);
        }
        log.error("Pin as list: " + PINList.toString());
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
        List<Byte> loginTimeList = ProtegeSystemConstants.intToByteList(loginTime, commandType.getDataLength());
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
            recordIndex = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_INDEX));
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND + "' has an invalid index: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_INDEX)
            );
        }
        List<Byte> recordIndexList = ProtegeSystemConstants.intToByteList(recordIndex, 4); //Due to reuse with login, no guarantee that commandType will be the right type.
        return recordIndexList; //4 bytes
    }
    
    /**
     * Returns a list of bytes containing the
     * integer value of the record.
     * Used for Variables and Timed Outputs.
     * 
     * @return 
     */
    private List<Byte> getRecordValueData()
    {
        int recordValue;
        try
        {
            recordValue = Integer.parseInt(paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE));
        } catch (NumberFormatException e)
        {
            throw new InvalidParameterException(
                    "Protege command '" + ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE + "' has an invalid value: '" + 
                    paramMap.get(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE)
            );
        }                
        List<Byte> activationTimeList = ProtegeSystemConstants.intToByteList(recordValue, commandType.getDataLength());
        return activationTimeList;
    }    
    
    /**
     * The check sum is a single byte which is the sum 
     * of all preceding bytes, modulo 256.
     * TODO implement
     */
    private List<Byte> getChecksum8Bit()
    {
        List<Byte> checksum = new ArrayList<>();
        return checksum;
    }
    
    /**
     * 16 bit CRC. The check sum is a 16 bit CRC 
     * based on the CRC-16-CCITT polynomial.   
     * TODO implement
     */
    private List<Byte> getChecksum16Bit()
    {     
        List<Byte> checksum = new ArrayList<>();
        return checksum;
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
    
    @Override
    public String toString()
    {
        String hexArray = ProtegeConnectionManager.byteArrayToHex(packet);
        String result = "[";
        for (int i = 0; i < hexArray.length(); i += 2)
        {
            result += "0x" + hexArray.charAt(i) + hexArray.charAt(i + 1) + ", ";
        }
        return result.substring(0, result.length() - 2) + "]";
    }
}
