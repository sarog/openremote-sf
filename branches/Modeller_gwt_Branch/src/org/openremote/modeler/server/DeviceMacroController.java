/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import org.openremote.modeler.client.rpc.DeviceMacroRPCService;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.service.DeviceMacroService;

import java.util.List;

/**
 * The Class DeviceMacroController.
 */
public class DeviceMacroController extends BaseGWTSpringControllerWithHibernateSupport implements DeviceMacroRPCService {
   
   /** The device macro service. */
   private DeviceMacroService deviceMacroService;


    /**
     * Sets the device macro service.
     * 
     * @param deviceMacroService the new device macro service
     */
   public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
      this.deviceMacroService = deviceMacroService;
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#loadAll()
    */
   public List<DeviceMacro> loadAll() {
      return deviceMacroService.loadAll();
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#save(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroService.saveDeviceMacro(deviceMacro);
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#edit(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroService.updateDeviceMacro(deviceMacro);
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      deviceMacroService.deleteDeviceMacro(id);
   }

    /* (non-Javadoc)
     * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#loadDeviceMacroItems(org.openremote.modeler.domain.DeviceMacro)
     */
    public List<DeviceMacroItem> loadDeviceMacroItems(DeviceMacro deviceMacro) {
        return deviceMacroService.loadById(deviceMacro.getOid()).getDeviceMacroItems();
    }

}
