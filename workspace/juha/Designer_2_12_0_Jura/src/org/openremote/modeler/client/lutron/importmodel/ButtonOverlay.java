package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class ButtonOverlay extends JavaScriptObject {

  protected ButtonOverlay() {}
  
  public final native String getName() /*-{ return this.name; }-*/;

  public final native String getNumber() /*-{ return this.number; }-*/;

}
