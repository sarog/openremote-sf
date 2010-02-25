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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class XScreen extends BusinessEntity {

   private int screenId;
   private String name;
   private ArrayList<LayoutContainer> layouts;
   private Background background;
   
   public XScreen(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.screenId = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
      this.name = nodeMap.getNamedItem("name").getNodeValue();
      this.layouts = new ArrayList<LayoutContainer>();
      NodeList childNodes = node.getChildNodes();
      int nodeNum = childNodes.getLength();
      for (int i = 0; i < nodeNum; i++) {
         if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = childNodes.item(i).getNodeName();
            if ("absolute".equals(nodeName)) {
               layouts.add(new AbsoluteLayoutContainer(childNodes.item(i)));
            } else if ("grid".equals(nodeName)) {
               layouts.add(new GridLayoutContainer(childNodes.item(i)));
            } else if ("background".equals(nodeName)) {
               this.background = new Background(childNodes.item(i));
            }
         }
      }
   }

   public int getScreenId() {
      return screenId;
   }

   public String getName() {
      return name;
   }

   public ArrayList<LayoutContainer> getLayouts() {
      return layouts;
   }
   
   public Background getBackground() {
      return background;
   }
   
   public String getBackgroundSrc() {
      if (background != null) {
         return background.getBackgroundImage().getSrc();
      }
      return null;
   }
}
