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

import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.service.ProtocolParser;
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
   private Map<String, ProtocolDefinition> protocols = new HashMap<String, ProtocolDefinition>();
   
   /** The protocols as a list sorted by displayName. */
   private ArrayList<ProtocolDefinition> protocolsList;

   public synchronized ArrayList<ProtocolDefinition> getProtocolsSortedByDisplayName() {
     // Cache list of protocols, ordered by display name (if the cache does not exist yet)
     if (protocolsList == null) {
       protocolsList = new ArrayList<ProtocolDefinition>();
       for (ProtocolDefinition protocolDefinition : protocols.values()) {
         protocolsList.add(protocolDefinition);
       }
       Collections.sort(protocolsList, new Comparator<ProtocolDefinition>() {
         @Override
         public int compare(ProtocolDefinition protocol1, ProtocolDefinition protocol2) {
           return protocol1.getDisplayName().compareToIgnoreCase(protocol2.getDisplayName());
         }
       });
     }
     
     // Filter out the cached list based on what current user is allowed to us and return that
     ArrayList<ProtocolDefinition> filteredProtocolsList =  new ArrayList<ProtocolDefinition>();
     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
     String name = auth.getName();
     for (ProtocolDefinition protocolDefinition : protocolsList) {
       if (protocolDefinition.getAllowedAccountIds() == null || protocolDefinition.getAllowedAccountIds().contains(name)) {
         filteredProtocolsList.add(protocolDefinition);
       }
     }
     return filteredProtocolsList;
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
   private void setProtocols(Map<String, ProtocolDefinition> protocols) {
      this.protocols = protocols;
      protocolsList = null;
   }
   
   /**
    * Instantiates a new protocol container.
    */
   public ProtocolContainer() {
     ProtocolParser parser = new ProtocolParser();
     setProtocols(parser.parseXmls());
   }

   /**
    * Find tag name.
    * 
    * @param protocolDisplayName the protocol display name
    * 
    * @return the string
    */
   public String findTagName(String protocolDisplayName) {
      ProtocolDefinition protocolDefinition = protocols.get(protocolDisplayName);
      return protocolDefinition == null ? "" : protocolDefinition.getTagName();
   }
}
