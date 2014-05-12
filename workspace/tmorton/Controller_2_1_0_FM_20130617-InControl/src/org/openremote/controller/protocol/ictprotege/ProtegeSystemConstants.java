/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openremote.controller.protocol.ictprotege;

/**
 * NOT TYPESAFE IN THE SLIGHTEST
 * 
 * @author Tomas
 */
public class ProtegeSystemConstants 
{
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
}
