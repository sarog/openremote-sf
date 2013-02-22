/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

public class DeviceServiceImpl extends BaseAbstractService<Device> implements DeviceService {

   private static Logger log = Logger.getLogger(DeviceServiceImpl.class);
   
   private Configuration configuration;
   
   /**
    * {@inheritDoc}
    */
   public Device saveDevice(Device device) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceUrl() + "save/" + device.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json");
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
    	 String []excludes = {"*.class", "*.device", "sensors", "switchs", "sliders", "deviceCommands"};
    	 String json = JsonGenerator.deepSerializerObjectExclude(device, excludes);
    	 System.out.println(json);
         httpPost.setEntity(new StringEntity(json, "UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() == 200) {
            String deviceJson = IOUtils.toString(response.getEntity().getContent());
            System.out.println("-----------");
            System.out.println(deviceJson);
            return new JSONDeserializer<Device>().use(null, Device.class).deserialize(deviceJson);
         } else if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed save device to beehive.");
         }
      } catch (IOException e) {
         log.error("Can't save device to beehive.", e);
         throw new BeehiveJDBCException("Failed save device to beehive.");
      }
   }

   /**
    * {@inheritDoc}
    */
   public void deleteDevice(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTDeviceUrl() + "delete/" + id);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != 200) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete device in beehive.");
         }
      } catch (IOException e) {
         log.error("Failed delete device in beehive.", e);
         throw new BeehiveJDBCException("Failed delete device in beehive.");
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<Device> loadAll(Account account) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTDeviceUrl() + "loadall/" + account.getId());
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String devicesJson = IOUtils.toString(response.getEntity().getContent());
            DeviceList result = new JSONDeserializer<DeviceList>().use(null, DeviceList.class).use("devices",
                  ArrayList.class).deserialize(devicesJson);
            return result.getDevices();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<Device>();
         } else {
            throw new BeehiveJDBCException("Failed load device from beehive.");
         }
      } catch (IOException e) {
         log.error("Can't load account devices from beehive.", e);
         throw new BeehiveJDBCException("Can't load account devices from beehive.");
      }
   }

   /**
    * {@inheritDoc}
    */
   public void updateDevice(Device device) {
      device.toSimple();
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceUrl() + "update");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
    	  String []excludes = {"*.class", "account", "*.device", "sensors", "switchs", "sliders", "deviceCommands"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectExclude(device, excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() != 200) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed update device to beehive.");
         }
      } catch (IOException e) {
         log.error("Can't update device to beehive.", e);
         throw new BeehiveJDBCException("Failed update device to beehive.");
      }
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Device loadById(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      String url = configuration.getBeehiveRESTDeviceUrl() + "load/" + id;
      HttpGet httpGet = new HttpGet(url);
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         if (response.getStatusLine().getStatusCode() == 200) {

            String deviceJson = IOUtils.toString(response.getEntity().getContent());
            Device device = new JSONDeserializer<Device>()
               .use(null, Device.class)
               .use("sensors.values", new TypeLocator<String>("classType")
                  .add("Sensor", Sensor.class)
                  .add("RangeSensor", RangeSensor.class)
                  .add("CustomSensor", CustomSensor.class)
                ).deserialize(deviceJson);
            for (DeviceCommand command : device.getDeviceCommands()) {
               command.setDevice(device);
            }
            return device;
         } else if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Can't load device content from beehive.");
         }
      } catch (IOException e) {
         log.error("Can't load device content from beehive.", e);
         throw new BeehiveJDBCException("Can't load device content from beehive.");
      }
   }

   public List<Device> loadSameDevices(Device device) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceUrl() + "loadsamedevices/" + device.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         device.toSimple();
         device.setDeviceAttrs(null);// not compare it
         httpPost.setEntity(new StringEntity(JsonGenerator.serializerObject(device),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String devicesJson = IOUtils.toString(response.getEntity().getContent());
            DeviceList result = new JSONDeserializer<DeviceList>().use(null, DeviceList.class).use("devices",
                  ArrayList.class).deserialize(devicesJson);
            return result.getDevices();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<Device>();
         } else {
            throw new BeehiveJDBCException("Failed load same devices from beehive.");
         }
      } catch (IOException e) {
         log.error("Can't load same devices from beehive.", e);
         throw new BeehiveJDBCException("Can't load same devices from beehive.");
      }
      
   }
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   @SuppressWarnings("unchecked")
   public Device saveDeviceWithContent(Device device) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceUrl() + "savewithcontent/" + device.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json");
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String []excludes = {"*.class", "*.device"};
         String deviceContent = JsonGenerator.deepSerializerObjectExclude(device, excludes);
         httpPost.setEntity(new StringEntity(deviceContent, "UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() == 200) {
            String deviceJson = IOUtils.toString(response.getEntity().getContent());
            Device dbDevice = new JSONDeserializer<Device>()
               .use(null, Device.class)
               .use("sensors.values", new TypeLocator<String>("classType")
                  .add("Sensor", Sensor.class)
                  .add("RangeSensor", RangeSensor.class)
                  .add("CustomSensor", CustomSensor.class)
                ).deserialize(deviceJson);
            for (DeviceCommand command : dbDevice.getDeviceCommands()) {
               command.setDevice(dbDevice);
            }
            return dbDevice;
         } else if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed save device to beehive.");
         }
      } catch (IOException e) {
         log.error("Can't save device to beehive.", e);
         throw new BeehiveJDBCException("Failed save device to beehive.");
      }
         
   }
   
}
