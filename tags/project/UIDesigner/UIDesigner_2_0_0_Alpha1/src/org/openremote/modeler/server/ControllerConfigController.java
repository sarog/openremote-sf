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

import java.util.Set;

import org.openremote.modeler.client.rpc.ControllerConfigPRCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Config;
import org.openremote.modeler.service.ControllerConfigService;
/**
 * The controller for configuring the Controller. 
 * @author javen
 *
 */
@SuppressWarnings("serial")
public class ControllerConfigController extends BaseGWTSpringControllerWithHibernateSupport implements ControllerConfigPRCService{
   private ControllerConfigService controllerConfigService = null;
   @Override
   public Set<Config> getConfigsByCategory(String categoryName, Account account) {
      return controllerConfigService.listAllConfigByCategoryNameForAccouont(categoryName, account);
   }

   @Override
   public Set<Config> getConfigsByCategoryForCurrentAccount(String categoryName) {
      return controllerConfigService.listAllConfigByCategoryForCurrentAccount(categoryName);
   }

   @Override
   public Set<Config> saveAll(Set<Config> cfgs) {
      return controllerConfigService.saveAll(cfgs);
   }

   @Override
   public Config update(Config config) {
      return controllerConfigService.update(config);
   }

   /*@Override
   public Set<ConfigCategory> getCategories() {
      return controllerConfigService.listAllCategory();
   }*/
   
   public void setControllerConfigService(ControllerConfigService controllerConfigService) {
      this.controllerConfigService = controllerConfigService;
   }


}
