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
package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class Gesture extends Control {

   private GestureSwipeType swipeType;
   private boolean hasControlCommand;
   private Navigate navigate;
   
   public Gesture(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.setComponentId(Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue()));
      String type = nodeMap.getNamedItem("type").getNodeValue();
      if ("swipe-top-to-bottom".equals(type)) {
         this.swipeType = GestureSwipeType.GestureSwipeTypeTopToBottom;
      } else if ("swipe-bottom-to-top".equals(type)) {
         this.swipeType = GestureSwipeType.GestureSwipeTypeBottomToTop;
      } else if ("swipe-left-to-right".equals(type)) {
         this.swipeType = GestureSwipeType.GestureSwipeTypeLeftToRight;
      } else if ("swipe-right-to-left".equals(type)) {
         this.swipeType = GestureSwipeType.GestureSwipeTypeRightToLeft;
      }
      
      if (nodeMap.getNamedItem("hasControlCommand") != null) {
         this.hasControlCommand = Boolean.valueOf(nodeMap.getNamedItem("hasControlCommand").getNodeValue());
      }
      
      NodeList childNodes = node.getChildNodes();
      int nodeNum = childNodes.getLength();
      for (int i = 0; i < nodeNum; i++) {
         if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
            Node elementNode = childNodes.item(i);
            if("navigate".equals(elementNode.getNodeName())) {
               this.navigate = new Navigate(elementNode);
            }
         }
      }
   }
   
   public static enum GestureSwipeType {
      GestureSwipeTypeTopToBottom, GestureSwipeTypeBottomToTop, GestureSwipeTypeLeftToRight, GestureSwipeTypeRightToLeft;
   }

   public GestureSwipeType getSwipeType() {
      return swipeType;
   }

   public boolean isHasControlCommand() {
      return hasControlCommand;
   }

   public Navigate getNavigate() {
      return navigate;
   }
   
   
}
