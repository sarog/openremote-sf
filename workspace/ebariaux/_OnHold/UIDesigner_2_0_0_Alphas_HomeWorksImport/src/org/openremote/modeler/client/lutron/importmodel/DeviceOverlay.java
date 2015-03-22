package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class DeviceOverlay extends JavaScriptObject {

  protected DeviceOverlay() {}
  
  public final native String getAddress() /*-{ return this.address; }-*/;

  public final native String getType() /*-{ return this.type; }-*/;
  
  public final native boolean getWebEnabled() /*-{ return this.webEnabled; }-*/;

  public final native String getWebKeypadName() /*-{ return this.webKeypadName; }-*/;

  public final native ArrayOverlay<ButtonOverlay> getButtons() /*-{ return this.buttons; }-*/;

}
