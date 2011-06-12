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

import org.openremote.web.console.client.rpc.CommandRPCService;
import org.openremote.web.console.net.ORConnection;
import org.openremote.web.console.net.ORHttpMethod;
import org.openremote.web.console.utils.StringUtil;

/**
 * <code>CommandRPCService</code> implementation.
 */
public class CommandController extends BaseGWTSpringController implements CommandRPCService {

   private static final long serialVersionUID = 2596589916062609326L;

   /**
    * If url is not empty, send command to controller
    */
   public void sendCommand(String url, String username, String password) {
      if (!StringUtil.isEmpty(url)) {
         new ORConnection(url, ORHttpMethod.POST, username, password);
      }
   }

}
