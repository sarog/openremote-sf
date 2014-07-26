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
package org.openremote.controller.protocol.openwebnet;

import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.openwebnet.connector.MyHomeJavaConnector;
import org.openremote.controller.protocol.openwebnet.datastructure.command.CommandOPEN;

/**
 * @author Marco Miccini
 */
abstract class OpenWebNetCommand implements Command
{
    // Instance Fields ------------------------------------------------------------
    /** The host to perform the own request on */
    protected String host;

    /** The port to perform the own request on */
    protected int port;

    /** The command which is sent */
    protected CommandOPEN command;

    /** The OpenWebNet connector to send message */
    protected MyHomeJavaConnector OWNConnector;

    // Constructors  ----------------------------------------------------------------
    public OpenWebNetCommand(String host, int port, CommandOPEN command)
    {
        this.host = host;
        this.port = port;
        this.command = command;
        OWNConnector = new MyHomeJavaConnector(host, port);
    }

    // Public Instance Methods
    // ----------------------------------------------------------------------

    public CommandOPEN getCommand()
    {
        return command;
    }

    public static Command createCommand(String host, int port, Integer pollingInterval, Integer timeout, String sensorNamesList, CommandOPEN command)
    {
        int type = command.getType();
        Command cmd;
        if (type == 2)
        {
            cmd = new OpenWebNetReadCommand(host, port, pollingInterval, command);
            return cmd;
        }
        else if (type == 3)
        {
            if (sensorNamesList == null || sensorNamesList.equals(""))
                cmd = new OpenWebNetMonitorReadCommand(host, port, pollingInterval, timeout, command);
            else
                cmd = new OpenWebNetMonitorReadCommand(host, port, pollingInterval, timeout, sensorNamesList, command);
            return cmd;
        }
        else if (type == 1 || type == 4)
        {
            cmd = new OpenWebNetWriteCommand(host, port, command);
            return cmd;
        }
        return null;
    }

    // Object Overrides -----------------------------------------------------------------------------

    /**
     * Returns a string representation of this command. Expected output is:
     * @return  this command as string
     */
    @Override public String toString()
    {
        return command.toString();
    }

}
