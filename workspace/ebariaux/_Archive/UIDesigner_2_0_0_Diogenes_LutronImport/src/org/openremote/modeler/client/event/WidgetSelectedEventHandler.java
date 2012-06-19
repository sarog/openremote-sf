package org.openremote.modeler.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface WidgetSelectedEventHandler extends EventHandler {

  void onSelectionChanged(WidgetSelectedEvent event);

}
