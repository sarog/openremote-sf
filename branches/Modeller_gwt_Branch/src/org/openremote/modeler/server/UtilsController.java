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
import org.openremote.modeler.domain.Screen;
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
   
   private static final String UI_DESIGNER_LAYOUT_PANEL_KEY = "panelList";

   /** The Constant for store group list in session. */
   private static final String UI_DESIGNER_LAYOUT_GROUP_KEY = "groupList";
   
   /** The Constant for store screen list in session. */
   private static final String UI_DESIGNER_LAYOUT_SCREEN_KEY = "screenList";
   
   private static final String UI_DESIGNER_LAYOUT_MAXID = "maxID";
  
   /**
    * {@inheritDoc}
    */
   public String exportFiles(long maxId, List<Panel> panelList) {  
      return resourceService.downloadZipResource(maxId, this.getThreadLocalRequest().getSession().getId(), panelList);
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
   @SuppressWarnings("unchecked")
   public AutoSaveResponse autoSaveUiDesignerLayout(List<Panel> panels, long maxID) {
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();
      
      List<Panel> oldPanels = new ArrayList<Panel>();
      /*
       * init the groups & screens information for the panels.  
       */
      /*Set<Group> groups = new LinkedHashSet<Group>();
      Set<Screen> screens = new LinkedHashSet<Screen>();
      for(Panel panel :panels){
         List<GroupRef> groupRefs = panel.getGroupRefs();
         for(GroupRef groupRef : groupRefs){
            groups.add(groupRef.getGroup());
         }
      }
      
      for(Group group:groups){
         List<ScreenRef> screenRefs = group.getScreenRefs();
         for(ScreenRef screenRef : screenRefs){
            screens.add(screenRef.getScreen());
         }
      }*/
      if(getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY) != null){
         oldPanels = (List<Panel>)getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY);
      }
      if (panels.size() > 0) {
         if (!resourceService.getPanelsJson(panels).equals(resourceService.getPanelsJson(oldPanels))) {
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
            autoSaveResponse.setUpdated(true);
         }
      }
      return autoSaveResponse;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Panel> loadPanelsFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY);
      return (obj == null) ? new ArrayList<Panel>() : (List<Panel>)obj;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public List<Group> loadGroupsFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_GROUP_KEY);
      return (obj == null) ? new ArrayList<Group>() : (List<Group>)obj;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Screen> loadScreensFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_SCREEN_KEY);
      return (obj == null) ? new ArrayList<Screen>() : (List<Screen>)obj;
   }

   @Override
   public Long loadMaxID() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_MAXID);
      return (obj == null) ? 0 : (Long)obj;
   }


}
