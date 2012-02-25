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
import java.util.List;

import org.openremote.modeler.client.rpc.DeviceMacroRPCService;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroItemDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

/**
 * The server side implementation of the RPC service <code>DeviceMacroRPCService</code>.
 */
@SuppressWarnings("serial")
public class DeviceMacroController extends BaseGWTSpringController implements DeviceMacroRPCService {
   
   /** The device macro service. */
   private DeviceMacroService deviceMacroService;
   
   /** The user service. */
   private UserService userService;


    /**
     * Sets the device macro service.
     * 
     * @param deviceMacroService the new device macro service
     */
   public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
      this.deviceMacroService = deviceMacroService;
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#loadAll()
    */
   public List<DeviceMacro> loadAll() {
      return deviceMacroService.loadAll(userService.getAccount());
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#save(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroService.saveDeviceMacro(deviceMacro);
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#updateDeviceMacro(org.openremote.modeler.domain.DeviceMacro, List<org.openremote.modeler.domain.DeviceMacroItem> items)
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro, List<DeviceMacroItem> items) {
     
      return deviceMacroService.updateDeviceMacro(deviceMacro, items);
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      deviceMacroService.deleteDeviceMacro(id);
   }

    /**
     * {@inheritDoc}
     * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#loadDeviceMacroItems(org.openremote.modeler.domain.DeviceMacro)
     */
    public List<DeviceMacroItem> loadDeviceMacroItems(DeviceMacro deviceMacro) {
        return deviceMacroService.loadByDeviceMacro(deviceMacro.getOid());
    }


   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public ArrayList<MacroDTO> loadAllDTOs() {
     ArrayList<MacroDTO> dtos = new ArrayList<MacroDTO>();
     for (DeviceMacro dm : deviceMacroService.loadAll(userService.getAccount())) {
       MacroDTO dto = new MacroDTO(dm.getOid(), dm.getDisplayName());
       ArrayList<MacroItemDTO> itemDTOs = new ArrayList<MacroItemDTO>();
       for (DeviceMacroItem dmi : dm.getDeviceMacroItems()) {
         if (dmi instanceof DeviceMacroRef) {
           itemDTOs.add(new MacroItemDTO(((DeviceMacroRef)dmi).getTargetDeviceMacro().getName(), MacroItemType.Macro));
         } else if (dmi instanceof DeviceCommandRef) {
           itemDTOs.add(new MacroItemDTO(((DeviceCommandRef)dmi).getDeviceCommand().getName(), MacroItemType.Command));
         } else if (dmi instanceof CommandDelay) {
           itemDTOs.add(new MacroItemDTO("Delay(" + ((CommandDelay)dmi).getDelaySecond() + "s)", MacroItemType.Delay));
         }
       }
       dto.setItems(itemDTOs);
       dtos.add(dto);
     }
     return dtos;
   }
}
