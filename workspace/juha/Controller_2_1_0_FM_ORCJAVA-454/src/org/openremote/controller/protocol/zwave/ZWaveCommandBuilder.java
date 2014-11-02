/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.zwave;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.service.Deployer;

/**
 * This is a dummy placeholder for a Z-Wave implementation.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ZWaveCommandBuilder implements CommandBuilder
{

  private static boolean hasAnnounced = false;

  public ZWaveCommandBuilder(Deployer d)
  {

    if (!hasAnnounced && System.getProperty("zwave.silent") == null)
    {
      System.out.println(
          "\n\n" +
          "*****************************************************************\n\n" +
          "  The license restrictions Sigma Designs places on Z-Wave \n" +
          "  implementations prevents us from distributing Z-Wave with  \n" +
          "  an Open Source product. \n\n" +
          "" +
          "  To test Z-Wave, download a freely available binary from \n" +
          "  http://download.openremote.org/free/zwave and follow the \n" +
          "  installation instructions at http://www.openremote.org/x/xAo4AQ \n\n" +
          "" +
          "*****************************************************************\n\n"
      );

      hasAnnounced = true;
    }
  }

  @Override public Command build(Element el)
  {
    // no-op

    return null;
  }

}

