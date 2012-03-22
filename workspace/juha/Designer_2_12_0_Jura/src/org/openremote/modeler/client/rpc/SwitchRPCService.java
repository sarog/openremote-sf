/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

import org.openremote.modeler.domain.Switch;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface defines some methods to manage the switch entity.
 */
@RemoteServiceRelativePath("switch.smvc")
public interface SwitchRPCService extends RemoteService {
   
   /**
    * Load all switch from database.
    * 
    * @return the list< switch>
    */
   List<Switch> loadAll();
   
   /**
    * Save switch into database.
    * 
    * @param switchToggle the switch toggle
    * 
    * @return the switch
    */
   Switch save(Switch switchToggle);
   
   /**
    * Update switch with database data.
    * 
    * @param switchToggle the switch toggle
    * 
    * @return the switch
    */
   Switch update(Switch switchToggle);
   
   /**
    * Delete switch by id.
    * 
    * @param id the id
    */
   void delete(long id);
}
