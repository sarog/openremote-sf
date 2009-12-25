package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Switch;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("switch.smvc")
public interface SwitchRPCService extends RemoteService {
   public List<Switch> loadAll();
   void save(Switch switchToggle);
   void update(Switch switchToggle);
   void delete(Switch switchToggle);
}
