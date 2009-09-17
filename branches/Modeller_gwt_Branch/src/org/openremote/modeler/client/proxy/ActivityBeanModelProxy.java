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

import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.data.BeanModel;

/**
 * The Class ActivityBeanModelProxy.
 */
public class ActivityBeanModelProxy {
   

   /**
    * The class shouldn't be instantiated.
    */
   private ActivityBeanModelProxy() {
   }
   /**
    * Delete activity.
    * 
    * @param activityBeanModel the activity bean model
    */
   public static void deleteActivity(BeanModel activityBeanModel) {
      Activity activity = activityBeanModel.getBean();
      for (Screen screen : activity.getScreens()) {
         BeanModelDataBase.screenTable.delete(screen.getOid());
      }
   }
}
