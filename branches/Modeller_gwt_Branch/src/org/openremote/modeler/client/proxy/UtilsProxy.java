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
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;


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
   public static void exportFiles(long maxId, List<Panel> panelList, final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().exportFiles(maxId, panelList, new AsyncSuccessCallback<String>() {
         @Override
         public void onSuccess(String exportURL) {
            callback.onSuccess(exportURL);
         }
      });
   
   }

   /**
    * Auto save ui designer layout json.
    * 
    * @param groups the activities
    * @param callback the callback
    */
   public static void autoSaveUiDesignerLayout(List<Panel> panels, long maxID, final AsyncSuccessCallback<AutoSaveResponse> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().autoSaveUiDesignerLayout(panels,  maxID, new AsyncSuccessCallback<AutoSaveResponse>() {
         @Override
         public void onSuccess(AutoSaveResponse result) {
            callback.onSuccess(result);
         }

         @Override
         public void onFailure(Throwable caught) {
            callback.onFailure(caught);
         }
         
      });
   }
   
   /**
    * Gets the beehive rest icon url.
    * 
    * @param callback the callback
    * 
    */
   public static void getBeehiveRestIconUrl(final AsyncSuccessCallback<String> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().beehiveRestIconUrl(new AsyncSuccessCallback<String>() {
         @Override
         public void onSuccess(String result) {
            callback.onSuccess(result);
         }
         
      });
   }
   
   public static void loadPanelsFromSession(final AsyncSuccessCallback<List<Panel>> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadPanelsFromSession(new AsyncSuccessCallback<List<Panel>>() {
         @Override
         public void onSuccess(List<Panel> panels) {
            callback.onSuccess(panels);
         }
      });
   }
   
   public static void loadGroupsFromSession(final AsyncSuccessCallback<List<Group>> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadGroupsFromSession(new AsyncSuccessCallback<List<Group>>() {
         @Override
         public void onSuccess(List<Group> groups) {
            callback.onSuccess(groups);
         }
      });
   }
   
   public static void loadScreensFromSession(final AsyncSuccessCallback<List<Screen>> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadScreensFromSession(new AsyncSuccessCallback<List<Screen>>() {
         @Override
         public void onSuccess(List<Screen> Screens) {
            callback.onSuccess(Screens);
         }
      });
   }
   
   /**
    * Load layout component's max id from session.
    * 
    */
   public static void loadMaxID(final AsyncSuccessCallback<Long> callback) {
      AsyncServiceFactory.getUtilsRPCServiceAsync().loadMaxID(new AsyncSuccessCallback<Long>() {
         @Override
         public void onSuccess(Long maxID) {
            callback.onSuccess(maxID);
         }
      });
   }
}
