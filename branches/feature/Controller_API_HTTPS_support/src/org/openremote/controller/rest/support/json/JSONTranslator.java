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
package org.openremote.controller.rest.support.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.openremote.controller.Constants;

/**
 * 
 * @author handy 2010-06-28
 * 
 * This is responsible for translating xml data to json data.
 *
 */
public class JSONTranslator {

   // translate xml data to json object with HTTPServletRequest.
   public static String toDesiredData(HttpServletRequest request, HttpServletResponse response, String xml) {
      String acceptTypeInHeader = request.getHeader(Constants.HTTP_ACCEPT_HEADER_NAME);
      return doTransalteXMLToJSONString(acceptTypeInHeader, response, xml);
   }
   
   public static String doTransalteXMLToJSONString(String acceptTypeInHeader, HttpServletResponse response, String xml) {
      if (Constants.HTTP_HEADER_ACCEPT_JSON_TYPE.equalsIgnoreCase(acceptTypeInHeader)) {
         return translate(response, xml);
      } else {
         return xml;
      }
   }
   
   public static String toDesiredData(HttpServletRequest request, HttpServletResponse response, int errorCode, String xml) {
      String acceptTypeInHeader = request.getHeader(Constants.HTTP_ACCEPT_HEADER_NAME);
      return doTransalteXMLToJSONString(acceptTypeInHeader, response, errorCode, xml);
   }
   
   private static String doTransalteXMLToJSONString(String acceptTypeInHeader, HttpServletResponse response, int errorCode, String xml) {
      if (Constants.HTTP_HEADER_ACCEPT_JSON_TYPE.equalsIgnoreCase(acceptTypeInHeader)) {
         return translate(response, xml);
      } else {
         response.setStatus(errorCode);
         return xml;
      }
   }
   

   private static String translate(HttpServletResponse response, String xml) {
      if (response != null) {
         response.setStatus(Constants.RESPONSE_SUCCESS);
      }
      xml = xml.replaceAll("xsi:schemaLocation=\".*\"", " ");
      XMLSerializer xmlSerializer = new XMLSerializer(); 
      xmlSerializer.setTypeHintsEnabled(false);
      xmlSerializer.setTypeHintsCompatibility(false);
      xmlSerializer.setSkipNamespaces(true);
      JSON json = xmlSerializer.read(xml);
      return json.toString(3);
   }
   
}
