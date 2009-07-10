package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("deviceCommand.smvc")
public interface DeviceCommandService extends RemoteService{
   List<DeviceCommand> saveAll(List<DeviceCommand> deviceCommands);
}
