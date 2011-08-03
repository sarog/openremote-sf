/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.xml.SensorBuilder;
import org.openremote.controller.config.ControllerXMLListenSharingData;
import org.openremote.controller.exception.ControllerException;
import org.openremote.controller.service.ControllerXMLChangeService;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.Logger;

/**
 * TODO : this needs a complete refactoring, see ORCJAVA-115
 * 
 * Controller.xml monitoring service.
 * 
 * @author handy.wang 2010-03-19
 *
 */
public class ControllerXMLChangeServiceImpl implements ControllerXMLChangeService
{
//
//  private static Logger logger = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);
//
//
//  private ControllerXMLListenSharingData controllerXMLListenSharingData;
//  private RemoteActionXMLParser controllerXMLParser;
//  private StatusCache deviceStateCache;
//  private ChangedStatusTable changedStatusTable;
//  private SensorBuilder sensorBuilder;
//
//  private Deployer deployer;
//
//
//  @Override public synchronized boolean refreshController()
//  {
//
//
//    if (!isObservedXMLContentChanged(Constants.CONTROLLER_XML) && !isObservedXMLContentChanged(Constants.PANEL_XML))
//    {
//       return true;
//    }
//
//    logger.info("Controller.xml of Controller changed, refreshing controller.xml");
//
//    //deployer.setIsControllerXMLChanged(true);
//
//    try
//    {
////       deployer.stopSensors();
////
////       deviceStateCache.clearChangedStatuses();
////
////       deployer.clearDeviceStateCache();
//
//       deployer.softRestart();
//
//       //deviceStateCache.startSensors();
//
//       return true;
//    }
//
//    catch (Throwable t)
//    {
//      logger.error("Error occured while refreshing controller.", t); // TODO
//
//      return false;
//    }
//
//  }
//
//
//  private boolean isObservedXMLContentChanged(String observedXMLFileName)
//  {
//    //if changed, save the latest controller.xml.
//    String observedXMLFilePath = PathUtil.addSlashSuffix(ControllerConfiguration.readXML().getResourcePath()) + observedXMLFileName;
//    File observedXMLFile = new File(observedXMLFilePath);
//    StringBuffer fileContent = new StringBuffer();
//    String oldXMLFileContent = new String();
//
//    if (Constants.CONTROLLER_XML.equals(observedXMLFileName))
//    {
//      oldXMLFileContent = controllerXMLListenSharingData.getControllerXMLFileContent();
//    }
//
//    else if (Constants.PANEL_XML.equals(observedXMLFileName))
//    {
//      oldXMLFileContent = controllerXMLListenSharingData.getPanelXMLFileContent();
//    }
//
//    try
//    {
//      fileContent.append(FileUtils.readFileToString(observedXMLFile, Constants.CHARACTER_ENCODING_UTF8));
//    }
//
//    catch (IOException ioe)
//    {
//      logger.warn("Skipped " + observedXMLFileName + " change check, Failed to read " + observedXMLFile.getAbsolutePath());
//      return false;
//    }
//
//    if (oldXMLFileContent.equals(fileContent.toString()))
//    {
//      return false;
//    }
//
//    if (Constants.CONTROLLER_XML.equals(observedXMLFileName))
//    {
//      controllerXMLListenSharingData.setControllerXMLFileContent(fileContent);
//    }
//
//    else if (Constants.PANEL_XML.equals(observedXMLFileName))
//    {
//      controllerXMLListenSharingData.setPanelXMLFileContent(fileContent);
//    }
//
//    return true;
//  }
//
//  private void stopSensors()
//  {
//    List<PollingMachineThread> pollingThreads = controllerXMLListenSharingData.getPollingMachineThreads();
//
//    for (PollingMachineThread pollingThread : pollingThreads)
//    {
//       pollingThread.kill();
//    }
//
//    nap(10); // Just give the theads some times to get the timeslice and really stop.
//
//    Iterator<Sensor> iterator = ServiceContext.getDeviceStateCache().listSensors();
//
//    while (iterator.hasNext())
//    {
//      Sensor sensor = iterator.next();
//
//      sensor.stop();
//    }
//
//    pollingThreads.clear();
//  }
//
//  private void clearChangedStatusTable()
//  {
//    Iterator<Sensor> iterator = deviceStateCache.listSensors();
//
//    while (iterator.hasNext())
//    {
//      Sensor sensor = iterator.next();
//
//      // Just wake up all the records, acturelly, the status didn't change.
//      changedStatusTable.updateStatusChangedIDs(sensor.getSensorID());
//    }
//
//    changedStatusTable.clearAllRecords();
//  }
//
//  private void clearStatusCache()
//  {
//    deviceStateCache.clearAllStatusCache();
//  }
   
//  private void clearAndReloadSensors()
//  {
//    deviceStateCache.unregisterAllSensors();
//
//    // re-parse sensors and their included statuscommand...
//
//    Element sensorsElement = controllerXMLParser.queryElementFromXMLByName(Constants.SENSORS_ELEMENT_NAME);
//
//    if (sensorsElement == null)
//    {
//      return;
//      //throw new NoSuchComponentException("DOM element " + Constants.SENSORS_ELEMENT_NAME + " doesn't exist in " + Constants.CONTROLLER_XML);
//    }
//
//    List<Element> sensorElements = sensorsElement.getChildren();
//
//    if (sensorElements == null)
//    {
//      return;
//      //throw new InitializationException(MessageFormat.format(
//      //    "The <{0}> element in {1} is empty.",
//      //    Constants.SENSORS_ELEMENT_NAME, Constants.CONTROLLER_XML
//      //));
//    }
//
//    Iterator<Element> sensorElementIterator = sensorElements.iterator();
//
//    while (sensorElementIterator.hasNext())
//    {
//      try
//      {
//        Element sensorElement = sensorElementIterator.next();
//        Sensor sensor = sensorBuilder.build(sensorElement);
//
//        deviceStateCache.register_Sensor(sensor); // TODO : in sensor constr.
//      }
//
//      catch (Throwable t)
//      {
//        // If building the sensor fails for any reason, log it and skip it...
//
//        // TODO :
//        //   - at this point it is likely the component will fail in this case, unless and
//        //     until its error handling is made more robust
//
//        logger.error("Creating sensor failed : " + t.getMessage(), t);
//      }
//    }
//  }
//
//
//
//  private void restartPollingSensorThreads()
//  {
//    Iterator<Sensor> sensorIterator = deviceStateCache.listSensors();
//
//    while (sensorIterator.hasNext())
//    {
//      Sensor sensor = sensorIterator.next();
//
//      sensor.stop();
//
//      sensor.start();
//
//    }
//  }
//
//
//  // Service Dependencies -------------------------------------------------------------------------
//
//  public void setControllerXMLListenSharingData(ControllerXMLListenSharingData controllerXMLListenSharingData)
//  {
//    this.controllerXMLListenSharingData = controllerXMLListenSharingData;
//  }
//
//  public void setRemoteActionXMLParser(RemoteActionXMLParser controllerXMLParser)
//  {
//    this.controllerXMLParser = controllerXMLParser;
//  }
//
//  public void setDeployer(Deployer deployer)
//  {
//    this.deployer = deployer;
//  }
//
//  public void setCache(StatusCache deviceStateCache)
//  {
//    this.deviceStateCache = deviceStateCache;
//  }
//
//  public void setChangedStatusTable(ChangedStatusTable changedStatusTable)
//  {
//    this.changedStatusTable = changedStatusTable;
//  }
//
//  public void setSensorBuilder(SensorBuilder sensorBuilder)
//  {
//    this.sensorBuilder = sensorBuilder;
//  }


}

