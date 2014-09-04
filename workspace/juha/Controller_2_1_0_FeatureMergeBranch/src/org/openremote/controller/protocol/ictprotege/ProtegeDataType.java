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
    SYSTEM_EVENT_NUMERICAL  ((byte) 0x00, (byte) 0x30),
    SYSTEM_EVENT_ASCII ((byte) 0x01, (byte) 0x30),
    END_OF_DATA ((byte) 0xFF, (byte) 0xFF) ;

   
    private byte dataTypeLow;
    private byte dataTypeHigh;
    
    private ProtegeDataType(byte dataTypeLow, byte dataTypeHigh)
    {
        this.dataTypeLow = dataTypeLow;
        this.dataTypeHigh = dataTypeHigh;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }    
    
    public byte[] getValue()
    {
        return new byte[] {dataTypeLow, dataTypeHigh};
    }    
    
    /**
     * TODO check if this is needed
     * @param compareTo
     * @return 
     */
    public boolean equals(ProtegeDataType compareTo)
    {
        return (dataTypeLow == compareTo.dataTypeLow)
            && (dataTypeHigh == compareTo.dataTypeHigh);
    }
    
    /**
     * TODO: Could also implement as map for efficiency.
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
