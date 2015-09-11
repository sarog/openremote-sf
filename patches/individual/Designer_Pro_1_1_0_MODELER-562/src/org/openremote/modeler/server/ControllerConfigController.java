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
package org.openremote.modeler.server;

import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.client.rpc.ControllerConfigRPCService;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.shared.dto.ControllerConfigDTO;
/**
 * The controller for configuring the Controller. 
 * @author javen
 *
 */
@SuppressWarnings("serial")
public class ControllerConfigController extends BaseGWTSpringController implements ControllerConfigRPCService{
   private ControllerConfigService controllerConfigService = null;

   public void setControllerConfigService(ControllerConfigService controllerConfigService) {
      this.controllerConfigService = controllerConfigService;
   }

   @Override
   public HashSet<ControllerConfigDTO> getConfigDTOsByCategoryForCurrentAccount(String categoryName) {
     return new HashSet<ControllerConfigDTO>(controllerConfigService.listAllConfigDTOsByCategory(categoryName));
   }

   @Override
   public HashSet<ControllerConfigDTO> listAllMissedConfigDTOsByCategoryName(String categoryName) {
     return new HashSet<ControllerConfigDTO>(controllerConfigService.listMissedConfigDTOsByCategoryName(categoryName));
   }

   @Override
   public HashSet<ControllerConfigDTO> saveAllDTOs(HashSet<ControllerConfigDTO> configs) {
     return new HashSet<ControllerConfigDTO>(controllerConfigService.saveAllDTOs(configs));
   }

   @Override
   public void resetToDefaults(HashSet<Long> configIds) {
     controllerConfigService.resetToDefaults(configIds);
   }

}
