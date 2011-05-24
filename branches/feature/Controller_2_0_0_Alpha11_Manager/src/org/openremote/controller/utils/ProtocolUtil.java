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
package org.openremote.controller.utils;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Protocol Utility.
 * 
 * @author Rich Turner 2011-04-10
 */
public class ProtocolUtil {
   
   private ProtocolUtil() {
   }
   
   /**
    * Checks that string is a valid URL but doesn't
    * check that the server exists
    */
   public static Boolean isUrlValid(String url) {
      Boolean isValid = true;
      try {
         URL tempUrl = new URL(url);
      } catch (MalformedURLException e) {
         isValid = false;
      }
      return isValid;  
   }
   
   /**
    * Method to get the URL host from URL String
    * @return a string representing host name
    */
   public static String getUrlHost(String url) {
      String hostName = "";
      try {
         URL tempUrl = new URL(url);
         hostName = tempUrl.getHost();
      } catch (MalformedURLException e) {}
      
      return hostName;
   }
}
