package org.openremote.web.console.rpc.json;

import org.openremote.web.console.entity.Group;
import org.openremote.web.console.entity.Screen;
import org.openremote.web.console.entity.TabBar;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class GroupJso extends JavaScriptObject implements Group {

	protected GroupJso() {}
	
	public final native int getGroupId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

//	public final native JsArray<ScreenJso> getScreens() /*-{
//		return this.screens;
//	}-*/;

	public final native TabBar getTabBar() /*-{
		return this.tabbar;
	}-*/;

	@Override
	public final Screen[] getScreens() {
		// TODO Auto-generated method stub
		return null;
	}
}
