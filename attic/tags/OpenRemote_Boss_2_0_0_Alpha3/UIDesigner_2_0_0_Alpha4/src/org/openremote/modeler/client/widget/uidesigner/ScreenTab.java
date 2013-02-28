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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * The Class ScreenTab.
 */
public class ScreenTab extends TabPanel {
   
   /** The change listener map. */
   private Map<ScreenTabItem, ChangeListener> changeListenerMap = null;
   /**
    * Instantiates a new screen tab.
    */
   public ScreenTab() {
      setTabScroll(true);
      setAnimScroll(true);
      addListener(Events.BeforeAdd, new Listener<TabPanelEvent>() {
         @Override
         public void handleEvent(TabPanelEvent be) {
            WidgetSelectionUtil.setSelectWidget(null);
         }
      });
      addListener(Events.BeforeRemove, new Listener<TabPanelEvent>() {
         @Override
         public void handleEvent(TabPanelEvent be) {
            WidgetSelectionUtil.setSelectWidget(null);
         }
      });
      addListener(Events.Select, new Listener<TabPanelEvent>() {
         @Override
         public void handleEvent(TabPanelEvent be) {
            WidgetSelectionUtil.setSelectWidget(null);
         }
      });
      addListener(Events.Add, new Listener<TabPanelEvent>() {
         public void handleEvent(TabPanelEvent be) {
            final ScreenTabItem screenTabItem = (ScreenTabItem) be.getItem();
            BeanModelDataBase.screenTable.addChangeListener(screenTabItem.getScreen().getOid(),
                  getScreenChangeListener(screenTabItem));
         }
      });
      addListener(Events.Remove, new Listener<TabPanelEvent>() {                
         public void handleEvent(TabPanelEvent be) {
            final ScreenTabItem screenTabItem = (ScreenTabItem) be.getItem();
            BeanModelDataBase.screenTable.removeChangeListener(screenTabItem.getScreen().getOid(),
                  getScreenChangeListener(screenTabItem));
         }
      });
      addInsertListener();
   }
   
   /**
    * Gets the screen change listener.
    * 
    * @param screenTabItem the screen tab item
    * 
    * @return the screen change listener
    */
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
               } else if (event.getType() == BeanModelTable.UPDATE) {
//                  if (screenTabItem.getRow() != screen.getRowCount()
//                        || screenTabItem.getColumn() != screen.getColumnCount()) {
//                     remove(screenTabItem);
//                     ScreenTabItem newScreenTabItem = new ScreenTabItem(screen);
//                     add(newScreenTabItem);
//                     setSelection(newScreenTabItem);
//                     return;
//                  }
                  if (!screen.getName().equals(screenTabItem.getText())) {
                     screenTabItem.setText(screen.getName());
                  }
                	  
                  ScreenCanvas screenCanvas = screenTabItem.getScreenCanvas();
                  if (screen.isHasTabbar()) {
                	  screenCanvas.addTabbar();
                  }
                  screenTabItem.updateTouchPanel();
                  screenCanvas.setSize(screen.getTouchPanelDefinition().getCanvas().getWidth(), screen.getTouchPanelDefinition().getCanvas().getHeight());
                  screenCanvas.setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
                  setSelection(screenTabItem);
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
                  ScreenTabItem screenTabItem = new ScreenTabItem((Screen) beanModel.getBean());
                  add(screenTabItem);
                  setSelection(screenTabItem);
               }
            }
         }

      });
   }
}
