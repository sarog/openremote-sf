package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.SwitchRPCService;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.service.impl.UserServiceImpl;

@SuppressWarnings("serial")
public class SwitchController extends BaseGWTSpringControllerWithHibernateSupport implements SwitchRPCService{

   private SwitchService switchService;
   
   private UserService userService;
   
   @Override
   public void delete(Switch switchToggle) {
      switchService.delete(switchToggle);
   }

   @Override
   public List<Switch> loadAll() {
      return switchService.loadAll();
   }

   @Override
   public void save(Switch switchToggle) {
      switchToggle.setAccount(userService.getAccount());
      switchService.save(switchToggle);
   }

   
   @Override
   public void update(Switch switchToggle) {
      switchToggle.setAccount(userService.getAccount());
      switchService.update(switchToggle);
   }

   public void setSwitchService(SwitchService switchService) {
      this.switchService = switchService;
   }

   public void setUserService(UserServiceImpl userService) {
      this.userService = userService;
   }

   
}
