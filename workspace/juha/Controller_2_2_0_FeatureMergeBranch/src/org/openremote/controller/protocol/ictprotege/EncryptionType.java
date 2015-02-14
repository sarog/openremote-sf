/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
 *
 * @author Tomas
 */
public enum EncryptionType
{    
    ENCRYPTION_NONE((byte) 0x00),
    ENCRYPTION_AES_128((byte) 0x01),
    ENCRYPTION_AES_192((byte) 0x02),
    ENCRYPTION_AES_256((byte) 0x03);
    
    private final byte commandValue;
    
    private EncryptionType(byte commandValue)
    {
        this.commandValue = commandValue;
    }

    public byte getCommandValue()
    {
        return commandValue;
    }
}
