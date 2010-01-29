package org.openremote.android.console.bindings;

import java.util.ArrayList;
import java.util.List;

import org.openremote.android.console.model.XMLEntityDataBase;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


@SuppressWarnings("serial")
public class Group extends BusinessEntity{

   private int groupId;
   private String name;
   private List<XScreen> screens;
   private TabBar tabBar;
   
   @Override
   public String getElementName() {
      return "group";
   }

   @Override
   public void initWithXML(Node node) {
      screens = new ArrayList<XScreen>();
      
      NamedNodeMap nodeMap = node.getAttributes();
      this.groupId = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
      this.name = nodeMap.getNamedItem("name").getNodeValue();
      NodeList nodeList = node.getChildNodes();
      int childNum = nodeList.getLength();
      for (int i = 0; i < childNum; i++) {
         Node childNode = nodeList.item(i);
         if(childNode.getNodeType() == Node.ELEMENT_NODE) {
            if ("tabbar".equals(childNode.getNodeName())) {
               this.tabBar = new TabBar();
               this.tabBar.initWithXML(childNode);
            } else if ("include".equals(childNode.getNodeName())) {
               screens.add(XMLEntityDataBase.screens.get(Integer.valueOf(childNode.getAttributes().getNamedItem("ref")
                     .getNodeValue())));
            }
         }
      }
   }

   public int getGroupId() {
      return groupId;
   }

   public String getName() {
      return name;
   }

   public List<XScreen> getScreens() {
      return screens;
   }

   public TabBar getTabBar() {
      return tabBar;
   }
   
}
