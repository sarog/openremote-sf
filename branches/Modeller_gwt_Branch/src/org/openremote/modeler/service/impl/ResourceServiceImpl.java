/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.UIButtonEvent;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.SensorOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIControl;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.XmlParserException;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.utils.FileUtilsExt;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.ProtocolEventContainer;
import org.openremote.modeler.utils.StringUtils;
import org.openremote.modeler.utils.UIComponentBox;
import org.openremote.modeler.utils.XmlParser;
import org.openremote.modeler.utils.ZipUtils;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * The Class ResourceServiceImpl.
 * 
 * @author Allen, Handy, Javen
 */
public class ResourceServiceImpl implements ResourceService {

   public static final String PANEL_XML_TEMPLATE = "panelXML.vm";
   public static final String CONTROLLER_XML_TEMPLATE = "controllerXML.vm";

   /** The Constant logger. */
   private static final Logger LOGGER = Logger.getLogger(ResourceServiceImpl.class);

   /** The configuration. */
   private Configuration configuration;

   /** The device command service. */
   private DeviceCommandService deviceCommandService;

   /** The event id. */
   private long eventId;

   /** The device macro service. */
   private DeviceMacroService deviceMacroService;

   private VelocityEngine velocity;

   private UIComponentBox uiComponentBox = new UIComponentBox();

   /**
    * {@inheritDoc}
    */
   public String downloadZipResource(long maxId, String sessionId, List<Panel> panels) {
      Set<Group> groups = new LinkedHashSet<Group>();
      Set<Screen> screens = new LinkedHashSet<Screen>();
      /*
       * initialize groups and screens.
       */
      initGroupsAndScreens(panels, groups, screens);

      String controllerXmlContent = getControllerXmlContent(maxId, screens);
      String panelXmlContent = getPanelXML(panels);
      String sectionIds = getSectionIds(screens);

      // replaceUrl(screens, sessionId);
      // String activitiesJson = getActivitiesJson(activities);

      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File sessionFolder = new File(pathConfig.userFolder(sessionId));
      if (!sessionFolder.exists()) {
         sessionFolder.mkdirs();
      }
      
      /*
       * down load the default image.
       */
      File defaultImage = new File(UIImage.DEFAULT_IMAGE_URL);
      FileUtilsExt.copyFile(defaultImage, new File(sessionFolder, defaultImage.getName()));
      
      File panelXMLFile = new File(pathConfig.panelXmlFilePath(sessionId));
      File controllerXMLFile = new File(pathConfig.controllerXmlFilePath(sessionId));
      File lircdFile = new File(pathConfig.lircFilePath(sessionId));
      // File dotImport = new File(pathConfig.dotImportFilePath(sessionId));

      /*
       * validate and output panel.xml.
       */
      String newIphoneXML = XmlParser.validateAndOutputXML(new File(getClass().getResource(
            configuration.getPanelXsdPath()).getPath()), panelXmlContent, sessionFolder);
      controllerXmlContent = XmlParser.validateAndOutputXML(new File(getClass().getResource(
            configuration.getControllerXsdPath()).getPath()), controllerXmlContent);
      /*
       * validate and output controller.xml
       */
      try {
         FileUtilsExt.deleteQuietly(panelXMLFile);
         FileUtilsExt.deleteQuietly(controllerXMLFile);
         FileUtilsExt.deleteQuietly(lircdFile);
         // FileUtilsExt.deleteQuietly(dotImport);

         FileUtilsExt.writeStringToFile(panelXMLFile, newIphoneXML);
         FileUtilsExt.writeStringToFile(controllerXMLFile, controllerXmlContent);
         // FileUtilsExt.writeStringToFile(dotImport, activitiesJson);

         if (sectionIds != "") {
            FileUtils
                  .copyURLToFile(buildLircRESTUrl(configuration.getBeehiveLircdConfRESTUrl(), sectionIds), lircdFile);
         }
         if (lircdFile.exists() && lircdFile.length() == 0) {
            lircdFile.delete();
         }
      } catch (IOException e) {
         LOGGER.error("Compress zip file occur IOException", e);
         throw new FileOperationException("Compress zip file occur IOException", e);
      }

      File zipFile = compressFilesToZip(sessionFolder.listFiles(), pathConfig.openremoteZipFilePath(sessionId));
      return pathConfig.getZipUrl(sessionId) + zipFile.getName();

   }

   /**
    * Replace url.
    * 
    * @param activities
    *           the activities
    * @param sessionId
    *           the user id
    */
   /*
    * private void replaceUrl(Collection<Screen> screens, String sessionId) { String rerlativeSessionFolderPath =
    * PathConfig.getInstance(configuration).getRelativeSessionFolderPath(sessionId); for (Screen screen : screens) { for
    * (Absolute absolute : screen.getAbsolutes()) {
    * absolute.getUIComponent().transImagePathToRelative(rerlativeSessionFolderPath); } for (UIGrid grid :
    * screen.getGrids()) { for (Cell cell : grid.getCells()) {
    * cell.getUIComponent().transImagePathToRelative(rerlativeSessionFolderPath); } } } }
    */
   /**
    * Compress files to zip.
    * 
    * @param files
    *           the files
    * @param zipFilePath
    *           the zip file path
    * 
    * @return the file
    */
   private File compressFilesToZip(File[] files, String zipFilePath) {
      List<File> compressedfiles = new ArrayList<File>();
      for (File file : files) {
         if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("zip")) {
            continue;
         }
         compressedfiles.add(file);
      }

      File zipFile = new File(zipFilePath);
      ZipUtils.compress(zipFile.getAbsolutePath(), compressedfiles);
      return zipFile;
   }

   /**
    * Builds the lirc rest url.
    * 
    * @param restAPIUrl
    *           the rESTAPI url
    * @param ids
    *           the ids
    * 
    * @return the uRL
    */
   private URL buildLircRESTUrl(String restAPIUrl, String ids) {
      URL lircUrl;
      try {
         lircUrl = new URL(restAPIUrl + "?ids=" + ids);
      } catch (MalformedURLException e) {
         LOGGER.error("Lirc file url is invalid", e);
         throw new IllegalArgumentException("Lirc file url is invalid", e);
      }
      return lircUrl;
   }

   /**
    * {@inheritDoc}
    */
   public String getDotImportFileForRender(String sessionId, InputStream inputStream) {
      File tmpDir = new File(PathConfig.getInstance(configuration).userFolder(sessionId));
      if (tmpDir.exists() && tmpDir.isDirectory()) {
         try {
            FileUtils.deleteDirectory(tmpDir);
         } catch (IOException e) {
            LOGGER.error("Delete temp dir Occur IOException", e);
            throw new FileOperationException("Delete temp dir Occur IOException", e);
         }
      }
      new File(PathConfig.getInstance(configuration).userFolder(sessionId)).mkdirs();
      String dotImportFileContent = "";
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry;
      FileOutputStream fileOutputStream = null;
      try {
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
               if (Constants.PANEL_DESC_FILE.equalsIgnoreCase(StringUtils.getFileExt(zipEntry.getName()))) {
                  dotImportFileContent = IOUtils.toString(zipInputStream);
               }
               if (!checkXML(zipInputStream, zipEntry, "iphone")) {
                  throw new XmlParserException("The iphone.xml schema validation fail, please check it");
               } else if (!checkXML(zipInputStream, zipEntry, "controller")) {
                  throw new XmlParserException("The controller.xml schema validation fail, please check it");
               }

               if (!FilenameUtils.getExtension(zipEntry.getName()).matches("(xml|import|conf)")) {
                  File file = new File(PathConfig.getInstance(configuration).userFolder(sessionId) + zipEntry.getName());
                  FileUtils.touch(file);

                  fileOutputStream = new FileOutputStream(file);
                  int b;
                  while ((b = zipInputStream.read()) != -1) {
                     fileOutputStream.write(b);
                  }
                  fileOutputStream.close();
               }
            }

         }
      } catch (IOException e) {
         LOGGER.error("Get import file from zip file Occur IOException", e);
         throw new FileOperationException("Get import file from zip file Occur IOException", e);
      } finally {
         try {
            zipInputStream.closeEntry();
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            LOGGER.error("Clean Resource used occured IOException when import a file", e);
         }

      }
      return dotImportFileContent;
   }

   /**
    * Check xml.
    * 
    * @param zipInputStream
    *           the zip input stream
    * @param zipEntry
    *           the zip entry
    * @param xmlName
    *           the xml name
    * 
    * @return true, if successful
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    */
   private boolean checkXML(ZipInputStream zipInputStream, ZipEntry zipEntry, String xmlName) throws IOException {
      if (zipEntry.getName().equals(xmlName + ".xml")) {
         String xsdRelativePath = "iphone".equals(xmlName) ? configuration.getPanelXsdPath() : configuration
               .getControllerXsdPath();
         String xsdPath = getClass().getResource(xsdRelativePath).getPath();
         if (!XmlParser.checkXmlSchema(xsdPath, IOUtils.toString(zipInputStream))) {
            return false;
         }
      }
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public File uploadImage(InputStream inputStream, String fileName, String sessionId) {
      File file = new File(PathConfig.getInstance(configuration).userFolder(sessionId) + File.separator + fileName);
      FileOutputStream fileOutputStream = null;
      try {
         File dir = file.getParentFile();
         if (!dir.exists()) {
            dir.mkdirs();
         }
         FileUtils.touch(file);
         fileOutputStream = new FileOutputStream(file);
         IOUtils.copy(inputStream, fileOutputStream);
      } catch (IOException e) {
         LOGGER.error("Save uploaded image to file occur IOException.", e);
         throw new FileOperationException("Save uploaded image to file occur IOException.", e);
      } finally {
         try {
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            LOGGER.error("Close FileOutputStream Occur IOException while save a uploaded image.", e);
         }

      }
      return file;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#getControllerXmlContent(java.util.List)
    */
   /**
    * Gets the controller xml content.
    * 
    * @param maxId
    *           the max id
    * @param screens
    *           the activity list
    * 
    * @return the controller xml content
    */
   private String getControllerXmlContent(long maxId, Collection<Screen> screens) {
      this.eventId = maxId + 1;
      return getControllerXML(screens);
   }

   /**
    * Gets the controller xml segment content.
    * 
    * @param command
    *           the device command item
    * @param protocolEventContaine
    *           the protocol event container
    * 
    * @return the controller xml segment content
    */
   public List<UIButtonEvent> getEventsOfDeviceMacroItem(UICommand command,
         ProtocolEventContainer protocolEventContainer) {
      List<UIButtonEvent> oneUIButtonEventList = new ArrayList<UIButtonEvent>();
      if (command instanceof DeviceMacroItem) {
         if (command instanceof DeviceCommandRef) {
            DeviceCommand deviceCommand = deviceCommandService.loadById(((DeviceCommandRef) command).getDeviceCommand()
                  .getOid());
            addDeviceCommandEvent(protocolEventContainer, oneUIButtonEventList, deviceCommand);
         } else if (command instanceof DeviceMacroRef) {
            DeviceMacro deviceMacro = ((DeviceMacroRef) command).getTargetDeviceMacro();
            deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
            for (DeviceMacroItem tempDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
               oneUIButtonEventList.addAll(getEventsOfDeviceMacroItem(tempDeviceMacroItem, protocolEventContainer));
            }
         } else if (command instanceof CommandDelay) {
            CommandDelay delay = (CommandDelay) command;
            UIButtonEvent uiButtonEvent = new UIButtonEvent();
            uiButtonEvent.setId(this.eventId++);
            uiButtonEvent.setDelay(delay.getDelaySecond());
            oneUIButtonEventList.add(uiButtonEvent);
         }
      } else if (command instanceof CommandRefItem) {
         DeviceCommand deviceCommand = deviceCommandService.loadById(((CommandRefItem) command).getDeviceCommand()
               .getOid());
         addDeviceCommandEvent(protocolEventContainer, oneUIButtonEventList, deviceCommand);
      } else {
         return new ArrayList<UIButtonEvent>();
      }
      return oneUIButtonEventList;
   }

   /**
    * @param protocolEventContainer
    * @param oneUIButtonEventList
    * @param deviceCommand
    */
   private void addDeviceCommandEvent(ProtocolEventContainer protocolEventContainer,
         List<UIButtonEvent> oneUIButtonEventList, DeviceCommand deviceCommand) {
      String protocolType = deviceCommand.getProtocol().getType();
      List<ProtocolAttr> protocolAttrs = deviceCommand.getProtocol().getAttributes();

      UIButtonEvent uiButtonEvent = new UIButtonEvent();
      uiButtonEvent.setId(this.eventId++);
      uiButtonEvent.setProtocolDisplayName(protocolType);
      for (ProtocolAttr protocolAttr : protocolAttrs) {
         uiButtonEvent.getProtocolAttrs().put(protocolAttr.getName(), protocolAttr.getValue());
      }
      uiButtonEvent.setLabel(deviceCommand.getName());
      protocolEventContainer.addUIButtonEvent(uiButtonEvent);
      oneUIButtonEventList.add(uiButtonEvent);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#getSectionIds()
    */
   /**
    * Gets the section ids.
    * 
    * @param screenList
    *           the activity list
    * 
    * @return the section ids
    */
   private String getSectionIds(Collection<Screen> screenList) {
      Set<String> sectionIds = new HashSet<String>();
      for (Screen screen : screenList) {
         for (Absolute absolute : screen.getAbsolutes()) {
            if (absolute.getUIComponent() instanceof UIControl) {
               for (UICommand command : ((UIControl) absolute.getUIComponent()).getCommands()) {
                  addSectionIds(sectionIds, command);
               }
            }
         }
         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               if (cell.getUIComponent() instanceof UIControl) {
                  for (UICommand command : ((UIControl) cell.getUIComponent()).getCommands()) {
                     addSectionIds(sectionIds, command);
                  }
               }
            }
         }
      }

      StringBuffer sectionIdsSB = new StringBuffer();
      int i = 0;
      for (String sectionId : sectionIds) {
         sectionIdsSB.append(sectionId);
         if (i < sectionIds.size() - 1) {
            sectionIdsSB.append(",");
         }
         i++;
      }
      return sectionIdsSB.toString();
   }

   /**
    * @param sectionIds
    * @param command
    */
   private void addSectionIds(Set<String> sectionIds, UICommand command) {
      if (command instanceof DeviceMacroItem) {
         sectionIds.addAll(getDevcieMacroItemSectionIds((DeviceMacroItem) command));
      } else if (command instanceof CommandRefItem) {
         sectionIds.add(((CommandRefItem) command).getDeviceCommand().getSectionId());
      }
   }

   /**
    * Gets the devcie macro item section ids.
    * 
    * @param deviceMacroItem
    *           the device macro item
    * 
    * @return the devcie macro item section ids
    */
   private Set<String> getDevcieMacroItemSectionIds(DeviceMacroItem deviceMacroItem) {
      Set<String> deviceMacroRefSectionIds = new HashSet<String>();
      if (deviceMacroItem instanceof DeviceCommandRef) {
         deviceMacroRefSectionIds.add(((DeviceCommandRef) deviceMacroItem).getDeviceCommand().getSectionId());
      } else if (deviceMacroItem instanceof DeviceMacroRef) {
         DeviceMacro deviceMacro = ((DeviceMacroRef) deviceMacroItem).getTargetDeviceMacro();
         deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
         for (DeviceMacroItem nextDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
            deviceMacroRefSectionIds.addAll(getDevcieMacroItemSectionIds(nextDeviceMacroItem));
         }
      }
      return deviceMacroRefSectionIds;
   }

   /**
    * Gets the configuration.
    * 
    * @return the configuration
    */
   public Configuration getConfiguration() {
      return configuration;
   }

   /**
    * Sets the configuration.
    * 
    * @param configuration
    *           the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * Gets the device command service.
    * 
    * @return the device command service
    */
   public DeviceCommandService getDeviceCommandService() {
      return deviceCommandService;
   }

   /**
    * Sets the device command service.
    * 
    * @param deviceCommandService
    *           the new device command service
    */
   public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
      this.deviceCommandService = deviceCommandService;
   }

   /**
    * Sets the device macro service.
    * 
    * @param deviceMacroService
    *           the new device macro service
    */
   public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
      this.deviceMacroService = deviceMacroService;
   }

   /**
    * {@inheritDoc}
    */
   public String getRelativeResourcePath(String sessionId, String fileName) {
      return PathConfig.getInstance(configuration).getRelativeResourcePath(fileName, sessionId);
   }

   public long getEventId() {
      return eventId;
   }

   public void setEventId(long eventId) {
      this.eventId = eventId;
   }

   @Override
   public String getGroupsJson(Collection<Group> groups) {
      try {
         String[] includedPropertyNames = {"screenRefs"};
         String[] excludePropertyNames = {};
         String groupsJson = JsonGenerator.serializerObjectInclude(groups, includedPropertyNames, excludePropertyNames);
         return groupsJson;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   @Override
   public String getScreensJson(Collection<Screen> screens) {
      try {
         String[] includedPropertyNames = {"absolutes", "absolutes.uiComponent", "grid", "grid.cells",
               "grid.cells.uiComponent"};
         String[] excludePropertyNames = {"absolutes.uiComponent.panelXml", "grid.cells.uiComponent.panelXml"};
         String groupsJson = JsonGenerator
               .serializerObjectInclude(screens, includedPropertyNames, excludePropertyNames);
         return groupsJson;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   @Override
   public String getPanelsJson(Collection<Panel> panels) {
      try {
         String[] includedPropertyNames = {"groupRefs", "tabbarItems", "groupRefs.group.tabbarItems",
               "groupRefs.group.screenRefs", "groupRefs.group.screenRefs.screen.absolutes.uiComponent",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent.uiCommand",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent.commands",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.uiCommand",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.commands",
               "groupRefs.group.screenRefs.screen.grids.uiComponent.sensor",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.sensor" };
         String[] excludePropertyNames = {};
         String panelsJson = JsonGenerator.serializerObjectInclude(panels, includedPropertyNames, excludePropertyNames);
         return panelsJson;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   public String getPanelXML(Collection<Panel> panels) {
      /*
       * init groups and screens.
       */
      Set<Group> groups = new LinkedHashSet<Group>();
      Set<Screen> screens = new LinkedHashSet<Screen>();
      initGroupsAndScreens(panels, groups, screens);

      try {
         Map<String, Object> context = new HashMap<String, Object>();
         context.put("panels", panels);
         context.put("groups", groups);
         context.put("screens", screens);
         return VelocityEngineUtils.mergeTemplateIntoString(velocity, PANEL_XML_TEMPLATE, context);
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("faild when get panel.xml by template", e);
      }

   }

   public VelocityEngine getVelocity() {
      return velocity;
   }

   public void setVelocity(VelocityEngine velocity) {
      this.velocity = velocity;
   }

   public String getControllerXML(Collection<Screen> screens) {
      /*
       * initialize UI component box.
       */
      initUIComponentBox(screens);
      Map<String, Object> context = new HashMap<String, Object>();
      ProtocolEventContainer eventContainer = new ProtocolEventContainer();
      ProtocolContainer protocolContainer = ProtocolContainer.getInstance();
      Collection<Sensor> sensors = getAllSensorWithoutDuplicate(screens);
      Collection<UIComponent> switchs = uiComponentBox.getUIComponentsByType(UISwitch.class);
      Collection<UIComponent> buttons = uiComponentBox.getUIComponentsByType(UIButton.class);
      Collection<UIComponent> gestures = uiComponentBox.getUIComponentsByType(Gesture.class);
      Collection<UIComponent> uiSliders = uiComponentBox.getUIComponentsByType(UISlider.class);
      Collection<UIComponent> uiImages = uiComponentBox.getUIComponentsByType(UIImage.class);
      Collection<UIComponent> uiLabels = uiComponentBox.getUIComponentsByType(UILabel.class);
      context.put("switchs", switchs);
      context.put("buttons", buttons);
      context.put("screens", screens);
      context.put("eventContainer", eventContainer);
      context.put("xmlParser", this);
      context.put("protocolContainer", protocolContainer);
      context.put("sensors", sensors);
      context.put("gestures", gestures);
      context.put("uiSliders", uiSliders);
      context.put("labels", uiLabels);
      context.put("images", uiImages);

      return VelocityEngineUtils.mergeTemplateIntoString(velocity, CONTROLLER_XML_TEMPLATE, context);
   }

   private void initGroupsAndScreens(Collection<Panel> panels, Set<Group> groups, Set<Screen> screens) {
      for (Panel panel : panels) {
         List<GroupRef> groupRefs = panel.getGroupRefs();
         for (GroupRef groupRef : groupRefs) {
            groups.add(groupRef.getGroup());
         }
      }

      for (Group group : groups) {
         List<ScreenRef> screenRefs = group.getScreenRefs();
         for (ScreenRef screenRef : screenRefs) {
            screens.add(screenRef.getScreen());
         }
      }
   }

   private Set<Sensor> getAllSensorWithoutDuplicate(Collection<Screen> screens) {
      Set<Sensor> SensorWithoutDuplicate = new HashSet<Sensor>();
      Collection<Sensor> allSensors = new ArrayList<Sensor>();

      for (Screen screen : screens) {
         for (Absolute absolute : screen.getAbsolutes()) {
            UIComponent component = absolute.getUIComponent();
            initSensors(allSensors, SensorWithoutDuplicate, component);
         }

         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               initSensors(allSensors, SensorWithoutDuplicate, cell.getUIComponent());
            }
         }
      }

      /*
       * reset sensor oid, avoid reduplicate id in export xml.
       */

      for (Sensor sensor : SensorWithoutDuplicate) {
         long sensorOid = sensor.getOid();
         long currentSensorId = eventId++;
         for (Sensor s : allSensors) {
            if (s.getOid() == sensorOid) {
               s.setOid(currentSensorId);
            }
         }
      }
      return SensorWithoutDuplicate;
   }

   private void initSensors(Collection<Sensor> allSensors, Set<Sensor> sensorsWithDuplicate,UIComponent component) {
      if (component instanceof SensorOwner) {
         SensorOwner sensorOwner = (SensorOwner) component;
         if (sensorOwner.getSensor() != null) {
            allSensors.add(sensorOwner.getSensor());
            sensorsWithDuplicate.add(sensorOwner.getSensor());
         }
      }
   }

   private void initUIComponentBox(Collection<Screen> screens) {
      for (Screen screen : screens) {
         for (Absolute absolute : screen.getAbsolutes()) {
            UIComponent component = absolute.getUIComponent();
            uiComponentBox.add(component);
         }

         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               uiComponentBox.add(cell.getUIComponent());
            }
         }

         for (Gesture gesture : screen.getGestures()) {
            uiComponentBox.add(gesture);
         }
      }
   }
}
