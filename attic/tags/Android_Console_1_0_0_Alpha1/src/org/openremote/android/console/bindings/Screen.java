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
 * Screen is used by the binding api to parse the XML file, each Activity
 * contains a collection of Screens. These are used by ActivityHandler to
 * configure a set of screens. Screens correspond to Views in Android
 * terminology. Screens contain buttons. This class conforms to the rules
 * required by SimpleBinder. ORScreen is an interface included for consistency
 * since Activity and Button have similar interfaces, there is no need to use
 * ORScreen.
 * 
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 * 
 */
public class Screen implements ORScreen, Serializable {

    private static final long serialVersionUID = Constants.BINDING_VERSION;
    private String id;
    private String name;
    private int row;
    private int col;
    private List<Button> buttons;

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#getButtons()
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#setButtons(java.util.List)
     */
    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#getId()
     */
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#setId(java.lang.String)
     */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#getRow()
     */
    public int getRow() {
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#setRow(int)
     */
    public void setRow(int row) {
        this.row = row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#getCol()
     */
    public int getCol() {
        return col;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openremote.android.console.ORScreen#setCol(int)
     */
    public void setCol(int col) {
        this.col = col;
    }

}
