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
package org.openremote.web.console.service;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.EnumControllerResponseCode;

import com.google.gwt.jsonp.client.TimeoutException;
import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public abstract class AsyncControllerCallback<T> implements AsyncCallback<T> {
	@Override
	public void onFailure(Throwable exception) {
		if (exception instanceof TimeoutException) {
			onFailure(EnumControllerResponseCode.NO_RESPONSE);
		} else {
			onFailure(EnumControllerResponseCode.UNKNOWN_ERROR);
		}
	}
	
	public void onFailure(EnumControllerResponseCode response) {
		WebConsole.getConsoleUnit().onError(response);
	}
}
