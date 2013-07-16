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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventHandler;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class PressStartEvent extends PressEvent<PressStartHandler> {
	private static final Type<PressStartHandler> TYPE = new Type<PressStartHandler>();

	public PressStartEvent(HumanInputEvent<? extends EventHandler> sourceEvent) {
		super(sourceEvent);
		
		if (sourceEvent.getClass().equals(MouseDownEvent.class)) {
			MouseDownEvent event = (MouseDownEvent)sourceEvent;
			clientXPos = event.getClientX();
			clientYPos = event.getClientY();
			screenXPos = event.getScreenX();
			screenYPos = event.getScreenY();
		} else if (sourceEvent.getClass().equals(TouchStartEvent.class)) {
			TouchStartEvent event = (TouchStartEvent)sourceEvent;
			Touch touch = event.getTouches().get(0);
			clientXPos = touch.getClientX();
			clientYPos = touch.getClientY();
			screenXPos = touch.getScreenX();
			screenYPos = touch.getScreenY();
		}
	}
	
	@Override
	public Type<PressStartHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressStartHandler handler) {
		handler.onPressStart(this);
	}

	public static Type<PressStartHandler> getType() {
		return TYPE;
	}
}
