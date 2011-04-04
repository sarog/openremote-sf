/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway.protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.openremote.controller.gateway.EnumGatewayConnectionType;
import org.openremote.controller.gateway.EnumGatewayPollingMethod;

/**
 * 
 * @author Rich Turner 2011-02-11
 */
public abstract class Protocol implements ProtocolInterface
{   
   protected List<EnumGatewayConnectionType> supportedConnectionTypes = new ArrayList<EnumGatewayConnectionType>();
   
   protected List<EnumGatewayPollingMethod> supportedPollingMethods = new ArrayList<EnumGatewayPollingMethod>();

   /**
    *
    * THE FOLLOWING METHODS ARE HERE TO MAINTAIN GATEWAY COMPATIBILITY FOR
    * CONNECTIONLESS PROTOCOLS; THIS AVOIDS THE NEED FOR HAVING TO IMPLEMENT
    * THESE METHODS IF THEY ARE NOT RELEVANT. IF THE PROTOCOL IS CONNECTION
    * BASED THEN THESE METHODS SHOULD BE OVERRIDEN BY THE IMPLEMENTATION SUBCLASS
    *
    */
   
   /**
    * This method deals with opening up communication with the server
    */
   public void connect(int timeOut) throws Exception {
      
   }
   
   /**
    * Cleans up and closes the connection to the server
    */
   public void disconnect() throws Exception {

   }
   
   /* Clears server response buffer */
   public void clearBuffer() throws Exception {
      
   }

   public EnumGatewayPollingMethod checkSetPollingMethod(EnumGatewayPollingMethod pollingMethod) {
      EnumGatewayPollingMethod result = null;
      for (EnumGatewayPollingMethod connType : supportedPollingMethods) {
         if (connType.equals(pollingMethod)) {
            result = pollingMethod;
            break;
         }
      }
      if (result == null && supportedPollingMethods.size() == 1) {
         result = supportedPollingMethods.get(0);  
      }
      
      return result;
   }
   
   public EnumGatewayConnectionType checkSetConnectionType(EnumGatewayConnectionType connectionType) {
      EnumGatewayConnectionType result = null;
      for (EnumGatewayConnectionType connType : supportedConnectionTypes) {
         if (connType.equals(connectionType)) {
            result = connectionType;
            break;
         }
      }
      if (result == null && supportedConnectionTypes.size() == 1) {
         result = supportedConnectionTypes.get(0);  
      }
      
      return result;
   }
}