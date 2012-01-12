package org.openremote.modeler.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeviceUpdatedEventHandler extends EventHandler {

  void onDeviceUpdated(DeviceUpdatedEvent event);

}
