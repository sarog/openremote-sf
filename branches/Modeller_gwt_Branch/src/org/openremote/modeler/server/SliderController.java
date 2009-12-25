package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.SliderRPCService;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.service.impl.UserServiceImpl;

@SuppressWarnings("serial")
public class SliderController extends BaseGWTSpringControllerWithHibernateSupport implements SliderRPCService{

   private SliderService sliderService;
   
   private UserService userService;
   
   @Override
   public void delete(Slider slider) {
      sliderService.delete(slider);
   }

   @Override
   public List<Slider> loadAll() {
      return sliderService.loadAll();
   }

   @Override
   public void save(Slider slider) {
      slider.setAccount(userService.getAccount());
      sliderService.save(slider);
   }

   
   @Override
   public void update(Slider slider) {
      slider.setAccount(userService.getAccount());
      sliderService.update(slider);
   }

   public void setSliderService(SliderService switchService) {
      this.sliderService = switchService;
   }

   public void setUserService(UserServiceImpl userService) {
      this.userService = userService;
   }

   
}
