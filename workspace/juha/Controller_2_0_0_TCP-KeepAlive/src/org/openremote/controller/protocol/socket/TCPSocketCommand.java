/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.socket;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.utils.Logger;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Marcus Redeker 2009-4-26
 * @author Phillip Lavender
 * @author Ivan Martinez
 */
public class TCPSocketCommand extends ReadCommand implements ExecutableCommand
{

  // Class Members --------------------------------------------------------------------------------

  private static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "tcp");



  // Instance Fields ------------------------------------------------------------------------------

  private String name;
  private TCPSocketCommandBuilder.TCPConnection connection;
  private Set<Message> messages;
  private Boolean waitForResponse;


  // Constructors ---------------------------------------------------------------------------------

  TCPSocketCommand(String name, Set<Message> messages, TCPSocketCommandBuilder.TCPConnection connection,
                   Boolean waitForResponse)
  {
    log.info("Created TCP command ''{0}'' for connection {1} ",name, connection);

    this.connection = connection;
    this.messages = messages;
    this.name = name;
    this.waitForResponse = waitForResponse;
  }



  // Implements ExecutableCommand -----------------------------------------------------------------

  @Override public void send()
  {
    if (waitForResponse)
    {
      sendAndReceive();
    }

    else
    {
      sendOnly();
    }
  }

  // Implements ReadCommand -----------------------------------------------------------------------

  @Override public String read(Sensor s)
  {
    String rawResult = sendAndReceive();

    // Strip response from control characters (non-ASCII)...

    Pattern p = Pattern.compile("\\p{Cntrl}");
    Matcher m = p.matcher(rawResult);
    String regexResult = m.replaceAll("");

    if ("".equals(regexResult))
    {
       return Sensor.UNKNOWN_STATUS;
    }

    return regexResult;
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void sendOnly()
  {
    try
    {
      for (Message m : messages)
      {
        log.info("Sent " + m);

        connection.send(m);
      }
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private String sendAndReceive()
  {
    StringBuffer sbuf = new StringBuffer();

    try
    {
      for (Message m : messages)
      {
        log.info("Sent " + m);

        connection.send(m);

        Message response = connection.receive();

        sbuf.append(new String(response.getContent()));
      }
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }

    return sbuf.toString();
  }


}
