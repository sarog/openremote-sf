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
package org.openremote.controller;

/**
 * The Class Constants.
 * 
 * @author Dan 2009-6-1
 */
public class Constants {

   /** The Constant LIRCD_CONF. */
   public final static String LIRCD_CONF = "lircd.conf";
   
   /** The Constant CONTROLLER_XSD_PATH. */
   public final static String CONTROLLER_XSD_PATH = "/controller-2.0-M6.xsd";
   
   public final static String PANEL_XSD_PATH = "/panel-2.0-M6.xsd";
   
   /** The Constant CONTROLLER_XML. */
   public final static String CONTROLLER_XML = "controller.xml";
   
   /** The Constant OPENREMOTE_WEBSITE. */
   public final static String OPENREMOTE_WEBSITE= "http://www.openremote.org";
   
   /** The Constant OPENREMOTE_NAMESPACE. */
   public final static String OPENREMOTE_NAMESPACE= "or";
   
   /** The Constant SCHEMA_LANGUAGE. */
   public final static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   
   /** The Constant XML_SCHEMA. */
   public final static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   
   /** The Constant SCHEMA_SOURCE. */
   public final static String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   
   /** The Constant TRUE. */
   public final static String TRUE = "TRUE";
   
   /** The Constant OK. */
   public final static String OK = "OK";
   
   public static final String SERVER_RESPONSE_TIME_OUT = "TIMEOUT";
   
   public static final String PANEL_XML = "panel.xml";
   
   /** #The following constants are about status and polling action# */
   /** The Constant SENSOR_ID_SEPARATOR. */
   public static final String STATUS_POLLING_SENSOR_IDS_SEPARATOR = ",";
   
   /** The Constant xmlHeader of composed xml-formatted status results. */
   public static final String STATUS_XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openremote.org http://www.openremote.org/schemas/status.xsd\">\n";
   
   /** The Constant XML_STATUS_RESULT_ELEMENT_NAME composed xml-formatted status results. */
   public static final String STATUS_XML_STATUS_RESULT_ELEMENT_NAME = "status";
   
   /** The Constant XML_STATUS_RESULT_ELEMENT_SENSOR_IDENTITY composed xml-formatted status results. */
   public static final String STATUS_XML_STATUS_RESULT_ELEMENT_SENSOR_IDENTITY = "id";
   
   /** The Constant XML_TAIL of composed xml-formatted status results. */
   public static final String STATUS_XML_TAIL = "</openremote>";
   /**##*/

}
