/*
 * LICENSE
 */

package org.openremote.controller.protocol.ictprotege;

/**
 * Enumerator of the record types to control in Protege.
 * MAKE SURE SYSTEM IS NOT SHOWN TO USER INTERFACE!?
 * 
 * @author Tomas Morton
 */
public enum ProtegeRecordType {
    SYSTEM(0), //TODO CHECK SYSTEM IS NOT SHOWN TO USER INTERFACE
    DOOR(1),
    AREA(2),
    OUTPUT(3),
    INPUT(4),
    VARIABLE(5);
    
    private int value;
            
    private ProtegeRecordType(int value)
    {
        this.value = value;        
    }
    
    @Override
    public String toString()
    {
        return Integer.toString(value);
    }
    
    public int getValue()
    {
        return value;
    }    
}
