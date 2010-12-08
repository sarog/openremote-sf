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
package org.openremote.modeler.listener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openremote.modeler.SpringContext;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.context.ApplicationEvent;

import flexjson.JSONSerializer;


/**
 * Init application when web server is started.
 * It make sure the resource folder("modeler_tmp") be created.
 * 
 * @see ApplicationEvent
 * @author Tomsky, Dan
 */
public class ApplicationListener implements ServletContextListener {
   
   private UserService userService = (UserService) SpringContext.getInstance().getBean("userService");

   public void contextDestroyed(ServletContextEvent event) {
      ;//do nothing
   }

   public void contextInitialized(ServletContextEvent event) {
      // set web root, eg: "E:\apache-tomcat-5.5.28\webapps\modeler\".
      PathConfig.WEBROOTPATH = event.getServletContext().getRealPath("/");
      File tempFolder = new File(PathConfig.WEBROOTPATH + File.separator + PathConfig.RESOURCEFOLDER);
      if (!tempFolder.exists()) {
         tempFolder.mkdirs();
      }
      userService.initRoles();
//      saveControllerConfigs();
   }

   private void saveControllerConfigs() {
      HttpClient httpClient = new DefaultHttpClient();
      String url = "http://localhost:8080/beehive/rest/controllerconfig/save/3";
      HttpPost httpPost = new HttpPost(url);
//      String[] includes = {"user","username","token","pendingRoleName"};
      String[] excludes = {"*.class","id"};
//      httpPost.setHeader("Content-Type", "application/json"); 
//      httpPost.addHeader("Accept", "application/json");
      
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      
      String json = JsonGenerator.serializerObjectExcludeWithRoot(allDefaultConfigs, excludes, "controllerConfigs");
//      String json = new JSONSerializer().exclude(excludes).serialize("controllerConfigs", allDefaultConfigs);
//      json = "{\"controllerConfigs\":" + json + "}";
      System.out.println(json);
      try {
      StringEntity entity  = new StringEntity(json,"UTF-8");
      entity.setContentType("application/json");
         httpPost.setEntity(entity);
      } catch (UnsupportedEncodingException e1) {
         e1.printStackTrace();
      }
      try {
         HttpResponse response = httpClient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() == 200) {
            System.out.println("save success.");
         } else {
            System.out.println("save failed");
         }
      } catch (ClientProtocolException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
