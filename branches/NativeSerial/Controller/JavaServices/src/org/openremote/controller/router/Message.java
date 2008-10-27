/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
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
package org.openremote.controller.router;

import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;

import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 * TODO: Internal message presentation.  <p>
 *
 * This message encapsulates both device commands and events generated by the devices.
 * 
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Message
{

  // Enums ----------------------------------------------------------------------------------------

  @Deprecated
  private enum MandatoryHeader
  {
    VERSION, HOP, UID, SOURCE, CLASS
  }

  private enum OptionalHeader
  {
    TARGET
  }

  public final static String OPENREMOTE_COMPONENT_BLOCK = "OpenRemote Component";



  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "CONTROL PROTOCOL MESSAGE";

  private final static String DEVICE_REGISTRATION_MSG_PREFIX = "deviceregistration";


  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private boolean isDeviceRegistrationMessage = false;

  private String versionHeader, hopHeader, uidHeader, sourceHeader, classHeader, targetHeader;

  private String componentName;
  
  private Map<String, String> optionalHeaders = new HashMap<String, String>();

  private Map<String, String> messageBlocks = new HashMap<String, String>();


  // Constructors ---------------------------------------------------------------------------------

  public Message(String messageFormat)
  {
    messageFormat = messageFormat.trim();

    if (!messageFormat.startsWith("header"))
    {
      // TODO

      throw new Error();
    }

    StringTokenizer blockTokenizer = new StringTokenizer(messageFormat, "{}");

    blockTokenizer.nextToken();   // remove 'header' string...
    
    parseHeaders(blockTokenizer.nextToken());

    while (blockTokenizer.hasMoreTokens())
    {
      addMessageBlock(blockTokenizer.nextToken().trim(), blockTokenizer.nextToken().trim());
    }

    if (classHeader.startsWith(DEVICE_REGISTRATION_MSG_PREFIX))
    {
      isDeviceRegistrationMessage = true;
    }


  }


  // Instance Methods -----------------------------------------------------------------------------

  public String getVersion()
  {
    return versionHeader;
  }

  public String getHop()
  {
    return hopHeader;
  }

  public String getUID()
  {
    return uidHeader;
  }

  public String getSource()
  {
    return sourceHeader;
  }



  public String getMessageClass()
  {
    return classHeader;
  }

  public String getTarget()
  {
    return targetHeader;
  }

  public String getComponentName()
  {
    return componentName;
  }

  public boolean isDeviceRegistrationMessage()
  {
    return isDeviceRegistrationMessage;
  }


  public String getAddress()
  {
    return targetHeader;
  }

  public void setAddress(String destinationAddress)
  {
    this.targetHeader = destinationAddress;
  }

  public void addMessageBlock(String blockID, String properties)
  {
    if (blockID.equalsIgnoreCase(OPENREMOTE_COMPONENT_BLOCK))
    {
      StringTokenizer tokenizer = new StringTokenizer(properties, "\n");

      while (tokenizer.hasMoreTokens())
      {
        String property = tokenizer.nextToken().trim();

        if (property.startsWith("ComponentName"))
        {
          StringTokenizer propertyTokenizer = new StringTokenizer(property, "=");

          propertyTokenizer.nextToken();

          componentName = propertyTokenizer.nextToken().trim();
        }
      }
    }
    
    messageBlocks.put(blockID, properties);  
  }

  public void send(String invokerComponentName)
  {

    // TODO : should be debug level logging...

    log.info("Invoking: '" + invokerComponentName + "'.");

    
    
  }


  // Object Overrides -----------------------------------------------------------------------------


  @Override public String toString()
  {
    StringBuilder builder = new StringBuilder(1024);

    builder
        .append("header\n")
        .append("{\n")
        .append("  version = ").append(getVersion()).append("\n")
        .append("  hop = ").append(getHop()).append("\n")
        .append("  uid = ").append(getUID()).append("\n")
        .append("  source = ").append(getSource()).append("\n")
        .append("  class = ").append(getMessageClass()).append("\n");

    if (targetHeader != null)
      builder.append("  target = ").append(getTarget()).append("\n");

    builder.append("}\n");

    for (String messageBlock : messageBlocks.keySet())
    {
      builder
          .append("\n")
          .append(messageBlock).append("\n")
          .append("{\n")
          .append(messageBlocks.get(messageBlock)).append("\n")
          .append("}\n");
    }

    return builder.toString();
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void parseHeaders(String headerBlock)
  {
    StringTokenizer propertyTokenizer = new StringTokenizer(headerBlock, "\n");

    // Mandatory 'Version' header...

    String[] elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.VERSION.name()))
      throw new Error("no version");

    versionHeader = elements[1].trim();


    // Mandatory 'Hop' header....

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.HOP.name()))
      throw new Error("no hop");

    hopHeader = elements[1].trim();


    // Mandatory 'UID' header...

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.UID.name()))
      throw new Error("no uid");

    uidHeader = elements[1].trim();


    // Mandatory 'Source' header...

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.SOURCE.name()))
      throw new Error("no source");

    sourceHeader = elements[1].trim();


    // Mandatory 'Class' header...

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.CLASS.name()))
      throw new Error("no class");

    classHeader = elements[1].trim();


    // Other headers...

    if (!propertyTokenizer.hasMoreTokens())
      return;

    while (propertyTokenizer.hasMoreTokens())
    {
      try
      {
        elements = propertyTokenizer.nextToken().trim().split("=");

        if (elements.length != 2)
        {
          log.debug("Skipping erroneous header line: " + Arrays.toString(elements));
          
          continue;
        }

        String key = elements[0].trim();
        String value = elements[1].trim();

        if (key.equalsIgnoreCase(OptionalHeader.TARGET.name()))
          targetHeader = value;
        else
          optionalHeaders.put(key, value);
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        // TODO

        log.error("Unable to parse message header block: " + Arrays.toString(elements) + "\n " + headerBlock);
      }
    }
  }

}
