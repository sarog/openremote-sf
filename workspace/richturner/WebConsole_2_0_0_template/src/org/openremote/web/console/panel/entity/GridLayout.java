package org.openremote.web.console.panel.entity;

import java.util.List;

public interface GridLayout {
	int getLeft();
	int getTop();
	int getWidth();
	int getHeight();
	int getRows();
	int getCols();
	List<Cell> getCell();
	
	void setLeft(int left);
	void setTop(int top);
	void setWidth(int width);
	void setHeight(int height);
	void setRows(int rows);
	void setCols(int cols);
	void setCell(List<Cell> cells);
}
