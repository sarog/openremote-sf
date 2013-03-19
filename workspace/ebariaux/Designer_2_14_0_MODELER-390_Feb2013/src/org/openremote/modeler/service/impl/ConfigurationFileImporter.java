/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openremote.modeler.cache.CacheOperationException;
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Panel.UIComponentOperation;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.SensorLinkOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsWithChildrenDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
import org.openremote.modeler.shared.dto.SliderWithInfoDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;
import org.openremote.modeler.shared.dto.UICommandDTO;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ConfigurationFileImporter {

  private Account account;
  private LocalFileCache cache;
  private DesignerState state;
  private File importFile;
  
  private DeviceMacroService deviceMacroService;
  private DeviceService deviceService;
  private ControllerConfigService controllerConfigService;

  // Private information collected and used during the import process
  private Map<String, Object> buildingModelerConfiguration;
  
  final Map<Long, Long> commandsOldOidToNewOid = new HashMap<Long, Long>();
  final Map<Long, Long> sensorsOldOidToNewOid = new HashMap<Long, Long>();
  final Map<Long, Long> switchesOldOidToNewOid = new HashMap<Long, Long>();
  final Map<Long, Long> slidersOldOidToNewOid = new HashMap<Long, Long>();

  private final static LogFacade serviceLog =
      LogFacade.getInstance(LogFacade.Category.RESOURCE_SERVICE);

  public ConfigurationFileImporter(Account account, LocalFileCache cache, DesignerState state, File importFile) {
    super();
    this.account = account;
    this.cache = cache;
    this.state = state;
    this.importFile = importFile;
  }

  public Map<String, Collection<? extends DTO>> importConfiguration() throws ConfigurationException, NetworkException, CacheOperationException {

    // Validate that we're in a correct state to import
    if (deviceMacroService == null) {
      throw new IllegalStateException("ConfigurationFileImporter does not have all required dependencies");
    }
    if (deviceService == null) {
      throw new IllegalStateException("ConfigurationFileImporter does not have all required dependencies");
    }
    if (controllerConfigService == null) {
      throw new IllegalStateException("ConfigurationFileImporter does not have all required dependencies");
    }
    
    if (buildingModelerConfiguration != null) {
      throw new IllegalStateException("ConfigurationFileImporter can not be re-used for other import");
    }

    // No need to clean any of the resources stored in the cache (UI, images, rules...).
    // The whole cache is deleted later before being replaced with the uploaded file.
    
    // Remove all building modeler information
    deleteBuildingModelerConfiguration();

    cache.replace(importFile);

    if (!cache.getBuildingModelerXmlFile().exists()) {
      throw new ConfigurationException("Invalid import file: no building modeler data");
    }
    if (!cache.hasXMLUIState()) {
      throw new ConfigurationException("Invalid import file: no UI data");
    }
    
    buildingModelerConfiguration = readBuildingModelerConfigurationFile();
    
    List <DeviceDTO> importedDeviceDTOs = importDevices();

    List <MacroDTO> importedMacroDTOs = importMacros();

    importControllerConfiguration();
    
    // UI
    
    state.restore(false);

    PanelsAndMaxOid panels = state.transformToPanelsAndMaxOid();
    
    fixPanelsDTOReferences(panels.getPanels());
    
    // All images still references original account, update their source to use this account
    fixImageSourcesForAccount(panels.getPanels());

    Set<Panel> panelSet = new HashSet<Panel>(panels.getPanels());
    cache.replace(panelSet, panels.getMaxOid());
    state.save(panelSet, panels.getMaxOid());

    Map<String, Collection<? extends DTO>> result = new HashMap<String, Collection<? extends DTO>>();
    result.put("devices", importedDeviceDTOs);
    result.put("macros", importedMacroDTOs);
    return result;
  }
  
  private void deleteBuildingModelerConfiguration() throws ConfigurationException {
    // Remove macros first, as they might reference commands
    deviceMacroService.deleteAll(account);

    // Then devices, no dependencies between them   
    List<Device> allDevices = deviceService.loadAll(account);
    for (Device d : allDevices) {
      deviceService.deleteDevice(d.getOid());
    }
    account.getSwitches().clear();
    account.getSliders().clear();
    account.getSensors().clear();
    account.getDevices().clear();
    
    // Remove configuration
    controllerConfigService.deleteAllConfigs();
  }
  
  @SuppressWarnings("unchecked")
  private Map<String, Object> readBuildingModelerConfigurationFile() throws ConfigurationException {
    Map<String, Object> map = null;
    InputStreamReader isr = null;
    try {
      XStream xstream = new XStream(new StaxDriver());

      // Going through a StreamReader to enforce UTF-8 encoding
      isr = new InputStreamReader(new FileInputStream(cache.getBuildingModelerXmlFile()), "UTF-8");
      map = (Map<String, Object>) xstream.fromXML(isr);
    } catch (UnsupportedEncodingException e) {
      throw new ConfigurationException("Issue reading file " + cache.getBuildingModelerXmlFile() + " in UTF-8 : " + e.getMessage(), e);
    } catch (FileNotFoundException e) {
      throw new ConfigurationException("File " + cache.getBuildingModelerXmlFile() +
          " can not be found, or was not a proper file : " + e.getMessage(), e);
    } finally {
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException e) {
          serviceLog.warn("Failed to close reader to " + cache.getBuildingModelerXmlFile() + " : " + e.getMessage(), e);
        }
      }
    }
    return map;
  }
  
  private List<DeviceDTO> importDevices() {
    @SuppressWarnings("unchecked")
    Collection<DeviceDetailsWithChildrenDTO> devices = (Collection<DeviceDetailsWithChildrenDTO>)buildingModelerConfiguration.get("devices");

    List<Device> importedDevices = new ArrayList<Device>();
    List <DeviceDTO> importedDeviceDTOs = new ArrayList<DeviceDTO>();
    
    // DTOs restored have oid but we don't care, they're not taken into account when saving new devices
    for (DeviceDetailsWithChildrenDTO dev : devices) {

      // The archived graph has DTOReferences with id, as it originally came from objects in DB.
      // Must iterate all DTOReferences, replacing ids with dto.
      dev.replaceIdWithDTOInReferences();

      // TODO EBR review : original MODELER-390 line was
      // importedDevices.add(deviceService.saveNewDeviceWithChildren(userService.getAccount(), dev, dev.getDeviceCommands(), dev.getSensors(), dev.getSwitches(), dev.getSliders()));

      importedDevices.add(deviceService.saveNewDeviceWithChildren(dev, dev.getDeviceCommands(), dev.getSensors(), dev.getSwitches(), dev.getSliders()));      
    }

    // Domain objects have been created and saved with a new id
    // During this process, old id has been saved as transient info
    // Create lookup map from originalId to new one
    for (Device dev : importedDevices) {
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

    return importedDeviceDTOs;
  }
  
  private List<MacroDTO> importMacros() throws ConfigurationException {
    @SuppressWarnings("unchecked")
    Collection<MacroDetailsDTO> macros = (Collection<MacroDetailsDTO>)buildingModelerConfiguration.get("macros");
    
    List <MacroDTO> importedMacroDTOs = new ArrayList<MacroDTO>();

    // Iterate over commands referenced in macros to adapt id to one of newly saved domain objects
    for (MacroDetailsDTO m : macros) {
      for (MacroItemDetailsDTO item : m.getItems()) {
        if (item.getType() == MacroItemType.Command) {
          item.getDto().setId(commandsOldOidToNewOid.get(item.getDto().getId()));
        }
      }
    }
    
    // Macros can reference other macros.
    // As for commands above, id in references need to be adapted so to use one of newly saved domain object.
    // This means macros should be processed in appropriate order, dependent macros before referencing ones.
    // Circular dependencies (m1 -> m2 and m2 -> m1) should not be allowed,
    // but if this is detected, it's considered an error and import is aborted.
    
    // Keep a list of macro ids that have already been processed.
    // On each iteration, macros that only reference those (or no other macro) are safe to process.
    // Ids kept are the ones of newly saved domain objects.
    Collection<Long> processedMacroIds = new ArrayList<Long>();
    
    Map<Long, Long> macrosOldOidToNewOid = new HashMap<Long, Long>();
    if (macros != null) {
      while (!macros.isEmpty()) {
        Collection<MacroDetailsDTO> processedMacrosThisTime = new ArrayList<MacroDetailsDTO>();
        
        for (MacroDetailsDTO m : macros) {
          // Macros not depending on any macro or only on ones already processed can be saved
          if (!m.dependsOnMacroNotInList(processedMacroIds)) {
            MacroDTO newMacro = deviceMacroService.saveNewMacro(m);
            macrosOldOidToNewOid.put(m.getOid(), newMacro.getOid());
            processedMacrosThisTime.add(m);
            importedMacroDTOs.add(newMacro);
          }
        }
        
        if (processedMacrosThisTime.isEmpty()) {
          // No macro could be processed -> there is a cyclic dependency
          throw new ConfigurationException("There is a cyclic dependency between macros in the imported configuration");
        }
        
        macros.removeAll(processedMacrosThisTime);
        
        // Keep track of macros that have been processed so far
        for (MacroDetailsDTO item : processedMacrosThisTime) {
          processedMacroIds.add(macrosOldOidToNewOid.get(item.getOid()));
        }
        
        // Now that dependencies have been saved, ensure referencing macros are using new id
        for (MacroDetailsDTO m : macros) {
          for (MacroItemDetailsDTO item : m.getItems()) {
            if (item.getType() == MacroItemType.Macro) {
              if (macrosOldOidToNewOid.get(item.getDto().getId()) != null) {
                item.getDto().setId(macrosOldOidToNewOid.get(item.getDto().getId()));
              }
            }
          }
        }
      }
    }
    return importedMacroDTOs;
  }
  
  private void importControllerConfiguration() {
    @SuppressWarnings("unchecked")
    Set<ControllerConfigDTO> configDTOs = (Set<ControllerConfigDTO>)buildingModelerConfiguration.get("configuration");
    
    if (configDTOs != null) {
      // Must reset oid before saving, or it'll update the "old" config elements (or crash if not found or random configs)!
      for (ControllerConfigDTO configDTO : configDTOs) {
        configDTO.setOid(null);
      }
      controllerConfigService.saveAllDTOs(configDTOs);
    }
  }
  
  private void fixPanelsDTOReferences(Collection<Panel> panels) {
    // All DTOs in the just imported object graph have ids of building elements from the original DB.
    // Walk the graph and change ids to the newly saved domain objects.
    Panel.walkAllUIComponents(panels, new UIComponentOperation() {

      @Override
      public void execute(UIComponent component) {
        if (component instanceof SensorLinkOwner) {
          SensorLinkOwner owner = ((SensorLinkOwner) component);
          if (owner != null && owner.getSensorLink() != null) {
            SensorWithInfoDTO sensorDTO = owner.getSensorLink().getSensorDTO();
            if (sensorDTO != null && sensorDTO.getOid() != null) {
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

      private void replaceOldOidWithNew(UICommandDTO commandDTO) {
        if (commandDTO == null) {
          return;
        }
        if (commandDTO.getOid() != null) {
          commandDTO.setOid(commandsOldOidToNewOid.get(commandDTO.getOid()));
        }
      }
    });
  }
  
  private void fixImageSourcesForAccount(Collection<Panel> panels) {
    for (Panel panel : panels) {
      panel.fixImageSource(new Panel.ImageSourceResolver() {
        Pattern p = Pattern.compile(PathConfig.RESOURCEFOLDER + "/(\\d+)/(.*)");
            
        @Override
        public String resolveImageSource(String source) {
          Matcher m = p.matcher(source);
          return (m.matches())?PathConfig.RESOURCEFOLDER + "/" + account.getOid() + "/" + m.group(2):source;
        }
      });
    }
  }

  public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
    this.deviceMacroService = deviceMacroService;
  }

  public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setControllerConfigService(ControllerConfigService controllerConfigService) {
    this.controllerConfigService = controllerConfigService;
  }

}