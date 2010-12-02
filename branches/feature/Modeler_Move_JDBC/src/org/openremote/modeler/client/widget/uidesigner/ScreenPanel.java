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
import org.openremote.modeler.domain.ScreenPair;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ContainerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * The panel stores the screenTab. It can change the screenTab according to the select screenPair changed.
 */
public class ScreenPanel extends LayoutContainer {

   /** The change listener map. */
   private Map<ScreenTab, ChangeListener> changeListenerMap = null;
   
   /** The screen item. */
   private ScreenTab screenItem = null;
   
   /**
    * Instantiates a new screen panel.
    */
   public ScreenPanel() {
      setLayout(new FitLayout());
      setStyleAttribute("backgroundColor", "white");
      addListeners();
      addInsertListener();
   }

   /**
    * Adds the listeners.
    */
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
            final ScreenTab screenTab = (ScreenTab) be.getItem();
            BeanModelDataBase.screenTable.addChangeListener(screenTab.getScreenPair().getId(),
                  getScreenChangeListener(screenTab));
         }
      });
      addListener(Events.Remove, new Listener<ContainerEvent>() {                
         public void handleEvent(ContainerEvent be) {
            final ScreenTab screenTab = (ScreenTab) be.getItem();
            BeanModelDataBase.screenTable.removeChangeListener(screenTab.getScreenPair().getId(),
                  getScreenChangeListener(screenTab));
         }
      });
   }
   
   /**
    * Gets the screen change listener.
    * 
    * @param screenTab the screen tab
    * 
    * @return the screen change listener
    */
   private ChangeListener getScreenChangeListener(final ScreenTab screenTab) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<ScreenTab, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(screenTab);

      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent event) {
               if (event.getType() == BeanModelTable.REMOVE) {
                  remove(screenTab);
                  screenItem = null;
               } else if (event.getType() == BeanModelTable.UPDATE) {
                  screenTab.updateTouchPanel();
               }
            }
         };
         changeListenerMap.put(screenTab, changeListener);
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
               if (beanModel.getBean() instanceof ScreenPair) {
                  setScreenItem(new ScreenTab((ScreenPair) beanModel.getBean()));
               }
            }
         }

      });
   }
   
   /**
    * Gets the screen item.
    * 
    * @return the screen item
    */
   public ScreenTab getScreenItem() {
      return screenItem;
   }
   
   /**
    * Sets the screen item.
    * 
    * @param screenItem the new screen item
    */
   public void setScreenItem(ScreenTab screenItem) {
      if (this.screenItem != null) {
         remove(this.screenItem);
      }
      add(screenItem);
      this.screenItem = screenItem;
      layout();
   }
   
   /**
    * Close current screen tab.
    */
   public void closeCurrentScreenTab() {
      if (this.indexOf(screenItem) != -1) {
         remove(this.screenItem);
      }
//      this.removeAll();
      this.screenItem = null;
   }
}
