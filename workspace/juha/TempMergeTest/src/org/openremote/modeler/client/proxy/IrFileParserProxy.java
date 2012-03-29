/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.IRCommandInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * proxy for managing loaded file information
 * 
 */
public class IrFileParserProxy {

   /**
    * not to be instantiated
    */
   private IrFileParserProxy() {
   }

   /**
    * load Brands into brands for use in brands combo box
    * 
    * @param callback
    */
   public static void loadBrands(final AsyncCallback<ArrayList<BrandInfo>> callback) {

      AsyncServiceFactory.getiRFileParserRPCServiceAsync().getBrands(
            new AsyncSuccessCallback<ArrayList<BrandInfo>>() {
               @Override
               public void onSuccess(ArrayList<BrandInfo> result) {
                  callback.onSuccess(result);
               }
            });
   }

   /**
    * load devices from a brand into devices for use in devices combo box
    * 
    * @param brand
    * @param callback
    */
   public static void loadModels(BrandInfo brand,
         final AsyncCallback<ArrayList<DeviceInfo>> callback) {

      AsyncServiceFactory.getiRFileParserRPCServiceAsync().getDevices(brand,
            new AsyncSuccessCallback<ArrayList<DeviceInfo>>() {

               @Override
               public void onSuccess(ArrayList<DeviceInfo> result) {
                  callback.onSuccess(result);

               }
            });

   }

   /**
    * load codesets from a device into codesets for use in codesets combo box
    * 
    * @param device
    * @param callback
    */
   public static void loadCodeSets(DeviceInfo device,
         final AsyncCallback<ArrayList<CodeSetInfo>> callback) {
      AsyncServiceFactory.getiRFileParserRPCServiceAsync().getCodeSets(device,
            new AsyncSuccessCallback<ArrayList<CodeSetInfo>>() {

               @Override
               public void onSuccess(ArrayList<CodeSetInfo> result) {
                  callback.onSuccess(result);
               }
            });
   }

   /**
    * load IR commands from a codeset into IRCommands for use in the form grid.
    * 
    * @param codeSet
    * @param callback
    */
   public static void loadIRCommands(CodeSetInfo codeSet,
         final AsyncCallback<ArrayList<IRCommandInfo>> callback) {

      AsyncServiceFactory.getiRFileParserRPCServiceAsync().getIRCommands(
            codeSet, new AsyncSuccessCallback<ArrayList<IRCommandInfo>>() {

               @Override
               public void onSuccess(ArrayList<IRCommandInfo> result) {
                  callback.onSuccess(result);
               }
            });
   }

}
