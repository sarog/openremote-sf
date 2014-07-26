/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.modeler.server;

import java.util.Map;

import org.openremote.modeler.client.rpc.OpenWebNetRPCService;
import org.openremote.modeler.openwebnet.OpenWebNetDefinition;
import org.openremote.modeler.openwebnet.OpenWebNetWho;
import org.openremote.modeler.service.OpenWebNetParser;

/**
 * The server side implementation of the RPC service <code>ProtocolRPCService</code>.
 */
public class OpenWebNetController extends BaseGWTSpringController implements OpenWebNetRPCService
{
   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -2388689684971606403L;

   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.OpenWebNetRPCService#getOWNDefinition()
    */
   public Map<String, OpenWebNetWho> getOWNDefinition()
   {
      if (OpenWebNetDefinition.getInstance().getWhos().size() == 0)
      {
         OpenWebNetParser parser = new OpenWebNetParser();
         OpenWebNetDefinition.getInstance().setWhos(parser.parseXml());
      }
      return OpenWebNetDefinition.getInstance().getWhos();
   }
}
