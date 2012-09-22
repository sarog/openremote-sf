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
package org.openremote.web.console.event.press;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * This event defines press move event
 * @author rich
 *
 */
public class PressMoveEvent extends PressEvent<PressMoveHandler> {
	private static final Type<PressMoveHandler> TYPE = new Type<PressMoveHandler>();

	public PressMoveEvent(HumanInputEvent<? extends EventHandler> sourceEvent) {
		super(sourceEvent);
		
		if (sourceEvent.getClass().equals(MouseMoveEvent.class)) {
			MouseMoveEvent event = (MouseMoveEvent)sourceEvent;
			clientXPos = event.getClientX();
			clientYPos = event.getClientY();
			screenXPos = event.getScreenX();
			screenYPos = event.getScreenY();
		} else if (sourceEvent.getClass().equals(TouchMoveEvent.class)) {
			TouchMoveEvent event = (TouchMoveEvent)sourceEvent;
			Touch touch = event.getTouches().get(0);
			clientXPos = touch.getClientX();
			clientYPos = touch.getClientY();
			screenXPos = touch.getScreenX();
			screenYPos = touch.getScreenY();
		}
	}
	
	@Override
	public Type<PressMoveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressMoveHandler handler) {
		handler.onPressMove(this);
	}

	public static Type<PressMoveHandler> getType() {
		return TYPE;
	}
}
