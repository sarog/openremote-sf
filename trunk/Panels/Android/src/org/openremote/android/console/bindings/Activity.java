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
package org.openremote.android.console.bindings;

import java.io.Serializable;
import java.util.List;

import org.openremote.android.console.Constants;

/**
 * Activity is bound from the openremote definition xml file. It corresponds to
 * both an Android "Activity" as well as an actual "Activity" like "watch TV".
 * Activities can have multiple screens. This is used by SimpleBinder to read
 * the XML file obtained from the webserver. It follows the rules required for
 * SimpleBinder. Because the name conflicts with Android's activity the
 * ORActivity interface exists as an alias to avoid too much typing. Activity
 * and friends are all serializable because it is necessary to pass them in the
 * Intent which launches ActivityHandler.
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
