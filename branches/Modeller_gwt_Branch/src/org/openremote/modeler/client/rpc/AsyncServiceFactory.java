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

import com.google.gwt.core.client.GWT;

/**
 * @author allen.wei
 */
public class AsyncServiceFactory {
   private static DeviceMacroRPCServiceAsync deviceMacroServiceAsync = null;
   private static DeviceRPCServiceAsync deviceServiceAsync = null;
   private static DeviceCommandRPCServiceAsync deviceCommandServiceAsync = null;
   
   public static DeviceMacroRPCServiceAsync getDeviceMacroServiceAsync() {
      if (deviceMacroServiceAsync == null) {
         deviceMacroServiceAsync = GWT.create(DeviceMacroRPCService.class);
      }
      return deviceMacroServiceAsync;
   }
   
   public static DeviceRPCServiceAsync getDeviceServiceAsync() {
      if (deviceServiceAsync == null) {
         deviceServiceAsync = GWT.create(DeviceRPCService.class);
      }
      return deviceServiceAsync;
   }
   
   public static DeviceCommandRPCServiceAsync getDeviceCommandServiceAsync() {
      if (deviceCommandServiceAsync == null) {
         deviceCommandServiceAsync = GWT.create(DeviceCommandRPCService.class);
      }
      return deviceCommandServiceAsync;
   }
}
