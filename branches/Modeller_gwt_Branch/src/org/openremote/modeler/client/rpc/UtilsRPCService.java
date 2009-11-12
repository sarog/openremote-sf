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
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.UIScreen;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface DeviceMacroService.
 * 
 * @author handy.wang
 */
@RemoteServiceRelativePath("utils.smvc")
public interface UtilsRPCService extends RemoteService {


   /**
    * Export files.
    * 
    * @param maxId the max id
    * @param activityList the activity list
    * 
    * @return the string
    */
   String exportFiles(long maxId, List<Panel> panelList, List<Group> groupList, List<UIScreen> screenList);
   
   /**
    * Beehive rest icon url.
    * 
    * @return the string
    */
   String beehiveRestIconUrl();

   /**
    * Load json string from session.
    * 
    * @return the string
    */
   String loadJsonStringFromSession();

   /**
    * Auto save activity json.
    * 
    * @param activities the activities
    * 
    * @return the auto save response
    */
   AutoSaveResponse autoSaveUiDesignerLayout(List<Group> groups, List<UIScreen> screens, long maxID);
   
   List<Group> loadGroupsFromSession();
   
   List<UIScreen> loadScreensFromSession();
   
   /**
    * Load layout component's max id from session.
    * 
    */
   Long loadMaxID();
}
