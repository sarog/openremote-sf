package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class LutronImportResultOverlay extends JavaScriptObject {

  // Overlay types always have protected, zero-arg ctors
  protected LutronImportResultOverlay() { } 
    
  // Typically, methods on overlay types are JSNI
  public final native String getErrorMessage() /*-{ return this.errorMessage; }-*/;

  public final native ProjectOverlay getProject() /*-{ return this.project; }-*/;

  public static native LutronImportResultOverlay fromJSONString(String jsonString) /*-{
    return eval('(' + jsonString + ')');
  }-*/;
  
}
