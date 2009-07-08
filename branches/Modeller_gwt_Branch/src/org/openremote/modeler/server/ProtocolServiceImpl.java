/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.server;

import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolService;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.service.ProtocolParser;

/**
 * The Class ProtocolServiceImpl.
 */
public class ProtocolServiceImpl extends BaseGWTSpringController implements ProtocolService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 8057648010410493998L;

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.ProtocolService#getProtocolContainer()
    */
   public Map<String, ProtocolDefinition> getProtocols() {
      if (ProtocolContainer.getInstance().getProtocols().size() == 0) {
         ProtocolParser parser = new ProtocolParser();
         ProtocolContainer.getInstance().setProtocols(parser.parseXmls());
      }
      return ProtocolContainer.getInstance().getProtocols();
   }

}
