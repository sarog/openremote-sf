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
package org.openremote.controller.protocol.knx.dib;

import java.util.Map;

import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;

/**
 * Supported Service Family DIB as defined in KNX 1.1 Specification, Volume 3: System
 * Specifications, Part 8: EIBnet/IP, Chapter 2: Core. <p>
 *
 * Supported Service Family DIB structure is used as part of
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier#DESCRIPTION_RESPONSE} and
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier#SEARCH_RESPONSE} frames. <p>
 *
 * Supported Service Family DIB structure follows the common KNXnet/IP frame structure
 * definition with first byte containing the size of the structure itself and the second byte
 * containing structure type identifier which determines the rest of the DIB structure content. <p>
 *
 * The variable content of Supported Service Family DIB is service type family identifier and
 * its associated version number. These are repeated for each supported service family. The
 * frame structure therefore looks as follows:
 *
 * <pre>
 *   +-------+--------+--------+--------+--------+--------+ ... +--------+--------+
 *   | Size  |TypeCode|Service | Family |Service | Family |     |Service | Family |
 *   |       |        | Family |Version | Family |Version |     | Family |Version |
 *   +-------+--------+--------+--------+--------+--------+ ... +--------+--------+
 *    1 byte   1 byte   1 byte   1 byte   1 byte   1 byte         1 byte   1 byte
 * </pre>
 *
 * The full length of the structure varies according to number of included supported service
 * families. The service family codes are defined in {@link ServiceTypeIdentifier.Family}.
 * The version number is an integer that matches the version of KNX specification document
 * that defines service implementations. <p>
 *
 * @see DescriptionInformationBlock
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SupportedServiceFamily extends DescriptionInformationBlock
{

  private Map<ServiceTypeIdentifier.Family, Integer> supportedServices;

  /**
   * Constructs a new SupportedServiceFamily DIB with given service family identifiers and
   * associated version numbers.
   *
   * @see org.openremote.controller.protocol.knx.ServiceTypeIdentifier.Family
   *
   * @param supportedServices   map of service family identifiers and their associated versions
   */
  public SupportedServiceFamily(Map<ServiceTypeIdentifier.Family, Integer> supportedServices)
  {
    super(TypeCode.SUPPORTED_SERVICE_FAMILIES, supportedServices.keySet().size() * 2);

    this.supportedServices = supportedServices;
  }

  /**
   * Returns the frame structure to be used in KNXnet/IP frame.
   *
   * @return  byte array describing this supported service family DIB structure
   */
  @Override public byte[] getFrameStructure()
  {
    byte size = (byte)(super.getStructureSize() & 0xFF);
    byte type = super.type.getValue();

    byte[] struct = new byte[size];

    struct[0] = size;
    struct[1] = type;

    int index = 2;

    for (ServiceTypeIdentifier.Family family : supportedServices.keySet())
    {
      struct[index++] = family.getValue();
      struct[index++] = (byte)(supportedServices.get(family) & 0xFF);
    }

    return struct;
  }
}

