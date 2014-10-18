/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.ictprotege.network;

import java.net.Socket;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.ictprotege.ChecksumType;
import org.openremote.controller.protocol.ictprotege.EncryptionType;
import org.openremote.controller.protocol.ictprotege.ProtegeDataType;
import org.openremote.controller.protocol.ictprotege.ProtegePacket;
import org.openremote.controller.protocol.ictprotege.ProtegeRecordType;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author Adam Mcnabb
 */
public class ProtegeConnectionManagerTest {
    
    private static ProtegeConnectionManager connectionManager;
    private static ProtegePacket packet;
    private static Sensor sensor;
    private static ProtegeTCPWriter tcpWriter;
    private static final String address = "203.97.50.236";
    private static final int port = 9189;
    private static final String key = "PROTEGE";
    private static final String PIN = "1";
    private static ProtegeConnectionManager sConnMgr;
    
    public ProtegeConnectionManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        connectionManager = new ProtegeConnectionManager(
                address, //address
                port, //port
                EncryptionType.ENCRYPTION_AES_128, //encryption type
                key, //encryption key
                ChecksumType.CHECKSUM_8, //checksum Type
                PIN); //PIN
        
        sConnMgr = spy(connectionManager);
        //Mock objects for ProtegeConnectionManager test usage
        packet = mock(ProtegePacket.class);
        sensor = mock(Sensor.class);
        tcpWriter = mock(ProtegeTCPWriter.class);
        //break encapsulation to set fields for isolation testing
        Whitebox.setInternalState(connectionManager, tcpWriter);
        //Values for mock objects to return when method called
        when(tcpWriter.getLastPacketSent()).thenReturn(packet);
        
        when(sensor.isRunning()).thenReturn(true);
        when(sensor.getSensorID()).thenReturn(1);
        when(packet.getRecordID()).thenReturn(1);
        when(packet.getRecordValue()).thenReturn(ProtegeRecordType.DOOR.toString());
        when(packet.getRecordType()).thenReturn(ProtegeRecordType.DOOR);
    }
    
    @AfterClass
    public static void tearDownClass() {
        connectionManager = null;
        packet = null;
        sensor = null;
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class ProtegeConnectionManager.
     */
    @Test
    public void testRun() {
//        connectionManager.run();
        verify(sConnMgr).run();
    }

    /**
     * Test of send method, of class ProtegeConnectionManager.
     */
    @Test
    public void testSend() {
        connectionManager.send(packet);
        verify(sConnMgr).send(packet);
    }

    /**
     * Test of loginMonitoring method, of class ProtegeConnectionManager.
     */
    @Test
    public void testLoginMonitoring() {
        connectionManager.loginMonitoring();
        verify(sConnMgr).loginMonitoring();
    }

    /**
     * Test of login method, of class ProtegeConnectionManager.
     */
    @Test
    public void testLogin() {
        //login method set to private
//        connectionManager.login("1");
//        verify(connectionManager).login("1");
    }

    /**
     * Test of notifyLoggedOut method, of class ProtegeConnectionManager.
     */
    @Test
    public void testNotifyLoggedOut() {
        verify(sConnMgr).notifyLoggedOut();
    }

    /**
     * Test of getEncryptionType method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetEncryptionType() {
        assertEquals(connectionManager.getEncryptionType(), 128);
    }

    /**
     * Test of getChecksumType method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetChecksumType() {
        assertEquals(connectionManager.getChecksumType(), 8);
    }

    /**
     * Test of getEncryptionKey method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetEncryptionKey() {
        assertEquals(connectionManager.getEncryptionKey(), key);
    }

    /**
     * Test of getSensors method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetSensors() {
        assertNotNull(connectionManager.getSensors());
        HashMap<Integer, Sensor> doorMap = 
                (HashMap) connectionManager.getSensors().get(ProtegeDataType.DOOR_STATUS);
        assertNotNull(doorMap);
        assertTrue(doorMap.isEmpty());
        HashMap<Integer, Sensor> areaMap = 
                (HashMap) connectionManager.getSensors().get(ProtegeDataType.AREA_STATUS);
        assertNotNull(areaMap);
        assertTrue(areaMap.isEmpty());
        assertNotSame(doorMap, areaMap);
    }

    /**
     * Test of setSensor method, of class ProtegeConnectionManager.
     */
    @Test
    public void testSetSensor() {
        //insert sensor
        connectionManager.setSensor(sensor, packet);
        //retrieve the bin of sensor type Door
        String rType = packet.getRecordValue();
        byte rByte = ProtegeRecordType.valueOf(rType).getValue();
        ProtegeDataType dType = ProtegeDataType.getDataType((byte) 0x00, rByte);
        HashMap<Integer, Sensor> sMap = (HashMap) connectionManager.getSensors().get(dType);
        //check objects are the same
        assertEquals(sMap.containsValue(sensor), sensor);
    }

    /**
     * Test of removeSensor method, of class ProtegeConnectionManager.
     */
    @Test
    public void testRemoveSensor() {
        connectionManager.removeSensor(packet);
        
        String rType = packet.getRecordValue();
        byte rByte = ProtegeRecordType.valueOf(rType).getValue();
        ProtegeDataType dType = ProtegeDataType.getDataType((byte) 0x00, rByte);
        HashMap<Integer, Sensor> sMap = (HashMap) connectionManager.getSensors().get(dType);
        
        assertFalse(sMap.containsKey(packet.getRecordID()));
        assertFalse(sMap.containsValue(sensor));
    }

    /**
     * Test of calcChecksum8Bit method, of class ProtegeConnectionManager.
     */
    @Test
    public void testCalcChecksum8Bit() {
        
    }

    /**
     * Test of calcChecksum16Bit method, of class ProtegeConnectionManager.
     */
    @Test
    public void testCalcChecksum16Bit() {
        
    }

    /**
     * Test of calcChecksum16Bit2 method, of class ProtegeConnectionManager.
     */
    @Test
    public void testCalcChecksum16Bit2() {
        
    }

    /**
     * Test of calcChecksum16Bit3 method, of class ProtegeConnectionManager.
     */
    @Test
    public void testCalcChecksum16Bit3() {
        
    }

    /**
     * Test of getSocket method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetSocket() {
        Socket s = connectionManager.getSocket();
        assertNotNull(s);
        assertEquals(s.getPort(), port);
        assertEquals(s.getInetAddress(), address);
    }

    /**
     * Test of notifyConnectionLost method, of class ProtegeConnectionManager.
     */
    @Test
    public void testNotifyConnectionLost() {
        connectionManager.notifyConnectionLost();
        assertFalse(connectionManager.isConnected());
    }

    /**
     * Test of isConnected method, of class ProtegeConnectionManager.
     */
    @Test
    public void testIsConnected() {
        
    }

    /**
     * Test of notifyAck method, of class ProtegeConnectionManager.
     */
    @Test
    public void testNotifyAck() {
        
    }

    /**
     * Test of getInputStream method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetInputStream() {
        
    }

    /**
     * Test of getOutputStream method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetOutputStream() {
        
    }

    /**
     * Test of processControllerCommand method, of class ProtegeConnectionManager.
     */
    @Test
    public void testProcessControllerCommand() {
        
    }

    /**
     * Test of getUserPin method, of class ProtegeConnectionManager.
     */
    @Test
    public void testGetUserPin() {
        assertEquals(connectionManager.getUserPin(), "1");
    }
    
}
