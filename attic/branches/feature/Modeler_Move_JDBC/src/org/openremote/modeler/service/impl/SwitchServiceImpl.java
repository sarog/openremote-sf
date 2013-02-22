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
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

public class SwitchServiceImpl extends BaseAbstractService<Switch> implements SwitchService {
   private Configuration configuration;
   private UserService userService = null;

   public void delete(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTSwitchUrl() + "delete/" + id);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete switch in beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed delete switch in beehive.");
      }
   }

   @SuppressWarnings("unchecked")
   public List<Switch> loadAll() {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTSwitchUrl() + "loadall/" + userService.getAccount().getId());
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String switchJson = IOUtils.toString(response.getEntity().getContent());
            SwitchList result = new JSONDeserializer<SwitchList>()
               .use(null, SwitchList.class)
               .use("switchs", ArrayList.class)
               .use("switchs.values.switchSensorRef.sensor", new TypeLocator<String>("classType")
                  .add("Sensor", Sensor.class)
                  .add("RangeSensor", RangeSensor.class)
                  .add("CustomSensor", CustomSensor.class)
                ).deserialize(switchJson);
            return result.getSwitchs();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<Switch>();
         } else {
            throw new BeehiveJDBCException("Failed load account switchs from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load account switchs from beehive.");
      }
   }


   public Switch save(Switch switchToggle) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSwitchUrl() + "save/" + switchToggle.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(switchToggle,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<Switch>()
             .use(null, Switch.class).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to save switch to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to save switch to beehive.");
      }
   }

   public Switch update(Switch switchToggle) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSwitchUrl() + "update");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(switchToggle,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<Switch>()
             .use(null, Switch.class).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to update switch to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to update switch to beehive.");
      }
   }
   
   public UserService getUserService() {
      return userService;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   
   @SuppressWarnings("unchecked")
   public List<Switch> loadSameSwitchs(Switch swh) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSwitchUrl() + "loadsameswitchs");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device","switchSensorRef.sensor.device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(swh,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String switchJson = IOUtils.toString(response.getEntity().getContent());
            SwitchList result = new JSONDeserializer<SwitchList>()
               .use(null, SwitchList.class)
               .use("switchs", ArrayList.class)
               .use("switchs.values.switchSensorRef.sensor", new TypeLocator<String>("classType")
                  .add("Sensor", Sensor.class)
                  .add("RangeSensor", RangeSensor.class)
                  .add("CustomSensor", CustomSensor.class)
                ).deserialize(switchJson);
            return result.getSwitchs();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<Switch>();
         } else {
            throw new BeehiveJDBCException("Failed load same switchs from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to same switchs from beehive.");
      }
   }
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
}
