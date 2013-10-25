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

import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents Data Link layer in KNX communication stack.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
class DataLink
{


  // Constants -----------------------------------------------------------------------------------

  protected final static MessageCode DATA_REQUEST =
      new MessageCode(MessageCode.DATA_REQUEST_BYTE, "L_Data.req");

  protected final static MessageCode DATA_INDICATE =
      new MessageCode(MessageCode.DATA_INDICATE_BYTE, "L_Data.ind");

  protected final static MessageCode DATA_CONFIRM =
      new MessageCode(MessageCode.DATA_CONFIRM_BYTE, "L_Data.con");


  protected final static MessageCode POLL_REQUEST =
      new MessageCode(MessageCode.POLL_REQUEST_BYTE, "L_Poll_Data.req");

  protected final static MessageCode POLL_CONFIRM =
      new MessageCode(MessageCode.POLL_CONFIRM_BYTE, "L_Poll_Data.con");


  protected final static MessageCode RAW_REQUEST =
      new MessageCode(MessageCode.RAW_REQUEST_BYTE, "L_Raw.req");

  protected final static MessageCode RAW_INDICATE =
      new MessageCode(MessageCode.RAW_INDICATE_BYTE, "L_Raw.ind");

  protected final static MessageCode RAW_CONFIRM =
      new MessageCode(MessageCode.RAW_CONFIRM_BYTE, "L_Raw.con");



  // Class Members --------------------------------------------------------------------------------

  /**
   * KNX logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger.getLogger(KNXCommandBuilder.KNX_LOG_CATEGORY);


  // Enums ----------------------------------------------------------------------------------------

  protected static enum Service
  {
    DATA(DATA_REQUEST, DATA_INDICATE, DATA_CONFIRM),
    POLL(POLL_REQUEST, POLL_CONFIRM),
    RAW (RAW_REQUEST,  RAW_INDICATE,  RAW_CONFIRM);



    MessageCode requestCode;
    MessageCode indicateCode;
    MessageCode confirmCode;

    Service(MessageCode req, MessageCode ind, MessageCode con)
    {
      this(req, con);
      this.indicateCode = ind;
    }

    Service(MessageCode req, MessageCode con)
    {
      this.requestCode = req;
      this.confirmCode = con;
    }

    byte getRequestMessageCode()
    {
      return requestCode.getByteValue();
    }
  }


  // Class Members --------------------------------------------------------------------------------

  /**
   * Resolves a message code byte value from a KNX frame to a message code instance.
   *
   * @param value   message code byte value in KNX frame -- should map to one of the service type
   *                identifier constants defined in this class
   *
   * @return        A message code instance matching the frame byte value. If message code
   *                cannot be resolved, returns a special {@link DataLink.UnknownMessageCode
   *                unknown message code} instance (to avoid null pointer exceptions).
   */
  protected static MessageCode resolveMessageCode(int value)
  {
    // IMPLEMENTATION NOTE:
    //
    //   - We are just delegating to message code implementation here -- this is done to ensure
    //     the correct initialization order for the static lookup collection in message code
    //     implementation. The message code constants are defined in this class so we need to
    //     force it to be loaded for the instances to end up in the lookup collection, otherwise
    //     we may resolve from an empty collection.

    return MessageCode.resolve(value);
  }


  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Represents the message code values (service type identifier) mainly used in the CEMI
   * frames in this implementation.
   */
  public static class MessageCode
  {

    // Constants ----------------------------------------------------------------------------------

    protected final static int DATA_REQUEST_BYTE  = 0x11;

    /**
     * @deprecated Use {@link DataLink.MessageCode#resolve(int)} instead.
     *
     * TODO: reduce visibility to protected
     */
    public final static int DATA_INDICATE_BYTE = 0x29;

    /**
     * @deprecated Use {@link DataLink.MessageCode#resolve(int)} instead.
     *
     * TODO: reduce visibility to protected
     */
    public final static int DATA_CONFIRM_BYTE  = 0x2E;

    protected final static int POLL_REQUEST_BYTE  = 0x13;
    protected final static int POLL_CONFIRM_BYTE  = 0x25;

    protected final static int RAW_REQUEST_BYTE   = 0x10;
    protected final static int RAW_INDICATE_BYTE  = 0x2D;
    protected final static int RAW_CONFIRM_BYTE   = 0x2F;


    // Class Members ------------------------------------------------------------------------------

    /**
     * Keep a lookup map of message code instances created by this implementation. Key is the
     * message code byte used in the KNX frame.  <p>
     *
     * Static lookup maps are generally in poor form but in this case it is going to be a fixed
     * set (not dynamic collection) so we might be ok.
     */
    private static Map<Integer, MessageCode> lookup = new ConcurrentHashMap<Integer, MessageCode>(15);

    /**
     * Resolves a message code byte value from a KNX frame to a message code instance.
     *
     * @param value   message code byte value in KNX frame -- should map to one of the service type
     *                identifier constants defined in this class
     *
     * @return        A message code instance matching the frame byte value. If message code
     *                cannot be resolved, returns a special unknown message code instance (to
     *                avoid null pointer exceptions).
     */
    private static MessageCode resolve(int value)
    {
      MessageCode mc = lookup.get(value);

      if (mc == null)
      {
        log.error(
            "Cannot resolve KNX frame message code {0}", Strings.byteToUnsignedHexString((byte)value)
        );

        return new UnknownMessageCode(value);
      }

      else
      {
        return mc;
      }
    }


    // Instance Fields ----------------------------------------------------------------------------

    /**
     * The message code byte value used in KNX frames.
     */
    private int messageCode;

    /**
     * String description of the primitive corresponding to the message code.
     */
    private String primitive;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a new message code instance with a given frame byte code and a string
     * description of the primitive corresponding to the message code.
     *
     * @param messageCode   byte value for the message code used in KNX frames
     * @param primitive     string description of the primitive corresponding to the message code
     */
    private MessageCode(int messageCode, String primitive)
    {
      this.messageCode = messageCode;
      this.primitive = primitive;

      lookup.put(messageCode, this);
    }


    // Instance Methods ---------------------------------------------------------------------------

    /**
     * Returns the byte value of this message code used in KNX frames.
     *
     * @return    frame byte value corresponding to this message code instance
     */
    protected byte getByteValue()
    {
      return (byte)messageCode;
    }

    /**
     * A text description of the primitive this message code represents.
     *
     * @return    a primitive name matching this message code
     */
    protected String getPrimitiveName()
    {
      return primitive;
    }
  }


  /**
   * Special definition for message codes we don't recognize. This avoids return null pointers.
   */
  protected static class UnknownMessageCode extends MessageCode
  {
    protected final static String UNKNOWN_PRIMITIVE = "<Unknown>";

    private UnknownMessageCode(int value)
    {
      super(value, "<Unknown>");
    }
  }
}
