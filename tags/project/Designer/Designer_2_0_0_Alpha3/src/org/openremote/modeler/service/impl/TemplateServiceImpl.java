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
import java.util.List;
import java.util.Map;

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
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.exception.BeehiveNotAvailableException;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.TemplateService;
import org.openremote.modeler.service.UserService;

import flexjson.ClassLocator;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.Path;

/**
 * 
 * @author javen
 *
 */
public class TemplateServiceImpl implements TemplateService {
   private static Log log = LogFactory.getLog(TemplateService.class);

   private Configuration configuration;
   private UserService userService ;
   private ResourceService resourceService ;

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
         httpPost.setHeader(Constants.HTTP_BASIC_AUTH_HEADER_NAME, Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
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
         log.error("faild to save a screen to a template", e);
         throw new BeehiveNotAvailableException("faild to save a screen to a template", e);
      }

      log.debug("save Template Ok!");
      return screenTemplate;
   }

   private String getTemplateContent(Screen screen) {
      try {
         String[] includedPropertyNames = { "absolutes.uiComponent.uiCommand", "absolutes.uiComponent.commands",
               "grids.cells.uiComponent", "grids.cells.uiComponent.uiCommand", "grids.cells.uiComponent.commands", };
         String[] excludePropertyNames = { "grid", "*.touchPanelDefinition", "*.refCount", "*.displayName", "*.oid",
               "*.proxyInformations", "*.proxyInformation", "gestures", "*.panelXml", "*.navigate" };
         return new JSONSerializer().include(includedPropertyNames).exclude(excludePropertyNames).deepSerialize(screen);
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   @Override
   public Screen buildScreenFromTemplate(Template template) {
      String screenJson = template.getContent();
      Screen screen = new JSONDeserializer<Screen>().use(null, Screen.class).use("absolutes.values.uiComponent",
            new SimpleClassLocator()).use("grids.values.cells.values.uiComponent", new SimpleClassLocator())
            .use("absolutes.values.uiComponent.uiCommand",new SimpleClassLocator())
            .use("grids.values.cells.values.uiComponent.uiCommand",new SimpleClassLocator())
            .deserialize(screenJson);
      // download resources (eg:images) from beehive.
      resourceService.downloadResourcesForTemplate(template.getOid());
//      TemplateUtil.rebuildScreen(screen);
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
            throw new BeehiveNotAvailableException("failed to delete template ");
         }
      } catch (Exception e) {
         log.error("failed to delete template", e);
         throw new BeehiveNotAvailableException("failed to delete template ", e);
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
      return (new sun.misc.BASE64Encoder()).encode(namePassword.getBytes());
   }
}
