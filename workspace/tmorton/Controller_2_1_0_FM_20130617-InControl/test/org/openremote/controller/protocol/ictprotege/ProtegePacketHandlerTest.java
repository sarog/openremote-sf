package org.openremote.controller.protocol.ictprotege;

import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.ictprotege.network.ProtegeConnectionManager;

/**
 *
 * @author Adam Mcnabb
 */


public class ProtegePacketHandlerTest {
    
    private static ProtegeConnectionManager connectionManager;
    private static ProtegePacketHandler packetHandler;
    private static byte[] packet;
    private Map<Integer, Sensor> sensors;
    
    public ProtegePacketHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        connectionManager = mock(ProtegeConnectionManager.class);
        packet = new byte[] {
        (byte) 0x49,(byte) 0x43,(byte) 0x09,(byte) 0x00,
            (byte) 0x00 ,(byte) 0x00 ,(byte) 0x00 ,(byte) 0x00 ,(byte) 0x95
    };
      packetHandler = spy(new ProtegePacketHandler(connectionManager, packet));  
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class ProtegePacketHandler.
     */
    @Test
    public void testRun() {
        packetHandler.run();
        verify(packetHandler).run();
    }
    
}
