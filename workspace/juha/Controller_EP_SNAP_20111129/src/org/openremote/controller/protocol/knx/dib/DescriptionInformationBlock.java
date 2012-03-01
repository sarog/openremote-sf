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

/**
 * Description Information Block (DIB) is used within
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier#DESCRIPTION_RESPONSE} and
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier#SEARCH_RESPONSE} frames. <p>
 *
 * The description information block structure follows the common KNXnet/IP frame structure
 * definition with first byte containing the size of the structure itself and the second byte
 * denoting the structure type identifier which determines the rest of the DIB structure content.
 * However, the DIB structure should always have an even number of bytes, so it may potentially
 * end with a single byte 0x00 padding. <p>
 *
 * The generic structure frame definition is therefore:
 *
 * <pre>
 *   +--------+--------+--------------- ... ----------------+
 *   |  Size  |TypeCode|             DIB Data               |
 *   |        |        |                                    |
 *   +--------+--------+--------------- ... ----------------+
 *     1 byte   1 byte                n bytes
 * </pre>
 *
 * The type codes are defined in {@link TypeCode}. For details on different DIB data structures,
 * see {@link DeviceInformation} and {@link SupportedServiceFamily}.
 *
 * @see SupportedServiceFamily
 * @see DeviceInformation
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class DescriptionInformationBlock
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Structure identifier for various Description Information Block (DIB).
   */
  public enum TypeCode
  {
    /**
     * Device Information DIB. See {@link DeviceInformation} for an implementation.
     */
    DEVICE_INFO(0x01),

    /**
     * Supported Service Families DIB. See {@link SupportedServiceFamily} for an implementation.
     */
    SUPPORTED_SERVICE_FAMILIES(0x02),

    /**
     * Manufacturer data DIB. TBD.
     */
    MANUFACTURER_DATA(0xFE);

    private byte code;

    private TypeCode(int code)
    {
      this.code = (byte)(code & 0xFF);
    }

    public byte getValue()
    {
      return code;
    }
  }



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * The variable size of the DIB structure, *not* including the first two standard bytes for size
   * and identifier
   */
  private int structureSize;

  /**
   * Description Information Block identifier.
   */
  protected TypeCode type;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new Description Information Block (DIB) with a given type identifier and
   * a variable structure size. <p>
   *
   * Note that the variable size only includes the non-fixed size of this structure. The mandatory
   * size and identifier fields will be added to it.
   *
   * @param type                    DIB type identifier
   * @param variableStructureSize   the size of the variable element in this DIB, *not* including
   *                                the mandatory size and identifier fields
   */
  DescriptionInformationBlock(TypeCode type, int variableStructureSize)
  {
    this.type = type;

    this.structureSize = variableStructureSize;
  }


  // Instance Methods -----------------------------------------------------------------------------

  /**
   * Returns the total size of this Description Information Block.
   *
   * @return  the size of DIB structure in bytes
   */
  public int getStructureSize()
  {
    return structureSize + 2;   // extra two bytes for size field and typecode field in frame...
  }

  /**
   * Returns the bytes for this Description Information Block to include in KNXnet/IP frame. <p>
   *
   * Concrete subclasses must implement this method to provide the correct sequence of bytes
   * to include in the frame.
   *
   * @return  byte array describing this frame structure
   */
  public abstract byte[] getFrameStructure();
}

