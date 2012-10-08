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

import org.openremote.ir.domain.CodeSetInfo;
import org.openremote.ir.domain.IRCommandInfo;
import org.openremote.modeler.shared.ir.IRServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service that handles IR import and conversion.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
@RemoteServiceRelativePath("ir.smvc")
public interface IRRPCService extends RemoteService {

  ArrayList<String> getBrands(String prontoHandle) throws IRServiceException;

  ArrayList<String> getDevices(String prontoHandle, String brandName) throws IRServiceException;
  
  ArrayList<CodeSetInfo> getCodeSets(String prontoHandle, String brandName, String deviceName) throws IRServiceException;
  
  ArrayList<IRCommandInfo> getIRCommands(String prontoHandle, String brandName, String deviceName, int index) throws IRServiceException;

  void unregisterFile(String prontoHandle);

}
