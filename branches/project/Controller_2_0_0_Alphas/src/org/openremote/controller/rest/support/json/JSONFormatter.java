/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class has been created to format the JSON object that is returned from the org.json XML -> JSON Serializer.
 * The sole purpose of this class is to ensure all objects within the JSON response that can be JSON arrays are
 * always JSON arrays. The JSON Serializer only creates JSON arrays if there is more than one object so the JSON
 * format varies depending on the structure of the panel.xml so this class ensures that the JSON output is consistent
 * independent of the panel.xml definition.
 * This was a requirement for the GWT Autobean code used within the Web Console 2.0 which is used to convert RPC data
 * to and from POJOs. Seems like Autobeans are very fussy about the structure of the data being supplied.    
 * 
 * @author      Rich Turner <richard@openremote.org>
 */
public class JSONFormatter {
   
   public static enum RequestType {
      GET_PANEL_LIST,
      GET_PANEL_LAYOUT,
      SEND_COMMAND,
      GET_SENSOR_STATUS,
      DO_SENSOR_POLLING,
      GET_ROUND_ROBIN_LIST,
      IS_ALIVE,
      IS_SECURE;
   }
   
   // Prevent instantiation
   private JSONFormatter() {}

   /**
    * This method is a crude way of forcing all values that could be arrays
    * to actually be arrays even if they have one element, this is to cater
    * for the Autobean POJO creation on the web console
    * 
    * @param jsonObj
    * @return
    */
   public static JSONObject format(HttpServletRequest request, JSONObject jsonObj) {
      String url = request.getRequestURL().toString().trim();
      String regexp = ".*/rest/([^/]*)/*.*";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      boolean matchFound = matcher.matches();
      RequestType requestType = null;
      
      if (matchFound) {
         String requestString = matcher.group(1);
         if (requestString != null && !requestString.equals("")) {
            if (requestString.equals("panel")) {
               requestType = RequestType.GET_PANEL_LAYOUT;
            } else if (requestString.equals("panels")) {
               requestType = RequestType.GET_PANEL_LIST;
            }
         }
      }
      
      if (requestType != null) {
         switch (requestType) {
            case GET_PANEL_LIST:
               updateValue(jsonObj, "panel", convertToArray(jsonObj.opt("panel")));
               break;
            case GET_PANEL_LAYOUT:
               // Convert tabbar item array
               JSONObject tabbarObj = jsonObj.optJSONObject("tabbar");
               if (tabbarObj != null) {
                  updateValue(tabbarObj, "item", convertToArray(tabbarObj.opt("item")));
               }
               
               // Convert Screen array
               JSONObject screensObj = jsonObj.optJSONObject("screens");
               updateValue(screensObj, "screen", convertToArray(screensObj.opt("screen")));
               JSONArray screenArr = screensObj.optJSONArray("screen");
               if (screenArr != null) {
                  for (int i=0; i<screenArr.length(); i++) {
                     JSONObject screenObj = screenArr.optJSONObject(i);
                     if (screenObj == null) {
                        continue;
                     }
                     
                     // Convert absolute array
                     updateValue(screenObj, "absolute", convertToArray(screenObj.opt("absolute")));
                     
                     // Convert any 0 string values to integer values
                     JSONArray absoluteComps = screenObj.optJSONArray("absolute");
                     if (absoluteComps != null) {
                        for (int j=0; j<absoluteComps.length(); j++) {
                           JSONObject comp = absoluteComps.optJSONObject(j);
                           if (comp != null) {
                              updateValue(comp, "top", comp.optInt("top"));
                              updateValue(comp, "left", comp.optInt("left"));
                              updateValue(comp, "width", comp.optInt("width"));
                              updateValue(comp, "height", comp.optInt("height"));
                              
                              // If component is a slider check min max values also
                              JSONObject slider = comp.optJSONObject("slider");
                              if (slider != null) {
                                 JSONObject min = slider.optJSONObject("min");
                                 JSONObject max = slider.optJSONObject("max");
                                 updateValue(min, "value", min.optInt("value"));
                                 updateValue(max, "value", max.optInt("value"));
                              }
                           }
                        }                  
                     }
                     
                     // Convert grid array and the child cell arrays
                     updateValue(screenObj, "grid", convertToArray(screenObj.opt("grid")));
                     JSONArray gridArr = screenObj.optJSONArray("grid");
                     if (gridArr != null) {
                        for (int j=0; j<gridArr.length(); j++) {
                           JSONObject gridObj = gridArr.optJSONObject(j);
                           if (gridObj != null) {
                              updateValue(gridObj, "top", gridObj.optInt("top"));
                              updateValue(gridObj, "left", gridObj.optInt("left"));
                              updateValue(gridObj, "width", gridObj.optInt("width"));
                              updateValue(gridObj, "height", gridObj.optInt("height"));
                              updateValue(gridObj, "rows", gridObj.optInt("rows"));
                              updateValue(gridObj, "cols", gridObj.optInt("cols"));
                              
                              updateValue(gridObj, "cell", convertToArray(gridObj.opt("cell")));
                              
                              // Convert any 0 string values to integer values
                              JSONArray cells = gridObj.optJSONArray("cell");
                              if (cells != null) {
                                 for (int k=0; k<cells.length(); k++) {
                                    JSONObject cell = cells.optJSONObject(k);
                                    if (cell != null) {
                                       updateValue(cell, "x", cell.optInt("x"));
                                       updateValue(cell, "y", cell.optInt("y"));
                                       updateValue(cell, "rowspan", cell.optInt("rowspan"));
                                       updateValue(cell, "colspan", cell.optInt("colspan"));
                                       
                                       // If component is a slider check min max values also
                                       JSONObject slider = cell.optJSONObject("slider");
                                       if (slider != null) {
                                          JSONObject min = slider.optJSONObject("min");
                                          JSONObject max = slider.optJSONObject("max");
                                          updateValue(min, "value", min.optInt("value"));
                                          updateValue(max, "value", max.optInt("value"));
                                       }
                                    }
                                 }                  
                              }
                           }
                        }                  
                     }
                     
                     // Update the gesture array
                     updateValue(screenObj, "gesture", convertToArray(screenObj.opt("gesture")));
                  }
               }
               updateValue(jsonObj, "screens", screensObj);

               // Convert Group array
               JSONObject groupsObj = jsonObj.optJSONObject("groups");
               updateValue(groupsObj, "group", convertToArray(groupsObj.opt("group")));
               JSONArray groupArr = groupsObj.optJSONArray("group");
               if (groupArr != null) {
                  for (int i=0; i<groupArr.length(); i++) {
                     JSONObject groupObj = groupArr.optJSONObject(i);
                     
                     // Convert include array
                     updateValue(groupObj, "include", convertToArray(groupObj.opt("include")));
                     
                     // Convert tabbar item array
                     JSONObject groupTabbarObj = groupObj.optJSONObject("tabbar");
                     if (groupTabbarObj != null) {
                        updateValue(groupTabbarObj, "item", convertToArray(groupTabbarObj.opt("item")));
                     }
                  }
               }
               updateValue(jsonObj, "groups", groupsObj);
               break;
         }
      }
      
      return jsonObj;
   }
   
   private static JSONArray convertToArray(Object jsonObj) {
      JSONArray arrayObj = new JSONArray();
      if (jsonObj == null) {
         return null;
      }
      if (!(jsonObj instanceof JSONArray)) {
         arrayObj.put(jsonObj);
      } else {
         return (JSONArray)jsonObj;
      }      
      return arrayObj;
   }
   
   private static void updateValue(JSONObject obj, String key, Object arrayObj) {
      if (obj != null) {
         try {
            obj.put(key, arrayObj);
         } catch(Exception e) {}
      }
   }
}
