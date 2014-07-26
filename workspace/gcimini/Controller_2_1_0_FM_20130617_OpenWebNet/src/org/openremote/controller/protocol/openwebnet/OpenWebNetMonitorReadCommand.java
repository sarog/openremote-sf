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

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.openwebnet.datastructure.command.CommandOPEN;
import org.openremote.controller.protocol.openwebnet.datastructure.command.DimensionRequestCmd;
import org.openremote.controller.protocol.openwebnet.datastructure.command.DimensionResponseCmd;
import org.openremote.controller.protocol.openwebnet.exception.MalformedCommandOPEN;
import org.openremote.controller.utils.Logger;

/**
 * @author Marco Miccini
 */
public class OpenWebNetMonitorReadCommand extends OpenWebNetReadCommand
{

    // Class Members --------------------------------------------------------------
    /**
     * Common logging category.
     */
    private static Logger logger = Logger.getLogger(OpenWebNetCommandBuilder.OPENWEBNET_PROTOCOL_LOG_CATEGORY);

    // Instance Fields ------------------------------------------------------------
    /** The timeout which is used for the monitor thread */
    protected Integer timeout;

    /** The map of sensors which all use the same command */
    private Map<String,Sensor> sensors;
    
    /** The ordered list of sensors'names */
    private String[] sensorNames = null;

    // Constructors  ----------------------------------------------------------------
    public OpenWebNetMonitorReadCommand(String host, int port, Integer pollingInterval, Integer timeout, CommandOPEN command)
    {
        super(host, port, pollingInterval, command);
        this.timeout = timeout;
        startBuffer(host, port);
    }
    public OpenWebNetMonitorReadCommand(String host, int port, Integer pollingInterval, Integer timeout, String sensorNamesList, CommandOPEN command)
    {
        super(host, port, pollingInterval, command);
        this.timeout = timeout;
        sensorNames = sensorNamesList.split(";");
        sensors = new HashMap<String,Sensor>();
        startBuffer(host, port);
    }

    // Public Instance Methods
    // ----------------------------------------------------------------------
    public Integer getTimeout()
    {
        return timeout;
    }

    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }

    public void setSensorValue(String[] values)
    {
        if (values == null || values.length == 0)
        {
            if (sensorNames != null)
                for (int i = 0; i < sensorNames.length; i++)
                    sensors.get(sensorNames[i]).update("N/A");
            else
                sensor.update("N/A");
        }
        else
        {
            if (sensorNames != null)
            {
                if (sensorNames.length != values.length)
                    logger.error("The values number (" + values.length +") doesn't correspond with the associated sensors number (" + sensorNames.length + ")");
                for (int i = 0; i < sensorNames.length && i < values.length; i++)
                {
                    if (!sensorNames[i].equals(""))
                    {
                        Sensor sen = sensors.get(sensorNames[i]);
                        if (sen instanceof SwitchSensor)
                        {
                            if (values[i].equals("0"))
                                sen.update("off");
                            else if (values[i].equals("1"))
                                sen.update("on");
                        }
                        else
                            sen.update(values[i]);
                    }
                }
            }
            else
            {
                if (values.length != 1)
                    logger.error("The same sensor (" + sensor + ") is associated with multiple values");
                sensor.update(values[0]);
            }
        }
    }

    // Private Instance Methods ---------------------------------------------------------------------
    private DimensionResponseCmd searchValues()
    {
        if (timeout == null)
            throw new RuntimeException("Could not search sensor value because no timeout was given");

        long ct = System.currentTimeMillis();
        DimensionResponseCmd resultCommand = null;
        do
        {
            try
            {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {}
            resultCommand = monitorBuffer.search((DimensionRequestCmd)command);
        }
        while (System.currentTimeMillis() - ct < timeout && resultCommand == null);
        return resultCommand;
    }

    private void send()
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

    private void inizializeSensor()
    {
        send();
        long ct = System.currentTimeMillis();
        DimensionResponseCmd resultCommand = null;
        do
        {
            try
            {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {}
            resultCommand = monitorBuffer.search((DimensionRequestCmd)command);
        }
        while (System.currentTimeMillis() - ct < 3000 && resultCommand == null);

        if (resultCommand == null)
        {
            logger.info("searched message: " + null);
            if (sensorNames != null)
                for (int i = 0; i < sensorNames.length; i++)
                    sensors.get(sensorNames[i]).update("N/A");
            else
                sensor.update("N/A");
        }
        else
        {
            logger.info("searched message: " + resultCommand.getCommandString());
            String[] values = ((DimensionResponseCmd)resultCommand).getValues();
            setSensorValue(values);
        }
    }

    // Implements EventListener
    // -----------------------------------------------------------------
    @Override
    public void setSensor(Sensor sensor)
    {
        logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
        if (pollingInterval == null)
        {
            monitorBuffer.addRequest(this, (DimensionRequestCmd)command);
            if (sensorNames != null)
                sensors.put(sensor.getName(), sensor);
            else
                this.sensor = sensor;
            inizializeSensor();
        }
        else
        {
            if (sensorNames != null)
            {
                sensors.put(sensor.getName(), sensor);
                if (sensors.size() == 1)
                {
                    this.doPoll = true;
                    pollingThread = new Thread(this);
                    pollingThread.setName("Polling thread for sensor: " + sensor.getName());
                    pollingThread.start();
                }
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
    }

    @Override
    public void stop(Sensor sensor)
    {
        if (sensorNames == null)
            this.doPoll = false;
        else
        {
            sensors.remove(sensor);
            if (sensors.size() == 0)
                this.doPoll = false;
        }
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
            send();
            DimensionResponseCmd resultCommand = searchValues();

            if (resultCommand == null)
            {
                logger.info("searched message: " + null);
                if (sensorNames != null)
                {
                    for (int i = 0; i < sensorNames.length; i++)
                        sensors.get(sensorNames[i]).update("N/A");
                }
                else
                    sensor.update("N/A");
            }
            else
            {
                logger.info("searched message: " + resultCommand.getCommandString());
                String[] values = ((DimensionResponseCmd)resultCommand).getValues();
                setSensorValue(values);
            }
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
