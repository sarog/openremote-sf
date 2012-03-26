package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class RoomOverlay extends JavaScriptObject {

  protected RoomOverlay() {}
  
  public final native String getName() /*-{ return this.name; }-*/;

  public final native ArrayOverlay<OutputOverlay> getOutputs() /*-{ return this.outputs; }-*/;

  public final native ArrayOverlay<ControlStationOverlay> getInputs() /*-{ return this.inputs; }-*/;

}
