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

import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;
import org.openremote.modeler.shared.dto.DeviceWithChildrenDTO;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>DeviceRPCService</code>.
 */
public interface DeviceRPCServiceAsync {
   
   /**
    * Delete device.
    * 
    * @param id the id
    * @param callback the callback
    */
   void deleteDevice(long id, AsyncCallback<Void> callback);
  
  void loadAllDTOs(AsyncCallback<ArrayList<DeviceDTO>> callback);
  
  void loadDeviceWithChildrenDTOById(long oid, AsyncCallback<DeviceWithChildrenDTO> callback);

  void loadDeviceWithCommandChildrenDTOById(long oid, AsyncCallback<DeviceWithChildrenDTO> callback);
  
  void loadDeviceDetailsDTO(long oid, AsyncCallback<DeviceDetailsDTO> callback);
  
  void saveNewDevice(DeviceDetailsDTO device, AsyncCallback<DeviceDTO> callback);
  
  void saveNewDeviceWithChildren(DeviceDetailsDTO device, ArrayList<DeviceCommandDetailsDTO> commands,
          ArrayList<SensorDetailsDTO> sensors, ArrayList<SwitchDetailsDTO> switches, ArrayList<SliderDetailsDTO> sliders, AsyncCallback<DeviceDTO> callback);

  void updateDeviceWithDTO(DeviceDetailsDTO device, AsyncCallback<Void> callback);
}
