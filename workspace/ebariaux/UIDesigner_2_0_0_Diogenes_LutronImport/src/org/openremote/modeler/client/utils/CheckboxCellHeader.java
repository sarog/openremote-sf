package org.openremote.modeler.client.utils;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;

/**
 * Header to be used with CellTable allowing select all/deselect all checkbox behavior.
 * 
 * Based on code found here: http://snipt.net/araujo921/checkbox-in-gwt-celltable-header/
 * 
 * @author ebr
 */
public class CheckboxCellHeader extends Header<Boolean> {

  public CheckboxCellHeader(Cell<Boolean> cell) {
    super(cell);
  }

  private boolean value;

  private ChangeValue changeValue;

  @Override
  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }
  
  public void setChangeValue(ChangeValue changeValue) {
    this.changeValue = changeValue;
  }

  @Override
  public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
    Event evt = Event.as(event);
    int eventType = evt.getTypeInt();
    switch (eventType) {
      case Event.ONCHANGE:
        value = !value;
        if (changeValue != null)
          changeValue.changedValue(context.getColumn(), value);
        break;
    }
    super.onBrowserEvent(context, elem, event);
  }

  @Override
  public void render(Context context, SafeHtmlBuilder sb) {
    super.render(context, sb.appendEscaped(""));
  }

  // Checkbox changed Handler

  public interface ChangeValue {
    void changedValue(int columnIndex, Boolean value);
  }
}
