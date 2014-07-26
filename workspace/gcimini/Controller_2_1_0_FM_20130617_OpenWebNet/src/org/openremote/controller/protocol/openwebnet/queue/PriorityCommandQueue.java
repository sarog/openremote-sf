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

import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.openremote.controller.protocol.openwebnet.OpenWebNetCommandBuilder;
import org.openremote.controller.utils.Logger;

/**
 * This class implements 3 priority queues of Open commands to be sent
 *
 * @author Flavio Crisciani
 */
public class PriorityCommandQueue
{
    // ---- MEMBERS ---- //
    final Vector<String> level1 = new Vector<String>();
    final Vector<String> level2 = new Vector<String>();
    final Vector<String> level3 = new Vector<String>();

    private static Logger logger = Logger.getLogger(OpenWebNetCommandBuilder.OPENWEBNET_PROTOCOL_LOG_CATEGORY);

    final Semaphore commandsAvailable = new Semaphore(0);

    // ---- METHODS ---- //
    /**
     * Add a command to the high priority queue
     * @param c command to queue
     * @return true if correctly queued
     */
    public void addHighLevel(String c)
    {
        this.level1.add(c);
        commandsAvailable.release();
    }
    /**
     * Add a command to the medium priority queue
     * @param c command to queue
     * @return true if correctly queued
     */
    public void addMediumLevel(String c)
    {
        this.level2.add(c);
        commandsAvailable.release();
    }
    /**
     * Add a command to the low priority queue
     * @param c command to queue
     * @return true if correctly queued
     */
    public void addLowLevel(String c)
    {
        this.level3.add(c);
        commandsAvailable.release();
    }
    /**
     * Get a command, when available from one of the queue, if they are all empty it suspend the thread on a semaphone
     * @return the command to execute
     */
    public String getCommand()
    {
        String resultCommand = null;
        try
        {
            if (commandsAvailable.availablePermits() == 0)
            {
                logger.debug("CommandTail: No commands to execute in the queues, suspending");
            }
            commandsAvailable.acquire();
        }
        catch(InterruptedException e)
        {
            logger.error("PriorityCommandQueue: Exception during suspetion on the semaphore: " + e.toString());
            resultCommand = null;
        }
        if (level1.size() > 0)
            resultCommand = level1.remove(0);
        else if (level2.size() > 0)
            resultCommand = level2.remove(0);
        else if (level3.size() > 0)
            resultCommand = level3.remove(0);

        return resultCommand;
    }
    /**
     * Returns number of commands available
     * @return the number of command available
     */
    public int numCommands()
    {
        return commandsAvailable.availablePermits();
    }

}
