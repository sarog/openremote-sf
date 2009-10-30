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
package org.openremote.controller.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.service.StatusCommandService;

/**
 * The implementation for ButtonCommandService class.
 * 
 * @author Handy.Wang 2009-10-15
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
    
    private StatusCacheService statusCacheService;

    /**
     * {@inheritDoc}
     */
    public String trigger(String unParsedcontrolIDs){
        
       String[] parsedControlIDs = unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
       Map<String, StatusCommand> statusCommands = new HashMap<String, StatusCommand>();
       for (String controlID : parsedControlIDs) {
           statusCommands.put(controlID, remoteActionXMLParser.findStatusCommandByControlID(controlID));//TODO replace with statusCache.getStatusByComponentId(controlID);
          //statusCommands.put(controlID, statusCacheService.getStatusByComponentId(Integer.parseInt(controlID)).toString());
       }
       StringBuffer sb = new StringBuffer();
       sb.append(XML_HEADER);
       
       Set<String> controlIDs = statusCommands.keySet();
       for (String controlID : controlIDs) {
           sb.append("<" + XML_STATUS_RESULT_ELEMENT_NAME + " " + XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY + "=\"" + controlID + "\">");
           sb.append(statusCommands.get(controlID).read());
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

   @Override
   public String readFromCache(String unParsedcontrolIDs) {
      Set<Integer> statusComponentIDs = parseStatusComponentIDStrToSet(unParsedcontrolIDs);
      Map<Integer, String> latestStatuses = statusCacheService.queryStatuses(statusComponentIDs);
      
      StringBuffer sb = new StringBuffer();
      sb.append(XML_HEADER);
      Set<Integer> componentIDs = latestStatuses.keySet();
      for (Integer componentID : componentIDs) {
          sb.append("<" + XML_STATUS_RESULT_ELEMENT_NAME + " " + XML_STATUS_RESULT_ELEMENT_CONTROL_IDENTITY + "=\"" + componentID + "\">");
          sb.append(latestStatuses.get(componentID));
          sb.append("</" + XML_STATUS_RESULT_ELEMENT_NAME + ">\n");
          sb.append("\n");
      }
      sb.append(XML_TAIL); 
      
      return sb.toString();
   }
   
   private Set<Integer> parseStatusComponentIDStrToSet(String unParsedcontrolIDs) {
      String[] parsedControlIDs = unParsedcontrolIDs.split(CONTROL_ID_SEPARATOR);
      Set<Integer> statusComponentIDs = new HashSet<Integer>();
     
      for (String statusConponentID : parsedControlIDs) {
         try {
            statusComponentIDs.add(Integer.parseInt(statusConponentID));
         } catch (NumberFormatException e) {
            throw new NoSuchComponentException("No such component whose id is :" + statusConponentID, e);
         }
      }
      
      return statusComponentIDs;
   }

   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }
   
}
