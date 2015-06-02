package org.openremote.controller.protocol.ictprotege;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Adam Mcnabb
 */


public class ProtegeUtilsTest {
    
    private List<Byte> example;
    private byte[] exampleArray;
    private int length = 10;
    private int number = 0;
    private ProtegeUtils utils;
    
    public ProtegeUtilsTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        utils = new ProtegeUtils();
        exampleArray = new byte[length];
        example = new ArrayList();
        for (int i = 0; i < 10; i++)    {
            example.add((byte) 0x00);
        }
    }
    
    @After
    public void tearDown() {
        utils = null;
        exampleArray = null;
        example = null;
    }

    /**
     * Test of intToByteList method, of class ProtegeSystemConstants.
     */
    @Test
    public void testIntToByteList() {
        List<Byte> result = utils.intToByteList(number, length);
        assertNotNull(result);
        assertEquals(result.size(), length);
        assertEquals(example, result);
    }

    /**
     * Test of byteListToArray method, of class ProtegeSystemConstants.
     */
    @Test
    public void testByteListToArray() {
        
        byte[] result = utils.byteListToArray(example);
        assertNotNull(result);
        assertEquals(result.length, example.size());
        assertArrayEquals(exampleArray, result);

    }

    
}
