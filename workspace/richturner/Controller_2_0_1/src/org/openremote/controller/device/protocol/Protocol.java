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
package org.openremote.controller.device.protocol;
/**
 * General Protocol interface that all protocols implement
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public interface Protocol {
   /**
    * Return a URI formatted string representing the unique address of the device
    * e.g. udp://192.168.1.1:1234/ or http://mydevice.com/
    * 
    * @return Unique Protocol URL String
    */
   String getProtocolURL();
   
   /**
    * Called by the Gateway when it is first created
    */
   void onStart();
   
   /**
    * Called by the Gateway when it is being destroyed, should be used
    * to clean up any resources that the protocol is using and to gracefully
    * close the connection to the device if it is open
    */
   void onStop();
   
   /**
    * Indicates the current status of the protocol, if the protocol is un-usable
    * then it's status should be set to ERROR and the gateway will then ignore it
    * 
    * @return The current status of this protocol
    */
   ProtocolStatus getStatus();

   /**
    * Get the parameters for this protocol
    * 
    * @return ProtocolParameters
    */
   public ProtocolParameters getParameters();

   
   /**
    * Get an array of required parameter names; these must exist in the protocol
    * parameters.
    * 
    * @return Array of required parameter names
    */
   public String[] getRequiredParameterNames();
}
