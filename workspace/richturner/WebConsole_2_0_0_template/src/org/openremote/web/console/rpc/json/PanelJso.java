package org.openremote.web.console.rpc.json;

import org.openremote.web.console.entity.Group;
import org.openremote.web.console.entity.Screen;
import org.openremote.web.console.entity.TabBar;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentity;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class PanelJso extends JavaScriptObject implements Panel {
	
	protected PanelJso() {}
	
	public final native JsArray<GroupJso> getJsoGroups() /*-{
		return this.groups.group;
	}-*/;

//	public final native JsArray<ScreenJso> getJsoScreens() /*-{
//		return this.screens;
//	}-*/;
	
//	public final native TabBarJso getJsoTabBar() /*-{
//		return this.tabbar;
//	}-*/;
	
	@Override
	public final Group[] getGroups() {
		JsArray<GroupJso> jsoGroups = getJsoGroups();
		Group[] groups = new Group[jsoGroups.length()];
		for (int i=0; i<jsoGroups.length(); i++) {
			groups[i] = (Group)jsoGroups.get(i);
		}
		return groups;
	}

	@Override
	public final Screen[] getScreens() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final TabBar getTabBar() {
		// TODO Auto-generated method stub
		return null;
	}
}
