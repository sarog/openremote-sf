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
package org.openremote.controller.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openremote.controller.event.RemoteActionXMLParser;
import org.openremote.controller.event.Stateful;
import org.openremote.controller.service.StatusCommandService;

/**
 * The implementation for ButtonCommandService class.
 * 
 * @author Handy.Wang
 */
public class StatusCommandServiceImpl implements StatusCommandService {
    
    /** The Constant CONTROL_ID_SEPARATOR. */
    private static final String CONTROL_ID_SEPARATOR = ",";

    /** The remote action xml parser. */
    private RemoteActionXMLParser remoteActionXMLParser;
    
    /** The Constant xmlHeader of composed xml-formatted status results. */
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"\">\n";
    
    /** The Constant XML_STATUS_RESULT_ELEMENT_NAME composed xml-formatted status results. */
    private static final String XML_STATUS_RESULT_ELEMENT_NAME = "status";
    
    /** The Constant XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY composed xml-formatted status results. */
    private static final String XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY = "id";
    
    /** The Constant XML_TAIL of composed xml-formatted status results. */
    private static final String XML_TAIL = "</openremote>";

    /**
     * {@inheritDoc}
     */
    public String trigger(String unParsedcontrolIDs){
        
       String[] parsedBtnIDs = unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
       Map<String, Stateful> statusEvents = new HashMap<String, Stateful>();
       for (String controlID : parsedBtnIDs) {
           statusEvents.put(controlID, remoteActionXMLParser.findStatusEventsByControlID(controlID));
       }
       StringBuffer sb = new StringBuffer();
       sb.append(XML_HEADER);
       
       Set<String> controlIDs = statusEvents.keySet();
       for (String controlID : controlIDs) {
           sb.append("<" + XML_STATUS_RESULT_ELEMENT_NAME + " " + XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY + "=\"" + controlID + "\">");
           sb.append(statusEvents.get(controlID).queryStatus());
           sb.append("</" + XML_STATUS_RESULT_ELEMENT_NAME + ">\n");
           sb.append("\n");
       }
       
       sb.append(XML_TAIL);       
       return sb.toString();
   }

    /**
     * Sets the remote action xml parser.
     * 
     * @param remoteActionXMLParser the new remote action xml parser
     */
    public void setRemoteActionXMLParser(
        RemoteActionXMLParser remoteActionXMLParser) {
        this.remoteActionXMLParser = remoteActionXMLParser;
    }

}
