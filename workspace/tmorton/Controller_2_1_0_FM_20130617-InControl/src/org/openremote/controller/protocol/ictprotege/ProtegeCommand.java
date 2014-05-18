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
package org.openremote.controller.protocol.ictprotege;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.ConnectionException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;


/**
 * NOTE THAT ALL MONITORING COMMANDS MUST BE SENT EACH TIME YOU LOGIN
 * 
 * TODO
 * //IMplement either EventListener or ReadComand, 
 * should use EventListener for async updates
 * use ReadCommand for polling updates at 500ms
 * @author Tomas Morton
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 */
public class ProtegeCommand implements ExecutableCommand, EventListener
{

    // Class Members --------------------------------------------------------------------------------

    private final static Logger log = ProtegeSystemConstants.log;


    // Private Instance Fields ----------------------------------------------------------------------

    private ProtegeConnectionManager connectionManager;
    private ProtegePacket packet;


    // Constructors ---------------------------------------------------------------------------------

    public ProtegeCommand(ProtegeConnectionManager connectionManager, ProtegePacket packet)
    {
       this.connectionManager = connectionManager;
       this.packet = packet;
    }


    // Implements ExecutableCommand -----------------------------------------------------------------

    @Override 
    public void send()
    {
        try
        {
            connectionManager.send(packet);
        }
        catch (ConnectionException e)
        {
            log.error(e.getMessage());
        }
    }

  //EventListener implementation for Asynchronous sensor updates

    @Override
    public void setSensor(Sensor sensor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop(Sensor sensor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
