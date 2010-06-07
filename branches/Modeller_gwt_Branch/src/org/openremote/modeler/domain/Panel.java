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
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;
import org.openremote.modeler.touchpanel.TouchPanelTabbarDefinition;

import flexjson.JSON;

/**
 * The Panel define the different device touch panel, such as iPhone panel, wall panel etc.
 */
public class Panel extends BusinessEntity {

   private static final long serialVersionUID = 6122936524433692761L;
   
   private static int defaultNameIndex = 1;
   private String name;
   private List<GroupRef> groupRefs = new ArrayList<GroupRef>();
   private List<UITabbarItem> tabbarItems = new ArrayList<UITabbarItem>();
   private TouchPanelDefinition touchPanelDefinition;
   
   private UITabbar tabbar = null; 
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public List<GroupRef> getGroupRefs() {
      return groupRefs;
   }
   public void setGroupRefs(List<GroupRef> groupRefs) {
      this.groupRefs = groupRefs;
   }
   public void addGroupRef(GroupRef groupRef) {
      groupRefs.add(groupRef);
   }
   public void removeGroupRef(GroupRef groupRef) {
      groupRefs.remove(groupRef);
   }
   public TouchPanelDefinition getTouchPanelDefinition() {
      return touchPanelDefinition;
   }
   public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition) {
      this.touchPanelDefinition = touchPanelDefinition;
   }
   public void insertGroupRef(GroupRef before, GroupRef target) {
      int index = groupRefs.indexOf(before);
      groupRefs.add(index, target);
   }
   public List<UITabbarItem> getTabbarItems() {
      return tabbarItems;
   }
   public void setTabbarItems(List<UITabbarItem> tabbarItems) {
      this.tabbarItems = tabbarItems;
   }
   /* (non-Javadoc)
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Transient
   public String getDisplayName() {
      TouchPanelCanvasDefinition canvas = touchPanelDefinition.getCanvas();
      return name + "(" + touchPanelDefinition.getName() + "," + canvas.getWidth() + "X" + canvas.getHeight() + ")";
   }
   
   @Transient
   public static String getNewDefaultName() {
      return "panel" + defaultNameIndex;
   }
   
   public void clearGroupRefs() {
      groupRefs.clear();
   }
   
   @Transient
   public static void increaseDefaultNameIndex() {
      defaultNameIndex++;
   }
   
   public String getType() {
      if (touchPanelDefinition != null) {
         return touchPanelDefinition.getType();
      }
      return Constants.CUSTOM_PANEL;
   }
   public UITabbar getTabbar() {
      return tabbar;
   }
   public void setTabbar(UITabbar tabbar) {
      this.tabbar = tabbar;
   }
   
   @Transient
   @JSON(include = false)
   public List<Group> getGroups() {
      List<Group> groups = new ArrayList<Group>();
      for (GroupRef groupRef : groupRefs) {
         groups.add(groupRef.getGroup());
      }
      return groups;
   }
   
   public Collection<ImageSource> getAllImageSources() {
	   Collection<ImageSource> imageSources = new HashSet<ImageSource>();
	   List<GroupRef> groupRefs = getGroupRefs();
       if (groupRefs != null && groupRefs.size() > 0) {
          for (GroupRef groupRef : groupRefs) {
             Group group = groupRef.getGroup();
             imageSources.addAll(group.getAllImageSources());
          }
       }
       imageSources.addAll(getImageSourcesFromTabbar());
       imageSources.addAll(getTouchPanelDefImageSources());
	   return imageSources;
   }
   
   private Collection<ImageSource> getImageSourcesFromTabbar() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(5);
      if (tabbar != null) {
         imageSources.addAll(tabbar.getAllImageSources());
      }
      return imageSources;
   }
   
   private Collection<ImageSource> getTouchPanelDefImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(3);
      if (Constants.CUSTOM_PANEL.equals(this.getType())) {
         ImageSource vBgImage = new ImageSource(getTouchPanelDefinition().getBgImage());
         ImageSource hBgImage = new ImageSource(getTouchPanelDefinition().getHorizontalDefinition().getBgImage());
         ImageSource tabbarImage = getTouchPanelDefinition().getTabbarDefinition().getBackground();
         if (!vBgImage.isEmpty()) {
            imageSources.add(vBgImage);
         }
         if (!hBgImage.isEmpty()) {
            imageSources.add(hBgImage);
         }
         if (tabbarImage != null && !tabbarImage.isEmpty() && !TouchPanelTabbarDefinition.IPHONE_TABBAR_BACKGROUND.equals(tabbarImage.getSrc())) {
            imageSources.add(tabbarImage);
         }
      }
      return imageSources;
   }

}
