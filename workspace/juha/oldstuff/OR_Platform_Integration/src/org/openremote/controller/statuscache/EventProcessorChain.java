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
package org.openremote.controller.statuscache;

import java.util.List;
import java.util.ArrayList;

import org.openremote.controller.protocol.Event;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class EventProcessorChain
{

  // Private Instance Fields ----------------------------------------------------------------------

  private List<EventProcessor> processors = new ArrayList<EventProcessor>(5);

  private boolean hasInit = false; // TODO : hack


  // Public Instance Methods ----------------------------------------------------------------------

  public void push(Event event)
  {
    if (!hasInit)
    {
      initProcessors();

      hasInit = true;
    }

    for (EventProcessor processor : processors)
    {
      event = processor.push(event);
    }
  }

  // Service Dependencies -------------------------------------------------------------------------

  public void setEventProcessors(List<EventProcessor> processors)
  {
    this.processors = processors;

  }

  //

  private void initProcessors()
  {
    for (EventProcessor ep : processors)
    {
      try
      {
        ep.init();
      }
      catch (Throwable t)
      {
        System.out.println("ERROR initializing event processor : " + t); // TODO
        t.printStackTrace();
      }
    }
  }
}

