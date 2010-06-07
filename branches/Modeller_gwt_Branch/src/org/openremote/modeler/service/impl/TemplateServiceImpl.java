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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.component.ImageSource;
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
import org.openremote.modeler.utils.ScreenCmdBuilder;

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
public class TemplateServiceImpl implements TemplateService {
   private static Logger log = Logger.getLogger(TemplateServiceImpl.class);;

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
      params.add(new BasicNameValuePair("shared",screenTemplate.isShared()+""));
      params.add(new BasicNameValuePair("keywords",screenTemplate.getKeywords()));
      
      log.debug("TemplateContent" + screenTemplate.getContent());

      try {
         String saveRestUrl = configuration.getBeehiveRESTRootUrl() + "account/" + userService.getAccount().getOid()
               + "/template/";

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
         throw new BeehiveNotAvailableException("Failed to save screen as a template: " + (e.getMessage()==null?"":e.getMessage()), e);
      }

      log.debug("save Template Ok!");
      return screenTemplate;
   }

   public String getTemplateContent(ScreenPair screen) {
      try {
         String[] includedPropertyNames = { 
               "*.gestures.uiCommand",
               "*.absolutes.uiComponent.sensorLink",
               "*.absolutes.uiComponent.oid",
               "*.grids.cells.uiComponent.sensorLink",
               "*.grids.cells.uiComponent.oid",
               "*.absolutes.uiComponent.uiCommand.deviceCommand.protocol.protocalAttrs",
               "*.absolutes.uiComponent.commands",
               "*.absolutes.uiComponent.slider.sliderSensorRef.sensor",
               "*.absolutes.uiComponent.switchCommand.switchSensorRef.sensor",
               "*.grids.cells.uiComponent",
               "*.grids.cells.uiComponent.slider.sliderSensorRef.sensor",
               "*.grids.cells.uiComponent.switchCommand.switchSensorRef.sensor",
               "*.grids.cells.uiComponent.uiCommand",
               "*.grids.cells.uiComponent.uiCommand.deviceCommand.protocol.protocalAttrs",
               "*.grids.cells.uiComponent.commands", "*.deviceCommand", "*.protocol", "*.attributes" };
         String[] excludePropertyNames = { "grid", /* "*.touchPanelDefinition", */"*.refCount", "*.displayName",
               "*.oid", "*.proxyInformations", "*.proxyInformation", /* "gestures", */"*.panelXml", /* "*.navigate", */
               "*.deviceCommands", "*.sensors", "*.sliders", "*.configs", "*.switchs", "DeviceMacros" ,
               };
         return new JSONSerializer().include(includedPropertyNames).exclude(excludePropertyNames).deepSerialize(screen);
      } catch (Exception e) {
         log.error(e.getMessage(), e);
         return "";
      }
   }

   @Override
   public ScreenFromTemplate buildFromTemplate(Template template) {
      ScreenPair screen = buildScreen(template);
      resetImageSourceLocationForScreen(screen);
      
      // ---------------download resources (eg:images) from beehive.
      resourceService.downloadResourcesForTemplate(template.getOid());
      return reBuildCommand(screen);
   }

   @Override
   public ScreenPair buildScreen(Template template) {
      String screenJson = template.getContent();
      ScreenPair screenPair = new JSONDeserializer<ScreenPair>()
            .use(null, ScreenPair.class)
            // portraitScreen
            .use("portraitScreen.absolutes.values.uiComponent", new SimpleClassLocator())
            .use("portraitScreen.grids.values.cells.values.uiComponent", new SimpleClassLocator())
            //1,absolutes
            //    1.1, uiCommand
            .use("portraitScreen.absolutes.values.uiComponent.uiCommand",new SimpleClassLocator())
            .use("portraitScreen.gestures.values.uiCommand",new SimpleClassLocator())
            //    1.2, sensor 
            .use("portraitScreen.absolutes.values.uiComponent.sensor",new SimpleClassLocator())
            .use("portraitScreen.absolutes.values.uiComponent.slider.sliderSensorRef.sensor",new SimpleClassLocator())
            .use("portraitScreen.absolutes.values.uiComponent.switchCommand.switchCommandOnRef.sensor",new SimpleClassLocator())
            .use("portraitScreen.absolutes.values.uiComponent.switchCommand.switchCommandOffRef.sensor",new SimpleClassLocator())
            //2,grids
            //    2.1 uiCommand
            .use("portraitScreen.grids.values.cells.values.uiComponent.uiCommand",new SimpleClassLocator())
            //    2.2 sensor 
            .use("portraitScreen.grids.values.cells.values.uiComponent.sensor",new SimpleClassLocator())
            .use("portraitScreen.grids.values.cells.values.uiComponent.slider.sliderSensorRef.sensor",new SimpleClassLocator())
            .use("portraitScreen.grids.values.cells.values.uiComponent.switchCommand.switchCommandOnRef.sensor",new SimpleClassLocator())
            .use("portraitScreen.grids.values.cells.values.uiComponent.switchCommand.switchCommandOffRef.sensor",new SimpleClassLocator())
            // landscapeScreen
            .use("landscapeScreen.absolutes.values.uiComponent", new SimpleClassLocator())
            .use("landscapeScreen.grids.values.cells.values.uiComponent", new SimpleClassLocator())
            //1,absolutes
            //    1.1, uiCommand
            .use("landscapeScreen.absolutes.values.uiComponent.uiCommand",new SimpleClassLocator())
            .use("landscapeScreen.gestures.values.uiCommand",new SimpleClassLocator())
            //    1.2, sensor 
            .use("landscapeScreen.absolutes.values.uiComponent.sensor",new SimpleClassLocator())
            .use("landscapeScreen.absolutes.values.uiComponent.slider.sliderSensorRef.sensor",new SimpleClassLocator())
            .use("landscapeScreen.absolutes.values.uiComponent.switchCommand.switchCommandOnRef.sensor",new SimpleClassLocator())
            .use("landscapeScreen.absolutes.values.uiComponent.switchCommand.switchCommandOffRef.sensor",new SimpleClassLocator())
            //2,grids
            //    2.1 uiCommand
            .use("landscapeScreen.grids.values.cells.values.uiComponent.uiCommand",new SimpleClassLocator())
            //    2.2 sensor 
            .use("landscapeScreen.grids.values.cells.values.uiComponent.sensor",new SimpleClassLocator())
            .use("landscapeScreen.grids.values.cells.values.uiComponent.slider.sliderSensorRef.sensor",new SimpleClassLocator())
            .use("landscapeScreen.grids.values.cells.values.uiComponent.switchCommand.switchCommandOnRef.sensor",new SimpleClassLocator())
            .use("landscapeScreen.grids.values.cells.values.uiComponent.switchCommand.switchCommandOffRef.sensor",new SimpleClassLocator())
            .deserialize(screenJson);
      screenPair.resetGestures();
      return screenPair;
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
      String shared = fromPrivate ? "private" : "public";
      List<Template> templates = new ArrayList<Template>();
      String restURL = configuration.getBeehiveRESTRootUrl() + "account/" + userService.getAccount().getOid()
            + "/templates/" + shared;

      HttpGet httpGet = new HttpGet(restURL);
      httpGet.setHeader("Accept", "application/json");
      this.addAuthentication(httpGet);
      HttpClient httpClient = new DefaultHttpClient();

      try {
         HttpResponse response = httpClient.execute(httpGet);

         if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User "+userService.getCurrentUser().getUsername() + " not authenticated! ");
            }
            throw new BeehiveNotAvailableException("Beehive is not available right now! ");
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
         List<TemplateFromBeehive> dtoes = templateList.getTemplates();

         for (TemplateFromBeehive dto : dtoes) {
            templates.add(dto.toTemplate());
         }
      } catch (IOException e) {
         throw new BeehiveNotAvailableException("Failed to get template list, The beehive is not available right now ", e);
      }

      return templates;
   }
   
   public List<Template> getTemplatesByKeywordsAndPage(String keywords,int page) {
      String newKeywords = keywords;
      if (keywords == null || keywords.trim().length() == 0) {
         newKeywords = TemplateService.NO_KEYWORDS;
      }
      List<Template> templates = new ArrayList<Template>();
      String restURL = configuration.getBeehiveRESTRootUrl() + "templates/keywords/"
            + newKeywords + "/page/"+page;

      HttpGet httpGet = new HttpGet(restURL);
      httpGet.setHeader("Accept", "application/json");
      this.addAuthentication(httpGet);
      HttpClient httpClient = new DefaultHttpClient();

      try {
         HttpResponse response = httpClient.execute(httpGet);

         if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User "+userService.getCurrentUser().getUsername() + " not authenticated! ");
            }
            throw new BeehiveNotAvailableException("Beehive is not available right now! ");
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
         List<TemplateFromBeehive> dtoes = templateList.getTemplates();

         for (TemplateFromBeehive dto : dtoes) {
            templates.add(dto.toTemplate());
         }
      } catch (IOException e) {
         throw new BeehiveNotAvailableException("Failed to get template list, The beehive is not available right now ", e);
      }

      return templates;
   }
   
   @Override
   public ScreenFromTemplate reBuildCommand(ScreenPair screenPair) {
      List<Screen> screens = screenPair.getScreens();
      ScreenCmdBuilder cmdBuilder = new ScreenCmdBuilder(screens);
      rebuild(cmdBuilder);

      return new ScreenFromTemplate(cmdBuilder.getDevices(), screenPair, cmdBuilder.getMacros());
   }

   private void rebuild(ScreenCmdBuilder cmdBuilder) {
      Account account = userService.getAccount();
     
      buildDevices(cmdBuilder.getDevices(), account);
      buildDeviceCommands(cmdBuilder.getDeviceCommands());
      buildSensors(cmdBuilder.getSensors(), account);
      buildSwitchs(cmdBuilder.getSwitches(), account);
      buildSliders(cmdBuilder.getSliders(), account);
      buildMacros(cmdBuilder.getMacros(), account);
      
      prepareToSendToClient(cmdBuilder, account);
   }

   private void buildMacros(Collection<DeviceMacro> macros, Account account) {
      for (DeviceMacro macro : macros) {
         macro.setAccount(account);
         saveMacro(macro);
      }
   }

   private void buildSliders(Collection<Slider> sliders, Account account) {
      for (Slider slider : sliders) {
         slider.setAccount(account);
         slider.initCmdSensorAndDevice();
         List<Slider> sameSliders = sliderService.loadSameSliders(slider);
         if (sameSliders != null && sameSliders.size() >0) {
            slider.setOid(sameSliders.get(0).getOid());
         } else {
            sliderService.save(slider);
         }
      }
   }

   private void buildSwitchs(Collection<Switch> switches, Account account) {
      for (Switch switchToggle : switches) {
         switchToggle.setAccount(account);
         switchToggle.initCmdsDeviceAndSensor();
         List<Switch> swhs = switchService.loadSameSwitchs(switchToggle);
         if (swhs !=null && swhs.size() >0) {
            switchToggle.setOid(swhs.get(0).getOid());
         } else {
            switchService.save(switchToggle);
         }
      }
   }

   private void buildSensors(Collection<Sensor> sensors, Account account) {
      for (Sensor sensor : sensors) {
         sensor.setAccount(account);
         sensor.initCmdAndDevice();
         List<Sensor> sameSensors = sensorService.loadSameSensors(sensor);
         if (sameSensors != null && sameSensors.size() >0) {
            sensor.setOid(sameSensors.get(0).getOid());
         } else {
            sensorService.saveSensor(sensor);
         }
      }
   }

   private void buildDeviceCommands(Collection<DeviceCommand> deviceCommands) {
      for (DeviceCommand deviceCommand : deviceCommands) {
         Protocol protocol = deviceCommand.getProtocol();
         protocol.initAttributes();
         List<DeviceCommand> sameCmds = deviceCommandService.loadSameCommands(deviceCommand);
         if (sameCmds != null && sameCmds.size() >0) {
            deviceCommand.setOid(sameCmds.get(0).getOid());
         } else {
            deviceCommandService.save(deviceCommand);
         }
      }
   }

   private void buildDevices(Collection<Device> devices, Account account) {
      for (Device device : devices) {
         device.setAccount(account);
         List<Device> sameDevices = deviceService.loadSameDevices(device);
         if (sameDevices != null && sameDevices.size() >0) {
            device.setOid(sameDevices.get(0).getOid());
         } else {
            deviceService.saveDevice(device);
         }
      }
   }

   private void prepareToSendToClient(ScreenCmdBuilder cmdBuilder, Account account) {
      // Because some of the domain classes are lazy loaded by hibernate, 
      // we need replace some hibernate proxy classes with the class declared in their own class. 
      // (for example we may need replace PersistentBag with ArrayList.)
      // so that they can be serialized by GWT. 
      for (DeviceCommand deviceCommand : cmdBuilder.getDeviceCommands()) {
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
      account.setUsers(new ArrayList<User>());
      userService.getCurrentUser().setRoles(new ArrayList<Role>());

      for (Device device : cmdBuilder.getDevices() ) {
         device.setAccount(null);
         device.setSensors(new ArrayList<Sensor>());
         device.setSwitchs(new ArrayList<Switch>());
         device.setSliders(new ArrayList<Slider>());
         device.setDeviceCommands(new ArrayList<DeviceCommand>());
      }

      for (DeviceMacro macro : cmdBuilder.getMacros()) {
         macro.setAccount(null);
         List<DeviceMacroItem> items = new ArrayList<DeviceMacroItem>();
         for (DeviceMacroItem item: macro.getDeviceMacroItems()) {
            items.add(item);
         }
         macro.setDeviceMacroItems(items);
      }
   }
   
   private void saveMacro(DeviceMacro macro) {
      if (null != macro) {
         List<DeviceMacroItem> items = macro.getDeviceMacroItems();
         //save the macros belongs to it. 
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
         // save the macro itself.  
         macro.setAccount(userService.getAccount());
         List<DeviceMacro> sameMacro = deviceMacroService.loadSameMacro(macro);
         if (sameMacro != null && sameMacro.size() >0) {
            macro.setOid(sameMacro.get(0).getOid());
         } else {
            this.deviceMacroService.saveDeviceMacro(macro);
         }
      }
   }
   
   
   private String encode(String namePassword) {
      if (namePassword == null) return null;
      return new String(Base64.encodeBase64(namePassword.getBytes()));
   }
   
   private void addAuthentication(AbstractHttpMessage httpMessage) {
      httpMessage.setHeader(Constants.HTTP_BASIC_AUTH_HEADER_NAME, Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + encode(userService.getCurrentUser().getUsername() + ":"
                  + userService.getCurrentUser().getPassword()));
   }
   
   private TemplateList buildTemplateListFromJson(String templatesJson) {
      TemplateList result = new TemplateList();

     //The json string from beehive is not easy to be convert to java object by FlexJson, so we remove and replace the unnecessary characters.
      try {
         String validTemplatesJson = "";

         if (templatesJson.contains("{\"template\":")) {
            if (templatesJson.contains("{\"template\":[")) {
               String tempString = templatesJson.replaceFirst("\\{\"template\":", "");
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
   static class SimpleClassLocator implements ClassLocator {

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
      private List<TemplateFromBeehive> templates = new ArrayList<TemplateFromBeehive> ();

      public List<TemplateFromBeehive> getTemplates() {
         return templates;
      }

      public void setTemplates(List<TemplateFromBeehive> templates) {
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
   public static class TemplateFromBeehive {
      private int id;
      private String content;
      private String name;
      private String keywords;
      private boolean shared = false;

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
      
      
      public String getKeywords() {
         return keywords;
      }

      public void setKeywords(String keywords) {
         this.keywords = keywords;
      }
      
      public boolean isShared() {
         return shared;
      }

      public void setShared(boolean shared) {
         this.shared = shared;
      }

      public Template toTemplate() {
         Template template = new Template();
         template.setName(name);
         template.setContent(content);
         template.setOid(id);
         template.setKeywords(keywords);
         template.setShared(shared);
         return template;
      }
   }

   @Override
   public Template updateTemplate(Template template) {
      template.setContent(getTemplateContent(template.getScreen()));
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("name", template.getName()));
      params.add(new BasicNameValuePair("content", template.getContent()));
      params.add(new BasicNameValuePair("shared",template.isShared()+""));
      params.add(new BasicNameValuePair("keywords",template.getKeywords()));
      
      try {
         String saveRestUrl = configuration.getBeehiveRESTRootUrl() + "account/" + userService.getAccount().getOid()
               + "/template/" + template.getOid();
         HttpPut httpPut = new HttpPut(saveRestUrl);
         addAuthentication(httpPut);
         UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
         httpPut.setEntity(formEntity);

         HttpClient httpClient = new DefaultHttpClient();
         HttpResponse response = httpClient.execute(httpPut);
         if (HttpServletResponse.SC_OK == response.getStatusLine().getStatusCode()) {
            resourceService.saveTemplateResourcesToBeehive(template);
         } else if (HttpServletResponse.SC_NOT_FOUND == response.getStatusLine().getStatusCode()) {
            return null;
         } else {
            throw new BeehiveNotAvailableException("Failed to update template:"+template.getName()+", Status code: "+response.getStatusLine().getStatusCode());
         }
      } catch (Exception e) {
         throw new BeehiveNotAvailableException("Failed to save screen as a template: "
               + (e.getMessage() == null ? "" : e.getMessage()), e);
      }
      return template;
   }
   
   private void resetImageSourceLocationForScreen(ScreenPair sp) {
      String accountPath = resourceService.getRelativeResourcePathByCurrentAccount("account");
      accountPath = accountPath.substring(0, accountPath.lastIndexOf("/") + 1);
      Collection<ImageSource> images = sp.getAllImageSources();
      if (images != null && images.size() >0) {
         for(ImageSource image: images) {
            String imageFileName = image.getImageFileName();
            image.setSrc(accountPath+imageFileName);
         }
      }
   }
}
