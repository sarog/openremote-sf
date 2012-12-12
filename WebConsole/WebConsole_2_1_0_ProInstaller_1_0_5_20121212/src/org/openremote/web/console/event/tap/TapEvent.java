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
package org.openremote.web.console.event.tap;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class TapEvent extends GwtEvent<TapHandler> {
	private static final Type<TapHandler> TYPE = new Type<TapHandler>();
	public static int TAP_X_TOLERANCE = 30;
	public static int TAP_Y_TOLERANCE = 30;
	private int xPos;
	private int yPos;
	private Widget source;
	
	public TapEvent(int xPos, int yPos, Widget source) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.source = source;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TapHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TapHandler handler) {
		handler.onTap(this);
	}

	public static Type<TapHandler> getType() {
		return TYPE;
	}

	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public Widget getSource() {
		return source;
	}
}
