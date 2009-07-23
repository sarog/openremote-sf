/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolContainer implements Serializable{
   
   private static final long serialVersionUID = -5478194408714473866L;
   
   private static Map<String,ProtocolDefinition> protocols = new HashMap<String, ProtocolDefinition>();

   public  Map<String, ProtocolDefinition> getProtocols() {
      return protocols;
   }

   public  void setProtocols(Map<String, ProtocolDefinition> protocols) {
      ProtocolContainer.protocols = protocols;
   }


   private static ProtocolContainer instance = new ProtocolContainer();
   private ProtocolContainer() {
     
   }

   public static synchronized ProtocolContainer getInstance() {
      return instance;
   }


   public static void readProtocolDefinitions() {
      
   }


   public ProtocolDefinition get(String name) {
      if (protocols.containsKey(name)) {
         return protocols.get(name);
      }
      return null;
   }
   
   

}
