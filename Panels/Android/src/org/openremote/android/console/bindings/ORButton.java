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

/**
 * This is a simple convenience interface so that you do not have to type the
 * FQN of Button each time due to name conflicts. SimpleBinder needs the real
 * class.
 * 
 * @see org.openremote.android.console.bindings.Button
 * @author Andrew C. Oliver <acoliver osintegrators.com>
 */
public interface ORButton extends Serializable {

    public String getId();

    public void setId(String id);

    public String getLabel();

    public void setLabel(String label);

    public int getX();

    public void setX(int x);

    public int getY();

    public void setY(int y);

    public int getWidth();

    public void setWidth(int width);

    public int getHeight();

    public void setHeight(int height);

    public String getIcon();

    public void setIcon(String icon);

}
