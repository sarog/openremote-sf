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

/**
 * Enumerator for commands that the ICT Protege controller understands. Each
 * includes: 
 * The enumerator name. This is equivalent to that in the
 *  controller.xml file with underscores instead of spaces. <br>
 * The byte value for the command. 
 *  The number of bytes in the data packet associated with this command.
 *
 * @author Tomas Morton
 */
public enum ProtegeCommandType
{
    //SYSTEM
    SYSTEM_POLL(ProtegeRecordType.SYSTEM, (byte) 0x0, 0, ProtegeCommandType.PRIORITY_HIGH),
    SYSTEM_DESCRIPTION(ProtegeRecordType.SYSTEM, (byte) 0x01, 0, ProtegeCommandType.PRIORITY_NORMAL),
    SEND_LOGIN(ProtegeRecordType.SYSTEM, (byte) 0x02, 2, ProtegeCommandType.PRIORITY_HIGH),
    SEND_LOGOUT(ProtegeRecordType.SYSTEM, (byte) 0x03, 0, ProtegeCommandType.PRIORITY_NORMAL),
    SYSTEM_LOGIN_TIME(ProtegeRecordType.SYSTEM, (byte) 0x04, 2, ProtegeCommandType.PRIORITY_NORMAL),
    SYSTEM_MONITOR_RECORD(ProtegeRecordType.SYSTEM, (byte) 0x05, 8, ProtegeCommandType.PRIORITY_NORMAL),
    SYSTEM_REQUEST_EVENTS(ProtegeRecordType.SYSTEM, (byte) 0x06, 2, ProtegeCommandType.PRIORITY_NORMAL),
    SYSTEM_ACK_CONTROL(ProtegeRecordType.SYSTEM, (byte) 0x07, 8, ProtegeCommandType.PRIORITY_NORMAL),
    
    //DOOR
    DOOR_LOCK(ProtegeRecordType.DOOR, (byte) 0x00, 4, ProtegeCommandType.PRIORITY_NORMAL),
    DOOR_UNLOCK(ProtegeRecordType.DOOR, (byte) 0x01, 4, ProtegeCommandType.PRIORITY_NORMAL),
    DOOR_LATCH(ProtegeRecordType.DOOR, (byte) 0x02, 4, ProtegeCommandType.PRIORITY_NORMAL),
    
    //AREA
    AREA_DISARM(ProtegeRecordType.AREA, (byte) 0x00, 4, ProtegeCommandType.PRIORITY_NORMAL),
    AREA_DISARM_24HR(ProtegeRecordType.AREA, (byte) 0x01, 4, ProtegeCommandType.PRIORITY_NORMAL),
    AREA_DISARM_ALL(ProtegeRecordType.AREA, (byte) 0x02, 4, ProtegeCommandType.PRIORITY_NORMAL),
    AREA_ARM(ProtegeRecordType.AREA, (byte) 0x03, 4, ProtegeCommandType.PRIORITY_NORMAL),
    AREA_FORCE_ARM(ProtegeRecordType.AREA, (byte) 0x04, 4, ProtegeCommandType.PRIORITY_NORMAL),
    AREA_STAY_ARM(ProtegeRecordType.AREA, (byte) 0x05, 4, ProtegeCommandType.PRIORITY_NORMAL),
    AREA_INSTANT_ARM(ProtegeRecordType.AREA, (byte) 0x06, 4, ProtegeCommandType.PRIORITY_NORMAL),
    
    //OUTPUT
    OUTPUT_OFF(ProtegeRecordType.OUTPUT, (byte) 0x00, 4, ProtegeCommandType.PRIORITY_NORMAL),
    OUTPUT_ON(ProtegeRecordType.OUTPUT, (byte) 0x01, 4, ProtegeCommandType.PRIORITY_NORMAL),
    OUTPUT_ON_TIMED(ProtegeRecordType.OUTPUT, (byte) 0x02, 6, ProtegeCommandType.PRIORITY_NORMAL),
    
    //INPUT
    INPUT_REMOVE_BYPASS(ProtegeRecordType.INPUT, (byte) 0x00, 4, ProtegeCommandType.PRIORITY_NORMAL),
    INPUT_TEMPORARY_BYPASS(ProtegeRecordType.INPUT, (byte) 0x01, 4, ProtegeCommandType.PRIORITY_NORMAL),
    INPUT_PERMANENT_BYPASS(ProtegeRecordType.INPUT, (byte) 0x02, 4, ProtegeCommandType.PRIORITY_NORMAL),
    
    //VARIABLE
    VARIABLE_SET(ProtegeRecordType.VARIABLE, (byte) 0x00, 6, ProtegeCommandType.PRIORITY_NORMAL),
    
    //ALL
    REQUEST_STATUS(ProtegeRecordType.SYSTEM, (byte) 0x80, 4, ProtegeCommandType.PRIORITY_NORMAL),

    //Controller Configuration.  Command is used for sensor index.
    PIN_DIGIT(ProtegeRecordType.CONFIG, (byte) 0x00, 0, ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG),
    CLEAR_LOGIN(ProtegeRecordType.CONFIG, (byte) 0x01, 0, ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG),
    MONITOR_LOGIN_STATUS(ProtegeRecordType.CONFIG, (byte) 0x02, 0, ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG),
    MONITOR_CONNECTION_STATUS(ProtegeRecordType.CONFIG, (byte) 0x03, 0, ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG),
    MONITOR_QUEUED_COMMANDS(ProtegeRecordType.CONFIG, (byte) 0x04, 0, ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG),
    MONITOR_PIN_DISPLAY(ProtegeRecordType.CONFIG, (byte) 0x05, 0, ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG);
    
    
    public static final int PRIORITY_CONTROLLER_CONFIG = -1;
    public static final int PRIORITY_LOWEST = 9;
    public static final int PRIORITY_LOW = 7;
    public static final int PRIORITY_NORMAL = 5;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_HIGHEST = 1;
    
    public static final String PIN_CLEAR = "PIN_CLEAR";
    public static final int PIN_MAX_LENGTH = 6;
    
    private final ProtegeRecordType recordType;
    private final byte command;
    private final int dataLength;
    private int priority;

    private ProtegeCommandType(ProtegeRecordType recordType, byte command, int dataSize, int priority)
    {        
        this.recordType = recordType;
        this.command = command;
        this.dataLength = dataSize;
        this.priority = priority;
    }

    public byte getValue()
    {
        return command;
    }

    public int getDataLength()
    {
        return dataLength;
    }

    public int getPriority()
    {
        return priority;
    }   
    
    public void setPriority(int priorty)
    {
        this.priority = priorty;
    }

    public ProtegeRecordType getRecordType()
    {
        return recordType;
    }
    
    
}
