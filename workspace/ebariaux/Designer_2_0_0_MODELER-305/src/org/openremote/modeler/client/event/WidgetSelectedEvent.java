package org.openremote.modeler.client.event;

import java.util.List;

import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;

import com.google.gwt.event.shared.GwtEvent;

public class WidgetSelectedEvent extends GwtEvent<WidgetSelectedEventHandler> {
  
  public static Type<WidgetSelectedEventHandler> TYPE = new Type<WidgetSelectedEventHandler>();

  private final List<ComponentContainer> selectedWidgets;
  
  public WidgetSelectedEvent(List<ComponentContainer> selectedWidgets) {
    super();
    this.selectedWidgets = selectedWidgets;
  }

  public List<ComponentContainer> getSelectedWidgets() {
    return selectedWidgets;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<WidgetSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(WidgetSelectedEventHandler handler) {
    handler.onSelectionChanged(this);
  }

}
