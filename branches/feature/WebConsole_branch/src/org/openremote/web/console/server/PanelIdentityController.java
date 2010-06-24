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

import java.util.List;

import org.openremote.web.console.client.rpc.PanelIdentityRPCService;
import org.openremote.web.console.service.PanelIdentityService;

/**
 * The Class PanelIdentityController is for get panel list from controller.
 */
public class PanelIdentityController extends BaseGWTSpringController implements PanelIdentityRPCService {

   private static final long serialVersionUID = -6124667768021371991L;

   private PanelIdentityService panelIdentityService;
   
   public void setPanelIdentityService(PanelIdentityService panelIdentityService) {
      this.panelIdentityService = panelIdentityService;
   }

   /* (non-Javadoc)
    * @see org.openremote.web.console.client.rpc.PanelIdentityRPCService#getPanels(java.lang.String, java.lang.String, java.lang.String)
    */
   public List<String> getPanels(String serverUrl, String username, String password) {
      return panelIdentityService.getPanels(serverUrl, username, password);
   }

}
