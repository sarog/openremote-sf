package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.client.rpc.DeviceCommandService;
import org.openremote.modeler.domain.DeviceCommand;

public class DeviceCommandServiceImpl extends BaseAbstractService<DeviceCommand> implements DeviceCommandService {

   public List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands) {
      for (DeviceCommand command : deviceCommands) {
         // genericDAO.save(command.getProtocol());
         // for (ProtocolAttr attr: command.getProtocol().getAttributes()) {
         // genericDAO.save(attr);
         // }
         genericDAO.save(command);
        
         // genericDAO.save(command.getDevice());
      }
      return deviceCommands;
   }

}
