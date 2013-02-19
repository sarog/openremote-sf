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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.velocity.app.VelocityEngine;
import org.openremote.modeler.cache.CacheOperationException;
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Panel.UIComponentOperation;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.SensorLinkOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.exception.IllegalRestUrlException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.exception.XmlExportException;
import org.openremote.modeler.logging.LogFacade;
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
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsWithChildrenDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;
import org.openremote.modeler.shared.dto.UICommandDTO;
import org.openremote.modeler.utils.FileUtilsExt;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.ZipUtils;
import org.springframework.transaction.annotation.Transactional;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * TODO : this class is a total garbage bin -- everything and the kitchen sink is thrown in. Blah.
 * 
 * @author Allen, Handy, Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 * 
 */
public class ResourceServiceImpl implements ResourceService
{
  private final static LogFacade serviceLog =
      LogFacade.getInstance(LogFacade.Category.RESOURCE_SERVICE);


  private Configuration configuration;

  private UserService userService;
  
  // Those services are not directly used by this class but injected in LocalFileCache or DesignerState when they get built
  private DeviceCommandService deviceCommandService;
  private DeviceMacroService deviceMacroService;
  private DeviceService deviceService;
  
  private SensorService sensorService;
  private SliderService sliderService;
  private SwitchService switchService;

  private ControllerConfigService controllerConfigService = null;
  private VelocityEngine velocity;

  //
  // TODO : this implementation should go away with MODELER-288
  //
  @Override public String downloadZipResource(LocalFileCache cache, List<Panel> panels, long maxOid)
  {
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
	 LocalFileCache cache = createLocalFileCache(userService.getCurrentUser());
	 Set<Panel> noPanels = new HashSet<Panel>(); 
	 cache.replace(noPanels, 0);
     saveResourcesToBeehive(noPanels, 0);
     
     // Clean images
     // TODO


    // Remove all building modeler information (except for configuration)
    Account account = userService.getAccount();
    List<Device> allDevices = deviceService.loadAll(account);
    for (Device d : allDevices) {
      deviceService.deleteDevice(d.getOid());
    }
    account.getDevices().clear();
    account.getSensors().clear();
    account.getSwitches().clear();
    account.getSliders().clear();
    
    // TODO: macro
    account.getDeviceMacros().clear();

     cache.replace(importFile);

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

                  // TODO EBR review : original MODELER-390 line was
                  // importedDevices.add(deviceService.saveNewDeviceWithChildren(userService.getAccount(), dev, dev.getDeviceCommands(), dev.getSensors(), dev.getSwitches(), dev.getSliders()));

                  
                  importedDevices.add(deviceService.saveNewDeviceWithChildren(dev, dev.getDeviceCommands(), dev.getSensors(), dev.getSwitches(), dev.getSliders()));
                }
    
    final Map<Long, Long> devicesOldOidToNewOid = new HashMap<Long, Long>();
    final Map<Long, Long> commandsOldOidToNewOid = new HashMap<Long, Long>();
    final Map<Long, Long> sensorsOldOidToNewOid = new HashMap<Long, Long>();
    final Map<Long, Long> switchesOldOidToNewOid = new HashMap<Long, Long>();
    final Map<Long, Long> slidersOldOidToNewOid = new HashMap<Long, Long>();
    
    // Domain objects have been created and saved with a new id
    // During this process, old id has been saved as transient info
    // Create lookup map from originalId to new one
    for (Device dev : importedDevices) {
      devicesOldOidToNewOid.put((Long)dev.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), dev.getOid());
      
      for (DeviceCommand dc : dev.getDeviceCommands()) {
        commandsOldOidToNewOid.put((Long)dc.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), dc.getOid());
      }
      for (Sensor s : dev.getSensors()) {
        sensorsOldOidToNewOid.put((Long)s.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), s.getOid());
      }
      for (Switch s : dev.getSwitchs()) {
        switchesOldOidToNewOid.put((Long)s.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), s.getOid());
      }
      for (Slider s : dev.getSliders()) {
        slidersOldOidToNewOid.put((Long)s.retrieveTransient(DeviceService.ORIGINAL_OID_KEY), s.getOid());
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
                
       DesignerState state = createDesignerState(userService.getCurrentUser(), cache);
       state.restore(false);

       // TODO: walk the just restored panels hierarchy and adapt all DTO references to the newly saved ones.
       // For now, this code will crash if panels are restored that reference any building modeler objects.
    PanelsAndMaxOid panels = state.transformToPanelsAndMaxOid();
    
    // All DTOs in the just imported object graph have ids of building elements from the original DB.
    // Walk the graph and change ids to the newly saved domain objects.
    Panel.walkAllUIComponents(panels.getPanels(), new UIComponentOperation() {
			@Override
      public void execute(UIComponent component) {
		    if (component instanceof SensorLinkOwner) {
					SensorLinkOwner owner = ((SensorLinkOwner) component);
					if (owner.getSensorLink() != null) {
						SensorWithInfoDTO sensorDTO = owner.getSensorLink().getSensorDTO();
						if (sensorDTO.getOid() != null) {
							sensorDTO.setOid(sensorsOldOidToNewOid.get(sensorDTO.getOid()));
						}
					}
		    }
		    if (component instanceof UISlider) {
		      UISlider uiSlider = (UISlider)component;
		      if (uiSlider.getSliderDTO() != null) {
		      	SliderWithInfoDTO sliderDTO = uiSlider.getSliderDTO();
		      	if (sliderDTO.getOid() != null) {
		      		sliderDTO.setOid(slidersOldOidToNewOid.get(sliderDTO.getOid()));
		      	}
		      }
		    }
		    if (component instanceof UISwitch) {
		      UISwitch uiSwitch = (UISwitch)component;
		      if (uiSwitch.getSwitchDTO() != null) {
		      	SwitchWithInfoDTO switchDTO = uiSwitch.getSwitchDTO();
		      	if (switchDTO.getOid() != null) {
		      		switchDTO.setOid(switchesOldOidToNewOid.get(switchDTO.getOid()));
		      	}
		      }
		    }
		    if (component instanceof UIButton) {
		    	replaceOldOidWithNew(((UIButton)component).getUiCommandDTO());
		    }
		    if (component instanceof ColorPicker) {
		    	replaceOldOidWithNew(((ColorPicker)component).getUiCommandDTO());
		    }
		    if (component instanceof Gesture) {
		    	replaceOldOidWithNew(((Gesture)component).getUiCommandDTO());
			  }
      }
			
			private void replaceOldOidWithNew(UICommandDTO commandDTO)
			{
				if (commandDTO == null) {
					return;
				}
      	if (commandDTO.getOid() != null) {
      		commandDTO.setOid(commandsOldOidToNewOid.get(commandDTO.getOid()));
      	}			      	
			}
    	
    });
          
       // TODO EBR : original import implementation has the following line, check if still required (new replace() call)).
       // initResources(panels.getPanels(), panels.getMaxOid());
       saveResourcesToBeehive(panels.getPanels(), panels.getMaxOid());
                
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

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public void setConfiguration(Configuration configuration)
  {
    this.configuration = configuration;
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

   public void setVelocity(VelocityEngine velocity) {
      this.velocity = velocity;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
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
   * This implementation has been moved and delegates to {@link DesignerState#restore}.
   */
  @Override @Deprecated @Transactional public PanelsAndMaxOid restore()
  {
    try
    {
      User currentUser = userService.getCurrentUser();
      LocalFileCache cache = createLocalFileCache(currentUser);
    	
      DesignerState state = createDesignerState(currentUser, cache);
      state.restore(true);

      PanelsAndMaxOid result = state.transformToPanelsAndMaxOid();

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
  @Override @Deprecated @Transactional public LocalFileCache saveResourcesToBeehive(Collection<Panel> panels, long maxOid)
  {
	User currentUser = userService.getCurrentUser();
	LocalFileCache cache = createLocalFileCache(currentUser);

    // Create a set of panels to eliminate potential duplicate instances...

    HashSet<Panel> panelSet = new HashSet<Panel>();
    panelSet.addAll(panels);
    
    // Delegate implementation to DesignerState...

    DesignerState state = createDesignerState(currentUser, cache);
    state.save(panelSet, maxOid);
    
    return cache;
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
   * Creates a LocalFileCache for the current user and injects all the required beans in it.
   * 
   * TODO: Injection should really be handled by Spring directly.
   * 
   * @return LocalFileCache a correctly configured LocalFileCache
   */
  private LocalFileCache createLocalFileCache(User user) {
	  LocalFileCache cache = new LocalFileCache(configuration, user);

	  cache.setDeviceService(deviceService);
	  cache.setDeviceMacroService(deviceMacroService);
	  cache.setDeviceCommandService(deviceCommandService);
	  cache.setControllerConfigService(controllerConfigService);
	  cache.setVelocity(velocity);

	  return cache;
  }

  /**
   * Creates a DesignerState instance for the current user and injects all the required beans in it.
   * 
   * TODO: Injection should really be handled by Spring directly (if possible)
   * 
   * @return DesignerState a correctly configured DesignerState
   */
  private DesignerState createDesignerState(User user, LocalFileCache cache) {
    DesignerState state = new DesignerState(configuration, user, cache);
    state.setDeviceCommandService(deviceCommandService);
    state.setDeviceMacroService(deviceMacroService);
    state.setSensorService(sensorService);
    state.setSliderService(sliderService);
    state.setSwitchService(switchService);
    return state;
  }
  
}
