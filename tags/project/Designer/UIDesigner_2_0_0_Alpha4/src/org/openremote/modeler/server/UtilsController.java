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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.rpc.UtilsRPCService;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.exception.FileOperationException;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.TemplateService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.ImageRotateUtil;
import org.openremote.modeler.utils.XmlParser;

/**
 * The server side implementation of the RPC service <code>DeviceRPCService</code>.
 * 
 * @author handy.wang
 */
@SuppressWarnings("serial")
public class UtilsController extends BaseGWTSpringController implements UtilsRPCService {
   
   private static final String ROTATED_FLAG = "ROTATE";
   
   private static final Logger LOGGER = Logger.getLogger(UtilsController.class);
   /** The resource service. */
   private ResourceService resourceService;
   private TemplateService screenTemplateService;
   private UserService userService;
   
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
   
   
   public void setScreenTemplateService(TemplateService screenTemplateService) {
      this.screenTemplateService = screenTemplateService;
   }

   /**
    * {@inheritDoc}
    */
   public String beehiveRestIconUrl() {
      return configuration.getBeehiveRestIconUrl();
   }

   
   public UserService getUserService() {
      return userService;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public AutoSaveResponse autoSaveUiDesignerLayout(Collection<Panel> panels, long maxID) {
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();
      
      List<Panel> oldPanels = new ArrayList<Panel>();
      if (getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY) != null) {
         oldPanels = (List<Panel>) getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY);
      }
      if (panels != null) {
         if (!resourceService.getPanelsJson(panels).equals(resourceService.getPanelsJson(oldPanels))) {
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
            getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
            autoSaveResponse.setUpdated(true);
            resourceService.initResources(panels, maxID);
            LOGGER.info("Auto save UI designerLayout sucessfully");
         }
      }
      return autoSaveResponse;
   }

   @Override
   public AutoSaveResponse saveUiDesignerLayout(Collection<Panel> panels, long maxID) {
      AutoSaveResponse autoSaveResponse = new AutoSaveResponse();

      if (panels != null) {
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxID);
         autoSaveResponse.setUpdated(true);
         resourceService.initResources(panels, maxID);
         LOGGER.info("manual save UI DesingerLayout successfully");
      }
      autoSaveResponse.setUpdated(true);
      return autoSaveResponse;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Collection<Panel> loadPanelsFromSession() {
      Object obj = getThreadLocalRequest().getSession().getAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY);
      if(obj == null){
         PanelsAndMaxOid panelsAndMaxOid = restore();
         obj = panelsAndMaxOid !=null ? panelsAndMaxOid.getPanels(): null; 
      }
      return (obj == null) ? new ArrayList<Panel>() : (Collection<Panel>)obj;
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

   @Override
   public String downLoadImage(String url) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      File userFolder = new File(pathConfig.userFolder(userService.getAccount()));
      if (!userFolder.exists()) {
         if (! userFolder.mkdirs()) {
            throw new FileOperationException("Can't create user folder for user: "+userService.getAccount().getUser().getUsername());
         }
      }
      File imageFile = new File(userFolder, url.substring(url.lastIndexOf("/") + 1));
      try {
         XmlParser.downloadFile(url, imageFile);
      } catch (IOException e) {
         LOGGER.error("Download image " + url + " occur IOException.", e);
      }
      return resourceService.getRelativeResourcePathByCurrentAccount(imageFile.getName());
   }

   @Override
   public PanelsAndMaxOid restore() {
      PanelsAndMaxOid result = resourceService.restore();
      if(result!=null){
         Collection<Panel> panels = result.getPanels();
         long maxOid = result.getMaxOid();
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_PANEL_KEY, panels);
         getThreadLocalRequest().getSession().setAttribute(UI_DESIGNER_LAYOUT_MAXID, maxOid);
      }
      return result;
   }

   @Override
   public boolean canRestore() {
      return resourceService.canRestore();
   }
   
   public ScreenFromTemplate buildScreenFromTemplate(Template template){
      return screenTemplateService.buildFromTemplate(template);
   }

   @Override
   public UISlider rotateImage(UISlider uiSlider) {
      PathConfig pathConfig = PathConfig.getInstance(configuration);
      String userFolderPath = pathConfig.userFolder(userService.getAccount());
      
      File userFolder = new File(userFolderPath);
      
      File minImageFile = new File(uiSlider.getMinImage().getSrc());
      File minTrackImageFile = new File(uiSlider.getMinTrackImage().getSrc());
      File thumbImageFile = new File(uiSlider.getThumbImage().getSrc());
      File maxTrackImageFile = new File(uiSlider.getMaxTrackImage().getSrc());
      File maxImageFile = new File(uiSlider.getMaxImage().getSrc());
      
      File minImageFileInUserFolder = new File(userFolderPath + minImageFile.getName());
      File minTrackImageFileInUserFolder = new File(userFolderPath + minTrackImageFile.getName());
      File thumbImageFileInUserFolder = new File(userFolderPath + thumbImageFile.getName());
      File maxTrackImageFileInUserFolder = new File(userFolderPath + maxTrackImageFile.getName());
      File maxImageFileInUserFolder = new File(userFolderPath + maxImageFile.getName());
      double degree = uiSlider.isVertical()?90:-90;
      for (File f : userFolder.listFiles()) {
         if (f.equals(minImageFileInUserFolder)) {
            uiSlider.setMinImage(new ImageSource(minImageFile.getParent() + File.separator +getImageNameAfterRotate(minImageFileInUserFolder,degree)));
         } else if (f.equals(minTrackImageFileInUserFolder)) {
            uiSlider.setMinTrackImage(new ImageSource(minTrackImageFile.getParent() + File.separator +getImageNameAfterRotate(minTrackImageFileInUserFolder,degree)));
         } else if (f.equals(thumbImageFileInUserFolder)) {
            uiSlider.setThumbImage(new ImageSource(thumbImageFile.getParent() + File.separator +getImageNameAfterRotate(thumbImageFileInUserFolder,degree)));
         } else if (f.equals(maxTrackImageFileInUserFolder)) {
            uiSlider.setMaxTrackImage(new ImageSource(maxTrackImageFile.getParent() + File.separator +getImageNameAfterRotate(maxTrackImageFileInUserFolder,degree)));
         } else if (f.equals(maxImageFileInUserFolder)) {
            uiSlider.setMaxImage(new ImageSource(maxImageFile.getParent() + File.separator +getImageNameAfterRotate(maxImageFileInUserFolder,degree)));
         }
      }
      return uiSlider;
   }
   
   private String getImageNameAfterRotate(File imageFileInUserFolder,double degree) {
      if (imageFileInUserFolder.getName().contains(ROTATED_FLAG)) {
         File beforeRotatedFile = new File(imageFileInUserFolder.getParent()+File.separator+(imageFileInUserFolder.getName().replace(ROTATED_FLAG, "")));
         if (beforeRotatedFile.exists()) {
            return beforeRotatedFile.getName();
         }
      }
      File fileAfterRotated = ImageRotateUtil.rotate(imageFileInUserFolder, imageFileInUserFolder.getParent() + File.separator + ROTATED_FLAG
            +imageFileInUserFolder.getName(), degree);
      return fileAfterRotated==null?imageFileInUserFolder.getName():fileAfterRotated.getName();
   }
}
