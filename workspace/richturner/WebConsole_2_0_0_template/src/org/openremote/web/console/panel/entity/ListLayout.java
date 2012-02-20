package org.openremote.web.console.panel.entity;

import java.util.List;

public interface ListLayout {
   String getHeight();
   String getWidth();
   String getLeft();
   String getTop();
   String getDataSource();
   String getItemBindingObject();
   ListItemLayout getItemtemplate();
	
   void setHeight(String height);
   void setWidth(String width);
   void setLeft(String left);
   void setTop(String top);
   void setDataSource(String dataSource);
   void setItemBindingObject(String bindingObject);
   void setItemtemplate(ListItemLayout itemLayout);
}
