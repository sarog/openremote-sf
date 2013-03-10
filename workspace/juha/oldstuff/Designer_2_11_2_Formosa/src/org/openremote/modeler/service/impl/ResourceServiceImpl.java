/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.Command;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.ControllerConfig;
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
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.ScreenPair.OrientationType;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.SensorOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIControl;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.IllegalRestUrlException;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.exception.XmlExportException;
import org.openremote.modeler.exception.NetworkException;
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
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.logging.AdministratorAlert;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * TODO
 * 
 * @author Allen, Handy, Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 * 
 */
public class ResourceServiceImpl implements ResourceService
{

  public static final String PANEL_XML_TEMPLATE = "panelXML.vm";
  public static final String CONTROLLER_XML_TEMPLATE = "controllerXML.vm";

  private final static LogFacade serviceLog =
      LogFacade.getInstance(LogFacade.Category.RESOURCE_SERVICE);

  //private static final Logger _LOGGER = Logger.getLogger(ResourceServiceImpl.class);


  private Configuration configuration;

  private DeviceCommandService deviceCommandService;

  private DeviceMacroService deviceMacroService;

  private VelocityEngine velocity;

  private UserService userService;

  private ControllerConfigService controllerConfigService = null;



  //
  // TODO : this implementation should go away with MODELER-288
  //
  @Override public String downloadZipResource(long maxOid, String sessionId, List<Panel> panels)
  {
    initResources(panels, maxOid);

    try
    {
      Set<String> imageNames = new HashSet<String>();
      Set<File> imageFiles = new HashSet<File>();

      for (Panel panel : panels)
      {
        imageNames.addAll(Panel.getAllImageNames(panel));
      }

      for (String name : imageNames)
      {
        name = DesignerState.uglyImageSourcePathHack(userService.getCurrentUser(), name);
        
        imageFiles.add(new File(name));

        serviceLog.debug("DownloadZipResource: Add image file ''{0}''.", name);
      }

      LocalFileCache cache = new LocalFileCache(configuration, userService.getCurrentUser());

      cache.markInUseImages(imageFiles);

      File zipFile = cache.createExportArchive();

      PathConfig pathConfig = PathConfig.getInstance(configuration);

      return pathConfig.getZipUrl(userService.getAccount()) + zipFile.getName();
    }

    catch (Throwable t)
    {
      serviceLog.error("Cannot export account resources : {0}", t, t.getMessage());

      throw new XmlExportException("Export failed : " + t.getMessage(), t);
    }
  }


  /**
   * TODO
   *
   * Builds the lirc rest url.
   */
  private URL buildLircRESTUrl(String restAPIUrl, String ids)
  {
    URL lircUrl;

    try
    {
      lircUrl = new URL(restAPIUrl + "?ids=" + ids);
    }

    catch (MalformedURLException e)
    {
      // TODO : don't throw runtime exceptions
      throw new IllegalArgumentException("Lirc file url is invalid", e);
    }

    return lircUrl;
  }

   @Deprecated @Override public String getDotImportFileForRender(String sessionId, InputStream inputStream) {
//      File tmpDir = new File(PathConfig.getInstance(configuration).userFolder(sessionId));
//      if (tmpDir.exists() && tmpDir.isDirectory()) {
//         try {
//            FileUtils.deleteDirectory(tmpDir);
//         } catch (IOException e) {
//            throw new FileOperationException("Error in deleting temp dir", e);
//         }
//      }
//      new File(PathConfig.getInstance(configuration).userFolder(sessionId)).mkdirs();
//      String dotImportFileContent = "";
//      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
//      ZipEntry zipEntry;
//      FileOutputStream fileOutputStream = null;
//      try {
//         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
//            if (!zipEntry.isDirectory()) {
//               if (Constants.PANEL_DESC_FILE.equalsIgnoreCase(StringUtils.getFileExt(zipEntry.getName()))) {
//                  dotImportFileContent = IOUtils.toString(zipInputStream);
//               }
//               if (!checkXML(zipInputStream, zipEntry, "iphone")) {
//                  throw new XmlParserException("The iphone.xml schema validation failed, please check it");
//               } else if (!checkXML(zipInputStream, zipEntry, "controller")) {
//                  throw new XmlParserException("The controller.xml schema validation failed, please check it");
//               }
//
//               if (!FilenameUtils.getExtension(zipEntry.getName()).matches("(xml|import|conf)")) {
//                  File file = new File(PathConfig.getInstance(configuration).userFolder(sessionId) + zipEntry.getName());
//                  FileUtils.touch(file);
//
//                  fileOutputStream = new FileOutputStream(file);
//                  int b;
//                  while ((b = zipInputStream.read()) != -1) {
//                     fileOutputStream.write(b);
//                  }
//                  fileOutputStream.close();
//               }
//            }
//
//         }
//      } catch (IOException e) {
//         throw new FileOperationException("Error in reading import file from zip", e);
//      } finally {
//         try {
//            zipInputStream.closeEntry();
//            if (fileOutputStream != null) {
//               fileOutputStream.close();
//            }
//         } catch (IOException e) {
//            LOGGER.warn("Failed to close import file resources", e);
//         }
//
//      }
//      return dotImportFileContent;
     return "";
   }

//   /**
//    * Check xml.
//    *
//    * @param zipInputStream
//    *           the zip input stream
//    * @param zipEntry
//    *           the zip entry
//    * @param xmlName
//    *           the xml name
//    *
//    * @return true, if successful
//    *
//    * @throws IOException
//    *            Signals that an I/O exception has occurred.
//    */
//   private boolean checkXML(ZipInputStream zipInputStream, ZipEntry zipEntry, String xmlName) throws IOException {
//      if (zipEntry.getName().equals(xmlName + ".xml")) {
//         String xsdRelativePath = "iphone".equals(xmlName) ? configuration.getPanelXsdPath() : configuration
//               .getControllerXsdPath();
//         String xsdPath = getClass().getResource(xsdRelativePath).getPath();
//         if (!XmlParser.checkXmlSchema(xsdPath, IOUtils.toString(zipInputStream))) {
//            return false;
//         }
//      }
//      return true;
//   }


  /**
   * TODO
   *
   * @deprecated looks unused
   */
  @Deprecated public File uploadImage(InputStream inputStream, String fileName, String sessionId)
  {
    File file = new File(
        PathConfig.getInstance(configuration).userFolder(sessionId) +
        File.separator + fileName
    );

    return uploadFile(inputStream, file);
  }

  //
  //  TODO :
  //
  //    - restrict file sizes
  //    - work through resource cache interface
  //    - restrict file names
  //    - should be a direct call to Beehive
  //
  public File uploadImage(InputStream inputStream, String fileName)
  {
    File file = new File(
        PathConfig.getInstance(configuration).userFolder(userService.getAccount()) +
        File.separator + fileName
    );

    return uploadFile(inputStream, file);
  }


  //
  //  TODO :
  //
  //    - error handling on file I/O
  //    - work through resource cache interface
  //
  private File uploadFile(InputStream inputStream, File file)
  {
    FileOutputStream fileOutputStream = null;

    try
    {
      File dir = file.getParentFile();

      if (!dir.exists())
      {
        dir.mkdirs();     // TODO : need to check success
      }

      FileUtils.touch(file);

      fileOutputStream = new FileOutputStream(file);
      IOUtils.copy(inputStream, fileOutputStream);
    }

    catch (IOException e)
    {
      throw new FileOperationException("Failed to save uploaded image", e);
    }

    finally
    {
      try
      {
        if (fileOutputStream != null)
        {
          fileOutputStream.close();
        }
      }

      catch (IOException e)
      {
        serviceLog.warn("Failed to close resources on uploaded file", e);
      }
    }

    return file;
  }


   /**
    * TODO
    * 
    * @param command
    *           the device command item
    * @param protocolEventContainer
    *           the protocol event container
    * 
    * @return the controller xml segment content
    */
   public List<Command> getCommandOwnerByUICommand(UICommand command, ProtocolCommandContainer protocolEventContainer,
         MaxId maxId) {
      List<Command> oneUIButtonEventList = new ArrayList<Command>();
      try {
         if (command instanceof DeviceMacroItem) {
            if (command instanceof DeviceCommandRef) {
               DeviceCommand deviceCommand = deviceCommandService.loadById(((DeviceCommandRef) command)
                     .getDeviceCommand().getOid());
               addDeviceCommandEvent(protocolEventContainer, oneUIButtonEventList, deviceCommand, maxId);
            } else if (command instanceof DeviceMacroRef) {
               DeviceMacro deviceMacro = ((DeviceMacroRef) command).getTargetDeviceMacro();
               deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
               for (DeviceMacroItem tempDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
                  oneUIButtonEventList.addAll(getCommandOwnerByUICommand(tempDeviceMacroItem, protocolEventContainer,
                        maxId));
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
            protocolEventContainer.removeDeviceCommand(deviceCommand);
            addDeviceCommandEvent(protocolEventContainer, oneUIButtonEventList, deviceCommand, maxId);
         } else {
            return new ArrayList<Command>();
         }
      } catch (Exception e) {
         serviceLog.warn("Some components referenced a removed object:  " + e.getMessage());
         return new ArrayList<Command>();
      }
      return oneUIButtonEventList;
   }

   private void addDeviceCommandEvent(ProtocolCommandContainer protocolEventContainer,
         List<Command> oneUIButtonEventList, DeviceCommand deviceCommand, MaxId maxId) {
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

   private void addSectionIds(Set<String> sectionIds, UICommand command) {
      if (command instanceof DeviceMacroItem) {
         sectionIds.addAll(getDeviceMacroItemSectionIds((DeviceMacroItem) command));
      } else if (command instanceof CommandRefItem) {
         sectionIds.add(((CommandRefItem) command).getDeviceCommand().getSectionId());
      }
   }

   /**
    * Gets the device macro item section ids.
    * 
    * @param deviceMacroItem
    *           the device macro item
    * 
    * @return the device macro item section ids
    */
   private Set<String> getDeviceMacroItemSectionIds(DeviceMacroItem deviceMacroItem) {
      Set<String> deviceMacroRefSectionIds = new HashSet<String>();
      try {
         if (deviceMacroItem instanceof DeviceCommandRef) {
            deviceMacroRefSectionIds.add(((DeviceCommandRef) deviceMacroItem).getDeviceCommand().getSectionId());
         } else if (deviceMacroItem instanceof DeviceMacroRef) {
            DeviceMacro deviceMacro = ((DeviceMacroRef) deviceMacroItem).getTargetDeviceMacro();
            if (deviceMacro != null) {
               deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
               for (DeviceMacroItem nextDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
                  deviceMacroRefSectionIds.addAll(getDeviceMacroItemSectionIds(nextDeviceMacroItem));
               }
            }
         }
      } catch (Exception e) {
         serviceLog.warn("Some components referenced a removed DeviceMacro!");
      }
      return deviceMacroRefSectionIds;
   }



  public Configuration getConfiguration()
  {
    return configuration;
  }

  public void setConfiguration(Configuration configuration)
  {
    this.configuration = configuration;
  }

  public DeviceCommandService getDeviceCommandService()
  {
    return deviceCommandService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService)
  {
    this.deviceCommandService = deviceCommandService;
  }

  public void setDeviceMacroService(DeviceMacroService deviceMacroService)
  {
    this.deviceMacroService = deviceMacroService;
  }

  /**
   * @deprecated looks unused
   */
  @Deprecated public String getRelativeResourcePath(String sessionId, String fileName)
  {
    return PathConfig.getInstance(configuration).getRelativeResourcePath(fileName, sessionId);
  }

  public void setControllerConfigService(ControllerConfigService controllerConfigService)
  {
    this.controllerConfigService = controllerConfigService;
  }

  //
  //  TODO :
  //
  //   - most likely should be internalized to resource cache implementation
  //
  @Override public String getRelativeResourcePathByCurrentAccount(String fileName)
  {
    return PathConfig.getInstance(configuration).getRelativeResourcePath(fileName, userService.getAccount());
  }


   @Override
   public String getPanelsJson(Collection<Panel> panels) {
      try {
         String[] includedPropertyNames = { "groupRefs", "tabbar.tabbarItems", "tabbar.tabbarItems.navigate",
               "groupRefs.group.tabbar.tabbarItems", "groupRefs.group.tabbar.tabbarItems.navigate",
               "groupRefs.group.screenRefs", "groupRefs.group.screenRefs.screen.absolutes.uiComponent",
               "groupRefs.group.screenRefs.screen.gestures", "groupRefs.group.screenRefs.screen.gestures.navigate",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent.uiCommand",
               "groupRefs.group.screenRefs.screen.absolutes.uiComponent.commands",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.uiCommand",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.commands",
               "groupRefs.group.screenRefs.screen.grids.uiComponent.sensor",
               "groupRefs.group.screenRefs.screen.grids.cells.uiComponent.sensor" };
         String[] excludePropertyNames = { "panelName", "*.displayName", "*.panelXml" };
         return JsonGenerator.serializerObjectInclude(panels, includedPropertyNames, excludePropertyNames);
      } catch (Exception e) {
         serviceLog.error(e.getMessage(), e);
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
         context.put("stringUtils", StringUtils.class);
         return VelocityEngineUtils.mergeTemplateIntoString(velocity, PANEL_XML_TEMPLATE, context);
      } catch (VelocityException e) {
         throw new XmlExportException("Failed to read panel.xml", e);
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

   @SuppressWarnings("unchecked")
   public String getControllerXML(Collection<Screen> screens, long maxOid)
   {

     // PATCH R3181 BEGIN ---8<-----
     /*
      * Get all sensors and commands from database.
      */
     List<Sensor> dbSensors = userService.getAccount().getSensors();
     List<Device> allDevices = userService.getAccount().getDevices();
     List<DeviceCommand> allDBDeviceCommands = new ArrayList<DeviceCommand>();

     for (Device device : allDevices)
     {
        allDBDeviceCommands.addAll(deviceCommandService.loadByDevice(device.getOid()));
     }
     // PATCH R3181 END ---->8-----

     
      /*
       * store the max oid
       */
      MaxId maxId = new MaxId(maxOid + 1);

      /*
       * initialize UI component box.
       */
      UIComponentBox uiComponentBox = new UIComponentBox();
      initUIComponentBox(screens, uiComponentBox);
      Map<String, Object> context = new HashMap<String, Object>();
      ProtocolCommandContainer eventContainer = new ProtocolCommandContainer();
      eventContainer.setAllDBDeviceCommands(allDBDeviceCommands);
      ProtocolContainer protocolContainer = ProtocolContainer.getInstance();

      Collection<Sensor> sensors = getAllSensorWithoutDuplicate(screens, maxId, dbSensors);

      Collection<UISwitch> switchs = (Collection<UISwitch>) uiComponentBox.getUIComponentsByType(UISwitch.class);
      Collection<UIComponent> buttons = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(UIButton.class);
      Collection<UIComponent> gestures = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(Gesture.class);
      Collection<UIComponent> uiSliders = (Collection<UIComponent>) uiComponentBox
            .getUIComponentsByType(UISlider.class);
      Collection<UIComponent> uiImages = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(UIImage.class);
      Collection<UIComponent> uiLabels = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(UILabel.class);
      Collection<ControllerConfig> configs = controllerConfigService.listAllConfigs();
      configs.removeAll(controllerConfigService.listAllexpiredConfigs());
      configs.addAll(controllerConfigService.listAllMissingConfigs());

      // TODO :  BEGIN HACK (TO BE REMOVED)
      //
      //   - the following removes the rules.editor configuration section from the controller.xml
      //     <config> section. The rules should not be defined in terms of controller configuration
      //     in the designer but as artifacts, similar to images (and multiple rule files should
      //     be supported).

      for (ControllerConfig controllerConfig : configs)
      {
        if (controllerConfig.getName().equals("rules.editor"))
        {
          configs.remove(controllerConfig);

          break;      // this fixes a concurrent modification error in this hack..
        }
      }

      // TODO : END HACK -------------------
     

      context.put("switchs", switchs);
      context.put("buttons", buttons);
      context.put("screens", screens);
      context.put("eventContainer", eventContainer);
      context.put("resouceServiceImpl", this);
      context.put("protocolContainer", protocolContainer);
      context.put("sensors", sensors);
      context.put("dbSensors", dbSensors);
      context.put("gestures", gestures);
      context.put("uiSliders", uiSliders);
      context.put("labels", uiLabels);
      context.put("images", uiImages);
      context.put("maxId", maxId);
      context.put("configs", configs);
      context.put("stringUtils", StringUtils.class);

      return VelocityEngineUtils.mergeTemplateIntoString(velocity, CONTROLLER_XML_TEMPLATE, context);
   }

  //
  // TODO: should be removed
  //
  //   - rules should not be defined in terms of controller configuration
  //     in the designer but as artifacts, similar to images (and multiple rule files should
  //     be supported).
  //
  private String getRulesFileContent()
  {
    Collection<ControllerConfig> configs = controllerConfigService.listAllConfigs();

    configs.removeAll(controllerConfigService.listAllexpiredConfigs());
    configs.addAll(controllerConfigService.listAllMissingConfigs());

    String result = "";

    for (ControllerConfig controllerConfig : configs)
    {
      if (controllerConfig.getName().equals("rules.editor"))
      {
        result = controllerConfig.getValue();
      }
    }

    return result;
  }


   //
   //  TODO :
   //
   //   - should be internalized as part of MODELER-287
   //
   private void initGroupsAndScreens(Collection<Panel> panels, Set<Group> groups, Set<Screen> screens) {
      for (Panel panel : panels) {
         List<GroupRef> groupRefs = panel.getGroupRefs();
         for (GroupRef groupRef : groupRefs) {
            groups.add(groupRef.getGroup());
         }
      }

      for (Group group : groups) {
         List<ScreenPairRef> screenRefs = group.getScreenRefs();
         for (ScreenPairRef screenRef : screenRefs) {
            ScreenPair screenPair = screenRef.getScreen();
            if (OrientationType.PORTRAIT.equals(screenPair.getOrientation())) {
               screens.add(screenPair.getPortraitScreen());
            } else if (OrientationType.LANDSCAPE.equals(screenPair.getOrientation())) {
               screens.add(screenPair.getLandscapeScreen());
            } else if (OrientationType.BOTH.equals(screenPair.getOrientation())) {
               screenPair.setInverseScreenIds();
               screens.add(screenPair.getPortraitScreen());
               screens.add(screenPair.getLandscapeScreen());
            }
         }
      }
   }


   private Set<Sensor> getAllSensorWithoutDuplicate(Collection<Screen> screens, MaxId maxId,
                                                    List<Sensor> dbSensors)
   {
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


      // PATCH R3181 BEGIN ---8<------
      List<Sensor> duplicateDBSensors = new ArrayList<Sensor>();

      for (Sensor dbSensor : dbSensors)
      {
        for (Sensor clientSensor : sensorWithoutDuplicate)
        {
          if (dbSensor.getOid() == clientSensor.getOid())
          {
            duplicateDBSensors.add(dbSensor);
          }
        }
      }

      dbSensors.removeAll(duplicateDBSensors);
      // PATCH R3181 END --->8-------


      /*
       * reset sensor oid, avoid duplicated id in export xml. make sure same sensors have same oid.
       */

      for (Sensor sensor : sensorWithoutDuplicate) {
         long currentSensorId = maxId.maxId();
         Collection<Sensor> sensorsWithSameOid = new ArrayList<Sensor>();
         sensorsWithSameOid.add(sensor);
         for (Sensor s : allSensors) {
            if (s.equals(sensor)) {
               sensorsWithSameOid.add(s);
            }
         }
         for (Sensor s : sensorsWithSameOid) {
            s.setOid(currentSensorId);
         }
      }
      return sensorWithoutDuplicate;
   }

   private void initSensors(Collection<Sensor> allSensors, Set<Sensor> sensorsWithoutDuplicate, UIComponent component) {
      if (component instanceof SensorOwner) {
         SensorOwner sensorOwner = (SensorOwner) component;
         if (sensorOwner.getSensor() != null) {
            allSensors.add(sensorOwner.getSensor());
            sensorsWithoutDuplicate.add(sensorOwner.getSensor());
         }
      }
   }

   private void initUIComponentBox(Collection<Screen> screens, UIComponentBox uiComponentBox) {
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


   //
   // TODO :
   //
   //  - should be internalized to resource cache as part of MODELER-287
   //
   @Override public void initResources(Collection<Panel> panels, long maxOid) {
      // 1, we must serialize panels at first, otherwise after integrating panel's ui component and commands(such as
      // device command, sensor ...)
      // the oid would be changed, that is not ought to happen. for example : after we restore panels, we create a
      // component with same sensor (like we did last time), the two
      // sensors will have different oid, if so, when we export controller.xml we my find that there are two (or more
      // sensors) with all the same property except oid.
      serializePanelsAndMaxOid(panels, maxOid);
      Set<Group> groups = new LinkedHashSet<Group>();
      Set<Screen> screens = new LinkedHashSet<Screen>();
      /*
       * initialize groups and screens.
       */
      initGroupsAndScreens(panels, groups, screens);

      String controllerXmlContent = getControllerXML(screens, maxOid);
      String panelXmlContent = getPanelXML(panels);
      String sectionIds = getSectionIds(screens);
      String rulesFileContent = getRulesFileContent();
     
      // replaceUrl(screens, sessionId);
      // String activitiesJson = getActivitiesJson(activities);

      PathConfig pathConfig = PathConfig.getInstance(configuration);
      // File sessionFolder = new File(pathConfig.userFolder(sessionId));
      File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
      if (!userFolder.exists()) {
         boolean success = userFolder.mkdirs();

         if (!success) {
            throw new FileOperationException("Failed to create directory path to user folder '" + userFolder + "'.");
         }
      }

      /*
       * down load the default image.
       */
      File defaultImage = new File(pathConfig.getWebRootFolder() + UIImage.DEFAULT_IMAGE_URL);
      FileUtilsExt.copyFile(defaultImage, new File(userFolder, defaultImage.getName()));

      File panelXMLFile = new File(pathConfig.panelXmlFilePath(userService.getAccount()));
      File controllerXMLFile = new File(pathConfig.controllerXmlFilePath(userService.getAccount()));
      File lircdFile = new File(pathConfig.lircFilePath(userService.getAccount()));

      File rulesDir = new File(pathConfig.userFolder(userService.getAccount()), "rules");
      File rulesFile = new File(rulesDir, "modeler_rules.drl");
     
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
         FileUtilsExt.deleteQuietly(rulesFile);

         FileUtilsExt.writeStringToFile(panelXMLFile, newIphoneXML);
         FileUtilsExt.writeStringToFile(controllerXMLFile, controllerXmlContent);
         FileUtilsExt.writeStringToFile(rulesFile, rulesFileContent);
        
         if (sectionIds != null && !sectionIds.equals("")) {
            FileUtils
                  .copyURLToFile(buildLircRESTUrl(configuration.getBeehiveLircdConfRESTUrl(), sectionIds), lircdFile);
         }
         if (lircdFile.exists() && lircdFile.length() == 0) {
            boolean success = lircdFile.delete();

            if (!success) {
               serviceLog.error("Failed to delete '" + lircdFile + "'.");
            }

         }

      } catch (IOException e) {
         throw new FileOperationException("Failed to write resource: " + e.getMessage(), e);
      }
   }


   //
   //   TODO :
   //
   //     - should be internalized as part of resource cache implementation, see MODELER-287
   //
   private void serializePanelsAndMaxOid(Collection<Panel> panels, long maxOid) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File panelsObjFile = new File(pathConfig.getSerializedPanelsFile(userService.getAccount()));
      ObjectOutputStream oos = null;
      try {
         FileUtilsExt.deleteQuietly(panelsObjFile);
         if (panels == null || panels.size() < 1) {
            return;
         }
         oos = new ObjectOutputStream(new FileOutputStream(panelsObjFile));
         oos.writeObject(panels);
         oos.writeLong(maxOid);
      } catch (FileNotFoundException e) {
         serviceLog.error(e.getMessage(), e);
      } catch (IOException e) {
         serviceLog.error(e.getMessage(), e);
      } finally {
         try {
            if (oos != null) {
               oos.close();
            }
         } catch (IOException e) {
            serviceLog.warn("Unable to close output stream to '" + panelsObjFile + "'.");
         }
      }
   }


  /**
   * This implementation has been moved and delegates to {@link DesignerState#restore}.
   */
  @Override @Deprecated public PanelsAndMaxOid restore()
  {
    try
    {
      DesignerState state = new DesignerState(configuration, userService.getCurrentUser());
      state.restore();

      return state.transformToPanelsAndMaxOid();
    }

    catch (NetworkException e)
    {
      serviceLog.error(
          "Could not restore designer state due to network error : {0}", e, e.getMessage()
      );

      // TODO :
      //   - could check network exception severity, and retry a few times in case of a
      //     network glitch, not a serious error
      //   - log and throw here since unclear if the runtime exceptions to client are
      //     logged anywhere

      throw new UIRestoreException(
          "Could not restore account data to Designer due to a network error. You can try " +
          "again later. If the problem persists, please contact support. Error : " +
          e.getMessage(), e
      );
    }
  }


  /**
   * This implementation has been moved and delegates to {@link DesignerState#save}.
   */
  @Override @Deprecated public void saveResourcesToBeehive(Collection<Panel> panels)
  {
    // Create a set of panels to eliminate potential duplicate instances...

    HashSet<Panel> panelSet = new HashSet<Panel>();
    panelSet.addAll(panels);

    // Delegate implementation to DesignerState...

    DesignerState state = new DesignerState(configuration, userService.getCurrentUser());
    state.save(panelSet);
  }


   //
   //   TODO :
   //
   //    - should migrate to resource cache API
   //
   public void saveTemplateResourcesToBeehive(Template template) {
      boolean share = template.getShareTo() == Template.PUBLIC;
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost();
      String beehiveRootRestURL = configuration.getBeehiveRESTRootUrl();
      String url = "";
      if (!share) {
         url = beehiveRootRestURL + "account/" + userService.getAccount().getOid() + "/template/" + template.getOid()
               + "/resource/";
      } else {
         url = beehiveRootRestURL + "account/0/template/" + template.getOid() + "/resource/";
      }
      try {
         httpPost.setURI(new URI(url));
         File templateZip = getTemplateResource(template);
         if (templateZip == null) {
            serviceLog.warn("There are no template resources for template \"" + template.getName()
                  + "\"to save to beehive!");
            return;
         }
         FileBody resource = new FileBody(templateZip);
         MultipartEntity entity = new MultipartEntity();
         entity.addPart("resource", resource);

         this.addAuthentication(httpPost);
         httpPost.setEntity(entity);

         HttpResponse response = httpClient.execute(httpPost);

         if (200 != response.getStatusLine().getStatusCode()) {
            throw new BeehiveNotAvailableException("Failed to save template to Beehive, status code: "
                  + response.getStatusLine().getStatusCode());
         }
      } catch (NullPointerException e) {
         serviceLog.warn("There are no template resources for template \"" + template.getName() + "\"to save to beehive!");
      } catch (IOException e) {
         throw new BeehiveNotAvailableException("Failed to save template to Beehive", e);
      } catch (URISyntaxException e) {
         throw new IllegalRestUrlException("Invalid Rest URL: " + url + " to save template resource to beehive! ", e);
      }
   }


  //
  // TODO :
  //
  //   - should migrate to ResourceCache interface
  //
  @Override public boolean canRestore()
  {
    PathConfig pathConfig = PathConfig.getInstance(configuration);
    File panelsObjFile = new File(pathConfig.getSerializedPanelsFile(userService.getAccount()));

    return panelsObjFile.exists();
  }

   //
   // TODO :
   //
   //  - should migrate to ResourceCache interface
   //
   @Override public void downloadResourcesForTemplate(long templateOid) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTRootUrl() + "account/"
            + userService.getAccount().getOid() + "/template/" + templateOid + "/resource");
      InputStream inputStream = null;
      FileOutputStream fos = null;
      this.addAuthentication(httpGet);

      try {
         HttpResponse response = httpClient.execute(httpGet);

         if (200 == response.getStatusLine().getStatusCode()) {
            inputStream = response.getEntity().getContent();
            File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
            if (!userFolder.exists()) {
               boolean success = userFolder.mkdirs();
               if (!success) {
                  throw new FileOperationException("Unable to create directories for path '" + userFolder + "'.");
               }
            }
            File outPut = new File(userFolder, "template.zip");
            FileUtilsExt.deleteQuietly(outPut);
            fos = new FileOutputStream(outPut);
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1) {
               fos.write(buffer, 0, len);
            }

            fos.flush();
            ZipUtils.unzip(outPut, pathConfig.userFolder(userService.getAccount()));
            FileUtilsExt.deleteQuietly(outPut);
         } else if (404 == response.getStatusLine().getStatusCode()) {
            serviceLog.warn("There are no resources for this template, ID:" + templateOid);
            return;
         } else {
            throw new BeehiveNotAvailableException("Failed to download resources for template, status code: "
                  + response.getStatusLine().getStatusCode());
         }
      } catch (IOException ioException) {
         throw new BeehiveNotAvailableException("I/O exception in handling user's template.zip file: "
               + ioException.getMessage(), ioException);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ioException) {
               serviceLog.warn("Failed to close input stream from '" + httpGet.getURI() + "': " + ioException.getMessage(),
                     ioException);
            }
         }

         if (fos != null) {
            try {
               fos.close();
            } catch (IOException ioException) {
               serviceLog.warn("Failed to close file output stream to user's template.zip file: "
                     + ioException.getMessage(), ioException);
            }
         }
      }
   }


  //
  // TODO :
  //
  //   - should be migrated as part of the cache implementation
  //
  @Override public File getTemplateResource(Template template)
  {
    ScreenPair sp = template.getScreen();
    Collection<ImageSource> images = sp.getAllImageSources();

    HashSet<String> filenames = new HashSet<String>();

    for (ImageSource source : images)
    {
      filenames.add(source.getImageFileName());
    }

    Set<File> templateRelatedFiles = new HashSet<File>();

    PathConfig pathConfig = PathConfig.getInstance(configuration);

    for (String name : filenames)
    {
      templateRelatedFiles.add(
          new File(pathConfig.userFolder(userService.getCurrentUser().getAccount()), name)
      );
    }

    if (templateRelatedFiles.size() == 0)
    {
      return null;
    }


    // File zipFile = new File(pathConfig.openremoteZipFilePath(userService.getAccount()));
    // FileUtilsExt.deleteQuietly(zipFile);

    File userDir = new File(pathConfig.userFolder(userService.getAccount()));
    File templateDir = new File(userDir, "template");

    if (!templateDir.exists())
    {
      boolean success = templateDir.mkdirs();

      if (!success)
      {
        serviceLog.error("Could not create template dir ''{0}''", templateDir.getAbsolutePath());
      }
    }

    File templateFile = new File(templateDir, "openremote.zip");

    ZipUtils.compress(templateFile.getAbsolutePath(), new ArrayList<File>(templateRelatedFiles));

    return templateFile;
  }


  /**
   * This method is calling by controllerXML.vm, to export sensors which from database.
   */
  public Long getMaxId(MaxId maxId)
  {
    // Part of patch R3181 -- include all components in controller.xml even if
    // not bound to UI components

    return maxId.maxId();
  }

  /**
   * Adds the data base commands into protocolEventContainer.
   */
  public void addDataBaseCommands(ProtocolCommandContainer protocolEventContainer, MaxId maxId)
  {
    // Part of patch R3181 -- include all components in controller.xml even if
    // not bound to UI components

    List<DeviceCommand> dbDeviceCommands = protocolEventContainer.getAllDBDeviceCommands();

    for (DeviceCommand deviceCommand : dbDeviceCommands)
    {
      String protocolType = deviceCommand.getProtocol().getType();
      List<ProtocolAttr> protocolAttrs = deviceCommand.getProtocol().getAttributes();

      Command uiButtonEvent = new Command();
      uiButtonEvent.setId(maxId.maxId());
      uiButtonEvent.setProtocolDisplayName(protocolType);

      for (ProtocolAttr protocolAttr : protocolAttrs)
      {
        uiButtonEvent.getProtocolAttrs().put(protocolAttr.getName(), protocolAttr.getValue());
      }

      uiButtonEvent.setLabel(deviceCommand.getName());
      protocolEventContainer.addUIButtonEvent(uiButtonEvent);
    }
  }
  




  @Deprecated private void addAuthentication(AbstractHttpMessage httpMessage)
  {
    httpMessage.setHeader(
        Constants.HTTP_BASIC_AUTH_HEADER_NAME,
        Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX +
            encode(userService.getCurrentUser().getUsername() + ":" +
            userService.getCurrentUser().getPassword())
    );
  }

  private String encode(String namePassword)
  {
    if (namePassword == null)
    {
      return null;
    }

    return new String(Base64.encodeBase64(namePassword.getBytes()));
  }



  static class MaxId {
      Long maxId = 0L;

      public MaxId(Long maxId) {
         this.maxId = maxId;
      }

      public Long maxId() {
         return maxId++;
      }
   }

}
