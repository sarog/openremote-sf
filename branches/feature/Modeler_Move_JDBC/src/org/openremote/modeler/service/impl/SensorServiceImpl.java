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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

public class SensorServiceImpl extends BaseAbstractService<Sensor> implements SensorService {

   private Configuration configuration;
   
   public Boolean deleteSensor(long id) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTSensorUrl() + "delete/" + id);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != 200) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete sensor in beehive.");
         } else {
            String result = IOUtils.toString(response.getEntity().getContent());
            if ("true".equals(result)) {
               return true;
            } else {
               return false;
            }
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed delete sensor in beehive.");
      }
   }

   public List<Sensor> loadAll(Account account) {
      List<Sensor> sensors = account.getSensors();
      for (Sensor sensor : sensors) {
         if (sensor.getType() == SensorType.CUSTOM) {
            Hibernate.initialize(((CustomSensor) sensor).getStates());
         }
      }
      return sensors;
   }

   @SuppressWarnings("unchecked")
   public Sensor saveSensor(Sensor sensor) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSensorUrl() + "save/" + sensor.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(sensor,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<Sensor>()
             .use(null, new TypeLocator<String>("classType")
                   .add("Sensor", Sensor.class)
                   .add("RangeSensor", RangeSensor.class)
                   .add("CustomSensor", CustomSensor.class)
                 ).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to save sensor to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to save sensor to beehive.");
      }
   }

   @SuppressWarnings("unchecked")
   public Sensor updateSensor(Sensor sensor) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTSensorUrl() + "update");
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      addAuthentication(httpPost);
      try {
         String[] includes = {"device"};
         String[] excludes = {"*.deviceAttrs","*.deviceCommands", "*.switchs", "*.sliders", "*.sensors","*.protocol","*.device"};
         httpPost.setEntity(new StringEntity(JsonGenerator.deepSerializerObjectInclude(sensor,includes,excludes),"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
             String returnedSensorJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<Sensor>()
             .use(null, new TypeLocator<String>("classType")
                   .add("Sensor", Sensor.class)
                   .add("RangeSensor", RangeSensor.class)
                   .add("CustomSensor", CustomSensor.class)
                 ).deserialize(returnedSensorJson);
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed to update sensor to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed to update sensor to beehive.");
      }
   }

   public Sensor loadById(long id) {
      Sensor sensor = genericDAO.getById(Sensor.class, id);
      if (sensor instanceof CustomSensor) {
         Hibernate.initialize(((CustomSensor) sensor).getStates());
      }
      return sensor;
   }

  /* @Override
   public List<Sensor> loadByDevice(Device device) {
      Device dvic = genericDAO.loadById(Device.class, device.getOid());
      return dvic.getSensors();
   }
*/
   public List<Sensor> loadSameSensors(Sensor sensor) {
      List<Sensor> result = null;
      DetachedCriteria critera = DetachedCriteria.forClass(Sensor.class);
      critera.add(Restrictions.eq("name", sensor.getName()));
      critera.add(Restrictions.eq("type", sensor.getType()));
      critera.add(Restrictions.eq("device.oid", sensor.getDevice().getId()));
      result = genericDAO.findByDetachedCriteria(critera);
      
      if (result != null) {
         for(Iterator<Sensor> iterator=result.iterator();iterator.hasNext();) {
            Sensor s = iterator.next();
            if(!s.equalsWithoutCompareOid(sensor)){
               iterator.remove();
            }
         }
      }
      return result;
   }
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
}
