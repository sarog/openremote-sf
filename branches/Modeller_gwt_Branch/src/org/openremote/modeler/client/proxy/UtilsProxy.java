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

import java.util.List;

import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Activity;


/**
 * The Class DeviceBeanModelProxy.
 * 
 * @author handy.wang
 */
public class UtilsProxy {
   
   /**
    * Not be instantiated.
    */
   private UtilsProxy() {
   }

   /**
    * Load device.
    * 
    * @param callback the callback
    * @param maxId the max id
    * @param activityList the activity list
    */
   public static void exportFiles(long maxId, List<Activity> activityList, final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().exportFiles(maxId, activityList, new AsyncSuccessCallback<String>() {
         @Override
         public void onSuccess(String exportURL) {
            callback.onSuccess(exportURL);
         }
      });
   
   }

   /**
    * Load json string from session.
    * 
    * @param asyncSuccessCallback the async success callback
    */
   public static void loadJsonStringFromSession(final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadJsonStringFromSession(new AsyncSuccessCallback<String>() {
         @Override
         public void onSuccess(String activityJSON) {
            callback.onSuccess(activityJSON);
         }
      });
   }

   /**
    * Auto save activity json.
    * 
    * @param asyncSuccessCallback the async success callback
    */
   public static void autoSaveUiDesignerLayoutJSON(List<Activity> activities, final AsyncSuccessCallback<AutoSaveResponse> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().autoSaveUiDesignerLayoutJSON(activities, new AsyncSuccessCallback<AutoSaveResponse>() {
         @Override
         public void onSuccess(AutoSaveResponse result) {
            callback.onSuccess(result);
         }
      });
   }
}
