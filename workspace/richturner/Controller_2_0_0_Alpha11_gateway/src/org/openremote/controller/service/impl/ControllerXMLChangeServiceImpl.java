/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.service.GatewayManagerService;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.service.ControllerXMLChangeService;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;

/**
 * Controller.xml monitoring service.
 * 
 * @author handy.wang 2010-03-19
 *
 */
public class ControllerXMLChangeServiceImpl implements ControllerXMLChangeService {

   private GatewayManagerService gatewayManagerService;
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   boolean controllerXMLChanged = false;
   
   @SuppressWarnings("finally")
   @Override
   public synchronized boolean refreshController() {
      boolean success = true;
      if (!isObservedXMLContentChanged(Constants.CONTROLLER_XML) && !isObservedXMLContentChanged(Constants.PANEL_XML)) {
         return success;
      }
      logger.info("Controller.xml of Controller changed, refreshing controller.xml");
      this.controllerXMLChanged = true;
      try {
         gatewayManagerService.restartGateways();
      } catch (ControllerException e) {
         logger.error("Error occured while refreshing controller.", e);
         success = false;
      }
      this.controllerXMLChanged = false;
      String isSuccessInfo = success ? " success " : " failed ";
      logger.info("Finished refreshing controller.xml" + isSuccessInfo);
      return success;
   }
   
   private boolean isObservedXMLContentChanged(String observedXMLFileName) {
      //if changed, save the latest controller.xml.
      String observedXMLFilePath = PathUtil.addSlashSuffix(ConfigFactory.getCustomBasicConfigFromDefaultControllerXML().getResourcePath()) + observedXMLFileName;
      File observedXMLFile = new File(observedXMLFilePath);
      StringBuffer fileContent = new StringBuffer();
      String oldXMLFileContent = new String();
      if (Constants.CONTROLLER_XML.equals(observedXMLFileName)) {
         oldXMLFileContent = gatewayManagerService.getControllerXMLFileContent();
      } else if (Constants.PANEL_XML.equals(observedXMLFileName)) {
         oldXMLFileContent = gatewayManagerService.getPanelXMLFileContent();
      }
      try {
         fileContent.append(FileUtils.readFileToString(observedXMLFile, "utf-8"));
      } catch (IOException ioe) {
         logger.warn("Skipped " + observedXMLFileName + " change check, Failed to read " + observedXMLFile.getAbsolutePath());
         return false;
      }
      if (oldXMLFileContent.equals(fileContent.toString()) || oldXMLFileContent.length() == 0) {
         return false;
      }
      return true;
   }
   
   public boolean isControllerXMLChanged() {
      return this.controllerXMLChanged;
   }
   
   public void setGatewayManagerService(GatewayManagerService gatewayManagerService) {
      this.gatewayManagerService = gatewayManagerService;
   }
}
