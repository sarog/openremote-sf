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
package org.openremote.web.console.event.hold;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 * @author rich
 *
 */
public class HoldEvent extends GwtEvent<HoldHandler> {
	private static final Type<HoldHandler> TYPE = new Type<HoldHandler>();
	public static final int MIN_HOLD_TIME_MILLISECONDS = 1000;
	int xPos;
	int yPos;
	Widget source;
	
	public HoldEvent(int xPos, int yPos, Widget source) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.source = source;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<HoldHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HoldHandler handler) {
		handler.onHold(this);
	}

	public static Type<HoldHandler> getType() {
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
