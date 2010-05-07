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
   private List<Screen> screens;
   private TabBar tabBar;
   
   public Group(Node node) {
      screens = new ArrayList<Screen>();
      
      NamedNodeMap nodeMap = node.getAttributes();
      this.groupId = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
      this.name = nodeMap.getNamedItem("name").getNodeValue();
      NodeList nodeList = node.getChildNodes();
      int childNum = nodeList.getLength();
      for (int i = 0; i < childNum; i++) {
         Node childNode = nodeList.item(i);
         if(childNode.getNodeType() == Node.ELEMENT_NODE) {
            if ("tabbar".equals(childNode.getNodeName())) {
               this.tabBar = new TabBar(childNode);
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

   public List<Screen> getScreens() {
      return screens;
   }

   public TabBar getTabBar() {
      return tabBar;
   }
   
}
