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

import flexjson.Path;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.openremote.controller.utils.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openremote.controller.model.sensor.Sensor;
import java.util.logging.Level;
import org.w3c.dom.Attr;
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

    private String eventMessage = "";
    private final Map<Integer, Sensor> sensors;
    private static final Logger log = ProtegeUtils.log;
    int numSensors = 0;
    int whichSensor = 1;
    //private int eventSensorNumber = 0;
    private String[] eventList;
    private int[] sensorList;
    
    
    public ProtegeEventHandler(String eventMessage, Map<Integer, Sensor> sensors)
    {
        this.eventMessage = eventMessage;
        this.sensors = sensors;
        
        numSensors = sensors.size();
        eventList = new String[numSensors];
        sensorList = new int[numSensors];
        for (Sensor s : sensors.values())
        {
            if (whichSensor == 1) {
                sensorList[0] = s.getSensorID();
            } else if (whichSensor == 2) {
                sensorList[1] = s.getSensorID();
            } else if (whichSensor == 3) {
                sensorList[2] = s.getSensorID();
            }
            whichSensor++;
        }
    } 

    @Override
    public void run()
    {
        try
        {
            saveEvent();
        }
        catch (SAXException ex)
        {
            log.debug("SAX Exception thrown." + ex);
        }
        catch (IOException ex)
        {
            log.debug("IO Exception thrown. " + ex);
        }
    }

    private void saveEvent() throws SAXException, IOException
    {    
        File checkFolder = new File("../logs/ICTProtege");
        if(!checkFolder.exists()) {
            File dir = new File("../logs/ICTProtege");
            dir.mkdir();
        }

        File checkFile = new File("../logs/ICTProtege/EventLog.xml");
        if(!checkFile.exists()) {
            log.debug("EVENT FILE DOESN'T EXIST");
            try
            {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                // root elements
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("Events");
                doc.appendChild(rootElement);

                // event elements
                Element event = doc.createElement("Event");
                rootElement.appendChild(event);

                // set attribute to event element
                int nextNodeID = 1;
                Attr attr = doc.createAttribute("id");
                attr.setValue("" + nextNodeID);
                event.setAttributeNode(attr);
                // Event Message elements
                String substring = eventMessage.substring(0, eventMessage.length()-1);
                Element EventMessageElement = doc.createElement("EventMessage");
                EventMessageElement.appendChild(doc.createTextNode(substring));
                event.appendChild(EventMessageElement);

                // Timestamp
                Element timeStamp = doc.createElement("Timestamp");
                timeStamp.appendChild(doc.createTextNode(dateFormat.format(cal.getTime())));
                event.appendChild(timeStamp);

                log.debug("WRITING NEW XML FILE");
                
                // write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("../logs/ICTProtege/EventLog.xml"));

                transformer.transform(source, result);
                
                for (Sensor s : sensors.values())
                {
                    UpdateSensor update = new UpdateSensor(s);
                    //eventSensorNumber++;
                    new Thread(update, "UpdateEventString").start();
                }
                //eventSensorNumber = 0;
            }
            catch (ParserConfigurationException pce)
            {
                log.debug("Parser configuration failure");
            }
            catch (TransformerException tfe)
            {
                log.debug("Transformer Exception thrown");
            }
        } else {

            try
            {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                // root elements
                Document doc = docBuilder.parse("../logs/ICTProtege/EventLog.xml");
                Element rootElement = doc.getDocumentElement();
                //doc.appendChild(rootElement);

                // event elements
                Element event = doc.createElement("Event");
                rootElement.appendChild(event);

                // set attribute to event element
                int nextNodeID = getNextNodeId();
                Attr attr = doc.createAttribute("id");
                attr.setValue("" + nextNodeID);
                event.setAttributeNode(attr);

                // Event Message elements
                Element EventMessageElement = doc.createElement("EventMessage");
                String substring = eventMessage.substring(0, eventMessage.length()-1);
                EventMessageElement.appendChild(doc.createTextNode(substring));
                event.appendChild(EventMessageElement);

                // Timestamp
                Element timeStamp = doc.createElement("Timestamp");
                timeStamp.appendChild(doc.createTextNode(dateFormat.format(cal.getTime())));
                event.appendChild(timeStamp);
                
                // write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult("../logs/ICTProtege/EventLog.xml");

                // Output to console for testing
                //StreamResult result = new StreamResult(System.out);
                transformer.transform(source, result);

                for (Sensor s : sensors.values())
                {
                   // eventSensorNumber++;
                    UpdateSensor update = new UpdateSensor(s);
                    new Thread(update, "UpdateEventString").start();
                }
               // eventSensorNumber = 0;
            }
            catch (ParserConfigurationException pce)
            {
                log.debug("Parser configuration failure");
            }
            catch (TransformerException tfe)
            {
                log.debug("Transformer Exception thrown");
            }
        }
        
    }

    private String[] readError()
    {
        String[] outputLastThreeEvents = new String[3];
        String latestEvent = "";
        String secondEvent = "";
        String thirdEvent = "";
        outputLastThreeEvents[0] = "";
        outputLastThreeEvents[1] = "";
        outputLastThreeEvents[2] = "";
        int eventNum = 1;
        try
        {
            File fXmlFile = new File("../logs/ICTProtege/EventLog.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Event");
            
            int numMinus = 0;
            int numNodes = getNextNodeId() - 1;
            
            if (numNodes < 3) {
                if (numNodes == 1) {
                    numMinus = 2;
                } else if (numNodes == 2) {
                    numMinus = 1;
                }
            }
            
            for (int temp = nList.getLength() - 1; temp > nList.getLength() - (4 - numMinus); temp--)
            {
                
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    
                    if (eventNum == 1) {
                        latestEvent += (eElement.getAttribute("id") + ":");
                        latestEvent+= ("\n" + eElement.getElementsByTagName("EventMessage").item(0).getTextContent());
                        latestEvent += ("\nTimestamp : " + eElement.getElementsByTagName("Timestamp").item(0).getTextContent());
                        latestEvent += "\n";
                        outputLastThreeEvents[0] = latestEvent;
                    } else if (eventNum == 2) {
                        secondEvent += (eElement.getAttribute("id") + ":");
                        secondEvent += ("\n" + eElement.getElementsByTagName("EventMessage").item(0).getTextContent());
                        secondEvent += ("\nTimestamp : " + eElement.getElementsByTagName("Timestamp").item(0).getTextContent());
                        secondEvent += "\n";  
                        outputLastThreeEvents[1] = secondEvent;
                    } else {
                        thirdEvent += (eElement.getAttribute("id") + ":");
                        thirdEvent += ("\n" + eElement.getElementsByTagName("EventMessage").item(0).getTextContent());
                        thirdEvent += ("\nTimestamp : " + eElement.getElementsByTagName("Timestamp").item(0).getTextContent());
                        thirdEvent += "\n";   
                        outputLastThreeEvents[2] = thirdEvent;
                    }
                    eventNum++;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return outputLastThreeEvents;
    }

    private int getNextNodeId() throws ParserConfigurationException, SAXException, IOException
    {
        String filepath = "../logs/ICTProtege/EventLog.xml";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(filepath);

        NodeList list = doc.getElementsByTagName("Event");

        return list.getLength() + 1;
    }

    public class UpdateSensor implements Runnable
    {

        private Sensor sensor = null;

        public UpdateSensor( Sensor sensor)
        {
            this.sensor = sensor;
        }

        @Override
        public void run()
        {

            updateEventString();
        }

        public void updateEventString()
        {
            String[] output = readError();
            if (sensor.getSensorID() == sensorList[0]) {
                sensor.update(output[0]);
                //log.error("" + eventList[0]);
            } else if (sensor.getSensorID() == sensorList[1]) {
                sensor.update(output[1]);
                //log.error("" + eventList[1]);
            } else if (sensor.getSensorID() == sensorList[2]) {
                sensor.update(output[2]);
                //log.error("" + eventList[2]);
            }
        }
    }
}
