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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ContainerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class ScreenPanel extends LayoutContainer {

   private Map<ScreenTabItem, ChangeListener> changeListenerMap = null;
   private ScreenTabItem screenItem = null;
   public ScreenPanel() {
      setBorders(true);
      setStyleAttribute("backgroundColor", "white");
      addStyleName("zero-border-top");
      setScrollMode(Scroll.AUTO);
      addListeners();
      addInsertListener();
   }

   @SuppressWarnings("unchecked")
   private void addListeners() {
      addListener(Events.BeforeAdd, new Listener<ContainerEvent>() {
         @Override
         public void handleEvent(ContainerEvent be) {
            WidgetSelectionUtil.setSelectWidget(null);
         }
      });
      addListener(Events.BeforeRemove, new Listener<ContainerEvent>() {
         @Override
         public void handleEvent(ContainerEvent be) {
            WidgetSelectionUtil.setSelectWidget(null);
         }
      });
      addListener(Events.Add, new Listener<ContainerEvent>() {
         public void handleEvent(ContainerEvent be) {
            final ScreenTabItem screenTabItem = (ScreenTabItem) be.getItem();
            BeanModelDataBase.screenTable.addChangeListener(screenTabItem.getScreen().getOid(),
                  getScreenChangeListener(screenTabItem));
         }
      });
      addListener(Events.Remove, new Listener<ContainerEvent>() {                
         public void handleEvent(ContainerEvent be) {
            final ScreenTabItem screenTabItem = (ScreenTabItem) be.getItem();
            BeanModelDataBase.screenTable.removeChangeListener(screenTabItem.getScreen().getOid(),
                  getScreenChangeListener(screenTabItem));
         }
      });
   }
   
   private ChangeListener getScreenChangeListener(final ScreenTabItem screenTabItem) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<ScreenTabItem, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(screenTabItem);

      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent event) {
               Screen screen = screenTabItem.getScreen();
               if (event.getType() == BeanModelTable.REMOVE) {
                  remove(screenTabItem);
                  screenItem = null;
               } else if (event.getType() == BeanModelTable.UPDATE) {
                  ScreenCanvas screenCanvas = screenTabItem.getScreenCanvas();
                  if (screen.isHasTabbar()) {
                    screenCanvas.addTabbar();
                  } else {
                     screenCanvas.removeTabbar();
                  }
                  screenTabItem.updateTouchPanel();
                  screenCanvas.setSize(screen.getTouchPanelDefinition().getCanvas().getWidth(), screen.getTouchPanelDefinition().getCanvas().getHeight());
                  screenCanvas.setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
               }
            }
         };
         changeListenerMap.put(screenTabItem, changeListener);
      }
      return changeListener;
   }
   
   /**
    * Adds the insert listener.
    */
   private void addInsertListener() {
      BeanModelDataBase.screenTable.addInsertListener(Constants.SCREEN_TABLE_OID, new ChangeListener() {
         public void modelChanged(ChangeEvent event) {
            if (event.getType() == BeanModelTable.ADD) {
               BeanModel beanModel = (BeanModel) event.getItem();
               if (beanModel.getBean() instanceof Screen) {
                  setScreenItem(new ScreenTabItem((Screen) beanModel.getBean()));
               }
            }
         }

      });
   }
   
   public ScreenTabItem getScreenItem() {
      return screenItem;
   }
   
   public void setScreenItem(ScreenTabItem screenItem) {
      if (this.screenItem != null) {
         remove(this.screenItem);
      }
      add(screenItem);
      this.screenItem = screenItem;
      layout();
   }
}
