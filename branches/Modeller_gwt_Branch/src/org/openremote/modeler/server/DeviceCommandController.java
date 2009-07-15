package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.DeviceCommandService;
import org.openremote.modeler.domain.DeviceCommand;

public class DeviceCommandController extends BaseGWTSpringControllerWithHibernateSupport implements
      DeviceCommandService {

   /**
    * 
    */
   private static final long serialVersionUID = -8417889117208060088L;
   private DeviceCommandService deviceCommandService;

   public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
      this.deviceCommandService = deviceCommandService;
   }

   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      return deviceCommandService.saveAll(deviceCommands);
   }

   public DeviceCommand save(DeviceCommand deviceCommand) {
      return deviceCommandService.save(deviceCommand);
   }

   public void removeCommand(DeviceCommand deviceCommand) {
      deviceCommandService.removeCommand(deviceCommand);
   }

   public void update(DeviceCommand deviceCommand) {
      deviceCommandService.update(deviceCommand);
   }

}
