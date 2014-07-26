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

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Vector;

import org.openremote.controller.protocol.openwebnet.OpenWebNetCommandBuilder;
import org.openremote.controller.protocol.openwebnet.OpenWebNetMonitorReadCommand;
import org.openremote.controller.protocol.openwebnet.OpenWebNetReadCommand;
import org.openremote.controller.protocol.openwebnet.connector.MyHomeJavaConnector;
import org.openremote.controller.utils.Logger;

/**
 * Singleton class to implement a circular buffer to manage the monitor output stream.
 * 
 * @author Marco Miccini
 */
public class MonitorBuffer extends Stack<String> implements Runnable
{
    // Class Members --------------------------------------------------------------
    /**
     * Common logging category.
     */
    private static Logger logger = Logger.getLogger(OpenWebNetCommandBuilder.OPENWEBNET_PROTOCOL_LOG_CATEGORY);
    private static MyHomeJavaConnector myPlant = null;
    private static Vector<Entry<OpenWebNetReadCommand,StatusRequestCmd>> statusRequests = null;
    private static Vector<Entry<OpenWebNetMonitorReadCommand,DimensionRequestCmd>> dimensionRequests = null;
    private final int maxSize = 100;

    // Instance Fields ------------------------------------------------------------
    /** Boolean to indicate if polling thread should run */
    boolean doPoll = false;

    /**
     * Private constructor to control the instance creation.
     */
    private MonitorBuffer() {}

    /**
     * This class is loaded/initialized to the first execution of getInstance() in thread-safe manner.
     * Also the initialization of the static attribute, therefore, is serialized.
     */
    private static class Container
    {
        private final static MonitorBuffer instance = new MonitorBuffer();
    }

    // Public Instance Methods
    // ----------------------------------------------------------------------
    /**
     * Method to access at the singleton instance. It provides for the thread-safe creation only with the first call.
     * @param ip
     * @param port
     * @return the singleton instance
     */
    public static MonitorBuffer getInstance(String ip, int port) throws IOException
    {
        if (myPlant == null)
            myPlant = new MyHomeJavaConnector(ip, port);
        else if (!myPlant.ip.equals(ip) || myPlant.port != port)
            throw new IOException();

        if (statusRequests == null)
            statusRequests = new Vector<Entry<OpenWebNetReadCommand,StatusRequestCmd>>();
        if (dimensionRequests == null)
            dimensionRequests = new Vector<Entry<OpenWebNetMonitorReadCommand,DimensionRequestCmd>>();

        return Container.instance;
    }

    /**
     * Starts the monitoring
     * @throws IOException
     */
    public void startMonitoring() throws IOException
    {
        myPlant.startMonitoring();
        doPoll = true;
    }

    /**
     * Stops the monitoring
     * @throws IOException
     */
    public void stopMonitoring() throws IOException
    {
        myPlant.stopMonitoring();
        doPoll = false;
    }

    /**
     * Searches a status response in the monitor buffer with who and where of the command given
     * @param command the command searched
     * @return the corresponding object command if found, null otherwise
     */
    public synchronized StatusResponseCmd search(StatusRequestCmd command)
    {
        CommandOPEN resultCommand = null;
        for (int i = elementCount - 1; i >= 0; i--)
        {
            if (elementData[i] != null)
            {
                resultCommand = CommandOPEN.getCommandByString((String)elementData[i]);
                if (resultCommand instanceof StatusResponseCmd && CommandOPEN.statusEquals(command, (StatusResponseCmd)resultCommand))
                    return (StatusResponseCmd)resultCommand;
            }
        }
        return null;
    }

    /**
     * Searches a dimension response in the monitor buffer with who, where and dimension of the command given
     * @param command the command searched
     * @return the corresponding object command if found, null otherwise
     */
    public synchronized DimensionResponseCmd search(DimensionRequestCmd command)
    {
        CommandOPEN resultCommand = null;
        for (int i = elementData.length - 1; i >= 0; i--)
        {
            if (elementData[i] != null)
            {
                resultCommand = CommandOPEN.getCommandByString((String)elementData[i]);
                if (resultCommand instanceof DimensionResponseCmd && CommandOPEN.dimensionEquals(command, (DimensionResponseCmd)resultCommand))
                    return (DimensionResponseCmd)resultCommand;
            }
        }
        return null;
    }

    /**
     * Stores a status request
     * @param cmd the object that sends the request
     * @param command the status request sent
     */
    public synchronized void addRequest(OpenWebNetReadCommand cmd, StatusRequestCmd command)
    {
        Entry<OpenWebNetReadCommand,StatusRequestCmd> e = new SimpleEntry<OpenWebNetReadCommand,StatusRequestCmd>(cmd,command);
        statusRequests.add(e);
    }
    /**
     * Stores a dimension request
     * @param cmd the object that sends the request
     * @param command the dimension request sent
     */
    public synchronized void addRequest(OpenWebNetMonitorReadCommand cmd, DimensionRequestCmd command)
    {
        Entry<OpenWebNetMonitorReadCommand,DimensionRequestCmd> e = new SimpleEntry<OpenWebNetMonitorReadCommand,DimensionRequestCmd>(cmd,command);
        dimensionRequests.add(e);
    }

    // Private Instance Methods
    // ----------------------------------------------------------------------
    private void serveRequest(String commandString)
    {
        CommandOPEN resultCommand = CommandOPEN.getCommandByString(commandString);
        if (resultCommand instanceof StatusResponseCmd)
        {
            for (int i = 0; i < statusRequests.size(); i++)
            {
                Entry<OpenWebNetReadCommand, StatusRequestCmd> e = statusRequests.elementAt(i);
                if (CommandOPEN.statusEquals(e.getValue(), (StatusResponseCmd)resultCommand))
                    e.getKey().setSensorValue(((StatusResponseCmd)resultCommand).getWhat());
            }
        }
        else if (resultCommand instanceof DimensionResponseCmd)
        {
            for (int i = 0; i < dimensionRequests.size(); i++)
            {
                Entry<OpenWebNetMonitorReadCommand, DimensionRequestCmd> e = dimensionRequests.elementAt(i);
                if (CommandOPEN.dimensionEquals(e.getValue(), (DimensionResponseCmd)resultCommand))
                    e.getKey().setSensorValue(((DimensionResponseCmd)resultCommand).getValues());
            }
        }
    }

    // Extendeds Stack
    // -----------------------------------------------------------------
    @Override
    public String push(String item)
    {
        if (this.size() == maxSize)
            removeElementAt(0);
        addElement(item);
        serveRequest(item);
        return item;
    }

    // Implements Runnable
    // -----------------------------------------------------------------
    @Override
    public void run()
    {
        while (doPoll)
        {
            try
            {
                myPlant.readMonitoring(this);
            }
            catch (InterruptedException e)
            {
                logger.info("InterruptedException MonitorBuffer: " + e);
                doPoll = false;
                Thread.currentThread().interrupt();
            }
        }
    }
}
