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
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;

import flexjson.JSONDeserializer;

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

   @Override
   public List<Switch> loadAll() {
      List<Switch> result = userService.getAccount().getSwitches();
      if (result == null || result.size() == 0) {
         return new ArrayList<Switch> ();
      }
      Hibernate.initialize(result);
      return result;
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
   
   public List<Switch> loadSameSwitchs(Switch swh) {
      List<Switch> result = null;
      DetachedCriteria critera = DetachedCriteria.forClass(Switch.class);
      critera.add(Restrictions.eq("device.oid", swh.getDevice().getId()));
      critera.add(Restrictions.eq("name", swh.getName()));
      result = genericDAO.findByDetachedCriteria(critera);
      if (result != null) {
         for(Iterator<Switch> iterator = result.iterator();iterator.hasNext();) {
            Switch tmp = iterator.next();
            if (! tmp.equalsWithoutCompareOid(swh)) {
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
