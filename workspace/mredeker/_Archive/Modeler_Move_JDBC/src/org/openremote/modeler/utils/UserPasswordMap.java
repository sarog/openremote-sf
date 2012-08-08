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
package org.openremote.modeler.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The UserPasswordMap is for storing username and plain password.
 */
public class UserPasswordMap {

   private static Map<String,String> userPassword = new HashMap<String,String>();
   
   public static void put(String username, String password) {
      userPassword.put(username, password);
   }
   
   public static String get(String username) {
      return userPassword.get(username);
   }
}
