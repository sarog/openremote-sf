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
package org.openremote.controller.service;

import org.jdom.Document;
import java.util.List;
import java.util.Arrays;
/**
 * This service is for generating the connection gateways that deal with command sending and receiving.<br /><br />
 * 
 * A seperate gateway is created for each unique hardware connection. The protocol class extends the gateway provinding<br />
 * a means of establishing the connection and providing an input and output stream for the sending and receiving of data<br />
 * the status update method(s) and the connection type determine the behaviour of the statusUpdater thread.<br /><br />
 * The statusUpdater is responsible for updating the status cache value of each sensor that resides under the gateway.<br />
 * 
 * @author Rich Turner 2011-02-09
 */
public interface GatewayManagerService {

   /* String constant for the top level gateway element connection type attribute: ("{@value}") */
   public String CONNECTION_ATTRIBUTE_NAME = "connectionType";
    
   /* String constant for the top level gateway element polling method attribute: ("{@value}") */
   public String POLLING_ATTRIBUTE_NAME = "pollingMethod";
   
   /**
    * A List of supported gateway Protocols, these protocols will be handled
    * by the gateway manager whereas others will go through the standard controller route
    */
   public List<String> supportedProtocols = Arrays.asList("telnet-gateway");

   /* Create Gateway instances from controller xml */
   public void initGatewaysWithControllerXML(Document document);
   
   /* Fire up the gateway threads */
   public void startGateways();

   /* Restart all the gateways */
   public void restartGateways();
   
   /* Stop all the gateway */
   public void stopGateways();

   public String getControllerXMLFileContent();

   public String getPanelXMLFileContent();
   
   public Boolean isProtocolSupported(String protocolType);
   
   public void trigger(String controlId, String controlAction, List<Integer> controlCommands);

   public List<Integer> getComponentCommandIds(String controlId, String controlAction);
}
