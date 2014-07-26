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
package org.openremote.controller.protocol.openwebnet.datastructure.command;

/**
 * This command represent a delay between one command and another one.
 * It is very useful if you want to create automation with delay: i.e. open the tent for 10s and then stop.
 * In the example the definition of the delay time is done using this class
 *
 * @author Flavio Crisciani
 */
public class DelayInterval extends CommandOPEN
{
    // ---- MEMBERS ---- //
    private long delayInMillisecond = 0;			// Delay time in milliseconds

    // ---- METHODS ---- //
    /**
     * Create a delay instance
     * @param who of the actuator
     * @param where of the actuator
     * @param delayInMillisecond delay to wait express in milliseconds [ms]
     */
    public DelayInterval(String who, String where, long delayInMillisecond)
    {
        super("", 6, who, where);
        if (delayInMillisecond < 0)
            delayInMillisecond = 0;
        this.delayInMillisecond = delayInMillisecond;
    }
    /**
     * Get command delay
     * @return the delay
     */
    public long getDelayInMillisecond()
    {
        return delayInMillisecond;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DelayInterval [delayInMillisecond=");
        builder.append(delayInMillisecond);
        builder.append("]");
        return builder.toString();
    }

}
