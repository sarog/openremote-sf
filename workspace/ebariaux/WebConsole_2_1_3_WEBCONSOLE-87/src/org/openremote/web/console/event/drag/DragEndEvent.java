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
package org.openremote.web.console.event.drag;

import org.openremote.web.console.event.press.PressEndEvent;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class DragEndEvent extends DragEvent<DragEndHandler> {
	private static final Type<DragEndHandler> TYPE = new Type<DragEndHandler>();

	public DragEndEvent(PressEndEvent event) {
		xPos = event.getClientX();
		yPos = event.getClientY();
		source = event.getSource();
	}
	
	@Override
	public Type<DragEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragEndHandler handler) {
		handler.onDragEnd(this);
	}

	public static Type<DragEndHandler> getType() {
		return TYPE;
	}
}
