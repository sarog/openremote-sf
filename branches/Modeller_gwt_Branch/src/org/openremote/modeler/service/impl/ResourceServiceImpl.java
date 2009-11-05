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
package org.openremote.modeler.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.UIButtonEvent;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Grid;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.domain.control.UIButton;
import org.openremote.modeler.domain.control.UIControl;
import org.openremote.modeler.domain.control.UISwitch;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.XmlParserException;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.utils.FileUtilsExt;
import org.openremote.modeler.utils.IphoneXmlParser;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.ProtocolEventContainer;
import org.openremote.modeler.utils.StringUtils;
import org.openremote.modeler.utils.ZipUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * The Class ResourceServiceImpl.
 * 
 * @author Allen, Handy
 */
public class ResourceServiceImpl implements ResourceService {

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


   /**
    * {@inheritDoc}
    */
   public String downloadZipResource(long maxId, String sessionId, List<Group> groups, List<UIScreen> screens) {
      String controllerXmlContent = getControllerXmlContent(maxId, screens);
      String panelXmlContent = getPanelXmlContent(groups, screens);
      String sectionIds = getSectionIds(screens);

      replaceUrl(screens, sessionId);
//      String activitiesJson = getActivitiesJson(activities);

      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File sessionFolder = new File(pathConfig.userFolder(sessionId));
      if (!sessionFolder.exists()) {
         sessionFolder.mkdirs();
      }
      File panelXMLFile = new File(pathConfig.panelXmlFilePath(sessionId));
      File controllerXMLFile = new File(pathConfig.controllerXmlFilePath(sessionId));
      File lircdFile = new File(pathConfig.lircFilePath(sessionId));
//      File dotImport = new File(pathConfig.dotImportFilePath(sessionId));

      String newIphoneXML = IphoneXmlParser.parserXML(new File(getClass().getResource(configuration.getIphoneXsdPath()).getPath()), 
            panelXmlContent, sessionFolder);
      
      try {
         FileUtilsExt.deleteQuietly(panelXMLFile);
         FileUtilsExt.deleteQuietly(controllerXMLFile);
         FileUtilsExt.deleteQuietly(lircdFile);
//         FileUtilsExt.deleteQuietly(dotImport);

         FileUtilsExt.writeStringToFile(panelXMLFile, newIphoneXML);
         FileUtilsExt.writeStringToFile(controllerXMLFile, controllerXmlContent);
//         FileUtilsExt.writeStringToFile(dotImport, activitiesJson);

         if (sectionIds != "") {
            FileUtils.copyURLToFile(buildLircRESTUrl(configuration.getBeehiveLircdConfRESTUrl(), sectionIds), lircdFile);
         } else {
            FileUtilsExt.writeStringToFile(lircdFile, "");
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
    * @param activities the activities
    * @param sessionId the user id
    */
   private void replaceUrl(List<UIScreen> screens, String sessionId) {
      String rerlativeSessionFolderPath = PathConfig.getInstance(configuration).getRelativeSessionFolderPath(sessionId);
         for (UIScreen screen : screens) {
            if (screen.isAbsoluteLayout()) {
               for (Absolute absolute : screen.getAbsolutes()) {
                  absolute.getUiControl().transImagePathToRelative(rerlativeSessionFolderPath);
               }
            } else {
               for (Cell cell : screen.getGrid().getCells()) {
                  cell.getUiControl().transImagePathToRelative(rerlativeSessionFolderPath);
               }
            }
      }
   }

   /**
    * Gets the activities json.
    * 
    * @param activities the activities
    * 
    * @return the activities json
    */
   public String getActivitiesJson(List<Activity> activities) {
      try {
         String [] includedPropertyNames = {"screens", "screens.buttons", "screens.buttons.uiCommand"};
         String [] excludePropertyNames = {};
         String activitiesJson = JsonGenerator.serializerObjectInclude(activities, includedPropertyNames, excludePropertyNames);
         return activitiesJson;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   /**
    * Compress files to zip.
    * 
    * @param files the files
    * @param zipFilePath the zip file path
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
    * @param restAPIUrl the rESTAPI url
    * @param ids the ids
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
    * @param zipInputStream the zip input stream
    * @param zipEntry the zip entry
    * @param xmlName the xml name
    * 
    * @return true, if successful
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   private boolean checkXML(ZipInputStream zipInputStream, ZipEntry zipEntry, String xmlName) throws IOException {
      if (zipEntry.getName().equals(xmlName + ".xml")) {
         String xsdRelativePath = "iphone".equals(xmlName) ? configuration.getIphoneXsdPath() : configuration.getControllerXsdPath();
         String xsdPath = getClass().getResource(xsdRelativePath).getPath();
         if (!IphoneXmlParser.checkXmlSchema(xsdPath, IOUtils.toString(zipInputStream))) {
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
    * @param maxId the max id
    * @param screenList the activity list
    * 
    * @return the controller xml content
    */
   private String getControllerXmlContent(long maxId, List<UIScreen> screenList) {
      this.eventId = maxId + 1;
      ProtocolEventContainer protocolEventContainer = new ProtocolEventContainer();
      StringBuffer controllerXml = new StringBuffer();
      controllerXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      controllerXml.append("<openremote xmlns=\"http://www.openremote.org\" " 
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " 
            + "xsi:schemaLocation=\"http://www.openremote.org " 
            + "http://www.openremote.org/schemas/controller.xsd\">\n");
      controllerXml.append(getControlsXmlContent(screenList, protocolEventContainer));
      controllerXml.append(protocolEventContainer.generateUIButtonEventsXml());
      controllerXml.append("</openremote>");
      return controllerXml.toString();
   }

   /**
    * Gets the buttons xml content.
    * 
    * @param activityList the activity list
    * @param protocolEventContainer the protocol event container
    * 
    * @return the buttons xml content
    */
   private String getControlsXmlContent(List<UIScreen> screenList, ProtocolEventContainer protocolEventContainer) {
      StringBuffer uiControlsXml = new StringBuffer();
      uiControlsXml.append("  <controls>\n");
      for (UIScreen screen : screenList) {
         if (screen.isAbsoluteLayout()) {
            for (Absolute absolute : screen.getAbsolutes()) {
               uiControlsXml.append(getControlXmlContent(absolute.getUiControl(), protocolEventContainer));
            }
         } else {
            for (Cell cell : screen.getGrid().getCells()) {
               uiControlsXml.append(getControlXmlContent(cell.getUiControl(), protocolEventContainer));
            }
         }
      }
      uiControlsXml.append("  </controls>\n");
      return uiControlsXml.toString();
   }

   /**
    * Gets the events from button.
    * 
    * @param uiButton the ui button
    * @param protocolEventContainer the protocol event container
    * 
    * @return the events from button
    */
   private String getControlXmlContent(UIControl uiControl, ProtocolEventContainer protocolEventContainer) {
      StringBuffer uiControlXml = new StringBuffer();
      if (uiControl instanceof UIButton) {
         uiControlXml.append("    <button id=\"" + uiControl.getOid() + "\">\n");
         UICommand uiCommand = ((UIButton)uiControl).getUiCommand();
         generateCommandXmlString(protocolEventContainer, uiControlXml, uiCommand);
         uiControlXml.append("    </button>\n");
      } else if (uiControl instanceof UISwitch) {
         uiControlXml.append("    <switch id=\"" + uiControl.getOid() + "\">\n");
         UISwitch uiSwitch = (UISwitch)uiControl;
         uiControlXml.append("     <on>\n");
         generateCommandXmlString(protocolEventContainer, uiControlXml, uiSwitch.getOnCommand());
         uiControlXml.append("     </on>\n");
         uiControlXml.append("     <off>\n");
         generateCommandXmlString(protocolEventContainer, uiControlXml, uiSwitch.getOffCommand());
         uiControlXml.append("     </off>\n");
         uiControlXml.append("     <status>\n");
         generateCommandXmlString(protocolEventContainer, uiControlXml, uiSwitch.getStatusCommand());
         uiControlXml.append("     </status>\n");
         uiControlXml.append("    <switch>\n");
      }
      return uiControlXml.toString();
   }

   /**
    * @param protocolEventContainer
    * @param uiControlXml
    * @param uiCommand
    */
   private void generateCommandXmlString(ProtocolEventContainer protocolEventContainer, StringBuffer uiControlXml,
         UICommand uiCommand) {
      if (uiCommand instanceof DeviceMacroItem) {
         List<UIButtonEvent> uiButtonEventList = getEventsOfDeviceMacroItem((DeviceMacroItem) uiCommand, protocolEventContainer);
         uiControlXml.append(generateButtonXmlString(uiButtonEventList));
      }
   }

   /**
    * Gets the controller xml segment content.
    * 
    * @param deviceMacroItem the device command item
    * @param protocolEventContainer the protocol event container
    * 
    * @return the controller xml segment content
    */
   private List<UIButtonEvent> getEventsOfDeviceMacroItem(DeviceMacroItem deviceMacroItem, ProtocolEventContainer protocolEventContainer) {
      List<UIButtonEvent> oneUIButtonEventList = new ArrayList<UIButtonEvent>();
      if (deviceMacroItem instanceof DeviceCommandRef) {
         DeviceCommand deviceCommand = deviceCommandService.loadById(((DeviceCommandRef) deviceMacroItem).getDeviceCommand().getOid());
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
      } else if (deviceMacroItem instanceof DeviceMacroRef) {
         DeviceMacro deviceMacro = ((DeviceMacroRef) deviceMacroItem).getTargetDeviceMacro();
         deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
         for (DeviceMacroItem tempDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
            oneUIButtonEventList.addAll(getEventsOfDeviceMacroItem(tempDeviceMacroItem, protocolEventContainer));
         }
      } else if(deviceMacroItem instanceof CommandDelay){
         CommandDelay delay = (CommandDelay)deviceMacroItem;
         UIButtonEvent uiButtonEvent = new UIButtonEvent();
         uiButtonEvent.setId(this.eventId++);
         uiButtonEvent.setDelay(delay.getDelaySecond());
         oneUIButtonEventList.add(uiButtonEvent);
      }
      return oneUIButtonEventList;
   }

   /**
    * Generate button xml string.
    * 
    * @param uiButtonId the ui button id
    * @param uiButtonEventList the ui button event list
    * 
    * @return the string
    */
   private String generateButtonXmlString(List<UIButtonEvent> uiButtonEventList) {
      StringBuffer buttonXml = new StringBuffer();
      for (UIButtonEvent uiButtonEvent : uiButtonEventList) {
         if ("".equals(uiButtonEvent.getDelay())) {
            buttonXml.append("      <command ref=\"" + uiButtonEvent.getId() + "\">\n");
         } else {
            buttonXml.append("      <delay>");
            buttonXml.append(uiButtonEvent.getDelay());
            buttonXml.append("</delay>\n");
         }
      }
      return buttonXml.toString();
   }


   /**
    * Gets the panel xml content.
    * 
    * @param activityList the activity list
    * 
    * @return the panel xml content
    */
   private String getPanelXmlContent(List<Group> groups, List<UIScreen> screens) {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      xmlContent.append("<openremote xmlns=\"http://www.openremote.org\" " 
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " 
            + "xsi:schemaLocation=\"http://www.openremote.org " 
            + "http://www.openremote.org/schemas/controller.xsd\">\n");
      xmlContent.append("  <screens>\n");
      for (UIScreen screen : screens) {
         xmlContent.append("    <screen id=\"" + screen.getOid() + "\" name=\"" + screen.getName() + "\"");
         if (!"".equals(screen.getBackground())) {
            xmlContent.append(" background=\"" + screen.getBackground() + "\"");
         }
         xmlContent.append(">\n");
         if (screen.isAbsoluteLayout()) {
            for (Absolute absolute : screen.getAbsolutes()) {
               xmlContent.append("      <absolute left=\"" + absolute.getLeft() + "\" top=\"" + absolute.getTop()
                     + "\" width=\"" + absolute.getWidth() + "\" height=\"" + absolute.getHeight() + "\">\n");
               xmlContent.append(absolute.getUiControl().getPanelXml());
               xmlContent.append("      </absolute>\n");
            }
         } else {
            Grid grid = screen.getGrid();
            xmlContent.append("      <grid left=\"" + grid.getLeft() + "\" top=\"" + grid.getTop() + "\" width=\""
                  + grid.getWidth() + "\" height=\"" + grid.getHeight() + "\" rows=\"" + grid.getRowCount()
                  + "\" cols=\"" + grid.getColumnCount() + "\">\n");
            for (Cell cell : grid.getCells()) {
               xmlContent.append("       <cell x=\"" + cell.getPosX() + "\" y=\"" + cell.getPosY() + "\" rowspan=\""
                     + cell.getRowspan() + "\" colspan=\"" + cell.getColspan() + "\">\n");
               xmlContent.append(cell.getUiControl().getPanelXml());
               xmlContent.append("       </cell>\n");
            }
            xmlContent.append("      </grid>\n");
         }
         xmlContent.append("    </screen>\n");
      }
      xmlContent.append("  </screens>\n");
      if (groups.size() > 0) {
         xmlContent.append("  <groups>\n");
         for (Group group : groups) {
            xmlContent.append("    <group id=\"" + group.getOid() + "\" name=\"" + group.getName() + "\">\n");
            for (ScreenRef screenRef : group.getScreenRefs()) {
               xmlContent.append("      <screenRef id=\"" + screenRef.getScreenId() + "\" />\n");
            }
            xmlContent.append("    </group>\n");
            
         }
         xmlContent.append("  </groups>\n");
      }
      xmlContent.append("</openremote>");
      return xmlContent.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#getSectionIds()
    */
   /**
    * Gets the section ids.
    * 
    * @param screenList the activity list
    * 
    * @return the section ids
    */
   private String getSectionIds(List<UIScreen> screenList) {
      Set<String> sectionIds = new HashSet<String>();
      for (UIScreen screen : screenList) {
         if (screen.isAbsoluteLayout()) {
            for (Absolute absolute : screen.getAbsolutes()) {
               for (UICommand command : absolute.getUiControl().getCommands()) {
                  if (command instanceof DeviceMacroItem) {
                     sectionIds.addAll(getDevcieMacroItemSectionIds((DeviceMacroItem)command));
                  }
               }
            }
         } else {
            for (Cell cell : screen.getGrid().getCells()) {
               for (UICommand command : cell.getUiControl().getCommands()) {
                  if (command instanceof DeviceMacroItem) {
                     sectionIds.addAll(getDevcieMacroItemSectionIds((DeviceMacroItem)command));
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
    * Gets the devcie macro item section ids.
    * 
    * @param deviceMacroItem the device macro item
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
    * @param configuration the new configuration
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
    * @param deviceCommandService the new device command service
    */
   public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
      this.deviceCommandService = deviceCommandService;
   }

   /**
    * Sets the device macro service.
    * 
    * @param deviceMacroService the new device macro service
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

   @Override
   public String getGroupsJson(List<Group> groups) {
      try {
         String [] includedPropertyNames = {"screenRefs"};
         String [] excludePropertyNames = {};
         String groupsJson = JsonGenerator.serializerObjectInclude(groups, includedPropertyNames, excludePropertyNames);
         return groupsJson;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   @Override
   public String getScreensJson(List<UIScreen> screens) {
      try {
         String [] includedPropertyNames = {"absolutes", "absolutes.uiCommand", "grid", "grid.cells", "grid.cells.uiCommand"};
         String [] excludePropertyNames = {"absolutes.uiControl.panelXml", "grid.cells.uiControl.panelXml"};
         String groupsJson = JsonGenerator.serializerObjectInclude(screens, includedPropertyNames, excludePropertyNames);
         return groupsJson;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }
}
