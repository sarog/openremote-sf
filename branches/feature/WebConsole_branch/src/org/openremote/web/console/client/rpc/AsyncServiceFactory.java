/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

package org.openremote.web.console.client.rpc;

import com.google.gwt.core.client.GWT;

/**
 * Factory of AsyncService. Lazy load all the Async RPC Service.
 * 
 */
public class AsyncServiceFactory {
   
   /**
    * Not be instantiated.
    */
   private AsyncServiceFactory() {
   }

   private static UserCacheRPCServiceAsync userCacheServiceAsync = null;
   
   private static IPAutoDiscoveryRPCServiceAsync ipAutoDiscoveryServiceAsync = null;
   
   private static PanelIdentityRPCServiceAsync panelIdentityServiceAsync = null;
   
   private static CommandRPCServiceAsync commandServiceAsync = null;
   
   public static UserCacheRPCServiceAsync getUserCacheServiceAsync() {
      if (userCacheServiceAsync == null) {
         userCacheServiceAsync = GWT.create(UserCacheRPCService.class);
      }
      return userCacheServiceAsync;
   }
   
   public static IPAutoDiscoveryRPCServiceAsync getIPAutoDiscoveryServiceAsync() {
      if (ipAutoDiscoveryServiceAsync == null) {
         ipAutoDiscoveryServiceAsync = GWT.create(IPAutoDiscoveryRPCService.class);
      }
      return ipAutoDiscoveryServiceAsync;
   }
   
   public static PanelIdentityRPCServiceAsync getPanelIdentityServiceAsync() {
      if (panelIdentityServiceAsync == null) {
         panelIdentityServiceAsync = GWT.create(PanelIdentityRPCService.class);
      }
      return panelIdentityServiceAsync;
   }
   
   public static CommandRPCServiceAsync getCommandServiceAsync() {
      if (commandServiceAsync == null) {
         commandServiceAsync = GWT.create(CommandRPCService.class);
      }
      return commandServiceAsync;
   }
   
}
