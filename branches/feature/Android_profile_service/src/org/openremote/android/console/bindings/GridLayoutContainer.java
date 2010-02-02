package org.openremote.android.console.bindings;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class GridLayoutContainer extends LayoutContainer {

   private int rows;
   private int cols;
   private ArrayList<GridCell> cells;
   
   public GridLayoutContainer(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      this.left = Integer.valueOf(nodeMap.getNamedItem("left").getNodeValue());
      this.top = Integer.valueOf(nodeMap.getNamedItem("top").getNodeValue());
      this.width = Integer.valueOf(nodeMap.getNamedItem("width").getNodeValue());
      this.height = Integer.valueOf(nodeMap.getNamedItem("height").getNodeValue());
      this.rows = Integer.valueOf(nodeMap.getNamedItem("rows").getNodeValue());
      this.cols = Integer.valueOf(nodeMap.getNamedItem("cols").getNodeValue());
      cells = new ArrayList<GridCell>();
      NodeList cellNodes = node.getChildNodes();
      int cellNodeSize = cellNodes.getLength();
      for (int i = 0; i < cellNodeSize; i++) {
         if(cellNodes.item(i).getNodeType() == Node.ELEMENT_NODE && "cell".equals(cellNodes.item(i).getNodeName())) {
            cells.add(new GridCell(cellNodes.item(i)));
         }
      }
   }
   
   public int getRows() {
      return rows;
   }
   
   public int getCols() {
      return cols;
   }
   
   public ArrayList<GridCell> getCells() {
      return cells;
   }
}
