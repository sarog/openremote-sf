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
package org.openremote.controller.protocol.meta;

import java.util.List;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.service.ControlCommandService;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class MetaCommand implements ExecutableCommand
{

  private String command;
  private String plcID;
  private String knxID;
  private ControlCommandService ccs;


  public MetaCommand(ControlCommandService ccs, String cmd, String plcID, String knxID)
  {
    this.command = cmd;
    this.plcID = plcID;
    this.knxID = knxID;
    this.ccs = ccs;
  }


  public void send()
  {
    if (command.equalsIgnoreCase("next"))
    {
      int index = MetaCommandBuilder.shiftAndReturnCurrentIndex(true);

      if (index == 0)
      {
        ccs.trigger(knxID, "click");
      }

      else
      {
        ccs.trigger(plcID, "click");
      }
    }

    else if (command.equalsIgnoreCase("prev"))
    {
      int index = MetaCommandBuilder.shiftAndReturnCurrentIndex(false);

      if (index == 0)
      {
        ccs.trigger(knxID, "click");
      }

      else
      {
        ccs.trigger(plcID, "click");
      }
    }

    else if (command.equalsIgnoreCase("on"))
    {
      if (MetaCommandBuilder.getCurrentIndex() == 0)
      {
        ccs.trigger(knxID, "click");
      }
      else
      {
        ccs.trigger(plcID, "click");
      }
    }

    else if (command.equalsIgnoreCase("off"))
    {
      if (MetaCommandBuilder.getCurrentIndex() == 0)
      {
        ccs.trigger(knxID, "click");
      }
      else
      {
        ccs.trigger(plcID, "click");
      }

    }
  }


}

