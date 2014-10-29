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

import org.openremote.controller.protocol.ictprotege.network.ProtegeConnectionManager;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

/**
 * Commands received from OpenRemote devices connecting to ICT Protege
 * controllers.
 *
 * @author Tomas Morton
 * @author Adam Mcnabb
 */
public class ProtegeCommand implements ExecutableCommand, EventListener
{

    private static final Logger log = ProtegeUtils.log;

    private ProtegeConnectionManager connectionManager;
    private ProtegePacket packet;

    public ProtegeCommand(ProtegeConnectionManager connectionManager, ProtegePacket packet)
    {
        this.connectionManager = connectionManager;
        this.packet = packet;
    }

    /**
     * ExecutableCommand implementation for sending a packet to the ICT Protege
     * controller.
     *
     */
    @Override
    public void send()
    {
        if (packet.getCommandType().getPriority() == ProtegeCommandType.PRIORITY_CONTROLLER_CONFIG)
        {
            connectionManager.processControllerCommand(packet);
        }
        else
        {
            connectionManager.send(packet);
        }

    }

    /**
     * EventListener implementation to add a sensor for Asynchronous sensor
     * updates.
     *
     */
    @Override
    public void setSensor(Sensor sensor)
    {
        
        connectionManager.setSensor(sensor, packet);
    }

    /**
     * EventListener implementation to remove a sensor from Asynchronous sensor
     * updates.
     *
     * @param sensor
     */
    @Override
    public void stop(Sensor sensor)
    {
        connectionManager.removeSensor(packet);
    }
}
