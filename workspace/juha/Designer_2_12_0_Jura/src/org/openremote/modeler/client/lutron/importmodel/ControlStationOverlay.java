package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class ControlStationOverlay extends JavaScriptObject {
  
  protected ControlStationOverlay() {}
  
  public final native String getName() /*-{ return this.name; }-*/;

  public final native ArrayOverlay<DeviceOverlay> getDevices() /*-{ return this.devices; }-*/;

}
