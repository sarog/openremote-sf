package org.openremote.android.console.bindings;

import org.w3c.dom.Node;


@SuppressWarnings("serial")
public class Control extends Component {

   public static Control buildWithXML(Node node) {
      Control control = null;
      if ("button".equals(node.getNodeName())) {
         control =  new XButton(node);
      }
      return control;
   }
}
