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
package org.openremote.controller.protocol.ip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;
import java.nio.ByteBuffer;

import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.DeviceProtocol;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.bus.PhysicalBus;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class IPConnection<T extends IPConnection> extends DeviceProtocol<T> implements PhysicalBus
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Options for IP based connections.
   */
  public enum Option
  {
    RESPONSE_READ_UNTIL_CLOSE,
    RESPONSE_READ_AVAILABLE,
    RESPONSE_READ_UNTIL_CR,
    RESPONSE_READ_UNTIL_CRLF,
    RESPONSE_READ_UNTIL_LF,
    RESPONSE_READ_NOTHING,

    CONNECTION_KEEPALIVE,
    CONNECTION_OWNED_KEEPALIVE,
    CONNECTION_OWNED_RESPONSE_POLICY
  }



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * This is the response read behavior on all sent messages -- the connection will wait to
   * consume a response from the server (based on response read policy).
   */
  private Option responsePolicy = Option.RESPONSE_READ_AVAILABLE;   // the default read.available() is unfortunate as it is unreliable but maintains backwards compatibility

  /**
   * Connection keep-alive setting. Defaults to false to maintain backwards compatibility.
   */
  private boolean keepAlive = false;

  /**
   * Indicates whether the connection owns its keep-alive setting for the lifetime of the connection
   * or if individual messages can change and control this connection setting on an in-use
   * connection.
   */
  private boolean connectionOwnedKeepAlive = false;

  /**
   * Indicates whether the connection owns its response read policy for the lifetime of the
   * connection or if individual messages can change and control this connection setting on
   * an in-use connection.
   */
  private boolean connectionOwnedResponsePolicy = false;



  /*
   *  The raw connection streams. Stream filters should be set by subclasses/clients.
   */
  private InputStream input;
  private OutputStream output;

  /**
   * The connection identifier.
   */
  private InetSocketAddress connectionID;



  // Constructors ---------------------------------------------------------------------------------

  public IPConnection(Collection<IPConnection.Option> ipOptions,
                      Collection<DeviceProtocol.Option> protocolOptions)
  {

    super(protocolOptions.toArray(new DeviceProtocol.Option[protocolOptions.size()]));

    for (IPConnection.Option option : ipOptions)
    {
      switch (option)
      {
        case RESPONSE_READ_AVAILABLE:
        case RESPONSE_READ_NOTHING:
        case RESPONSE_READ_UNTIL_CLOSE:
        case RESPONSE_READ_UNTIL_CR:
        case RESPONSE_READ_UNTIL_CRLF:
        case RESPONSE_READ_UNTIL_LF:

          responsePolicy = option;
          break;

        case CONNECTION_OWNED_RESPONSE_POLICY:

          connectionOwnedResponsePolicy = true;
          break;

        case CONNECTION_OWNED_KEEPALIVE:

          connectionOwnedKeepAlive = true;

        case CONNECTION_KEEPALIVE:

          keepAlive = true;
          break;


      }
    }

  }


  // Implements DeviceProtocol --------------------------------------------------------------------

  @Override public Command create(Properties properties, Set<Message> msgs)
  {
    parseKeepAlive(properties.getOptionalBoolean("keepAlive"));
    parseResponsePolicy(properties.getOptionalProperty("responsePolicy"));

    return createIPCommand(properties, msgs, responsePolicy);
  }

  @Override protected void closeConnection() throws IOException
  {
    output.close();
  }


  // Implements PhysicalBus -----------------------------------------------------------------------


  @Override public void stop()
  {
    // closeConnection Impl should be here...
  }


  @Override public void send(Message m) throws IOException
  {
    connectionLog.info("Connection ID ''{0}'' sending ''{1}''", formatConnection(), formatMessage(m));

    byte[] payload = m.getContent();

    output.write(payload);

    output.flush();

    if (responsePolicy == Option.RESPONSE_READ_NOTHING && !keepAlive)
    {
      removeConnection(connectionID);
    }
  }


  @Override public Message receive() throws IOException
  {
    switch (responsePolicy)
    {
      case RESPONSE_READ_NOTHING:

        return new Message(new byte[] {});

      case RESPONSE_READ_AVAILABLE:

        // the default (unfortunately)

        return readAvailable();

      case RESPONSE_READ_UNTIL_CLOSE:

        return readUntilClose();

      default:
          throw new Error("RESPONSE HANDLING NOT IMPLEMENTED");
    }
  }


  // Object Overrides -----------------------------------------------------------------------------

  @Override public String toString()
  {
    StringBuffer sbuf = new StringBuffer(100);

    sbuf.append(connectionID);
    sbuf.append(" (keep-alive = ");
    sbuf.append(keepAlive);
    sbuf.append(" [");

    if (connectionOwnedKeepAlive)
    {
      sbuf.append("LOCKED] ");
    }

    else
    {
      sbuf.append("Modifiable] ");
    }

    sbuf.append("response-policy = ");
    sbuf.append(responsePolicy);

    sbuf.append(" [");

    if (connectionOwnedResponsePolicy)
    {
      sbuf.append("LOCKED])");
    }

    else
    {
      sbuf.append("Modifiable])");
    }

    return sbuf.toString();
  }



  // Abstract Implementations ---------------------------------------------------------------------

  // could migrate away from command here (via adapter)
  protected abstract Command createIPCommand(Properties properties, Set<Message> msgs,
                                             Option responsePolicy);




  // Protected Methods ----------------------------------------------------------------------------

  protected void startConnection(InetSocketAddress socketAddress, InputStream in, OutputStream out)
  {
    this.input = in;
    this.output = out;
    this.connectionID = socketAddress;
  }



  // Private Methods ------------------------------------------------------------------------------

  private String formatMessage(Message m)
  {
    byte[] content = m.getContent();

    return formatMessage(content, content.length);
  }

  private String formatMessage(byte[] content, int len)
  {
    StringBuffer sbuf = new StringBuffer(128);

    for (int i = 0; i < len; ++i)
    {
      if (Character.isISOControl(content[i]))
      {
        if (content[i] == 13)
        {
          sbuf.append("\\r");
        }

        else
        {
          sbuf.append(" [");
          sbuf.append(((int)content[i]) & 0xF);
          sbuf.append("]");
        }
      }

      else
      {
        sbuf.append((char)content[i]);
      }
    }

    return sbuf.toString();
  }


  private void parseResponsePolicy(String policyValue)
  {
    if (connectionOwnedResponsePolicy)
    {
      return;
    }

    if (policyValue != null && !policyValue.equals(""))
    {
      policyValue = "RESPONSE_" + policyValue.trim().toUpperCase();
      policyValue = policyValue.replace(' ', '_');

      responsePolicy = Option.valueOf(policyValue);
    }
  }


  private void parseKeepAlive(Boolean b)
  {
    if (connectionOwnedKeepAlive)
    {
      return;
    }

    if (b != null)
    {
      keepAlive = b;
    }

  }


  private String formatConnection()
  {
    StringBuffer sbuf = new StringBuffer();

    sbuf.append(connectionID);
    sbuf.append(" (keep-alive = ");
    sbuf.append(keepAlive);
    sbuf.append(", response-policy = ");
    sbuf.append(responsePolicy);
    sbuf.append(")");

    return sbuf.toString();
  }




  private Message readAvailable() throws IOException
  {
    connectionLog.info("Reading all available bytes from ''{0}''", formatConnection());
    
    byte[] buffer = new byte[200];    // default buffer size (too low)

    int len = input.read(buffer, 0, buffer.length);

    if (len > 0)
    {
      byte[] content = new byte[len];
      System.arraycopy(buffer, 0, content, 0, len);


      Message m = new Message(content);

      connectionLog.info("Read {0}", formatMessage(m));

      return m;
    }

    else
    {
      connectionLog.info("Nothing read, stream was empty.");

      return new Message(new byte[] {});
    }
  }

  private Message readUntilClose() throws IOException
  {
    connectionLog.info("Reading connection until it will close (''{0}'')...", formatConnection());

    int len = 0;
    byte[] msgbuffer = new byte[1024];
    int pos = 0;

    while ( true )
    {
      byte[] buffer = new byte[1024];

      if (msgbuffer.length < pos + 1024)
      {
        byte[] resizeBuffer = new byte[msgbuffer.length + 1024];

        System.arraycopy(msgbuffer, 0, resizeBuffer, 0, pos);
        
        msgbuffer = resizeBuffer;
      }

      len = input.read(buffer, 0, buffer.length);

      if (len == -1)
        break;

      connectionLog.info("Received ''{0}'', continuing to read...", formatMessage(buffer, len));

      System.arraycopy(buffer, 0, msgbuffer, pos, len);
      pos += len;
    }

    // read until closed means we get rid of the connection at the end..

    removeConnection(connectionID);

    byte[] content = new byte[pos];
    System.arraycopy(msgbuffer, 0, content, 0, pos);
    
    Message msg = new Message(content);

    connectionLog.info("Read complete, complete message: ''{0}''", formatMessage(msg));

    return msg;
  }

}



