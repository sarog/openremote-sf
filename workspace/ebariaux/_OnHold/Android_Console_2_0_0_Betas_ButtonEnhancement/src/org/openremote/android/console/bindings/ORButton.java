/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The button can send command to controller and navigate to, has default image and pressed image.
 * If it has set repeat, it would repeat send command.
 * Use "ORButton" as its name is because android has "Button" component.
 */
@SuppressWarnings("serial")
public class ORButton extends Control {

   private String name;
   private boolean hasPressCommand;
   private boolean hasShortReleaseCommand;
   private boolean hasLongPressCommand;
   private boolean hasLongReleaseCommand;
   private boolean repeat;
   private int repeatDelay = 100;
   private int longPressDelay = 250;
   
   /** The button's normal image. */
   private Image defaultImage;
   
   /** The button's pressed image. */
   private Image pressedImage;
   
   /** Navigate to. */
   private Navigate navigate;
   
   /**
    * Instantiates a new button by parse the button node.
    * 
    * @param node the node
    */
   public ORButton(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.setComponentId(Integer.valueOf(nodeMap.getNamedItem(ID).getNodeValue()));
      this.name = nodeMap.getNamedItem(NAME).getNodeValue();

      if (nodeMap.getNamedItem("repeatDelay") != null) {
        this.repeatDelay = Integer.valueOf(nodeMap.getNamedItem("repeatDelay").getNodeValue());
      }
      if (this.repeatDelay < 100) {
        this.repeatDelay = 100;
      }
      
      if (nodeMap.getNamedItem("hasPressCommand") != null) {
         this.hasPressCommand = Boolean.valueOf(nodeMap.getNamedItem("hasPressCommand").getNodeValue());
      }
      if (nodeMap.getNamedItem("hasShortReleaseCommand") != null) {
        this.hasShortReleaseCommand = Boolean.valueOf(nodeMap.getNamedItem("hasShortReleaseCommand").getNodeValue());
      }
      if (nodeMap.getNamedItem("hasLongPressCommand") != null) {
        this.hasLongPressCommand = Boolean.valueOf(nodeMap.getNamedItem("hasLongPressCommand").getNodeValue());
      }
      if (nodeMap.getNamedItem("hasLongReleaseCommand") != null) {
        this.hasLongReleaseCommand = Boolean.valueOf(nodeMap.getNamedItem("hasLongReleaseCommand").getNodeValue());
      }
      if (nodeMap.getNamedItem("repeat") != null) {
         this.repeat = Boolean.valueOf(nodeMap.getNamedItem("repeat").getNodeValue());
      }
      if (nodeMap.getNamedItem("longPressDelay") != null) {
        this.longPressDelay = Integer.valueOf(nodeMap.getNamedItem("longPressDelay").getNodeValue());
      }
      if (this.longPressDelay < 250) {
        this.longPressDelay = 250;
      }
      if (this.hasLongPressCommand || this.hasLongReleaseCommand) {
        this.repeat = false;
      }
      
      NodeList childNodes = node.getChildNodes();
      int nodeNum = childNodes.getLength();
      for (int i = 0; i < nodeNum; i++) {
         if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
            Node elementNode = childNodes.item(i);
            if (DEFAULT.equals(elementNode.getNodeName())) {
               this.defaultImage = createImage(elementNode);
            } else if (PRESSED.equals(elementNode.getNodeName())) {
               this.pressedImage = createImage(elementNode);
            } else if (NAVIGATE.equals(elementNode.getNodeName())) {
               this.navigate = new Navigate(elementNode);
            }
         }
      }
   }
   
   public String getName() {
      return name;
   }
   
   public boolean isHasPressCommand() {
      return hasPressCommand;
   }
   
   public boolean isHasShortReleaseCommand() {
     return hasShortReleaseCommand;
   }

   public boolean isHasLongPressCommand() {
     return hasLongPressCommand;
   }

   public boolean isHasLongReleaseCommand() {
     return hasLongReleaseCommand;
   }

   public int getLongPressDelay() {
     return longPressDelay;
   }

   public boolean isRepeat() {
      return repeat;
   }

   public int getRepeatDelay() {
     return repeatDelay;
   }

   public Image getDefaultImage() {
      return defaultImage;
   }

   public Image getPressedImage() {
      return pressedImage;
   }

   public Navigate getNavigate() {
      return navigate;
   }
   
   /**
    * Creates the image by parse image node.
    * 
    * @param elementNode the element node
    * 
    * @return the image
    */
   private Image createImage(Node elementNode) {
      for (Node imageNode = elementNode.getFirstChild(); imageNode != null; imageNode = imageNode.getNextSibling()) {
         if (imageNode.getNodeType() == Node.ELEMENT_NODE && "image".equals(imageNode.getNodeName())) {
            return new Image(imageNode);
         }
      }
      return null;
   }
}
