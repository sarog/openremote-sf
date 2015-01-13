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
import org.openremote.controller.protocol.ictprotege.ProtegePacketHandler;
import org.openremote.controller.protocol.ictprotege.ProtegeUtils;
import org.openremote.controller.utils.Logger;

/**
 * Network receiver thread for ICT Protege implementation.
 *
 * @author Tomas Morton
 */
public class ProtegeTCPReader implements Runnable
{

    private static final Logger log = ProtegeUtils.log;
    private final ProtegeConnectionManager manager;
    private boolean stopRequested;

    public ProtegeTCPReader(ProtegeConnectionManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void run()
    {
        while (!stopRequested)
        {
            verifyConnection();
            try
            {
                //Monitor for an ICT packet header - "IC"
                if (waitForPacket())
                {
                    log.debug("ICT Packet received.");
                    byte[] input = readInputPacket();
                    //Create a thread to handle the packet
                    ProtegePacketHandler handler = new ProtegePacketHandler(manager, input);
                    new Thread(handler, "ProtegePacketHandler").start();
                }
                else
                {
                    log.info("Connection to controller lost while waiting for packet.");
                    manager.notifyConnectionLost();
                    synchronized (manager)
                    {
                        log.debug("Notifying manager of connection lost.");
                        manager.notifyAll();
                    }
                }
            }
            catch (IOException e)
            {
                log.debug("Protege network error: " + e);
                manager.notifyConnectionLost();
                synchronized (manager)
                {
                    log.info("Connection lost due to IO exception.");
                    manager.notifyAll();
                }
            }
        }
    }

    /**
     * Listens for a packet from the Protege controller. Returns true if an ICT
     * packet has been found, or false if the connection has been lost.
     *
     * @return
     * @throws IOException
     */
    private boolean waitForPacket() throws IOException
    {
        boolean packetInitiated = false;
        do
        {
            byte received = (byte) manager.getInputStream().read();
            log.debug("Received byte: " + received);
            if (received == ProtegeUtils.HEADER_LOW)
            {
                received = (byte) manager.getInputStream().read();
                if (received == ProtegeUtils.HEADER_HIGH)
                {
                    packetInitiated = true;
                }
            }
            else
            {
                if (received == -1)
                {
                    break;
                }
            }
        }
        while (!packetInitiated && !stopRequested);
       
        return packetInitiated;
    }

    /**
     * Reads and returns a packet from the input stream.
     *
     * @return
     * @throws IOException
     */
    private byte[] readInputPacket() throws IOException
    {
        byte packetLengthLow = (byte) manager.getInputStream().read();
        byte packetLengthHigh = (byte) manager.getInputStream().read();
        int packetLength = packetLengthLow + packetLengthHigh;
        //Set up the array to store the packet and put in received values for checksums
        byte[] input = new byte[packetLength];
        input[0] = ProtegeUtils.HEADER_LOW;
        input[1] = ProtegeUtils.HEADER_HIGH;
        input[2] = packetLengthLow;
        input[3] = packetLengthHigh;
        int bytesRead = 4;
        int received;
        do
        {
            received = manager.getInputStream().read(input, bytesRead, packetLength - bytesRead);
            bytesRead += received;
        }
        while (bytesRead < packetLength && received != -1);

        log.debug("Received packet: " + ProtegeUtils.byteArrayToHex(input));
        return input;
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
                    log.debug("Waiting for reconnect.");
                    manager.wait();

                }
                catch (InterruptedException ex)
                {
                }
            }
        }
    }

}
