package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class Image extends BusinessEntity {

   private String src;
   private String style;
   
   public Image(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      if (nodeMap.getNamedItem("src") != null) {
         this.src = nodeMap.getNamedItem("src").getNodeValue();
      }
      // TODO: parse sub nodes(sensory/include).
   }
   public String getSrc() {
      return src;
   }
   public String getStyle() {
      return style;
   }
   
   
}
