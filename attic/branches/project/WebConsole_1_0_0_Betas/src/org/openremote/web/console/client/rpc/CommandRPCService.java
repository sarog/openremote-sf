/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.client.rpc;

import org.openremote.web.console.exception.NotAuthenticatedException;
import org.openremote.web.console.exception.ORConnectionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Sends control command to controller with url, username and password.
 */
@RemoteServiceRelativePath("command.smvc")
public interface CommandRPCService extends RemoteService {

   /**
    * Send control command to controller.
    * 
    * @param url 
    * The url can't be null.
    * @param username the username 
    * @param password the password
    * 
    * @throws NotAuthenticatedException the not authenticated exception
    * @throws ORConnectionException the OR connection exception
    */
   void sendCommand(String url, String username, String password) throws NotAuthenticatedException, ORConnectionException;
}
