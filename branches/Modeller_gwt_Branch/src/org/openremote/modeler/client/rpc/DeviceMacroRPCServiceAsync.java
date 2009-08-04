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

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

import java.util.List;


/**
 * The Interface DeviceMacroServiceAsync.
 */
public interface DeviceMacroRPCServiceAsync {


    /**
     * Load all.
     * 
     * @param async the async
     * 
     * @return the list< device macro>
     */
    void loadAll(AsyncCallback<List<DeviceMacro>> async);

    /**
     * Save device macro.
     * 
     * @param deviceMacro the device macro
     * @param async the async
     * 
     * @return the device macro
     */
    void saveDeviceMacro(DeviceMacro deviceMacro, AsyncCallback<DeviceMacro> async);

    /**
     * Update device macro.
     * 
     * @param deviceMacro the device macro
     * @param async the async
     * 
     * @return the device macro
     */
    void updateDeviceMacro(DeviceMacro deviceMacro, AsyncCallback<DeviceMacro> async);

    /**
     * Delete device macro.
     * 
     * @param id the id
     * @param async the async
     */
    void deleteDeviceMacro(long id, AsyncCallback<Void> async);

    /**
     * Load device macro items.
     * 
     * @param deviceMacro the device macro
     * @param async the async
     */
    void loadDeviceMacroItems(DeviceMacro deviceMacro, AsyncCallback<List<DeviceMacroItem>> async);

}
