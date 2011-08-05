/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.proxy;



import java.util.Set;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class is for managing contoller configurations.
 */
public class ControllerConfigBeanModelProxy {
   
   public static void getConfigs(final ConfigCategory category, final AsyncCallback<Set<ControllerConfig>> callback) {
      AsyncServiceFactory.getControllerConfigPRCServiceAsync().getConfigsByCategoryForCurrentAccount(
            category.getName(), new AsyncSuccessCallback<Set<ControllerConfig>>() {

               @Override
               public void onSuccess(Set<ControllerConfig> result) {
                  callback.onSuccess(result);
               }

            });
   }
   
   public static void saveAllConfigs(final Set<ControllerConfig> configs,
         final AsyncCallback<Set<ControllerConfig>> callback) {
      AsyncServiceFactory.getControllerConfigPRCServiceAsync().saveAll(configs,
            new AsyncSuccessCallback<Set<ControllerConfig>>() {

               @Override
               public void onSuccess(Set<ControllerConfig> result) {
                  callback.onSuccess(result);
               }

               @Override
               public void onFailure(Throwable caught) {
                  callback.onFailure(caught);
               }

            });
   }
   
   public static void listAllMissingConfigs(final String categoryName,
         final AsyncCallback<Set<ControllerConfig>> callback) {
      AsyncServiceFactory.getControllerConfigPRCServiceAsync().listAllMissedConfigsByCategoryName(categoryName,
            new AsyncCallback<Set<ControllerConfig>>() {

               @Override
               public void onFailure(Throwable caught) {
                  callback.onFailure(caught);
               }

               @Override
               public void onSuccess(Set<ControllerConfig> result) {
                  callback.onSuccess(result);
               }

            });
   }
}
