package org.openremote.web.console.rpc.json;

import org.openremote.web.console.panel.PanelIdentity;
import com.google.gwt.core.client.JavaScriptObject;

public class PanelIdentityJso extends JavaScriptObject implements PanelIdentity {
	
	protected PanelIdentityJso() {}
	
	public final native String getName() /*-{
   	return this.name;
 	}-*/;
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
}
