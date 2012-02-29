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

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.nio.charset.Charset;

import org.openremote.controller.protocol.knx.IndividualAddress;
import org.openremote.controller.protocol.knx.KNXCommandBuilder;
import org.openremote.controller.utils.Logger;

/**
 * This is Device Information DIB as defined in KNX 1.1 Specification, Volume 3: System
 * Specifications, Part 8: EIBnet/IP, Chapter 2: Core. As is common with all KNXnet/IP
 * frame structures, Device Information DIB also starts with first byte including the
 * total size of the frame structure, and the second byte containing a type identifier
 * which dictates the variable part of this Descriptiong Information Block (DIB). <p>
 *
 * Device Information DIB structure bytes are defined as follows:
 *
 * <pre>
 *         +--------+--------+--------+--------+----------------+----------------+-- ...
 *         |  Size  |TypeCode|  KNX   | Device |   Individual   |Project-Install |
 *         |        |        | Medium | Status |    Address     |  Identifier    |
 *         +--------+--------+--------+--------+----------------+----------------+-- ...
 *           1 byte   1 byte   1 byte   1 byte      2 bytes          2 bytes
 *
 *   ... --+------- ... -------+------- ... -------+------- ... -------+------ ... -------+
 *         |   Serial Number   | Routing Multicast |    MAC address    |   Device Name    |
 *         |                   |      Address      |                   |                  |
 *   ... --+------- ... -------+------- ... -------+------- ... -------+------ ... -------+
 *                6 bytes             4 bytes             6 bytes            30 bytes
 * </pre>
 *
 * The type code is defined in {@link DescriptionInformationBlock.TypeCode} and KNX medium is
 * defined in {@link KNXMedium}. <p>
 *
 * Device status byte only defines one bit (bit 0) to indicate program mode.
 * The project-installation bytes are defined as having install identifier as the least
 * significant nibble (last 4 bits) of the least significant (low) byte and project number
 * occupying the rest of the bits in the two byte value. <p>
 *
 * Routing multicast address is used by devices that implement the routing services (see
 * {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier.Family#ROUTING}). For
 * devices that do not implement routing services, the address should be set to unbound
 * 0.0.0.0 address. <p>
 *
 * The device name must be 0x00 (Null) terminated ISO-8859-1 encoded string, at most 30 bytes
 * long. When name does not use the entire length, the frame is padded with 0x00 bytes to fill
 * the entire 30 byte field.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeviceInformation extends DescriptionInformationBlock
{

  // Constants ------------------------------------------------------------------------------------
  /**
   * A fixed size for a KNXnet/IP Device Information DIB : {@value}
   */
  public final static int KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE = 54;

  // TODO : not using 'Program Mode' for anything at the moment
  private final static int DEVICE_STATUS_PROGRAM_MODE_BIT = 0x1;


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Describes the physical layer used for KNX bus communication.
   */
  public enum KNXMedium
  {
    /**
     * Twisted Pair 0 (TP0)
     */
    TP0(0x01),

    /**
     * Twisted Pair 1 (TP1-64 and TP1-256)
     */
    TP1(0x02),

    /**
     * Powerline 230V -- 110kHz
     */
    PL110(0x04),

    /**
     * Powerline 230V -- 132kHz
     */
    PL132(0x08),

    /**
     * Radio Frequency.
     */
    RF(0x10);


    private byte code;

    private KNXMedium(int code)
    {
      this.code = (byte)(code & 0xFF);
    }

    byte getValue()
    {
      return code;
    }
  }



  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging (reuse KNX protocol category).
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * KNX physical medium for this description.
   */
  private KNXMedium medium;

  /**
   * KNX device individual address for this description.
   */
  private IndividualAddress individualAddress;

  /**
   * Multicast routing address for this description. Should only be defined for devices that
   * support {@link org.openremote.controller.protocol.knx.ServiceTypeIdentifier.Family#ROUTING
   * routing} services.
   */
  private InetSocketAddress multicastRoutingAddress = new InetSocketAddress("0.0.0.0", 0);

  /**
   * Network interface used by the device.
   */
  private NetworkInterface nic;

  /**
   * User-friendly name. Must be encoded as ISO-8859-1. At most 30 characters long *including*
   * an ending zero byte.
   */
  private String name;

  /**
   * Project and installation codes. Installation code is a 4-bit nibble. Project code is
   * 12 bits.
   */
  private int project, installation;

  /**
   * Six byte serial number of the device.
   */
  private long serial;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new device information description.
   *
   * @param name
   *          User friendly name. Should be at most 29 characters long, and will
   *          be truncated to that lenght. Character set must be ISO-8859-1 and
   *          all characters will be mapped to ISO-8859-1 where such mapping
   *          exists, or mapped to default unknown character byte.
   *
   * @param medium
   *          KNX physical medium of the device
   *
   * @param address
   *          KNX individual address of the device, see {@link IndividualAddress}
   *
   * @param nic
   *          Network interface used by the device. Will be used to access the MAC address
   *          of the network interface.
   *
   * @param projectNumber
   *          Project code number. This is a 12-bit value.
   *
   * @param installationNumber
   *          Project installation number. This is a 4-bit value.
   *
   * @param serialNumber
   *          Serial number of the device. This is at most a six-byte (48-bit) signed integer.
   */
  public DeviceInformation(String name, KNXMedium medium, IndividualAddress address,
                           NetworkInterface nic, int projectNumber, int installationNumber,
                           long serialNumber)
  {
    super(DescriptionInformationBlock.TypeCode.DEVICE_INFO, KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE);

    this.medium = medium;
    this.individualAddress = address;

    // TODO : could warn if these values are getting truncated
    this.project = projectNumber & 0xFFF;
    this.installation = installationNumber & 0xF;

    this.serial = serialNumber;

    if (nic == null)
    {
      throw new IllegalArgumentException("null network interface");
    }

    this.nic = nic;

    Charset iso_8859_1 = Charset.forName("ISO-8859-1");

    if (name.length() > 29)
    {
      this.name = new String(iso_8859_1.encode(name.substring(0, 29)).array());

      log.warn("Truncated device name to '" + this.name + "'.");
    }

    else
    {
     this.name = new String(iso_8859_1.encode(name).array());
    }
  }


  /**
   * Returns the fixed structure size of this Device Information DIB
   *
   * @return    value of {@link #KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE}
   */
  @Override public int getStructureSize()
  {
    return KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE;
  }


  /**
   * Returns the bytes for this Device Information DIB to include in KNXnet/IP frame. <p>
   *
   * @return  byte array describing this frame structure
   */
  @Override public byte[] getFrameStructure()
  {
    byte[] struct = new byte[KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE];

    byte[] addrBytes = individualAddress.getAddress();


    // massage 4-bit installation code and 12-bit project code into an integer...

    int projectInstallation = project << 4;
    projectInstallation += installation & 0x0F;

    // massage 48-bit serial number into two ints...

    int serialHi = (int)((serial >> 32) & 0xFFFF);
    int serialLo = (int)(serial & 0xFFFFFFFF);

    // pad name string with zeroes (already converted to ISO-8859-1 so each char is 1-byte long)...

    byte[] nameBytes = Arrays.copyOf(name.getBytes(), 30);

    byte[] mcastRoutingAddress = multicastRoutingAddress.getAddress().getAddress();

    byte[] mac;

    try
    {
      mac = nic.getHardwareAddress();

      if (mac == null || mac.length != 6)
      {
        log.warn("Could not retrieve MAC address from " + nic);

        mac = new byte[6];
        Arrays.fill(mac, (byte)0x00);
      }
    }

    catch (SocketException e)
    {
      throw new Error("Could not retrieve MAC addres from " + nic + " : " + e.getMessage(), e);
    }

    
    struct[0] = KNXNET_IP_10_DEVICEINFORMATION_DIB_SIZE;
    struct[1] = super.type.getValue();
    struct[2] = medium.getValue();
    struct[3] = 0x00;                                         // device status
    struct[4] = addrBytes[0];
    struct[5] = addrBytes[1];
    struct[6] = (byte)((projectInstallation >> 8) & 0xFF);
    struct[7] = (byte)(projectInstallation & 0xFF);
    struct[8] = (byte)((serialHi >> 8) & 0xFF);
    struct[9] = (byte)(serialHi & 0xFF);
    struct[10] = (byte)((serialLo >> 24) & 0xFF);
    struct[11] = (byte)((serialLo >> 16) & 0xFF);
    struct[12] = (byte)((serialLo >> 8) & 0xFF);
    struct[13] = (byte)(serialLo & 0xFF);
    struct[14] = mcastRoutingAddress[0];
    struct[15] = mcastRoutingAddress[1];
    struct[16] = mcastRoutingAddress[2];
    struct[17] = mcastRoutingAddress[3];
    struct[18] = mac[0];
    struct[19] = mac[1];
    struct[20] = mac[2];
    struct[21] = mac[3];
    struct[22] = mac[4];
    struct[23] = mac[5];
    
    System.arraycopy(nameBytes, 0, struct, 24, 30);

    return struct;
  }
}

