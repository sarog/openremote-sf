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

import org.openremote.android.console.Constants;

/**
 * Buttons are contained by instances of Screen. ORButton interface exists as a
 * convenience interface due to the name conflict with Android's Button (so that
 * you don't have to type the FQN every time). The class conforms to the rules
 * required by SimpleBinder. It is used together with Screen by ActivityHandler
 * to construct the views for activities. It is constructed by SimpleBinder
 * based on the openremote definition XML files.
 * 
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 * 
 */
public class Button implements ORButton, Serializable {
    private static final long serialVersionUID = Constants.BINDING_VERSION;
    private String id;
    private String label;
    private int x;
    private int y;
    private int width;
    private int height;
    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
