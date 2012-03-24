package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class ProjectOverlay extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected ProjectOverlay() { } 
  
  // Typically, methods on overlay types are JSNI
  public final native String getName() /*-{ return this.name; }-*/;

  public final native ArrayOverlay<AreaOverlay> getAreas() /*-{ return this.areas; }-*/;
}
