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
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UIButton;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.XmlParserException;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.UserService;
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
   private static final Logger logger = Logger.getLogger(ResourceServiceImpl.class);

   /** The configuration. */
   private Configuration configuration;

   /** The device command service. */
   private DeviceCommandService deviceCommandService;

   /** The event id. */
   private long eventId;

   /** The user service. */
   private UserService userService;
   
   /** The device macro service. */
   private DeviceMacroService deviceMacroService;

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#downloadZipResource(java.lang.String, java.lang.String,
    * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   public String downloadZipResource(long maxId, List<Activity> activities) {
      String controllerXmlContent = getControllerXmlContent(maxId, activities);
      String panelXmlContent = getPanelXmlContent(activities);
      String sectionIds = getSectionIds(activities);
      String activitiesJson = getActivitiesJson(activities);
      String userId = String.valueOf(userService.getAccount().getUser().getOid());

      PathConfig pathConfig = PathConfig.getInstance(userId, configuration);
      File sessionFolder = new File(pathConfig.userFolder());
      if (!sessionFolder.exists()) {
         sessionFolder.mkdirs();
      }
      File iphoneXMLFile = new File(pathConfig.iPhoneXmlFilePath());
      File controllerXMLFile = new File(pathConfig.controllerXmlFilePath());
      File lircdFile = new File(pathConfig.lircFilePath());
      File dotImport = new File(pathConfig.dotImportFilePath());

      String newIphoneXML = IphoneXmlParser.parserXML(new File(getClass().getResource(configuration.getIphoneXsdPath()).getPath()), panelXmlContent, sessionFolder);

      try {
         FileUtilsExt.deleteQuietly(iphoneXMLFile);
         FileUtilsExt.deleteQuietly(controllerXMLFile);
         FileUtilsExt.deleteQuietly(lircdFile);
         FileUtilsExt.deleteQuietly(dotImport);

         FileUtilsExt.writeStringToFile(iphoneXMLFile, newIphoneXML);
         FileUtilsExt.writeStringToFile(controllerXMLFile, controllerXmlContent);
         FileUtilsExt.writeStringToFile(dotImport, activitiesJson);

         if (sectionIds != "") {
            FileUtils.copyURLToFile(buildLircRESTUrl(configuration.getBeehiveLircdConfRESTUrl(), sectionIds), lircdFile);
         } else {
            FileUtilsExt.writeStringToFile(lircdFile, "");
         }
      } catch (IOException e) {
         logger.error("Compress zip file occur IOException", e);
         throw new FileOperationException("Compress zip file occur IOException", e);
      }

      File zipFile = compressFilesToZip(sessionFolder.listFiles(), pathConfig.openremoteZipFilePath());
      return pathConfig.getZipUrl() + zipFile.getName();
   }

   /**
    * Gets the activities json.
    * 
    * @param activities the activities
    * 
    * @return the activities json
    */
   private String getActivitiesJson(List<Activity> activities) {
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
    * @param RESTAPIUrl the rESTAPI url
    * @param ids the ids
    * 
    * @return the uRL
    */
   private URL buildLircRESTUrl(String RESTAPIUrl, String ids) {
      URL lircUrl;
      try {
         lircUrl = new URL(RESTAPIUrl + "?ids=" + ids);
      } catch (MalformedURLException e) {
         logger.error("Lirc file url is invalid", e);
         throw new IllegalArgumentException("Lirc file url is invalid", e);
      }
      return lircUrl;
   }
   
   @SuppressWarnings({ "unchecked" })
   public MultipartFile getMultipartFileFromRequest(HttpServletRequest request, String fileFieldName) {
      MultipartFile multipartFile = null;
      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      List items = null;
      try {
         items = upload.parseRequest(request);
      } catch (FileUploadException e) {
         logger.error("get InputStream from httpServletRequest error.", e);
         e.printStackTrace();
      }
      if (items == null) {
         return null;
      }
      Iterator it = items.iterator();
      FileItem fileItem = null;
      while (it.hasNext()) {
         fileItem = (FileItem) it.next();
         if (!fileItem.isFormField() && fileFieldName != null && fileFieldName.equals(fileItem.getFieldName())) {
            break;
         }
      }
      if(fileItem != null){
         multipartFile = new CommonsMultipartFile(fileItem);
      }
      return multipartFile;
//      InputStream fileItemInputStream = null;
//      try {
//         fileItemInputStream =  (fileItem == null) ? null : fileItem.getInputStream();
//      } catch (IOException e) {
//         logger.error("get InputStream from httpServletRequest error.", e);
//         e.printStackTrace();
//      } finally {
//         return fileItemInputStream;
//      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#getIrbFileFromZip(java.io.InputStream, java.lang.String)
    */
   public String getDotImportFileForRender(InputStream inputStream) {
      String userId = String.valueOf(userService.getAccount().getUser().getOid());
      
      File tmpDir = new File(PathConfig.getInstance(userId, configuration).userFolder());
      if (tmpDir.exists() && tmpDir.isDirectory()) {
         try {
            FileUtils.deleteDirectory(tmpDir);
         } catch (IOException e) {
            logger.error("Delete temp dir Occur IOException", e);
            throw new FileOperationException("Delete temp dir Occur IOException", e);
         }
      }
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
            }

         }
      } catch (IOException e) {
         logger.error("Get import file from zip file Occur IOException", e);
         throw new FileOperationException("Get import file from zip file Occur IOException", e);
      } finally {
         try {
            zipInputStream.closeEntry();
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            logger.error("Clean Resource used occured IOException when import a file", e);
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

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#uploadImage(java.io.InputStream, java.lang.String,
    * java.lang.String)
    */
   public File uploadImage(InputStream inputStream, String fileName, String userId) {
      File file = new File(PathConfig.getInstance(userId, configuration).userFolder() + File.separator + fileName);
      FileOutputStream fileOutputStream = null;
      try {
         File dir = file.getParentFile();
         if (dir.exists() == false) {
            dir.mkdirs();
         }
         FileUtils.touch(file);
         fileOutputStream = new FileOutputStream(file);
         IOUtils.copy(inputStream, fileOutputStream);
      } catch (IOException e) {
         logger.error("Save uploaded image to file occur IOException.", e);
         throw new FileOperationException("Save uploaded image to file occur IOException.", e);
      } finally {
         try {
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            logger.error("Close FileOutputStream Occur IOException while save a uploaded image.", e);
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
    * @param activityList the activity list
    * 
    * @return the controller xml content
    */
   private String getControllerXmlContent(long maxId, List<Activity> activityList) {
      this.eventId = maxId + 1;
      ProtocolEventContainer protocolEventContainer = new ProtocolEventContainer();
      StringBuffer controllerXml = new StringBuffer();
      controllerXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      controllerXml
            .append("<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openremote.org http://www.openremote.org/schemas/controller.xsd\">\n");
      controllerXml.append(getButtonsXmlContent(activityList, protocolEventContainer));
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
   private String getButtonsXmlContent(List<Activity> activityList, ProtocolEventContainer protocolEventContainer) {
      StringBuffer uiButtonsXml = new StringBuffer();
      for (Activity activity : activityList) {
         for (Screen screen : activity.getScreens()) {
            uiButtonsXml.append("  <buttons>\n");
            for (UIButton btn : screen.getButtons()) {
               uiButtonsXml.append(getButtonXmlContent(btn, protocolEventContainer));
            }
            uiButtonsXml.append("  </buttons>\n");
         }
      }
      return uiButtonsXml.toString();
   }

   /**
    * Gets the events from button.
    * 
    * @param uiButton the ui button
    * @param protocolEventContainer the protocol event container
    * 
    * @return the events from button
    */
   private String getButtonXmlContent(UIButton uiButton, ProtocolEventContainer protocolEventContainer) {
      UICommand uiCommand = uiButton.getUiCommand();
      if (uiCommand instanceof DeviceMacroItem) {
         List<UIButtonEvent> uiButtonEventList = getEventsOfDeviceMacroItem((DeviceMacroItem) uiCommand, protocolEventContainer);
         return generateButtonXmlString(uiButton.getOid(), uiButtonEventList);
      } else {
         return "";
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
         uiButtonEvent.setProtocolType(protocolType);
         for (ProtocolAttr protocolAttr : protocolAttrs) {
            uiButtonEvent.getProtocolAttrs().put(protocolAttr.getName(), protocolAttr.getValue());
         }
         protocolEventContainer.addUIButtonEvent(uiButtonEvent);
         oneUIButtonEventList.add(uiButtonEvent);
      } else if (deviceMacroItem instanceof DeviceMacroRef) {
         DeviceMacro deviceMacro = ((DeviceMacroRef) deviceMacroItem).getTargetDeviceMacro();
         deviceMacro = deviceMacroService.loadById(deviceMacro.getOid());
         for (DeviceMacroItem tempDeviceMacroItem : deviceMacro.getDeviceMacroItems()) {
            oneUIButtonEventList.addAll(getEventsOfDeviceMacroItem(tempDeviceMacroItem, protocolEventContainer));
         }
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
   private String generateButtonXmlString(long uiButtonId, List<UIButtonEvent> uiButtonEventList) {
      StringBuffer buttonXml = new StringBuffer();
      buttonXml.append("    <button id=\"" + uiButtonId + "\">\n");
      for (UIButtonEvent uiButtonEvent : uiButtonEventList) {
         buttonXml.append("      <event>");
         buttonXml.append(uiButtonEvent.getId());
         buttonXml.append("</event>\n");
      }
      buttonXml.append("    </button>\n");
      return buttonXml.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.openremote.modeler.service.ResourceService#getPanelXmlContent()
    */
   /**
    * Gets the panel xml content.
    * 
    * @param activityList the activity list
    * 
    * @return the panel xml content
    */
   private String getPanelXmlContent(List<Activity> activityList) {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      xmlContent
            .append("<openremote xmlns=\"http://www.openremote.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openremote.org http://www.openremote.org/schemas/iphone.xsd\">\n");
      for (Activity activity : activityList) {
         xmlContent.append("  <activity id=\"" + activity.getOid() + "\" name=\"" + activity.getName() + "\">\n");
         for (Screen screen : activity.getScreens()) {
            xmlContent.append("    <screen id=\"" + screen.getOid() + "\" name=\"" + screen.getName() + "\" row=\""
                  + screen.getRowCount() + "\" col=\"" + screen.getColumnCount() + "\">\n");
            xmlContent.append("      <buttons>\n");
            for (UIButton btn : screen.getButtons()) {
               xmlContent.append("        <button id=\"" + btn.getOid() + "\" label=\"" + btn.getLabel() + "\" x=\""
                     + btn.getPosition().getPosX() + "\" y=\"" + btn.getPosition().getPosY() + "\" width=\""
                     + btn.getWidth() + "\" height=\"" + btn.getHeight() + "\"");
               if(btn.getIcon() != null){
                  xmlContent.append(" icon=\""+btn.getIcon()+"\"");
               }
               xmlContent.append(" />\n");
            }
            xmlContent.append("      </buttons>\n");
            xmlContent.append("    </screen>\n");
         }
         xmlContent.append("  </activity>\n");
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
    * @param activityList the activity list
    * 
    * @return the section ids
    */
   private String getSectionIds(List<Activity> activityList) {
      Set<String> sectionIds = new HashSet<String>();
      for (Activity activity : activityList) {
         for (Screen screen : activity.getScreens()) {
            List<UIButton> screenButtons = screen.getButtons();
            for (int i = 0; i < screenButtons.size(); i++) {
               UIButton btn = screenButtons.get(i);
               if (btn.getUiCommand() instanceof DeviceMacroItem) {
                  DeviceMacroItem deviceMacroItem = (DeviceMacroItem) btn.getUiCommand();
                  sectionIds.addAll(getDevcieMacroItemSectionIds(deviceMacroItem));
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
    * Gets the user service.
    * 
    * @return the user service
    */
   public UserService getUserService() {
      return userService;
   }

   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   /**
    * Sets the device macro service.
    * 
    * @param deviceMacroService the new device macro service
    */
   public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
      this.deviceMacroService = deviceMacroService;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.service.ResourceService#getRelativeResourcePath(java.lang.String, java.lang.String)
    */
   public String getRelativeResourcePath(String sessionId, String fileName) {
      return PathConfig.getInstance(sessionId, configuration).getRelativeResourcePath(fileName);
   }
}
