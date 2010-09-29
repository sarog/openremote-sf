/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
 * TODO
 * 
 * @author Dan 2009-6-1
 */
public class Constants {

   public final static String LIRCD_CONF = "lircd.conf";
   
   public final static String CONTROLLER_XSD_PATH = "/controller-2.0-M7.xsd";
   
   public final static String PANEL_XSD_PATH = "/panel-2.0-M7.xsd";
   
   public final static String CONTROLLER_XML = "controller.xml";
   
   public final static String OPENREMOTE_WEBSITE= "http://www.openremote.org";
   
   public final static String OPENREMOTE_NAMESPACE= "or";
   
   public final static String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   
   public final static String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   
   public final static String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
   
   public final static String TRUE = "TRUE";
   
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
   
   public static final String SENSORS_ELEMENT_NAME = "sensors";
   
   public static final String INCLUDE_ELEMENT_NAME = "include";
   
   public static final String SENSOR_TYPE_ATTRIBUTE_NAME = "type";
   
   public static final String ID_ATTRIBUTE_NAME = "id";
   
   public static final String REF_ATTRIBUTE_NAME = "ref";
   
   /** The Constant XML_STATUS_RESULT_ELEMENT_SENSOR_IDENTITY composed xml-formatted status results. */
   public static final String STATUS_XML_STATUS_RESULT_ELEMENT_SENSOR_IDENTITY = "id";
   
   /** The Constant XML_TAIL of composed xml-formatted status results. */
   public static final String STATUS_XML_TAIL = "</openremote>";

   public static final String HTTP_BASIC_AUTH_HEADER_NAME = "Authorization";

   public static final String HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX = "Basic ";

}
