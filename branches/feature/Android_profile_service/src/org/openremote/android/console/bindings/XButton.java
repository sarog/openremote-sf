package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class XButton extends Control {

   private int buttonId;
   private String name;
   private boolean hasControlCommand;
   public XButton(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.buttonId = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
      this.name = nodeMap.getNamedItem("name").getNodeValue();
      if (nodeMap.getNamedItem("hasControlCommand") != null) {
         this.hasControlCommand = Boolean.valueOf(nodeMap.getNamedItem("hasControlCommand").getNodeValue());
      }
   }
   
   public int getButtonId() {
      return buttonId;
   }
   
   public String getName() {
      return name;
   }
   
   public boolean isHasControlCommand() {
      return hasControlCommand;
   }
}
