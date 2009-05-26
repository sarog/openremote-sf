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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.event.Event;
import org.openremote.controller.event.EventFactory;
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.exception.InvalidControllerXMLException;
import org.openremote.controller.exception.NoSuchButtonException;
import org.openremote.controller.exception.NoSuchEventException;


/**
 * The controller.xml Parser.
 * 
 * @author Dan 2009-4-3
 */
public class RemoteActionXMLParser {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(RemoteActionXMLParser.class.getName());
   
   /** The event factory. */
   private EventFactory eventFactory;
   


   /**
    * Find event commanders by button id.
    * 
    * @param buttonID the button id
    * 
    * @return the list< event commander>
    */
   @SuppressWarnings("unchecked")
   public List<Event> findEventsByButtonID(String buttonID) {
      List<Event> events = new ArrayList<Event>();
      Element button = queryElementFromXMLById(buttonID);
      if (button == null) {
         throw new NoSuchButtonException("Cannot find that button with id = " + buttonID);
      }
      List<Element> children = button.getChildren();
      for (Element elementRef : children) {
         String eventID = elementRef.getTextTrim();
         String delay = elementRef.getAttributeValue("delay");
         Element element = queryElementFromXMLById(eventID);
         if (element != null) {
            Event event = eventFactory.getEvent(element);
            if (delay != null) {
               event.setDelay(Long.valueOf(delay));
            }
            events.add(event);
         }else{
            throw new NoSuchEventException("Cannot find that event with id = " + eventID);
         }
      }
      return events;
   }
   

   /**
    * Query element from xml by id.
    * 
    * @param id the id
    * 
    * @return the element
    */
   private Element queryElementFromXMLById(String id){
      return queryElementFromXML("//or:*[@id='" + id + "']");
   }
   

   /**
    * Query element from xml.
    * 
    * @param xPath the x path
    * 
    * @return the element
    */
   @SuppressWarnings("unchecked")
   private Element queryElementFromXML(String xPath) {
      SAXBuilder sb = new SAXBuilder(true);
      sb.setValidation(true);
      File xsdfile = new File(getClass().getResource("/controller.xsd").getPath());
      final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
      final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
      final String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
      sb.setProperty(SCHEMA_LANGUAGE, XML_SCHEMA);
      sb.setProperty(SCHEMA_SOURCE, xsdfile);
      String xmlPath = PathUtil.resourcesPath() + "controller.xml";
      
      if (!new File(xmlPath).exists()) {
         throw new ControllerXMLNotFoundException(" Make sure it's in controller/resources");
      }
      try {
         Document doc = sb.build(new File(xmlPath));
         XPath xpath = XPath.newInstance(xPath);
         xpath.addNamespace("or", "http://www.openremote.org");
         List<Element> elements = xpath.selectNodes(doc);
         if(!elements.isEmpty()){
           return elements.get(0);
         }
      } catch (JDOMException e) {
         logger.error("Parser controller.xml occur JDOMException", e);
         throw new InvalidControllerXMLException();
      } catch (IOException e) {
         logger.error("Parser controller.xml occur IOException", e);
      }
      return null;
   }


   /**
    * Sets the event factory.
    * 
    * @param eventFactory the new event factory
    */
   public void setEventFactory(EventFactory eventFactory) {
      this.eventFactory = eventFactory;
   }
   
}
