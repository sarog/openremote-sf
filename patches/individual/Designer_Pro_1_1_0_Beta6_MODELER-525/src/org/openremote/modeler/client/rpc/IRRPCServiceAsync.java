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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public interface IRRPCServiceAsync {

  void getBrands(String prontoHandle, AsyncCallback<ArrayList<String>> callback);
  
  void getDevices(String prontoHandle, String brandName, AsyncCallback<ArrayList<String>> callback);
  
  void getCodeSets(String prontoHandle, String brandName, String deviceName, AsyncCallback<ArrayList<CodeSetInfo>> callback);
  
  void getIRCommands(String prontoHandle, String brandName, String deviceName, int index, AsyncCallback<ArrayList<IRCommandInfo>> callback);
  
  void unregisterFile(String prontoHandle, AsyncCallback<Void> callback);

}
