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
 * Enumerator for the current state of records returned 
 * by the Protege controller.
 * 
 * The dataType parameter is which DataType the status represents.
 * The valueIndex is used to indicate which state the status is for, as
 *      some DataTypes have multiple states at once.
 * The dataStateValue is the byte value that represents this state.
 * 
 * @author Tomas Morton
 */
public enum ProtegeDataState 
{
    //Door Lock
    DOOR_LOCK_LOCKED ("Locked", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x00),
    DOOR_LOCK_UNLOCKED_BY_USER_ACCESS("Unlocked (User)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x01),
    DOOR_LOCK_UNLOCKED_BY_SCHEDULE ("Unlocked (Schedule)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x02),
    DOOR_LOCK_UNLOCKED_BY_USER_TIMED ("Unlocked (Timed)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x03),
    DOOR_LOCK_UNLOCKED_AND_LATCHED ("Unlocked (Latched)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x04),
    DOOR_LOCK_UNLOCKED_BY_REX ("Unlocked (REX)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x05),
    DOOR_LOCK_UNLOCKED_BY_REN ("Unlocked (REN)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x06),
    DOOR_LOCK_UNLOCKED_BY_KEYPAD_MENU ("Unlocked (Keypad)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x07),
    DOOR_LOCK_UNLOCKED_BY_AREA ("Unlocked (Area)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x08),
    DOOR_LOCK_UNLOCKED_BY_FIRE_ALARM ("Unlocked (Fire Alarm)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_LOCK_STATE, (byte) 0x09),
    
    //Door
    DOOR_CLOSED ("Closed", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x00),
    DOOR_OPEN ("Open", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x01),
    DOOR_OPEN_ALERT ("Open (Alert)", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x02),
    DOOR_LEFT_OPEN ("Left Open", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x03),
    DOOR_FORCED_OPEN ("Forced", ProtegeDataType.DOOR_STATUS, ProtegeDataState.INDEX_DOOR_STATE, (byte) 0x04),
    
    //Area
    AREA_DISARMED ("Disarmed", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x00),
    AREA_ZONES_OPEN_WAITING_FOR_USER_INPUT ("Zones Open", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x01),
    AREA_TROUBLE_CONDITION_WAITING_FOR_USER_INPUT ("Trouble Open", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x02),
    AREA_BYPASS_ERROR_WAITING_FOR_USER_INPUT ("Bypass Error", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x03),
    AREA_BYPASS_WARNING_WAITING_FOR_USER_INPUT ("Bypass Warning", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x04),
    AREA_USER_COUNT_NOT_ZERO_WAITING_FOR_USER_INPUT ("User Count Not 0", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x05),
    AREA_ARMED ("Armed", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x80),
    AREA_EXIT_DELAY ("Exit Delay", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x81),
    AREA_ENTRY_DELAY ("Entry Delay", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x82),
    AREA_DISARM_DELAY ("Disarm Delay", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x83),
    AREA_CODE_DELAY ("Code Delay", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE, (byte) 0x84),
    
    //Area Tamper (24hr)
    AREA_TAMPER_DISARMED ("24hr Disarmed", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_TAMPER_STATE, (byte) 0x00),
    AREA_TAMPER_BUSY ("24hr Busy", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_TAMPER_STATE, (byte) 0x01),
    AREA_TAMPER_ARMED ("24hr Armed", ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_TAMPER_STATE, (byte) 0x80),
    
    //Area Flags - bits represent state
    //AREA_ALARM_ACTIVATED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           0),
    //AREA_SIREN_ACTIVATED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           1),
    //AREA_ALARMS_IN_MEMORY (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           2),
    //AREA_REMOTE_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           3),
    //AREA_FORCE_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,           4),
    //AREA_INSTANT_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,          5),
    //AREA_PARTIAL_ARMED (ProtegeDataType.AREA_STATUS, ProtegeDataState.INDEX_AREA_STATE_FLAG,          6),
    
    //Output
    OUTPUT_OUTPUT_OFF ("Off", ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x00),
    OUTPUT_OUTPUT_ON ("On", ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x01),
    OUTPUT_OUTPUT_ON_PULSED ("On Pulsed", ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x02),
    OUTPUT_OUTPUT_ON_TIMED ("On Timed", ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x03),
    OUTPUT_OUTPUT_ON_PULSED_TIMED ("On Timed/Pulsed", ProtegeDataType.OUTPUT_STATUS, ProtegeDataState.INDEX_OUTPUT_STATE, (byte) 0x04),
    
    //Input
    INPUT_CLOSED ("Closed", ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x00),
    INPUT_OPEN ("Open", ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x01),
    INPUT_SHORT ("Short", ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x02),
    INPUT_TAMPER ("Tamper", ProtegeDataType.INPUT_STATUS, ProtegeDataState.INDEX_INPUT_STATE, (byte) 0x03);
    
    //Input Bypass - bits represent state
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
    
    private final String statusText;
    private final ProtegeDataType dataType;
    private final int valueIndex;
    private final byte dataStateValue;
    
    private ProtegeDataState(String statusText, ProtegeDataType dataType, int valueIndex, byte dataStateValue)
    {
        this.statusText = statusText;
        this.dataType = dataType;
        this.valueIndex = valueIndex;
        this.dataStateValue = dataStateValue;
    }
    
    /**
     * Use the constant int values in this class for valueIndex.
     * 
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
    
    public String getStatusText()
    {
        return statusText;
    }
}
