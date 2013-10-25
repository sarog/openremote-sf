/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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

import org.openremote.controller.exception.OpenRemoteException;
import org.openremote.controller.protocol.port.Message;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class CommonEMI extends Message
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The minimum possible size of a CEMI frame with data payload: {@value}
   */
  public final static int MINIMUM_CEMI_FRAME_SIZE = 11;

  /**
   * The byte index of common EMI message code field in a common EMI frame (Note: excluding the
   * KNX frame header bytes) : {@value}
   */
  public final static int CEMI_MESSAGE_CODE_INDEX = 0;

  /**
   * The byte index of additional info length field in common EMI frame (Note: excluding the KNX
   * frame header bytes) : {@value}
   */
  public final static int CEMI_ADDITIONAL_INFO_LENGTH_INDEX = CEMI_MESSAGE_CODE_INDEX + 1;


  /**
   * The byte offset value of control field 1 in common EMI frame (Note: excluding the KNX
   * frame header bytes). If additional info bytes are included in the frame, they
   * must be added to this offset: {@value}
   */
  private final static int CEMI_CONTROL_FIELD1_OFFSET = CEMI_ADDITIONAL_INFO_LENGTH_INDEX + 1;

  /**
   * The byte offset value of control field 2 in common EMI frame (Note: excluding the KNX
   * frame header bytes). If additional info bytes are included in the frame, they
   * must be added to this offset: {@value}
   */
  private final static int CEMI_CONTROL_FIELD2_OFFSET = CEMI_CONTROL_FIELD1_OFFSET + 1;

  /**
   * The byte offset value of source address high byte (most significant byte) in common EMI frame
   * (Note: excluding the KNX frame header bytes). If additional info bytes are included in the
   * frame, they must be added to this offset: {@value}
   */
  private final static int CEMI_SOURCE_ADDRESS_HIBYTE_OFFSET = CEMI_CONTROL_FIELD2_OFFSET + 1;

  /**
   * The byte offset value of source address low byte (least significant byte) in common EMI frame
   * (Note: excluding the KNX frame header bytes). If additional info bytes are included in the
   * frame, they must be added to this offset: {@value}
   */
  private final static int CEMI_SOURCE_ADDRESS_LOBYTE_OFFSET = CEMI_SOURCE_ADDRESS_HIBYTE_OFFSET + 1;

  /**
   * The byte offset value of destination address high byte (most significant byte) in common EMI
   * frame (Note: excluding the KNX frame header bytes). If additional info bytes are included in
   * the frame, they must be added to this offset: {@value}
   */
  private final static int CEMI_DESTINATION_ADDRESS_HIBYTE_OFFSET = CEMI_SOURCE_ADDRESS_LOBYTE_OFFSET + 1;

  /**
   * The byte offset value of source address low byte (least significant byte) in common EMI frame
   * (Note: excluding the KNX frame header bytes). If additional info bytes are included in the
   * frame, they must be added to this offset: {@value}
   */
  private final static int CEMI_DESTINATION_ADDRESS_LOBYTE_OFFSET = CEMI_DESTINATION_ADDRESS_HIBYTE_OFFSET + 1;

  /**
   * The byte offset value of APDU length field in common EMI frame (Note: excluding the KNX frame
   * header bytes). If additional info bytes are included in the frame, they must be added to this
   * offset: {@value}
   */
  private final static int CEMI_APDU_LENGTH_OFFSET = CEMI_DESTINATION_ADDRESS_LOBYTE_OFFSET + 1;


  /**
   * Standard response string from {@link #getFrameError()} when no errors are detected:
   * {@value}
   */
  private final static String VALID_CEMI_FRAME_MESSAGE = "<CEMI FRAME OK>";


  // Enums ----------------------------------------------------------------------------------------


  /**
   * CEMI frame priority bits in the control 1 field. <p>
   *
   * Valid priorities are system (0x00), normal (0x01), urgent (0x02) and low (0x03).
   */
  protected enum Priority
  {
    /**
     * CEMI frame system priority (0x00)
     */
    SYSTEM(0x00),

    /**
     * CEMI frame normal priority (0x01)
     */
    NORMAL(0x01),

    /**
     * CEMI frame urgent priority (0x02)
     */
    URGENT(0x02),

    /**
     * CEMI frame low priority (0x03)
     */
    LOW(0x03);


    /**
     * Resolves a priority bit value (range [0x00...0x03] to a typed priority enum. Note that
     * the bit value from a CEMI control field must be shifted first to the right so that only
     * the three least significant bits are used.
     *
     * @param bitValue    Priority bit value between [0x00...0x03]. When extracting from CEMI
     *                    control field, bit shift to the right by two steps (>> 2) first.
     *
     * @return    a typed priority enum, or an error if the value cannot be resolved to a valid
     *            priority
     */
    private static Priority resolve(int bitValue)
    {
      switch (bitValue)
      {
        case 0x00: return SYSTEM;
        case 0x01: return NORMAL;
        case 0x02: return URGENT;
        case 0x03: return LOW;

        default:

          throw new Error("Unrecognized CEMI frame priority value: " + bitValue);
      }
    }

    private int bitValue;

    private Priority(int bitValue)
    {
      this.bitValue = bitValue;
    }
  }


  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging to KNX log category.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  /**
   * Constructs a default CEMI frame from a given message code, destination address and APDU. <p>
   *
   * The source address in the frame will be set to 0/0/0.  <p>
   *
   * The control bits in the control fields of the frame will be set as follows:
   *
   * <ul>
   *   <li>standard frame type</li>
   *   <li>repeat frame in case of an error</li>
   *   <li>system broadcast</li>
   *   <li>normal priority</li>
   *   <li>no acknowledgement required (see note 1 below)</li>
   *   <li>destination address is group address type</li>
   *   <li>frame hop count is 6</li>
   * </ul>
   *
   * Note 1: We are not requiring acknowledgement on datalink layer for the frame transmission.
   * While this in general would be a good idea for transport reliability, the use of this bit
   * was causing issues with some KNX gateways (Jung IPS 100 REG and Hager). So we are defaulting
   * to turning it off to be on the safe side (an alternative would be to turn this datalink ack
   * off specifically for these gateways that are causing issues but turning it off in general
   * and by default is the easier fix for now). The lack of ACK on datalink layer while desirable
   * is not necessarily causing a complete loss of transport reliability since at the application
   * layer the CEMI frames may be ack'ed.
   *
   *
   * @param messageCode           CEMI frame message code. See {@link DataLink.MessageCode}
   * @param destinationAddress    destination address for this CEMI frame
   * @param apdu                  the APDU payload for this CEMI frame
   *
   * @return   CEMI frame as a byte array, excluding required KNX headers and KNX frame structures
   */
  private static byte[] constructDefaultCEMIFrame(DataLink.MessageCode messageCode,
                                                  GroupAddress destinationAddress,
                                                  ApplicationProtocolDataUnit apdu)
  {
    ControlBits bits = new ControlBits();

    // Control Field 1 bits...

    bits.setStandardFrameType(true);
    bits.setFrameRepeat(true);
    bits.setSystemBroadcast(true);
    bits.setPriority(Priority.NORMAL);

    /*
     * 2011-04-14 OG : We force this bit to 0 (ACK not requested). If set to 1, the KNX/IP interface from Jung (IPS
     * 100 REG) and the Hager as well don't transmit telegrams to the KNX bus, for some unknown reason. Not requesting
     * the ACK is not a big deal as the GroupValue_Write.con telegram sent by the the server acts as an applicative
     * ACK.
     */
    bits.setAcknowledgeRequest(false);


    // Control Field 2 bits...

    bits.useGroupAddressForDestination(true);
    bits.setHopCount(6);


    byte[] struct = new byte[9 + apdu.getProtocolDataUnit().length];

    byte[] destAddrBytes = destinationAddress.asByteArray();

    struct[0] = messageCode.getByteValue();

    struct[1] = 0x00;               // additional info length

    struct[2] = bits.controlField1ToByteValue();
    struct[3] = bits.controlField2ToByteValue();

    struct[4] = 0x00;               // source address
    struct[5] = 0x00;               // source address

    struct[6] = destAddrBytes[0];   // dest address
    struct[7] = destAddrBytes[1];   // dest address

    struct[8] = (byte)(apdu.getDataLength() & 0xFF);

    Byte[] apduBytes = apdu.getProtocolDataUnit();

    for (int i = 0; i < apduBytes.length; i++)
    {
      struct[9 + i] = apduBytes[i];
    }

    return struct;
  }



  // Instance Fields ------------------------------------------------------------------------------


  /**
   * The message code for this CEMI instance. See {@link DataLink.MessageCode}.
   */
  private DataLink.MessageCode msgCode;

  /**
   * The number of additional length bytes included in the CEMI frame, if any. Normally this
   * value is zero (no additional info in the frame).
   */
  private int additionalInfoLength;

  /**
   * The destination address for this CEMI instance.
   *
   * TODO:
   *    Note that destination address could also be an individual address, not necessarily
   *    always a group address. This is determined by the configuration of the flags in the
   *    CEMI control field 2. We don't currently handle the case of individual destination
   *    addresses.
   */
  private GroupAddress destinationAddress;

  /**
   * The source address of this CEMI instance.
   */
  private GroupAddress sourceAddress;

  /**
   * The data payload (APDU) for this CEMI instance.
   */
  private ApplicationProtocolDataUnit apdu;

  /**
   * Control flags for this CEMI instnace. Control flags determine frame behavior such as
   * resend/no resend, priority, acknowledgements, hop count, etc.
   */
  private ControlBits controlBits;




  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new common EMI instance from a given CEMI frame structure. Note that the CEMI
   * frame structure is the stand-alone CEMI, without the KNX frame headers or KNX frame structures. <p>
   *
   * Some basic validation is done on the frame byte array to determine it's correctness. However,
   * these checks may not be comprehensive so frame bytes that would not normally make sense can
   * still get through the validation.
   *
   * @param cemiFrameStructure  Byte array containing the full CEMI frame, excluding the containing
   *                            KNX headers and KNX frame structures.
   *
   * @throws InvalidFrameException    in case the frame appears to be not a valid CEMI frame
   */
  public CommonEMI(byte[] cemiFrameStructure) throws InvalidFrameException
  {
    super(cemiFrameStructure);

    if (!isValidFrame())
    {
      throw new InvalidFrameException(getFrameError());
    }

    // Track the size of additional info length field, if non-zero. We know from the
    // validateCEMIFrame call above that if non-zero, the additional bytes are present
    // in the frame structure...

    additionalInfoLength = getAdditionalInfoLength();

    msgCode = resolveMessageCode();

    int control1 = getContent() [additionalInfoLength + CEMI_CONTROL_FIELD1_OFFSET] & 0xFF;
    int control2 = getContent() [additionalInfoLength + CEMI_CONTROL_FIELD2_OFFSET] & 0xFF;

    controlBits = new ControlBits(control1, control2);

    // TODO : dest. address may be individual address instead of group address depending on control bits

    destinationAddress = parseDestinationAddress();
    sourceAddress = parseSourceAddress();

    // May still throw invalid frame exception if the APDU part looks funky...

    apdu = parseAPDU();
  }

  /**
   * Constructs a new common EMI instance with a given message code, destination group address
   * and data (APDU) payload.  <p>
   *
   * The constructed CEMI instance uses default values for its control field flags and defaults
   * to a source group address of '0/0/0'. <p>
   *
   * The control flags for this CEMI will be set as follows:
   *
   * <ul>
   *   <li>standard frame type</li>
   *   <li>repeat frame in case of an error</li>
   *   <li>system broadcast</li>
   *   <li>normal priority</li>
   *   <li>no acknowledgement required (see note 1 below)</li>
   *   <li>destination address is group address type</li>
   *   <li>frame hop count is 6</li>
   * </ul>
   *
   * Note 1: We are not requiring acknowledgement on datalink layer for the frame transmission.
   * While this in general would be a good idea for transport reliability, the use of this bit
   * was causing issues with some KNX gateways (Jung IPS 100 REG and Hager). So we are defaulting
   * to turning it off to be on the safe side (an alternative would be to turn this datalink ack
   * off specifically for these gateways that are causing issues but turning it off in general
   * and by default is the easier fix for now). The lack of ACK on datalink layer while desirable
   * is not necessarily causing a complete loss of transport reliability since at the application
   * layer the CEMI frames may be ack'ed.
   *
   * @param mc            CEMI message code, see {@link DataLink.MessageCode}.
   * @param destAddress   destination group address
   * @param apdu          data payload
   */
  public CommonEMI(DataLink.MessageCode mc, GroupAddress destAddress, ApplicationProtocolDataUnit apdu)
  {
    super(constructDefaultCEMIFrame(mc, destAddress, apdu));

    try
    {
      this.msgCode = mc;
      this.additionalInfoLength = 0;

      this.controlBits = new ControlBits(
          getContent() [CEMI_CONTROL_FIELD1_OFFSET],
          getContent() [CEMI_CONTROL_FIELD2_OFFSET]
      );

      this.sourceAddress = new GroupAddress("0/0/0");
      this.destinationAddress = destAddress;
      this.apdu = apdu;
    }

    catch (InvalidGroupAddressException e)
    {
      throw new Error("Unable to create source address '0/0/0': " + e.getMessage());
    }

    catch (InvalidFrameException e)
    {
      throw new Error(
          "Implementation Error: created invalid CEMI control fields. Msg: " + e.getMessage()
      );
    }
  }


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Returns the destination group address of this CEMI instance.
   *
   * @return
   */
  protected GroupAddress getDestinationAddress()
  {
    // TODO
    //   Note that destination address could also be an individual address, not necessarily
    //   always a group address. This is determined by the configuration of the flags in the
    //   CEMI control field 2. We don't currently handle the case of individual destination
    //   addresses.

    return destinationAddress;
  }

  /**
   * Returns the source group address of this CEMI instance.
   *
   * @return    KNX source group address
   */
  protected GroupAddress getSourceAddress()
  {
    return sourceAddress;
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Helper method to parse a destination group address from a CEMI frame byte array.
   *
   * @return    destination group address
   */
  private GroupAddress parseDestinationAddress()
  {
    // TODO
    //   Note that destination address could also be an individual address, not necessarily
    //   always a group address. This is determined by the configuration of the flags in the
    //   CEMI control field 2. We don't currently handle the case of individual destination
    //   addresses.


    // Adjust offsets with additional info bytes, if any...

    final int CEMI_DESTINATION_ADDRESS_HIBYTE_INDEX =
        CEMI_DESTINATION_ADDRESS_HIBYTE_OFFSET + additionalInfoLength;

    final int CEMI_DESTINATION_ADDRESS_LOBYTE_INDEX =
        CEMI_DESTINATION_ADDRESS_LOBYTE_OFFSET + additionalInfoLength;

    byte[] frame = getContent();

    int destAddrHiByte = frame [CEMI_DESTINATION_ADDRESS_HIBYTE_INDEX] & 0xFF;
    int destAddrLoByte = frame [CEMI_DESTINATION_ADDRESS_LOBYTE_INDEX] & 0xFF;

    return new GroupAddress((byte)destAddrHiByte, (byte)destAddrLoByte);
  }

  /**
   * Helper method to parse a source group address from a CEMI frame byte array.
   *
   * @return    KNX source group address
   */
  private GroupAddress parseSourceAddress()
  {
    // Adjust offsets with additional info bytes, if any...

    final int CEMI_SOURCE_ADDRESS_HIBYTE_INDEX =
        CEMI_SOURCE_ADDRESS_HIBYTE_OFFSET + additionalInfoLength;

    final int CEMI_SOURCE_ADDRESS_LOBYTE_INDEX =
        CEMI_SOURCE_ADDRESS_LOBYTE_OFFSET + additionalInfoLength;

    byte[] frame = getContent();

    int destAddrHiByte = frame [CEMI_SOURCE_ADDRESS_HIBYTE_INDEX] & 0xFF;
    int destAddrLoByte = frame [CEMI_SOURCE_ADDRESS_LOBYTE_INDEX] & 0xFF;

    return new GroupAddress((byte)destAddrHiByte, (byte)destAddrLoByte);
  }


  /**
   * Utility method to parse the APDU instance from a CEMI frame byte array.
   *
   * @return    APDU instance
   *
   * @see ApplicationProtocolDataUnit
   *
   * @throws InvalidFrameException    in case of a frame error
   */
  private ApplicationProtocolDataUnit parseAPDU() throws InvalidFrameException
  {
    // Adjust offsets with additional info bytes, if any...

    final int CEMI_APDU_DATA_LENGTH_INDEX =  CEMI_APDU_LENGTH_OFFSET + additionalInfoLength;

    // Retrive the CEMI data length field value...

    int apduDataLength = getContent() [CEMI_APDU_DATA_LENGTH_INDEX] & 0xFF;

    // Zero data length field value would be a frame error...

    if (apduDataLength == 0)
    {
      throw new InvalidFrameException(
          "CEMI data length is zero in frame : {0}", Strings.byteArrayToUnsignedHexString(getContent())
      );
    }

    // Allocate and copy the APDU structure -- this includes the TCPI/APCI byte (hence +1 to
    // apdu length value), the APCI/Data byte (only used with 6 bit payloads) and the full
    // data (above 6 bit value length) payload, up to 14 bytes.

    byte[] apdu = new byte[apduDataLength + 1];
    System.arraycopy(getContent(), CEMI_APDU_DATA_LENGTH_INDEX + 1, apdu, 0, apduDataLength);

    return ApplicationProtocolDataUnit.constructKNXFrameAPDU(apdu, apduDataLength);
  }


  /**
   * Does some basic validations on the frame bytes, such as recognized message code value,
   * correctly reported frame length values, etc.
   *
   * @return    true if the CEMI byte array appears valid, false otherwise
   */
  private boolean isValidFrame()
  {
    return (getFrameError().equals(VALID_CEMI_FRAME_MESSAGE));
  }

  /**
   * Returns a detailed description of an error in CEMI frame.
   *
   * @return  a descriptive error message why the CEMI frame is not valid
   */
  private String getFrameError()
  {
    DataLink.MessageCode mc = resolveMessageCode();

    if (mc == null)
    {
      return "Unrecognized CEMI message code in frame " +
             Strings.byteArrayToUnsignedHexString(getContent());
    }

    int additionalInfoLength = getAdditionalInfoLength();

    if (getContent().length < MINIMUM_CEMI_FRAME_SIZE + additionalInfoLength)
    {
      return "Expected CEMI frame total length of " + MINIMUM_CEMI_FRAME_SIZE + additionalInfoLength +
             " bytes (additional info length " + additionalInfoLength + " bytes) but " +
              "received " + getContent().length + " bytes instead.";
    }

    return VALID_CEMI_FRAME_MESSAGE;
  }


  /**
   * This utility method parses the additional info length field from the CEMI frame byte array.
   * The additional info length field is expected at index {@link #CEMI_ADDITIONAL_INFO_LENGTH_INDEX}.
   * Note: <b>if</b> additional info length has a non-zero value, the following field values in
   * the CEMI frame should be calculated by adding this additional info length value to the default
   * CEMI frame field offset values, starting from {@link #CEMI_CONTROL_FIELD1_OFFSET}.
   *
   * @return      the length value at index {@link #CEMI_ADDITIONAL_INFO_LENGTH_INDEX} of the
   *              given CEMI frame byte array
   */
  private int getAdditionalInfoLength()
  {
    return getContent() [CEMI_ADDITIONAL_INFO_LENGTH_INDEX] & 0xFF;
  }


  /**
   * This utility method parses the CEMI message code from the CEMI frame byte array.
   * The message code byte is expected at index {@link #CEMI_MESSAGE_CODE_INDEX}.
   *
   * @return      A message code instance matching the message code in the given frame
   */
  private DataLink.MessageCode resolveMessageCode()
  {
    int messageCode = getContent() [CEMI_MESSAGE_CODE_INDEX] & 0xFF;

    return DataLink.MessageCode.resolve(messageCode);
  }


  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Represents the two control fields in a CEMI frame (one byte each):
   *
   * <pre>
   *   Control Field 1
   *
   *          Bit  |
   *         ------+---------------------------------------------------------------
   *           7   | Frame Type  - 0x0 for extended frame
   *               |               0x1 for standard frame
   *         ------+---------------------------------------------------------------
   *           6   | Reserved
   *               |
   *         ------+---------------------------------------------------------------
   *           5   | Repeat Flag - 0x0 repeat frame on medium in case of an error
   *               |               0x1 do not repeat
   *         ------+---------------------------------------------------------------
   *           4   | System Broadcast - 0x0 system broadcast
   *               |                    0x1 broadcast
   *         ------+---------------------------------------------------------------
   *           3   | Priority    - 0x0 system
   *               |               0x1 normal
   *         ------+               0x2 urgent
   *           2   |               0x3 low
   *               |
   *         ------+---------------------------------------------------------------
   *           1   | Acknowledge Request - 0x0 no ACK requested
   *               | (L_Data.req)          0x1 ACK requested
   *         ------+---------------------------------------------------------------
   *           0   | Confirm      - 0x0 no error
   *               | (L_Data.con) - 0x1 error
   *         ------+---------------------------------------------------------------
   *
   *
   *   Control Field 2
   *
   *          Bit  |
   *         ------+---------------------------------------------------------------
   *           7   | Destination Address Type - 0x0 individual address
   *               |                          - 0x1 group address
   *         ------+---------------------------------------------------------------
   *          6-4  | Hop Count (0-7)
   *         ------+---------------------------------------------------------------
   *          3-0  | Extended Frame Format - 0x0 standard frame
   *         ------+---------------------------------------------------------------
   * </pre>
   */
  protected static class ControlBits
  {

    // Constants ----------------------------------------------------------------------------------

    /**
     * The least significant bit placement of CEMI frame control field 2 hop count value in the
     * control 2 field byte.
     */
    protected final static int CEMI_CONTROL_FIELD2_HOPCOUNT_BIT_POSITION = 4;

    /**
     * The least significant bit placement of CEMI frame control field 1 priority value in the
     * control 1 field byte.
     */
    protected final static int CEMI_CONTROL_FIELD1_PRIORITY_BIT_POSITION = 2;

    /**
     * Bit mask to extract the two-bit priority value from control field 1.
     */
    protected final static int CEMI_CONTROL_FIELD1_PRIORITY_BITMASK =
        0x03 << CEMI_CONTROL_FIELD1_PRIORITY_BIT_POSITION;

    /**
     * Bit mask to extract the three bit hop count value from control field 2.
     */
    protected final static int CEMI_CONTROL_FIELD2_HOPCOUNT_BITMASK =
        0x07 << CEMI_CONTROL_FIELD2_HOPCOUNT_BIT_POSITION;


    /**
     *  A bit flag for standard common EMI frame type (not extended) in the first control field.
     *  Placed in the most significant bit in the control field 1 (bit shift left by seven bits)
     */
    private final static int CEMI_CONTROL_FIELD1_STANDARD_FRAME_TYPE = 0x01 << 7;

    /**
     *  A bit flag for extended common EMI frame type (non-standard) in the first control field.
     *  Placed in the most significant bit in the control field 1 (bit shift left by seven bits)
     */
    private final static int CEMI_CONTROL_FIELD1_EXTENDED_FRAME_TYPE = 0x00 << 7;

    /**
     *  A bit flag for not repeating frame transmit in case of error, used in first control field.
     *  Bit shifted left by five bits for the correct bit location.
     */
    private final static int CEMI_CONTROL_FIELD1_DO_NOT_REPEAT_FRAME = 0x01 << 5;

    /**
     *  A bit flag for frame repeat in case of transmit errors, used in first control field.
     *  Bit shifted left by five bits for the correct bit location.
     */
    private final static int CEMI_CONTROL_FIELD1_REPEAT_FRAME = 0x00 << 5;

    /**
     *  A bit flag for broadcast frame used in the first control field. Bit shifted left by four
     *  bits for the correct bit location.
     */
    private final static int CEMI_CONTROL_FIELD1_BROADCAST_FRAME = 0x01 << 4;

    /**
     *  A bit flag for system broadcast frame used in the first control field. Bit shifted left
     *  by four bits for the correct bit location.
     */
    private final static int CEMI_CONTROL_FIELD1_SYSTEM_BROADCAST_FRAME = 0x00 << 4;

    /**
     *  A bit flag for requesting datalink layer acknowledgement on frame transmit, used in the
     *  first control field. Bit shifted left by one bit for the correct bit location.
     */
    private final static int CEMI_CONTROL_FIELD1_REQUEST_ACK = 0x01 << 1;

    /**
     *  A bit flag for not requesting datalink layer acknowledgement on frame transmit, used in the
     *  first control field. Bit shifted left by one bit for the correct bit location.
     */
    private final static int CEMI_CONTROL_FIELD1_DO_NOT_REQUEST_ACK = 0x00 << 1;



    /**
     *  A bit flag for destination address type in the second control field of the CEMI frame -
     *  most significant bit of the byte. This flag indicates a group address type of destination.
     *  Bit shifted left by seven bits for the appropriate bit position in the control field.
     */
    private final static int CEMI_CONTROL_FIELD2_GROUP_ADDRESS_DESTINATION = 0x01 << 7;

    /**
     *  A bit flag for individual address type in the second control field of the CEMI frame -
     *  most significant bit of the byte. This flag indicates an individual address type of
     *  destination. Bit shifted left by sevent bits for the appropriate bit position in the
     *  control field.
     */
    private final static int CEMI_CONTROL_FIELD2_INDIVIDUAL_ADDRESS_DESTINATION = 0x00 << 7;



    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Indicates if the CEMI frame is of standard type. Defaults to: {@value}
     */
    private boolean isStandardFrameType = true;

    /**
     * Indicates if the CEMI frame is of extended type. Defaults to: {@value}
     */
    private boolean isExtendedFrameType = false;

    /**
     * Indicates whether frame transmission should be repeated in case of a transmission error.
     * Defaults to: {@value}
     */
    private boolean frameRepeat = true;

    /**
     * Indicates if a frame is transmitted as a system broadcast frame. Defaults to: {@value}
     */
    private boolean isSystemBroadcast = true;

    /**
     * Indicates if a frame is transmitted as a broadcast frame. Defaults to: {@value}
     */
    private boolean isBroadcast = false;

    /**
     * Indicates if datalink layer level acknowledgement is requested in frame transmission.
     * Defaults to: {@value}
     *
     * NOTE: Defaulting to false here. While in general would be a good idea to use true for
     * transport reliability, the use of this bit was causing issues with some KNX gateways
     * (Jung IPS 100 REG and Hager). So we are defaulting to turning it off to be on the safe side --
     * an alternative would be to turn this datalink ack off specifically for the gateways causing
     * issues but turning it off in general and by default is the easier fix for now. The lack of
     * ACK on datalink layer while desirable is not necessarily causing a complete loss of
     * transport reliability since at the application layer the CEMI frames may be ack'ed.
     */
    private boolean acknowledgeRequest = false;

    /**
     * The CEMI frame transmission priority. Defaults to: {@value}
     */
    private Priority priority = Priority.NORMAL;

    /**
     * Indicates if the destination address of the CEMI frame is a group address type.
     * Defaults to: {@value}
     */
    private boolean useGroupAddressForDestination = true;

    /**
     * Indicates if the destination address of the CEMI frame is an individual address type.
     * Defaults to: {@value}
     */
    private boolean useIndividualAddressForDestination = false;

    /**
     * The CEMI frame hop count. Defaults to: {@value}
     */
    private int hopCount = 6;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs CEMI control field bits for both fields with default flag values:
     *
     * <ul>
     *   <li>Standard frame type</li>
     *   <li>Repeat frames in case of transmission errors</li>
     *   <li>System broadcast frame</li>
     *   <li>Frames are not ack'ed at datalink layer</li>
     *   <li>Priority is normal</li>
     *   <li>Destination address type is a group address</li>
     *   <li>Hop count is six</li>
     * </ul>
     */
    protected ControlBits() {}

    /**
     * Constructs CEMI control field bits from both fields by parsing the given byte field values.
     *
     * @param control1    the first control field byte of a CEMI frame
     * @param control2    the second control field byte of a CEMI frame
     *
     * @throws InvalidFrameException    if the control fields cannot be parsed
     */
    protected ControlBits(int control1, int control2) throws InvalidFrameException
    {
      // mask out the most significant bits, just keep the lowest eight...

      control1 = control1 & 0xFF;
      control2 = control2 & 0xFF;

      isStandardFrameType = parseFrameTypeFlag(control1);
      isExtendedFrameType = !isStandardFrameType;

      frameRepeat = parseFrameRepeatFlag(control1);

      isSystemBroadcast = parseSystemBroadcastFlag(control1);
      isBroadcast = !isSystemBroadcast;

      priority = parsePriority(control1);
      acknowledgeRequest = parseAcknowledgementBitFlag(control1);

      useGroupAddressForDestination = parseGroupAddressforDestinationFlag(control2);
      useIndividualAddressForDestination = !useGroupAddressForDestination;

      hopCount = parseHopCount(control2);

      // TODO : control field 1 confirmation flag
    }


    // Protected Instance Methods -----------------------------------------------------------------

    /**
     * Converts the bit flags in this instance to a CEMI frame control field 1 byte.
     *
     * @return    byte for control field 1 in CEMI frame
     */
    protected byte controlField1ToByteValue()
    {
      int bits = 0x00;

      if (isStandardFrameType())
      {
        bits += CEMI_CONTROL_FIELD1_STANDARD_FRAME_TYPE;
      }

      if (isExtendedFrameType())
      {
        bits += CEMI_CONTROL_FIELD1_EXTENDED_FRAME_TYPE;
      }

      if (hasFrameRepeatEnabled())
      {
        bits += CEMI_CONTROL_FIELD1_REPEAT_FRAME;
      }

      else
      {
        bits += CEMI_CONTROL_FIELD1_DO_NOT_REPEAT_FRAME;
      }

      if (isSystemBroadcast())
      {
        bits += CEMI_CONTROL_FIELD1_SYSTEM_BROADCAST_FRAME;
      }

      if (isBroadcast())
      {
        bits += CEMI_CONTROL_FIELD1_BROADCAST_FRAME;
      }

      bits += (getPriority().bitValue << 2);

      if (hasAcknowledgeEnabled())
      {
        bits += CEMI_CONTROL_FIELD1_REQUEST_ACK;
      }

      else
      {
        bits += CEMI_CONTROL_FIELD1_DO_NOT_REQUEST_ACK;
      }

      return (byte)bits;
    }

    /**
     * Converts the bit flags in this instance to a CEMI frame control field 2 byte.
     *
     * @return    byte for control field 2 in CEMI frame
     */
    protected byte controlField2ToByteValue()
    {
      int bits = 0x00;

      if (useGroupAddressForDestination)
      {
        bits += CEMI_CONTROL_FIELD2_GROUP_ADDRESS_DESTINATION;
      }

      if (useIndividualAddressForDestination)
      {
        bits += CEMI_CONTROL_FIELD2_INDIVIDUAL_ADDRESS_DESTINATION;
      }

      bits += (hopCount << 4);

      return (byte)bits;
    }

    /**
     * Sets the hop count for CEMI frame. Value range is [0..7].
     *
     * @param hopCount    value for hop count between zero and seven
     */
    protected void setHopCount(int hopCount)
    {
      if (hopCount < 0 || hopCount > 7)
      {
        throw new IllegalArgumentException(
            "CEMI frame hop count must be a value between 0..7, got " + hopCount
        );
      }

      this.hopCount = hopCount;
    }

    /**
     * Indicates if a flag for standard frame type has been set for this control field.
     *
     * @return    true if CEMI frame control field value will have standard frame type flag set;
     *            false otherwise
     */
    protected boolean isStandardFrameType()
    {
      return isStandardFrameType;
    }

    /**
     * Indicates if a flag for an extended frame type has been set for this control field.
     *
     * @return    true if CEMI frame control field value will have extended frame type flag set;
     *            false otherwise
     */
    protected boolean isExtendedFrameType()
    {
      return isExtendedFrameType;
    }

    /**
     * Indicates if a flag for frame repeat has been set for this control field.
     *
     * @return    true if CEMI frame control field value will have frame repeat flag set; false
     *            otherwise
     */
    protected boolean hasFrameRepeatEnabled()
    {
      return frameRepeat;
    }

    /**
     * Indicates if a flag for system broadcast has been set for this control field.
     *
     * @return    true if CEMI frame control field value will have system broadcast flag set;
     *            false otherwise
     */
    protected boolean isSystemBroadcast()
    {
      return isSystemBroadcast;
    }

    /**
     * Indicates if a flag for broadcast has been set for this control field.
     *
     * @return    true if CEMI frame control field value will have broadcast flag set; false
     *            otherwise
     */
    protected boolean isBroadcast()
    {
      return isBroadcast;
    }

    /**
     * Indicates if the priority in this control field has been set to {@link Priority#NORMAL}.
     *
     * @return  true if CEMI frame control field has priority NORMAL; false otherwise
     */
    protected boolean isNormalPriority()
    {
      return priority == Priority.NORMAL;
    }

    /**
     * Indicates if the priority in this control field has been set to {@link Priority#LOW}.
     *
     * @return  true if CEMI frame control field has priority LOW; false otherwise
     */
    protected boolean isLowPriority()
    {
      return priority == Priority.LOW;
    }

    /**
     * Indicates if the priority in this control field has been set to {@link Priority#URGENT}.
     *
     * @return  true if CEMI frame control field has priority URGENT; false otherwise
     */
    protected boolean isUrgentPriority()
    {
      return priority == Priority.URGENT;
    }

    /**
     * Indicates if the priority in this control field has been set to {@link Priority#SYSTEM}.
     *
     * @return  true if CEMI frame control field has priority SYSTEM; false otherwise
     */
    protected boolean isSystemPriority()
    {
      return priority == Priority.SYSTEM;
    }


    /**
     * Returns the {@link Priority} value in this control field.
     *
     * @return  CEMI frame priority
     */
    protected Priority getPriority()
    {
      return priority;
    }


    /**
     * Indicates if a datalink acknowledge frame transmit flag has been set in this control field.
     *
     * @return    true if CEMI frame control field value will have acknowledge flag set;
     *            false otherwise
     */
    protected boolean hasAcknowledgeEnabled()
    {
      return acknowledgeRequest;
    }

    /**
     * Indicates if CEMI frame destination address should be interpreted as a group address, not
     * as an individual address.
     *
     * @return    true if CEMI destination address is a group address; false otherwise
     */
    protected boolean useGroupAddressForDestination()
    {
      return useGroupAddressForDestination;
    }

    /**
     * Returns the hop count in this control field. The valid value range is [0..7].
     *
     * @return    hop count for a CEMI frame
     */
    protected int getHopCount()
    {
      return hopCount;
    }

    /**
     * Sets the bit flag for standard frame type in this control field. Setting the flag for
     * standard frame type will automatically exclude flag for extended frame type.
     *
     * @param stdFrameType    true to set frame type to standard; false to set frame type to
     *                        extended
     */
    protected void setStandardFrameType(boolean stdFrameType)
    {
      isStandardFrameType = stdFrameType;
      isExtendedFrameType = !isStandardFrameType;
    }

    /**
     * Sets the bit flag for extended frame type in this control field. Setting the flag for
     * extended frame type will automatically exclude flag for standard frame type.
     *
     * @param extFrameType  true to set frame type to extended; false to set frame type to
     *                      standard
     */
    protected void setExtendedFrameType(boolean extFrameType)
    {
      isExtendedFrameType = extFrameType;
      isStandardFrameType = !isExtendedFrameType;
    }

    /**
     * Sets the bit flag for frame repeat in case of errors in this control field.
     *
     * @param b     true to enable frame repeat in CEMI frame, false to disable it
     */
    protected void setFrameRepeat(boolean b)
    {
      frameRepeat = b;
    }

    /**
     * Sets the bit flag for system broadcast frame in this control field. Setting the flag for
     * system broadcast will automatically exclude flag for broadcast frame.
     *
     * @param systemBroadcast   true to set system broadcast frame flag; false to set broadcast
     *                          frame flag
     */
    protected void setSystemBroadcast(boolean systemBroadcast)
    {
      isSystemBroadcast = systemBroadcast;
      isBroadcast = !isSystemBroadcast;
    }

    /**
     * Sets the bit flag for broadcast frame in this control field. Setting the flag for broadcast
     * will automatically exclude flag for system broadcast frame.
     *
     * @param broadcast         true to set broadcast framel flag; false to set system broadcast
     *                          frame flag
     */
    protected void setBroadcast(boolean broadcast)
    {
      isBroadcast = broadcast;
      isSystemBroadcast = !isBroadcast;
    }

    /**
     * Sets the {@link Priority priority} for a CEMI frame in this control field.
     *
     * @param pri   CEMI frame priority
     */
    protected void setPriority(Priority pri)
    {
      this.priority = pri;
    }

    /**
     * Sets the bit flag for datalink acknowledgement in this control field.
     *
     * @param b     true to enable datalink layer acknowledgements, false to disable them
     */
    protected void setAcknowledgeRequest(boolean b)
    {
      acknowledgeRequest = b;
    }

    /**
     * Set the bit flag that indicates the destination address in the CEMI frame should
     * be treated as a group address.
     *
     * @param useGroupAddress   true to use group addresses for destination field in CEMI frame;
     *                          false to use individual addresses instead
     */
    protected void useGroupAddressForDestination(boolean useGroupAddress)
    {
      useGroupAddressForDestination = useGroupAddress;
      useIndividualAddressForDestination = !useGroupAddressForDestination;
    }



    // Private Instance Methods -------------------------------------------------------------------

    /**
     * This is a helper method to parse the frame repeat bit flag from the control field 1 byte
     * in a CEMI frame
     *
     * @param controlField1     the value of the control field 1 in CEMI frame
     *
     * @return    true if frame repeat flag has been set, false otherwise
     */
    private boolean parseFrameRepeatFlag(int controlField1)
    {
      return (controlField1 & CEMI_CONTROL_FIELD1_DO_NOT_REPEAT_FRAME) !=
          CEMI_CONTROL_FIELD1_DO_NOT_REPEAT_FRAME;
    }

    /**
     * This is a helper method to parse the standard vs extended frame bit flag from the control
     * field 1 byte in the CEMI frame.
     *
     * @param controlField1     the value of the control field 1 in CEMI frame
     *
     * @return    true if standard frame type has been set; false if extended frame type should
     *            be used
     */
    private boolean parseFrameTypeFlag(int controlField1)
    {
      return (controlField1 & CEMI_CONTROL_FIELD1_STANDARD_FRAME_TYPE) ==
          CEMI_CONTROL_FIELD1_STANDARD_FRAME_TYPE;
    }

    /**
     * This is a helper method to parse the system broadcast bit flag from the control field 1
     * byte in the CEMI frame.
     *
     * @param controlField1     the value of the control field 1 in CEMI frame
     *
     * @return    true if system broadcast flag has been set; false if broadcast should be used
     *            instead
     */
    private boolean parseSystemBroadcastFlag(int controlField1)
    {
      return (controlField1 & CEMI_CONTROL_FIELD1_BROADCAST_FRAME) !=
          CEMI_CONTROL_FIELD1_BROADCAST_FRAME;
    }

    /**
     * This is a helper method to parse priority from the control field 1 byte in the CEMI frame.
     *
     * @param controlField1   the value of the control field 1 in CEMI frame
     *
     * @return  CEMI frame priority
     */
    private Priority parsePriority(int controlField1)
    {
      return Priority.resolve(
          (controlField1 & CEMI_CONTROL_FIELD1_PRIORITY_BITMASK)
              >> CEMI_CONTROL_FIELD1_PRIORITY_BIT_POSITION
      );
    }

    /**
     * This is a helper method to parse CEMI frame hop count from the control field *2* byte.
     *
     * @param controlField2   the value of the control field *2* in CEMI frame
     *
     * @return    the maximum number of hops for the CEMI frame in a KNX network
     */
    private int parseHopCount(int controlField2)
    {
      return (controlField2 & CEMI_CONTROL_FIELD2_HOPCOUNT_BITMASK)
          >> CEMI_CONTROL_FIELD2_HOPCOUNT_BIT_POSITION;
    }

    /**
     * This is a helper method to parse datalink frame acknowledgement bit flag from the control
     * field 1 byte.
     *
     * @param controlField1   the value of the control field 1 in CEMI frame
     *
     * @return    true if datalink layer frame acknowledgement flag has been set
     */
    private boolean parseAcknowledgementBitFlag(int controlField1)
    {
      return (controlField1 & CEMI_CONTROL_FIELD1_REQUEST_ACK) == CEMI_CONTROL_FIELD1_REQUEST_ACK;
    }

    /**
     * This is a helper method to parse bit flag from the control field *2* byte that determines
     * whether an individual or group address is used for the CEMI frame destination.
     *
     * @param controlField2   the value of the control field 2 in CEMI frame
     *
     * @return    true if the group address flag has been set, false if individual address
     *            should be used
     */
    private boolean parseGroupAddressforDestinationFlag(int controlField2)
    {
      return (controlField2 & CEMI_CONTROL_FIELD2_GROUP_ADDRESS_DESTINATION) ==
          CEMI_CONTROL_FIELD2_GROUP_ADDRESS_DESTINATION;
    }
  }


  /**
   * Exception type to throw whenever there's an error in parsing the CEMI frame.
   */
  public static class InvalidFrameException extends OpenRemoteException
  {

    /**
     * Constructs a new invalid frame exception with a given message
     *
     * @param msg   exception message
     */
    protected InvalidFrameException(String msg)
    {
      super(msg);
    }

    /**
     * Constructs a new invalid frame exception with a given message and message parameters
     *
     * @param msg       exception message
     * @param params    exception message parameters
     */
    protected InvalidFrameException(String msg, Object... params)
    {
      super(msg, params);
    }
  }
}


