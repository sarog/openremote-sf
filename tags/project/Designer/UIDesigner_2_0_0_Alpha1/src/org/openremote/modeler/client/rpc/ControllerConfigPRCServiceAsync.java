/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.rpc;

import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Config;

import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * 
 * @author javen
 *
 */
public interface ControllerConfigPRCServiceAsync {
   public void saveAll(Set<Config> configs,AsyncCallback<Set<Config>>callback);
   
   public void getConfigsByCategoryForCurrentAccount(String categoryName,AsyncCallback<Set<Config>>callback);
   
   public void getConfigsByCategory(String categoryName,Account accouont,AsyncCallback<Set<Config>>callback);
   
   public void update(Config config,AsyncCallback<Config> callback);
   
//   public void getCategories(AsyncCallback<Set<ConfigCategory>> callback);
}
