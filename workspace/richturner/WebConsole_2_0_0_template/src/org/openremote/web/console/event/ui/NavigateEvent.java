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
package org.openremote.web.console.event.ui;

import org.openremote.web.console.panel.entity.Navigate;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author rich
 */
public class NavigateEvent extends GwtEvent<NavigateHandler> {
	private static final Type<NavigateHandler> TYPE = new Type<NavigateHandler>();
	private Navigate navigate;
	
	public NavigateEvent(Navigate navigate) {
		this.navigate = navigate;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<NavigateHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NavigateHandler handler) {
		handler.onNavigate(this);
	}

	public static Type<NavigateHandler> getType() {
		return TYPE;
	}
	
	public Navigate getNavigate() {
		return navigate;
	}
}
