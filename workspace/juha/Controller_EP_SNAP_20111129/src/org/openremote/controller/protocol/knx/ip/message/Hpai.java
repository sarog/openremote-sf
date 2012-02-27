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
package org.openremote.controller.protocol.knx.ip.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * This is an implementation of a Host Protocol Address Information (HPAI) structure used in
 * KNXnet/IP v1.0 frames (IPv4 UDP) as defined in KNX 1.1 specifications Volume 3: System
 * Specifications, Part 8: EIBnet/IP, Chapter 2: Core. <p>
 *
 * This implementation is specific to IPv4 UDP protocol. <p>
 *
 * The generic HPAI structure in KNXnet/IP frame is as follows:
 *
 * <pre>
 *   +--------+--------+-------- ... --------+
 *   |  Size  |Protocol|  Protocol Specific  |
 *   |        |  Code  |    Variable Data    |
 *   +--------+--------+-------- ... --------+
 *     1 byte   1 byte         n bytes
 *</pre>
 *
 * Where first two bytes are common to all structures used in KNXnet/IP frames: first byte
 * contains the entire frame structure size and the second byte (for HPAI, the Protocol Code)
 * is the identifier that determines the rest of the structure's variable size content (NOTE:
 * if a size of structure would exceed 252 bytes, i.e. more than can be expressed in single
 * byte, the size byte will have value 0xFF and the next two bytes will contain the actual
 * structure lenght as a 16-bit integer). <p>
 *
 * This implementation is fixed for IPv4 UDP host protocol addresses, therefore the frame
 * is of fixed size:
 *
 * <pre>
 *   +--------+--------+--------------------------------+----------------+
 *   |  Size  |IPV4_UDP|     IPv4 Address               |    Port        |
 *   |        |        |                                |                |
 *   +--------+--------+--------------------------------+----------------+
 *     1 byte   1 byte             4 bytes                     2 bytes
 * </pre>
 *
 * Where structure size is fixed to value eight (as defined in {@link #KNXNET_IP_10_HPAI_SIZE})
 * and protocol code is fixed to IPV4_UDP (as defined in {@link HostProtocolCode#IPV4_UDP}). <p>
 *
 * The IPv4 address is in network byte order with highest order byte at index 2 of this frame
 * structure and lowest order byte at index 5 of this frame structure. The UDP (or TCP) port
 * number is a two byte value also in network byte order (highest byte order at index 6 of this
 * frame structure and lowest byte order at index 7 of this frame structure).
 *
 * @author Olivier Gandit
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Hpai
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The Host Protocol Address Information (HPAI) structure size for IPV4 UDP/TCP based
   * addressing : {@value}
   */
  public final static int KNXNET_IP_10_HPAI_SIZE = 0x08;

  /**
   * The common KNXnet/IP frame structure header : structure size + identifier
   */
  private final static byte[] HEADER = {
      KNXNET_IP_10_HPAI_SIZE,
      HostProtocolCode.IPV4_UDP.getValue()
  };


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Host protocol codes for IPv4 based HPAI (KNXNet/IP frame structure identifier).
   */
  private enum HostProtocolCode
  {
    /**
     * IPv4 UDP protocol : {@value}
     */
    IPV4_UDP(0x01),

    /**
     * IPv4 TCP protocol : {@value}
     */
    IPV4_TCP(0x02);


    private byte value;

    private HostProtocolCode(int value)
    {
      this.value = (byte)(value & 0xFF);
    }

    /**
     * Returns the byte value of host protocol code to use in KNXnet/IP HPAI structure.
     *
     * @return  host protocol code
     */
    byte getValue()
    {
      return value;
    }
  }


  // Class Members --------------------------------------------------------------------------------


  /**
   * Returns the size of this host protocol address information structure. <p>
   *
   * This implementation assumes IPv4 addressing and therefore returns a fixed structure size
   * as defined in {@link #KNXNET_IP_10_HPAI_SIZE}.
   *
   * @return    HPAI structure size for IPv4 based addressing
   */
  static int getStructureSize()
  {
    return KNXNET_IP_10_HPAI_SIZE;
  }


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Stores an IPv4 address and port for this host protocol address information structure.
   */
  private InetSocketAddress address;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new host protocol address information (HPAI) with a given IPv4 address and port.
   *
   * @param address   IPv4 address and port
   */
  public Hpai(InetSocketAddress address)
  {
    this.address = address;
  }

  /**
   * Constructs a new host protocol address information (HPAI) from a given input stream. <p>
   *
   * The input stream must be positioned exactly at the start of the HPAI structure. <p>
   *
   * The implementation assumes an IPv4 addressing.
   *
   * @param   is      input stream to read from
   *
   * @throws IOException
   *            if there was an I/O error reading from the input stream
   */
  public Hpai(InputStream is) throws IOException
  {
    // TODO check structure length & protocol type
    is.skip(2);

    // TODO check read return values
    byte[] a = new byte[4];
    is.read(a);

    int p = (is.read() << 8) + is.read();

    this.address = new InetSocketAddress(InetAddress.getByAddress(a), p);
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Writes this host protocol address information (HPAI) structure into the given output
   * stream. The number of bytes written is defined in {@link #KNXNET_IP_10_HPAI_SIZE}. This
   * includes the HPAI structure size byte, protocol code byte, a four-byte IPv4 address
   * (written in network byte order) and a two byte IPv4 port (in network byte order).
   *
   * @param   os    output stream to write to
   *
   * @throws IOException
   *              if there was an I/O error writing to the output stream
   */
  public void write(OutputStream os) throws IOException
  {
    os.write(HEADER);

    // TODO check byte array contains exactly 4 bytes
    byte[] a = this.address.getAddress().getAddress();
    os.write(a);

    int p = this.address.getPort();
    os.write((p >> 8) & 0xFF);
    os.write(p & 0xFF);
  }

  /**
   * Returns the IPv4 host address and port of this HPAI.
   *
   * @return  IPv4 address and port
   */
  public InetSocketAddress getAddress()
  {
    return this.address;
  }
}
