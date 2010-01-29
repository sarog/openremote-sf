package org.openremote.android.console.bindings;

import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class Component extends BusinessEntity {

   public static Component buildWithXML(Node node) {
      if ("label".equals(node.getNodeName())) {
      } else if("image".equals(node.getNodeName())) {
      } else {
         return Control.buildWithXML(node);
      }
      return null;
   }

}
