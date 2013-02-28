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
package org.openremote.controller.rest.support.xml;

import org.openremote.controller.Constants;


/**
 * It is responsible for composing RESTful service error code.
 * 
 * @author handy.wang 2009-7-2
 */
public class RESTfulErrorCodeComposer {
   
   public static String composeXMLFormatStatusCode(int errorCode, String errorMessage) {
      StringBuffer sb = new StringBuffer();
      sb.append(Constants.STATUS_XML_HEADER);
      sb.append("<error>");
      sb.append("<code>");
      sb.append(errorCode);
      sb.append("</code>");
      
      sb.append("<message>");
      sb.append(errorMessage);
      sb.append("</message>");
      sb.append("</error>");
      sb.append(Constants.STATUS_XML_TAIL);
      return sb.toString();
   }
   
}
