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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.Command;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.Config;
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
import org.openremote.modeler.domain.Template;
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
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.FileUtilsExt;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.ProtocolCommandContainer;
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
//   private long eventId;

   /** The device macro service. */
   private DeviceMacroService deviceMacroService;

   private VelocityEngine velocity;
   
   private UserService userService;

   private ControllerConfigService controllerConfigService = null;
   /**
    * {@inheritDoc}
    */
   public String downloadZipResource(long maxOid, String sessionId, List<Panel> panels) {
      updateResources(panels, maxOid);
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File zipFile = this.getExportResource();
      return pathConfig.getZipUrl(userService.getAccount())+zipFile.getName();

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

   private File compressFilesToZip(File[] files, String zipFilePath,List<String>ignoreExtentions) {
      List<File> compressedfiles = new ArrayList<File>();
      for (File file : files) {
         
         if (file==null || ignoreExtentions.contains(FilenameUtils.getExtension(file.getName()))) {
            continue;
         }
         compressedfiles.add(file);
      }

      File zipFile = new File(zipFilePath);
      FileUtilsExt.deleteQuietly(zipFile);
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
   @SuppressWarnings("unused")
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
      return uploadFile(inputStream, file);
   }
   
   public File uploadImage(InputStream inputStream, String fileName) {
      File file = new File(PathConfig.getInstance(configuration).userFolder(userService.getAccount()) + File.separator
            + fileName);
      return uploadFile(inputStream, file);
   }
   
   private File uploadFile(InputStream inputStream, File file) {
//      File file = new File(PathConfig.getInstance(configuration).userFolder(prefix) + File.separator + fileName);
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
    * Gets the controller xml segment content.
    * 
    * @param command
    *           the device command item
    * @param protocolEventContaine
    *           the protocol event container
    * 
    * @return the controller xml segment content
    */
   public List<Command> getCommandOwnerByUICommand(UICommand command,
         ProtocolCommandContainer protocolEventContainer,MaxId  maxId) {
      List<Command> oneUIButtonEventList = new ArrayList<Command>();
      if (command instanceof DeviceMacroItem) {
         if (command instanceof DeviceCommandRef) {
            DeviceCommand deviceCommand = deviceCommandService.loadById(((DeviceCommandRef) command).getDeviceCommand()
                  .getOid());
            addDeviceCommandEvent(protocolEventContainer, oneUIButtonEventList, deviceCommand,maxId);
         } else if (command instanceof DeviceMacroRef) {
            DeviceMacro deviceMacro = ((DeviceMacroRef) command).getTargetDeviceMacro();
            deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
            for (DeviceMacroItem tempDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
               oneUIButtonEventList.addAll(getCommandOwnerByUICommand(tempDeviceMacroItem, protocolEventContainer,maxId));
            }
         } else if (command instanceof CommandDelay) {
            CommandDelay delay = (CommandDelay) command;
            Command uiButtonEvent = new Command();
            uiButtonEvent.setId(maxId.maxId());
            uiButtonEvent.setDelay(delay.getDelaySecond());
            oneUIButtonEventList.add(uiButtonEvent);
         }
      } else if (command instanceof CommandRefItem) {
         DeviceCommand deviceCommand = deviceCommandService.loadById(((CommandRefItem) command).getDeviceCommand()
               .getOid());
         addDeviceCommandEvent(protocolEventContainer, oneUIButtonEventList, deviceCommand,maxId);
      } else {
         return new ArrayList<Command>();
      }
      return oneUIButtonEventList;
   }

   /**
    * @param protocolEventContainer
    * @param oneUIButtonEventList
    * @param deviceCommand
    */
   private void addDeviceCommandEvent(ProtocolCommandContainer protocolEventContainer,
         List<Command> oneUIButtonEventList, DeviceCommand deviceCommand,MaxId maxId) {
      String protocolType = deviceCommand.getProtocol().getType();
      List<ProtocolAttr> protocolAttrs = deviceCommand.getProtocol().getAttributes();

      Command uiButtonEvent = new Command();
      uiButtonEvent.setId(maxId.maxId());
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
            if (absolute.getUiComponent() instanceof UIControl) {
               for (UICommand command : ((UIControl) absolute.getUiComponent()).getCommands()) {
                  addSectionIds(sectionIds, command);
               }
            }
         }
         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               if (cell.getUiComponent() instanceof UIControl) {
                  for (UICommand command : ((UIControl) cell.getUiComponent()).getCommands()) {
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

   public void setControllerConfigService(ControllerConfigService controllerConfigService) {
      this.controllerConfigService = controllerConfigService;
   }

   @Override
   public String getRelativeResourcePathByCurrentAccount(String fileName) {
      return PathConfig.getInstance(configuration).getRelativeResourcePath(fileName, userService.getAccount());
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
         String[] includedPropertyNames = { "groupRefs", "tabbarItems", "tabbarItems.navigate",
               "groupRefs.group.tabbarItems", "groupRefs.group.tabbarItems.navigate", "groupRefs.group.screenRefs",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent", "groupRefs.group.screenRefs.screen.gestures",
               "groupRefs.group.screenRefs.screen.gestures.navigate",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent.uiCommand",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent.commands",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.uiCommand",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.commands",
               "groupRefs.group.screenRefs.screen.grids.uiComponent.sensor",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.sensor" };
         String[] excludePropertyNames = {"panelName"};
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
   
   
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public String getControllerXML(Collection<Screen> screens,long maxOid) {
      
      /*
       * store the max oid 
       */
      MaxId maxId = new MaxId(maxOid+1);
      
      /*
       * initialize UI component box.
       */
      UIComponentBox uiComponentBox = new UIComponentBox();
      initUIComponentBox(screens,uiComponentBox);
      Map<String, Object> context = new HashMap<String, Object>();
      ProtocolCommandContainer eventContainer = new ProtocolCommandContainer();
      ProtocolContainer protocolContainer = ProtocolContainer.getInstance();
      Collection<Sensor> sensors = getAllSensorWithoutDuplicate(screens,maxId);
      Collection<UIComponent> switchs = uiComponentBox.getUIComponentsByType(UISwitch.class);
      Collection<UIComponent> buttons = uiComponentBox.getUIComponentsByType(UIButton.class);
      Collection<UIComponent> gestures = uiComponentBox.getUIComponentsByType(Gesture.class);
      Collection<UIComponent> uiSliders = uiComponentBox.getUIComponentsByType(UISlider.class);
      Collection<UIComponent> uiImages = uiComponentBox.getUIComponentsByType(UIImage.class);
      Collection<UIComponent> uiLabels = uiComponentBox.getUIComponentsByType(UILabel.class);
      Collection<Config> configs = controllerConfigService.listAllForCurrentAccount();
      
      context.put("switchs", switchs);
      context.put("buttons", buttons);
      context.put("screens", screens);
      context.put("eventContainer", eventContainer);
      context.put("resouceServiceImpl", this);
      context.put("protocolContainer", protocolContainer);
      context.put("sensors", sensors);
      context.put("gestures", gestures);
      context.put("uiSliders", uiSliders);
      context.put("labels", uiLabels);
      context.put("images", uiImages);
      context.put("maxId", maxId);
      context.put("configs", configs);


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

   private Set<Sensor> getAllSensorWithoutDuplicate(Collection<Screen> screens,MaxId maxId) {
      Set<Sensor> sensorWithoutDuplicate = new HashSet<Sensor>();
      Collection<Sensor> allSensors = new ArrayList<Sensor>();

      for (Screen screen : screens) {
         for (Absolute absolute : screen.getAbsolutes()) {
            UIComponent component = absolute.getUiComponent();
            initSensors(allSensors, sensorWithoutDuplicate, component);
         }

         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               initSensors(allSensors, sensorWithoutDuplicate, cell.getUiComponent());
            }
         }
      }

      /*
       * reset sensor oid, avoid duplicated id in export xml.
       */

      for (Sensor sensor : sensorWithoutDuplicate) {
         long sensorOid = sensor.getOid();
         long currentSensorId = maxId.maxId();
         for (Sensor s : allSensors) {
            if (s.getOid() == sensorOid) {
               s.setOid(currentSensorId);
            }
         }
      }
      return sensorWithoutDuplicate;
   }

   private void initSensors(Collection<Sensor> allSensors, Set<Sensor> sensorsWithoutDuplicate,UIComponent component) {
      if (component instanceof SensorOwner) {
         SensorOwner sensorOwner = (SensorOwner) component;
         if (sensorOwner.getSensor() != null) {
            allSensors.add(sensorOwner.getSensor());
            sensorsWithoutDuplicate.add(sensorOwner.getSensor());
         }
      }
   }

   private void initUIComponentBox(Collection<Screen> screens,UIComponentBox uiComponentBox) {
      uiComponentBox.clear();
      for (Screen screen : screens) {
         for (Absolute absolute : screen.getAbsolutes()) {
            UIComponent component = absolute.getUiComponent();
            uiComponentBox.add(component);
         }

         for (UIGrid grid : screen.getGrids()) {
            for (Cell cell : grid.getCells()) {
               uiComponentBox.add(cell.getUiComponent());
            }
         }

         for (Gesture gesture : screen.getGestures()) {
            uiComponentBox.add(gesture);
         }
      }
   }
   
   static class MaxId{
      Long maxId = 0L;
      public MaxId(Long maxId){
         this.maxId = maxId;
      }
      
      public Long maxId(){
         return maxId++;
      }
   }

   @Override
   public void updateResources(Collection<Panel> panels,long maxOid) {
      Set<Group> groups = new LinkedHashSet<Group>();
      Set<Screen> screens = new LinkedHashSet<Screen>();
      /*
       * initialize groups and screens.
       */
      initGroupsAndScreens(panels, groups, screens);

      String controllerXmlContent = getControllerXML(screens,maxOid);
      String panelXmlContent = getPanelXML(panels);
      String sectionIds = getSectionIds(screens);

      // replaceUrl(screens, sessionId);
      // String activitiesJson = getActivitiesJson(activities);

      PathConfig pathConfig = PathConfig.getInstance(configuration);
//      File sessionFolder = new File(pathConfig.userFolder(sessionId));
      File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
      if (!userFolder.exists()) {
         userFolder.mkdirs();
      }
      
      /*
       * down load the default image.
       */
      File defaultImage = new File(pathConfig.getWebRootFolder() + UIImage.DEFAULT_IMAGE_URL);
      FileUtilsExt.copyFile(defaultImage, new File(userFolder, defaultImage.getName()));
      
      File panelXMLFile = new File(pathConfig.panelXmlFilePath(userService.getAccount()));
      File controllerXMLFile = new File(pathConfig.controllerXmlFilePath(userService.getAccount()));
      File lircdFile = new File(pathConfig.lircFilePath(userService.getAccount()));
      // File dotImport = new File(pathConfig.dotImportFilePath(sessionId));

      /*
       * validate and output panel.xml.
       */
      String newIphoneXML = XmlParser.validateAndOutputXML(new File(getClass().getResource(
            configuration.getPanelXsdPath()).getPath()), panelXmlContent, userFolder);
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
//            FileUtils.copyURLToFile(buildLircRESTUrl(configuration.getBeehiveLircdConfRESTUrl(), sectionIds), lircdFile);
         }
         if (lircdFile.exists() && lircdFile.length() == 0) {
            lircdFile.delete();
         }
         
         serialize(panels,maxOid);
         saveResourcesToBeehive();
      } catch (IOException e) {
         LOGGER.error("Compress zip file occur IOException", e);
         throw new FileOperationException("Compress zip file occur IOException", e);
      }
   }
   
   private void serialize(Collection<Panel> panels, long maxOid) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File panelsObjFile = new File(pathConfig.getSerizalizedPanelsFile(userService.getAccount()));
      ObjectOutputStream oos = null;
      try {
         FileUtilsExt.deleteQuietly(panelsObjFile);
         if(panels==null || panels.size()<1){
            return;
         }
         oos = new ObjectOutputStream(new FileOutputStream(panelsObjFile));
         oos.writeObject(panels);
         oos.writeLong(maxOid);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            if (oos != null) {
               oos.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   @SuppressWarnings("unchecked")
   public PanelsAndMaxOid restore(){
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File panelsObjFile = new File(pathConfig.getSerizalizedPanelsFile(userService.getAccount()));
      if(!panelsObjFile.exists()){
         return null;
      }
      ObjectInputStream ois = null;
      PanelsAndMaxOid panelsAndMaxOid = null;
      try{
         
         ois = new ObjectInputStream(new FileInputStream(panelsObjFile));
         Collection<Panel> panels = (Collection<Panel>) ois.readObject();
         Long maxOid = ois.readLong();
         panelsAndMaxOid = new PanelsAndMaxOid(panels,maxOid);
         
      } catch(Exception e){
         LOGGER.fatal("restore failed from server", e);
      } finally {
         try {
            if(ois!=null){
               ois.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return panelsAndMaxOid;
   }

   @SuppressWarnings("all")
   public void saveResourcesToBeehive() {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost();
      String beehiveRootRestURL = configuration.getBeehiveRESTRootUrl();
      try {
         httpPost.setURI(new URI(beehiveRootRestURL + "account/" + userService.getAccount().getOid() + "/resource/"));
         FileBody resource = new FileBody(getExportResource());
         MultipartEntity entity = new MultipartEntity();
         entity.addPart("resource", resource);
         httpPost.setEntity(entity);
         httpClient.execute(httpPost);
      } catch (Exception e) {
         LOGGER.error("failed to save resource to beehive", e);
      }
   }
   
   public void saveTemplateResourcesToBeehive(Template template) {
      boolean share = template.getShareTo() == Template.PUBLIC;
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost();
      String beehiveRootRestURL = configuration.getBeehiveRESTRootUrl();
      try {
         if (!share) {
            httpPost
                  .setURI(new URI(beehiveRootRestURL + "account/" + userService.getAccount().getOid() + "/resource/template/"+template.getOid()));
         } else {
            httpPost.setURI(new URI(beehiveRootRestURL + "account/0/resource/template/"+template.getOid()));
         }
         
         FileBody resource = new FileBody(getTemplateZipResource());
         MultipartEntity entity = new MultipartEntity();
         entity.addPart("resource", resource);

         httpPost.setEntity(entity);

         httpClient.execute(httpPost);
      } catch (Exception e) {
         LOGGER.error("failed to save resource to beehive", e);
      }
   }
   @Override
   public boolean canRestore() {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File panelsObjFile = new File(pathConfig.getSerizalizedPanelsFile(userService.getAccount()));
      return panelsObjFile.exists();
   }

   @Override
   public void downloadResourcesForTemplate(long templateOid) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTRootUrl() + "account/"
            + userService.getAccount().getOid() + "/template/resource/" + templateOid);
      InputStream inputStream = null;
      FileOutputStream fos = null;
      try {
         HttpResponse response = httpClient.execute(httpGet);
         if(200 == response.getStatusLine().getStatusCode()){
            LOGGER.error("failed to save resource to beehive");
            inputStream = response.getEntity().getContent();
            File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
            userFolder.mkdirs();
            File outPut = new File(userFolder, "template.zip");
            FileUtilsExt.deleteQuietly(outPut);
            fos = new FileOutputStream(outPut);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
               fos.write(buffer, 0, len);
            }
            ZipUtils.unzip(outPut, pathConfig.userFolder(userService.getAccount()));
            FileUtilsExt.deleteQuietly(outPut);
         }
      } catch (Exception e) {
         LOGGER.error("failed to down load resource from beehive!", e);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
   
   private File getResourceZipFile(List<String> ignoreExtentions) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
      File[] filesInAccountFolder = userFolder.listFiles();
      File[] filesInZip = new File[filesInAccountFolder.length];
      int i = 0;
      for (File file : filesInAccountFolder) {
         if (file.exists() && !file.getPath().equals(pathConfig.getSerizalizedPanelsFile(userService.getAccount()))) {
            filesInZip[i++] = file;
         }
      }
      File zipFile = compressFilesToZip(filesInZip, pathConfig.openremoteZipFilePath(userService.getAccount()),
            ignoreExtentions);
      return zipFile;

   }
   
   private File getTemplateZipResource(){
      List<String> ignoreExtentions = new ArrayList<String>();
      ignoreExtentions.add("zip");
      ignoreExtentions.add("xml");
      return getResourceZipFile(ignoreExtentions);
   }
   private File getExportResource() {
      List<String> ignoreExtentions = new ArrayList<String>();
      return getResourceZipFile(ignoreExtentions);
   }
}
