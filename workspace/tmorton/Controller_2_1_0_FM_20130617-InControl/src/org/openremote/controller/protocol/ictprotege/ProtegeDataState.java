/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

/**
 * TODO find a better solution...
 * @author Tomas
 */
public enum ProtegeDataState 
{
    //Door Lock
    DOOR_LOCK_LOCKED (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x00),
    DOOR_LOCK_UNLOCKED_BY_USER_ACCESS (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x01),
    DOOR_LOCK_UNLOCKED_BY_SCHEDULE (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x02),
    DOOR_LOCK_UNLOCKED_BY_USER_TIMED (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x03),
    DOOR_LOCK_UNLOCKED_AND_LATCHED (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x04),
    DOOR_LOCK_UNLOCKED_BY_REX (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x05),
    DOOR_LOCK_UNLOCKED_BY_REN (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x06),
    DOOR_LOCK_UNLOCKED_BY_KEYPAD_MENU (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x07),
    DOOR_LOCK_UNLOCKED_BY_AREA (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x08),
    DOOR_LOCK_UNLOCKED_BY_FIRE_ALARM (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x09),
    
    //Door
    DOOR_CLOSED (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x00),
    DOOR_OPEN (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x01),
    DOOR_OPEN_ALERT (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x02),
    DOOR_LEFT_OPEN (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x03),
    DOOR_FORCED_OPEN (ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x04),
    
    //Area
    AREA_DISARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x00),
    AREA_ZONES_OPEN_WAITING_FOR_USER_INPUT (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x01),
    AREA_TROUBLE_CONDITION_WAITING_FOR_USER_INPUT (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x02),
    AREA_BYPASS_ERROR_WAITING_FOR_USER_INPUT (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x03),
    AREA_BYPASS_WARNING_WAITING_FOR_USER_INPUT (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x04),
    AREA_USER_COUNT_NOT_ZERO_WAITING_FOR_USER_INPUT (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x05),
    AREA_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x80),
    AREA_EXIT_DELAY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x81),
    AREA_ENTRY_DELAY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x82),
    AREA_DISARM_DELAY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x83),
    AREA_CODE_DELAY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x84),
    
    //Area Tamper (24hr)
    AREA_TAMPER_DISARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_TAMPER_STATE, (byte) 0x00),
    AREA_TAMPER_BUSY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_TAMPER_STATE, (byte) 0x01),
    AREA_TAMPER_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_TAMPER_STATE, (byte) 0x80),
    
    //Area Flags
    //AREA_ALARM_ACTIVATED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           0),
    //AREA_SIREN_ACTIVATED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           1),
    //AREA_ALARMS_IN_MEMORY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           2),
    //AREA_REMOTE_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           3),
    //AREA_FORCE_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           4),
    //AREA_INSTANT_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,          5),
    //AREA_PARTIAL_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,          6),
    
    //Output
    OUTPUT_OUTPUT_OFF (ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x00),
    OUTPUT_OUTPUT_ON (ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x01),
    OUTPUT_OUTPUT_ON_PULSED (ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x02),
    OUTPUT_OUTPUT_ON_TIMED (ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x03),
    OUTPUT_OUTPUT_ON_PULSED_TIMED (ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x04),
    
    //Input
    INPUT_CLOSED (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x00),
    INPUT_OPEN (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x01),
    INPUT_SHORT (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x02),
    INPUT_TAMPER (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x03);
    
    //Input Bypass
    //INPUT_BYPASS_BYPASSED (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_BYPASS_STATE,           0),
    //INPUT_BYPASS_BYPASS_LATCHED (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_BYPASS_STATE,         1),
    //INPUT_BYPASS_SIREN_LOCKOUT (ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_BYPASS_STATE,          3);


    
    /**
     * Static identifiers to determine which record command
     * is being analyzed.
     * For example, door status packets have two separate values:
     * Lock State and Door State.  The hex values of the status overlap
     * for each of these, so an identifier is needed to retrieve the right
     * data for the event.
     */
    public static final int INDEX_LOCK_STATE = 0;
    public static final int INDEX_DOOR_STATE = 1;
    public static final int INDEX_AREA_STATE = 0;
    public static final int INDEX_AREA_TAMPER_STATE = 1;
    public static final int INDEX_AREA_STATE_FLAG = 2;
    public static final int INDEX_OUTPUT_STATE = 0;
    public static final int INDEX_INPUT_STATE = 0;
    public static final int INDEX_INPUT_BYPASS_STATE = 1;
    
    private ProtegeDataType dataType;
    private int valueIndex;
    private byte dataStateValue;
    
    private ProtegeDataState(ProtegeDataType dataType, int valueIndex, byte dataStateValue)
    {
        this.dataType = dataType;
        this.valueIndex = valueIndex;
        this.dataStateValue = dataStateValue;
    }
    
    /**
     * Use the constant int values in this class for valueIndex.
     * TODO: Could also implement as map for efficiency.
     */
    public static ProtegeDataState getDataState(ProtegeDataType dataType, int valueIndex, byte dataStateValue) 
    {
        for (ProtegeDataState dataState : ProtegeDataState.values()) {
            if ( (dataState.dataType == dataType)
              && (dataState.valueIndex == valueIndex)
              && (dataState.dataStateValue == dataStateValue))
            {
                return dataState;
            }
        }
        return null;
    }
}
