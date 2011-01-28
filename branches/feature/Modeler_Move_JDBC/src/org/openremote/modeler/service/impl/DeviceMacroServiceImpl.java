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
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;


/**
 * Default implements of {@link DeviceMacroService}.
 */
public class DeviceMacroServiceImpl extends BaseAbstractService<DeviceMacro> implements DeviceMacroService {

   private UserService userService;

   private Configuration configuration;
   

   /**
    * For spring IOC.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadAll()
    */
   @SuppressWarnings("unchecked")
   public List<DeviceMacroItem> loadDeviceMacroItems(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTDeviceMacroUrl() + "loaditemsbyid/" + id);
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String itemsJson = IOUtils.toString(response.getEntity().getContent());
            DeviceMacroItemList result = new JSONDeserializer<DeviceMacroItemList>()
                     .use(null, DeviceMacroItemList.class)
                     .use("deviceMacroItems.values", new TypeLocator<String>("classType")
                           .add("DeviceCommandRef", DeviceCommandRef.class)
                           .add("DeviceMacroRef", DeviceMacroRef.class)
                           .add("CommandDelay", CommandDelay.class))
                     .deserialize(itemsJson);
            return result.getDeviceMacroItems();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<DeviceMacroItem>();
         } else {
            throw new BeehiveJDBCException("Failed load DeviceMacroItems from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load DeviceMacroItems from beehive.");
      }
      
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.BaseAbstractService#loadAll()
    * @see org.openremote.modeler.service.DeviceMacroService#loadAll(org.openremote.modeler.domain.Account)
    */
   @SuppressWarnings("unchecked")
   public List<DeviceMacro> loadAll(Account account) {
      
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTDeviceMacroUrl() + "loadall/" + account.getId());
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String deviceMacrosJson = IOUtils.toString(response.getEntity().getContent());
            DeviceMacroList result = new JSONDeserializer<DeviceMacroList>()
            .use(null, DeviceMacroList.class)
            .use("deviceMacros.values.deviceMacroItems.values", new TypeLocator<String>("classType")
                               .add("DeviceCommandRef", DeviceCommandRef.class)
                               .add("DeviceMacroRef", DeviceMacroRef.class)
                               .add("CommandDelay", CommandDelay.class))
             .deserialize(deviceMacrosJson);
            return result.getDeviceMacros();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<DeviceMacro>();
         } else {
            throw new BeehiveJDBCException("Failed load device macros from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load device macros from beehive.");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroService#saveDeviceMacro(org.openremote.modeler.domain.DeviceMacro)
    */
   @SuppressWarnings("unchecked")
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      deviceMacro.setAccount(userService.getAccount());
      
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceMacroUrl() + "save/" + deviceMacro.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String []includes = {"*.targetDeviceMacro"};
         String []excludes = {"*.device", "*.protocol", "*.deviceName", "*.targetDeviceMacro.deviceMacroItems"};
         String macroContent = JsonGenerator.deepSerializerObjectInclude(deviceMacro, includes, excludes);
         httpPost.setEntity(new StringEntity(macroContent,"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<DeviceMacro>()
                         .use(null, DeviceMacro.class)
                         .use("deviceMacroItems.values", new TypeLocator<String>("classType")
                               .add("DeviceCommandRef", DeviceCommandRef.class)
                               .add("DeviceMacroRef", DeviceMacroRef.class)
                               .add("CommandDelay", CommandDelay.class)
                         ).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to save device macro to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to save device macro to beehive.");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroService#updateDeviceMacro(org.openremote.modeler.domain.DeviceMacro)
    */
   @SuppressWarnings("unchecked")
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceMacroUrl() + "update");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String []includes = {"*.targetDeviceMacro"};
         String []excludes = {"*.device", "*.protocol", "*.deviceName", "*.targetDeviceMacro.deviceMacroItems"};
         String macroContent = JsonGenerator.deepSerializerObjectInclude(deviceMacro, includes, excludes);
         httpPost.setEntity(new StringEntity(macroContent,"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<DeviceMacro>()
                         .use(null, DeviceMacro.class)
                         .use("deviceMacroItems.values", new TypeLocator<String>("classType")
                               .add("DeviceCommandRef", DeviceCommandRef.class)
                               .add("DeviceMacroRef", DeviceMacroRef.class)
                               .add("CommandDelay", CommandDelay.class)
                         ).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to save device macro to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to save device macro to beehive.");
      }
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.service.DeviceMacroService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTDeviceMacroUrl() + "delete/" + id);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete deviceMacro in beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed delete deviceMacro in beehive.");
      }
   }


   public List<DeviceMacro> loadSameMacro(DeviceMacro macro, Account account) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTDeviceMacroUrl() + "loadsamemacros/" + account.getId());
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String deviceMacrosJson = IOUtils.toString(response.getEntity().getContent());
            DeviceMacroList result = new JSONDeserializer<DeviceMacroList>()
            .use(null, DeviceMacroList.class)
             .deserialize(deviceMacrosJson);
            return result.getDeviceMacros();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<DeviceMacro>();
         } else {
            throw new BeehiveJDBCException("Failed load same device macros from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load same device macros from beehive.");
      }
      
   }
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
}
