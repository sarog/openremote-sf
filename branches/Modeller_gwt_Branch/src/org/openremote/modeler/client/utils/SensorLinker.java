package org.openremote.modeler.client.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Sensor;

@SuppressWarnings("serial")
public class SensorLinker extends BusinessEntity{
   private long sensorId;
   private Set<LinkerChild> linkerChildren = new HashSet<LinkerChild>(5);
   
   public SensorLinker(){};
   
   
   public SensorLinker(Sensor sensor){
      this.sensorId = sensor.getOid();
   }
   
   public void clear(){
      linkerChildren.removeAll(linkerChildren);
   }
   public String getXMLString(){
      StringBuilder sb = new StringBuilder();
      sb.append("<link type=\"sensor\" ref=\""+sensorId+"\">");
      for(LinkerChild child: linkerChildren){
         sb.append(child.toString());
      }
      sb.append("</link>");
      return sb.toString();
   }
   public String getAttributeValue(String childName,String attributeName){
      String result = null;
      for(LinkerChild child : linkerChildren){
         if(child.childName.equals(childName)){
            result =  child.attributes.get(attributeName);
         }
      }
      return result;
   }
   
   public void setAttribute(String childName,String attributeName,String attributeValue){
      for(LinkerChild child : linkerChildren){
         if(child.childName.equals(childName)){
            child.attributes.get(attributeName);
         }
      }
   }
   public void AddChildForSensorLinker(String childName,Map<String,String> attrMap){
      LinkerChild child = new LinkerChild(childName);
      child.setAttributes(attrMap);
      if(linkerChildren.contains(child)){
         linkerChildren.remove(child);
      }
      linkerChildren.add(child);
   }
   
   public long getSensorId() {
      return sensorId;
   }

   public void setSensorId(long sensorId) {
      this.sensorId = sensorId;
   }

   public static class LinkerChild extends BusinessEntity{
      String childName = "";
      Map<String,String> attributes = new HashMap<String,String>();
      
      public LinkerChild(){}
      public LinkerChild(String childName){
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
         if(!other.childName.equals(childName)){
            return false;
         }
         if(attributes.get("name")!=null && other.attributes.get("name")!=null){
            return attributes.get("name").equals(other.attributes.get("name"));
         }
         return false;
      }
      
      public void setAttribute(String attrName,String attrValue){
         if(attrName!=null&&! attrName.trim().isEmpty()){
            attributes.put(attrName, attrValue);
         }
      }
      public void setAttributes(Map<String,String> attrMap){
         attributes.putAll(attrMap);
      }
      public String toString(){
         StringBuilder sb = new StringBuilder();
         sb.append("<"+childName);
         Set<String> keys = attributes.keySet();
         for(String key: keys){
            sb.append(" "+key+"=\""+attributes.get(key)+"\"");
         }
         sb.append(">");
         sb.append("</"+childName+">");
         return sb.toString();
      }
   }
   
   
}
