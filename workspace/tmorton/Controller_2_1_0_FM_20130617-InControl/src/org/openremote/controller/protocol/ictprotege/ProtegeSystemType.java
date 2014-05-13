/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

/**
 *
 * @author Tomas
 */
public enum ProtegeSystemType {
    //Ack and Nack headers
    SYSTEM_ACK_LOW (0xFF),
    SYSTEM_ACK_HIGH (0x00),
    SYSTEM_NACK_LOW (0xFF),
    SYSTEM_NACK_HIGH (0xFF),
    
    //System packet types
    SERVICE_INDEX_NOT_VALID (0x0121), //The index of the record to control was not valid.
    SERVICE_COMMAND_NOT_VALID (0x0120), //The requested command was not valid.
    USER_LOGIN (0x0300), //A login command was received while a User was already logged in.
    USER_LOGOUT (0x0301), //A control command was received while no User was logged in.
    USER_INVALID (0x0302), //A login command was received but the PIN did not match a valid User.
    USER_DOOR_GROUP (0x030A), //The Door group was not valid or the User did not have access rights to that Door group.
    USER_AXS_DOOR_AXS_LVL (0x030F), //The Door action was denied because the User does not have any valid access levels assigned.
    DOOR_SVC_DENIED_LOCKDOWN (0x0A23), //The Door action was denied because the Door was in lock‐down mode.    
    DOOR_ALREADY_IN_STATE (0x0A32), //The Door action was valid but the Door did not change state because it was already in that state.
    DOOR_INTERLOCK_ACTIVE (0x0A12), //The Door action was denied because of an interlock on the Door.
    AREA_NO_CHANGE (0x0869), //The Area action was valid but the Area did not change state because it was already in that state.
    USER_ACCESS_RIGHTS (0x0303), //The Area action was denied because the User did not have sufficient Arm/Disarm rights.
    ZONE_COMMAND_FAILED (0x040E); //The Zone was unable to be bypassed either because of Zone configuration or because it's in an armed Area. 
    
    
    private int typeValue;
    
    private ProtegeSystemType(int typeValue)
    {
        this.typeValue = typeValue;
    }
    
    public static ProtegeSystemType getSystemType(int typeValue)
    {
        for (ProtegeSystemType systemType : ProtegeSystemType.values())
        {
            if (systemType.typeValue == typeValue)
            {
                return systemType;
            }
        }
        return null;
    }
    
    public int getTypeValue()
    {
        return typeValue;
    }
}
