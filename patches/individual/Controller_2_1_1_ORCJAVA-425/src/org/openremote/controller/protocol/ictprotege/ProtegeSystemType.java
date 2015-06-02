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
 * Enumerator for System packets sent by the Protege controller.<br>
 * Each ProtegeSystemType is identified by two bytes.
 *
 * @author Tomas
 */
public enum ProtegeSystemType
{

    //Ack and Nack headers
    SYSTEM_ACK((byte) 0xFF, (byte) 0x00),
    SYSTEM_NACK((byte) 0xFF, (byte) 0xFF),
    
    //System packet types
    SERVICE_INDEX_NOT_VALID((byte) 0x21, (byte) 0x01),      //The index of the record to control was not valid.
    SERVICE_COMMAND_NOT_VALID((byte) 0x20, (byte) 0x01),    //The requested command was not valid.
    USER_LOGIN((byte) 0x00, (byte) 0x03),                   //A login command was received while a User was already logged in.
    USER_LOGOUT((byte) 0x01, (byte) 0x03),                  //A control command was received while no User was logged in.
    USER_INVALID((byte) 0x02, (byte) 0x03),                 //A login command was received but the PIN did not match a valid User.
    USER_DOOR_GROUP((byte) 0x0A, (byte) 0x03),              //The Door group was not valid or the User did not have access rights to that Door group.
    USER_AXS_DOOR_AXS_LVL((byte) 0x0F, (byte) 0x03),        //The Door action was denied because the User does not have any valid access levels assigned.
    DOOR_SVC_DENIED_LOCKDOWN((byte) 0x23, (byte) 0x0A),     //The Door action was denied because the Door was in lock-down mode.    
    DOOR_ALREADY_IN_STATE((byte) 0x32, (byte) 0x0A),        //The Door action was valid but the Door did not change state because it was already in that state.
    DOOR_INTERLOCK_ACTIVE((byte) 0x12, (byte) 0x0A),        //The Door action was denied because of an interlock on the Door.
    AREA_NO_CHANGE((byte) 0x69, (byte) 0x08),               //The Area action was valid but the Area did not change state because it was already in that state.
    USER_ACCESS_RIGHTS((byte) 0x03, (byte) 0x03),           //The Area action was denied because the User did not have sufficient Arm/Disarm rights.
    ZONE_COMMAND_FAILED((byte) 0x11, (byte) 0x04);         //The Zone was unable to be bypassed either because of Zone configuration or because it's in an armed Area. 

    private byte typeValueLow;
    private byte typeValueHigh;

    private ProtegeSystemType(byte typeValueLow, byte typeValueHigh)
    {
        this.typeValueLow = typeValueLow;
        this.typeValueHigh = typeValueHigh;
    }

    public static ProtegeSystemType getSystemType(byte typeValueLow, byte typeValueHigh)
    {
        for (ProtegeSystemType systemType : ProtegeSystemType.values())
        {
            if (systemType.typeValueLow == typeValueLow
                    && systemType.typeValueHigh == typeValueHigh)
            {
                return systemType;
            }
        }
        return null;
    }

    public byte[] getTypeValue()
    {
        return new byte[]
        {
            typeValueLow, typeValueHigh
        };
    }
}
