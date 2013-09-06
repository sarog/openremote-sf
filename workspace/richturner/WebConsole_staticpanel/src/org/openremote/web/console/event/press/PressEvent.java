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

import java.util.Date;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public abstract class PressEvent<H extends EventHandler> extends GwtEvent<H> {
	protected int clientXPos;
	protected int clientYPos;
	protected int screenXPos;
	protected int screenYPos;
	protected long time;
	protected Widget source;
	
	public PressEvent(GwtEvent<? extends EventHandler> sourceEvent) {
		time = new Date().getTime();
		source = (Widget)sourceEvent.getSource();
	}
	
	public int getClientX() {
		return clientXPos;
	}
	
	public int getClientY() {
		return clientYPos;
	}
	
	public int getScreenX() {
		return screenXPos;
	}
	
	public int getScreenY() {
		return screenYPos;
	}
	
	public Widget getSource() {
		return source;
	}
	
	public void setSource(Widget source) {
		this.source = source;
	}
	
	public long getTime() {
		return time;
	}
}
