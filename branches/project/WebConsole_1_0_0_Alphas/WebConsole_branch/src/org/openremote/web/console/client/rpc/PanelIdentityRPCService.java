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

import java.util.List;

import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.exception.NotAuthenticatedException;
import org.openremote.web.console.exception.ORConnectionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * RPC interface for client gets panel names, gets panel entity, checks the current controller if is support JSON API.
 */
@RemoteServiceRelativePath("panelIdentity.smvc")
public interface PanelIdentityRPCService extends RemoteService {

   /**
    * Gets the panel identity names.
    * 
    * @param serverUrl the server url
    * @param username the username
    * @param password the password
    * 
    * @return the panel names
    * 
    * @throws NotAuthenticatedException the not authenticated exception
    * @throws ORConnectionException the OR connection exception
    */
   List<String> getPanelNames(String serverUrl, String username, String password) throws NotAuthenticatedException, ORConnectionException;
   
   /**
    * Gets the panel entity, which contains groups,screens and global tabBar.
    * 
    * @param url the url
    * @param username the username
    * @param password the password
    * 
    * @return the panel xml entity
    * 
    * @throws NotAuthenticatedException the not authenticated exception
    * @throws ORConnectionException the OR connection exception
    */
   PanelXmlEntity getPanelXmlEntity(String url, String username, String password) throws NotAuthenticatedException, ORConnectionException;
   
   /**
    * Checks the current controller if is support JSON API.
    * 
    * @param url the url
    * @param username the username
    * @param password the password
    * 
    * @throws NotAuthenticatedException the not authenticated exception
    * @throws ORConnectionException the OR connection exception
    */
   boolean isSupportJsonp(String url, String username, String password) throws NotAuthenticatedException, ORConnectionException;
}
