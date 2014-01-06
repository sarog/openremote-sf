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

import com.google.gwt.event.shared.GwtEvent;
/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ScreenViewChangeEvent extends GwtEvent<ScreenViewChangeHandler> {
	private static final Type<ScreenViewChangeHandler> TYPE = new Type<ScreenViewChangeHandler>();
	private int screenId;
	private int groupId;
	
	public ScreenViewChangeEvent(int screenId, int groupId) {
		this.screenId = screenId;
		this.groupId = groupId;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ScreenViewChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ScreenViewChangeHandler handler) {
		handler.onScreenViewChange(this);
	}

	public static Type<ScreenViewChangeHandler> getType() {
		return TYPE;
	}
	
	public int getNewScreenId() {
		return screenId;
	}
	
	public int getNewGroupId() {
		return groupId;
	}
}
