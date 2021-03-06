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
package org.openremote.modeler.client.rpc;

import java.util.ArrayList;

import org.openremote.modeler.exception.ControllerManagementException;
import org.openremote.useraccount.domain.ControllerDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface is for managing linked controller.
 */
@RemoteServiceRelativePath("linkController.smvc")
public interface LinkControllerRPCService extends RemoteService {

   /**
    * Link controller to this account
    * 
    * @param macAddress - The MAC address of the controller that should be linked
    * 
    * @return the linked controller
    */
   ControllerDTO linkController(String macAddress) throws ControllerManagementException;
   
   /**
    * Delete the controller with the given oid
    * 
    * @param uid the uid
    */
   void deleteController(long oid) throws ControllerManagementException;
   
  
   /**
    * Get the list of linked controller
    * 
    * @return the linked controller
    */
   ArrayList<ControllerDTO> getLinkedControllerDTOs() throws ControllerManagementException;
}
