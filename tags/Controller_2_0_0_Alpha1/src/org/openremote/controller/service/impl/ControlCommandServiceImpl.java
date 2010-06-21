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

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.component.ComponentFactory;
import org.openremote.controller.component.Sensory;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.utils.MacrosIrDelayUtil;


/**
 * The implementation for ControlCommandService class.
 * 
 * @author Handy.Wang
 */
public class ControlCommandServiceImpl implements ControlCommandService {

   /** The remote action xml parser. */
   private RemoteActionXMLParser remoteActionXMLParser;
   
   private ComponentFactory componentFactory;
   
   private StatusCacheService statusCacheService;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   /**
    * {@inheritDoc}
    */
   public void trigger(String controlID, String commandParam) {
      
      Control control = getControl(controlID, commandParam);
      List<ExecutableCommand> executableCommands = control.getExecutableCommands();
      MacrosIrDelayUtil.ensureDelayForIrCommand(executableCommands);
      for (ExecutableCommand executableCommand : executableCommands) {
         executableCommand.send();
      }
      if (control instanceof Sensory) {
         logger.info("Begin updating statuscache after sending command to device.");
         int sensorID = ((Sensory)control).fetchSensorID();
         statusCacheService.saveOrUpdateStatus(sensorID, commandParam.toUpperCase());
         logger.info("Finish updating statuscache after sending command to device.");
      }
   }
   
   private Control getControl(String controlID, String commandParam) {
      Element controlElement = remoteActionXMLParser.queryElementFromXMLById(controlID);
      if (controlElement == null) {
         throw new NoSuchComponentException("No such component id :" + controlID);
      }
      return (Control) componentFactory.getComponent(controlElement, commandParam);
   }
   
   /**
    * Sets the remote action xml parser.
    * 
    * @param remoteActionXMLParser the new remote action xml parser
    */
   public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
      this.remoteActionXMLParser = remoteActionXMLParser;
   }

   public void setComponentFactory(ComponentFactory componentFactory) {
      this.componentFactory = componentFactory;
   }

   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }
   
   
}
