/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import org.apache.log4j.Logger;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an abstract superclass for KNX protocol read/write commands.
 *
 * @see KNXWriteCommand
 * @see KNXReadCommand
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
abstract class KNXCommand implements Command
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);

  /**
   * Factory method for creating KNX command instances {@link KNXWriteCommand} and
   * {@link KNXReadCommand} based on a human-readable configuration strings.  <p>
   *
   * Each KNX command instance is associated with a link to a connection manager and a
   * destination group address.
   *
   * @param name      Command lookup name. This is usually a human-readable string used in
   *                  configuration and tools. Note that multiple lookup names can be used to
   *                  return Java equal() (but not same instance) commands.
   * @param mgr       KNX connection manager used to transmit this command
   * @param address   KNX destination group address.
   *
   * @throws NoSuchCommandException if command cannot be created by its lookup name
   *
   * @return  new KNX command instance
   */
  static KNXCommand createCommand(String name, KNXConnectionManager mgr, GroupAddress address)
  {
    name = name.trim().toUpperCase();

    KNXWriteCommand writeCmd = KNXWriteCommand.createCommand(name, mgr, address);

    if (writeCmd != null)
      return writeCmd;

    KNXReadCommand readCmd = KNXReadCommand.createCommand(name, mgr, address);

    if (readCmd != null)
      return readCmd;

    throw new NoSuchCommandException("Unknown command '" + name + "'.");

  }


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Destination address for this command.
   */
  private GroupAddress address;

  /**
   * Connection manager to be used to transmit this command.
   */
  private KNXConnectionManager connectionManager;

  /**
   * Command payload (APDU).
   */
  private ApplicationProtocolDataUnit apdu;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a KNX command with a given connection manager, destination group address and
   * an APDU payload.
   *
   * @param connectionManager KNX connection manager instance used for transmitting this commnad
   * @param address           destination group address
   * @param apdu              command payload
   */
  KNXCommand(KNXConnectionManager connectionManager, GroupAddress address,
             ApplicationProtocolDataUnit apdu)
  {
    this.address = address;
    this.connectionManager = connectionManager;
    this.apdu = apdu;
  }



  // Package-Private Instance Methods -------------------------------------------------------------

  /**
   * Relay a write command to an open KNX/IP connection.
   *
   * @param command   KNX write command
   *
   * @throws ConnectionException  if connection fails for any reason
   */
  void send(KNXWriteCommand command) throws ConnectionException
  {
      KNXConnection connection = connectionManager.getConnection();
      connection.send(command);
  }


  /**
   * TODO
   *
   * @param command
   * @return
   * @throws ConnectionException
   */
  ApplicationProtocolDataUnit read(KNXReadCommand command) throws ConnectionException
  {
    KNXConnection connection = connectionManager.getConnection();
    return connection.read(command);
  }
  

  /**
   * The KNX Common External Message Interface (a.k.a cEMI) frame has a variable length
   * and structure depending on the Common EMI frame message code (first byte) and additional
   * info length (second byte).   <p>
   *
   * In a very generic fashion, a Common EMI frame can be defined as follows
   * (KNX 1.1 Application Note 033 - Common EMI Specification, 2.4 Basic Message Structure,
   * page 8):
   *
   * <pre>{@code
   *
   * +----+----+---- ... ----+-------- ... --------+
   * | MC | AI |  Add. Info  |   Service Info      |
   * +----+----+---- ... ----+-------- ... --------+
   *
   * MC = Message Code
   * AI = Additional Info Length (0x00 if no additional info is included)
   *
   * }</pre>
   *
   * KNX communication stack defines a frame transfer service (known as L_Data Service) in the
   * data link layer (KNX 1.1 -- Volume 3 System Specification, Part 2 Communication,
   * Chapter 2 Data Link Layer General, section 2.1 L_Data Service, page 8).  <p>
   *
   * Link layer data services are available in "normal" mode (vs. bus monitor mode). A data
   * request (known as L_Data.req primitive) is used to transmit a frame. The corresponding
   * Common EMI frame for L_Data.req is defined as shown below (KNX 1.1 Application Note 033,
   * section 2.5.33 L_Data.req, page 13). Example assumes a standard (non-extended) frame with
   * no additional info fields set in the frame. The application protocol data unit (APDU) is
   * for a short data (<= 6 bits) group value write request (A_GroupValue_Write.req)
   *
   * <pre>{@code
   *
   * +--------+--------+--------+--------+----------------+----------------+--------+----------------+
   * |  Msg   |Add.Info| Ctrl 1 | Ctrl 2 | Source Address | Dest. Address  |  Data  |      APDU      |
   * | Code   | Length |        |        |                |                | Length |                |
   * +--------+--------+--------+--------+----------------+----------------+--------+----------------+
   *  1 byte   1 byte   1 byte   1 byte      2 bytes          2 bytes       1 byte      2 bytes
   *
   * Message Code    = 0x11 - a L_Data.req primitive
   * Add.Info Length = 0x00 - no additional info
   * Control Field 1 = see the bit structure below
   * Control Field 2 = see the bit structure below
   * Source Address  = 0x0000 - filled in by router/gateway with its source address which is
   *                   part of the KNX subnet
   * Dest. Address   = KNX group or individual address (2 byte)
   * Data Length     = Number of bytes of data in the APDU excluding the TPCI/APCI bits
   * APDU            = Application Protocol Data Unit - the actual payload including transport
   *                   protocol control information (TPCI), application protocol control
   *                   information (APCI) and data passed as an argument from higher layers of
   *                   the KNX communication stack
   *
   * }</pre>
   *
   * Common External Message Interface Control Fields [KNX 1.1 Application Note 033]  <p>
   *
   * Common External Message Interface (EMI) defines two control fields in its frame format
   * (one byte each). The bit structure of each control field is defined in the KNX 1.1
   * Application Note 033: Common EMI Specification, section 2.4 Basic Message Structure:
   *
   *
   * <pre>{@code
   *
   *        Control Field 1
   *
   *    Bit  |
   *   ------+---------------------------------------------------------------
   *     7   | Frame Type  - 0x0 for extended frame
   *         |               0x1 for standard frame
   *   ------+---------------------------------------------------------------
   *     6   | Reserved
   *         |
   *   ------+---------------------------------------------------------------
   *     5   | Repeat Flag - 0x0 repeat frame on medium in case of an error
   *         |               0x1 do not repeat
   *   ------+---------------------------------------------------------------
   *     4   | System Broadcast - 0x0 system broadcast
   *         |                    0x1 broadcast
   *   ------+---------------------------------------------------------------
   *     3   | Priority    - 0x0 system
   *         |               0x1 normal
   *   ------+               0x2 urgent
   *     2   |               0x3 low
   *         |
   *   ------+---------------------------------------------------------------
   *     1   | Acknowledge Request - 0x0 no ACK requested
   *         | (L_Data.req)          0x1 ACK requested
   *   ------+---------------------------------------------------------------
   *     0   | Confirm      - 0x0 no error
   *         | (L_Data.con) - 0x1 error
   *   ------+---------------------------------------------------------------
   *
   *       //   Control Field 2
   *
   *    Bit  |
   *   ------+---------------------------------------------------------------
   *     7   | Destination Address Type - 0x0 individual address
   *         |                          - 0x1 group address
   *   ------+---------------------------------------------------------------
   *    6-4  | Hop Count (0-7)
   *   ------+---------------------------------------------------------------
   *    3-0  | Extended Frame Format - 0x0 for standard frame
   *   ------+---------------------------------------------------------------
   *
   * }</pre>
   *
   */
  Byte[] getCEMIFrame()
  {
    final int LINK_LAYER_DATA_REQUEST   = 0x11;       // Message Code, L_Data.req primitive
    final int NO_ADDITIONAL_INFORMATION = 0x00;       // Additional info length = 0
    final int SOURCE_ADDRESS_HIBYTE     = 0x00;       // Source address will be filled in by
    final int SOURCE_ADDRESS_LOBYTE     = 0x00;       // KNX gateway/router

    //   Control Field 1

    /* A bit for standard common EMI frame type (not extended) in the first control field. */
    final int STANDARD_FRAME_TYPE = 0x01 << 7;

    /* Use frame repeat in the first control field. */
    final int REPEAT_FRAME = 0x00;

    /* Use system broadcast in the first control field. */
    final int SYSTEM_BROADCAST = 0x00;

    /* Bits for normal frame priority (%01) in the first control field of the common EMI frame. */
    final int NORMAL_PRIORITY = 0x01 << 2;

    /* Bit for requesting an ACK (L_Data.req only) for the frame in the first control field. */
    final int REQUEST_ACK = 0x01 << 1;


    //   Control Field 2

    /* Destination Address Type bit for group address in the second control field of the common
     * EMI frame - most significant bit of the byte. */
    final int GROUP_ADDRESS = 0x01 << 7;

    /* Hop count. Default to six. Bits 4 to 6 in the second control field of the cEMI frame. */
    final int HOP_COUNT =  0x06 << 4;

    /* Non-extended frame format in the second control field of the common EMI frame
     *(four zero bits) */
    final int NON_EXTENDED_FRAME_FORMAT = 0x0;


    byte[] destinationAddress = address.asByteArray();
    Byte[] protocolDataUnit = apdu.getProtocolDataUnit();
    int apduDataLength = apdu.getDataLength();

    List<Byte> cemi = new ArrayList<Byte>(11);

    cemi.add((byte)LINK_LAYER_DATA_REQUEST);              // Message Code
    cemi.add((byte)NO_ADDITIONAL_INFORMATION);            // Additional Info Length

    cemi.add(                                             // Control Field 1
        (byte)(STANDARD_FRAME_TYPE +
              REPEAT_FRAME +
              SYSTEM_BROADCAST +
              NORMAL_PRIORITY +
              REQUEST_ACK)
    );

    cemi.add(                                             // Control Field 2
        (byte)(GROUP_ADDRESS +
               HOP_COUNT +
               NON_EXTENDED_FRAME_FORMAT)
    );

    cemi.add((byte)SOURCE_ADDRESS_HIBYTE);                // Source address
    cemi.add((byte)SOURCE_ADDRESS_LOBYTE);

    cemi.add(destinationAddress[0]);                      // Destination address
    cemi.add(destinationAddress[1]);

    cemi.add((byte)apduDataLength);                       // Data Length
    cemi.add(protocolDataUnit[0]);                        // TPCI + APCI high bits
    cemi.add(protocolDataUnit[1]);                        // APCI low bits + data

    if (apduDataLength > 1)
    {
      // Sanity check...

      if (apduDataLength != protocolDataUnit.length - 1)
      {
        throw new Error(); // TODO
      }

      for (int pduIndex = 2; pduIndex < protocolDataUnit.length; ++pduIndex)
      {
        cemi.add(protocolDataUnit[pduIndex]);
      }
    }

    Byte[] cemiBytes = new Byte[cemi.size()];

    return cemi.toArray(cemiBytes);
  }
}
