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
 * Enumerator that represents the type of a data value
 * returned by the Protege controller. <br> 
 * Each packet contains two bytes representing which 
 * DataType the packet has information about.
 * 
 * @author Tomas Morton
 */
public enum ProtegeDataType {
    
    PANEL_SERIAL_NUMBER ((byte) 0x00, (byte) 0x00),
    PANEL_HARDWARE_VERSION  ((byte) 0x01, (byte) 0x00),
    FIRMWARE_TYPE  ((byte) 0x02, (byte) 0x00), 
    FIRMWARE_VERSION  ((byte) 0x03, (byte) 0x00),
    FIRMWARE_BUILD  ((byte) 0x04, (byte) 0x00),
    DOOR_STATUS ((byte) 0x00, (byte) 0x01),
    AREA_STATUS ((byte) 0x00, (byte) 0x02),
    OUTPUT_STATUS  ((byte) 0x00, (byte) 0x03),
    INPUT_STATUS ((byte) 0x00, (byte) 0x04),
    VARIABLE_STATUS ( (byte) 0x00,  (byte) 0x05),
    SYSTEM_EVENT_NUMERICAL  ((byte) 0x30, (byte) 0x00),
    SYSTEM_EVENT_ASCII ((byte) 0x30, (byte) 0x01),
    END_OF_DATA ((byte) 0xFF, (byte) 0xFF),
    
    //Internal types.  Second byte must link to ProtegeRecordType.
    CONTROLLER_STATUS((byte) 0x00, (byte) 0xF0);
   
    private final byte dataTypeLow;
    private final byte dataTypeHigh;
    
    private ProtegeDataType(byte dataTypeLow, byte dataTypeHigh)
    {
        this.dataTypeLow = dataTypeLow;
        this.dataTypeHigh = dataTypeHigh;
    }

    public byte[] getValue()
    {
        return new byte[] {dataTypeLow, dataTypeHigh};
    }    
    
    
    /**
     * Returns a ProtegeDataType with matching byte values.
     * 
     */
    public static ProtegeDataType getDataType(byte dataTypeLow, byte dataTypeHigh) 
    {
        for (ProtegeDataType dataType : ProtegeDataType.values()) {
           if ( (dataType.dataTypeLow == dataTypeLow)
              && (dataType.dataTypeHigh == dataTypeHigh))
            {
                return dataType;
            }
        }
        return null;
    }
}
