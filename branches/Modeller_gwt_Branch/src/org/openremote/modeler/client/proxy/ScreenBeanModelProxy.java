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
package org.openremote.modeler.client.proxy;

import java.util.Map;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.uidesigner.ScreenWindow;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * The Class ScreenBeanModelProxy.
 */
public class ScreenBeanModelProxy {

   /**
    * The class shouldn't be instantiated.
    */
   private ScreenBeanModelProxy() {
   }

   /**
    * Creates the screen.
    * 
    * @param activity the activity
    * @param map the map
    * 
    * @return the bean model
    */
   public static BeanModel createScreen(Activity activity, Map<String, String> map) {
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      setAttrsToScreen(map, screen);
      activity.addScreen(screen);
      screen.setActivity(activity);
      BeanModelDataBase.screenTable.insert(screen.getBeanModel());
      return screen.getBeanModel();
   }

   /**
    * Update screen.
    * 
    * @param screen the screen
    * @param map the map
    * 
    * @return the bean model
    */
   public static BeanModel updateScreen(Screen screen, Map<String, String> map) {
      setAttrsToScreen(map, screen);
      BeanModelDataBase.screenTable.update(screen.getBeanModel());
      return screen.getBeanModel();
   }
   
   /**
    * Delete screen.
    * 
    * @param screenBeanModel the screen bean model
    */
   public static void deleteScreen(BeanModel screenBeanModel) {
      Screen screen = screenBeanModel.getBean();
      Activity activity = screen.getActivity();
      activity.deleteScreen(screen);
      BeanModelDataBase.screenTable.delete(screenBeanModel);
   }

   /**
    * Sets the attrs to screen.
    * 
    * @param map the map
    * @param screen the screen
    */
   private static void setAttrsToScreen(Map<String, String> map, Screen screen) {
      screen.setName(map.get(ScreenWindow.SCREEN_NAME));
      Integer row = Integer.parseInt(map.get(ScreenWindow.SCREEN_ROW_COUNT));
      Integer column = Integer.parseInt(map.get(ScreenWindow.SCREEN_COLUMN_COUNT));
      if (!row.equals(screen.getRowCount()) || !column.equals(screen.getColumnCount())) {
         screen.getButtons().clear();
      }
      screen.setRowCount(row);
      screen.setColumnCount(column);
   }
}
