/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.web.console.event.rotate;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author rich
 *
 */
public class RotationEvent extends GwtEvent<RotationHandler> {
	private static final Type<RotationHandler> TYPE = new Type<RotationHandler>();
	String orientation;
	int winWidth;
	int winHeight;
	
	public RotationEvent(String orientation, int winWidth, int winHeight) {
			this.orientation = orientation;
			this.winWidth = winWidth;
			this.winHeight = winHeight;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RotationHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RotationHandler handler) {
		handler.onRotate(this);
	}

	public static Type<RotationHandler> getType() {
		return TYPE;
	}

	public String getOrientation() {
		return orientation;
	}
	
	public int getWindowWidth() {
		return winWidth;
	}
	
	public int getWindowHeight() {
		return winHeight;
	}
}