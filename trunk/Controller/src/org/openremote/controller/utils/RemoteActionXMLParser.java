/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openremote.controller.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.openremote.controller.commander.EventCommander;
import org.openremote.controller.commander.EventCommanderFactory;
import org.openremote.controller.event.EventFactory;
import org.openremote.irbuilder.domain.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The Remote Action XML Parser.
 * 
 * @author Dan 2009-4-3
 */
public class RemoteActionXMLParser {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(RemoteActionXMLParser.class.getName());
   
   /** The config xml path. */
   private String configXMLPath;
   
   /** The event factory. */
   private EventFactory eventFactory;
   
   /** The event commander factory. */
   private EventCommanderFactory eventCommanderFactory;



   /**
    * Find event commanders by button id.
    * 
    * @param buttonID the button id
    * 
    * @return the list< event commander>
    */
   public List<EventCommander> findEventCommandersByButtonID(String buttonID) {
      List<EventCommander> commanders = new ArrayList<EventCommander>();
      Element button = queryElementFromXMLById(buttonID);
      NodeList nodes = button.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++) {
         String eventID = nodes.item(i).getTextContent().trim();
         Element element = queryElementFromXMLById(eventID);
         if(element != null){
            Event event = eventFactory.getEvent(element);
            commanders.add(eventCommanderFactory.getCommander(element.getNodeName(), event));
         }
      }
      return commanders;
   }
   

   /**
    * Query element from xml by id.
    * 
    * @param id the id
    * 
    * @return the element
    */
   private Element queryElementFromXMLById(String id){
      return queryElementFromXML("//*[@id='" + id + "']");
   }
   

   /**
    * Query element from xml.
    * 
    * @param xPath the x path
    * 
    * @return the element
    */
   private Element queryElementFromXML(String xPath) {
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); // never forget this!
      Object result = null;
      try {
         DocumentBuilder builder = domFactory.newDocumentBuilder();
         Document doc = builder.parse(configXMLPath);
         XPathFactory factory = XPathFactory.newInstance();
         XPath xpath = factory.newXPath();
         XPathExpression expr = xpath.compile(xPath);
         result = expr.evaluate(doc, XPathConstants.NODESET);
      } catch (XPathExpressionException e) {
         logger.error("XPathExpression error while parsing the controller.xml", e);
      } catch (ParserConfigurationException e) {
         logger.error("Can't parse the controller.xml", e);
      } catch (SAXException e) {
         logger.error("Can't parse the controller.xml", e);
      } catch (IOException e) {
         logger.error("Can't find the controller.xml", e);
      }
      NodeList nodes = (NodeList) result;
      if (nodes.getLength() > 0) {
         return (Element)nodes.item(0);
      }
      return null;
   }

   /**
    * Sets the config xml path.
    * 
    * @param configXMLPath the new config xml path
    */
   public void setConfigXMLPath(String configXMLPath) {
      this.configXMLPath = configXMLPath;
   }

   /**
    * Sets the event factory.
    * 
    * @param eventFactory the new event factory
    */
   public void setEventFactory(EventFactory eventFactory) {
      this.eventFactory = eventFactory;
   }

   /**
    * Sets the event commander factory.
    * 
    * @param eventCommanderFactory the new event commander factory
    */
   public void setEventCommanderFactory(EventCommanderFactory eventCommanderFactory) {
      this.eventCommanderFactory = eventCommanderFactory;
   }
   
   
}
