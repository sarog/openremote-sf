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
package org.openremote.web.console.client;

/**
 * Puts all the fronted constant here.
 */
public class Constants {

   /**
    * Not be instantiated.
    */
   private Constants() {
   }

   public static final String CONSOLE_USERINFO = "console_userinfo";
   public static final String CONSOLE_SETTINGS = "console_settings";
   public static final String GROUP_MEMBERS = "group_members";
   public static final String SSL_STATUS = "ssl_enabled";
   public static final String SSL_PORT = "ssl_port";
   public static final String SSL_DISABLED = "false";
   public static final String DEFAULT_SSL_PORT = "8443";
   
   public static final String ListenerPollingStatusIdFormat = "polling_status_";
   public static final String ListenerNavigateTo = "navigateToListener";
   public static final String ListenerToPopSetting = "listenerToPopSetting";
   
   public static final String REG_POSITIVEINT = "^[1-9][0-9]*$";
}
