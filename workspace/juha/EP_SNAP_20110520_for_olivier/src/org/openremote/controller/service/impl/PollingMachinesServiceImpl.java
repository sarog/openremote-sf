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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.model.xml.SensorBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.config.ControllerXMLListenSharingData;
import org.openremote.controller.exception.ControllerXMLNotFoundException;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.service.PollingMachinesService;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.statuscache.PollingMachineThread;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.Logger;

/**
 * TODO :
 *
 *   Polling status from devices by sensor status command.
 *
 *   Deprecated and to be removed by ORCJAVA-101, ORCJAVA-115 and ORCJAVA-116
 *
 *   See ORCJAVA-117 -- http://jira.openremote.org/browse/ORCJAVA-117
 *
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Handy.Wang 2010-03-17
 *
 * @deprecated This class has no clear purpose, it is a mix of controller initialization,
 *             runtime deployment, and thread management. A typical ball-of-mud spaghetti
 *             monster from China.
 *
 *             For the parts related to deployment and initialization, see ORCJAVA-115
 *
 *             For the parts related to sensor thread management, see ORCJAVA-116, ORCJAVA-101
 *
 */
@Deprecated public class PollingMachinesServiceImpl implements PollingMachinesService {


  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for startup related events.
   */
  private final static Logger log = Logger.getLogger(Constants.INIT_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  private StatusCacheService statusCacheService;
  private RemoteActionXMLParser remoteActionXMLParser;
  private ControllerXMLListenSharingData controllerXMLListenSharingData;
  private SensorBuilder sensorBuilder;



  // Implements PollingMachinesService ------------------------------------------------------------

  @Override public void initStatusCacheWithControllerXML(Document doc)
  {
    List<Element> sensorElements = null;

    try
    {
//      if (document == null)
//      {
        sensorElements = remoteActionXMLParser.queryElementsFromXMLByName("sensor");
//      }
//
//      else
//      {
//        sensorElements = remoteActionXMLParser.queryElementsFromXMLByName(document, "sensor");
//      }
    }
    catch (ControllerXMLNotFoundException e)
    {
      log.info(
         "\n\n" +
         "********************************************************************************\n" +
         "\n" +
         " File controller.xml was not found in this OpenRemote Controller instance.      \n" +
         "\n" +
         " If you are starting the controller for the first time, please use your web     \n" +
         " browser to connect to the controller home page and synchronize it with your    \n" +
         " online account. \n" +
         "\n" +
         "********************************************************************************\n\n"
      );

      log.debug(e.getMessage(), e);

      // No controller.xml meeans no sensors to init, so just return back...

      return;
    }

    if (sensorElements != null)
    {
      for (Element sensorElement : sensorElements)
      {
        try
        {
          Sensor sensor = sensorBuilder.build(sensorElement);

          // Pull out a specific log category just to log the creation of sensor objects
          // in this method (happens at startup or soft restart)...

          Logger.getLogger(Constants.SENSOR_INIT_LOG_CATEGORY)
              .info("Created sensor : {0}", sensor.toString());
          
          controllerXMLListenSharingData.addSensor(sensor);

          statusCacheService.saveOrUpdateStatus(sensor.getSensorID(), StatusCommand.UNKNOWN_STATUS);
        }
        catch (NoSuchCommandException e)
        {
          // TODO : should be a checked exception so this doesn't accidentally propagate higher

          log.warn(
              "Unable to initialize sensor at startup due to configuration error: " +
              e.getMessage(), e
          );
        }
        catch (Throwable t)
        {
          // Catch all -- keep trying subsequent sensors...

          log.warn("Failed to initialize a sensor: " + t.getMessage(), t);
        }
      }
    }
  }


  @Override public void startPollingMachineMultiThread() throws InterruptedException
  {
    for (Sensor sensor : controllerXMLListenSharingData.getSensors())
    {
      if (sensor.isPolling())
      {
        PollingMachineThread pollingMachineThread = new PollingMachineThread(sensor, statusCacheService);
        pollingMachineThread.start();

        Thread.sleep(3);    // TODO : the nap makes no sense -- is it because thread synchronization has not been implemented ? [JPL]

        controllerXMLListenSharingData.addPollingMachineThread(pollingMachineThread);
      }

      if (sensor.isEventListener())
      {
        sensor.start();
      }
    }

    storeXMLContent(Constants.CONTROLLER_XML);
    storeXMLContent(Constants.PANEL_XML);
  }


   private void storeXMLContent(String xmlFileName)
   {
      String xmlFilePath = PathUtil.addSlashSuffix(ControllerConfiguration.readXML().getResourcePath()) + xmlFileName;
      File xmlFile = new File(xmlFilePath);
      try {
         StringBuffer fileContent = new StringBuffer(FileUtils.readFileToString(xmlFile, "utf-8"));
         if (Constants.CONTROLLER_XML.equals(xmlFileName)) {
            controllerXMLListenSharingData.setControllerXMLFileContent(fileContent);
         } else if (Constants.PANEL_XML.equals(xmlFileName)) {
            controllerXMLListenSharingData.setPanelXMLFileContent(fileContent);
         }
      } catch (IOException ioe) {
         log.info("Skipped " + xmlFileName + " change check, failed to read " + xmlFile.getAbsolutePath());
      }
   }
   
//   /** Sleep for several seconds */
//   private void nap(long sec) {
//      try {
//         Thread.sleep(sec);
//      } catch (InterruptedException e) {
//         Thread.currentThread().interrupt();
//      }
//   }

   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }

   public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
      this.remoteActionXMLParser = remoteActionXMLParser;
   }

   public void setControllerXMLListenSharingData(ControllerXMLListenSharingData controllerXMLListenSharingData) {
      this.controllerXMLListenSharingData = controllerXMLListenSharingData;
   }

   public void setSensorBuilder(SensorBuilder sensorBuilder) {
      this.sensorBuilder = sensorBuilder;
   }
   
}
