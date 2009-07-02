package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Activity;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt.smvc")
public interface MyService extends RemoteService {
   public List<Activity> getString();
   
   public void addScreen();
}


