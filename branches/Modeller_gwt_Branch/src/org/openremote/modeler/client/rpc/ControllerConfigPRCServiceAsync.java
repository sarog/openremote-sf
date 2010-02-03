package org.openremote.modeler.client.rpc;

import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Config;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ControllerConfigPRCServiceAsync {
   public void saveAll(Set<Config> configs,AsyncCallback<Set<Config>>callback);
   
   public void getConfigsByCategoryForCurrentAccount(String categoryName,AsyncCallback<Set<Config>>callback);
   
   public void getConfigsByCategory(String categoryName,Account accouont,AsyncCallback<Set<Config>>callback);
   
   public void update(Config config,AsyncCallback<Config> callback);
   
//   public void getCategories(AsyncCallback<Set<ConfigCategory>> callback);
}
