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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * Class with utilities used by the Protege implementation.
 * TODO: Convert values to enums for type safety.
 * 
 * @author Tomas
 */
public class ProtegeUtils 
{    
    /**
     * A common log category name intended to be used across all classes related to
     * Protege implementation.
     */
    public final static String PROTEGE_LOG_CATEGORY  = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ICTPROTEGE";
    public final static Logger log = Logger.getLogger(PROTEGE_LOG_CATEGORY);
    
    //Packet values
    public static final byte HEADER_LOW = 0x49;
    public static final byte HEADER_HIGH = 0x43;
    public static final byte LOGIN_PREFIX = 0x18;
    public static final byte LOGIN_SUFFIX = 0x0D;
    public static final byte LOGIN_DATA_INCOMPLETE = 0x0F; //Append to the end of each login digit to indicate more are coming (in single digit mode)
    public static final String LOGIN_INITIATE = "START";
    public static final String LOGIN_SEND = "SEND";
    public static final String LOGIN_CLEAR = "CLEAR";
    
    //Packet types    
    public static final int PACKET_COMMAND = 0x00;
    public static final int PACKET_DATA = 0x1;
    public static final int PACKET_SYSTEM = 0xC0;
    
    
    /**
     * Converts an integer into a list of bytes. The size 
     * of the result is specified by the <code>length</code>
     * parameter.  <br>
     * If the number is too small to fill the list
     * it will be padded with 0x00 values. <br>
     * If the number it too big to fit in the list,
     * an InvalidParameterException is thrown.
     * 
     * @param number number to convert to a list of bytes.
     * @param length final size of the list (padded).
     * @return 
     */
    public static List<Byte> intToByteList(int number, int length) 
            throws InvalidParameterException
    {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(number);
        byte[] bytes = buffer.array();
        //find the actual number of values in the array
        int arraySize = bytes.length;
        for (int i = bytes.length - 1; i >= 0; i--)
        {
            if (bytes[i] == 0x00)
            {
                arraySize--;
            } else
            {
                break;
            }            
        }
        if (arraySize > length)
        {
            log.error(
                    "A Protege value has a number that is too large (" + number +
                    ").  Expected length " + length + " but received " + bytes.length
            );
            bytes = Arrays.copyOf(bytes, length);
            log.debug("Resized array to length " + length + ": " + ProtegeUtils.byteArrayToHex(bytes));
        }
        //Transfer to a correctly sized list with padded 0x00 values.
        List<Byte> paddedResult = new ArrayList(length);
        for (int i = 0; i < length; i++)
        {
            if (i < bytes.length) 
            {
                paddedResult.add(bytes[i]); 
            } else
            {
                paddedResult.add( (byte) 0x00);
            }
        }
        log.debug("Converted " + number + " to byte array: " + ProtegeUtils.byteArrayToHex(byteListToArray(paddedResult)));
        return paddedResult;
    }

    /**
     * Copies the contents of a byte array into a list.
     * 
     * @param bytes
     * @return 
     */
    public static byte[] byteListToArray(List<Byte> bytes)
    {
        byte[] bytesArray = new byte[bytes.size()];
        for (int i=0; i < bytesArray.length; i++)
        {
            bytesArray[i] = bytes.get(i);
        }
        return bytesArray;
    }
    
    public static String byteArrayToHex(byte[] array) 
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (byte b : array)
        {
           builder.append(String.format("%02X, ", b&0xff));
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("]");
        
        return builder.toString();
     }
    
    
    public static int byteArrayToInt(byte[] input)
    {
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }
}
