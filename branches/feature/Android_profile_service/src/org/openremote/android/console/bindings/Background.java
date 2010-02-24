package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class Background extends BusinessEntity {

   private boolean fillScreen = false;
   private boolean isBackgroundImageAbsolutePosition = false;
   private int backgroundImageAbsolutePositionLeft;
   private int backgroundImageAbsolutePositionTop;
   private String backgroundImageRelativePosition;
   private Image backgroundImage;
   
   public Background(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      if (nodeMap.getNamedItem("fillScreen") != null) {
         this.fillScreen = Boolean.valueOf(nodeMap.getNamedItem("fillScreen").getNodeValue());
      }
      if (nodeMap.getNamedItem("absolute") != null) {
         this.isBackgroundImageAbsolutePosition = true;
         String[] absolute = nodeMap.getNamedItem("absolute").getNodeValue().split("\\,");
         this.backgroundImageAbsolutePositionLeft = Integer.valueOf(absolute[0]);
         this.backgroundImageAbsolutePositionTop = Integer.valueOf(absolute[1]);
      } else if (nodeMap.getNamedItem("relative") != null) {
         this.backgroundImageRelativePosition = nodeMap.getNamedItem("relative").getNodeValue().toLowerCase();
      }
      
      NodeList childNodes = node.getChildNodes();
      int nodeNum = childNodes.getLength();
      for (int i = 0; i < nodeNum; i++) {
         if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE && "image".equals(childNodes.item(i).getNodeName())) {
            this.backgroundImage = new Image(childNodes.item(i));
         }
      }
   }
   public boolean isFillScreen() {
      return fillScreen;
   }
   public boolean isBackgroundImageAbsolutePosition() {
      return isBackgroundImageAbsolutePosition;
   }
   public int getBackgroundImageAbsolutePositionLeft() {
      return backgroundImageAbsolutePositionLeft;
   }
   public int getBackgroundImageAbsolutePositionTop() {
      return backgroundImageAbsolutePositionTop;
   }
   public String getBackgroundImageRelativePosition() {
      return backgroundImageRelativePosition;
   }
   public Image getBackgroundImage() {
      return backgroundImage;
   }
}
