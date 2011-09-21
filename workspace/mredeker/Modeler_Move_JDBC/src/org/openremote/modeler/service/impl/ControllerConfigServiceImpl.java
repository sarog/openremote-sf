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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.XmlParser;

import flexjson.JSONDeserializer;

/**
 * The Interface ControllerConfigService for operating controller configurations.
 * 
 * @author javen, tomsky
 */
public class ControllerConfigServiceImpl extends BaseAbstractService<ControllerConfig> implements ControllerConfigService {
   private UserService userService = null;
   private Configuration configuration;
   
   @Override
   public Set<ControllerConfig>listAllConfigsByCategoryNameForAccount(String categoryName,Account account) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTControllerCongigUrl() + "load/" + account.getId() + "/" + categoryName);
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String itemsJson = IOUtils.toString(response.getEntity().getContent());
            ControllerConfigList result = new JSONDeserializer<ControllerConfigList>()
                     .use(null, ControllerConfigList.class).deserialize(itemsJson);
            List<ControllerConfig> configs = result.getControllerConfigs();
            Set<ControllerConfig> configSet = new LinkedHashSet<ControllerConfig>();
            configSet.addAll(configs);
            configSet.removeAll(listAllexpiredConfigs());
            initializeConfigs(configSet);
            return configSet;
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed load controller configs from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load controller configs from beehive.");
      }
      
   }
   
   public Set<ControllerConfig> saveAll(Set<ControllerConfig> configs) {
      String[] excludes = {"*.class","*.hint","*.validation","*.options"};
      String json = JsonGenerator.serializerObjectExcludeWithRoot(configs, excludes, "controllerConfigs");
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTControllerCongigUrl() + "saveall/" + userService.getAccount().getId());
      httpPost.setHeader("Content-Type", "application/json");
      addAuthentication(httpPost);
      try {
         httpPost.setEntity(new StringEntity(json,"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String itemsJson = IOUtils.toString(response.getEntity().getContent());
            ControllerConfigList result = new JSONDeserializer<ControllerConfigList>()
                     .use(null, ControllerConfigList.class).deserialize(itemsJson);
            List<ControllerConfig> dbConfigs = result.getControllerConfigs();
            Set<ControllerConfig> cfgs = new LinkedHashSet<ControllerConfig>();
            cfgs.addAll(dbConfigs);
            initializeConfigs(cfgs);
            return cfgs;
         } else if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed save controller configs to beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Failed save controller configs to beehive.");
      }
      
   }

   @Override
   public Set<ControllerConfig> listAllConfigsByCategory(String categoryName) {
      Account account = userService.getAccount();
      return this.listAllConfigsByCategoryNameForAccount(categoryName, account);
   }

   @Override
   public Set<ControllerConfig> listAllByAccount(Account account) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTControllerCongigUrl() + "loadall/" + account.getId());
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String itemsJson = IOUtils.toString(response.getEntity().getContent());
            ControllerConfigList result = new JSONDeserializer<ControllerConfigList>()
                     .use(null, ControllerConfigList.class).deserialize(itemsJson);
            List<ControllerConfig> configs = result.getControllerConfigs();
            Set<ControllerConfig> configSet = new LinkedHashSet<ControllerConfig>();
            configSet.addAll(configs);
            initializeConfigs(configSet);
            return configSet;
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed load all controller configs from beehive.");
         }
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't load all controller configs from beehive.");
      }
      
   }

   @Override
   public Set<ControllerConfig> listAllConfigs() {
     Account account = userService.getAccount();
     return listAllByAccount(account);
   }

   @Override
   public Set<ConfigCategory> listAllCategory() {
      Set<ConfigCategory> categories = new LinkedHashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new LinkedHashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      return categories;
   }
   @Override
   public Set<ControllerConfig> listMissedConfigsByCategoryName(String categoryName) {
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      
      Set<ControllerConfig> unMissedConfigs = this.listAllConfigsByCategory(categoryName);
      Set<ControllerConfig> missedConfigs = new HashSet<ControllerConfig> ();
      for (ControllerConfig cfg : allDefaultConfigs) {
         if (cfg.getCategory().equals(categoryName) && !unMissedConfigs.contains(cfg)) {
            missedConfigs.add(cfg);
         }
      }
      return missedConfigs;
   }
   @Override
   public Set<ControllerConfig> listAllMissingConfigs() {
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      
      Set<ControllerConfig> unMissedConfigs = this.listAllConfigs();
      Set<ControllerConfig> missedConfigs = new HashSet<ControllerConfig> ();
      for (ControllerConfig cfg : allDefaultConfigs) {
         if (!unMissedConfigs.contains(cfg)) {
            missedConfigs.add(cfg);
         }
      }
      return missedConfigs;
   }
   
   public Set<ControllerConfig> listAllexpiredConfigs() {
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(new HashSet<ConfigCategory>(), allDefaultConfigs);
      Set<ControllerConfig> allSavedConfigs = this.listAllConfigs();
      Set<ControllerConfig> expiredConfigs = new HashSet<ControllerConfig>();
      for (ControllerConfig config: allSavedConfigs) {
         if (!allDefaultConfigs.contains(config)) {
            expiredConfigs.add(config);
         }
      }
      
      return expiredConfigs;
   }
   public void setUserService(UserService userService) {
      this.userService = userService;
   }
   private static void initializeConfigs(Set<ControllerConfig> configs){
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      for(ControllerConfig cfg : configs){
         ControllerConfig oldCfg = cfg;
         for(ControllerConfig tmp: allDefaultConfigs){
            if(tmp.getName().equals(cfg.getName())&& tmp.getCategory().equals(cfg.getCategory())){
               oldCfg = tmp;
               break;
            }
         }
         cfg.setHint(oldCfg.getHint());
         cfg.setOptions(oldCfg.getOptions());
         cfg.setValidation(oldCfg.getValidation());
      }
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
}
