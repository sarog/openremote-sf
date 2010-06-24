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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.PollingMachinesService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.statuscache.PollingMachineThread;

/**
 * 
 * @author Handy.Wang 2010-03-17
 *
 */
public class PollingMachinesServiceImpl implements PollingMachinesService {
   private StatusCacheService statusCacheService;
   private RemoteActionXMLParser remoteActionXMLParser;
   private CommandFactory commandFactory;
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void initStatusCacheWithControllerXML(Document document, Map<String, StatusCommand> sensorIdAndStatusCommandsMap) {
      List<Element> sensorElements = null;
      if (document == null) {
         sensorElements = remoteActionXMLParser.queryElementsFromXMLByName("sensor");
      } else {
         sensorElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "sensor");
      }
      for (Element sensorElement : sensorElements) {
        String sensorID = sensorElement.getAttributeValue("id");
        sensorIdAndStatusCommandsMap.put(sensorID, getStatusCommand(document, sensorID));
        statusCacheService.saveOrUpdateStatus(Integer.parseInt(sensorID), "noStatus");
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startPollingMachineMultiThread(Map<String, StatusCommand> sensorIdAndStatusCommandsMap) {
      Set<String> sensorIDs = sensorIdAndStatusCommandsMap.keySet();
      for (String sensorID : sensorIDs) {
         StatusCommand statusCommand = sensorIdAndStatusCommandsMap.get(sensorID);
         Thread pollingMachineThread = new Thread(new PollingMachineThread(sensorID, statusCommand, statusCacheService));
         pollingMachineThread.start();
         nap(3);
      }
   }
   
   @SuppressWarnings("unchecked")
   private StatusCommand getStatusCommand(Document document, String sensorID) {
      Element sensorElement = null;
      if (document == null) {
         sensorElement = remoteActionXMLParser.queryElementFromXMLById(sensorID);   
      } else {
         sensorElement = remoteActionXMLParser.queryElementFromXMLById(document, sensorID);
      }
      
      if (sensorElement == null) {
         throw new NoSuchComponentException("Cannot find that sensor with id = " + sensorID);
      }
      
      List<Element>childrenOfSensor = sensorElement.getChildren();
      String commandElementId = "";
      for (Element childOfSensor:childrenOfSensor) {
        if ("include".equalsIgnoreCase(childOfSensor.getName()) && "command".equalsIgnoreCase(childOfSensor.getAttributeValue("type"))) {
           commandElementId = childOfSensor.getAttributeValue("ref");
        }
      }
      if ("".equals(commandElementId)) {
        return new NoStatusCommand();
      }
      Element commandElement = null;
      if (document == null) {
         commandElement = remoteActionXMLParser.queryElementFromXMLById(commandElementId);
      } else {
         commandElement = remoteActionXMLParser.queryElementFromXMLById(document, commandElementId);
      }
      StatusCommand statusCommand = (StatusCommand) commandFactory.getCommand(commandElement);
      return statusCommand;
   }
   
   /** Sleep for several seconds */
   private void nap(long sec) {
      try {
         Thread.sleep(sec);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }

   public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
      this.remoteActionXMLParser = remoteActionXMLParser;
   }

   public void setCommandFactory(CommandFactory commandFactory) {
      this.commandFactory = commandFactory;
   }
}
