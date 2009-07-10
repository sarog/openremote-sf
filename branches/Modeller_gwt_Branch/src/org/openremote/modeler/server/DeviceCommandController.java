package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.DeviceCommandService;
import org.openremote.modeler.domain.DeviceCommand;

public class DeviceCommandController extends BaseGWTSpringControllerWithHibernateSupport implements
      DeviceCommandService {

   private DeviceCommandService deviceCommandService;

   public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
      this.deviceCommandService = deviceCommandService;
   }

   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      return deviceCommandService.saveAll(deviceCommands);
   }

}
