/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Damon Williams
 */
public class ProtegeEventWriter
{

    private static final Logger log = ProtegeUtils.log;
    private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    private static final Calendar cal = Calendar.getInstance();
    
    /**
     * Saves an event to the protege event log file. 
     * Returns the time the event was logged on the OpenRemote controller.
     *
     * @param eventMessage Event to write to file.
     * @return time the event was logged
     */
    public synchronized String saveEvent(String eventMessage)
    {
        int nextNodeID = 0;
        File checkFolder = new File("../logs/ICTProtege");
        if (!checkFolder.exists())
        {
            File dir = new File("../logs/ICTProtege");
            dir.mkdir();
        }

        String curTime = dateFormat.format(cal.getTime());
        
        if (ProtegeEventHandler.getICTLogFile().exists())
        {
            try
            {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // root elements
                Document doc;
                try
                {
                    try
                    {
                        doc = docBuilder.parse(ProtegeEventHandler.getICTLogFile());                        
                    } 
                    catch (SAXException e)
                    {
                        //Invalid XML file - create a backup and then make a new one
//                        DateFormat backupFileFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
//                        String fileTime = backupFileFormat.format(cal.getTime());
//                        File backup = new File("../logs/ICTProtege/EventLog.xml_Corrupt_" + fileTime + ".xml");
//                        log.error("Corrupt Protege Event Log found.  Backing up to '../logs/ICTProtege/EventLog_Corrupt_"  + fileTime + ".xml" + "'.");
                        createNewLogFile(eventMessage);
                        return curTime;
                    }
                    Element rootElement = doc.getDocumentElement();
                    if (rootElement == null)
                    {
                        rootElement = doc.createElement("Events");
                        doc.appendChild(rootElement);
                    }
                    // event elements
                    Element event = doc.createElement("Event");
                    rootElement.appendChild(event);

                    // set attribute to event element
                    nextNodeID = getNextNodeId();
                    Attr attr = doc.createAttribute("id");
                    attr.setValue("" + nextNodeID);
                    event.setAttributeNode(attr);

                    // Event Message elements
                    Element EventMessageElement = doc.createElement("EventMessage");
                    String substring = eventMessage.substring(0, eventMessage.length() - 1);
                    EventMessageElement.appendChild(doc.createTextNode(substring));
                    event.appendChild(EventMessageElement);

                    // Timestamp
                    Element timeStamp = doc.createElement("Timestamp");
                    timeStamp.appendChild(doc.createTextNode(curTime));
                    event.appendChild(timeStamp);

                    // write the content into xml file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(ProtegeEventHandler.getICTLogFile());

                    transformer.transform(source, result);
                }
                catch (SAXException ex)
                {
                    log.error("SAX Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
                catch (IOException ex)
                {
                    log.error("IO Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            catch (ParserConfigurationException pce)
            {
                log.error("Parser configuration failure" + pce.getMessage());
                pce.printStackTrace();
            }
            catch (TransformerException tfe)
            {
                log.error("Transformer Exception thrown" + tfe.getMessage());
                tfe.printStackTrace();
            }
        } else
        {
            createNewLogFile(eventMessage);
        }
        return curTime;
    }

    private int getNextNodeId() throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(ProtegeEventHandler.getICTLogFile());

        NodeList list = doc.getElementsByTagName("Event");

        return list.getLength() + 1;
    }
    
    private void createNewLogFile(String eventMessage)
    {        
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Events");
            doc.appendChild(rootElement);

            // event elements
            Element event = doc.createElement("Event");
            rootElement.appendChild(event);

            // set attribute to event element
            Attr attr = doc.createAttribute("id");
            attr.setValue(Integer.toString(1));
            event.setAttributeNode(attr);
            // Event Message elements
            String substring = eventMessage.substring(0, eventMessage.length() - 1);
            Element EventMessageElement = doc.createElement("EventMessage");
            EventMessageElement.appendChild(doc.createTextNode(substring));
            event.appendChild(EventMessageElement);

            // Timestamp
            Element timeStamp = doc.createElement("Timestamp");
            timeStamp.appendChild(doc.createTextNode(dateFormat.format(cal.getTime())));
            event.appendChild(timeStamp);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(ProtegeEventHandler.getICTLogFile());

            transformer.transform(source, result);
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Parser configuration failure: " + pce);
            pce.printStackTrace();
        }
        catch (TransformerException tfe)
        {
            log.error("Transformer Exception thrown: " + tfe);
            tfe.printStackTrace();
        }
    }
}
