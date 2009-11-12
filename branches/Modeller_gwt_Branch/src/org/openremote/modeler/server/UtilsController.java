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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.rpc.UtilsRPCService;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.UIScreen;
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
   
   /** The Constant for store group list in session. */
   private static final String UI_DESIGNER_LAYOUT_GROUP_KEY = "groupList";
   
   /** The Constant for store screen list in session. */
   private static final String UI_DESIGNER_LAYOUT_SCREEN_KEY = "screenList";
   
   private static final String UI_DESIGNER_LAYOUT_MAXID = "maxID";
  
   /**
    * {@inheritDoc}
    */
   public String exportFiles(long maxId, List<Panel> panelList, List<Group> groupList, List<UIScreen> screenList) {  
      return resourceService.downloadZipResource(maxId, this.getThreadLocalRequest().getSession().getId(), panelList, groupList, screenList);
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
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY);
      return (obj == null) ? "" : obj.toString();
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public AutoSaveResponse autoSaveUiDesignerLayout(List<Group> groups, List<UIScreen> screens, long maxID) {
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();
      List<Group> oldGroups = new ArrayList<Group>();
      if(getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY) != null){
         oldGroups = (List<Group>)getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY);
      }
      if (groups.size() > 0) {
         if (!resourceService.getGroupsJson(groups).equals(resourceService.getGroupsJson(oldGroups))) {
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY, groups);
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
            autoSaveResponse.setUpdated(true);
         }
      }
      
      List<UIScreen> oldScreens = new ArrayList<UIScreen>();
      if(getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_SCREEN_KEY) != null){
         oldScreens = (List<UIScreen>)getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_SCREEN_KEY);
      }
      if (screens.size() > 0) {
         if (!resourceService.getScreensJson(screens).equals(resourceService.getScreensJson(oldScreens))) {
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_SCREEN_KEY, screens);
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
            autoSaveResponse.setUpdated(true);
         }
      }
      return autoSaveResponse;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Group> loadGroupsFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY);
      return (obj == null) ? new ArrayList<Group>() : (List<Group>)obj;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<UIScreen> loadScreensFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_SCREEN_KEY);
      return (obj == null) ? new ArrayList<UIScreen>() : (List<UIScreen>)obj;
   }

   @Override
   public Long loadMaxID() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_MAXID);
      return (obj == null) ? 0 : (Long)obj;
   }

}
