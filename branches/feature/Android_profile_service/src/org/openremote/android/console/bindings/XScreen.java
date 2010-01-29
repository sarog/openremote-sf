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
            } else if ("grid".equals(nodeName)){
               layouts.add(new GridLayoutContainer(childNodes.item(i)));
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
}
