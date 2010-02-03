package org.openremote.modeler.client.rpc;

import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Config;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("controllerConfig.smvc")
public interface ControllerConfigPRCService extends RemoteService{
   public Set<Config> saveAll(Set<Config> cfgs);
   public Set<Config> getConfigsByCategoryForCurrentAccount(String categoryName);
   public Set<Config> getConfigsByCategory(String categoryName,Account account);
   public Config update(Config config);
//   public Set<ConfigCategory> getCategories(); 
}
