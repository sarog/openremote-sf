package org.openremote.android.console.bindings;

import java.io.Serializable;
import java.util.List;

import org.openremote.android.console.Constants;

/**
 * Activity is bound from the openremote definition xml file.  It corresponds to both an Android "Activity" as well as an actual "Activity" like
 * "watch TV".  Activities can have multiple screens.  This is used by SimpleBinder to read the XML file obtained from the webserver.  It follows
 * the rules required for SimpleBinder.  Because the name conflicts with Android's activity the ORActivity interface exists as an alias to avoid
 * too much typing.  Activity and friends are all serializable because it is necessary to pass them in the Intent which launches ActivityHandler.
 * 
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 */
public class Activity implements ORActivity, Serializable {
	
	private static final long serialVersionUID = Constants.BINDING_VERSION;
	private String id;
	private String name;
	private List<Screen> screens;
	
	public List<Screen> getScreens() {
		return screens;
	}


	public void setScreens(List<Screen> screens) {
		this.screens = screens;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return this.name;
	}

}
