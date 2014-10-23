/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

package org.openremote.modeler.server.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openremote.modeler.protocol.ProtocolDefinition;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

/**
 * The Class is used for containing <b>ProtocolDefinition</b> of different protocol types.</br>
 * The container defined as a hash map, it use protocol displayname as the key, protocolDefinition 
 * as the value.</br></br>
 * 
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolContainer implements Serializable {
   
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -5478194408714473866L;
   
   /** The protocols. */
   private static Map<String, ProtocolDefinition> protocols = new HashMap<String, ProtocolDefinition>();
   
   /** The protocols as a list sorted by displayName. */
   private static ArrayList<ProtocolDefinition> protocolsList;

   public synchronized ArrayList<ProtocolDefinition> getProtocolsSortedByDisplayName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        ProtocolContainer.protocolsList = new ArrayList<ProtocolDefinition>();
        for (ProtocolDefinition protocolDefinition : ProtocolContainer.getInstance().getProtocols().values()) {
         if (protocolDefinition.getAllowedAccountIds() == null || protocolDefinition.getAllowedAccountIds().contains(name)) {
            ProtocolContainer.protocolsList.add(protocolDefinition);
         }
      }
       Collections.sort(ProtocolContainer.protocolsList, new Comparator<ProtocolDefinition>() {
         @Override
         public int compare(ProtocolDefinition protocol1, ProtocolDefinition protocol2) {
           return protocol1.getDisplayName().compareToIgnoreCase(protocol2.getDisplayName());
         }
       });
     return ProtocolContainer.protocolsList;
   }
   
   /**
    * Gets the protocols.
    * 
    * @return the protocols
    */
   public  Map<String, ProtocolDefinition> getProtocols() {
      return protocols;
   }

   /**
    * Sets the protocols.
    * 
    * @param protocols the protocols
    */
   public  void setProtocols(Map<String, ProtocolDefinition> protocols) {
      ProtocolContainer.protocols = protocols;
      ProtocolContainer.protocolsList = null;
   }


   /** The instance. */
   private static ProtocolContainer instance = new ProtocolContainer();
   
   /**
    * Instantiates a new protocol container.
    */
   private ProtocolContainer() {
     
   }

   /**
    * Gets the single instance of ProtocolContainer.
    * 
    * @return single instance of ProtocolContainer
    */
   public static synchronized ProtocolContainer getInstance() {
      return instance;
   }


   /**
    * Read protocol definitions.
    */
   public static void readProtocolDefinitions() {
      
   }

   /**
    * Find tag name.
    * 
    * @param protocolDisplayName the protocol display name
    * 
    * @return the string
    */
   public static String findTagName(String protocolDisplayName) {
      ProtocolDefinition protocolDefinition = protocols.get(protocolDisplayName);
      return protocolDefinition == null ? "" : protocolDefinition.getTagName();
   }
}
