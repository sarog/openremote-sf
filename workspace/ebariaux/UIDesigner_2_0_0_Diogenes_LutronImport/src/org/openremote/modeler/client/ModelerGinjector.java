package org.openremote.modeler.client;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.dispatch.client.gin.StandardDispatchModule;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(StandardDispatchModule.class)
public interface ModelerGinjector extends Ginjector {

  public DispatchAsync getDispatchAsync();

}
