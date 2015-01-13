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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.openremote.controller.utils.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openremote.controller.model.sensor.Sensor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class to save events from the Protege Controller.
 *
 * @author Damon Williams
 */
public class ProtegeEventHandler implements Runnable
{

    private static final File logFile = new File("../logs/ICTProtege/EventLog.xml");
    private static final Logger log = ProtegeUtils.log;
    private static ProtegeEventHandler instance;
    private static ProtegeEventWriter eventWriter;
    
    private List<Sensor> sensors;
    private BlockingQueue<String> eventQueue;
    private LinkedList<String> sensorValues;
    private boolean stopRequested;

    public static synchronized ProtegeEventHandler getInstance()
    {
        if (instance == null)
        {
            instance = new ProtegeEventHandler();
            new Thread(instance, "ProtegeEventHandler").start();
        }
        return instance;
    }

    private ProtegeEventHandler()
    {
        this.eventQueue = new LinkedBlockingQueue<String>();
        this.sensorValues = new LinkedList<String>();
        ProtegeEventHandler.eventWriter = new ProtegeEventWriter();
    }

    public void logEvent(String message)
    {
        eventQueue.add(message);
    }

    @Override
    public void run()
    {
        while (!stopRequested)
        {
            try
            {
                String eventMessage = eventQueue.take(); //blocking
                String eventTime = eventWriter.saveEvent(eventMessage);
                while (sensorValues.size() >= sensors.size())
                {
                    sensorValues.poll(); //Remove oldest event
                }
                sensorValues.offer(eventMessage + " \n(" + eventTime + ")"); //Add new event
                updateSensors();
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public static void requestStop()
    {
        if (instance != null)
        {
            instance.stopRequested = true;
        }
    }

    public static synchronized File getICTLogFile()
    {
        return logFile;
    }

    private static final Comparator<Sensor> sensorIDComparator = new Comparator<Sensor>() {
    @Override
    public int compare(Sensor s1, Sensor s2) {
        return s2.getSensorID() - s1.getSensorID();
        }
    };
    
    public void setSensors(Map<Integer, Sensor> sensorMap)
    {
        sensors = new LinkedList<Sensor>(sensorMap.values());
        Collections.sort(sensors, sensorIDComparator);
        sensorValues = readLastXEvents(sensorMap.size());
        updateSensors();
    }

    private void updateSensors()
    {
        for (int i = 0; i < sensors.size(); i++)
        {        
            String event = (i < sensorValues.size()) ? sensorValues.get(i)
                    : "_";
            sensors.get(i).update(event);
        }
    }

    private LinkedList<String> readLastXEvents(int numEvents)
    {
        LinkedList<String> lastXEvents = new LinkedList<String>();
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(ProtegeEventHandler.getICTLogFile());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Event");

            int numNodes = nList.getLength();

            for (int i = numNodes - 1; i >= numNodes - numEvents; i--)
            {
                if (i < 0 ) //check if there are no more events in the file
                {
                    return lastXEvents;
                }
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    String event = eElement.getElementsByTagName("EventMessage").item(0).getTextContent() +
                            ("\n (" + eElement.getElementsByTagName("Timestamp").item(0).getTextContent() + ")");
                    lastXEvents.push(event);
                }
            }
        }
        //XML errors will be fixed on write
        catch (ParserConfigurationException ex)
        {
        }
        catch (SAXException ex)
        {
        }
        catch (IOException ex)
        {
        }
        return lastXEvents;
    }
}
