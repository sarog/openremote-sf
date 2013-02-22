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
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

public class SliderServiceImpl extends BaseAbstractService<Slider> implements SliderService {

   private Configuration configuration;
   private UserService userService = null;
   
   public void delete(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTSliderUrl() + "delete/" + id);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete slider in beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed delete slider in beehive.");
      }
   }

   @SuppressWarnings("unchecked")
   public List<Slider> loadAll() {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTSliderUrl() + "loadall/" + userService.getAccount().getId());
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String sliderJson = IOUtils.toString(response.getEntity().getContent());
            SliderList result = new JSONDeserializer<SliderList>()
               .use(null, SliderList.class)
               .use("sliders", ArrayList.class)
               .use("sliders.values.sliderSensorRef.sensor", new TypeLocator<String>("classType")
                  .add("Sensor", Sensor.class)
                  .add("RangeSensor", RangeSensor.class)
                  .add("CustomSensor", CustomSensor.class)
                ).deserialize(sliderJson);
            return result.getSliders();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<Slider>();
         } else {
            throw new BeehiveJDBCException("Failed load account sliders from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load account sliders from beehive.");
      }
   }

   public Slider save(Slider slider) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSliderUrl() + "save/" + slider.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(slider,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<Slider>()
             .use(null, Slider.class).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to save slider to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to save slider to beehive.");
      }
   }

   public Slider update(Slider slider) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSliderUrl() + "update");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(slider,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<Slider>()
             .use(null, Slider.class).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to update slider to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to update slider to beehive.");
      }
   }
   
   @SuppressWarnings("unchecked")
   public List<Slider> loadSameSliders(Slider slider) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSliderUrl() + "loadsamesliders");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device", "sliderSensorRef.sensor.device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(slider,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String sliderJson = IOUtils.toString(response.getEntity().getContent());
            SliderList result = new JSONDeserializer<SliderList>()
               .use(null, SliderList.class)
               .use("sliders", ArrayList.class)
               .use("sliders.values.sliderSensorRef.sensor", new TypeLocator<String>("classType")
                  .add("Sensor", Sensor.class)
                  .add("RangeSensor", RangeSensor.class)
                  .add("CustomSensor", CustomSensor.class)
                ).deserialize(sliderJson);
            return result.getSliders();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<Slider>();
         } else {
            throw new BeehiveJDBCException("Failed load same sliders from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to same sliders from beehive.");
      }
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }
}
