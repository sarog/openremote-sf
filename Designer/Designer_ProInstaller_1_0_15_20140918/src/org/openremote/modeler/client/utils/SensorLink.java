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
package org.openremote.modeler.client.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import flexjson.JSON;
/**
 * This class is used for recording the property for a sensor 
 * A sensor is defined in the build modeler, include the state, state name... But we can only set the value for the state in the UI designer.
 * we can't change the property for a sensor because it only have a device command or state name. therefore we need a class to record the state value 
 * for every SensorOwner and here it is. 
 * Attention: If it already has a element which has a attribute called <b>name</b>,when you add another element which has the same name as well as a attribute called name
 * the element will be replaced by the other one. 
 * @author Javen
 *
 */
public class SensorLink extends BusinessEntity {
   
   private static final long serialVersionUID = 4070183628030926123L;
   
   private Sensor sensor = null;
   
   private SensorWithInfoDTO sensorDTO;
   
   private Set<LinkerChild> linkerChildren = new HashSet<LinkerChild>(5);
   
   public SensorLink() {
   };
   
   
   public SensorLink(Sensor sensor) {
      this.sensor = sensor;
   }
   /**
    * Most of the sensors have some states,So the method can be used get the state value by state name,like getStateValueByStateName("on") can get the value for a switch sensor when it is on the on state.
    * @param stateName The name for the state. 
    * @return The state value for the state.
    */
   public String getStateValueByStateName(String stateName) {
      String result = "";
      for (LinkerChild child : linkerChildren) {
         if (child.childName.equals("state") && child.getAttributeValue("name") != null
               && child.getAttributeValue("name").equals(stateName)) {
            result = child.attributes.get("value");
         }
      }
      return result;
   }
   /**
    * Remove all the sensor property record.
    * This method is useful when your change the sensor for a SensorOwner.
    */
   public void clear() {
      linkerChildren.removeAll(linkerChildren);
   }

   /**
    * Generate XML string related to sensor for inclusion in panel.xml file.
    * This is the <link> tag and any required child <state> element.
    * 
    * @return String link tag XML snippet to include in panel.xml
    */
   @JSON(include=false)
   public String getXMLString() {
     if (sensorDTO == null) {
       return "";
     }
     StringBuffer sb = new StringBuffer();
     sb.append("<link type=\"sensor\" ref=\"" + sensorDTO.getOffsetId() + "\">");
     if (sensorDTO.getStateNames() != null) {
       for (String stateName : sensorDTO.getStateNames()) {
         sb.append("<state name=\"" + stateName + "\" value=\"" + getStateValueByStateName(stateName) + "\"/>");
       }
     }
     sb.append("</link>");
     return sb.toString();
   }
   
   /**
    * add or update the sensor property whose name is <b>childName</b>
    * @param childName the child name for a SensorLinker node. 
    * @param attrMap a map contains the all attribute for the sensor linker's child. 
    */
   public void addOrUpdateChildForSensorLinker(String childName, Map<String, String> attrMap) {
      LinkerChild child = new LinkerChild(childName);
      child.setAttributes(attrMap);
      if (linkerChildren.contains(child)) {
         linkerChildren.remove(child);
      }
      linkerChildren.add(child);
   }
   
   public void removeChildForSensorLinker(String childName, Map<String, String> attrMap) {
      LinkerChild child = new LinkerChild(childName);
      child.setAttributes(attrMap);
      if (linkerChildren.contains(child)) {
         linkerChildren.remove(child);
      }
   }
   @JSON(include = false)
   public Sensor getSensor() {
      return sensor;
   }


   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
   }
   
   public SensorWithInfoDTO getSensorDTO() {
    return sensorDTO;
  }


  public void setSensorDTO(SensorWithInfoDTO sensorDTO) {
    this.sensorDTO = sensorDTO;
  }


  public Set<LinkerChild> getLinkerChildren() {
      return linkerChildren;
   }


   public void setLinkerChildren(Set<LinkerChild> linkerChildren) {
      this.linkerChildren = linkerChildren;
   }


   /**
    * A class for storing the property for a sensor linker child
    * @author Javen
    *
    */
   public static class LinkerChild extends BusinessEntity {
      
      private static final long serialVersionUID = 7295371507206111361L;
      
      String childName = "";
      Map<String,String> attributes = new HashMap<String,String>();
      
      public LinkerChild() {
      }
      public LinkerChild(String childName) {
         this.childName = childName;
      }
      @Override
      public int hashCode() {
        return childName.hashCode();
      }
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null) return false;
         if (getClass() != obj.getClass()) return false;
         LinkerChild other = (LinkerChild) obj;
         if (!other.childName.equals(childName)) {
            return false;
         }
         if (attributes.get("name") != null && other.attributes.get("name") != null) {
            return attributes.get("name").equals(other.attributes.get("name"));
         }
         return false;
      }
      
      public void setAttribute(String attrName, String attrValue) {
         if (attrName != null && !attrName.trim().isEmpty()) {
            attributes.put(attrName, attrValue);
         }
      }
      public void setAttributes(Map<String, String> attrMap) {
         attributes.putAll(attrMap);
      }
      
      public String getAttributeValue(String attributeName) {
         return attributes.get(attributeName);
      }
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("<" + childName);
         Set<String> keys = attributes.keySet();
         for (String key : keys) {
            sb.append(" " + key + "=\"" + attributes.get(key) + "\"");
         }
         sb.append(">");
         sb.append("</" + childName + ">");
         return sb.toString();
      }
      public String getChildName() {
         return childName;
      }
      public void setChildName(String childName) {
         this.childName = childName;
      }
      public Map<String, String> getAttributes() {
         return attributes;
      }
      
      
   }
   
   
}
