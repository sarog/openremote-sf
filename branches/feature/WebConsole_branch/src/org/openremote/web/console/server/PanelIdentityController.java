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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.web.console.client.rpc.PanelIdentityRPCService;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.service.PanelIdentityService;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Class PanelIdentityController is for get panel list from controller.
 */
public class PanelIdentityController extends BaseGWTSpringController implements PanelIdentityRPCService {

   private static final long serialVersionUID = -6124667768021371991L;

   private PanelIdentityService panelIdentityService;
   
   public void setPanelIdentityService(PanelIdentityService panelIdentityService) {
      this.panelIdentityService = panelIdentityService;
   }

   /**
    * {@inheritDoc}
    */
   public List<String> getPanelNames(String serverUrl, String username, String password) {
      return panelIdentityService.getPanelNames(serverUrl, username, password);
   }

   @Override
   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
      ModelAndView mav = super.handleRequest(request, response);
//      if ( mav != null) {
//         mav.setViewName("home");
//      }
      
      return mav;
   }

   public PanelXmlEntity getPanelXmlEntity(String url, String username, String password) {
      return panelIdentityService.getPanelXmlEntity(url, username, password);
   }
   
}
