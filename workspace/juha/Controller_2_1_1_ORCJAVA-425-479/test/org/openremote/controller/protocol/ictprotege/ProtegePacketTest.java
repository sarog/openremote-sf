package org.openremote.controller.protocol.ictprotege;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openremote.controller.command.CommandBuilder;

/**
 *
 * @author Adam Mcnabb
 */


public class ProtegePacketTest {
    
    private static ProtegePacket packet;
    private static Map<String, String> paramMap;
    
    public ProtegePacketTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        paramMap = new HashMap<String, String>();
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        //CommandBuilder XML Attribue Name and Value.  Loops over all elements from XML
        //and adds to the paramMap.  
        paramMap.put("nameOfSomething", "valueOfSOmething");
        //adds a SEND_LOGIN to the packet IF the RECORD_COMMAND == ProtegeCommandType.SEND_LOGIN
        paramMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_VALUE, "1");
        //required value
        paramMap.put(ProtegeCommandBuilder.PROTEGE_XMLPROPERTY_RECORD_COMMAND, "mandatoryValue");
        packet = new ProtegePacket(paramMap, EncryptionType.ENCRYPTION_AES_128, ChecksumType.CHECKSUM_8);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getSize method, of class ProtegePacket.
     */
    @Test
    public void testGetSize() {
        
    }

    /**
     * Test of getRecordType method, of class ProtegePacket.
     */
    @Test
    public void testGetRecordType() {
        
    }

    /**
     * Test of getCommandType method, of class ProtegePacket.
     */
    @Test
    public void testGetCommandType() {
       
    }

    /**
     * Test of getEncryptionType method, of class ProtegePacket.
     */
    @Test
    public void testGetEncryptionType() {
        
    }

    /**
     * Test of getChecksumType method, of class ProtegePacket.
     */
    @Test
    public void testGetChecksumType() {
       
    }

    /**
     * Test of getPacket method, of class ProtegePacket.
     */
    @Test
    public void testGetPacket() {
        
    }

    /**
     * Test of toString method, of class ProtegePacket.
     */
    @Test
    public void testToString() {
        
    }

    /**
     * Test of getRecordID method, of class ProtegePacket.
     */
    @Test
    public void testGetRecordID() {
       
    }

    /**
     * Test of getParamMap method, of class ProtegePacket.
     */
    @Test
    public void testGetParamMap() {
       
    }

    /**
     * Test of getRecordValue method, of class ProtegePacket.
     */
    @Test
    public void testGetRecordValue() {
        
    }

    /**
     * Test of compareTo method, of class ProtegePacket.
     */
    @Test
    public void testCompareTo() {
        
    }

    /**
     * Test of getSequenceNumber method, of class ProtegePacket.
     */
    @Test
    public void testGetSequenceNumber() {
       
    }

    /**
     * Test of updateSequenceNumber method, of class ProtegePacket.
     */
    @Test
    public void testUpdateSequenceNumber() {
       
    }
    
}
