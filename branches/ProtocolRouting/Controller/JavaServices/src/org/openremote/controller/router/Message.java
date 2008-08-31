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

import java.util.StringTokenizer;

/**
 * Internal message presentation.  <p>
 *
 * This message encapsulates both device commands and events generated by the devices.
 * 
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Message
{

  // Enums ----------------------------------------------------------------------------------------

  private enum MandatoryHeader
  {
    VERSION, HOP, UID, SOURCE, CLASS
  }


  // Constants ------------------------------------------------------------------------------------

  private final static String DEVICE_REGISTRATION_MSG_PREFIX = "deviceregistration";



  // Instance Fields ------------------------------------------------------------------------------

  private boolean isDeviceRegistrationMessage = false;

  private String versionHeader, hopHeader, uidHeader, sourceHeader, classHeader;


  // Constructors ---------------------------------------------------------------------------------

  public Message(String messageFormat)
  {
    final String HEADER = "header";

    messageFormat = messageFormat.trim();

    if (!messageFormat.startsWith(HEADER))
      throw new Error();

    messageFormat = messageFormat.substring(HEADER.length()).trim();

    StringTokenizer blockTokenizer = new StringTokenizer(messageFormat, "{}");

    String headerBlock = blockTokenizer.nextToken();

    parseHeaders(headerBlock);

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

  public boolean isDeviceRegistrationMessage()
  {
    return isDeviceRegistrationMessage;
  }


/*
  public Address getAddress()
  {

    return null;
  }

  public void setAddress(Address destinationAddress)
  {


  }
*/

  public void send()
  {

  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void parseHeaders(String headerBlock)
  {
    StringTokenizer propertyTokenizer = new StringTokenizer(headerBlock, "\n");


    // Mandatory 'Version' header...

    String[] elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.VERSION.name()))
      throw new Error("no version");

    versionHeader = elements[1].trim().toLowerCase();


    // Mandatory 'Hop' header....

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.HOP.name()))
      throw new Error("no hop");

    hopHeader = elements[1].trim().toLowerCase();


    // Mandatory 'UID' header...

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.UID.name()))
      throw new Error("no uid");

    uidHeader = elements[1].trim().toLowerCase();


    // Mandatory 'Source' header...

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.SOURCE.name()))
      throw new Error("no source");

    sourceHeader = elements[1].trim().toLowerCase();


    // Mandatory 'Class' header...

    elements = propertyTokenizer.nextToken().trim().split("=");

    if (!elements[0].trim().equalsIgnoreCase(MandatoryHeader.CLASS.name()))
      throw new Error("no class");

    classHeader = elements[1].trim().toLowerCase();

  }
}
