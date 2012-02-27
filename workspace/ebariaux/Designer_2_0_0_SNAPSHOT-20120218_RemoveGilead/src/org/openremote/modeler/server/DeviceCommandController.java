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

package org.openremote.modeler.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.DeviceCommandRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;

/**
 * The server side implementation of the RPC service <code>DeviceCommandRPCService</code>.
 */
public class DeviceCommandController extends BaseGWTSpringController implements
        DeviceCommandRPCService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -8417889117208060088L;
   
   /** The device command service. */
   private DeviceCommandService deviceCommandService;

    /**
     * Sets the device command service.
     * 
     * @param deviceCommandRPCService the new device command service
     */
    public void setDeviceCommandService(DeviceCommandService deviceCommandRPCService) {
      this.deviceCommandService = deviceCommandRPCService;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceCommandRPCService#saveAll(java.util.List)
    */
   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      return deviceCommandService.saveAll(deviceCommands);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceCommandRPCService#save(org.openremote.modeler.domain.DeviceCommand)
    */
   public DeviceCommand save(DeviceCommand deviceCommand) {
      return deviceCommandService.save(deviceCommand);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceCommandRPCService#deleteCommand(long)
    */
   public Boolean deleteCommand(long id) {
      return deviceCommandService.deleteCommand(id);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceCommandRPCService#update(org.openremote.modeler.domain.DeviceCommand)
    */
   public DeviceCommand update(DeviceCommand deviceCommand) {
      return deviceCommandService.update(deviceCommand);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceCommandRPCService#loadById(long)
    */
   public DeviceCommand loadById(long id) {
      return deviceCommandService.loadById(id);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.openremote.modeler.client.rpc.DeviceCommandRPCService#loadByDevice(long)
    */
   public List<DeviceCommand> loadByDevice(long id) {
      return deviceCommandService.loadByDevice(id);
   }

   public ArrayList<DeviceCommandDTO> loadCommandsDTOByDevice(long id) {
     System.out.println(">>DeviceCommandController.loadCommandsDTOByDevice");
     return deviceCommandService.loadCommandsDTOByDevice(id);
   }
   
   public DeviceCommandDetailsDTO loadCommandDetailsDTO(long id) {
     DeviceCommand dc = deviceCommandService.loadById(id);
     DeviceCommandDetailsDTO dto = new DeviceCommandDetailsDTO(dc.getOid(), dc.getName(), dc.getProtocol().getType());
     HashMap<String, String> attributes = new HashMap<String, String>();
     for (ProtocolAttr attr : dc.getProtocol().getAttributes()) {
       attributes.put(attr.getName(), attr.getValue());
     }
     dto.setProtocolAttributes(attributes);
     return dto;
   }

   public void updateDeviceCommandWithDTO(DeviceCommandDetailsDTO dto) {
     DeviceCommand dc = deviceCommandService.loadById(dto.getOid());
     dc.setName(dto.getName());
     Protocol protocol = new Protocol();
     protocol.setDeviceCommand(dc);
     dc.setProtocol(protocol);
     protocol.setType(dto.getProtocolType());
     for (Map.Entry<String, String> e : dto.getProtocolAttributes().entrySet()) {
       protocol.addProtocolAttribute(e.getKey(), e.getValue());
     }
     deviceCommandService.update(dc);
   }
}
