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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;

/**
 * The implementation for DeviceCommandService interface.
 * 
 * @author Allen, Tomsky
 */
public class DeviceCommandServiceImpl extends BaseAbstractService<DeviceCommand> implements DeviceCommandService {

   private static Logger log = Logger.getLogger(DeviceCommandServiceImpl.class);
 

   private Configuration configuration;
   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#saveAll(java.util.List)
    */
   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      
      String[] excludes = {"*.class","*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors"};
      String json = "{\"deviceCommands\":" + JsonGenerator.deepSerializerObjectExclude(deviceCommands, excludes) + "}";
      
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceCommandUrl() + "saveall");
      httpPost.setHeader("Content-Type", "application/json");
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         httpPost.setEntity(new StringEntity(json,"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
            String deviceCommandJson = IOUtils.toString(response.getEntity().getContent());
            DeviceCommandList result = new JSONDeserializer<DeviceCommandList>().use(null, DeviceCommandList.class).use("deviceCommands",ArrayList.class).deserialize(deviceCommandJson);
            return result.getDeviceCommands();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
           throw new BeehiveJDBCException("Failed to save device commands to beehive.");
        }
      } catch (UnsupportedEncodingException e) {
         throw new BeehiveJDBCException("Failed to save device commands to beehive.");
      } catch (ClientProtocolException e) {
         throw new BeehiveJDBCException("Failed to save device commands to beehive.");
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to save device commands to beehive.");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#save(org.openremote.modeler.domain.DeviceCommand)
    */
   public DeviceCommand save(DeviceCommand deviceCommand) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceCommandUrl() + "save");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] excludes = {"*.class","device.deviceAttrs","device.deviceCommands", "device.switchs", "device.sliders", "device.sensors", "*.deviceCommand"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectExclude(deviceCommand,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
             String deviceCommandJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<DeviceCommand>().use(null, DeviceCommand.class).deserialize(deviceCommandJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to save device command to beehive.");
         }
      } catch (IOException e) {
         log.error("Failed to save device command from beehive.", e);
         throw new BeehiveJDBCException("Failed to save device command to beehive.");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#deleteCommand(long)
    */
   public Boolean deleteCommand(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTDeviceCommandUrl() + "delete/" + id);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != 200) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete device command in beehive.");
         } else {
            String result = IOUtils.toString(response.getEntity().getContent());
            if ("true".equals(result)) {
               return true;
            } else {
               return false;
            }
         }
      } catch (IOException e) {
         log.error("Failed delete device command in beehive.", e);
         throw new BeehiveJDBCException("Failed delete device command in beehive.");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#update(org.openremote.modeler.domain.DeviceCommand)
    */
   public DeviceCommand update(DeviceCommand deviceCommand) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceCommandUrl() + "update");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] excludes = { "*.class", "device.deviceAttrs", "device.deviceCommands", "device.switchs",
               "device.sliders", "device.sensors" };
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectExclude(deviceCommand, excludes),
               "UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() == 200) {
            String deviceCommandJson = IOUtils.toString(response.getEntity().getContent());
            return new JSONDeserializer<DeviceCommand>().use(null, DeviceCommand.class).deserialize(deviceCommandJson);
         } else {
            throw new BeehiveJDBCException("Failed to update device command to beehive.");
         }
      } catch (IOException e) {
         log.error("Can't update device command to beehive.", e);
         throw new BeehiveJDBCException("Failed update device command to beehive.");
      }

   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadById(long)
    */
   public DeviceCommand loadById(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTDeviceCommandUrl() + "load/" + id);
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         if (response.getStatusLine().getStatusCode() == 200) {
             String deviceCommandJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<DeviceCommand>().use(null, DeviceCommand.class).deserialize(deviceCommandJson);
         } else {
            throw new BeehiveJDBCException("Failed get device command from beehive.");
         }
      } catch (IOException e) {
         log.error("Failed get device command from beehive.", e);
         throw new BeehiveJDBCException("Failed get device command from beehive.");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceCommandService#loadByDevice(long)
    */
   public List<DeviceCommand> loadByDevice(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTDeviceCommandUrl() + "loadbydevice/" + id);
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String deviceCommandsJson = IOUtils.toString(response.getEntity().getContent());
            DeviceCommandList result = new JSONDeserializer<DeviceCommandList>().use(null, DeviceCommandList.class).use("deviceCommands",
                  ArrayList.class).deserialize(deviceCommandsJson);
            return result.getDeviceCommands();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<DeviceCommand>();
         } else {
            throw new BeehiveJDBCException("Failed load device commands from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load device commands from beehive.");
      }
   }

   public List<DeviceCommand> loadSameCommands(DeviceCommand deviceCommand) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceCommandUrl() + "loadsamecommands");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] excludes = { "*.class", "device.deviceAttrs", "device.deviceCommands", "device.switchs",
               "device.sliders", "device.sensors", "protocol" };
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectExclude(deviceCommand, excludes),
               "UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String deviceCommandsJson = IOUtils.toString(response.getEntity().getContent());
            DeviceCommandList result = new JSONDeserializer<DeviceCommandList>().use(null, DeviceCommandList.class).use("deviceCommands",
                  ArrayList.class).deserialize(deviceCommandsJson);
            return result.getDeviceCommands();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<DeviceCommand>();
         } else {
            throw new BeehiveJDBCException("Failed load same commands from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load same commands from beehive.");
      }
   }
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
}
