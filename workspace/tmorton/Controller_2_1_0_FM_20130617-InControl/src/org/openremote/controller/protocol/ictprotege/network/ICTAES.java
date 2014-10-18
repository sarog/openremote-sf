/*
 *  FIPS-197 compliant AES implementation
 *
 *  Copyright (C) 2003-2006  Christophe Devine
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License, version 2.1 as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *  MA  02110-1301  USA
 */
/*
 *  The AES block cipher was designed by Vincent Rijmen and Joan Daemen.
 *
 *  http://csrc.nist.gov/encryption/aes/rijndael/Rijndael.pdf
 *  http://csrc.nist.gov/publications/fips/fips197/fips-197.pdf
 */
package org.openremote.controller.protocol.ictprotege.network;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.openremote.controller.protocol.ictprotege.ChecksumType;
import org.openremote.controller.protocol.ictprotege.EncryptionType;
import org.openremote.controller.protocol.ictprotege.ProtegeCommandBuilder;
import org.openremote.controller.protocol.ictprotege.ProtegePacket;
import org.openremote.controller.protocol.ictprotege.ProtegeUtils;

public class ICTAES
{    
    private static final int AES_KEY_COUNT = 8;
    private static final int AES_BLOCK_SIZE = 16;
    
    private byte[] aesKeyInUse;
    private AESContext[] aesKeyBuffer;
    private final int[] key;
    private final int encryptionTypeLength;
    
    public ICTAES(String key, EncryptionType type)
    {
        aesKeyInUse = new byte[AES_KEY_COUNT];
        aesKeyBuffer = new AESContext[AES_KEY_COUNT];
        for (int i = 0; i < aesKeyBuffer.length; i++)
        {
            aesKeyBuffer[i] = new AESContext();
        }
        this.key = byteArraySignedToUnsigned(key.getBytes());
        switch (type)
        {
            case ENCRYPTION_AES_128 :
                encryptionTypeLength = 128;
                break;
            case ENCRYPTION_AES_192 :
                encryptionTypeLength = 192;
                break;
            case ENCRYPTION_AES_256 :
                encryptionTypeLength = 256;
                break;
            default:
                encryptionTypeLength = 0;
        }
    }

    /*
     * 32-bit integer manipulation macros (big endian)
     */
    public static BigInteger GET_UNS_32_BE(int[] b, int i)
    {
        BigInteger result = (BigInteger.valueOf(b[(i)    ]).shiftLeft(24)) .or(
                            (BigInteger.valueOf(b[(i) + 1]).shiftLeft(16))).or(
                            (BigInteger.valueOf(b[(i) + 2]).shiftLeft(8 ))).or(
                            (BigInteger.valueOf(b[(i) + 3])              ));
//        if (result < 0)
//        {
////            result += Math.pow(2, 32);
//        }
        return result;
    }

    public static int[] PUT_UNS_32_BE(BigInteger n, int[] out, int i)
    {
        out[i    ] = byteSignedToUnsigned(n.shiftRight(24).and(BigInteger.valueOf(0xFF)).byteValue());
        out[i + 1] = byteSignedToUnsigned(n.shiftRight(16).and(BigInteger.valueOf(0xFF)).byteValue());
        out[i + 2] = byteSignedToUnsigned(n.shiftRight(8 ).and(BigInteger.valueOf(0xFF)).byteValue());
        out[i + 3] = byteSignedToUnsigned(n               .and(BigInteger.valueOf(0xFF)).byteValue());
        
        return out;
    }

    /*
     //     * Forward S-box
     //     */
    private static final byte[] FSb =
    {
        (byte) 0x63, (byte) 0x7C, (byte) 0x77, (byte) 0x7B, (byte) 0xF2, (byte) 0x6B, (byte) 0x6F, (byte) 0xC5,
        (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2B, (byte) 0xFE, (byte) 0xD7, (byte) 0xAB, (byte) 0x76,
        (byte) 0xCA, (byte) 0x82, (byte) 0xC9, (byte) 0x7D, (byte) 0xFA, (byte) 0x59, (byte) 0x47, (byte) 0xF0,
        (byte) 0xAD, (byte) 0xD4, (byte) 0xA2, (byte) 0xAF, (byte) 0x9C, (byte) 0xA4, (byte) 0x72, (byte) 0xC0,
        (byte) 0xB7, (byte) 0xFD, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3F, (byte) 0xF7, (byte) 0xCC,
        (byte) 0x34, (byte) 0xA5, (byte) 0xE5, (byte) 0xF1, (byte) 0x71, (byte) 0xD8, (byte) 0x31, (byte) 0x15,
        (byte) 0x04, (byte) 0xC7, (byte) 0x23, (byte) 0xC3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9A,
        (byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xE2, (byte) 0xEB, (byte) 0x27, (byte) 0xB2, (byte) 0x75,
        (byte) 0x09, (byte) 0x83, (byte) 0x2C, (byte) 0x1A, (byte) 0x1B, (byte) 0x6E, (byte) 0x5A, (byte) 0xA0,
        (byte) 0x52, (byte) 0x3B, (byte) 0xD6, (byte) 0xB3, (byte) 0x29, (byte) 0xE3, (byte) 0x2F, (byte) 0x84,
        (byte) 0x53, (byte) 0xD1, (byte) 0x00, (byte) 0xED, (byte) 0x20, (byte) 0xFC, (byte) 0xB1, (byte) 0x5B,
        (byte) 0x6A, (byte) 0xCB, (byte) 0xBE, (byte) 0x39, (byte) 0x4A, (byte) 0x4C, (byte) 0x58, (byte) 0xCF,
        (byte) 0xD0, (byte) 0xEF, (byte) 0xAA, (byte) 0xFB, (byte) 0x43, (byte) 0x4D, (byte) 0x33, (byte) 0x85,
        (byte) 0x45, (byte) 0xF9, (byte) 0x02, (byte) 0x7F, (byte) 0x50, (byte) 0x3C, (byte) 0x9F, (byte) 0xA8,
        (byte) 0x51, (byte) 0xA3, (byte) 0x40, (byte) 0x8F, (byte) 0x92, (byte) 0x9D, (byte) 0x38, (byte) 0xF5,
        (byte) 0xBC, (byte) 0xB6, (byte) 0xDA, (byte) 0x21, (byte) 0x10, (byte) 0xFF, (byte) 0xF3, (byte) 0xD2,
        (byte) 0xCD, (byte) 0x0C, (byte) 0x13, (byte) 0xEC, (byte) 0x5F, (byte) 0x97, (byte) 0x44, (byte) 0x17,
        (byte) 0xC4, (byte) 0xA7, (byte) 0x7E, (byte) 0x3D, (byte) 0x64, (byte) 0x5D, (byte) 0x19, (byte) 0x73,
        (byte) 0x60, (byte) 0x81, (byte) 0x4F, (byte) 0xDC, (byte) 0x22, (byte) 0x2A, (byte) 0x90, (byte) 0x88,
        (byte) 0x46, (byte) 0xEE, (byte) 0xB8, (byte) 0x14, (byte) 0xDE, (byte) 0x5E, (byte) 0x0B, (byte) 0xDB,
        (byte) 0xE0, (byte) 0x32, (byte) 0x3A, (byte) 0x0A, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5C,
        (byte) 0xC2, (byte) 0xD3, (byte) 0xAC, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xE4, (byte) 0x79,
        (byte) 0xE7, (byte) 0xC8, (byte) 0x37, (byte) 0x6D, (byte) 0x8D, (byte) 0xD5, (byte) 0x4E, (byte) 0xA9,
        (byte) 0x6C, (byte) 0x56, (byte) 0xF4, (byte) 0xEA, (byte) 0x65, (byte) 0x7A, (byte) 0xAE, (byte) 0x08,
        (byte) 0xBA, (byte) 0x78, (byte) 0x25, (byte) 0x2E, (byte) 0x1C, (byte) 0xA6, (byte) 0xB4, (byte) 0xC6,
        (byte) 0xE8, (byte) 0xDD, (byte) 0x74, (byte) 0x1F, (byte) 0x4B, (byte) 0xBD, (byte) 0x8B, (byte) 0x8A,
        (byte) 0x70, (byte) 0x3E, (byte) 0xB5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xF6, (byte) 0x0E,
        (byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xB9, (byte) 0x86, (byte) 0xC1, (byte) 0x1D, (byte) 0x9E,
        (byte) 0xE1, (byte) 0xF8, (byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xD9, (byte) 0x8E, (byte) 0x94,
        (byte) 0x9B, (byte) 0x1E, (byte) 0x87, (byte) 0xE9, (byte) 0xCE, (byte) 0x55, (byte) 0x28, (byte) 0xDF,
        (byte) 0x8C, (byte) 0xA1, (byte) 0x89, (byte) 0x0D, (byte) 0xBF, (byte) 0xE6, (byte) 0x42, (byte) 0x68,
        (byte) 0x41, (byte) 0x99, (byte) 0x2D, (byte) 0x0F, (byte) 0xB0, (byte) 0x54, (byte) 0xBB, (byte) 0x16
    };
//
//    /*
//     * Forward tables
//     */
//    #define FT 
//    
        private static final String[][] FT = {
            {"C6", "63", "63", "A5"}, {"F8", "7C", "7C", "84"}, {"EE", "77", "77", "99"}, {"F6", "7B", "7B", "8D"}, 
            {"FF", "F2", "F2", "0D"}, {"D6", "6B", "6B", "BD"}, {"DE", "6F", "6F", "B1"}, {"91", "C5", "C5", "54"}, 
            {"60", "30", "30", "50"}, {"02", "01", "01", "03"}, {"CE", "67", "67", "A9"}, {"56", "2B", "2B", "7D"}, 
            {"E7", "FE", "FE", "19"}, {"B5", "D7", "D7", "62"}, {"4D", "AB", "AB", "E6"}, {"EC", "76", "76", "9A"}, 
            {"8F", "CA", "CA", "45"}, {"1F", "82", "82", "9D"}, {"89", "C9", "C9", "40"}, {"FA", "7D", "7D", "87"}, 
            {"EF", "FA", "FA", "15"}, {"B2", "59", "59", "EB"}, {"8E", "47", "47", "C9"}, {"FB", "F0", "F0", "0B"}, 
            {"41", "AD", "AD", "EC"}, {"B3", "D4", "D4", "67"}, {"5F", "A2", "A2", "FD"}, {"45", "AF", "AF", "EA"}, 
            {"23", "9C", "9C", "BF"}, {"53", "A4", "A4", "F7"}, {"E4", "72", "72", "96"}, {"9B", "C0", "C0", "5B"}, 
            {"75", "B7", "B7", "C2"}, {"E1", "FD", "FD", "1C"}, {"3D", "93", "93", "AE"}, {"4C", "26", "26", "6A"}, 
            {"6C", "36", "36", "5A"}, {"7E", "3F", "3F", "41"}, {"F5", "F7", "F7", "02"}, {"83", "CC", "CC", "4F"}, 
            {"68", "34", "34", "5C"}, {"51", "A5", "A5", "F4"}, {"D1", "E5", "E5", "34"}, {"F9", "F1", "F1", "08"}, 
            {"E2", "71", "71", "93"}, {"AB", "D8", "D8", "73"}, {"62", "31", "31", "53"}, {"2A", "15", "15", "3F"}, 
            {"08", "04", "04", "0C"}, {"95", "C7", "C7", "52"}, {"46", "23", "23", "65"}, {"9D", "C3", "C3", "5E"}, 
            {"30", "18", "18", "28"}, {"37", "96", "96", "A1"}, {"0A", "05", "05", "0F"}, {"2F", "9A", "9A", "B5"}, 
            {"0E", "07", "07", "09"}, {"24", "12", "12", "36"}, {"1B", "80", "80", "9B"}, {"DF", "E2", "E2", "3D"}, 
            {"CD", "EB", "EB", "26"}, {"4E", "27", "27", "69"}, {"7F", "B2", "B2", "CD"}, {"EA", "75", "75", "9F"}, 
            {"12", "09", "09", "1B"}, {"1D", "83", "83", "9E"}, {"58", "2C", "2C", "74"}, {"34", "1A", "1A", "2E"}, 
            {"36", "1B", "1B", "2D"}, {"DC", "6E", "6E", "B2"}, {"B4", "5A", "5A", "EE"}, {"5B", "A0", "A0", "FB"}, 
            {"A4", "52", "52", "F6"}, {"76", "3B", "3B", "4D"}, {"B7", "D6", "D6", "61"}, {"7D", "B3", "B3", "CE"}, 
            {"52", "29", "29", "7B"}, {"DD", "E3", "E3", "3E"}, {"5E", "2F", "2F", "71"}, {"13", "84", "84", "97"}, 
            {"A6", "53", "53", "F5"}, {"B9", "D1", "D1", "68"}, {"00", "00", "00", "00"}, {"C1", "ED", "ED", "2C"}, 
            {"40", "20", "20", "60"}, {"E3", "FC", "FC", "1F"}, {"79", "B1", "B1", "C8"}, {"B6", "5B", "5B", "ED"}, 
            {"D4", "6A", "6A", "BE"}, {"8D", "CB", "CB", "46"}, {"67", "BE", "BE", "D9"}, {"72", "39", "39", "4B"}, 
            {"94", "4A", "4A", "DE"}, {"98", "4C", "4C", "D4"}, {"B0", "58", "58", "E8"}, {"85", "CF", "CF", "4A"}, 
            {"BB", "D0", "D0", "6B"}, {"C5", "EF", "EF", "2A"}, {"4F", "AA", "AA", "E5"}, {"ED", "FB", "FB", "16"}, 
            {"86", "43", "43", "C5"}, {"9A", "4D", "4D", "D7"}, {"66", "33", "33", "55"}, {"11", "85", "85", "94"}, 
            {"8A", "45", "45", "CF"}, {"E9", "F9", "F9", "10"}, {"04", "02", "02", "06"}, {"FE", "7F", "7F", "81"}, 
            {"A0", "50", "50", "F0"}, {"78", "3C", "3C", "44"}, {"25", "9F", "9F", "BA"}, {"4B", "A8", "A8", "E3"}, 
            {"A2", "51", "51", "F3"}, {"5D", "A3", "A3", "FE"}, {"80", "40", "40", "C0"}, {"05", "8F", "8F", "8A"}, 
            {"3F", "92", "92", "AD"}, {"21", "9D", "9D", "BC"}, {"70", "38", "38", "48"}, {"F1", "F5", "F5", "04"}, 
            {"63", "BC", "BC", "DF"}, {"77", "B6", "B6", "C1"}, {"AF", "DA", "DA", "75"}, {"42", "21", "21", "63"}, 
            {"20", "10", "10", "30"}, {"E5", "FF", "FF", "1A"}, {"FD", "F3", "F3", "0E"}, {"BF", "D2", "D2", "6D"}, 
            {"81", "CD", "CD", "4C"}, {"18", "0C", "0C", "14"}, {"26", "13", "13", "35"}, {"C3", "EC", "EC", "2F"}, 
            {"BE", "5F", "5F", "E1"}, {"35", "97", "97", "A2"}, {"88", "44", "44", "CC"}, {"2E", "17", "17", "39"}, 
            {"93", "C4", "C4", "57"}, {"55", "A7", "A7", "F2"}, {"FC", "7E", "7E", "82"}, {"7A", "3D", "3D", "47"}, 
            {"C8", "64", "64", "AC"}, {"BA", "5D", "5D", "E7"}, {"32", "19", "19", "2B"}, {"E6", "73", "73", "95"}, 
            {"C0", "60", "60", "A0"}, {"19", "81", "81", "98"}, {"9E", "4F", "4F", "D1"}, {"A3", "DC", "DC", "7F"}, 
            {"44", "22", "22", "66"}, {"54", "2A", "2A", "7E"}, {"3B", "90", "90", "AB"}, {"0B", "88", "88", "83"}, 
            {"8C", "46", "46", "CA"}, {"C7", "EE", "EE", "29"}, {"6B", "B8", "B8", "D3"}, {"28", "14", "14", "3C"}, 
            {"A7", "DE", "DE", "79"}, {"BC", "5E", "5E", "E2"}, {"16", "0B", "0B", "1D"}, {"AD", "DB", "DB", "76"}, 
            {"DB", "E0", "E0", "3B"}, {"64", "32", "32", "56"}, {"74", "3A", "3A", "4E"}, {"14", "0A", "0A", "1E"}, 
            {"92", "49", "49", "DB"}, {"0C", "06", "06", "0A"}, {"48", "24", "24", "6C"}, {"B8", "5C", "5C", "E4"}, 
            {"9F", "C2", "C2", "5D"}, {"BD", "D3", "D3", "6E"}, {"43", "AC", "AC", "EF"}, {"C4", "62", "62", "A6"}, 
            {"39", "91", "91", "A8"}, {"31", "95", "95", "A4"}, {"D3", "E4", "E4", "37"}, {"F2", "79", "79", "8B"}, 
            {"D5", "E7", "E7", "32"}, {"8B", "C8", "C8", "43"}, {"6E", "37", "37", "59"}, {"DA", "6D", "6D", "B7"}, 
            {"01", "8D", "8D", "8C"}, {"B1", "D5", "D5", "64"}, {"9C", "4E", "4E", "D2"}, {"49", "A9", "A9", "E0"}, 
            {"D8", "6C", "6C", "B4"}, {"AC", "56", "56", "FA"}, {"F3", "F4", "F4", "07"}, {"CF", "EA", "EA", "25"}, 
            {"CA", "65", "65", "AF"}, {"F4", "7A", "7A", "8E"}, {"47", "AE", "AE", "E9"}, {"10", "08", "08", "18"}, 
            {"6F", "BA", "BA", "D5"}, {"F0", "78", "78", "88"}, {"4A", "25", "25", "6F"}, {"5C", "2E", "2E", "72"}, 
            {"38", "1C", "1C", "24"}, {"57", "A6", "A6", "F1"}, {"73", "B4", "B4", "C7"}, {"97", "C6", "C6", "51"}, 
            {"CB", "E8", "E8", "23"}, {"A1", "DD", "DD", "7C"}, {"E8", "74", "74", "9C"}, {"3E", "1F", "1F", "21"}, 
            {"96", "4B", "4B", "DD"}, {"61", "BD", "BD", "DC"}, {"0D", "8B", "8B", "86"}, {"0F", "8A", "8A", "85"}, 
            {"E0", "70", "70", "90"}, {"7C", "3E", "3E", "42"}, {"71", "B5", "B5", "C4"}, {"CC", "66", "66", "AA"}, 
            {"90", "48", "48", "D8"}, {"06", "03", "03", "05"}, {"F7", "F6", "F6", "01"}, {"1C", "0E", "0E", "12"}, 
            {"C2", "61", "61", "A3"}, {"6A", "35", "35", "5F"}, {"AE", "57", "57", "F9"}, {"69", "B9", "B9", "D0"}, 
            {"17", "86", "86", "91"}, {"99", "C1", "C1", "58"}, {"3A", "1D", "1D", "27"}, {"27", "9E", "9E", "B9"}, 
            {"D9", "E1", "E1", "38"}, {"EB", "F8", "F8", "13"}, {"2B", "98", "98", "B3"}, {"22", "11", "11", "33"}, 
            {"D2", "69", "69", "BB"}, {"A9", "D9", "D9", "70"}, {"07", "8E", "8E", "89"}, {"33", "94", "94", "A7"}, 
            {"2D", "9B", "9B", "B6"}, {"3C", "1E", "1E", "22"}, {"15", "87", "87", "92"}, {"C9", "E9", "E9", "20"}, 
            {"87", "CE", "CE", "49"}, {"AA", "55", "55", "FF"}, {"50", "28", "28", "78"}, {"A5", "DF", "DF", "7A"}, 
            {"03", "8C", "8C", "8F"}, {"59", "A1", "A1", "F8"}, {"09", "89", "89", "80"}, {"1A", "0D", "0D", "17"}, 
            {"65", "BF", "BF", "DA"}, {"D7", "E6", "E6", "31"}, {"84", "42", "42", "C6"}, {"D0", "68", "68", "B8"}, 
            {"82", "41", "41", "C3"}, {"29", "99", "99", "B0"}, {"5A", "2D", "2D", "77"}, {"1E", "0F", "0F", "11"}, 
            {"7B", "B0", "B0", "CB"}, {"A8", "54", "54", "FC"}, {"6D", "BB", "BB", "D6"}, {"2C", "16", "16", "3A"}
        };
//
    private static final long[] FT0 = getFT0();
    private static final long[] FT1 = getFT1();
    private static final long[] FT2 = getFT2();
    private static final long[] FT3 = getFT3();
    
    /**
     * getFT*() functions
     * 
     * Get each array within FT and then rearrange.
     * 
     */
    
//    #define V(a,b,c,d) 0x##a##b##c##d
    private static final long[] getFT0()
    {
        long[] FT0 = new long[256];
        for (int i = 0; i < FT.length; i++)
        {
            FT0[i] = Long.decode("0x" + FT[i][0] + FT[i][1] + FT[i][2] + FT[i][3]);
        }
        return FT0;
    }
    
//    #define V(a,b,c,d) 0x##d##a##b##c
    private static final long[] getFT1()
    {
        long[] FT1 = new long[256];
        for (int i = 0; i < FT.length; i++)
        {
            FT1[i] = Long.decode("0x" +  FT[i][3] + FT[i][0] + FT[i][1] + FT[i][2]);
        }
        return FT1;
    }
//
//    #define V(a,b,c,d) 0x##c##d##a##b
    private static final long[] getFT2()
    {
        long[] FT2 = new long[256];
        for (int i = 0; i < FT.length; i++)
        {
            FT2[i] = Long.decode("0x" +  FT[i][2] + FT[i][3] + FT[i][0] + FT[i][1]);
        }
        return FT2;
    }
//
//    #define V(a,b,c,d) 0x##b##c##d##a
    private static final long[] getFT3()
    {
        long[] FT3 = new long[256];
        for (int i = 0; i < FT.length; i++)
        {
            FT3[i] = Long.decode("0x" +  FT[i][1] + FT[i][2] + FT[i][3] + FT[i][0]);
        }
        return FT3;
    }
//
//    /*
//     * Reverse S-box
//     */
    private static final byte[] RSb =
    {
        (byte) 0x52, (byte) 0x09, (byte) 0x6A, (byte) 0xD5, (byte) 0x30, (byte) 0x36, (byte) 0xA5, (byte) 0x38,
        (byte) 0xBF, (byte) 0x40, (byte) 0xA3, (byte) 0x9E, (byte) 0x81, (byte) 0xF3, (byte) 0xD7, (byte) 0xFB,
        (byte) 0x7C, (byte) 0xE3, (byte) 0x39, (byte) 0x82, (byte) 0x9B, (byte) 0x2F, (byte) 0xFF, (byte) 0x87,
        (byte) 0x34, (byte) 0x8E, (byte) 0x43, (byte) 0x44, (byte) 0xC4, (byte) 0xDE, (byte) 0xE9, (byte) 0xCB,
        (byte) 0x54, (byte) 0x7B, (byte) 0x94, (byte) 0x32, (byte) 0xA6, (byte) 0xC2, (byte) 0x23, (byte) 0x3D,
        (byte) 0xEE, (byte) 0x4C, (byte) 0x95, (byte) 0x0B, (byte) 0x42, (byte) 0xFA, (byte) 0xC3, (byte) 0x4E,
        (byte) 0x08, (byte) 0x2E, (byte) 0xA1, (byte) 0x66, (byte) 0x28, (byte) 0xD9, (byte) 0x24, (byte) 0xB2,
        (byte) 0x76, (byte) 0x5B, (byte) 0xA2, (byte) 0x49, (byte) 0x6D, (byte) 0x8B, (byte) 0xD1, (byte) 0x25,
        (byte) 0x72, (byte) 0xF8, (byte) 0xF6, (byte) 0x64, (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16,
        (byte) 0xD4, (byte) 0xA4, (byte) 0x5C, (byte) 0xCC, (byte) 0x5D, (byte) 0x65, (byte) 0xB6, (byte) 0x92,
        (byte) 0x6C, (byte) 0x70, (byte) 0x48, (byte) 0x50, (byte) 0xFD, (byte) 0xED, (byte) 0xB9, (byte) 0xDA,
        (byte) 0x5E, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xA7, (byte) 0x8D, (byte) 0x9D, (byte) 0x84,
        (byte) 0x90, (byte) 0xD8, (byte) 0xAB, (byte) 0x00, (byte) 0x8C, (byte) 0xBC, (byte) 0xD3, (byte) 0x0A,
        (byte) 0xF7, (byte) 0xE4, (byte) 0x58, (byte) 0x05, (byte) 0xB8, (byte) 0xB3, (byte) 0x45, (byte) 0x06,
        (byte) 0xD0, (byte) 0x2C, (byte) 0x1E, (byte) 0x8F, (byte) 0xCA, (byte) 0x3F, (byte) 0x0F, (byte) 0x02,
        (byte) 0xC1, (byte) 0xAF, (byte) 0xBD, (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8A, (byte) 0x6B,
        (byte) 0x3A, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4F, (byte) 0x67, (byte) 0xDC, (byte) 0xEA,
        (byte) 0x97, (byte) 0xF2, (byte) 0xCF, (byte) 0xCE, (byte) 0xF0, (byte) 0xB4, (byte) 0xE6, (byte) 0x73,
        (byte) 0x96, (byte) 0xAC, (byte) 0x74, (byte) 0x22, (byte) 0xE7, (byte) 0xAD, (byte) 0x35, (byte) 0x85,
        (byte) 0xE2, (byte) 0xF9, (byte) 0x37, (byte) 0xE8, (byte) 0x1C, (byte) 0x75, (byte) 0xDF, (byte) 0x6E,
        (byte) 0x47, (byte) 0xF1, (byte) 0x1A, (byte) 0x71, (byte) 0x1D, (byte) 0x29, (byte) 0xC5, (byte) 0x89,
        (byte) 0x6F, (byte) 0xB7, (byte) 0x62, (byte) 0x0E, (byte) 0xAA, (byte) 0x18, (byte) 0xBE, (byte) 0x1B,
        (byte) 0xFC, (byte) 0x56, (byte) 0x3E, (byte) 0x4B, (byte) 0xC6, (byte) 0xD2, (byte) 0x79, (byte) 0x20,
        (byte) 0x9A, (byte) 0xDB, (byte) 0xC0, (byte) 0xFE, (byte) 0x78, (byte) 0xCD, (byte) 0x5A, (byte) 0xF4,
        (byte) 0x1F, (byte) 0xDD, (byte) 0xA8, (byte) 0x33, (byte) 0x88, (byte) 0x07, (byte) 0xC7, (byte) 0x31,
        (byte) 0xB1, (byte) 0x12, (byte) 0x10, (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xEC, (byte) 0x5F,
        (byte) 0x60, (byte) 0x51, (byte) 0x7F, (byte) 0xA9, (byte) 0x19, (byte) 0xB5, (byte) 0x4A, (byte) 0x0D,
        (byte) 0x2D, (byte) 0xE5, (byte) 0x7A, (byte) 0x9F, (byte) 0x93, (byte) 0xC9, (byte) 0x9C, (byte) 0xEF,
        (byte) 0xA0, (byte) 0xE0, (byte) 0x3B, (byte) 0x4D, (byte) 0xAE, (byte) 0x2A, (byte) 0xF5, (byte) 0xB0,
        (byte) 0xC8, (byte) 0xEB, (byte) 0xBB, (byte) 0x3C, (byte) 0x83, (byte) 0x53, (byte) 0x99, (byte) 0x61,
        (byte) 0x17, (byte) 0x2B, (byte) 0x04, (byte) 0x7E, (byte) 0xBA, (byte) 0x77, (byte) 0xD6, (byte) 0x26,
        (byte) 0xE1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55, (byte) 0x21, (byte) 0x0C, (byte) 0x7D
    };
//
//    /*
//     * Reverse tables
//     */
//    #define RT \
//    \
    private static final String[][] RT = {
        {"51" , "F4" , "A7" , "50"}, {"7E" , "41" , "65" , "53"}, {"1A" , "17" , "A4" , "C3"}, {"3A" , "27" , "5E" , "96"}, 
        {"3B" , "AB" , "6B" , "CB"}, {"1F" , "9D" , "45" , "F1"}, {"AC" , "FA" , "58" , "AB"}, {"4B" , "E3" , "03" , "93"}, 
        {"20" , "30" , "FA" , "55"}, {"AD" , "76" , "6D" , "F6"}, {"88" , "CC" , "76" , "91"}, {"F5" , "02" , "4C" , "25"}, 
        {"4F" , "E5" , "D7" , "FC"}, {"C5" , "2A" , "CB" , "D7"}, {"26" , "35" , "44" , "80"}, {"B5" , "62" , "A3" , "8F"}, 
        {"DE" , "B1" , "5A" , "49"}, {"25" , "BA" , "1B" , "67"}, {"45" , "EA" , "0E" , "98"}, {"5D" , "FE" , "C0" , "E1"}, 
        {"C3" , "2F" , "75" , "02"}, {"81" , "4C" , "F0" , "12"}, {"8D" , "46" , "97" , "A3"}, {"6B" , "D3" , "F9" , "C6"}, 
        {"03" , "8F" , "5F" , "E7"}, {"15" , "92" , "9C" , "95"}, {"BF" , "6D" , "7A" , "EB"}, {"95" , "52" , "59" , "DA"}, 
        {"D4" , "BE" , "83" , "2D"}, {"58" , "74" , "21" , "D3"}, {"49" , "E0" , "69" , "29"}, {"8E" , "C9" , "C8" , "44"}, 
        {"75" , "C2" , "89" , "6A"}, {"F4" , "8E" , "79" , "78"}, {"99" , "58" , "3E" , "6B"}, {"27" , "B9" , "71" , "DD"}, 
        {"BE" , "E1" , "4F" , "B6"}, {"F0" , "88" , "AD" , "17"}, {"C9" , "20" , "AC" , "66"}, {"7D" , "CE" , "3A" , "B4"}, 
        {"63" , "DF" , "4A" , "18"}, {"E5" , "1A" , "31" , "82"}, {"97" , "51" , "33" , "60"}, {"62" , "53" , "7F" , "45"}, 
        {"B1" , "64" , "77" , "E0"}, {"BB" , "6B" , "AE" , "84"}, {"FE" , "81" , "A0" , "1C"}, {"F9" , "08" , "2B" , "94"}, 
        {"70" , "48" , "68" , "58"}, {"8F" , "45" , "FD" , "19"}, {"94" , "DE" , "6C" , "87"}, {"52" , "7B" , "F8" , "B7"}, 
        {"AB" , "73" , "D3" , "23"}, {"72" , "4B" , "02" , "E2"}, {"E3" , "1F" , "8F" , "57"}, {"66" , "55" , "AB" , "2A"}, 
        {"B2" , "EB" , "28" , "07"}, {"2F" , "B5" , "C2" , "03"}, {"86" , "C5" , "7B" , "9A"}, {"D3" , "37" , "08" , "A5"}, 
        {"30" , "28" , "87" , "F2"}, {"23" , "BF" , "A5" , "B2"}, {"02" , "03" , "6A" , "BA"}, {"ED" , "16" , "82" , "5C"}, 
        {"8A" , "CF" , "1C" , "2B"}, {"A7" , "79" , "B4" , "92"}, {"F3" , "07" , "F2" , "F0"}, {"4E" , "69" , "E2" , "A1"}, 
        {"65" , "DA" , "F4" , "CD"}, {"06" , "05" , "BE" , "D5"}, {"D1" , "34" , "62" , "1F"}, {"C4" , "A6" , "FE" , "8A"}, 
        {"34" , "2E" , "53" , "9D"}, {"A2" , "F3" , "55" , "A0"}, {"05" , "8A" , "E1" , "32"}, {"A4" , "F6" , "EB" , "75"}, 
        {"0B" , "83" , "EC" , "39"}, {"40" , "60" , "EF" , "AA"}, {"5E" , "71" , "9F" , "06"}, {"BD" , "6E" , "10" , "51"}, 
        {"3E" , "21" , "8A" , "F9"}, {"96" , "DD" , "06" , "3D"}, {"DD" , "3E" , "05" , "AE"}, {"4D" , "E6" , "BD" , "46"}, 
        {"91" , "54" , "8D" , "B5"}, {"71" , "C4" , "5D" , "05"}, {"04" , "06" , "D4" , "6F"}, {"60" , "50" , "15" , "FF"}, 
        {"19" , "98" , "FB" , "24"}, {"D6" , "BD" , "E9" , "97"}, {"89" , "40" , "43" , "CC"}, {"67" , "D9" , "9E" , "77"}, 
        {"B0" , "E8" , "42" , "BD"}, {"07" , "89" , "8B" , "88"}, {"E7" , "19" , "5B" , "38"}, {"79" , "C8" , "EE" , "DB"}, 
        {"A1" , "7C" , "0A" , "47"}, {"7C" , "42" , "0F" , "E9"}, {"F8" , "84" , "1E" , "C9"}, {"00" , "00" , "00" , "00"}, 
        {"09" , "80" , "86" , "83"}, {"32" , "2B" , "ED" , "48"}, {"1E" , "11" , "70" , "AC"}, {"6C" , "5A" , "72" , "4E"}, 
        {"FD" , "0E" , "FF" , "FB"}, {"0F" , "85" , "38" , "56"}, {"3D" , "AE" , "D5" , "1E"}, {"36" , "2D" , "39" , "27"}, 
        {"0A" , "0F" , "D9" , "64"}, {"68" , "5C" , "A6" , "21"}, {"9B" , "5B" , "54" , "D1"}, {"24" , "36" , "2E" , "3A"}, 
        {"0C" , "0A" , "67" , "B1"}, {"93" , "57" , "E7" , "0F"}, {"B4" , "EE" , "96" , "D2"}, {"1B" , "9B" , "91" , "9E"}, 
        {"80" , "C0" , "C5" , "4F"}, {"61" , "DC" , "20" , "A2"}, {"5A" , "77" , "4B" , "69"}, {"1C" , "12" , "1A" , "16"}, 
        {"E2" , "93" , "BA" , "0A"}, {"C0" , "A0" , "2A" , "E5"}, {"3C" , "22" , "E0" , "43"}, {"12" , "1B" , "17" , "1D"}, 
        {"0E" , "09" , "0D" , "0B"}, {"F2" , "8B" , "C7" , "AD"}, {"2D" , "B6" , "A8" , "B9"}, {"14" , "1E" , "A9" , "C8"}, 
        {"57" , "F1" , "19" , "85"}, {"AF" , "75" , "07" , "4C"}, {"EE" , "99" , "DD" , "BB"}, {"A3" , "7F" , "60" , "FD"}, 
        {"F7" , "01" , "26" , "9F"}, {"5C" , "72" , "F5" , "BC"}, {"44" , "66" , "3B" , "C5"}, {"5B" , "FB" , "7E" , "34"}, 
        {"8B" , "43" , "29" , "76"}, {"CB" , "23" , "C6" , "DC"}, {"B6" , "ED" , "FC" , "68"}, {"B8" , "E4" , "F1" , "63"}, 
        {"D7" , "31" , "DC" , "CA"}, {"42" , "63" , "85" , "10"}, {"13" , "97" , "22" , "40"}, {"84" , "C6" , "11" , "20"}, 
        {"85" , "4A" , "24" , "7D"}, {"D2" , "BB" , "3D" , "F8"}, {"AE" , "F9" , "32" , "11"}, {"C7" , "29" , "A1" , "6D"}, 
        {"1D" , "9E" , "2F" , "4B"}, {"DC" , "B2" , "30" , "F3"}, {"0D" , "86" , "52" , "EC"}, {"77" , "C1" , "E3" , "D0"}, 
        {"2B" , "B3" , "16" , "6C"}, {"A9" , "70" , "B9" , "99"}, {"11" , "94" , "48" , "FA"}, {"47" , "E9" , "64" , "22"}, 
        {"A8" , "FC" , "8C" , "C4"}, {"A0" , "F0" , "3F" , "1A"}, {"56" , "7D" , "2C" , "D8"}, {"22" , "33" , "90" , "EF"}, 
        {"87" , "49" , "4E" , "C7"}, {"D9" , "38" , "D1" , "C1"}, {"8C" , "CA" , "A2" , "FE"}, {"98" , "D4" , "0B" , "36"}, 
        {"A6" , "F5" , "81" , "CF"}, {"A5" , "7A" , "DE" , "28"}, {"DA" , "B7" , "8E" , "26"}, {"3F" , "AD" , "BF" , "A4"}, 
        {"2C" , "3A" , "9D" , "E4"}, {"50" , "78" , "92" , "0D"}, {"6A" , "5F" , "CC" , "9B"}, {"54" , "7E" , "46" , "62"}, 
        {"F6" , "8D" , "13" , "C2"}, {"90" , "D8" , "B8" , "E8"}, {"2E" , "39" , "F7" , "5E"}, {"82" , "C3" , "AF" , "F5"}, 
        {"9F" , "5D" , "80" , "BE"}, {"69" , "D0" , "93" , "7C"}, {"6F" , "D5" , "2D" , "A9"}, {"CF" , "25" , "12" , "B3"}, 
        {"C8" , "AC" , "99" , "3B"}, {"10" , "18" , "7D" , "A7"}, {"E8" , "9C" , "63" , "6E"}, {"DB" , "3B" , "BB" , "7B"}, 
        {"CD" , "26" , "78" , "09"}, {"6E" , "59" , "18" , "F4"}, {"EC" , "9A" , "B7" , "01"}, {"83" , "4F" , "9A" , "A8"}, 
        {"E6" , "95" , "6E" , "65"}, {"AA" , "FF" , "E6" , "7E"}, {"21" , "BC" , "CF" , "08"}, {"EF" , "15" , "E8" , "E6"}, 
        {"BA" , "E7" , "9B" , "D9"}, {"4A" , "6F" , "36" , "CE"}, {"EA" , "9F" , "09" , "D4"}, {"29" , "B0" , "7C" , "D6"}, 
        {"31" , "A4" , "B2" , "AF"}, {"2A" , "3F" , "23" , "31"}, {"C6" , "A5" , "94" , "30"}, {"35" , "A2" , "66" , "C0"}, 
        {"74" , "4E" , "BC" , "37"}, {"FC" , "82" , "CA" , "A6"}, {"E0" , "90" , "D0" , "B0"}, {"33" , "A7" , "D8" , "15"}, 
        {"F1" , "04" , "98" , "4A"}, {"41" , "EC" , "DA" , "F7"}, {"7F" , "CD" , "50" , "0E"}, {"17" , "91" , "F6" , "2F"}, 
        {"76" , "4D" , "D6" , "8D"}, {"43" , "EF" , "B0" , "4D"}, {"CC" , "AA" , "4D" , "54"}, {"E4" , "96" , "04" , "DF"}, 
        {"9E" , "D1" , "B5" , "E3"}, {"4C" , "6A" , "88" , "1B"}, {"C1" , "2C" , "1F" , "B8"}, {"46" , "65" , "51" , "7F"}, 
        {"9D" , "5E" , "EA" , "04"}, {"01" , "8C" , "35" , "5D"}, {"FA" , "87" , "74" , "73"}, {"FB" , "0B" , "41" , "2E"}, 
        {"B3" , "67" , "1D" , "5A"}, {"92" , "DB" , "D2" , "52"}, {"E9" , "10" , "56" , "33"}, {"6D" , "D6" , "47" , "13"}, 
        {"9A" , "D7" , "61" , "8C"}, {"37" , "A1" , "0C" , "7A"}, {"59" , "F8" , "14" , "8E"}, {"EB" , "13" , "3C" , "89"}, 
        {"CE" , "A9" , "27" , "EE"}, {"B7" , "61" , "C9" , "35"}, {"E1" , "1C" , "E5" , "ED"}, {"7A" , "47" , "B1" , "3C"}, 
        {"9C" , "D2" , "DF" , "59"}, {"55" , "F2" , "73" , "3F"}, {"18" , "14" , "CE" , "79"}, {"73" , "C7" , "37" , "BF"}, 
        {"53" , "F7" , "CD" , "EA"}, {"5F" , "FD" , "AA" , "5B"}, {"DF" , "3D" , "6F" , "14"}, {"78" , "44" , "DB" , "86"}, 
        {"CA" , "AF" , "F3" , "81"}, {"B9" , "68" , "C4" , "3E"}, {"38" , "24" , "34" , "2C"}, {"C2" , "A3" , "40" , "5F"}, 
        {"16" , "1D" , "C3" , "72"}, {"BC" , "E2" , "25" , "0C"}, {"28" , "3C" , "49" , "8B"}, {"FF" , "0D" , "95" , "41"}, 
        {"39" , "A8" , "01" , "71"}, {"08" , "0C" , "B3" , "DE"}, {"D8" , "B4" , "E4" , "9C"}, {"64" , "56" , "C1" , "90"}, 
        {"7B" , "CB" , "84" , "61"}, {"D5" , "32" , "B6" , "70"}, {"48" , "6C" , "5C" , "74"}, {"D0" , "B8" , "57" , "42"}
    };    
    
    private static final long[] RT0 = getRT0();
    private static final long[] RT1 = getRT1();
    private static final long[] RT2 = getRT2();
    private static final long[] RT3 = getRT3();
    
    /**
     * getRT*() functions
     * 
     * Get each array within RT and then rearrange.
     * 
     */
    
    //#define V(a,b,c,d) 0x##a##b##c##d
    private static final long[] getRT0()
    {
        long[] RT0 = new long[256];
        for (int i = 0; i < RT.length; i++)
        {
            RT0[i] = Long.decode("0x" + RT[i][0] + RT[i][1] + RT[i][2] + RT[i][3]);
        }
        return RT0;
    }
    
    //#define V(a,b,c,d) 0x##d##a##b##c
    private static final long[] getRT1()
    {
        long[] RT1 = new long[256];
        for (int i = 0; i < RT.length; i++)
        {
            RT1[i] = Long.decode("0x" +  RT[i][3] + RT[i][0] + RT[i][1] + RT[i][2]);
        }
        return RT1;
    }
    
    //#define V(a,b,c,d) 0x##c##d##a##b
    private static final long[] getRT2()
    {
        long[] RT2 = new long[256];
        for (int i = 0; i < RT.length; i++)
        {
            RT2[i] = Long.decode("0x" +  RT[i][2] + RT[i][3] + RT[i][0] + RT[i][1]);
        }
        return RT2;
    }

    //#define V(a,b,c,d) 0x##b##c##d##a
    private static final long[] getRT3()
    {
        long[] RT3 = new long[256];
        for (int i = 0; i < RT.length; i++)
        {
            RT3[i] = Long.decode("0x" +  RT[i][1] + RT[i][2] + RT[i][3] + RT[i][0]);
        }
        return RT3;
    }
    /*
     * Round constants
     */
    private static final long[] RCON =
    {
        0x01000000l, 0x02000000l, 0x04000000l, 0x08000000l,
        0x10000000l, 0x20000000l, 0x40000000l, 0x80000000l,
        0x1B000000l, 0x36000000l
    };

    /*
     * Decryption key schedule tables
     */

    private static final long[] KT0 = 
    {
	    0x00000000, 0x0E090D0B, 0x1C121A16, 0x121B171D, 0x3824342C, 0x362D3927, 0x24362E3A, 0x2A3F2331, 
	    0x70486858, 0x7E416553, 0x6C5A724E, 0x62537F45, 0x486C5C74, 0x4665517F, 0x547E4662, 0x5A774B69, 
	    0xE090D0B0, 0xEE99DDBB, 0xFC82CAA6, 0xF28BC7AD, 0xD8B4E49C, 0xD6BDE997, 0xC4A6FE8A, 0xCAAFF381, 
	    0x90D8B8E8, 0x9ED1B5E3, 0x8CCAA2FE, 0x82C3AFF5, 0xA8FC8CC4, 0xA6F581CF, 0xB4EE96D2, 0xBAE79BD9, 
	    0xDB3BBB7B, 0xD532B670, 0xC729A16D, 0xC920AC66, 0xE31F8F57, 0xED16825C, 0xFF0D9541, 0xF104984A, 
	    0xAB73D323, 0xA57ADE28, 0xB761C935, 0xB968C43E, 0x9357E70F, 0x9D5EEA04, 0x8F45FD19, 0x814CF012, 
	    0x3BAB6BCB, 0x35A266C0, 0x27B971DD, 0x29B07CD6, 0x038F5FE7, 0x0D8652EC, 0x1F9D45F1, 0x119448FA, 
	    0x4BE30393, 0x45EA0E98, 0x57F11985, 0x59F8148E, 0x73C737BF, 0x7DCE3AB4, 0x6FD52DA9, 0x61DC20A2, 
	    0xAD766DF6, 0xA37F60FD, 0xB16477E0, 0xBF6D7AEB, 0x955259DA, 0x9B5B54D1, 0x894043CC, 0x87494EC7, 
	    0xDD3E05AE, 0xD33708A5, 0xC12C1FB8, 0xCF2512B3, 0xE51A3182, 0xEB133C89, 0xF9082B94, 0xF701269F, 
	    0x4DE6BD46, 0x43EFB04D, 0x51F4A750, 0x5FFDAA5B, 0x75C2896A, 0x7BCB8461, 0x69D0937C, 0x67D99E77, 
	    0x3DAED51E, 0x33A7D815, 0x21BCCF08, 0x2FB5C203, 0x058AE132, 0x0B83EC39, 0x1998FB24, 0x1791F62F, 
	    0x764DD68D, 0x7844DB86, 0x6A5FCC9B, 0x6456C190, 0x4E69E2A1, 0x4060EFAA, 0x527BF8B7, 0x5C72F5BC, 
	    0x0605BED5, 0x080CB3DE, 0x1A17A4C3, 0x141EA9C8, 0x3E218AF9, 0x302887F2, 0x223390EF, 0x2C3A9DE4, 
	    0x96DD063D, 0x98D40B36, 0x8ACF1C2B, 0x84C61120, 0xAEF93211, 0xA0F03F1A, 0xB2EB2807, 0xBCE2250C, 
	    0xE6956E65, 0xE89C636E, 0xFA877473, 0xF48E7978, 0xDEB15A49, 0xD0B85742, 0xC2A3405F, 0xCCAA4D54, 
	    0x41ECDAF7, 0x4FE5D7FC, 0x5DFEC0E1, 0x53F7CDEA, 0x79C8EEDB, 0x77C1E3D0, 0x65DAF4CD, 0x6BD3F9C6, 
	    0x31A4B2AF, 0x3FADBFA4, 0x2DB6A8B9, 0x23BFA5B2, 0x09808683, 0x07898B88, 0x15929C95, 0x1B9B919E, 
	    0xA17C0A47, 0xAF75074C, 0xBD6E1051, 0xB3671D5A, 0x99583E6B, 0x97513360, 0x854A247D, 0x8B432976, 
	    0xD134621F, 0xDF3D6F14, 0xCD267809, 0xC32F7502, 0xE9105633, 0xE7195B38, 0xF5024C25, 0xFB0B412E, 
	    0x9AD7618C, 0x94DE6C87, 0x86C57B9A, 0x88CC7691, 0xA2F355A0, 0xACFA58AB, 0xBEE14FB6, 0xB0E842BD, 
	    0xEA9F09D4, 0xE49604DF, 0xF68D13C2, 0xF8841EC9, 0xD2BB3DF8, 0xDCB230F3, 0xCEA927EE, 0xC0A02AE5, 
	    0x7A47B13C, 0x744EBC37, 0x6655AB2A, 0x685CA621, 0x42638510, 0x4C6A881B, 0x5E719F06, 0x5078920D, 
	    0x0A0FD964, 0x0406D46F, 0x161DC372, 0x1814CE79, 0x322BED48, 0x3C22E043, 0x2E39F75E, 0x2030FA55, 
	    0xEC9AB701, 0xE293BA0A, 0xF088AD17, 0xFE81A01C, 0xD4BE832D, 0xDAB78E26, 0xC8AC993B, 0xC6A59430, 
	    0x9CD2DF59, 0x92DBD252, 0x80C0C54F, 0x8EC9C844, 0xA4F6EB75, 0xAAFFE67E, 0xB8E4F163, 0xB6EDFC68, 
	    0x0C0A67B1, 0x02036ABA, 0x10187DA7, 0x1E1170AC, 0x342E539D, 0x3A275E96, 0x283C498B, 0x26354480, 
	    0x7C420FE9, 0x724B02E2, 0x605015FF, 0x6E5918F4, 0x44663BC5, 0x4A6F36CE, 0x587421D3, 0x567D2CD8, 
	    0x37A10C7A, 0x39A80171, 0x2BB3166C, 0x25BA1B67, 0x0F853856, 0x018C355D, 0x13972240, 0x1D9E2F4B, 
	    0x47E96422, 0x49E06929, 0x5BFB7E34, 0x55F2733F, 0x7FCD500E, 0x71C45D05, 0x63DF4A18, 0x6DD64713, 
	    0xD731DCCA, 0xD938D1C1, 0xCB23C6DC, 0xC52ACBD7, 0xEF15E8E6, 0xE11CE5ED, 0xF307F2F0, 0xFD0EFFFB, 
	    0xA779B492, 0xA970B999, 0xBB6BAE84, 0xB562A38F, 0x9F5D80BE, 0x91548DB5, 0x834F9AA8, 0x8D4697A3, 
    };
    
    private static final long[] KT1 = 
    {
	    0x00000000, 0x0B0E090D, 0x161C121A, 0x1D121B17, 0x2C382434, 0x27362D39, 0x3A24362E, 0x312A3F23, 
	    0x58704868, 0x537E4165, 0x4E6C5A72, 0x4562537F, 0x74486C5C, 0x7F466551, 0x62547E46, 0x695A774B, 
	    0xB0E090D0, 0xBBEE99DD, 0xA6FC82CA, 0xADF28BC7, 0x9CD8B4E4, 0x97D6BDE9, 0x8AC4A6FE, 0x81CAAFF3, 
	    0xE890D8B8, 0xE39ED1B5, 0xFE8CCAA2, 0xF582C3AF, 0xC4A8FC8C, 0xCFA6F581, 0xD2B4EE96, 0xD9BAE79B, 
	    0x7BDB3BBB, 0x70D532B6, 0x6DC729A1, 0x66C920AC, 0x57E31F8F, 0x5CED1682, 0x41FF0D95, 0x4AF10498, 
	    0x23AB73D3, 0x28A57ADE, 0x35B761C9, 0x3EB968C4, 0x0F9357E7, 0x049D5EEA, 0x198F45FD, 0x12814CF0, 
	    0xCB3BAB6B, 0xC035A266, 0xDD27B971, 0xD629B07C, 0xE7038F5F, 0xEC0D8652, 0xF11F9D45, 0xFA119448, 
	    0x934BE303, 0x9845EA0E, 0x8557F119, 0x8E59F814, 0xBF73C737, 0xB47DCE3A, 0xA96FD52D, 0xA261DC20, 
	    0xF6AD766D, 0xFDA37F60, 0xE0B16477, 0xEBBF6D7A, 0xDA955259, 0xD19B5B54, 0xCC894043, 0xC787494E, 
	    0xAEDD3E05, 0xA5D33708, 0xB8C12C1F, 0xB3CF2512, 0x82E51A31, 0x89EB133C, 0x94F9082B, 0x9FF70126, 
	    0x464DE6BD, 0x4D43EFB0, 0x5051F4A7, 0x5B5FFDAA, 0x6A75C289, 0x617BCB84, 0x7C69D093, 0x7767D99E, 
	    0x1E3DAED5, 0x1533A7D8, 0x0821BCCF, 0x032FB5C2, 0x32058AE1, 0x390B83EC, 0x241998FB, 0x2F1791F6, 
	    0x8D764DD6, 0x867844DB, 0x9B6A5FCC, 0x906456C1, 0xA14E69E2, 0xAA4060EF, 0xB7527BF8, 0xBC5C72F5, 
	    0xD50605BE, 0xDE080CB3, 0xC31A17A4, 0xC8141EA9, 0xF93E218A, 0xF2302887, 0xEF223390, 0xE42C3A9D, 
	    0x3D96DD06, 0x3698D40B, 0x2B8ACF1C, 0x2084C611, 0x11AEF932, 0x1AA0F03F, 0x07B2EB28, 0x0CBCE225, 
	    0x65E6956E, 0x6EE89C63, 0x73FA8774, 0x78F48E79, 0x49DEB15A, 0x42D0B857, 0x5FC2A340, 0x54CCAA4D, 
	    0xF741ECDA, 0xFC4FE5D7, 0xE15DFEC0, 0xEA53F7CD, 0xDB79C8EE, 0xD077C1E3, 0xCD65DAF4, 0xC66BD3F9, 
	    0xAF31A4B2, 0xA43FADBF, 0xB92DB6A8, 0xB223BFA5, 0x83098086, 0x8807898B, 0x9515929C, 0x9E1B9B91, 
	    0x47A17C0A, 0x4CAF7507, 0x51BD6E10, 0x5AB3671D, 0x6B99583E, 0x60975133, 0x7D854A24, 0x768B4329, 
	    0x1FD13462, 0x14DF3D6F, 0x09CD2678, 0x02C32F75, 0x33E91056, 0x38E7195B, 0x25F5024C, 0x2EFB0B41, 
	    0x8C9AD761, 0x8794DE6C, 0x9A86C57B, 0x9188CC76, 0xA0A2F355, 0xABACFA58, 0xB6BEE14F, 0xBDB0E842, 
	    0xD4EA9F09, 0xDFE49604, 0xC2F68D13, 0xC9F8841E, 0xF8D2BB3D, 0xF3DCB230, 0xEECEA927, 0xE5C0A02A, 
	    0x3C7A47B1, 0x37744EBC, 0x2A6655AB, 0x21685CA6, 0x10426385, 0x1B4C6A88, 0x065E719F, 0x0D507892, 
	    0x640A0FD9, 0x6F0406D4, 0x72161DC3, 0x791814CE, 0x48322BED, 0x433C22E0, 0x5E2E39F7, 0x552030FA, 
	    0x01EC9AB7, 0x0AE293BA, 0x17F088AD, 0x1CFE81A0, 0x2DD4BE83, 0x26DAB78E, 0x3BC8AC99, 0x30C6A594, 
	    0x599CD2DF, 0x5292DBD2, 0x4F80C0C5, 0x448EC9C8, 0x75A4F6EB, 0x7EAAFFE6, 0x63B8E4F1, 0x68B6EDFC, 
	    0xB10C0A67, 0xBA02036A, 0xA710187D, 0xAC1E1170, 0x9D342E53, 0x963A275E, 0x8B283C49, 0x80263544, 
	    0xE97C420F, 0xE2724B02, 0xFF605015, 0xF46E5918, 0xC544663B, 0xCE4A6F36, 0xD3587421, 0xD8567D2C, 
	    0x7A37A10C, 0x7139A801, 0x6C2BB316, 0x6725BA1B, 0x560F8538, 0x5D018C35, 0x40139722, 0x4B1D9E2F, 
	    0x2247E964, 0x2949E069, 0x345BFB7E, 0x3F55F273, 0x0E7FCD50, 0x0571C45D, 0x1863DF4A, 0x136DD647, 
	    0xCAD731DC, 0xC1D938D1, 0xDCCB23C6, 0xD7C52ACB, 0xE6EF15E8, 0xEDE11CE5, 0xF0F307F2, 0xFBFD0EFF, 
	    0x92A779B4, 0x99A970B9, 0x84BB6BAE, 0x8FB562A3, 0xBE9F5D80, 0xB591548D, 0xA8834F9A, 0xA38D4697, 
	    };
    private static final long[] KT2 = 
    {
	    0x00000000, 0x0D0B0E09, 0x1A161C12, 0x171D121B, 0x342C3824, 0x3927362D, 0x2E3A2436, 0x23312A3F, 
	    0x68587048, 0x65537E41, 0x724E6C5A, 0x7F456253, 0x5C74486C, 0x517F4665, 0x4662547E, 0x4B695A77, 
	    0xD0B0E090, 0xDDBBEE99, 0xCAA6FC82, 0xC7ADF28B, 0xE49CD8B4, 0xE997D6BD, 0xFE8AC4A6, 0xF381CAAF, 
	    0xB8E890D8, 0xB5E39ED1, 0xA2FE8CCA, 0xAFF582C3, 0x8CC4A8FC, 0x81CFA6F5, 0x96D2B4EE, 0x9BD9BAE7, 
	    0xBB7BDB3B, 0xB670D532, 0xA16DC729, 0xAC66C920, 0x8F57E31F, 0x825CED16, 0x9541FF0D, 0x984AF104, 
	    0xD323AB73, 0xDE28A57A, 0xC935B761, 0xC43EB968, 0xE70F9357, 0xEA049D5E, 0xFD198F45, 0xF012814C, 
	    0x6BCB3BAB, 0x66C035A2, 0x71DD27B9, 0x7CD629B0, 0x5FE7038F, 0x52EC0D86, 0x45F11F9D, 0x48FA1194, 
	    0x03934BE3, 0x0E9845EA, 0x198557F1, 0x148E59F8, 0x37BF73C7, 0x3AB47DCE, 0x2DA96FD5, 0x20A261DC, 
	    0x6DF6AD76, 0x60FDA37F, 0x77E0B164, 0x7AEBBF6D, 0x59DA9552, 0x54D19B5B, 0x43CC8940, 0x4EC78749, 
	    0x05AEDD3E, 0x08A5D337, 0x1FB8C12C, 0x12B3CF25, 0x3182E51A, 0x3C89EB13, 0x2B94F908, 0x269FF701, 
	    0xBD464DE6, 0xB04D43EF, 0xA75051F4, 0xAA5B5FFD, 0x896A75C2, 0x84617BCB, 0x937C69D0, 0x9E7767D9, 
	    0xD51E3DAE, 0xD81533A7, 0xCF0821BC, 0xC2032FB5, 0xE132058A, 0xEC390B83, 0xFB241998, 0xF62F1791, 
	    0xD68D764D, 0xDB867844, 0xCC9B6A5F, 0xC1906456, 0xE2A14E69, 0xEFAA4060, 0xF8B7527B, 0xF5BC5C72, 
	    0xBED50605, 0xB3DE080C, 0xA4C31A17, 0xA9C8141E, 0x8AF93E21, 0x87F23028, 0x90EF2233, 0x9DE42C3A, 
	    0x063D96DD, 0x0B3698D4, 0x1C2B8ACF, 0x112084C6, 0x3211AEF9, 0x3F1AA0F0, 0x2807B2EB, 0x250CBCE2, 
	    0x6E65E695, 0x636EE89C, 0x7473FA87, 0x7978F48E, 0x5A49DEB1, 0x5742D0B8, 0x405FC2A3, 0x4D54CCAA, 
	    0xDAF741EC, 0xD7FC4FE5, 0xC0E15DFE, 0xCDEA53F7, 0xEEDB79C8, 0xE3D077C1, 0xF4CD65DA, 0xF9C66BD3, 
	    0xB2AF31A4, 0xBFA43FAD, 0xA8B92DB6, 0xA5B223BF, 0x86830980, 0x8B880789, 0x9C951592, 0x919E1B9B, 
	    0x0A47A17C, 0x074CAF75, 0x1051BD6E, 0x1D5AB367, 0x3E6B9958, 0x33609751, 0x247D854A, 0x29768B43, 
	    0x621FD134, 0x6F14DF3D, 0x7809CD26, 0x7502C32F, 0x5633E910, 0x5B38E719, 0x4C25F502, 0x412EFB0B, 
	    0x618C9AD7, 0x6C8794DE, 0x7B9A86C5, 0x769188CC, 0x55A0A2F3, 0x58ABACFA, 0x4FB6BEE1, 0x42BDB0E8, 
	    0x09D4EA9F, 0x04DFE496, 0x13C2F68D, 0x1EC9F884, 0x3DF8D2BB, 0x30F3DCB2, 0x27EECEA9, 0x2AE5C0A0, 
	    0xB13C7A47, 0xBC37744E, 0xAB2A6655, 0xA621685C, 0x85104263, 0x881B4C6A, 0x9F065E71, 0x920D5078, 
	    0xD9640A0F, 0xD46F0406, 0xC372161D, 0xCE791814, 0xED48322B, 0xE0433C22, 0xF75E2E39, 0xFA552030, 
	    0xB701EC9A, 0xBA0AE293, 0xAD17F088, 0xA01CFE81, 0x832DD4BE, 0x8E26DAB7, 0x993BC8AC, 0x9430C6A5, 
	    0xDF599CD2, 0xD25292DB, 0xC54F80C0, 0xC8448EC9, 0xEB75A4F6, 0xE67EAAFF, 0xF163B8E4, 0xFC68B6ED, 
	    0x67B10C0A, 0x6ABA0203, 0x7DA71018, 0x70AC1E11, 0x539D342E, 0x5E963A27, 0x498B283C, 0x44802635, 
	    0x0FE97C42, 0x02E2724B, 0x15FF6050, 0x18F46E59, 0x3BC54466, 0x36CE4A6F, 0x21D35874, 0x2CD8567D, 
	    0x0C7A37A1, 0x017139A8, 0x166C2BB3, 0x1B6725BA, 0x38560F85, 0x355D018C, 0x22401397, 0x2F4B1D9E, 
	    0x642247E9, 0x692949E0, 0x7E345BFB, 0x733F55F2, 0x500E7FCD, 0x5D0571C4, 0x4A1863DF, 0x47136DD6, 
	    0xDCCAD731, 0xD1C1D938, 0xC6DCCB23, 0xCBD7C52A, 0xE8E6EF15, 0xE5EDE11C, 0xF2F0F307, 0xFFFBFD0E, 
	    0xB492A779, 0xB999A970, 0xAE84BB6B, 0xA38FB562, 0x80BE9F5D, 0x8DB59154, 0x9AA8834F, 0x97A38D46, 
    };
    
    private static final long[] KT3 = 
    {
	    0x00000000, 0x090D0B0E, 0x121A161C, 0x1B171D12, 0x24342C38, 0x2D392736, 0x362E3A24, 0x3F23312A, 
	    0x48685870, 0x4165537E, 0x5A724E6C, 0x537F4562, 0x6C5C7448, 0x65517F46, 0x7E466254, 0x774B695A, 
	    0x90D0B0E0, 0x99DDBBEE, 0x82CAA6FC, 0x8BC7ADF2, 0xB4E49CD8, 0xBDE997D6, 0xA6FE8AC4, 0xAFF381CA, 
	    0xD8B8E890, 0xD1B5E39E, 0xCAA2FE8C, 0xC3AFF582, 0xFC8CC4A8, 0xF581CFA6, 0xEE96D2B4, 0xE79BD9BA, 
	    0x3BBB7BDB, 0x32B670D5, 0x29A16DC7, 0x20AC66C9, 0x1F8F57E3, 0x16825CED, 0x0D9541FF, 0x04984AF1, 
	    0x73D323AB, 0x7ADE28A5, 0x61C935B7, 0x68C43EB9, 0x57E70F93, 0x5EEA049D, 0x45FD198F, 0x4CF01281, 
	    0xAB6BCB3B, 0xA266C035, 0xB971DD27, 0xB07CD629, 0x8F5FE703, 0x8652EC0D, 0x9D45F11F, 0x9448FA11, 
	    0xE303934B, 0xEA0E9845, 0xF1198557, 0xF8148E59, 0xC737BF73, 0xCE3AB47D, 0xD52DA96F, 0xDC20A261, 
	    0x766DF6AD, 0x7F60FDA3, 0x6477E0B1, 0x6D7AEBBF, 0x5259DA95, 0x5B54D19B, 0x4043CC89, 0x494EC787, 
	    0x3E05AEDD, 0x3708A5D3, 0x2C1FB8C1, 0x2512B3CF, 0x1A3182E5, 0x133C89EB, 0x082B94F9, 0x01269FF7, 
	    0xE6BD464D, 0xEFB04D43, 0xF4A75051, 0xFDAA5B5F, 0xC2896A75, 0xCB84617B, 0xD0937C69, 0xD99E7767, 
	    0xAED51E3D, 0xA7D81533, 0xBCCF0821, 0xB5C2032F, 0x8AE13205, 0x83EC390B, 0x98FB2419, 0x91F62F17, 
	    0x4DD68D76, 0x44DB8678, 0x5FCC9B6A, 0x56C19064, 0x69E2A14E, 0x60EFAA40, 0x7BF8B752, 0x72F5BC5C, 
	    0x05BED506, 0x0CB3DE08, 0x17A4C31A, 0x1EA9C814, 0x218AF93E, 0x2887F230, 0x3390EF22, 0x3A9DE42C, 
	    0xDD063D96, 0xD40B3698, 0xCF1C2B8A, 0xC6112084, 0xF93211AE, 0xF03F1AA0, 0xEB2807B2, 0xE2250CBC, 
	    0x956E65E6, 0x9C636EE8, 0x877473FA, 0x8E7978F4, 0xB15A49DE, 0xB85742D0, 0xA3405FC2, 0xAA4D54CC, 
	    0xECDAF741, 0xE5D7FC4F, 0xFEC0E15D, 0xF7CDEA53, 0xC8EEDB79, 0xC1E3D077, 0xDAF4CD65, 0xD3F9C66B, 
	    0xA4B2AF31, 0xADBFA43F, 0xB6A8B92D, 0xBFA5B223, 0x80868309, 0x898B8807, 0x929C9515, 0x9B919E1B, 
	    0x7C0A47A1, 0x75074CAF, 0x6E1051BD, 0x671D5AB3, 0x583E6B99, 0x51336097, 0x4A247D85, 0x4329768B, 
	    0x34621FD1, 0x3D6F14DF, 0x267809CD, 0x2F7502C3, 0x105633E9, 0x195B38E7, 0x024C25F5, 0x0B412EFB, 
	    0xD7618C9A, 0xDE6C8794, 0xC57B9A86, 0xCC769188, 0xF355A0A2, 0xFA58ABAC, 0xE14FB6BE, 0xE842BDB0, 
	    0x9F09D4EA, 0x9604DFE4, 0x8D13C2F6, 0x841EC9F8, 0xBB3DF8D2, 0xB230F3DC, 0xA927EECE, 0xA02AE5C0, 
	    0x47B13C7A, 0x4EBC3774, 0x55AB2A66, 0x5CA62168, 0x63851042, 0x6A881B4C, 0x719F065E, 0x78920D50, 
	    0x0FD9640A, 0x06D46F04, 0x1DC37216, 0x14CE7918, 0x2BED4832, 0x22E0433C, 0x39F75E2E, 0x30FA5520, 
	    0x9AB701EC, 0x93BA0AE2, 0x88AD17F0, 0x81A01CFE, 0xBE832DD4, 0xB78E26DA, 0xAC993BC8, 0xA59430C6, 
	    0xD2DF599C, 0xDBD25292, 0xC0C54F80, 0xC9C8448E, 0xF6EB75A4, 0xFFE67EAA, 0xE4F163B8, 0xEDFC68B6, 
	    0x0A67B10C, 0x036ABA02, 0x187DA710, 0x1170AC1E, 0x2E539D34, 0x275E963A, 0x3C498B28, 0x35448026, 
	    0x420FE97C, 0x4B02E272, 0x5015FF60, 0x5918F46E, 0x663BC544, 0x6F36CE4A, 0x7421D358, 0x7D2CD856, 
	    0xA10C7A37, 0xA8017139, 0xB3166C2B, 0xBA1B6725, 0x8538560F, 0x8C355D01, 0x97224013, 0x9E2F4B1D, 
	    0xE9642247, 0xE0692949, 0xFB7E345B, 0xF2733F55, 0xCD500E7F, 0xC45D0571, 0xDF4A1863, 0xD647136D, 
	    0x31DCCAD7, 0x38D1C1D9, 0x23C6DCCB, 0x2ACBD7C5, 0x15E8E6EF, 0x1CE5EDE1, 0x07F2F0F3, 0x0EFFFBFD, 
	    0x79B492A7, 0x70B999A9, 0x6BAE84BB, 0x62A38FB5, 0x5D80BE9F, 0x548DB591, 0x4F9AA883, 0x4697A38D, 
    };

    /**
     * Accessor methods for the tables to return "unsigned" 
     * values.
     */
    private int getFSb(int index)
    {
        if (index < 0) index += 256;
        return byteSignedToUnsigned(FSb[index]);
    }
    
    private int getRSb(int index)
    {
        if (index < 0) index += 256;
        return byteSignedToUnsigned(RSb[index]);
    }
    
    private BigInteger getKT0(int index)
    {        
        if (index < 0) index += 256;
        return longSignedToUnsigned(KT0[index]);
    }    
    
    private BigInteger getKT1(int index)
    {        
        if (index < 0) index += 256;
        return longSignedToUnsigned(KT1[index]);
    }    
    
    private BigInteger getKT2(int index)
    {        
        if (index < 0) index += 256;
        return longSignedToUnsigned(KT2[index]);
    }
    
    private BigInteger getKT3(int index)
    {        
        if (index < 0) index += 256;
        return longSignedToUnsigned(KT3[index]);
    }
    
    private BigInteger getRCON(int index)
    {
        if (index < 0) index += 256;
        return longSignedToUnsigned(RCON[index]);
    }
    
    public static BigInteger longSignedToUnsigned(long l)
    {
        BigInteger result = BigInteger.valueOf(l);
        if (result.compareTo(BigInteger.ZERO) < 0)
        {
            result = result.add(BigInteger.valueOf(4294967296l)); //2^32
        }
        return result;  
    }
    
    public static long intSignedToUnsigned(int i)
    {
        long result = i;
        if (result < 0)
        {
            result += 65536; //2^16
        }
        return result;  
    }
    
    public static int byteSignedToUnsigned(byte b)
    {
        int result = b;
        if (result < 0)
        {
            result += 256;
        }
        return result;
    }
    
    public static byte intUnsignedToSigned(int i)
    {
        if (i > 128)
        {
            i -= 256;
        }
        return (byte) i;
        
    }
    
 /***********************************************************************************************
  * AES key schedule
  **********************************************************************************************/
    /**
    * AES key schedule
    *
    * @param ctx      AES context to be initialized
    * @param key      the secret key
    * @param keysize  must be 128, 192 or 256
    */
   public void aesSetKey(AESContext ctx, int[] key, int keysize)
   {
       int RKIndex = 0; //Emulate pointer in RK array
       int SKIndex = 0; //Emulate pointer in SK array

       switch (keysize)
       {
           case 128:
               ctx.nr = 10;
               break;
           case 192:
               ctx.nr = 12;
               break;
           case 256:
               ctx.nr = 14;
               break;
           default:
               return;
       }

       BigInteger[] RK = ctx.erk;

       for (int i = 0; i < (keysize >> 5); i++)
       {
           RK[i] = GET_UNS_32_BE(key, i << 2);
       }
       /*
        * setup encryption round keys
        */
       switch (ctx.nr)
       {
           case 10:
               for (int i = 0; i < 10; i++, RKIndex += 4)
               {
                   RK[RKIndex + 4] = RK[RKIndex].xor ( getRCON(i) )
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 3].shiftRight(16).byteValue() & 0xFF)
                                ).shiftLeft(24)) 
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 3].shiftRight(8 ).byteValue() & 0xFF)
                                ).shiftLeft(16))
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 3]               .byteValue() & 0xFF)
                                ).shiftLeft(8))
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 3].shiftRight(24).byteValue() & 0xFF)      
                                ) );
                   
//                   if (RK[RKIndex + 4].compareTo(BigInteger.ZERO) < 0)
//                   {
//                       RK[RKIndex + 4] += Math.pow(2, 32);
//                   }
                   RK[RKIndex + 5] = RK[RKIndex + 1].xor(RK[RKIndex + 4]);
                   RK[RKIndex + 6] = RK[RKIndex + 2].xor(RK[RKIndex + 5]);
                   RK[RKIndex + 7] = RK[RKIndex + 3].xor(RK[RKIndex + 6]);
               }
               break;

           case 12:
               for (int i = 0; i < 8; i++, RKIndex += 6)
               {
                        RK[RKIndex + 6] = RK[RKIndex].xor ( getRCON(i) )
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 5].shiftRight(16).and(BigInteger.valueOf(0xFF)).byteValue() )
                                ).shiftLeft(24)) 
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 5].shiftRight(8 ).and(BigInteger.valueOf(0xFF)).byteValue() )
                                ).shiftLeft(16))
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 5]               .and(BigInteger.valueOf(0xFF)).byteValue() )
                                ).shiftLeft(8))
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 5].shiftRight(24).and(BigInteger.valueOf(0xFF)).byteValue() )     
                                ) );
                   RK[RKIndex + 7 ] = RK[RKIndex + 1].xor(RK[RKIndex + 6 ]);
                   RK[RKIndex + 8 ] = RK[RKIndex + 2].xor(RK[RKIndex + 7 ]);
                   RK[RKIndex + 9 ] = RK[RKIndex + 3].xor(RK[RKIndex + 8 ]);
                   RK[RKIndex + 10] = RK[RKIndex + 4].xor(RK[RKIndex + 9 ]);
                   RK[RKIndex + 11] = RK[RKIndex + 5].xor(RK[RKIndex + 10]);
               }
               break;

           case 14:
               for (int i = 0; i < 7 ; i++, RKIndex += 8)
               {
                    RK[RKIndex + 8] = RK[RKIndex].xor ( getRCON(i) )
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 7].shiftRight(16).and(BigInteger.valueOf(0xFF)).byteValue() )
                                ).shiftLeft(24)) 
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 7].shiftRight(8 ).and(BigInteger.valueOf(0xFF)).byteValue() )
                                ).shiftLeft(16))
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 7]               .and(BigInteger.valueOf(0xFF)).byteValue() )
                                ).shiftLeft(8))
                           .xor( BigInteger.valueOf(
                                   getFSb( RK[RKIndex + 7].shiftRight(24).and(BigInteger.valueOf(0xFF)).byteValue() )     
                                ) );

                   RK[RKIndex + 9 ] = RK[RKIndex + 1].xor(RK[RKIndex + 8 ]);
                   RK[RKIndex + 10] = RK[RKIndex + 2].xor(RK[RKIndex + 9 ]);
                   RK[RKIndex + 11] = RK[RKIndex + 3].xor(RK[RKIndex + 10]);

                   RK[RKIndex + 12] = RK[RKIndex + 4]
                            .xor( BigInteger.valueOf(getFSb( (byte) (RK[RKIndex + 11].longValue() >> 24) )
                                   ).shiftLeft(24))
                            .xor( BigInteger.valueOf(getFSb( (byte) (RK[RKIndex + 11].longValue() >> 16) )
                                ).shiftLeft(16))
                            .xor( BigInteger.valueOf(getFSb( (byte) (RK[RKIndex + 11].longValue() >> 8 ) )
                                ).shiftLeft(8))
                            .xor( BigInteger.valueOf(getFSb( (byte) (RK[RKIndex + 11].longValue()      )))) ;

                   RK[RKIndex + 13] = RK[RKIndex + 5].xor(RK[RKIndex + 12]);
                   RK[RKIndex + 14] = RK[RKIndex + 6].xor(RK[RKIndex + 13]);
                   RK[RKIndex + 15] = RK[RKIndex + 7].xor(RK[RKIndex + 14]);
               }
               break;

           default:

               break;
       }      
       
        BigInteger[] SK = ctx.drk;
        SK[0] = RK[RKIndex++];
        SK[1] = RK[RKIndex++];
        SK[2] = RK[RKIndex++];
        SK[3] = RK[RKIndex++];
        SKIndex = 4;
        
        for(int i = 1; i < ctx.nr; i++ )
        {
            RKIndex -= 8;

            SK[SKIndex] = 
                    getKT0( RK[RKIndex].shiftRight(24).byteValue() & 0xFF) 
                    .xor(
                    getKT1( RK[RKIndex].shiftRight(16).byteValue() & 0xFF) )
                    .xor(
                    getKT2( RK[RKIndex].shiftRight(8 ).byteValue() & 0xFF) )
                    .xor(
                    getKT3( RK[RKIndex]               .byteValue() & 0xFF)  
                    );
//            if (SK[SKIndex] < 0) SK[SKIndex] += Math.pow(2, 32);
            RKIndex++;
            SKIndex++;
            
            SK[SKIndex] = 
                    getKT0( RK[RKIndex].shiftRight(24).byteValue() & 0xFF ) 
                    .xor(
                    getKT1( RK[RKIndex].shiftRight(16).byteValue() & 0xFF ) )
                    .xor(
                    getKT2( RK[RKIndex].shiftRight(8 ).byteValue() & 0xFF ) )
                    .xor(
                    getKT3( RK[RKIndex]               .byteValue() & 0xFF) //neg??!?!
                    ); 
//            if (SK[SKIndex] < 0) SK[SKIndex] += Math.pow(2, 32);
            RKIndex++;
            SKIndex++;

            SK[SKIndex] = 
                    getKT0( RK[RKIndex].shiftRight(24).and(BigInteger.valueOf(0xFF)).byteValue() ) 
                    .xor(
                    getKT1( RK[RKIndex].shiftRight(16).and(BigInteger.valueOf(0xFF)).byteValue() ) )
                    .xor(
                    getKT2( RK[RKIndex].shiftRight(8 ).and(BigInteger.valueOf(0xFF)).byteValue() ) )
                    .xor(
                    getKT3( RK[RKIndex]               .and(BigInteger.valueOf(0xFF)).byteValue() ) 
                    );
//            if (SK[SKIndex] < 0) SK[SKIndex] += Math.pow(2, 32);
            RKIndex++;
            SKIndex++;

            SK[SKIndex] = 
                    getKT0( RK[RKIndex].shiftRight(24).and(BigInteger.valueOf(0xFF)).byteValue() ) 
                    .xor(
                    getKT1( RK[RKIndex].shiftRight(16).and(BigInteger.valueOf(0xFF)).byteValue() ) )
                    .xor(
                    getKT2( RK[RKIndex].shiftRight(8 ).and(BigInteger.valueOf(0xFF)).byteValue() ) )
                    .xor(
                    getKT3( RK[RKIndex]               .and(BigInteger.valueOf(0xFF)).byteValue() )
            ); 
//            if (SK[SKIndex] < 0) SK[SKIndex] += Math.pow(2, 32);
            RKIndex++;
            SKIndex++;
        }

        RKIndex -= 8;

        SK[SKIndex++] = RK[RKIndex++];
        SK[SKIndex++] = RK[RKIndex++];
        SK[SKIndex++] = RK[RKIndex++];
        SK[SKIndex++] = RK[RKIndex++];
   }
   
   
/***********************************************************************************************
* AES Counter Mode Setup
***********************************************************************************************/

void aesSetCtr( AESContext ctx )
{
    ctx.nonce = System.currentTimeMillis();
//    ctx.nonce = 155;
}

private List<BigInteger> AES_FROUND(BigInteger[] RK, int RKIndex, BigInteger Y0, BigInteger Y1, BigInteger Y2, BigInteger Y3)             
{        
//    if (Y0 < 0) Y0 += Math.pow(2, 32);
//    if (Y1 < 0) Y1 += Math.pow(2, 32);
//    if (Y2 < 0) Y2 += Math.pow(2, 32);
//    if (Y3 < 0) Y3 += Math.pow(2, 32);
    List<BigInteger> result = new ArrayList<BigInteger>(4);                                           
                                       
    result.add( RK[RKIndex]
            .xor(BigInteger.valueOf( FT0[byteSignedToUnsigned(Y0.shiftRight(24).byteValue())]) )         
            .xor(BigInteger.valueOf( FT1[byteSignedToUnsigned(Y1.shiftRight(16).byteValue())]) )         
            .xor(BigInteger.valueOf( FT2[byteSignedToUnsigned(Y2.shiftRight(8 ).byteValue())]) )         
            .xor(BigInteger.valueOf( FT3[byteSignedToUnsigned(Y3               .byteValue())]) ) 
            );    

    result.add( RK[RKIndex + 1]
            .xor(BigInteger.valueOf( FT0[byteSignedToUnsigned(Y1.shiftRight(24).byteValue())]) )         
            .xor(BigInteger.valueOf( FT1[byteSignedToUnsigned(Y2.shiftRight(16).byteValue())]) )         
            .xor(BigInteger.valueOf( FT2[byteSignedToUnsigned(Y3.shiftRight(8 ).byteValue())]) )         
            .xor(BigInteger.valueOf( FT3[byteSignedToUnsigned(Y0               .byteValue())]) ) 
            );  

    result.add( RK[RKIndex + 2]
            .xor(BigInteger.valueOf( FT0[byteSignedToUnsigned(Y2.shiftRight(24).byteValue())]) )         
            .xor(BigInteger.valueOf( FT1[byteSignedToUnsigned(Y3.shiftRight(16).byteValue())]) )         
            .xor(BigInteger.valueOf( FT2[byteSignedToUnsigned(Y0.shiftRight(8 ).byteValue())]) )         
            .xor(BigInteger.valueOf( FT3[byteSignedToUnsigned(Y1               .byteValue())]) ) 
            );  

    result.add( RK[RKIndex + 3]
            .xor(BigInteger.valueOf( FT0[byteSignedToUnsigned(Y3.shiftRight(24).byteValue())]) )         
            .xor(BigInteger.valueOf( FT1[byteSignedToUnsigned(Y0.shiftRight(16).byteValue())]) )         
            .xor(BigInteger.valueOf( FT2[byteSignedToUnsigned(Y1.shiftRight(8 ).byteValue())]) )         
            .xor(BigInteger.valueOf( FT3[byteSignedToUnsigned(Y2               .byteValue())]) ) 
            );  

    
    return result;
}

/***********************************************************************************************
* AES block encryption (ECB mode)
***********************************************************************************************/
/**
 * AES block encryption (ECB mode)
 *
 * param ctx      AES context
 * param input    plaintext  block
 * param output   ciphertext block
 */
private int[] aesEncrypt( AESContext ctx,
                  int[] input,
                  int[] output)
{
    int RKIndex = 0;
    BigInteger[] RK = ctx.erk;
    BigInteger X0, X1, X2, X3;
    BigInteger Y0 = BigInteger.ZERO; 
    BigInteger Y1 = BigInteger.ZERO;
    BigInteger Y2 = BigInteger.ZERO;
    BigInteger Y3 = BigInteger.ZERO;

    X0 = GET_UNS_32_BE( input,  0 ); X0 = X0.xor(RK[0]);
    X1 = GET_UNS_32_BE( input,  4 ); X1 = X1.xor(RK[1]);
    X2 = GET_UNS_32_BE( input,  8 ); X2 = X2.xor(RK[2]);
    X3 = GET_UNS_32_BE( input, 12 ); X3 = X3.xor(RK[3]);
    
//    if (X0 < 0) X0 += Math.pow(2, 32);
//    if (X1 < 0) X1 += Math.pow(2, 32);
//    if (X2 < 0) X2 += Math.pow(2, 32);
//    if (X3 < 0) X3 += Math.pow(2, 32);
    //AES_FROUND loop
    List<BigInteger> out;
    int loops = 9;
    loops += (ctx.nr > 10) ? 2 : (ctx.nr > 12) ? 4 : 0;
    for (int i = 0; i < loops; i++)
    {
        RKIndex += 4;
        if (i % 2 == 0)
        {
            out = AES_FROUND( RK, RKIndex, X0, X1, X2, X3 );
            Y0 = out.get(0);
            Y1 = out.get(1);
            Y2 = out.get(2);
            Y3 = out.get(3);
        } else
        {
            out = AES_FROUND( RK, RKIndex, Y0, Y1, Y2, Y3 );
            X0 = out.get(0);
            X1 = out.get(1);
            X2 = out.get(2);
            X3 = out.get(3);
        }
    }

    RKIndex += 4;

    
            
    X0 = RK[RKIndex]
            .xor(BigInteger.valueOf( getFSb(Y0.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getFSb(Y1.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getFSb(Y2.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getFSb(Y3               .byteValue())) );
    
    X1 = RK[RKIndex + 1]
            .xor(BigInteger.valueOf( getFSb(Y1.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getFSb(Y2.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getFSb(Y3.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getFSb(Y0               .byteValue())) );
    
    X2 = RK[RKIndex + 2]
            .xor(BigInteger.valueOf( getFSb(Y2.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getFSb(Y3.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getFSb(Y0.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getFSb(Y1               .byteValue())) );
    
    X3 = RK[RKIndex + 3]
            .xor(BigInteger.valueOf( getFSb(Y3.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getFSb(Y0.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getFSb(Y1.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getFSb(Y2               .byteValue())) );
    
    
    output = PUT_UNS_32_BE( X0, output,  0 );
    output = PUT_UNS_32_BE( X1, output,  4 );
    output = PUT_UNS_32_BE( X2, output,  8 );
    output = PUT_UNS_32_BE( X3, output, 12 );
    
    return output;
}
/***********************************************************************************************
* AES block decryption (ECB mode)
***********************************************************************************************/

    private List<BigInteger> AES_RROUND(BigInteger[] RK, int RKIndex, BigInteger Y0, BigInteger Y1, BigInteger Y2, BigInteger Y3)             
    {                 
        List<BigInteger> result = new ArrayList<BigInteger>(4);                                           

        result.add( RK[RKIndex]
                .xor(BigInteger.valueOf( RT0[byteSignedToUnsigned(Y0.shiftRight(24).byteValue())]) )         
                .xor(BigInteger.valueOf( RT1[byteSignedToUnsigned(Y3.shiftRight(16).byteValue())]) )         
                .xor(BigInteger.valueOf( RT2[byteSignedToUnsigned(Y2.shiftRight(8 ).byteValue())]) )         
                .xor(BigInteger.valueOf( RT3[byteSignedToUnsigned(Y1               .byteValue())]) ) 
                );    
        
        result.add( RK[RKIndex + 1]
                .xor(BigInteger.valueOf( RT0[byteSignedToUnsigned(Y1.shiftRight(24).byteValue())]) )         
                .xor(BigInteger.valueOf( RT1[byteSignedToUnsigned(Y0.shiftRight(16).byteValue())]) )         
                .xor(BigInteger.valueOf( RT2[byteSignedToUnsigned(Y3.shiftRight(8 ).byteValue())]) )         
                .xor(BigInteger.valueOf( RT3[byteSignedToUnsigned(Y2               .byteValue())]) ) 
                );  
        
        result.add( RK[RKIndex + 2]
                .xor(BigInteger.valueOf( RT0[byteSignedToUnsigned(Y2.shiftRight(24).byteValue())]) )         
                .xor(BigInteger.valueOf( RT1[byteSignedToUnsigned(Y1.shiftRight(16).byteValue())]) )         
                .xor(BigInteger.valueOf( RT2[byteSignedToUnsigned(Y0.shiftRight(8 ).byteValue())]) )         
                .xor(BigInteger.valueOf( RT3[byteSignedToUnsigned(Y3               .byteValue())]) ) 
                );  
        
        result.add( RK[RKIndex + 3]
                .xor(BigInteger.valueOf( RT0[byteSignedToUnsigned(Y3.shiftRight(24).byteValue())]) )         
                .xor(BigInteger.valueOf( RT1[byteSignedToUnsigned(Y2.shiftRight(16).byteValue())]) )         
                .xor(BigInteger.valueOf( RT2[byteSignedToUnsigned(Y1.shiftRight(8 ).byteValue())]) )         
                .xor(BigInteger.valueOf( RT3[byteSignedToUnsigned(Y0               .byteValue())]) ) 
                );  
        
        return result;
    }

/**
 * AES block decryption (ECB mode)
 *
 * @param ctx      AES context
 * @param input    ciphertext block
 * @param output   plaintext  block
 */
private int[] aesDecrypt( AESContext ctx,
                  int[] input,
                  int[] output ) throws ArrayIndexOutOfBoundsException
{
    int RKIndex = 0;
    BigInteger[] RK = ctx.drk;
    BigInteger X0, X1, X2, X3;
    BigInteger Y0 = BigInteger.ZERO; 
    BigInteger Y1 = BigInteger.ZERO;
    BigInteger Y2 = BigInteger.ZERO;
    BigInteger Y3 = BigInteger.ZERO;

    X0 = GET_UNS_32_BE( input,  0 ); X0 = X0.xor(RK[0]);
    X1 = GET_UNS_32_BE( input,  4 ); X1 = X1.xor(RK[1]);
    X2 = GET_UNS_32_BE( input,  8 ); X2 = X2.xor(RK[2]);
    X3 = GET_UNS_32_BE( input, 12 ); X3 = X3.xor(RK[3]);


    //AES_RROUND loop
    List<BigInteger> out;
    int loops = 9;
    loops += (ctx.nr > 10) ? 2 : (ctx.nr > 12) ? 4 : 0;
    for (int i = 0; i < loops; i++)
    {
        RKIndex += 4;
        if (i % 2 == 0)
        {
            out = AES_RROUND( RK, RKIndex, X0, X1, X2, X3 );
            Y0 = out.get(0);
            Y1 = out.get(1);
            Y2 = out.get(2);
            Y3 = out.get(3);
        } else
        {
            out = AES_RROUND( RK, RKIndex, Y0, Y1, Y2, Y3 );
            X0 = out.get(0);
            X1 = out.get(1);
            X2 = out.get(2);
            X3 = out.get(3);
        }
    }

    RKIndex += 4;

            
    X0 = RK[RKIndex]
            .xor(BigInteger.valueOf( getRSb(Y0.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getRSb(Y3.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getRSb(Y2.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getRSb(Y1               .byteValue())) );
                
   
    X1 = RK[RKIndex + 1]
            .xor(BigInteger.valueOf( getRSb(Y1.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getRSb(Y0.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getRSb(Y3.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getRSb(Y2               .byteValue())) );
    
    X2 = RK[RKIndex + 2]
            .xor(BigInteger.valueOf( getRSb(Y2.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getRSb(Y1.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getRSb(Y0.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getRSb(Y3               .byteValue())) );
    
    X3 = RK[RKIndex + 3]
            .xor(BigInteger.valueOf( getRSb(Y3.shiftRight(24).byteValue())).shiftLeft(24) )         
            .xor(BigInteger.valueOf( getRSb(Y2.shiftRight(16).byteValue())).shiftLeft(16) )         
            .xor(BigInteger.valueOf( getRSb(Y1.shiftRight(8 ).byteValue())).shiftLeft(8 ) )         
            .xor(BigInteger.valueOf( getRSb(Y0               .byteValue())) );

    output = PUT_UNS_32_BE( X0, output,  0 );
    output = PUT_UNS_32_BE( X1, output,  4 );
    output = PUT_UNS_32_BE( X2, output,  8 );
    output = PUT_UNS_32_BE( X3, output, 12 );
    
    return output;
}

/***********************************************************************************************
 * AES-CTR buffer encryption
***********************************************************************************************/
/**
 * AES-CTR buffer encryption
 *
 * @param ctx      AES context
 * @param input    buffer holding the plaintext
 * @param output   buffer holding the ciphertext
 * @param len      length of the data to be encrypted
 */
    private int[] aesEncryptCtrMode ( AESContext ctx,
                          int[]  input,
                          int len )
    {
	int[] counterblock = new int[16];
	int[] ciphercntr = new int[16];        
	int blockcount, blocklength;
	int txlen;

	if ( ctx == null )
	{
            return null;
	}

	ctx.nonce += (System.currentTimeMillis() & 0xF) + 1;

	// encode nonce in two stages to cater for 32-bit limit on bitwise ops
	for (int i = 0; i < 4; i++) 
        {
            counterblock[i] = (byte) ((ctx.nonce >> i * 8) & 0xFF);
        }
        for (int i = 0; i < 4; i++) 
        {
            counterblock[i+4] = (byte) ((ctx.nonce / 0x100000000l >> i * 8) & 0xFF);
        }
        
        counterblock = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	txlen = len + (16 - (len & 0xF)); /* Make the length a multiple of 16 */

	blockcount = txlen / AES_BLOCK_SIZE;

        int[] output = new int[len + 16];
	output = aesEncrypt(ctx, counterblock, output);
	txlen = 16;

	for (int b = 1; b < blockcount + 1; b++)
	{
            // set counter (block #) in last 8 bytes of counter block (leaving nonce in 1st 8 bytes)
            // again done in two stages for 32-bit ops
            for (int c = 0; c < 4; c++) 
            {
                    counterblock[15 - c] = (byte) ((b >> c * 8) & 0xFF);
            }
            for (int c = 0; c < 4; c++) 
            {
                counterblock[15 - c - 4] = (byte) (((b/0x100000000L) >> c * 8) & 0xFF);
            }

            ciphercntr = aesEncrypt(ctx, counterblock, ciphercntr); 
            blocklength = (b < blockcount) ? AES_BLOCK_SIZE : len % AES_BLOCK_SIZE; 
            int i = 0;
            while(i < blocklength)
            {
                output[b * AES_BLOCK_SIZE + i] = (byte) (input[((b - 1) * AES_BLOCK_SIZE + i)] ^ ciphercntr[i]);
                txlen++;
                i++;		
            }		
	}
        
	return output;
    }

/************************************************************************************************************
 * AES-CTR buffer decryption
************************************************************************************************************/

    /**
     * AES-CTR buffer decryption
     *
     * @param ctx      AES context
     * @param input    buffer holding the ciphertext
     * @param output   buffer holding the plaintext
     * @param len      length of the data to be decrypted
     */
    private int[] aesDecryptCtrMode ( AESContext ctx,
                      int[] input,
                      int len ) throws ArrayIndexOutOfBoundsException
    {
	int[] counterblock = new int[16];
	int[] ciphercntr = new int[16];
        int[] output = new int[(int) len];
	int blockcount, blocklength;
	int rxlen = 0;

	if ( ctx == null )
	{
            return null;
	}
	
	blockcount = (len / AES_BLOCK_SIZE);

	counterblock = aesDecrypt(ctx, input, counterblock);

	for (int b = 1; b < (blockcount + 1); b++) 
	{
            // set counter (block #) in last 8 bytes of counter block (leaving nonce in 1st 8 bytes)
            for (int c = 0; c < 4; c++) 
            {
                counterblock[15 - c] = (byte) (((b) >> c * 8) & 0xFF);
            }
            for (int c = 0; c < 4; c++) 
            {                                          
                counterblock[15 - c - 4] = (byte) (((b/0x100000000L) >> c * 8) & 0xFF);
            }

            ciphercntr = aesEncrypt(ctx, counterblock, ciphercntr);

            blocklength = (b < blockcount) ? AES_BLOCK_SIZE : (len % AES_BLOCK_SIZE); 

            for (int i = 0; i < blocklength; i++) 
            {
                    output[(b - 1) * AES_BLOCK_SIZE + i] = (byte) (input[(b * AES_BLOCK_SIZE + i)] ^ ciphercntr[i]);
                    rxlen++;
            }
	}
        return Arrays.copyOf(output, rxlen);
    }

/***********************************************************************************************
***********************************************************************************************/

    private AESContext aesAllocKey()
    {
	for (byte key = 0; key < AES_KEY_COUNT; key++ )
	{
            if ( aesKeyInUse[key] == 0 )
            {
                    aesKeyInUse[key] = 1;
                    return aesKeyBuffer[key]; 
            }
	}
	return new AESContext();
    }

/***********************************************************************************************
***********************************************************************************************/

    private void aesFreeKey(AESContext pfarKey)
    {
	if ( pfarKey == null )
	{	
            return;
        }

	for (byte key = 0; key < AES_KEY_COUNT; key++ )
	{
            if ( pfarKey == aesKeyBuffer[key] )
            {
                aesKeyInUse[key] = 0;
                return; 
            }
	}
    }

/***********************************************************************************************
***********************************************************************************************/
    
    public static int[] byteArraySignedToUnsigned(byte[] array)
    {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = byteSignedToUnsigned(array[i]);
        }
        return result;
    }
    
    public static byte[] intArrayUnsignedToSigned(int[] array)
    {
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = intUnsignedToSigned(array[i]);
        }
        return result;
    }
    
    
    public byte[] encrypt(byte[] packet)
    {
        AESContext aesKey = aesAllocKey();
        aesSetCtr(aesKey);
        aesSetKey(aesKey, this.key, 128);
        //Encrypt the array, providing it the original data length.
        int[] encrypted =  aesEncryptCtrMode(aesKey, 
                        byteArraySignedToUnsigned(packet), 
                        packet.length
                );
        aesFreeKey(aesKey);
        return intArrayUnsignedToSigned(encrypted);
    }
    
    public byte[] decrypt(byte[] packet)
    {
        AESContext aesKey = aesAllocKey();
        aesSetCtr(aesKey);
        aesSetKey(aesKey, this.key, 128);
        int[] decrypted = null;
        try
        {
            decrypted = aesDecryptCtrMode(aesKey, 
                        byteArraySignedToUnsigned(packet), 
                        packet.length
            ); 
        } catch (ArrayIndexOutOfBoundsException e)
        {
            ProtegeUtils.log.debug("Index out of bounds for packet: " + ProtegeUtils.byteArrayToHex(packet) + "." + e);
            return packet; //Received packet was not encrypted - likely a nack
        }
        aesFreeKey(aesKey);
        return intArrayUnsignedToSigned(decrypted);        
    }
    
	/**
	 * Methods used for testing encryption and
	 * decryption.
	 */
    public static ProtegePacket createLoginPacket(String PIN)
    {
        Map<String, String> loginMap = new HashMap<String, String>();
        loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "SEND_LOGIN");
        loginMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE, PIN);
        return new ProtegePacket(loginMap, EncryptionType.ENCRYPTION_AES_128, ChecksumType.CHECKSUM_8);
    }
    
    public static ProtegePacket createPollPacket()
    {
        Map<String, String> pollMap = new HashMap<String, String>();
        pollMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "SYSTEM_POLL");
        return new ProtegePacket(pollMap, EncryptionType.ENCRYPTION_AES_128, ChecksumType.CHECKSUM_8);
    }
	
	/**
	 * Test and demonstration of encryption and 
	 * decryption.
	 */
    public static void main(String[] args)
    {
		String key = "0123456789012345"; //128 bit key
        ICTAES crypto = new ICTAES(key, EncryptionType.ENCRYPTION_AES_128);
        byte[] testMessage = createPollPacket().getPacket();
		
        System.out.println("INITIAL: " + ProtegeUtils.byteArrayToHex((testMessage)) + 
                " (Size: " + testMessage.length + ")"); 
				
		//Encrypt the data section
        byte[] header = Arrays.copyOf(testMessage, 6);
        byte[] data = Arrays.copyOfRange(testMessage, 6, testMessage.length);
        byte[] dataEncrypted = crypto.encrypt(data);
        byte[] encrypted = ArrayUtils.addAll(header, dataEncrypted);
		
        System.out.println("ENCRYPTED: " + ProtegeUtils.byteArrayToHex((encrypted)) + 
                " (Size: " + encrypted.length + ")");
        
		//Decrypt the data section of the packet.
        header = Arrays.copyOf(encrypted, 6);
        data = Arrays.copyOfRange(encrypted, 6, encrypted.length);
        byte[] dataDecrypted = crypto.decrypt(data);
        byte[] decrypted = ArrayUtils.addAll(header, dataDecrypted);
        System.out.println(ProtegeUtils.byteArrayToHex(dataDecrypted));
        System.out.println("DECRYPTED: " + ProtegeUtils.byteArrayToHex((decrypted)) + 
                " (Size: " + decrypted.length + ")");
        System.out.println("Decrypted ASCII: " + new String(decrypted));
    }
}