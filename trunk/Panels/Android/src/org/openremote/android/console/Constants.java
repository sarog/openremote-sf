/* OpenRemote, the Home of the Digital Home.
 * Copyright 2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.android.console;

import android.view.Menu;

/**
 * constants used throughout the application in more than one place
 * 
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 * 
 */
public interface Constants {

    long BINDING_VERSION = 1L;
    int MENU_ITEM_CONFIG = Menu.FIRST + 1;
    int MENU_ITEM_QUIT = Menu.FIRST + 2;
    int MENU_ITEM_ACTIVITIES = Menu.FIRST + 3;
    String ERROR = "error";
    int DIALOG_ERROR_ID = 0;
    int HTTP_SUCCESS = 200;
    String ACTIVITY = "activity";
    String ELEMENT_OPENREMOTE = "openremote";
    String ELEMENT_BUTTONS = "buttons";
    String URL = "url";
    String LOADER = "loader";
}
