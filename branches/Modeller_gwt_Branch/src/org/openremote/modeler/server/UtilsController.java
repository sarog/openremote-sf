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
package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.rpc.UtilsRPCService;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.service.ResourceService;

/**
 * The server side implementation of the RPC service <code>DeviceRPCService</code>.
 * 
 * @author handy.wang
 */
@SuppressWarnings("serial")
public class UtilsController extends BaseGWTSpringController implements UtilsRPCService {
   
   /** The resource service. */
   private ResourceService resourceService;
   
   /** The configuration. */
   private Configuration configuration;
   
   /** The Constant ACTIVIY_JSON_SESSION_ATTR_KEY. */
   private static final String UI_DESIGNER_LAYOUT_JSON_KEY = "activityJSON";
   

  
   /**
    * {@inheritDoc}
    */
   public String exportFiles(long maxId, List<Activity> activities) {  
      return resourceService.downloadZipResource(maxId, this.getThreadLocalRequest().getSession().getId(), activities);
   }
   
   /**
    * Gets the resource service.
    * 
    * @return the resource service
    */
   public ResourceService getResourceService() {
      return resourceService;
   }

   /**
    * Sets the resource service.
    * 
    * @param resourceService the new resource service
    */
   public void setResourceService(ResourceService resourceService) {
      this.resourceService = resourceService;
   }

   /**
    * Gets the configuration.
    * 
    * @return the configuration
    */
   public Configuration getConfiguration() {
      return configuration;
   }

   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    */
   public String beehiveRestIconUrl() {
      return configuration.getBeehiveRestIconUrl();
   }

   /**
    * {@inheritDoc}
    */
   public String loadJsonStringFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_JSON_KEY);
      return (obj == null) ? "" : obj.toString();
   }

   /**
    * {@inheritDoc}
    */
   public AutoSaveResponse autoSaveUiDesignerLayoutJSON(List<Activity> activities) {
      String jsonString = resourceService.getActivitiesJson(activities);
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();
      if (!"".equals(jsonString)) {
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_JSON_KEY, jsonString);
         autoSaveResponse.setSavedSuccess(true);
      }
      return autoSaveResponse;
   }
}
