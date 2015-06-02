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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.commons.lang.ArrayUtils;
import org.openremote.controller.protocol.ictprotege.ChecksumType;
import org.openremote.controller.protocol.ictprotege.EncryptionType;
import org.openremote.controller.protocol.ictprotege.ProtegePacket;
import org.openremote.controller.protocol.ictprotege.ProtegeUtils;
import org.openremote.controller.utils.Logger;

/**
 * Network writer thread for the ICT Protege implementation.
 *
 * @author Tomas Morton
 */
public class ProtegeTCPWriter implements Runnable
{

    private static final int PACKET_RESEND_DELAY = 2000;
    private static final Logger log = ProtegeUtils.log;

    private boolean stopRequested;
    private boolean packetAcknowledged;
    private ProtegePacket currentPacket;

    private final ProtegeConnectionManager manager;
    private final PriorityBlockingQueue<ProtegePacket> packetBuffer;

    private int sendAttempts;
    
    public ProtegeTCPWriter(ProtegeConnectionManager manager)
    {
        this.manager = manager;
        this.packetBuffer = new PriorityBlockingQueue<ProtegePacket>(100);
    }

    /**
     * Queues a packet for sending to the Protege controller.<br>
     * Checks that the packet has not already been queued before
     * adding.
     * @param packet
     */
    public void sendPacket(ProtegePacket packet)
    {
        if (!packetBuffer.contains(packet))
        {
            log.info("Added " + packet.getCommandType() + " to buffer.");
            packetBuffer.offer(packet);
            manager.updateQueueSensor(Integer.toString(packetBuffer.size()));
        }
    }

    /**
     * Retrieves packets from the buffer and sends them to the Protege
     * controller. Packets are guaranteed to be delivered if a connection can be
     * established eventually.
     */
    @Override
    public void run()
    {
        sendAttempts = 0;
        while (!stopRequested)
        {
            sendAttempts++;
            if (sendAttempts > 10)
            {
                sendAttempts = 1; //Prevent time from getting too large
            }
            manager.updateQueueSensor(Integer.toString(packetBuffer.size()));
            //Retrieve a packet from the buffer
            log.debug("Retrieving packet from buffer.");
            currentPacket = getPacketFromBuffer();
            log.debug("Attempting to send Protege packet " + currentPacket.getCommandType().name()
                    + ". Data: " + currentPacket.toString());

            //Send packet
            writePacketToStream(currentPacket);
            int waitTime = sendAttempts * PACKET_RESEND_DELAY;
            log.debug("Waiting for reply (max time: " + waitTime + ")");
            waitForAcknowledgement(waitTime);
        }
    }

    /**
     * Retrieves a packet from the buffer.
     *
     * @return
     */
    private ProtegePacket getPacketFromBuffer()
    {
        ProtegePacket packet;
        do
        {
            packet = packetBuffer.peek(); //blocking
        }
        while (packet == null);

        return packet;
    }

    /**
     * Writes the byte array within a packet to the output stream.
     *
     * @param packet
     */
    private void writePacketToStream(ProtegePacket packet)
    {
        boolean packetSent = false;
        do
        {
            verifyConnection();
            //Encrypt and add checksum
            byte[] data = packet.getPacket().clone();
            data = encryptPacket(data);
            log.debug("Encrypted: " + ProtegeUtils.byteArrayToHex(data));
            data = addLength(data);
            log.debug("Packet with Length: " + ProtegeUtils.byteArrayToHex(data));
            data = addChecksum(data);
            log.debug("Sending final packet: " + ProtegeUtils.byteArrayToHex(data));
            //send packet
            try
            {
                manager.getOutputStream().write(data);
                manager.getOutputStream().flush();
                packetSent = true;
                packetAcknowledged = false;
            }
            catch (IOException ex)
            {
                log.debug("Error sending command '" + packet.getCommandType().name()
                        + "' to controller. " + ex);
                manager.notifyConnectionLost();
                synchronized (manager)
                {
                    log.debug("Notifying manager of connection lost.");
                    manager.notifyAll();
                }
            }
        }
        while (!packetSent);
    }
    
    /**
     * Waits for PACKET_RESEND_DELAY milliseconds for an ack packet from the
     * Protege controller.
     *
     */
    private void waitForAcknowledgement(int maxWaitTime)
    {
        long timeSent = System.currentTimeMillis();
        long timeWaitedForReply;
        do
        {
            timeWaitedForReply = System.currentTimeMillis() - timeSent;
            try
            {
                long waitTime = maxWaitTime - timeWaitedForReply;
                if (waitTime > 0) //prevent an indefinite call to wait(0)
                {
                    synchronized (manager)
                    {
                        manager.wait(waitTime);
                    }
                }
            }
            catch (InterruptedException ex)
            {
            }
        }
        while (!packetAcknowledged && timeWaitedForReply < maxWaitTime);
    }

    /**
     * Encrypts a packet using AESCrypt based on the encryption type specified
     * in the global configuration.
     *
     * @param packet
     * @return
     */
    private byte[] encryptPacket(byte[] packet)
    {
        if (manager.getEncryptionType() != EncryptionType.ENCRYPTION_NONE)
        {
            byte[] header = Arrays.copyOf(packet, 6);
            byte[] data = Arrays.copyOfRange(packet, 6, packet.length);
            log.debug("Header: " + ProtegeUtils.byteArrayToHex(header));
            log.debug("Unencrypted data: " + ProtegeUtils.byteArrayToHex(data));
            byte[] encrypted = manager.getCrypto().encrypt(data);
            return ArrayUtils.addAll(header, encrypted);
        }
        return packet;
    }

    /**
     * Takes an input packet and returns a copy of the packet with the checksum
     * added on the end. <br>
     * The input packet is not modified.
     *
     * @param packet
     * @return
     */
    private byte[] addChecksum(byte[] packet)
    {
        //checksum - 0-2 bytes (variable)
        byte[] checksum;
        switch (manager.getChecksumType())
        {
            case CHECKSUM_8:
                checksum = ProtegeConnectionManager.calcChecksum8Bit(packet);
                break;

            case CHECKSUM_16_CRC:
                checksum = ProtegeConnectionManager.calcChecksum16Bit(packet);
                break;

            case CHECKSUM_NONE:
            default:
                return packet; //Save processing by returning immediately
        }
        //Create an expanded array and copy in the checksum byte(s).
        byte[] newPacket = new byte[packet.length + checksum.length];
        System.arraycopy(packet, 0, newPacket, 0, packet.length);
        System.arraycopy(checksum, 0, newPacket, packet.length, checksum.length);
        return newPacket;
    }

    private byte[] addLength(byte[] packet)
    {
        int packetLength = packet.length;
        packetLength += (manager.getChecksumType() == ChecksumType.CHECKSUM_16_CRC) ? 2
                : (manager.getChecksumType() == ChecksumType.CHECKSUM_8) ? 1 : 0; //checksum length
        List<Byte> length = ProtegeUtils.intToByteList(packetLength, 2);
        byte[] result = packet.clone();
        result[2] = length.get(0);
        result[3] = length.get(1);
        
        return result;
    }
    
    public void requestStop()
    {
        stopRequested = true;
    }

    /**
     * Checks if a connection is currently live. If not it will block until the
     * connection is established.
     *
     */
    private void verifyConnection()
    {
        while (!manager.isConnected())
        {
            synchronized (manager)
            {
                try
                {
                    log.debug("Waiting for manager to reconnect.");
                    manager.wait();
                    log.debug("Woken by manager.");
                }
                catch (InterruptedException ex)
                {
                }
            }
        }
    }

    /**
     * Notify the TCPWriter that a packet has been acknowledged by the Protege
     * controller.
     *
     */
    public void notifyPacketReceived()
    {
        log.debug("Writer notified of Ack.");
        sendAttempts = 0;
        this.packetAcknowledged = true;
        try
        {
            packetBuffer.remove();
        }
        catch (NoSuchElementException ex)
        {
        }
    }

    public ProtegePacket getLastPacketSent()
    {
        return currentPacket;
    }
}
