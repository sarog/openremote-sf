/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

package org.openremote.controller.event;

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
import org.openremote.controller.Configuration;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.exception.InvalidControllerXMLException;
import org.openremote.controller.exception.NoSuchButtonException;
import org.openremote.controller.exception.NoSuchEventException;
import org.openremote.controller.utils.PathUtil;


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
   
   /** The configuration. */
   private Configuration configuration;
   


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
      return queryElementFromXML("//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']");
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
      File xsdfile = new File(getClass().getResource(Constants.CONTROLLER_XSD_PATH).getPath());
      
      sb.setProperty(Constants.SCHEMA_LANGUAGE, Constants.XML_SCHEMA);
      sb.setProperty(Constants.SCHEMA_SOURCE, xsdfile);
      String xmlPath = PathUtil.addSlashSuffix(configuration.getResourcePath()) + Constants.CONTROLLER_XML;
      if (!new File(xmlPath).exists()) {
         throw new ControllerXMLNotFoundException(" Make sure it's in /resources");
      }
      try {
         Document doc = sb.build(new File(xmlPath));
         XPath xpath = XPath.newInstance(xPath);
         xpath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
         List<Element> elements = xpath.selectNodes(doc);
         if(!elements.isEmpty()){
           return elements.get(0);
         }
      } catch (JDOMException e) {
         logger.error("JDOMException occurs when parsing controller.xml.", e);
         throw new InvalidControllerXMLException("check the version of schema or structure of controller.xml with "
               + Constants.CONTROLLER_XSD_PATH);
      } catch (IOException e) {
         String msg = " An I/O error prevents a controller.xml from being fully parsed";
         logger.error(msg, e);
         throw new ControllerXMLNotFoundException(msg);
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


   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
   
}
