package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class XScreen extends BusinessEntity {

   private int screenId;
   private String name;
   
   @Override
   public String getElementName() {
      return "screen";
   }

   @Override
   public void initWithXML(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.screenId = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
      this.name = nodeMap.getNamedItem("name").getNodeValue();
   }

   public int getScreenId() {
      return screenId;
   }

   public String getName() {
      return name;
   }

   
}
