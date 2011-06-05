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
package org.openremote.web.console.server;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openremote.web.console.client.rpc.PanelIdentityRPCService;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.net.ORConnection;
import org.openremote.web.console.net.ORHttpMethod;
import org.openremote.web.console.service.PanelIdentityService;

/**
 * <code>PanelIdentityRPCService</code> implementation.
 */
public class PanelIdentityController extends BaseGWTSpringController implements PanelIdentityRPCService {

   private static final long serialVersionUID = -6124667768021371991L;
   
   private static Logger log = Logger.getLogger(PanelIdentityController.class);
   
   private PanelIdentityService panelIdentityService;
   
   public void setPanelIdentityService(PanelIdentityService panelIdentityService) {
      this.panelIdentityService = panelIdentityService;
   }

   public List<String> getPanelNames(String serverUrl, String username, String password) {
      return panelIdentityService.getPanelNames(serverUrl, username, password);
   }

   public PanelXmlEntity getPanelXmlEntity(String url, String username, String password) {
      return panelIdentityService.getPanelXmlEntity(url, username, password);
   }

   /**
    * Request panel names as jsonp data to check controller if is support JSON API, 
    */
   public boolean isSupportJsonp(String url, String username, String password) {
      boolean isSupportJsonp = false;
      url = url + "/rest/panels?callback=jsonp";
      ORConnection orConnection = new ORConnection(url, ORHttpMethod.GET, username, password, true);
      try {
         String result = IOUtils.toString(orConnection.getResponseData());
         if (result.startsWith("jsonp")) {
            isSupportJsonp = true;
         }
      } catch (IOException e) {
         log.error("Parse inputstream to string error.", e);
      }
      return isSupportJsonp;
   }
   
}
