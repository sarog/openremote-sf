/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.util.List;

import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.CodeSetInfo;
import org.openremote.modeler.client.DeviceInfo;
import org.openremote.modeler.client.IRCommandInfo;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRTrans;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IRFileParserRPCServiceAsync {

//	void getDevices(Brand brand, AsyncCallback<List<Device>> callback);


	void getBrands(AsyncCallback<List<BrandInfo>> callback);
	void getDevices(BrandInfo brand, AsyncCallback<List<DeviceInfo>> callback);
	void getCodeSets(DeviceInfo device,
			AsyncCallback<List<CodeSetInfo>> callback);
	void getIRCommands(CodeSetInfo codeset,
			AsyncCallback<List<IRCommandInfo>> callback);
	void saveCommands(Device device, GlobalCache globalCache, IRTrans irTrans, List<IRCommandInfo> selectedFunctions,
			AsyncCallback<Void> callback);
	



}
