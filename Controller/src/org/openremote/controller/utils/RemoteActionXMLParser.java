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
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openremote.controller.domain.Event;
import org.openremote.controller.domain.IREvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RemoteActionXMLParser {
   
   private Properties events;
   
   public String configXMLPath;

   public List<Event> findEventsByButtonID(String buttonID) {
      List<Event> events = new ArrayList<Event>();
      Element button = query("//button[@id='" + buttonID + "']");
      NodeList nodes = button.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++) {
         String eventID = nodes.item(i).getTextContent().trim();
         Element element = query("//irEvent[@id='" + eventID + "']");
         if(element != null){
            if(IREvent.NODE_NAME.equals(element.getNodeName())){
               events.add(new IREvent(element.getAttribute("name"), element.getAttribute("command")));
            }
         }
      }
      return events;
   }
   
   private Element query(String xPath) {
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
         e.printStackTrace();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      NodeList nodes = (NodeList) result;
      if (nodes.getLength() > 0) {
         return (Element)nodes.item(0);
      }
      return null;
   }

   public void setConfigXMLPath(String configXMLPath) {
      this.configXMLPath = configXMLPath;
   }

   public void setEvents(Properties events) {
      this.events = events;
   }
   
   
   
   
}
