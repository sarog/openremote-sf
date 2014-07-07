package org.openremote.modeler.client.widget.utils;

import java.util.Set;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;

public class DraggableHelper<T> extends Label {

  private Set<T> draggedData;

  public Set<T> getDraggedData() {
    return draggedData;
  }

  public void setDraggedData(Set<T> draggedData) {
    this.draggedData = draggedData;
  }
  public DraggableHelper() {
    super();
    this.getElement().getStyle().setBorderWidth(1, Unit.PX);
    this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    this.getElement().getStyle().setOpacity(1.0);
  }
}
