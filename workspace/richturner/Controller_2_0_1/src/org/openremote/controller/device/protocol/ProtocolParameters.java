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
package org.openremote.controller.device.protocol;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Protocol Parameters 
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class ProtocolParameters {
   
   private HashMap<String, LinkedHashSet<String>> paramMap;
   
   public ProtocolParameters()
   {
      this(new HashMap<String, LinkedHashSet<String>>());
   }
   
   public ProtocolParameters(HashMap<String, LinkedHashSet<String>> parameterMap)
   {
      this.paramMap = parameterMap;
   }
   
   /**
    * Adds a value to the specified parameter
    * 
    * @param parameterName
    * @param parameterValue
    */
   public void setParameterValue(String parameterName, String parameterValue)
   {
      if (!paramMap.containsKey(parameterName)) paramMap.put(parameterName, new LinkedHashSet<String>());
      
      paramMap.get(parameterName).add(parameterValue);
   }
   
   /**
    * Adds a set of values to the specified parameter
    * 
    * @param parameterName
    * @param parameterValues
    */
   public void setParameterValues(String parameterName, LinkedHashSet<String> parameterValues)
   {
      parameterName = parameterName.toLowerCase();
      if (!paramMap.containsKey(parameterName)) paramMap.put(parameterName, new LinkedHashSet<String>());
      
      LinkedHashSet<String> values = paramMap.get(parameterName);
      
      for(String value : parameterValues)
      {
         values.add(value);
      }
   }
   
   /**
    * Returns the last value added for the specified parameter
    * 
    * @param parameterName
    * @return String value of parameter
    */
   public String getParameterValue(String parameterName)
   {
      String result = null;
      parameterName = parameterName.toLowerCase();
      LinkedHashSet<String> values = paramMap.get(parameterName);
      
      if (values != null)
      {
         result = ((String[])values.toArray())[values.size()-1];
      }
      
      return result;
   }
   
   /**
    * Returns all values for the specified parameter
    * 
    * @param parameterName
    * @return LinkedHashSet<String> values of parameter
    */
   public LinkedHashSet<String> getParameterValues(String parameterName)
   {
      parameterName = parameterName.toLowerCase();
      return paramMap.get(parameterName);
   }
   
   private HashMap<String, LinkedHashSet<String>> getMap()
   {
      return paramMap;
   }
   
   public static ProtocolParameters mergeParameters(ProtocolParameters baseParameters, ProtocolParameters overrideParameters)
   {
      if (baseParameters == null) return baseParameters;
      if (overrideParameters == null) return baseParameters;
      
      ProtocolParameters mergedParams = new ProtocolParameters((HashMap<String, LinkedHashSet<String>>) baseParameters.getMap().clone());
      HashMap<String, LinkedHashSet<String>> overrideParams = overrideParameters.getMap();
      
      for(Map.Entry<String, LinkedHashSet<String>> mapEntry : overrideParams.entrySet())
      {
         mergedParams.setParameterValues(mapEntry.getKey(), mapEntry.getValue());
      }
      
      return mergedParams; 
   }
}
