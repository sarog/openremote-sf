package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class OutputOverlay extends JavaScriptObject {

  protected OutputOverlay() {}
  
  public final native String getName() /*-{ return this.name; }-*/;

  public final native String getAddress() /*-{ return this.address; }-*/;

  public final native String getType() /*-{ return this.type; }-*/;

}
