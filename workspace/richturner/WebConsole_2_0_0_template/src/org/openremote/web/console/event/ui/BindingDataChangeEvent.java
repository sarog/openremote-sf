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
 * Binding data source changed event
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class BindingDataChangeEvent extends GwtEvent<BindingDataChangeHandler> {
	private static final Type<BindingDataChangeHandler> TYPE = new Type<BindingDataChangeHandler>();
	private String dataSource;
	
	public BindingDataChangeEvent(String dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BindingDataChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BindingDataChangeHandler handler) {
		handler.onBindingDataChange(this);
	}

	public static Type<BindingDataChangeHandler> getType() {
		return TYPE;
	}
	
	public String getDataSource() {
		return dataSource;
	}
}
