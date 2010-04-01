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
package org.openremote.modeler.client.widget.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.WidgetDeleteEvent;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.TabbarPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.domain.component.UITabbar.Scope;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * 
 * @author javen
 *
 */
public class ScreenTabbar extends ScreenComponent {
   public static final int PADDING = 5;
   
   private List<ScreenTabbarItem> screenTabbarItems = new ArrayList<ScreenTabbarItem>();
   
   private FlexTable tabItemContainer = new FlexTable();
   
   private UITabbar uiTabbar = null;

   public ScreenTabbar(ScreenCanvas screenCanvas) {
      super(screenCanvas);
   }

   public ScreenTabbar(ScreenCanvas screenCanvas,UITabbar uiTabbar){
      super(screenCanvas);
      this.uiTabbar = uiTabbar;
      setToGroup();
      Screen screen = this.getScreenCanvas().getScreen();
      
      setHeight(44-2*PADDING);
      setWidth(screen.getTouchPanelDefinition().getCanvas().getWidth()-2*PADDING);
      addStyleName("tabbar-background");
      setPosition(0, screen.getTouchPanelDefinition().getCanvas().getHeight() - (44-2*PADDING));
      tabItemContainer.setSize(screen.getTouchPanelDefinition().getCanvas().getWidth()+"", 44+"");
      add(tabItemContainer);
      setStyleAttribute("position", "absolute");
      setStyleAttribute("leftPadding", PADDING+"px");
      setStyleAttribute("rightPadding", PADDING+"px");
      initTabbar();
      addDeleteListener();
   }
   @Override
   public String getName() {
      return "Tabbar";
   }

   @Override
   public void setName(String name) {
      // TODO Auto-generated method stub
      
   }
   
   @Override
   public PropertyForm getPropertiesForm() {
      return new TabbarPropertyForm(this);
   }
   
   @Override
   public void onComponentEvent(ComponentEvent ce) {
      if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
         WidgetSelectionUtil.setSelectWidget(this);
      }
      ce.cancelBubble();
      super.onComponentEvent(ce);
   }
   @Override
   protected void afterRender() {
      super.el().updateZIndex(1);
      super.afterRender();
   }
   public List<ScreenTabbarItem> getScreenTabbarItems() {
      return screenTabbarItems;
   }

   public void setScreenTabbarItems(List<ScreenTabbarItem> screenTabbarItems) {
      this.screenTabbarItems = screenTabbarItems;
   }

   public void setToGroup() {
      uiTabbar.setScope(Scope.GROUP);
      
      //1, remove tabbar from panel:
      Panel panel = getScreenCanvas().getScreen().getScreenPair().getParentGroup().getParentPanel();
      if (panel.getTabbar()==this.uiTabbar){
         panel.setTabbar(null);
      }
      
      //2, add tabbar to group
      Group group = getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      group.setTabbar(this.uiTabbar);
   }
   
   public void setToPanel() {
      uiTabbar.setScope(Scope.PANEL);
      //1, remove tabbar from group
      Group group = getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      if (group.getTabbar() == this.uiTabbar){
         group.setTabbar(null);
      }
      //2, add tabbar from panel:
      Panel panel = getScreenCanvas().getScreen().getScreenPair().getParentGroup().getParentPanel();
      panel.setTabbar(this.uiTabbar);
   }
   
   public boolean isPanelScope() {
      return uiTabbar.getScope() == Scope.PANEL;
   }
   
   public boolean isGroupScope() {
      return uiTabbar.getScope() == Scope.GROUP;
   }
   
   public int getTabbarItemCount() {
      return uiTabbar.getTabbarItems().size();
   }
   
   public void addTabbarItem(final UITabbarItem uiTabbarItem) {
      this.uiTabbar.addTabbarItem(uiTabbarItem);
      
      final ScreenTabbarItem screenTabbarItem = new ScreenTabbarItem(this.getScreenCanvas(),uiTabbarItem);
      
      /*screenTabbarItem.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            uiTabbar.removeTabarItem(uiTabbarItem);
            screenTabbarItem.removeFromParent();
            getScreenCanvas().layout();
         }
         
      });*/
      addDeleteListenerToTabItem(screenTabbarItem);
      
      this.getScreenTabbarItems().add(screenTabbarItem);
      makeTabItemDragable(screenTabbarItem);
      this.updateTabbar();
      WidgetSelectionUtil.setSelectWidget(screenTabbarItem);
   }
   
   private void updateTabbar() {
      int tabbarNumber = getTabbarItemCount();
      if (tabbarNumber > 0) {
         int index = 0;
         int width = (getScreenCanvas().getScreen().getTouchPanelDefinition().getCanvas().getWidth()-2*PADDING)/tabbarNumber;
         this.removeAll();
         this.setLayout(new AbsoluteLayout());
         for(ScreenTabbarItem item : getScreenTabbarItems()) {
            item.setWidth(width);
            item.setHeight(44-2*PADDING);
            item.setPosition(index*width+PADDING, 0);
//            makeTabItemDragable(item);
            add(item);
            index++;
         }
      }
   }
   
   private void initTabbar() {
      int tabbarNumber = getTabbarItemCount();
      int index = 0;
      if (tabbarNumber >0 ) {
         int width = (getScreenCanvas().getScreen().getTouchPanelDefinition().getCanvas().getWidth()-2*PADDING)/tabbarNumber;
         this.screenTabbarItems.removeAll(screenTabbarItems);
         for (UITabbarItem uiTabbarItem : this.uiTabbar.getTabbarItems()) {
            ScreenTabbarItem screenTabbarItem = new ScreenTabbarItem(this.getScreenCanvas(),uiTabbarItem);
            this.getScreenTabbarItems().add(screenTabbarItem);
            this.addDeleteListenerToTabItem(screenTabbarItem);
            makeTabItemDragable(screenTabbarItem);
         }
         this.removeAll();
         this.setLayout(new AbsoluteLayout());
         for(ScreenTabbarItem item : getScreenTabbarItems()) {
            item.setWidth(width);
            item.setHeight(44-2*PADDING);
            item.setPosition(index*width+PADDING, 0);
            add(item);
            index++;
         }
      }
      this.getScreenCanvas().layout();
   }
   
   private void makeTabItemDragable(final ScreenTabbarItem tabItem) {
      
      Draggable draggable = new Draggable(tabItem);
      draggable.setConstrainVertical(true);
      draggable.setContainer(this);
      draggable.setUseProxy(false);
      draggable.addDragListener(new DragListener() {

         @Override
         public void dragEnd(DragEvent de) {
            super.dragEnd(de);
            int index = getOrder(de.getClientX() - getScreenCanvas().getAbsoluteLeft());
            ScreenTabbarItem screenTabarItem = (ScreenTabbarItem) de.getComponent();
            screenTabarItem.removeFromParent();
            ScreenTabbar.this.uiTabbar.removeTabarItem(screenTabarItem.getUITabbarIem());
            ScreenTabbar.this.uiTabbar.insertTabbarItem(index, screenTabarItem.getUITabbarIem());
            ScreenTabbar.this.initTabbar();
         }

         @Override
         public void dragStart(DragEvent de) {
            super.dragStart(de);
            tabItem.hide();
         }
         
         
      });
   }
   
   private void addDeleteListener() {
      addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            removeItself();
            getScreenCanvas().layout();
         }
         
      });
   }
   
   private void removeItself () {
      Group group = this.getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      if (group.getTabbar() != null && group.getTabbar().equals(uiTabbar)) {
         group.setTabbar(null);
      } 
      Panel panel = group.getParentPanel();
      if (panel.getTabbar() != null && panel.getTabbar().equals(uiTabbar)) {
         panel.setTabbar(null);
      }
      for (ScreenTabbarItem item : this.getScreenTabbarItems()) {
         item.removeFromParent();
      }
      uiTabbar.removeAll();
      this.removeFromParent();
   }
   
   private int getOrder(int xPosition) {
      int result = 0;
      int tabitemCount = getTabbarItemCount();
      if (tabitemCount > 0) {
         int width = (getScreenCanvas().getScreen().getTouchPanelDefinition().getWidth()-(2*PADDING)) / tabitemCount;
         /*float temp =((float) xPosition) / width;
         int integer = (int)temp;
         float decimal = temp - integer;
         if (decimal >= 0.5) {
            result = integer +1;
         } else {
            result = integer;
         }
         result = integer;*/
         result = xPosition/ width;
      }
      return result;
   }
   
   private void addDeleteListenerToTabItem(final ScreenTabbarItem screenTabbarItem) {
      screenTabbarItem.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            uiTabbar.removeTabarItem(screenTabbarItem.getUITabbarIem());
            screenTabbarItem.removeFromParent();
            getScreenCanvas().layout();
         }
         
      });
   }
}
