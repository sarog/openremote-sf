package org.openremote.modeler.client.lutron.importmodel;

import com.google.gwt.core.client.JavaScriptObject;

public class ArrayOverlay<E extends JavaScriptObject> extends JavaScriptObject {

  protected ArrayOverlay() {
  }

  public final native int length() /*-{
		return this.length;
  }-*/;

  public final native E get(int i) /*-{
		return this[i];
  }-*/;
}
