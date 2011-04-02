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
package org.openremote.controller.gateway;

/**
 * 
 * @author Rich Turner 2011-02-11
 *
 */
public enum EnumGatewayConnectionType {
      MANAGED,
      PERMANENT,
      TIMED,
      CONNECTIONLESS;
   
   @Override
   public String toString() {
      return super.toString().toLowerCase();
   }
   
   public static EnumGatewayConnectionType enumValueOf(String connectionTypeValueOfGateway) {
      EnumGatewayConnectionType result = null;
      try {
         result = Enum.valueOf(EnumGatewayConnectionType.class, connectionTypeValueOfGateway.toUpperCase());
      } catch (Exception e) {}
      return result;
   }
}