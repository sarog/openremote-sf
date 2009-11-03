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
package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.UIScreen;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Interface UtilsRPCServiceAsync.
 * 
 * @author handy.wang
 */
public interface UtilsRPCServiceAsync {

   /**
    * Export.
    * 
    * @param callback the callback
    * @param maxId the max id
    * @param activityList the activity list
    */
   void exportFiles(long maxId, List<Group> groupList, List<UIScreen> screenList, AsyncCallback<String> callback);

   /**
    * Beehive rest icon url.
    * 
    * @param callback the callback
    */
   void beehiveRestIconUrl(AsyncCallback<String> callback);

   /**
    * Load json string from session.
    * 
    * @param asyncSuccessCallback the async success callback
    */
   void loadJsonStringFromSession(AsyncCallback<String> asyncSuccessCallback);

   /**
    * Auto save ui designer layout json.
    * 
    * @param groups the activities
    * @param asyncSuccessCallback the async success callback
    */
   void autoSaveUiDesignerLayout(List<Group> groups, List<UIScreen> screens, AsyncCallback<AutoSaveResponse> asyncSuccessCallback);

   void loadGroupsFromSession(AsyncCallback<List<Group>> callback);
   
   void loadScreensFromSession(AsyncCallback<List<UIScreen>> callback);
}
