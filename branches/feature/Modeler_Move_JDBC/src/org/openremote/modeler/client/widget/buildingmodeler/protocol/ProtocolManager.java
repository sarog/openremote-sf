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
package org.openremote.modeler.client.widget.buildingmodeler.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolRPCService;
import org.openremote.modeler.client.rpc.ProtocolRPCServiceAsync;
import org.openremote.modeler.protocol.ProtocolDefinition;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Utility class for managing protocols.
 * Includes xml defined protocols and UI hard coded protocols.
 */
public class ProtocolManager {

   private static ProtocolManager instance;
   
   /** All protocol display names. */
   private static List<String> protocolNames = new ArrayList<String>();
   
   /** Map for storing the xml defined protocols. The protocol's displayName is key. */
   private static Map<String, ProtocolDefinition> xmlProtocolMaps;
   
   /** Map for storing the UI hard coded protocols. The protocol's displayName is key. */
   private static Map<String, AbstractProtocolFieldSet> uiProtocolMaps = new HashMap<String, AbstractProtocolFieldSet>();
   
   private static final ProtocolRPCServiceAsync protocolService = (ProtocolRPCServiceAsync) GWT.create(ProtocolRPCService.class);
   
   /** Map for storing the UI hard coded protocol name and type. 
    *  The name is key and type is value.
    * */
   private Map<String, String> uiProtocolNameTypes = new HashMap<String, String>();
   
   private ProtocolManager() {
      protocolService.getProtocols(new AsyncCallback<Map<String, ProtocolDefinition>>() {
         public void onFailure(Throwable caught) {
            MessageBox.info("Error", "Can't get protocols from xml file!", null);
         }
         public void onSuccess(Map<String, ProtocolDefinition> protocols) {
            xmlProtocolMaps = protocols;
         }
      });
      
      registerUIProtocols();
      
      if (uiProtocolNameTypes.size() > 0) {
         // Save the UI hard coded protocol name and type to server.
         protocolService.saveUIProtocolNameAndValues(uiProtocolNameTypes, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
               MessageBox.info("Error", "Can't save UI protocols to server!", null);
            }
            public void onSuccess(Void result) {
               // donothing.
            }
         });
      }
      
   }
   
   /**
    * Register all UI hard coded protocols.
    * Each UI hard coded protocol must be registered here.
    */
   private void registerUIProtocols() {
      registerUIProtocol(new MockProtocolFieldSet());
   }
   
   private void registerUIProtocol(AbstractProtocolFieldSet protocolFieldSet) {
      uiProtocolMaps.put(protocolFieldSet.getProtocolDisplayName(), protocolFieldSet);
      uiProtocolNameTypes.put(protocolFieldSet.getProtocolDisplayName(), protocolFieldSet.getProtocolType());
   }
   
   public static synchronized ProtocolManager getInstance() {
      if (instance == null) {
         instance = new ProtocolManager();
      }
      return instance;
   }
   
   /**
    * Returns all protocol displayNames.
    */
   public List<String> getProtocolNames() {
      if (protocolNames.size() == 0) {
         if (xmlProtocolMaps != null) {
            for (String protocolDisplayName : xmlProtocolMaps.keySet()) {
               protocolNames.add(protocolDisplayName);
            }
         }
         if (uiProtocolMaps.size() > 0) {
            for (String protocolDisplayName : uiProtocolMaps.keySet()) {
               if (!protocolNames.contains(protocolDisplayName)) {
                  protocolNames.add(protocolDisplayName);
               }
            }
         }
      }
      
      return protocolNames;
   }
   
   public ProtocolDefinition getXmlProtocol(String protocolDisplayName) {
      return xmlProtocolMaps.get(protocolDisplayName);
   }
   
   public AbstractProtocolFieldSet getUIProtocol(String protocolDisplayName) {
      return uiProtocolMaps.get(protocolDisplayName);
   }
}
