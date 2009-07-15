/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
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

import java.util.List;

import org.openremote.modeler.domain.DeviceMacro;

import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeviceMacroServiceAsync.
 */
public interface DeviceMacroServiceAsync {


   /**
    * Load all.
    * 
    * @param callback the callback
    */
   public void loadAll(AsyncCallback<List<DeviceMacro>> callback);


   /**
    * Save device macro.
    * 
    * @param deviceMacro the device macro
    * @param callback the callback
    */
   public void saveDeviceMacro(DeviceMacro deviceMacro, AsyncCallback<DeviceMacro> callback);
   

   /**
    * Update device macro.
    * 
    * @param deviceMacro the device macro
    * @param callback the callback
    */
   public void updateDeviceMacro(DeviceMacro deviceMacro,AsyncCallback<DeviceMacro> callback);
   
   

   /**
    * Delete device macro.
    * 
    * @param id the id
    * @param callback the callback
    */
   public void deleteDeviceMacro(long id,AsyncCallback<Void> callback);
}
