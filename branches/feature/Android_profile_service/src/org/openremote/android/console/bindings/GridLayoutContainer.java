package org.openremote.android.console.bindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class GridLayoutContainer extends LayoutContainer {

   private int rows;
   private int cols;
   public GridLayoutContainer(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.left = Integer.valueOf(nodeMap.getNamedItem("left").getNodeValue());
      this.top = Integer.valueOf(nodeMap.getNamedItem("top").getNodeValue());
      this.width = Integer.valueOf(nodeMap.getNamedItem("width").getNodeValue());
      this.height = Integer.valueOf(nodeMap.getNamedItem("height").getNodeValue());
      this.rows = Integer.valueOf(nodeMap.getNamedItem("rows").getNodeValue());
      this.cols = Integer.valueOf(nodeMap.getNamedItem("cols").getNodeValue());
   }
   
   public int getRows() {
      return rows;
   }
   
   public int getCols() {
      return cols;
   }
}
