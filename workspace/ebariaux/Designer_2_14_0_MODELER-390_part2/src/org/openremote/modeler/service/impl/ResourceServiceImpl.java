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
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Base64;
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
import org.apache.http.message.AbstractHttpMessage;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeXmlReference;
import org.hibernate.ObjectNotFoundException;
import org.openremote.modeler.cache.CacheOperationException;
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.Command;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Panel.UIComponentOperation;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPair.OrientationType;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.SensorLinkOwner;
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
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.IllegalRestUrlException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.exception.XmlExportException;
import org.openremote.modeler.logging.AdministratorAlert;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.GraphicalAssetDTO;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsWithChildrenDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.UICommandDTO;
import org.openremote.modeler.utils.FileUtilsExt;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.ProtocolCommandContainer;
import org.openremote.modeler.utils.UIComponentBox;
import org.openremote.modeler.utils.XmlParser;
import org.openremote.modeler.utils.ZipUtils;
import org.springframework.transaction.annotation.Transactional;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * TODO : this class is a total garbage bin -- everything and the kitchen sink is thrown in. Blah.
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


  private Configuration configuration;

  private DeviceCommandService deviceCommandService;

  private DeviceMacroService deviceMacroService;
  private DeviceService deviceService;
  
  private SensorService sensorService;
  private SliderService sliderService;
  private SwitchService switchService;

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

  private File storeAsLocalTemporaryFile(InputStream inputStream) {
    File temporaryFile = null;
    FileOutputStream fileOutputStream = null;
	try {
	  temporaryFile = File.createTempFile("import_", ".zip", new File(PathConfig.getInstance(configuration).tempFolder()));
      fileOutputStream = new FileOutputStream(temporaryFile);
	  IOUtils.copy(inputStream, fileOutputStream);
    } catch (IOException e) {
    	throw new FileOperationException("Error in storing zip import file", e);
	} finally {
	  if (fileOutputStream != null) {
        try {
		  fileOutputStream.close();
		} catch (IOException e) {
          serviceLog.warn("Failed to close import file resources", e);
		}
	  }
	}
	
	return temporaryFile;
  }
  
   @Deprecated @Override @Transactional public List<DeviceDTO> getDotImportFileForRender(String sessionId, InputStream inputStream) throws NetworkException, ConfigurationException, CacheOperationException {
	 // Store the upload zip file locally before processing
	 File importFile = storeAsLocalTemporaryFile(inputStream);

	System.out.println("local import file is " + importFile.getAbsolutePath());

	 
    // TODO: try to revert changes on all loadAll methods now that this method is transactional
     
    // TODO: delete of UI is not working at all
    // -> client side must be instructed to reload

    // First part of import is getting rid of what's currently in the account

    // UI
    initResources(new ArrayList<Panel>(), 0);
    saveResourcesToBeehive(new ArrayList<Panel>());
     
    // Clean images
    // TODO

    // Remove all building modeler information (except for configuration)
    Account account = userService.getAccount();
    List<Device> allDevices = deviceService.loadAll(account);
    for (Device d : allDevices) {
      deviceService.deleteDevice(d.getOid());
    }
    // TODO: macro

    LocalFileCache lfc = new LocalFileCache(configuration, userService.getCurrentUser());
    lfc.replace(importFile);

    List <DeviceDTO> importedDeviceDTOs = new ArrayList<DeviceDTO>();


    // TODO: try get rid of hibernate extension on save also -> seems to work
        
        
    XStream xstream = new XStream(new StaxDriver());
    Map<String, Object> map = (Map<String, Object>) xstream.fromXML(new File(PathConfig.getInstance(configuration).buildingModelerXmlFilePath(account)));
    Collection<DeviceDetailsWithChildrenDTO> devices = (Collection<DeviceDetailsWithChildrenDTO>)map.get("devices");
    
    List<Device> importedDevices = new ArrayList<Device>();
    
    // DTOs restored have oid but we don't care, they're not taken into account when saving new devices
    for (DeviceDetailsWithChildrenDTO dev : devices) {

      // The archived graph has DTOReferences with id, as it originally came from objects in DB.
      // Must iterate all DTOReferences, replacing ids with dto.
      dev.replaceIdWithDTOInReferences();

      importedDevices.add(deviceService.saveNewDeviceWithChildren(userService.getAccount(), dev, dev.getDeviceCommands(), dev.getSensors(), dev.getSwitches(), dev.getSliders()));
    }
    
    Map<Long, Long> devicesOldOidToNewOid = new HashMap<Long, Long>();
    Map<Long, Long> commandsOldOidToNewOid = new HashMap<Long, Long>();
    Map<Long, Long> sensorsOldOidToNewOid = new HashMap<Long, Long>();
    Map<Long, Long> switchesOldOidToNewOid = new HashMap<Long, Long>();
    Map<Long, Long> slidersOldOidToNewOid = new HashMap<Long, Long>();
    
    // Domain objects have been created and saved with a new id
    // During this process, old id has been saved as transient info
    // Create lookup map from originalId to new one
    for (Device dev : importedDevices) {
      devicesOldOidToNewOid.put((Long)dev.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), dev.getOid());
      
      for (DeviceCommand dc : dev.getDeviceCommands()) {
        commandsOldOidToNewOid.put((Long)dev.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), dc.getOid());
      }
      for (Sensor s : dev.getSensors()) {
        sensorsOldOidToNewOid.put((Long)dev.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), s.getOid());
      }
      for (Switch s : dev.getSwitchs()) {
        switchesOldOidToNewOid.put((Long)dev.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), s.getOid());
      }
      for (Slider s : dev.getSliders()) {
        slidersOldOidToNewOid.put((Long)dev.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), s.getOid());
      }
      
      importedDeviceDTOs.add(new DeviceDTO(dev.getOid(), dev.getDisplayName()));
    }
    
    
    
    // TODO: what about macro order ???
    // If one macro depends on another, the second should be imported first !
    
    Collection<MacroDetailsDTO> macros = (Collection<MacroDetailsDTO>)map.get("macros");
    for (MacroDetailsDTO m : macros) {
      
      // Replace old with new command ids
      for (MacroItemDetailsDTO item : m.getItems()) {
        item.getDto().setId(commandsOldOidToNewOid.get(item.getDto().getId()));
      }
      
      deviceMacroService.saveNewMacro(m);
    }
                
                
                
    DesignerState state = new DesignerState(configuration, userService.getCurrentUser());
    state.restore(false);

    // TODO: walk the just restored panels hierarchy and adapt all DTO references to the newly saved ones.
    // For now, this code will crash if panels are restored that reference any building modeler objects.

    PanelsAndMaxOid panels = state.transformToPanelsAndMaxOid();
      
    initResources(panels.getPanels(), panels.getMaxOid());
    saveResourcesToBeehive(panels.getPanels());

    return importedDeviceDTOs;
  }


  /**
   * TODO
   *
   * @deprecated looks unused
   */
  @Deprecated @Override public File uploadImage(InputStream inputStream, String fileName, String sessionId)
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
  //
  //  @deprecated Should eventually go away, with a direct API in
  //  {@link org.openremote.modeler.beehive.BeehiveService} to upload images to account (and not
  //  upload as part of save-cycle). See MODELER-292.
  //
  //
  @Deprecated @Override public File uploadImage(InputStream inputStream, String fileName)
  {
    File file = new File(
        PathConfig.getInstance(configuration).userFolder(userService.getAccount()) +
        File.separator + fileName
    );

    return uploadFile(inputStream, file);
  }

  @Override public List<GraphicalAssetDTO>getUserImagesURLs() {
    File userFolder = new File(PathConfig.getInstance(configuration).userFolder(userService.getAccount()));
    String[] imageFiles = userFolder.list(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        String lowercaseName = name.toLowerCase();
        return (lowercaseName.endsWith("png") || lowercaseName.endsWith("gif") || lowercaseName.endsWith("jpg") || lowercaseName.endsWith("jpeg"));
      }
    });
    List<GraphicalAssetDTO> assets = new ArrayList<GraphicalAssetDTO>();
    if (imageFiles != null) { // Seems we sometimes get a null (got it when tomcat was still starting)
      for (int i = 0; i < imageFiles.length; i++) {
        assets.add(new GraphicalAssetDTO(imageFiles[i], getRelativeResourcePathByCurrentAccount(imageFiles[i])));
      }
    }
    return assets;
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

      String originalFileName = file.getName();

      // First get rid of "invalid" characters in filename
      String escapedChar = "[ \\+\\-\\*%\\!\\(\\\"')_#;/?:&;=$,#<>]";
      originalFileName = originalFileName.replaceAll(escapedChar, "");
      file = new File(dir.getAbsolutePath() + File.separator + originalFileName);

      // Don't replace an existing file, add "index" if required to not have a name clash
      String extension = FilenameUtils.getExtension(originalFileName);
      String originalNameWithoutExtension = originalFileName.replace("." + extension, "");
      int index = 0;
      while (file.exists()) {
        file = new File(dir.getAbsolutePath() + File.separator + originalNameWithoutExtension + "." + Integer.toString(++index) + "." + extension);
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
        if (sectionId != null) {
         sectionIdsSB.append(sectionId);
         if (i < sectionIds.size() - 1) {
            sectionIdsSB.append(",");
         }
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

   public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
   }

   public void setSliderService(SliderService sliderService) {
     this.sliderService = sliderService;
   }

   public void setSwitchService(SwitchService switchService) {
     this.switchService = switchService;
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


   @Override @Transactional
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

      Map<String, Object> context = new HashMap<String, Object>();
      context.put("panels", panels);
      context.put("groups", groups);
      context.put("screens", screens);
      try {
        return mergeXMLTemplateIntoString(PANEL_XML_TEMPLATE, context);
      } catch (Exception e) {
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
      addDataBaseCommands(eventContainer, maxId);
      ProtocolContainer protocolContainer = ProtocolContainer.getInstance();

      Collection<Sensor> sensors = getAllSensorWithoutDuplicate(screens, maxId, dbSensors);

      Collection<UISwitch> switchs = (Collection<UISwitch>) uiComponentBox.getUIComponentsByType(UISwitch.class);
      Collection<UIComponent> buttons = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(UIButton.class);
      Collection<UIComponent> gestures = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(Gesture.class);
      Collection<UIComponent> uiSliders = (Collection<UIComponent>) uiComponentBox
            .getUIComponentsByType(UISlider.class);
      Collection<UIComponent> uiImages = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(UIImage.class);
      Collection<UIComponent> uiLabels = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(UILabel.class);
      Collection<UIComponent> colorPickers = (Collection<UIComponent>) uiComponentBox.getUIComponentsByType(ColorPicker.class);
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
      context.put("colorPickers", colorPickers);
      context.put("maxId", maxId);
      context.put("configs", configs);
      
      try {
        return mergeXMLTemplateIntoString(CONTROLLER_XML_TEMPLATE, context);
      } catch (Exception e) {
        throw new XmlExportException("Failed to read panel.xml", e);
      }
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

      try
      {
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
      }

      // TODO :
      //        strictly speaking this should be unnecessary if database schema has been configured
      //        to enforce correct referential integrity constraints -- this hasn't always been the
      //        case so catching the error here. Unfortunately there isn't much we can do in terms
      //        of recovery other than have the DBA step in.

      catch (ObjectNotFoundException e)
      {
          AdministratorAlert.getInstance(AdministratorAlert.Type.DATABASE).alert(
              "Database integrity error -- referencing an unknown entity: {0}, id: {1}, message: {2}",
              e, e.getEntityName(), e.getIdentifier(), e.getMessage()
          );

          //TODO: the wrong exception type, but it will get propagated back to user's browser

          throw new FileOperationException(
              "Save/Export failed due to database integrity error. This requires administrator intervention " +
              "to solve. Please avoid making any further changes to your account until this issue has been " +
              "resolved (the integrity offender: " + e.getEntityName() + ", id: " + e.getIdentifier() + ")."
          );
      }

       dbSensors.removeAll(duplicateDBSensors);
      // PATCH R3181 END --->8-------

       // MODELER-396
       
       // Validate there are no duplicate ids
       Set<Long> ids = new HashSet<Long>();
       
       // First in sensors from UI
       for (Sensor s : sensorWithoutDuplicate) {
         ids.add(s.getOid());
       }
       if (ids.size() != sensorWithoutDuplicate.size()) {
         AdministratorAlert.getInstance(AdministratorAlert.Type.DESIGNER_STATE).alert("Found sensors with same id but different data");
       }
       // Then in sensors from DB
       ids.clear();
       for (Sensor s : dbSensors) {
         ids.add(s.getOid());
       }
       if (ids.size() != dbSensors.size()) {
         AdministratorAlert.getInstance(AdministratorAlert.Type.DESIGNER_STATE).alert("Found sensors with same id but different data");
       }
       
       // Then combined
       for (Sensor s : sensorWithoutDuplicate) {
         ids.add(s.getOid());
       }
       if (ids.size() != sensorWithoutDuplicate.size() + dbSensors.size()) {
         AdministratorAlert.getInstance(AdministratorAlert.Type.DESIGNER_STATE).alert("Found sensors with same id but different data");
       }
       
       // MODELER-396 end

      /*
       * reset sensor oid, avoid duplicated id in export xml. make sure same sensors have same oid.
       */
       /* MODELER-396
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
      */
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

   /**
    * Iterates over all buttons in the design and when the name is null, replaces with empty string.
    * Having null name causes issue when template is created from screen.
    *
    * @param panels All panels in which to process the buttons
    */
   private void replaceNullNamesWithEmptyString(Collection<Panel> panels) {
  	 Panel.walkAllUIComponents(panels, new UIComponentOperation() {

			@Override
      public void execute(UIComponent component) {
				if (component instanceof UIButton) {
					UIButton uiButton = (UIButton) component;
					if (uiButton.getName() == null) {
						uiButton.setName("");
					}
				}
      }
  	 });
   }

  /**
   * Prepares the objects to be sent to client side by replacing references to entity beans with references to DTOs.
   * The inverse operation is performed by the resolveDTOReferences method before using the objects on the server side.
   *
   * @param panels
   */
  private void populateDTOReferences(Collection<Panel> panels) {
  	Panel.walkAllUIComponents(panels, new UIComponentOperation() {

  		@Override
      public void execute(UIComponent component) {
		    if (component instanceof SensorOwner) {
		      SensorOwner owner = (SensorOwner) component;
		      if (owner.getSensorDTO() == null && owner.getSensor() != null) {
		        Sensor sensor = sensorService.loadById(owner.getSensor().getOid());

						if (sensor != null) {
							owner.setSensorDTO(sensor.getSensorWithInfoDTO());
						}
						owner.setSensor(null);

						if (owner instanceof SensorLinkOwner) {
							((SensorLinkOwner) owner).getSensorLink().setSensor(null);
							((SensorLinkOwner) owner).getSensorLink().setSensorDTO(owner.getSensorDTO());
						}
			    }
			    if (component instanceof UISlider) {
			      UISlider uiSlider = (UISlider)component;
			      if (uiSlider.getSliderDTO() == null && uiSlider.getSlider() != null) {
			        // We must load slider because referenced sensor / command are not serialized, this reloads from DB
			        Slider slider = sliderService.loadById(uiSlider.getSlider().getOid());
			        if (slider != null) { // Just in case we have a dangling pointer
			          uiSlider.setSliderDTO(slider.getSliderWithInfoDTO());
			        }
			        uiSlider.setSlider(null);
			      }
			    }
			    if (component instanceof UISwitch) {
			      UISwitch uiSwitch = (UISwitch)component;
			      if (uiSwitch.getSwitchDTO() == null && uiSwitch.getSwitchCommand() != null) {
			        Switch switchBean = switchService.loadById(uiSwitch.getSwitchCommand().getOid());
			        if (switchBean != null) { // Just in case we have a dangling pointer
			          uiSwitch.setSwitchDTO(switchBean.getSwitchWithInfoDTO());
			        }
			        uiSwitch.setSwitchCommand(null);
			      }
			    }
			    if (component instanceof UIButton) {
			      UIButton uiButton = (UIButton)component;
			      if (uiButton.getUiCommandDTO() == null && uiButton.getUiCommand() != null) {
			        uiButton.setUiCommandDTO(createUiCommandDTO(uiButton.getUiCommand()));
			        uiButton.setUiCommand(null);
			      }
			    }
			    if (component instanceof ColorPicker) {
			      ColorPicker colorPicker = (ColorPicker)component;
			      if (colorPicker.getUiCommandDTO() == null && colorPicker.getUiCommand() != null) {
			        colorPicker.setUiCommandDTO(createUiCommandDTO(colorPicker.getUiCommand()));
			        colorPicker.setUiCommand(null);
			      }
			    }
			    if (component instanceof Gesture) {
			    	Gesture gesture = (Gesture)component;
			      if (gesture.getUiCommandDTO() == null && gesture.getUiCommand() != null) {
			        gesture.setUiCommandDTO(createUiCommandDTO(gesture.getUiCommand()));
			        gesture.setUiCommand(null);
			      }
			    }
			  }
  		}

		  private UICommandDTO createUiCommandDTO(UICommand uiCommand)
		  {
		    if (uiCommand instanceof DeviceCommandRef)
		    {
		      try
		      {
		        DeviceCommand dc = deviceCommandService.loadById(((DeviceCommandRef)uiCommand).getDeviceCommand().getOid());
		        return (dc != null)?new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()):null;
		      }
		      catch (ObjectNotFoundException e)
		      {
		        serviceLog.warn("Button is referencing inexistent command with id " + ((DeviceCommandRef)uiCommand).getDeviceCommand().getOid(), e);
		        return null;
		      }
		    }

		    else if (uiCommand instanceof DeviceMacroRef)
		    {
		      try
		      {
		        DeviceMacro targetMacro = ((DeviceMacroRef)uiCommand).getTargetDeviceMacro();

		        if (targetMacro != null)
		        {
		          long oid = targetMacro.getOid();

		          DeviceMacro dm = deviceMacroService.loadById(oid);

		          return (dm != null) ? new MacroDTO(dm.getOid(), dm.getDisplayName()) : null;
		        }

		        else
		        {
		          serviceLog.error("DeviceMacroRef had a null target device macro reference");

		          return null;
		        }
		      }

		      catch (ObjectNotFoundException e)
		      {
		        serviceLog.warn("Button is referencing inexistent macro with id " + ((DeviceMacroRef)uiCommand).getTargetDeviceMacro().getOid(), e);
		        return null;
		      }
		    }

		    throw new RuntimeException("We don't expect any other type of UICommand"); // TODO : review that exception type
		  }

  	});
  }
  
  /**
   * {@inheritDoc}
   */
  @Override @Transactional
  public void resolveDTOReferences(Collection<Panel> panels) {
  	Panel.walkAllUIComponents(panels, new UIComponentOperation() {

			@Override
      public void execute(UIComponent component) {
		    if (component instanceof SensorOwner) {
		      SensorOwner owner = (SensorOwner) component;
		      if (owner.getSensor() == null && owner.getSensorDTO() != null) {
		        Sensor sensor = sensorService.loadById(owner.getSensorDTO().getOid());
		        owner.setSensor(sensor);
		        owner.setSensorDTO(null);
		        if (owner instanceof SensorLinkOwner) {
		          ((SensorLinkOwner) owner).getSensorLink().setSensor(sensor);
		          ((SensorLinkOwner) owner).getSensorLink().setSensorDTO(null);
		        }
		      }
		    }
		    if (component instanceof UISlider) {
		      UISlider uiSlider = (UISlider)component;
		      if (uiSlider.getSlider() == null && uiSlider.getSliderDTO() != null) {
		        Slider slider = sliderService.loadById(uiSlider.getSliderDTO().getOid());
		        uiSlider.setSlider(slider);
		        uiSlider.setSliderDTO(null);
		      }
		    }
		    if (component instanceof UISwitch) {
		      UISwitch uiSwitch = (UISwitch)component;
		      if (uiSwitch.getSwitchCommand() == null && uiSwitch.getSwitchDTO() != null) {
		        Switch sw = switchService.loadById(uiSwitch.getSwitchDTO().getOid());
		        uiSwitch.setSwitchCommand(sw);
		        uiSwitch.setSwitchDTO(null);
		      }
		    }
		    if (component instanceof UIButton) {
		      UIButton uiButton = (UIButton)component;
		      if (uiButton.getUiCommand() == null && uiButton.getUiCommandDTO() != null) {
		        uiButton.setUiCommand(lookupUiCommandFromDTO(uiButton.getUiCommandDTO()));
		        uiButton.setUiCommandDTO(null);
		      }
		    }
		    if (component instanceof ColorPicker) {
		      ColorPicker colorPicker = (ColorPicker)component;
		      if (colorPicker.getUiCommand() == null && colorPicker.getUiCommandDTO() != null) {
		        colorPicker.setUiCommand(lookupUiCommandFromDTO(colorPicker.getUiCommandDTO()));
		        colorPicker.setUiCommandDTO(null);
		      }
		    }
		    if (component instanceof Gesture) {
		    	Gesture gesture = (Gesture)component;
			    if (gesture.getUiCommand() == null && gesture.getUiCommandDTO() != null) {        
			      gesture.setUiCommand(lookupUiCommandFromDTO(gesture.getUiCommandDTO()));
			      gesture.setUiCommandDTO(null);
			    }
		    }
      }

		  private UICommand lookupUiCommandFromDTO(UICommandDTO uiCommandDTO) {
		    if (uiCommandDTO instanceof DeviceCommandDTO) {
		      DeviceCommand dc = deviceCommandService.loadById(uiCommandDTO.getOid(), true); // Load device and its relationships eagerly
		      return  (dc != null)?new DeviceCommandRef(dc):null;
		    } else if (uiCommandDTO instanceof MacroDTO) {
		      DeviceMacro dm = deviceMacroService.loadById(uiCommandDTO.getOid());
		      return (dm != null)?new DeviceMacroRef(dm):null;
		    }
		    throw new RuntimeException("We don't expect any other type of UICommand"); // TODO : review that exception type
		  }

  	});
  }

   //
   // TODO :
   //
   //  - should be internalized to resource cache as part of MODELER-287
   //
   @Override @Transactional public void initResources(Collection<Panel> panels, long maxOid) {
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
       * copy the default image and default colorpicker image.
       */
      File defaultImage = new File(pathConfig.getWebRootFolder() + UIImage.DEFAULT_IMAGE_URL);
      FileUtilsExt.copyFile(defaultImage, new File(userFolder, defaultImage.getName()));
      File defaultColorPickerImage = new File(pathConfig.getWebRootFolder() + ColorPicker.DEFAULT_COLORPICKER_URL);
      FileUtilsExt.copyFile(defaultColorPickerImage, new File(userFolder, defaultColorPickerImage.getName()));

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
        Map<String, Object> map = new HashMap<String, Object>();
        Account account = userService.getAccount();
        map.put("devices", deviceService.loadAllDeviceDetailsWithChildrenDTOs(account));
        map.put("macros", deviceMacroService.loadAllMacroDetailsDTOs(account));
        
        
        // TODO
//        map.put("configuration", controllerConfigService.listAllConfigs());
        
        
        // TODO: add configuration
        XStream xstream = new XStream(new StaxDriver());
        /*
        XStream xstream = new XStream(new StaxDriver() {
          protected MapperWrapper wrapMapper(final MapperWrapper next) {
            return new HibernateMapper(next);
          }
        });
        xstream.registerConverter(new HibernateProxyConverter());
        xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));
        */
        FileWriter fw = new FileWriter(new File(pathConfig.buildingModelerXmlFilePath(userService.getAccount())));
        xstream.toXML(map, fw);
        fw.close();
        
        

        
        
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
         // EBR - MODELER-390 : why must there be at least 1 panel ????
         if (panels == null /*|| panels.size() < 1*/) {
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
      
      
      
      // MODELER-390 also write UI XML as newer format is read in priority to serialized format
      // Note this is a copy of code from DesignerState.generateXMLUiState method
      // TO BE CLEANED
      
      XStream xstream = new XStream(new StaxDriver() {
        protected MapperWrapper wrapMapper(final MapperWrapper next) {
          return new HibernateMapper(next);
        }
      });
      xstream.registerConverter(new HibernateProxyConverter());
      xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
      xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
      xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
      xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));
      
      xstream.alias("panel", Panel.class);
      xstream.alias("group", GroupRef.class);
      xstream.alias("screenPair", ScreenPairRef.class);
      xstream.alias("absolute", Absolute.class);
      FileWriter fw;
      try {
        fw = new FileWriter(new File(pathConfig.getXMLUIFile(userService.getAccount())));
        xstream.toXML(new PanelsAndMaxOid(panels, maxOid), fw);
        fw.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      
      
   }


  /**
   * This implementation has been moved and delegates to {@link DesignerState#restore}.
   */
  @Override @Deprecated @Transactional public PanelsAndMaxOid restore()
  {
    try
    {
      DesignerState state = new DesignerState(configuration, userService.getCurrentUser());
      state.restore(true);

      PanelsAndMaxOid result = state.transformToPanelsAndMaxOid();

      // EBR

      // TODO :
      //    this should be pushed deeper into the call stack, either into the designer state
      //    implementation (which can implement a translation to DTOs, similar to the current
      //    hack of transformToPanelsAndMaxOid() as seen above) or better yet into the domain
      //    package as was done in the case of Panel.getAllImageNames() method implementation --
      //    same comment applies, the processing is part of the domain object which should also
      //    help reduce the very brittle instanceof semantics seen in the populate and resolve
      //    DTO references here.
      //
      //    Also the error handling needs to be pushed to new DesignerState implementation
      //    so that errors in the implementation below are correctly handled and potentially
      //    preventing user data corruption.
      //                                                                            [JPL]
      //
      //    UPDATE 2012-09-13: Have not accomplished the task above yet (pushing call down
      //    to DesignerState implementation which would have more robust error handling
      //    *and* better error reporting due to user and account references that are carried
      //    in it. Duplicating some error handling here until have time to reorganize the
      //    code better, at which point the duplicate error handling can probably be removed. [JPL]

      try
      {
        populateDTOReferences(result.getPanels());
      }

      catch (Throwable t)
      {
        // This exception type and message will propagate to the user...

        throw new UIRestoreException(
            "Restoring your account data has failed. Please contact an administrator for " +
            "assistance. Avoid making further changes to your account and design to prevent " +
            "any potential data corruption issues: " + t.getMessage(), t
        );
      }
      
      // EBR - MODELER-315
      replaceNullNamesWithEmptyString(result.getPanels());      

      return result;
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
  @Override @Deprecated @Transactional public void saveResourcesToBeehive(Collection<Panel> panels)
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


  @Override
  public File getTempDirectory(String sessionId) {

       File tmpDir = new File(PathConfig.getInstance(configuration).userFolder(sessionId));
      if (tmpDir.exists() && tmpDir.isDirectory()) {
         try {
            FileUtils.deleteDirectory(tmpDir);
         } catch (IOException e) {
            throw new FileOperationException("Error in deleting temp dir", e);
         }
      }
      new File(PathConfig.getInstance(configuration).userFolder(sessionId)).mkdirs();
       return tmpDir;
  }

  @Override public void deleteImage(String imageName) {

    // TODO: make it fail to test UI reporting

    File image = new File(PathConfig.getInstance(configuration).userFolder(userService.getAccount()) + imageName);
    if (!image.delete()) {
      // TODO: handle correctly
      throw new RuntimeException("Could not delete file");
    }
  }

  /**
   * Executes merge on template, performing appropriate XML escaping and returns result as String.
   * 
   * This is basically a simplified copy of the code from VelocityEngineUtils class of Spring framework,
   * but is required to have access to the context before doing the merge.
   * This allows using an EscapeXmlReference subclass to do proper escaping for XML output.
   * The subclass modifies to standard XML escaping to ensure the XML output of our
   * getPanelXml() methods on the UI model don't get escaped. 
   * 
   * @see https://github.com/SpringSource/spring-framework/blob/master/spring-context-support/src/main/java/org/springframework/ui/velocity/VelocityEngineUtils.java
   * 
   * @param templateLocation
   * @param model
   * @return
   * @throws Exception 
   */
  public String mergeXMLTemplateIntoString(String templateLocation, Map model) throws Exception {
    StringWriter result = new StringWriter();
    VelocityContext velocityContext = new VelocityContext(model);
    EventCartridge ec = new EventCartridge();
    ec.addEventHandler(new EscapeXmlReference() {
      @Override
      public Object referenceInsert(String reference, Object value) {
        int lastDot = reference.lastIndexOf(".");
        if (lastDot != -1) {
          if (".getPanelXml()}".equals(reference.substring(lastDot))) {
            return value;
          }
        }
        return super.referenceInsert(reference, value);
      }
    });
    ec.attachToContext(velocityContext);
    velocity.mergeTemplate(templateLocation, "UTF8", velocityContext, result);
    return result.toString();
  }
}
