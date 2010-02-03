package org.openremote.modeler.server;

import java.util.Set;

import org.openremote.modeler.client.rpc.ControllerConfigPRCService;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Config;
import org.openremote.modeler.service.ControllerConfigService;

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
