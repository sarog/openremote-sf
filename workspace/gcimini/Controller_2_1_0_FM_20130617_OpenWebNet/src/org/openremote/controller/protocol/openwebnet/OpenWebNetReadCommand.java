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

import java.io.IOException;

import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.openwebnet.connector.MyHomeSocketFactory;
import org.openremote.controller.protocol.openwebnet.datastructure.command.CommandOPEN;
import org.openremote.controller.protocol.openwebnet.datastructure.command.MonitorBuffer;
import org.openremote.controller.protocol.openwebnet.datastructure.command.StatusRequestCmd;
import org.openremote.controller.protocol.openwebnet.datastructure.command.StatusResponseCmd;
import org.openremote.controller.protocol.openwebnet.exception.MalformedCommandOPEN;
import org.openremote.controller.utils.Logger;

/**
 * @author Marco Miccini
 */
public class OpenWebNetReadCommand extends OpenWebNetCommand implements EventListener, Runnable
{

    // Class Members --------------------------------------------------------------
    /**
     * Common logging category.
     */
    private static Logger logger = Logger.getLogger(OpenWebNetCommandBuilder.OPENWEBNET_PROTOCOL_LOG_CATEGORY);
    /**
     * The monitor circular buffer.
     */
    protected static MonitorBuffer monitorBuffer = null;
    /** The thread that is used to capture the monitor messages */
    protected static Thread monitorThread = null;


    // Instance Fields ------------------------------------------------------------
    /** The polling interval which is used for the sensor update thread */
    protected Integer pollingInterval;

    /** The thread that is used to peridically update the sensor */
    protected Thread pollingThread;

    /** The sensor which is updated */
    protected Sensor sensor;

    /** Boolean to indicate if polling thread should run */
    boolean doPoll = false;

    // Constructors  ----------------------------------------------------------------
    public OpenWebNetReadCommand(String host, int port, Integer pollingInterval, CommandOPEN command)
    {
        super(host, port, command);
        this.pollingInterval = pollingInterval;
    }

    // Public Instance Methods
    // ----------------------------------------------------------------------
    public Integer getPollingInterval()
    {
        return pollingInterval;
    }

    public void setPollingInterval(Integer pollingInterval)
    {
        this.pollingInterval = pollingInterval;
    }

    public void setSensorValue(String what)
    {
        if (what != null)
        {
            if (sensor instanceof SwitchSensor)
            {
                if (what.equals("0"))
                    sensor.update("off");
                else if (what.equals("1"))
                    sensor.update("on");
            }
            else
                sensor.update(what);
        }
        else
            sensor.update("N/A");
    }

    // Private Instance Methods ---------------------------------------------------------------------
    /**
     * Starts buffer generation
     * @param host the host to start the monitor session
     * @param port the port to start the monitor session
     */
    protected void startBuffer(String host, int port)
    {
        if (monitorBuffer == null)
        {
            try
            {
                monitorBuffer = MonitorBuffer.getInstance(host, port);
            }
            catch (IOException e)
            {
                logger.error("Different monitor connections required: " + e);
            }

            int retry = 0;
            do
            {
                try
                {
                    monitorBuffer.startMonitoring();
                    if (monitorThread == null)
                    {
                        monitorThread = new Thread(monitorBuffer);
                        monitorThread.setName("Thread for monitor");
                        monitorThread.start();
                    }
                    break;
                }
                catch (IOException e1)
                {
                    retry++;
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e) {}
                    logger.error("Start monitor connection problem retry temptative: " + retry);
                }
            }
            while (retry < 10);
        }
    }

    private String requestOWN()
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
        {
            for (String resp_i : resp)
                logger.info("received message: " + resp_i);
            if (!MyHomeSocketFactory.isACK(resp[0]) && !MyHomeSocketFactory.isNACK(resp[0]))
                return resp[0];
            else
                return null;
        }
        else
        {
            logger.info("received message: " + resp);
            return null;
        }
    }

    private void inizializeSensor()
    {
        String readValue = requestOWN();
        if (readValue != null)
        {
            CommandOPEN resultCommand;
            resultCommand = new StatusResponseCmd(readValue);
            setSensorValue(((StatusResponseCmd)resultCommand).getWhat());
        }
        else
            sensor.update("N/A");
    }

    // Implements EventListener
    // -----------------------------------------------------------------
    @Override
    public void setSensor(Sensor sensor)
    {
        logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
        if (pollingInterval == null)
        {
            startBuffer(host, port);
            monitorBuffer.addRequest(this, (StatusRequestCmd)command);
            this.sensor = sensor;
            inizializeSensor();
        }
        else
        {
            this.sensor = sensor;
            this.doPoll = true;
            pollingThread = new Thread(this);
            pollingThread.setName("Polling thread for sensor: " + sensor.getName());
            pollingThread.start();
        }
    }

    @Override
    public void stop(Sensor sensor)
    {
        this.doPoll = false;
    }

    // Implements Runnable
    // -----------------------------------------------------------------
    @Override
    public void run()
    {
        logger.debug("Sensor thread started for sensor: " + sensor);
        while (doPoll)
        {
            // TODO: al momento viene considerato solo il caso in cui il messaggio ricevuto è formato da un solo comando
            String readValue = requestOWN();
            if (readValue != null)
            {
                CommandOPEN resultCommand;
                resultCommand = new StatusResponseCmd(readValue);
                setSensorValue(((StatusResponseCmd)resultCommand).getWhat());
            }
            else
                sensor.update("N/A");
            try
            {
                Thread.sleep(pollingInterval);          // We wait for the given pollingInterval before requesting URL again
            }
            catch (InterruptedException e)
            {
                doPoll = false;
                pollingThread.interrupt();
            }
        }
        logger.debug("*** Out of run method: " + sensor);
    }

}
