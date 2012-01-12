package org.openremote.modeler.client.event;

import org.openremote.modeler.domain.Device;

import com.google.gwt.event.shared.GwtEvent;

public class DeviceUpdatedEvent extends GwtEvent<DeviceUpdatedEventHandler> {

  public static Type<DeviceUpdatedEventHandler> TYPE = new Type<DeviceUpdatedEventHandler>();

  private final Device updatedDevice;
  
  public DeviceUpdatedEvent(Device updatedDevice) {
    super();
    this.updatedDevice = updatedDevice;
  }

  public Device getUpdatedDevice() {
    return updatedDevice;
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<DeviceUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DeviceUpdatedEventHandler handler) {
    handler.onDeviceUpdated(this);
  }

}
