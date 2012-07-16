package org.openremote.web.console.panel.entity;

import java.util.List;

public interface GridLayout {
   String getHeight();
   String getWidth();
   String getLeft();
   String getTop();
   String getRight();
   String getBottom();
	Integer getRows();
	Integer getCols();
	List<Cell> getCell();
	List<Column> getCol();
	
   void setHeight(String height);
   void setWidth(String width);
   void setLeft(String left);
   void setTop(String top);
   void setRight(String right);
   void setBottom(String bottom);
	void setRows(Integer rows);
	void setCols(Integer cols);
	void setCell(List<Cell> cells);
	void setCol(List<Column> columns);
}
