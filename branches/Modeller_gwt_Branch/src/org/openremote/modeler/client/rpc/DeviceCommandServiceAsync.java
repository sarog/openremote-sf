package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.DeviceCommand;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DeviceCommandServiceAsync {
   public void saveAll(List<DeviceCommand> deviceCommands,AsyncCallback<List<DeviceCommand>> callback);
}
