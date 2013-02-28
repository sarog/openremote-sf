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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.SensorOwner;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.TemplateService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.UIComponentBox;

import flexjson.ClassLocator;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.Path;

/**
 * 
 * @author javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class TemplateServiceImpl implements TemplateService
{
   private static Log log = LogFactory.getLog(TemplateService.class);

   private Configuration configuration;
   private UserService userService;
   private ResourceService resourceService;
   
   private DeviceService deviceService  ;
   private DeviceCommandService deviceCommandService;
   private SwitchService switchService ;
   private SliderService sliderService ;
   private SensorService sensorService ;
   private DeviceMacroService deviceMacroService ; 

   @Override

   public Template saveTemplate(Template screenTemplate) {

      log.debug("save Template Name: " + screenTemplate.getName());

      screenTemplate.setContent(getTemplateContent(screenTemplate.getScreen()));
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("name", screenTemplate.getName()));
      params.add(new BasicNameValuePair("content", screenTemplate.getContent()));
      
      log.debug("TemplateContent" + screenTemplate.getContent());

      try {
         String saveRestUrl = configuration.getBeehiveRESTRootUrl() + "account/" + userService.getAccount().getOid()
               + "/template/";

         if (screenTemplate.getShareTo() == Template.PUBLIC) {
            saveRestUrl = configuration.getBeehiveRESTRootUrl() + "account/0" + "/template/";
         }

         HttpPost httpPost = new HttpPost(saveRestUrl);
         addAuthentication(httpPost);
         UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
         httpPost.setEntity(formEntity);
         HttpClient httpClient = new DefaultHttpClient();

         String result = httpClient.execute(httpPost, new ResponseHandler<String>() {

            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

               InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
               BufferedReader buffReader = new BufferedReader(reader);
               StringBuilder sb = new StringBuilder();
               String line = "";

               while ((line = buffReader.readLine()) != null) {
                  sb.append(line);
                  sb.append("\n");
               }

               return sb.toString();
            }

         });

         if (result.indexOf("<id>") != -1 && result.indexOf("</id>") != -1) {
            long templateOid = Long.parseLong(result.substring(result.indexOf("<id>") + "<id>".length(), result
                  .indexOf("</id>")));
            screenTemplate.setOid(templateOid);
            // save the resources (eg:images) to beehive.
            resourceService.saveTemplateResourcesToBeehive(screenTemplate);
         } else {
            throw new BeehiveNotAvailableException();
         }
      } catch (Exception e) {
         throw new BeehiveNotAvailableException("Failed to save screen as a template: " + e.getMessage(), e);
      }

      log.debug("save Template Ok!");
      return screenTemplate;
   }

   private String getTemplateContent(Screen screen) {
      try {
         String[] includedPropertyNames = { 
               "absolutes.uiComponent.oid",
               "grids.cells.uiComponent.oid",
               "absolutes.uiComponent.uiCommand.deviceCommand.protocol.protocalAttrs",
               "absolutes.uiComponent.commands",
               "absolutes.uiComponent.slider.sliderSensorRef.sensor",
               "absolutes.uiComponent.switchCommand.switchSensorRef.sensor",
               "grids.cells.uiComponent",
               "grids.cells.uiComponent.slider.sliderSensorRef.sensor",
               "grids.cells.uiComponent.switchCommand.switchSensorRef.sensor",
               "grids.cells.uiComponent.uiCommand",
               "grids.cells.uiComponent.uiCommand.deviceCommand.protocol.protocalAttrs",
               "grids.cells.uiComponent.commands", "*.deviceCommand", "*.protocol", "*.attributes" };
         String[] excludePropertyNames = { "grid", "*.touchPanelDefinition", "*.refCount", "*.displayName", "*.oid",
               "*.proxyInformations", "*.proxyInformation", "gestures", "*.panelXml", "*.navigate","*.deviceCommands","*.sensors","*.sliders","*.configs","*.switchs","DeviceMacros" };
         return new JSONSerializer().include(includedPropertyNames).exclude(excludePropertyNames).deepSerialize(screen);
      } catch (Exception e) {

         log.error(e.getMessage(), e);
        
         return "";
      }
   }

   @Override
   public ScreenFromTemplate buildFromTemplate(Template template) {
      Screen screen = buildScreenFromTemplate(template);
      
      // ---------------download resources (eg:images) from beehive.
      resourceService.downloadResourcesForTemplate(template.getOid());
      return reBuildCommand(screen);
   }

   @Override
   public Screen buildScreenFromTemplate(Template template) {
      String screenJson = template.getContent();
      Screen screen = new JSONDeserializer<Screen>().use(null, Screen.class).use("absolutes.values.uiComponent",
            new SimpleClassLocator()).use("grids.values.cells.values.uiComponent", new SimpleClassLocator())
            //1,absolutes
            //    1.1, uiCommand
            .use("absolutes.values.uiComponent.uiCommand",new SimpleClassLocator())
            //    1.2, sensor 
            .use("absolutes.values.uiComponent.sensor",new SimpleClassLocator())
            .use("absolutes.values.uiComponent.slider.sliderSensorRef.sensor",new SimpleClassLocator())
            .use("absolutes.values.uiComponent.switchCommand.switchCommandOnRef.sensor",new SimpleClassLocator())
            .use("absolutes.values.uiComponent.switchCommand.switchCommandOffRef.sensor",new SimpleClassLocator())
            //2,grids
            //    2.1 uiCommand
            .use("grids.values.cells.values.uiComponent.uiCommand",new SimpleClassLocator())
            //    2.2 sensor 
            .use("grids.values.cells.values.uiComponent.sensor",new SimpleClassLocator())
            .use("grids.values.cells.values.uiComponent.slider.sliderSensorRef.sensor",new SimpleClassLocator())
            .use("grids.values.cells.values.uiComponent.switchCommand.switchCommandOnRef.sensor",new SimpleClassLocator())
            .use("grids.values.cells.values.uiComponent.switchCommand.switchCommandOffRef.sensor",new SimpleClassLocator())
            .deserialize(screenJson);
      return screen;
   }
   
   @Override
   public boolean deleteTemplate(long templateOid) {

      log.debug("Delete Template id: " + templateOid);

      String deleteRestUrl = configuration.getBeehiveRESTRootUrl() + "account/" + userService.getAccount().getOid()
            + "/template/" + templateOid;

      HttpDelete httpDelete = new HttpDelete();
      addAuthentication(httpDelete);

      try {
         httpDelete.setURI(new URI(deleteRestUrl));
         HttpClient httpClient = new DefaultHttpClient();
         HttpResponse response = httpClient.execute(httpDelete);

         if (200 == response.getStatusLine().getStatusCode()) {
            return true;
         } else {
            throw new BeehiveNotAvailableException("Failed to delete template");
         }
      } catch (Exception e) {
         throw new BeehiveNotAvailableException("Failed to delete template: " + e.getMessage(), e);
      }
   }

   public List<Template> getTemplates(boolean fromPrivate) {
      List<Template> templates = new ArrayList<Template>();
      String restURL = configuration.getBeehiveRESTRootUrl() + "account/"
            + (fromPrivate ? userService.getAccount().getOid() : 0) + "/templates";

      HttpGet httpGet = new HttpGet(restURL);
      httpGet.setHeader("Accept", "application/json");
      this.addAuthentication(httpGet);
      HttpClient httpClient = new DefaultHttpClient();

      try {
         HttpResponse response = httpClient.execute(httpGet);

         if (response.getStatusLine().getStatusCode() != 200) {
            if (response.getStatusLine().getStatusCode() == 401) {
               throw new NotAuthenticatedException();
            }
            throw new BeehiveNotAvailableException();
         }

         InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
         BufferedReader buffReader = new BufferedReader(reader);
         StringBuilder sb = new StringBuilder();
         String line = "";

         while ((line = buffReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
         }

         String result = sb.toString();
         TemplateList templateList = buildTemplateListFromJson(result);
         List<TemplateDTO> dtoes = templateList.getTemplates();

         for (TemplateDTO dto : dtoes) {
            templates.add(dto.toTemplate());
         }
      } catch (IOException e) {
         throw new BeehiveNotAvailableException("Failed to get template list", e);
      }

      return templates;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public ScreenFromTemplate reBuildCommand(Screen screen) {

      UIComponentBox box = initUIComponentBox(screen);
      Set<Device> devices = getDevices(screen);
      Set<DeviceCommand> commands = getDeviceCommands(screen);
      Set<Slider> sliders = getSliders((Collection<UISlider>) (box.getUIComponentsByType(UISlider.class)));
      Set<Switch> switchs = getSwitches((Collection<UISwitch>) box.getUIComponentsByType(UISwitch.class));
      Set<Sensor> sensors = getSensors(screen);
      Set<DeviceMacro> macros = getMacros(box);
      rebuild(devices, commands, sensors, switchs, sliders, macros);

      return new ScreenFromTemplate(devices, screen,macros);
   }


   private static UIComponentBox initUIComponentBox(Screen screen) {
      UIComponentBox box = new UIComponentBox();

      for (Absolute absolute : screen.getAbsolutes()) {
         UIComponent component = absolute.getUiComponent();
         box.add(component);
      }

      for (UIGrid grid : screen.getGrids()) {
         for (Cell cell : grid.getCells()) {
            box.add(cell.getUiComponent());
         }
      }

      return box;
   }

   private Set<DeviceCommand> getDeviceCommands(Screen screen) {
      UIComponentBox box = initUIComponentBox(screen);
      Set<DeviceCommand> uiCmds = new HashSet<DeviceCommand>();
      
      getDeviceCommandsFromSlider(box, uiCmds);
      getDeviceCommandsFromSwitch(box, uiCmds);
      getDeviceCommandsFromButton(box, uiCmds);
      getDeviceCommandsFromSensor(screen, uiCmds);
      
      return uiCmds;
   }

   private void getDeviceCommandsFromSensor(Screen screen, Set<DeviceCommand> uiCmds) {
      Collection<Sensor> sensors = getSensors(screen);

      for (Sensor sensor: sensors) {
         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(sensor.getSensorCommandRef().getDeviceCommand())) {
               sensor.getSensorCommandRef().setDeviceCommand(cmd);
               sensor.setDevice(cmd.getDevice());
            }
         }
         uiCmds.add(sensor.getSensorCommandRef().getDeviceCommand());
      }
   }

   @SuppressWarnings("unchecked")
   private void getDeviceCommandsFromButton(UIComponentBox box, Set<DeviceCommand> uiCmds) {
      Collection<UIButton> buttons = (Collection<UIButton>) box.getUIComponentsByType(UIButton.class);

      for (UIButton btn : buttons ) {
         UICommand cmd = btn.getUiCommand();

         if (cmd != null) {
            if (cmd instanceof DeviceCommandRef) {
               DeviceCommandRef cmdRef = (DeviceCommandRef) cmd;

               for (DeviceCommand tmpCmd : uiCmds) {
                  if (tmpCmd.equals(cmdRef.getDeviceCommand())) {
                     cmdRef.setDeviceCommand(tmpCmd);
                  }
               }

               uiCmds.add(cmdRef.getDeviceCommand());
               
            } else if (cmd instanceof DeviceMacroRef) {
               DeviceMacroRef macroRef = (DeviceMacroRef) cmd;
               DeviceMacro macro = macroRef.getTargetDeviceMacro();

               if (macro != null) {
                  Collection<DeviceCommandRef> cmds = getDeviceCommandsRefsFromMacro(macro);

                  for (DeviceCommandRef cmdFromMacro : cmds) {
                     for (DeviceCommand cmdInCommandSet : uiCmds) {
                        if (cmdFromMacro.getDeviceCommand().equals(cmdInCommandSet)) {
                           cmdFromMacro.setDeviceCommand(cmdInCommandSet);
                        }
                     }
                     uiCmds.add(cmdFromMacro.getDeviceCommand());
                  }
               }
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void getDeviceCommandsFromSwitch(UIComponentBox box, Set<DeviceCommand> uiCmds) {
      Collection<Switch> switchs = getSwitches((Collection<UISwitch>) box.getUIComponentsByType(UISwitch.class));

      for (Switch switchToggle : switchs) {
         DeviceCommand onCmd = switchToggle.getSwitchCommandOnRef().getDeviceCommand();

         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(onCmd)) {
               switchToggle.getSwitchCommandOnRef().setDeviceCommand(cmd);
            }
         }

         uiCmds.add(onCmd);
         DeviceCommand offCmd = switchToggle.getSwitchCommandOffRef().getDeviceCommand();

         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(offCmd)) {
               switchToggle.getSwitchCommandOffRef().setDeviceCommand(cmd);
            }
         }

         uiCmds.add(offCmd);

      }
   }

   @SuppressWarnings("unchecked")
   private void getDeviceCommandsFromSlider(UIComponentBox box, Set<DeviceCommand> uiCmds) {
      Collection<Slider> sliders = getSliders((Collection<UISlider>) box.getUIComponentsByType(UISlider.class));

      for (Slider slider : sliders) {
         for (DeviceCommand cmd : uiCmds) {
            if (cmd.equals(slider.getSetValueCmd().getDeviceCommand())) {
               slider.getSetValueCmd().setDeviceCommand(cmd);
            }
         }

         uiCmds.add(slider.getSetValueCmd().getDeviceCommand());
      }
   }

   private Set<Device> getDevices(Screen screen) {
      Set<Device> devices = new HashSet<Device>();
      // Because UICommand like the Slider, Switch can only select DeviceCommand from one device and the DeviceCommand only belongs to one device, the UICommand are in the same device as the DeviceCommand they have selected.
      // Therefore, we can get all the device by the DeviceCommand without get device from UICommand. 
      Collection<DeviceCommand> deviceCmds = getDeviceCommands(screen);

      for (DeviceCommand cmd : deviceCmds ) {
         Device device = cmd.getDevice();

         if (devices.contains(device)) {
            for (Device dvc : devices) {
               if (dvc.equals(device)) {
                  cmd.setDevice(dvc);
               }
            }
         }

         devices.add(device);
      }
      return devices;
   }

   private Set<Slider> getSliders(Collection<UISlider> uiSliders) {
      Set<Slider> sliders = new HashSet<Slider>();

      for (UISlider uiSlider : uiSliders ) {
         Slider slider = uiSlider.getSlider();

         if (slider != null) {
            for (Slider sld : sliders) {
               if (slider.equals(sld)) {
                  uiSlider.setSlider(sld);
               }
            }

            sliders.add(slider);
         }
      }
      return sliders;
   }

   private Set<Switch> getSwitches(Collection<UISwitch> uiSwitchs) {
      Set<Switch> switches = new HashSet<Switch>();

      for (UISwitch uiSwitch : uiSwitchs) {
         Switch switchToggle = uiSwitch.getSwitchCommand();
         if (switchToggle != null) {
            for (Switch swh: switches) {
               if (switchToggle.equals(swh)) {
                  uiSwitch.setSwitchCommand(swh);
               }
            }
            switches.add(switchToggle);
         }
      }
      return switches;
   }

   private Set<Sensor> getSensors(Screen screen) {
      Set<Sensor> sensors = new HashSet<Sensor>();

      for (Absolute absolute : screen.getAbsolutes()) {
         UIComponent component = absolute.getUiComponent();

         if (component instanceof SensorOwner) {
            SensorOwner sensorOwner = (SensorOwner) component;
            Sensor s = sensorOwner.getSensor();

            if (s != null) {

               for (Sensor sensor: sensors) {
                  if (sensor.equals(sensorOwner.getSensor())) {
                     sensorOwner.setSensor(sensor);
                  }
               }

               initSensorLinker(component,sensorOwner);
               sensors.add(s);
            }
         }
      }

      for (UIGrid grid : screen.getGrids()) {
         for (Cell cell : grid.getCells()) {
            UIComponent component = cell.getUiComponent();
            if (component instanceof SensorOwner) {
               SensorOwner sensorOwner = (SensorOwner) component;
               for (Sensor sensor: sensors) {
                  if (sensor.equals(sensorOwner.getSensor())) {
                     sensorOwner.setSensor(sensor);
                  }
               }

               initSensorLinker(component,sensorOwner);
               sensors.add(sensorOwner.getSensor());
            }
         }
      }

      return sensors;
   }

   private void initSensorLinker(UIComponent component,SensorOwner sensorOwner) {

      if (component != null ) {
         if(component instanceof UILabel ) {
            UILabel uiLabel = (UILabel) component;
            uiLabel.setSensorLinker(new SensorLink(sensorOwner.getSensor()));
         } else if (component instanceof UIImage) {
            UIImage uiImage = (UIImage) component;
            uiImage.setSensorLinker(new SensorLink(sensorOwner.getSensor()));
         }
      }
   }

   @SuppressWarnings("unchecked")
   private Set<DeviceMacro> getMacros(UIComponentBox box) {
      Set<DeviceMacro> macros = new HashSet<DeviceMacro>();
      Collection<UIButton> uiButtons = (Collection<UIButton>) box.getUIComponentsByType(UIButton.class);

      for (UIButton btn : uiButtons) {
         if (btn.getUiCommand() instanceof DeviceMacroRef) {
            DeviceMacroRef macroRef = (DeviceMacroRef) btn.getUiCommand();

            if (macroRef.getTargetDeviceMacro() != null) {
               DeviceMacro macro = macroRef.getTargetDeviceMacro();
               macros.add(macro);
               macros.addAll(macro.getSubMacros());
            }
         }
      }

      return macros;
   }

   /*private  Set<DeviceMacro> getSubMacrosForMacro(DeviceMacro macro) {
      Set<DeviceMacro> macros = new HashSet<DeviceMacro> ();
      if (macro != null && macro.getDeviceMacroItems() != null) {
         List<DeviceMacroItem> macroItems = macro.getDeviceMacroItems();
         for (DeviceMacroItem item : macroItems) {
            if (item instanceof DeviceMacroRef) {
               DeviceMacroRef macroRef = (DeviceMacroRef) item;
               DeviceMacro dvcMacro = macroRef.getTargetDeviceMacro();
               macros.add(dvcMacro);
               macros.addAll(getSubMacrosForMacro(dvcMacro));
            } else if (item instanceof DeviceCommandRef) {
               item.setParentDeviceMacro(macro);
            }
         }
      }
      return macros;
   }*/

   private Collection<DeviceCommandRef> getDeviceCommandsRefsFromMacro(DeviceMacro deviceMacro) {
      Collection<DeviceCommandRef> deviceCommands = new ArrayList<DeviceCommandRef> ();

      if (deviceMacro != null) {
         List<DeviceMacroItem> macroRefs = deviceMacro.getDeviceMacroItems();

         if (macroRefs != null && macroRefs.size() >0) {
            for (DeviceMacroItem macroItem : macroRefs) {
               if (macroItem instanceof DeviceCommandRef) {
                  DeviceCommandRef cmdRef = (DeviceCommandRef) macroItem;
                  deviceCommands.add(cmdRef);
               } else if (macroItem instanceof DeviceMacroRef) {
                  DeviceMacroRef macroRef = (DeviceMacroRef) macroItem;
                  if (macroRef.getTargetDeviceMacro() != null) {
                     Collection<DeviceCommandRef> cmds = getDeviceCommandsRefsFromMacro(macroRef.getTargetDeviceMacro());
                     deviceCommands.addAll(cmds);
                  }
               }
            }
         }
      }
      return deviceCommands;
   }

   private void rebuild(Collection<Device> devices, Collection<DeviceCommand> deviceCommands, Collection<Sensor> sensors,
         Collection<Switch> switches,Collection<Slider> sliders,Collection<DeviceMacro> macros) {

      Account account = userService.getAccount();
     
      //1, build devices. 
      for (Device device : devices) {
         device.setAccount(account);
         deviceService.saveDevice(device);
      }
      
      //2, build DeviceCommands. 
      for (DeviceCommand deviceCommand : deviceCommands) {
         Protocol protocol = deviceCommand.getProtocol();
         if (protocol.getAttributes() != null) {
            for (ProtocolAttr attr : protocol.getAttributes()) {
               attr.setProtocol(protocol);
            }
         }

         deviceCommandService.save(deviceCommand);
      }

      //3, build sensors. 
      for (Sensor sensor : sensors) {
         sensor.setAccount(account);
         sensor.getSensorCommandRef().setSensor(sensor);
         sensor.setDevice(sensor.getSensorCommandRef().getDeviceCommand().getDevice());
         sensorService.saveSensor(sensor);
      }

      //4, build switch. 
      for (Switch switchToggle : switches) {
         switchToggle.setAccount(account);
         switchToggle.getSwitchCommandOffRef().setOffSwitch(switchToggle);
         switchToggle.getSwitchCommandOnRef().setOnSwitch(switchToggle);
         switchToggle.setDevice(switchToggle.getSwitchCommandOffRef().getDeviceCommand().getDevice());
         switchToggle.getSwitchSensorRef().setSwitchToggle(switchToggle);
         switchService.save(switchToggle);
      }

      //5, build slider. 
      for (Slider slider : sliders) {
         slider.setAccount(account);
         slider.setDevice(slider.getSetValueCmd().getDeviceCommand().getDevice());
         slider.getSliderSensorRef().setSlider(slider);
         slider.getSetValueCmd().setSlider(slider);
         sliderService.save(slider);
      }

      //6, build macro. 
      for (DeviceMacro macro : macros) {
         macro.setAccount(account);
         saveMacro(macro);
      }

      //7, prepare to send to client.
      prepareToSendToClient(devices, deviceCommands, macros, account);
   }

   private void prepareToSendToClient(Collection<Device> devices, Collection<DeviceCommand> deviceCommands,
         Collection<DeviceMacro> macros, Account account) {
      // Because some of the domain classes are lazy loaded by hibernate, 
      // we need replace some hibernate proxy classes with the class declared in their own class. 
      // (for example we may need replace PersistentBag with ArrayList.)
      // so that they can be serialized by GWT. 
      for (DeviceCommand deviceCommand : deviceCommands) {
         Protocol protocol = deviceCommand.getProtocol();

         if (protocol.getAttributes() != null) {
            for (ProtocolAttr attr : protocol.getAttributes()) {
               attr.setProtocol(protocol);
            }
         }

         List<ProtocolAttr> attrs = new ArrayList<ProtocolAttr> ();

         for (ProtocolAttr attr : protocol.getAttributes()) {
           attrs.add(attr);
         }
        
         deviceCommand.getProtocol().setAttributes(attrs);
      }

      account.setConfigs(new ArrayList<ControllerConfig>());
      account.setDeviceMacros(new ArrayList<DeviceMacro>());
      account.setSensors(new ArrayList<Sensor>());
      account.setSliders(new ArrayList<Slider>());
      account.setSwitches(new ArrayList<Switch>());
      account.setDevices(new ArrayList<Device>());
      account.getUser().setRoles(new ArrayList<Role>());

      for (Device device : devices ) {
         device.setAccount(null);
         device.setSensors(new HashSet<Sensor>());
         device.setSwitchs(new HashSet<Switch>());
         device.setSliders(new HashSet<Slider>());
         device.setDeviceCommands(new ArrayList<DeviceCommand>());
      }

      for (DeviceMacro macro : macros) {
         macro.setAccount(null);
         macro.setDeviceMacroItems(new ArrayList<DeviceMacroItem>());
      }
   }
   
   private void saveMacro(DeviceMacro macro) {

      if (null != macro) {
         List<DeviceMacroItem> items = macro.getDeviceMacroItems();

         // first, save the macros belongs to it. 
         if (null != items) {
            for (DeviceMacroItem item : items) {
               if (item instanceof DeviceMacroRef) {
                  DeviceMacroRef macroRef = (DeviceMacroRef) item;
                  DeviceMacro subMacro = macroRef.getTargetDeviceMacro();
                  saveMacro(subMacro);
               }
               item.setParentDeviceMacro(macro);
            }
         }

         // second, save the macro itself. 
         this.deviceMacroService.saveDeviceMacro(macro);
      }
   }
   
   
   private String encode(String namePassword) {
      if (namePassword == null) return null;
      return new String(Base64.encodeBase64(namePassword.getBytes()));
   }
   
   private void addAuthentication(AbstractHttpMessage httpMessage) {
      httpMessage.setHeader(Constants.HTTP_BASIC_AUTH_HEADER_NAME, Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + encode(userService.getAccount().getUser().getUsername() + ":"
                  + userService.getAccount().getUser().getPassword()));
   }
   
   private TemplateList buildTemplateListFromJson(String templatesJson) {
      TemplateList result = new TemplateList();

     //The json string from beehive is not easy to be convert to java object by FlexJson, so we remove and replace the unnecessary characters.
      try {
         String validTemplatesJson = "";

         if (templatesJson.contains("{\"template\":")) {
            if (templatesJson.contains("{\"template\":[")) {
               String tempString =  templatesJson.replaceFirst("\\{\"template\":", "");
               validTemplatesJson = tempString.substring(0, tempString.lastIndexOf("}}")) + "}";
            } else {
               String tempString = templatesJson.replaceFirst("\\{\"template\":", "[");
               validTemplatesJson = tempString.substring(0, tempString.lastIndexOf("}}")) + "]}";
            }

            result = new JSONDeserializer<TemplateList>().use(null, TemplateList.class).use("templates",
                  ArrayList.class).deserialize(validTemplatesJson);
         }
      } catch (RuntimeException e) {
         log.warn("Faild to get template list, there are no templats in beehive ");
      }
      return result;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public void setResourceService(ResourceService resourceService) {
      this.resourceService = resourceService;
   }
   
   public void setDeviceService(DeviceService deviceService) {
      this.deviceService = deviceService;
   }

   public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
      this.deviceCommandService = deviceCommandService;
   }

   public void setSwitchService(SwitchService switchService) {
      this.switchService = switchService;
   }

   public void setSliderService(SliderService sliderService) {
      this.sliderService = sliderService;
   }

   public void setSensorService(SensorService sensorService) {
      this.sensorService = sensorService;
   }

   public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
      this.deviceMacroService = deviceMacroService;
   }

  /**
    * A class to help flexjson to deserialize a UIComponent
    * 
    * @author javen
    * 
    */
   private static class SimpleClassLocator implements ClassLocator {

      @SuppressWarnings("unchecked")
      public Class locate(Map map, Path currentPath) throws ClassNotFoundException {
         return Class.forName(map.get("class").toString());
      }
   }

   /**
    * A class used to help flexjson convert json string to a template list. 
    * flexjson need a java class to map a json string. 
    * @author javen
    *
    */
   public static class TemplateList {
      private List<TemplateDTO> templates = new ArrayList<TemplateDTO> ();

      public List<TemplateDTO> getTemplates() {
         return templates;
      }

      public void setTemplates(List<TemplateDTO> templates) {
         this.templates = templates;
      }
      
   }

   /**
    * A class used to help flexjson convert json string to template list. 
    * The class Template need a property <b>oid</b>, but in json string it is mapped to <b>id</b>,therefore at first we need convert the string to TemplateDTO and then 
    * convert it to Template later.  
    * @author javen
    *
    */
   public static class TemplateDTO {
      private int id;
      private String content;
      private String name;

      public int getId() {
         return id;
      }

      public void setId(int id) {
         this.id = id;
      }

      public String getContent() {
         return content;
      }

      public void setContent(String content) {
         this.content = content;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }
      
      public Template toTemplate() {
         Template template = new Template();
         template.setName(name);
         template.setContent(content);
         template.setOid(id);
         return template;
      }
   }
}
