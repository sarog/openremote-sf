/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.openwebnet.queue;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.openremote.controller.protocol.openwebnet.OpenWebNetCommandBuilder;
import org.openremote.controller.protocol.openwebnet.connector.*;
import org.openremote.controller.utils.Logger;

/**
 * This thread extract new command from the priority queue and send them on the Plant.
 * Between each one it put a 300ms of delay to assure the correct execution on the plant.
 * If there is no available command it suspends on the priority queue semaphore.
 *
 * @author Flavio Crisciani
 */
public class PriorityQueueThread implements Runnable
{
    // ---- MEMBERS ---- //
    MyHomeJavaConnector myConnector = null;
    PriorityCommandQueue list = null;
    Socket sk = null;
    PrintWriter output = null;

    private static Logger logger = Logger.getLogger(OpenWebNetCommandBuilder.OPENWEBNET_PROTOCOL_LOG_CATEGORY);

    // ---- METHODS ---- //
    private void closeSocket()
    {
        if (output != null)
        {
            output.close();
            output = null;
        }
        if (sk != null)
        {
            try
            {
                MyHomeSocketFactory.disconnect(sk);
            }
            catch (IOException e)
            {
                logger.error("PriorityQueueThread: Problem during connection closure - " + e.toString());
                e.printStackTrace();
            }
            sk = null;
        }
    }
    /**
     * Create the Priority Queue Thread giving the reference to the MyHome connector and the Priority queue
     * @param myConnector myhome connector used only for IP, port read
     * @param list priority queue to handle
     */
    public PriorityQueueThread(final MyHomeJavaConnector myConnector, final PriorityCommandQueue list)
    {
        this.myConnector = myConnector;
        this.list = list;
    }

    @Override
    public void run()
    {
        String tosend = null;
        do
        {
            try
            {
                tosend = list.getCommand();
                if(sk == null)                      // Create a new command session
                    try
                    {
                        sk = MyHomeSocketFactory.openCommandSession(myConnector.ip, myConnector.port);
                    }
                    catch(IOException e)
                    {
                        logger.error("PriorityQueueThread: Problem during socket monitor opening - " + e.toString());
                        continue;
                    }

                try
                {
                    if (output == null)
                        output = new PrintWriter(sk.getOutputStream());
                    output.write(tosend);
                    output.flush();
                }
                catch(IOException e)
                {
                    logger.error("PriorityQueueThread: Problem during command sending - " + e.toString());
                    closeSocket();
                    continue;
                }
                try
                {
                    Thread.sleep(300);              // Wait 300ms to be sure that command sent had been executed
                }
                catch(InterruptedException e)
                {
                    logger.error("PriorityQueueThread: Problem during suspension - " + e.toString());
                    continue;
                }
                if (list.numCommands() == 0)        // There are no more message to handle close command session
                    closeSocket();
            }
            catch (Exception e)
            {
                logger.error("PriorityQueueThread: Not handled exception - " + e.toString());
                closeSocket();
            }
        }
        while(true);
    }

}
