/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * NOT TYPESAFE IN THE SLIGHTEST
 * 
 * @author Tomas
 */
public class ProtegeSystemConstants 
{    
    /**
     * A common log category name intended to be used across all classes related to
     * Protege implementation.
     */
    public final static String PROTEGE_LOG_CATEGORY  = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ICTPROTEGE";
    public final static Logger log = Logger.getLogger(PROTEGE_LOG_CATEGORY);
    
    //Packet types    
    public static final int PACKET_COMMAND = 0x00;
    public static final int PACKET_DATA = 0x1;
    public static final int PACKET_SYSTEM = 0xC0;
    
    //Encryption values
    public static final int ENCRYPTION_NONE = 0x00;
    public static final int ENCRYPTION_AES_128 = 0x01;
    public static final int ENCRYPTION_AES_192 = 0x02;
    public static final int ENCRYPTION_AES_256 = 0x03;
            
    //Checksum values
    public static final int CHECKSUM_NONE = 0x00;
    public static final int CHECKSUM_8 = 0x01;
    public static final int CHECKSUM_16 = 0x02;
    
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
            throw new InvalidParameterException(
                    "A Protege value has a number that is too large (" + number +
                    ") expected length " + length + " but got " + bytes.length
            );
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
        log.error("Converted " + number + " to byte array: " + paddedResult.toString());
        return paddedResult;
    }

    /**
     * Copies the contents of a byte array
     * into a list.
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
}
