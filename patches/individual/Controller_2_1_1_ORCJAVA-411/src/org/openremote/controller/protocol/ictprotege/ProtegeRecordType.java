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
 * Enumerator of the record types to control in Protege. * 
 * 
 * @author Tomas Morton
 */
public enum ProtegeRecordType {
    SYSTEM( (byte) 0x00), 
    DOOR( (byte) 0x01),
    AREA( (byte) 0x02),
    OUTPUT( (byte) 0x03),
    INPUT( (byte) 0x04),
    VARIABLE( (byte) 0x05),
    CONFIG( (byte) (0xF0));
    
    private final byte value;
            
    private ProtegeRecordType(byte value)
    {
        this.value = value;        
    }
        
    public byte getValue()
    {
        return value;
    }    
}
