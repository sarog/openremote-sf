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

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.openwebnet.datastructure.command.CommandOPEN;
import org.openremote.controller.protocol.openwebnet.exception.MalformedCommandOPEN;
import org.openremote.controller.utils.Logger;

/**
 * @author Marco Miccini
 */
public class OpenWebNetWriteCommand extends OpenWebNetCommand implements ExecutableCommand
{
    // Class Members --------------------------------------------------------------
    /**
     * Common logging category.
     */
    private static Logger logger = Logger.getLogger(OpenWebNetCommandBuilder.OPENWEBNET_PROTOCOL_LOG_CATEGORY);


    // Constructors  ----------------------------------------------------------------
    public OpenWebNetWriteCommand(String host, int port, CommandOPEN command)
    {
        super(host, port, command);
    }


    // Implements ExecutableCommand
    // -----------------------------------------------------------------
    @Override
    public void send()
    {
        String[] resp = null;
        try
        {
            resp = OWNConnector.sendCommandSync(command);
            logger.info("sent message: " + command.getCommandString());
        }
        catch (MalformedCommandOPEN e)
        {
            logger.error("The OWN command is incorrect", e);
        }
        if (resp != null)
            for (String resp_i : resp)
                logger.info("received message: " + resp_i);
        else
            logger.info("received message: " + resp);
    }

}
