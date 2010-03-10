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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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
import org.openremote.modeler.service.DeviceCommandService;
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
         UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
         httpPost.addHeader(Constants.HTTP_BASIC_AUTH_HEADER_NAME, Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
               + encode(userService.getAccount().getUser().getUsername() + ":"
                     + userService.getAccount().getUser().getPassword()));
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

   /*private HttpEntity getTemplateFormEntity(Template template) throws Exception {
      File file = resourceService.getTemplateResource(template);
      FileBody fileBody = new FileBody(file);
      MultipartEntity entity = new MultipartEntity();
      entity.addPart("template.zip", fileBody);
      return entity;
   }*/
   
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
         e.printStackTrace();
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
      httpDelete.setHeader(Constants.HTTP_BASIC_AUTH_HEADER_NAME, Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + encode(userService.getAccount().getUser().getUsername() + ":"
                  + userService.getAccount().getUser().getPassword()));
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

   
   @SuppressWarnings("unchecked")
   @Override
   public ScreenFromTemplate reBuildCommand(Screen screen) {

      UIComponentBox box = initUIComponentBox(screen);
      Set<Device> devices = getDevices(screen);
      Set<DeviceCommand> commands = getDeviceCommands(screen);
      Set<Slider> sliders = getSliders((Collection<UISlider>) (box.getUIComponentsByType(UISlider.class)));
      Set<Switch> switchs = getSwitchs((Collection<UISwitch>) box.getUIComponentsByType(UISwitch.class));
      Set<Sensor> sensors = getSensors(screen);

      reBuild(devices, commands, sensors, switchs, sliders);

      return new ScreenFromTemplate(devices, screen);
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

   @SuppressWarnings("unchecked")
   private Set<DeviceCommand> getDeviceCommands(Screen screen) {
      UIComponentBox box = initUIComponentBox(screen);
      Set<DeviceCommand> uiCmds = new HashSet<DeviceCommand>();
      //get device commands from slider :
      Collection<Slider> sliders = getSliders((Collection<UISlider>) box.getUIComponentsByType(UISlider.class));
      for(Slider slider : sliders) {
         for(DeviceCommand cmd : uiCmds) {
            if(cmd.equals(slider.getSetValueCmd().getDeviceCommand())) {
               slider.getSetValueCmd().setDeviceCommand(cmd);
            }
         }
         uiCmds.add(slider.getSetValueCmd().getDeviceCommand());
      }
      //get device commands from switch
      Collection<Switch> switchs = getSwitchs((Collection<UISwitch>) box.getUIComponentsByType(UISwitch.class));
      for (Switch switchToggle : switchs) {
         DeviceCommand onCmd = switchToggle.getSwitchCommandOnRef().getDeviceCommand();
         for(DeviceCommand cmd : uiCmds) {
            if(cmd.equals(onCmd)) {
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
      //get device command from button 
      Collection<UIButton> buttons = (Collection<UIButton>) box.getUIComponentsByType(UIButton.class);
      for(UIButton btn : buttons ) {
         UICommand cmd = btn.getUiCommand();
         if(cmd != null) {
            if(cmd instanceof DeviceCommandRef) {
               DeviceCommandRef cmdRef = (DeviceCommandRef) cmd;
               for(DeviceCommand tmpCmd : uiCmds) {
                  if(tmpCmd.equals(cmdRef.getDeviceCommand())) {
                     cmdRef.setDeviceCommand(tmpCmd);
                  }
               }
               uiCmds.add(cmdRef.getDeviceCommand());
               
            } else {
//TODO Rebuild macro for a button. 
            }
         }
      }
      //get device command from sensors. 
      Collection<Sensor> sensors = getSensors(screen);
      for(Sensor sensor: sensors) {
         for (DeviceCommand cmd : uiCmds) {
            if(cmd.equals(sensor.getSensorCommandRef().getDeviceCommand())) {
               sensor.getSensorCommandRef().setDeviceCommand(cmd);
               sensor.setDevice(cmd.getDevice());
            }
         }
         uiCmds.add(sensor.getSensorCommandRef().getDeviceCommand());
      }
      return uiCmds;
   }

   private Set<Device> getDevices(Screen screen) {
      Set<Device> devices = new HashSet<Device>();
      // Because UICommand like the Slider, Switch can only select DeviceCommand from one device and the DeviceCommand only belongs to one device, the UICommand are in the same device as the DeviceCommand they have selected.
      // Therefore, we can get all the device by the DeviceCommand without get device from UICommand. 
      Collection<DeviceCommand> deviceCmds = getDeviceCommands(screen);
      for(DeviceCommand cmd : deviceCmds ) {
         Device device = cmd.getDevice();
         if(devices.contains(device)) {
            for(Device dvc : devices) {
               if(dvc.equals(device)) {
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
      for(UISlider uiSlider : uiSliders ) {
         Slider slider = uiSlider.getSlider();
         if(slider != null) {
            for(Slider sld : sliders) {
               if(slider.equals(sld)) {
                  uiSlider.setSlider(sld);
               }
            }
            sliders.add(slider);
         }
      }
      return sliders;
   }

   private Set<Switch> getSwitchs(Collection<UISwitch> uiSwitchs) {
      Set<Switch> switchs = new HashSet<Switch>();
      for(UISwitch uiSwitch : uiSwitchs) {
         Switch switchToggle = uiSwitch.getSwitchCommand();
         if(switchToggle != null) {
            for(Switch swh: switchs) {
               if(switchToggle.equals(swh)) {
                  uiSwitch.setSwitchCommand(swh);
               }
            }
            switchs.add(switchToggle);
         }
      }
      return switchs;
   }

   private Set<Sensor> getSensors(Screen screen) {
      Set<Sensor> sensors = new HashSet<Sensor>();
      for (Absolute absolute : screen.getAbsolutes()) {
         UIComponent component = absolute.getUiComponent();
         if (component instanceof SensorOwner) {
            SensorOwner sensorOwner = (SensorOwner) component;
            Sensor s = sensorOwner.getSensor();
            if(s != null) {
               for(Sensor sensor: sensors) {
                  if(sensor.equals(sensorOwner.getSensor())) {
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
               for(Sensor sensor: sensors) {
                  if(sensor.equals(sensorOwner.getSensor())) {
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
      if (component !=null ) {
         if(component instanceof UILabel ) {
            UILabel uiLabel = (UILabel) component;
            uiLabel.setSensorLinker(new SensorLink(sensorOwner.getSensor()));
         } else if (component instanceof UIImage) {
            UIImage uiImage = (UIImage) component;
            uiImage.setSensorLinker(new SensorLink(sensorOwner.getSensor()));
         }
      }
   }
   /*@SuppressWarnings("unchecked")
   private static Set<DeviceMacro> getMacors(UIComponentBox box) {
      Set<DeviceMacro> macros = new HashSet<DeviceMacro>();
      Collection<UIButton> uiButtons = (Collection<UIButton>) box.getUIComponentsByType(UIButton.class);
      for(UIButton btn : uiButtons) {
         if(btn.getUiCommand() instanceof DeviceMacroRef){
            DeviceMacroRef macroItem = (DeviceMacroRef) btn.getUiCommand();
            macros.add(macroItem.getTargetDeviceMacro());
         }
      }
      return macros;
   }*/

   private void reBuild(Collection<Device> devices,Collection<DeviceCommand> deviceCommands, Collection<Sensor> sensors,Collection<Switch> switchs,Collection<Slider> sliders){
      Account account = userService.getAccount();
     
      //1, build devices. 
      for(Device device : devices ) {
         device.setAccount(account);
         deviceService.saveDevice(device);
      }
      
      //2, build DeviceCommands. 
      for(DeviceCommand deviceCommand : deviceCommands) {
         Protocol protocol = deviceCommand.getProtocol();
         if(protocol.getAttributes() !=null) {
            for(ProtocolAttr attr : protocol.getAttributes()) {
               attr.setProtocol(protocol);
            }
         }
        deviceCommandService.save(deviceCommand);
      }
      //3, build sensors. 
      for(Sensor sensor : sensors ){
         sensor.setAccount(account);
         sensor.getSensorCommandRef().setSensor(sensor);
         sensor.setDevice(sensor.getSensorCommandRef().getDeviceCommand().getDevice());
         sensorService.saveSensor(sensor);
//         sensor.setAccount(null);
      }
      //4, build switch. 
      for(Switch switchToggle : switchs) {
         switchToggle.setAccount(account);
         switchToggle.getSwitchCommandOffRef().setOffSwitch(switchToggle);
         switchToggle.getSwitchCommandOnRef().setOnSwitch(switchToggle);
         switchToggle.setDevice(switchToggle.getSwitchCommandOffRef().getDeviceCommand().getDevice());
         switchToggle.getSwitchSensorRef().setSwitchToggle(switchToggle);
         switchService.save(switchToggle);
//         switchToggle.setAccount(null);
      }
      //5, build slider. 
      for(Slider slider : sliders) {
         slider.setAccount(account);
         slider.setDevice(slider.getSetValueCmd().getDeviceCommand().getDevice());
         slider.getSliderSensorRef().setSlider(slider);
         slider.getSetValueCmd().setSlider(slider);
         sliderService.save(slider);
//         slider.setAccount(null);
      }
      //prepare to send to client. 
      for(DeviceCommand deviceCommand : deviceCommands) {
         Protocol protocol = deviceCommand.getProtocol();
         if(protocol.getAttributes() !=null) {
            for(ProtocolAttr attr : protocol.getAttributes()) {
               attr.setProtocol(protocol);
            }
         }
        List<ProtocolAttr> attrs = new ArrayList<ProtocolAttr> ();
        for(ProtocolAttr attr : protocol.getAttributes()) {
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
      for(Device device : devices ) {
         device.setAccount(null);
         device.setSensors(new HashSet<Sensor>());
         device.setSwitchs(new HashSet<Switch>());
         device.setSliders(new HashSet<Slider>());
         device.setDeviceCommands(new ArrayList<DeviceCommand>());
      }
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

   private String encode(String namePassword) {
      if (namePassword == null) return null;
      return new String(Base64.encodeBase64(namePassword.getBytes()));
   }
}
