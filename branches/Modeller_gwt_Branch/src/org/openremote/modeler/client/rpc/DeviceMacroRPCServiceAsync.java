/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeviceMacroServiceAsync.
 */
public interface DeviceMacroRPCServiceAsync {


    /**
     * Load all.
     *
     * @return the list< device macro>
     */
    void loadAll(AsyncCallback<List<DeviceMacro>> async);

    /**
     * Save device macro.
     *
     * @param deviceMacro the device macro
     * @return the device macro
     */
    void saveDeviceMacro(DeviceMacro deviceMacro, AsyncCallback<DeviceMacro> async);

    /**
     * Update device macro.
     *
     * @param deviceMacro the device macro
     * @return the device macro
     */
    void updateDeviceMacro(DeviceMacro deviceMacro, AsyncCallback<DeviceMacro> async);

    /**
     * Delete device macro.
     *
     * @param id the id
     */
    void deleteDeviceMacro(long id, AsyncCallback<Void> async);

    void loadDeviceMacroItems(DeviceMacro deviceMacro, AsyncCallback<List<DeviceMacroItem>> async);

}
