package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class AreaOverlay extends JavaScriptObject {

  protected AreaOverlay() {}
  
  public final native String getName() /*-{ return this.name; }-*/;

  public final native ArrayOverlay<RoomOverlay> getRooms() /*-{ return this.rooms; }-*/;

}
