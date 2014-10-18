package org.openremote.controller.protocol.ictprotege;

import java.util.HashMap;
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


public class ProtegeCommandTest {
    
    private static ProtegeConnectionManager connectionManager;
    private static ProtegePacket packet;
    private static Sensor sensor;
    private static Map<ProtegeDataType, Map<Integer, Sensor>> sensors;
    
    public ProtegeCommandTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        connectionManager = mock(ProtegeConnectionManager.class);
        packet = mock(ProtegePacket.class);
        sensor = mock(Sensor.class);
//        sensors = new HashMap<ProtegeDataType, Map<Integer, Sensor>>();
//        for ( ProtegeDataType type : ProtegeDataType.values() ) {
//            Map<Integer, Sensor> map = new HashMap<Integer, Sensor>();
//            sensors.put(type, map);
//        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        connectionManager = null;
        packet = null;
        sensor = null;
        sensors = null;
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSetSensor() {
        ProtegeCommand testCommand = new ProtegeCommand(connectionManager, packet);
        when(packet.getRecordID()).thenReturn(1);
        when(packet.getRecordValue()).thenReturn(ProtegeRecordType.DOOR.toString());
//        when(connectionManager.getSensors()).thenReturn(sensors);
        assertNotNull(testCommand);
        testCommand.setSensor(sensor);
        verify(connectionManager).setSensor(sensor, packet);
        
//        HashMap<Integer, Sensor> sensorMap = (HashMap) 
//                connectionManager.getSensors()
//                    .get(ProtegeDataType.getDataType((byte) 0x00, ProtegeRecordType.DOOR.getValue()));
//        assertEquals(connectionManager.getSensors().get(ProtegeDataType.DOOR_STATUS), sensorMap);
//        assertEquals(1, sensorMap.size());
//        assertTrue(sensorMap.containsValue(sensor));
//        assertEquals(sensorMap.get(0), sensor);
    }
    
    /**
     * 
     */
    @Test
    public void testSend()  {
        ProtegeCommand testCommand = new ProtegeCommand(connectionManager, packet);
        when(packet.getCommandType()).thenReturn(ProtegeCommandType.DOOR_LOCK);
        assertNotNull(testCommand);
        testCommand.send();
        verify(connectionManager).send(packet);
    }
    
//    /**
//     * this test is deprecated since it only wraps a call to remove the sensor
//     * from the connection manager
//     */
//    @Test
//    public void testStop() {
        
//        ProtegeCommand testCommand = new ProtegeCommand(connectionManager, packet);
//        assertNotNull(testCommand);
//        assertTrue(sensor.isRunning());
//        when(sensor.isRunning()).thenReturn(Boolean.FALSE);
//        testCommand.stop(sensor);
//    }
}
