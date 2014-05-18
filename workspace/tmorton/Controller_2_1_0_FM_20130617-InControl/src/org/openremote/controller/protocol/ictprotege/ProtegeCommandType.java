/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

/**
 * NOTE THAT ALL MONITORING COMMANDS MUST BE SENT EACH TIME YOU LOGIN
 * 
 * @author Tomas
 */
public enum ProtegeCommandType 
{    
    //SYSTEM
    SYSTEM_POLL(0x0, 0),
    SYSTEM_DESCRIPTION(0x01, 0),
    SYSTEM_LOGIN(0x02, 2),
    SYSTEM_LOGOUT(0x03, 0),
    SYSTEM_LOGIN_TIME(0x04, 2),
    SYSTEM_REQUEST_MONITOR(0x05, 8),
    SYSTEM_REQUEST_EVENTS(0x06, 2),
    SYSTEM_ACK_CONTROL(0x07, 8),
    
    //DOOR
    DOOR_LOCK(0x00, 4),
    DOOR_UNLOCK(0x01, 4),
    DOOR_UNLOCK_LATCHED(0x02, 4),
    
    //AREA
    AREA_DISARM(0x00, 4),
    AREA_DISARM_24HR(0x01, 4),
    AREA_DISARM_ALL(0x02, 4),
    AREA_ARM(0x03, 4),
    AREA_ARM_FORCE(0x04, 4),
    AREA_ARM_STAY(0x05, 4),
    AREA_ARM_INSTANT(0x06, 4),
    
    //OUTPUT
    OUTPUT_OFF(0x00, 4),
    OUTPUT_ON(0x01, 4),
    OUTPUT_TIMED(0x02, 6),
    
    //INPUT
    INPUT_BYPASS_REMOVE(0x00, 4),
    INPUT_BYPASS_TEMPORARY(0x01, 4),
    INPUT_BYPASS_PERMANENT(0x02, 4),
    
    //VARIABLE
    VARIABLE_SET(0x00, 6),
    
    //ALL
    REQUEST_STATUS(0x80, 4);
    
    private int command;
    private int dataLength;
    
    private ProtegeCommandType(int command, int dataSize)
    {
        this.command = command;
        this.dataLength = dataSize;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }    
    
    public int getValue()
    {
        return command;
    }    
    
    public int getDataLength()
    {
        return dataLength;
    }
}
