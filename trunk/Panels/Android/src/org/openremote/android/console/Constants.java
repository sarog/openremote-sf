package org.openremote.android.console;

import android.view.Menu;

/**
 * constants used throughout the application in more than one place
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 *
 */
public interface Constants {

	long BINDING_VERSION = 1L;
	int MENU_ITEM_CONFIG = Menu.FIRST+1;
	int MENU_ITEM_QUIT = Menu.FIRST+2;
	int MENU_ITEM_ACTIVITIES = Menu.FIRST+3;
	String ERROR = "error";
	int DIALOG_ERROR_ID = 0;
	int HTTP_SUCCESS = 200;
	String ACTIVITY = "activity";
    String ELEMENT_OPENREMOTE = "openremote";
	String ELEMENT_BUTTONS = "buttons";
	String URL = "url";
	String LOADER = "loader";
}
