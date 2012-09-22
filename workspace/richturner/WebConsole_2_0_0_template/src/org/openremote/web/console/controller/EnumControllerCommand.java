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
package org.openremote.web.console.controller;

public enum EnumControllerCommand {
	GET_PANEL_LIST,
	GET_PANEL_LAYOUT,
	SEND_COMMAND,
	GET_SENSOR_STATUS,
	DO_SENSOR_POLLING,
	GET_ROUND_ROBIN_LIST,
	IS_ALIVE,
	IS_SECURE;
	
   @Override
   public String toString() {
      return super.toString().toLowerCase();
   }
   
   public static EnumControllerCommand enumValueOf(String commandActionTypeValue) {
   	EnumControllerCommand result = null;
      try {
         result = Enum.valueOf(EnumControllerCommand.class, commandActionTypeValue.toUpperCase());
      } catch (Exception e) {}
      return result;
   }
}
