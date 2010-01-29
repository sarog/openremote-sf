package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class AbsoluteLayoutContainer extends LayoutContainer {

   private Component component;
   
   public AbsoluteLayoutContainer(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.left = Integer.valueOf(nodeMap.getNamedItem("left").getNodeValue());
      this.top = Integer.valueOf(nodeMap.getNamedItem("top").getNodeValue());
      this.width = Integer.valueOf(nodeMap.getNamedItem("width").getNodeValue());
      this.height = Integer.valueOf(nodeMap.getNamedItem("height").getNodeValue());
      NodeList nodes = node.getChildNodes();
      int nodeNum = nodes.getLength();
      for (int i = 0; i < nodeNum; i++) {
         if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
            this.component = Component.buildWithXML(nodes.item(i));
            break;
         }
      }
   }
   
   public Component getComponent() {
      return component;
   }
}
