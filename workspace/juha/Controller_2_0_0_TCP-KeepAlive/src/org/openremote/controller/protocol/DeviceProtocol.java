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
package org.openremote.controller.protocol;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Arrays;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.io.IOException;

import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.bus.PhysicalBus;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class DeviceProtocol<T extends PhysicalBus> implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  public final static String PIPED_COMMAND_SEPARATOR = "|";


  // Enums ----------------------------------------------------------------------------------------

  protected enum Option
  {
    ENABLE_PIPED_MESSAGES,
    ENABLE_HEX_STRING_MESSAGES,
    INCLUDE_CR_AT_END_OF_MESSAGE,
    INCLUDE_LF_AT_END_OF_MESSAGE

  }



  // Instance Fields ------------------------------------------------------------------------------

  private Set<Option> options = new HashSet<Option>(1);

  protected Logger connectionLog =
      Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "connection");

  private Map<InetSocketAddress, T> connections = new HashMap<InetSocketAddress, T>();

  private final Object CONNECTIONS_MUTEX = new Object();



  // Constructors ---------------------------------------------------------------------------------

  protected DeviceProtocol(Option... opts)
  {
    this.options.addAll(Arrays.asList(opts));

    connectionLog.info("Created device protocol implementation with options = ({0})", formatOptions(opts));
  }



  // Protected Instance Methods -------------------------------------------------------------------


  /**
   * TODO
   *
   * This implementation manages a generic pool of connections.
   */
  protected T getConnection(InetSocketAddress socketAddress)
  {
    synchronized (CONNECTIONS_MUTEX)
    {
      if (connections.keySet().contains(socketAddress))
      {
        return connections.get(socketAddress);
      }

      else
      {
        T connection = createConnection();

        connections.put(socketAddress, connection);

        connection.start(socketAddress, socketAddress);

        return connection;
      }
    }
  }

  protected void removeConnection(InetSocketAddress socketAddress)
  {
    synchronized (CONNECTIONS_MUTEX)
    {
      try
      {
        closeConnection();
      }

      catch (IOException e)
      {
        e.printStackTrace();
      }

      connections.remove(socketAddress);

      connectionLog.info("Connection ID ''{0}'' removed.", socketAddress);
    }
  }



  protected abstract Command create(Properties properties, Set<Message> messages);

  protected abstract T createConnection();

  protected abstract void closeConnection() throws IOException;




  // Implements CommandBuilder --------------------------------------------------------------------

  @Override public Command build(Element element)
  {
    List<Element> propertyElements = getPropertyElements(element);
    Properties protocolProperties = new Properties();
    
    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);


      protocolProperties.add(propertyName, propertyValue);
    }

    Set<Message> messages = new HashSet<Message>(1);

    String unparsedPayload = protocolProperties.getMandatoryProperty("command");
    CommandUtil.parseStringWithParam(element, unparsedPayload);

    if (options.contains(Option.ENABLE_PIPED_MESSAGES))
    {
      messages = parseMessages(unparsedPayload, options);
    }

    else
    {
      messages.add(new Message(unparsedPayload.getBytes()));
    }

    return create(protocolProperties, messages);

  }



  // Private Instance Methods ---------------------------------------------------------------------


  private Set<Message> parseMessages(String payload, Set<Option> options)
  {
    StringTokenizer st = new StringTokenizer(payload, PIPED_COMMAND_SEPARATOR);

    Set<Message> msgs = new HashSet<Message>();


    while (st.hasMoreElements())
    {
      msgs.add(new ByteArrayMessage(st.nextToken(), options));
    }

    return msgs;
  }


  @SuppressWarnings("unchecked")
  private List<Element> getPropertyElements(Element element)
  {
    return element.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY, element.getNamespace());
  }

  private String formatOptions(Option... opts)
  {
    StringBuffer sbuf = new StringBuffer(10);

    if (opts.length <= 1)
    {
      return opts[0].toString();
    }

    else
    {
      sbuf.append(opts[0]);

      for (int i = 1; i < opts.length; i++)
      {
        sbuf.append(", ");
        sbuf.append(opts[i]);
      }
    }

    return sbuf.toString();
  }


  // Nested Classes -------------------------------------------------------------------------------


  private static class ByteArrayMessage extends Message
  {
    private static byte[] parse(String unparsedPayload, Set<Option> options)
    {
      byte[] bytes;

      if (options.contains(Option.ENABLE_HEX_STRING_MESSAGES) && unparsedPayload.startsWith("0x"))
      {
        String tmp = unparsedPayload.substring(2);
        bytes = hexStringToByteArray(tmp.replaceAll(" ", "").toLowerCase());
      }

      else
      {
        String eomSeparator = "";

        if (options.contains(Option.INCLUDE_CR_AT_END_OF_MESSAGE) &&
            options.contains(Option.INCLUDE_LF_AT_END_OF_MESSAGE))
        {
          eomSeparator = "\r\n";
        }

        else if (options.contains(Option.INCLUDE_CR_AT_END_OF_MESSAGE))
        {
          eomSeparator = "\r";
        }

        else if (options.contains(Option.INCLUDE_LF_AT_END_OF_MESSAGE))
        {
          eomSeparator = "\n";
        }

        bytes = (unparsedPayload + eomSeparator).getBytes();
      }

      return bytes;
    }

    private static byte[] hexStringToByteArray(String s)
    {
      int len = s.length();
      byte[] data = new byte[len / 2];

      for (int i = 0; i < len; i += 2)
      {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }

      return data;
    }

    private ByteArrayMessage(String unparsedPayload, Set<Option> options)
    {
      super(parse(unparsedPayload, options));
    }

    @Override public String toString()
    {
      return new String(super.getContent());
    }
  }


  public static class Properties
  {
    private Map<String, String> commandProperties = new HashMap<String, String>(2);


    public String getMandatoryProperty(String name) throws RuntimeException
    {
      if (name == null || name.equals(""))
      {
        throw new RuntimeException("");
      }

      String value = commandProperties.get(name.toLowerCase());

      if (value == null || value.equals(""))
      {
        throw new RuntimeException("");
      }

      return value;
    }

    public InetAddress getMandatoryInetAddress(String name) throws RuntimeException
    {
      if (name == null || name.equals(""))
      {
        throw new RuntimeException("");
      }

      String value = commandProperties.get(name.toLowerCase());

      if (value == null || value.equals(""))
      {
        throw new RuntimeException("");
      }

      try
      {
        return InetAddress.getByName(value);
      }

      catch (UnknownHostException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    public Number getMandatoryNumber(String name) throws RuntimeException
    {
      if (name == null || name.equals(""))
      {
        throw new RuntimeException("");
      }

      String value = commandProperties.get(name.toLowerCase());

      if (value == null || value.equals(""))
      {
        throw new RuntimeException("");
      }

      try
      {
        return new BigInteger(value);
      }

      catch (NumberFormatException e)
      {

      }

      try
      {
        return new BigDecimal(value);
      }

      catch (NumberFormatException e)
      {
        throw new RuntimeException("can't parse it");
      }
    }

    public String getOptionalProperty(String name)
    {
      if (name == null || name.equals(""))
      {
        return null;
      }

      return commandProperties.get(name.toLowerCase());
    }

    public Boolean getOptionalBoolean(String name)
    {
      if (name == null || name.equals(""))
      {
        return null;
      }

      String value = commandProperties.get(name.toLowerCase());

      if (value == null || value.equals(""))
      {
        return null;
      }
      
      return (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
    }


    protected void add(String name, String value)
    {
      commandProperties.put(name.toLowerCase(), value);
    }


  }
}

