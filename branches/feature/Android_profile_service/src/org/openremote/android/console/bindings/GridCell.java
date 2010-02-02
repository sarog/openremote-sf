package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class GridCell extends BusinessEntity {

   private int x;
   private int y;
   private int rowspan = 1;
   private int colspan = 1;
   private Component component;
   
   public GridCell(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.x = Integer.valueOf(nodeMap.getNamedItem("x").getNodeValue()); 
      this.y = Integer.valueOf(nodeMap.getNamedItem("y").getNodeValue());
      if (nodeMap.getNamedItem("rowspan") != null) {
         this.rowspan = Integer.valueOf(nodeMap.getNamedItem("rowspan").getNodeValue()); 
      }
      if (nodeMap.getNamedItem("colspan") != null) {
         this.colspan = Integer.valueOf(nodeMap.getNamedItem("colspan").getNodeValue());
      }
      
      NodeList nodes = node.getChildNodes();
      int nodeNum = nodes.getLength();
      for (int i = 0; i < nodeNum; i++) {
         if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
            this.component = Component.buildWithXML(nodes.item(i));
            break;
         }
      }
   }
   public int getX() {
      return x;
   }
   public int getY() {
      return y;
   }
   public int getRowspan() {
      return rowspan;
   }
   public int getColspan() {
      return colspan;
   }
   public Component getComponent() {
      return component;
   }
   
   
   
}
