package org.openremote.web.console.panel.entity;

import java.util.List;

import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;

public interface FormLayout {
   String getHeight();
   String getWidth();
   String getLeft();
   String getTop();
   String getDataSource();
   List<Field> getField();
   List<FormButton> getButton();
	
   void setHeight(String height);
   void setWidth(String width);
   void setLeft(String left);
   void setTop(String top);
   void setDataSource(String dataSource);
   void setField(List<Field> field);
   void setButton(List<FormButton> button);
}
