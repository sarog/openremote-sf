package org.openremote.controller;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.utils.SpringTestContext;

public class ConfigTestFactory {

   private static RemoteActionXMLParser remoteActionXMLParser = (RemoteActionXMLParser) SpringTestContext.getInstance().getBean("remoteActionXMLParser");

   private static Configuration getConfig() {
      return (Configuration) SpringTestContext.getInstance().getBean("configuration");
   }

   private static RoundRobinConfig getRoundRobinConfig() {
      return (RoundRobinConfig) SpringTestContext.getInstance().getBean("roundRobinConfig");
   }

   public static Configuration getCustomBasicConfigFromDefaultControllerXML() {

      Map<String, String> attrMap = parseCustomConfigAttrMap();
      Configuration config = getConfig();
      config.setCustomAttrMap(attrMap);
      return config;
   }

   public static Configuration getCustomBasicConfigFromControllerXML(Document doc) {

      Map<String, String> attrMap = parseCustomConfigAttrMap(doc);
      Configuration config = getConfig();
      config.setCustomAttrMap(attrMap);
      return config;
   }

   public static RoundRobinConfig getCustomRoundRobinConfigFromDefaultControllerXML() {

      Map<String, String> attrMap = parseCustomConfigAttrMap();
      RoundRobinConfig config = getRoundRobinConfig();
      config.setCustomAttrMap(attrMap);
      return config;
   }

   public static RoundRobinConfig getCustomRoundRobinConfigFromControllerXML(Document doc) {

      Map<String, String> attrMap = parseCustomConfigAttrMap(doc);
      RoundRobinConfig config = getRoundRobinConfig();
      config.setCustomAttrMap(attrMap);
      return config;
   }

   private static Map<String, String> parseCustomConfigAttrMap() {
      Element element = null;
      try {
         element = remoteActionXMLParser.queryElementFromXMLByName("config");
      } catch (Exception e) {
         return null;
      }
      return pullAllCustomConfigs(element);
   }

   private static Map<String, String> pullAllCustomConfigs(Element element) {
      Map<String, String> attrMap = new HashMap<String, String>();
      for (Object o : element.getChildren()) {
         Element e = (Element) o;
         String name = e.getAttributeValue("name");
         String value = e.getAttributeValue("value");
         attrMap.put(name, value);
      }
      return attrMap;
   }

   private static Map<String, String> parseCustomConfigAttrMap(Document doc) {
      Element element = remoteActionXMLParser.queryElementFromXMLByName(doc, "config");
      return pullAllCustomConfigs(element);
   }

}
