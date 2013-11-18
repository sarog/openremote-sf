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
package org.openremote.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Configuration accepts a map to prefer custom attribute values by name.
 * 
 * @author Dan Cong
 */
public class CustomConfiguration {
   
   /** custom attributes map, will use these values first, other than default values from config.properties */
   private Map<String, String> customAttrMap = new HashMap<String, String>();

   
   public Map<String, String> getCustomAttrMap() {
      return customAttrMap;
   }

   public void setCustomAttrMap(Map<String, String> customAttrMap) {
      if (customAttrMap == null) {
         return;
      }
      this.customAttrMap = customAttrMap;
   }

   protected String preferAttrCustomValue(String attrName, String defaultValue) {
      return customAttrMap.containsKey(attrName) ? customAttrMap.get(attrName) : defaultValue;
   }

   protected boolean preferAttrCustomValue(String attrName, boolean defaultValue) {
      return customAttrMap.containsKey(attrName) ? Boolean.valueOf(customAttrMap.get(attrName)) : defaultValue;
   }

   protected int preferAttrCustomValue(String attrName, int defaultValue) {
      return customAttrMap.containsKey(attrName) ? Integer.valueOf(customAttrMap.get(attrName)) : defaultValue;
   }

   protected long preferAttrCustomValue(String attrName, long defaultValue) {
      return customAttrMap.containsKey(attrName) ? Long.valueOf(customAttrMap.get(attrName)) : defaultValue;
   }
   
   protected String[] preferAttrCustomValue(String attrName, String[] defaultValue) {
      return customAttrMap.containsKey(attrName) ? customAttrMap.get(attrName).split(",") : defaultValue;
   }
}
