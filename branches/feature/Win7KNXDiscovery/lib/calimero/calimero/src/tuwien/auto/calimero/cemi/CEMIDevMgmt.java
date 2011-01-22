/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package tuwien.auto.calimero.cemi;

import java.io.ByteArrayInputStream;
import java.util.BitSet;

import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 * A cEMI device management message.
 * <p>
 * Function properties are not supported for the time being.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class CEMIDevMgmt implements CEMI
{
	// ??? implement function properties

	/**
	 * Message code for property read request, code = {@value #MC_PROPREAD_REQ}.
	 * <p>
	 */
	public static final short MC_PROPREAD_REQ = 0xFC;
	/**
	 * Message code for property read confirmation, code = {@value #MC_PROPREAD_CON}.
	 * <p>
	 */
	public static final short MC_PROPREAD_CON = 0xFB;
	/**
	 * Message code for property write request, code = {@value #MC_PROPWRITE_REQ}.
	 * <p>
	 */
	public static final short MC_PROPWRITE_REQ = 0xF6;
	/**
	 * Message code for property write confirmation, code = {@value #MC_PROPWRITE_CON}.
	 * <p>
	 */
	public static final short MC_PROPWRITE_CON = 0xF5;
	/**
	 * Message code for property info indication, code = {@value #MC_PROPINFO_IND}.
	 * <p>
	 */
	public static final short MC_PROPINFO_IND = 0xF7;
	/**
	 * Message code for property reset request, code = {@value #MC_RESET_REQ}.
	 * <p>
	 */
	public static final short MC_RESET_REQ = 0xF1;
	/**
	 * Message code for property reset indication, code = {@value #MC_RESET_IND}.
	 * <p>
	 */
	public static final short MC_RESET_IND = 0xF0;

	// mandatory properties for server objects

	/**
	 * Property for media types supported by cEMI server (read only), PID =
	 * {@value #PID_MEDIUM_TYPE}.
	 * <p>
	 */
	// public static final short PID_MEDIUM_TYPE = 51;
	// optional properties for server objects
	/**
	 * Property for the domain address used on PL medium (read / write), PID =
	 * {@value #PID_DOMAIN_ADDRESS}.
	 * <p>
	 */
	// public static final short PID_DOMAIN_ADDRESS = 70;
	/**
	 * Property for list of interface objects (read / write), PID = {@value #PID_IO_LIST}.
	 * <p>
	 */
	// public static final short PID_IO_LIST = 71;
	/**
	 * Property for communication mode (read / write), PID = {@value #PID_COMM_MODE}.
	 * <p>
	 */
	// public static final short PID_COMM_MODE = 52;
	/**
	 * Property for time base in extended relative timestamps (read only), PID =
	 * {@value #PID_TIMEBASE}.
	 * <p>
	 */
	// public static final short PID_TIMEBASE = 55;
	/**
	 * Property for transparency mode (read / write), PID = {@value #PID_TRANSP_ENABLE}.
	 * <p>
	 */
	// public static final short PID_TRANSP_ENABLE = 56;
	
	
	private static BitSet msgCodes;
	private static final int MC_OFFSET = 0xF0;

	private static final byte[] empty = new byte[0];

	private static final String[] errors = new String[] {
		"unspecified Error (unknown error)",
		"out of Range (write value not allowed)",
		"out of MaxRange (write value too high)",
		"out of MinRange (write value too low)",
		"memory Error (memory can not be written or only with faults)",
		"read Only (write access to a read-only or write-protected property)",
		"illegal Command (command not valid or not supported)",
		"void DP (read or write access to a nonexisting property)",
		"type Conflict (write access with a wrong data type (datapoint length))",
		"prop. Index/Range Error (read or write access to a nonexisting property index)", };

	private short mc;
	private int iot;
	private short oi;
	private short pid;
	private byte elems;
	private short start;
	private byte[] data = empty;
	private int header = 1;

	static {
		msgCodes = new BitSet(20);
		msgCodes.set(MC_PROPREAD_REQ - MC_OFFSET);
		msgCodes.set(MC_PROPREAD_CON - MC_OFFSET);
		msgCodes.set(MC_PROPWRITE_REQ - MC_OFFSET);
		msgCodes.set(MC_PROPWRITE_CON - MC_OFFSET);
		msgCodes.set(MC_PROPINFO_IND - MC_OFFSET);
		msgCodes.set(MC_RESET_REQ - MC_OFFSET);
		msgCodes.set(MC_RESET_IND - MC_OFFSET);
	}

	/**
	 * Creates a new device management message from a byte stream.
	 * <p>
	 * 
	 * @param data byte stream containing a cEMI device management message
	 * @param offset start offset of cEMI frame in <code>data</code>
	 * @param length length in bytes of the whole device management message
	 * @throws KNXFormatException if no device management frame found or invalid frame
	 *         structure
	 */
	public CEMIDevMgmt(byte[] data, int offset, int length) throws KNXFormatException
	{
		final ByteArrayInputStream is = new ByteArrayInputStream(data, offset, length);
		checkLength(is, 1);
		try {
			checkSetMC((short) is.read());
		}
		catch (final KNXIllegalArgumentException e) {
			throw new KNXFormatException(e.getMessage());
		}
		if (mc == MC_RESET_REQ || mc == MC_RESET_IND)
			initReset(is);
		else {
			initHeader(is);
			initBody(is);
		}
	}

	/**
	 * Creates a new device management message with the given message code.
	 * <p>
	 * The message structure (and resulting frame) will only consist of the message code
	 * field. All other device management methods are not used (and will consequently
	 * return 0 or empty fields by default).
	 * <p>
	 * Used for reset messages.
	 * 
	 * @param msgCode a message code value specified by this class
	 */
	public CEMIDevMgmt(int msgCode)
	{
		checkSetMC(msgCode);
	}

	/**
	 * Creates a new device management message.
	 * <p>
	 * Used for messages without a data (or error information) part in the message
	 * structure (like read request).
	 * 
	 * @param msgCode a message code constant declared by this class
	 * @param objType interface object type, value in the range 0 &lt;= value &lt;= 0xFFFF
	 * @param objInstance object instance, value in the range 1 &lt;= value &lt;= 0xFF
	 * @param propID property identifier (PID), in the range 0 &lt;= PID &lt;= 0xFF
	 * @param startIndex start index into the property array, first element value has
	 *        index 1, index 0 in the property contains the current number of valid
	 *        elements (read only)
	 * @param elements number of elements in the array of the property, in the range 1
	 *        &lt;= elements &lt;= 0xFF;
	 */
	public CEMIDevMgmt(int msgCode, int objType, int objInstance, int propID,
		int startIndex, int elements)
	{
		this(msgCode);
		header = 7;
		checkSetHeaderInfo(objType, objInstance, propID, startIndex, elements);
	}

	/**
	 * Creates a new device management message.
	 * <p>
	 * Used for messages containing a data (or error information) part in the message
	 * structure (like read confirmation).
	 * 
	 * @param msgCode a message code constant declared by this class
	 * @param objType interface object type, value in the range 0 &lt;= value &lt;= 0xFFFF
	 * @param objInstance object instance, value in the range 1 &lt;= value &lt;= 0xFF
	 * @param propID property identifier (PID), in the range 0 &lt;= PID &lt;= 0xFF
	 * @param startIndex start index in the property, first element has index 1, index 0
	 *        in the property contains the current number of valid elements (read only)
	 * @param elements number of elements in the array of the property, in the range 0
	 *        &lt;= elements &lt;= 0xFF; the number 0 is used to indicate a negative
	 *        response
	 * @param data contains the data (or the error information, if numElems = 0) as byte
	 *        array
	 */
	public CEMIDevMgmt(int msgCode, int objType, int objInstance, int propID,
		int startIndex, int elements, byte[] data)
	{
		this(msgCode, objType, objInstance, propID, startIndex, elements);
		this.data = (byte[]) data.clone();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#getMessageCode()
	 */
	public final short getMessageCode()
	{
		return mc;
	}

	/**
	 * Returns the data part, i.e. the property data or error information following the
	 * start index in the message structure.
	 * <p>
	 * The property content depends on the property data type, and in case of an array
	 * structured property value also on the accessed number of array elements.
	 * <p>
	 * In case of a message carrying a negative response, the payload holds error
	 * information. To determine whether the returned array contains error information,
	 * use {@link #isNegativeResponse()}.<br>
	 * If the message does not contain any data (or error information), a byte array with
	 * length 0 is returned.
	 * 
	 * @return a copy of the data part in the message structure as byte array
	 */
	public final byte[] getPayload()
	{
		return (byte[]) data.clone();
	}

	/**
	 * Returns a descriptive error message on a negative response, as determined by
	 * {@link #isNegativeResponse()}.
	 * <p>
	 * A negative response contains an error information code, which is used to find the
	 * associated message.<br>
	 * If invoked on positive response, "no error" will be returned.
	 * 
	 * @return error status message as string
	 */
	public String getErrorMessage()
	{
		if (!isNegativeResponse())
			return "no error";
		final int err = data[0] & 0xFF;
		if (err > errors.length - 1)
			return "unknown error code";
		return errors[err];
	}

	/**
	 * Returns the interface object type.
	 * <p>
	 * For example,<br>
	 * <ul>
	 * <li>Standardized system interface object types
	 * <ol start="0">
	 * <li>Device object</li>
	 * <li>Addresstable object</li>
	 * <li>Associationtable object</li>
	 * <li>...</li>
	 * </ol>
	 * </li>
	 * <li>Standardized application interface object types
	 * <ul>
	 * <li>[100..399] HVAC</li>
	 * <li>[400..599] Lighting</li>
	 * <li>[600..799] HVAC (sensors & actuators)</li>
	 * <li>[800..999] Shutters and blinds</li>
	 * <li>...</li>
	 * </ul>
	 * </li>
	 * <li>Non-standardized interface object types
	 * <ul>
	 * <li>...</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @return the object type as 16 bit identifier
	 */
	public final int getObjectType()
	{
		return iot;
	}

	/**
	 * Returns the object instance field from the message.
	 * <p>
	 * The instance value is in the range 1 to 0xFF.<br>
	 * If the message structure does not contain this field, 0 is returned.
	 * 
	 * @return the object instance
	 */
	public final short getObjectInstance()
	{
		return oi;
	}

	/**
	 * Returns the number of elements field from the message.
	 * <p>
	 * An element count in the range 1 to 15 indicates the presence of element data. A
	 * value of 0 indicates a negative response.<br>
	 * If the message structure does not contain this field, 0 is returned.
	 * 
	 * @return number of elements as unsigned 4 bit value
	 */
	public final byte getElementCount()
	{
		return elems;
	}

	/**
	 * Returns the property identifier used in the message.
	 * <p>
	 * If the message structure does not contain this field, 0 is returned.
	 * 
	 * @return the PID
	 */
	public final short getPID()
	{
		return pid;
	}

	/**
	 * Returns the start index in the data array of the property.
	 * <p>
	 * If the message structure does not contain this field, 0 is returned.
	 * 
	 * @return start index as 12 bit value
	 */
	public final short getStartIndex()
	{
		return start;
	}

	/**
	 * Returns if the message contains a negative response.
	 * <p>
	 * A message contains a negative response, iff the message code equals
	 * {@link #MC_PROPREAD_CON} or {@link #MC_PROPWRITE_CON} and number of elements is 0.
	 * 
	 * @return response state as boolean
	 */
	public final boolean isNegativeResponse()
	{
		return (mc == MC_PROPREAD_CON || mc == MC_PROPWRITE_CON) && elems == 0;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#getStructLength()
	 */
	public final short getStructLength()
	{
		return (short) (header + data.length);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#toByteArray()
	 */
	public byte[] toByteArray()
	{
		final byte[] buf = new byte[header + data.length];
		int i = 0;
		buf[i++] = (byte) mc;
		if (header > 1) {
			buf[i++] = (byte) (iot >>> 8);
			buf[i++] = (byte) iot;
			buf[i++] = (byte) oi;
			buf[i++] = (byte) pid;
			buf[i++] = (byte) (elems << 4 | start >>> 8);
			buf[i++] = (byte) start;
			for (int k = 0; k < data.length; ++k)
				buf[i++] = data[k];
		}
		return buf;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer buf = new StringBuffer(30);
		buf.append("DM ");
		if (mc == MC_PROPREAD_REQ)
			buf.append("prop-read.req");
		else if (mc == MC_PROPREAD_CON)
			buf.append("prop-read.con");
		else if (mc == MC_PROPWRITE_REQ)
			buf.append("prop-write.req");
		else if (mc == MC_PROPWRITE_CON)
			buf.append("prop-write.con");
		else if (mc == MC_PROPINFO_IND)
			buf.append("prop-info.ind");
		else if (mc == MC_RESET_REQ)
			return "DM reset.req";
		else if (mc == MC_RESET_IND)
			return "DM reset.ind";
		buf.append(" objtype ").append(iot);
		buf.append(" instance ").append(oi);
		buf.append(" pid ").append(pid);
		buf.append(" start ").append(start);
		if (isNegativeResponse())
			buf.append(" ").append(getErrorMessage());
		else {
			buf.append(" elements ").append(elems);
			buf.append(" data ").append(DataUnitBuilder.toHex(data, " "));
		}
		return buf.toString();
	}

	private void checkLength(ByteArrayInputStream is, int len) throws KNXFormatException
	{
		if (is.available() < len)
			throw new KNXFormatException("invalid frame length of " + len + " bytes", len);
	}

	private void checkSetMC(int msgCode)
	{
		if (msgCode < MC_OFFSET || !msgCodes.get(msgCode - MC_OFFSET))
			throw new KNXIllegalArgumentException("unknown message code");
		mc = (short) msgCode;
	}

	private void initReset(ByteArrayInputStream is) throws KNXFormatException
	{
		if (is.available() != 0)
			throw new KNXFormatException("invalid length for a reset frame");
	}

	private void initHeader(ByteArrayInputStream is) throws KNXFormatException
	{
		checkLength(is, 6);
		header = 7;
		try {
			final int tmp;
			checkSetHeaderInfo(is.read() << 8 | is.read(), is.read(), is.read(),
				((tmp = is.read()) & 0x0F) << 8 | is.read(), tmp >> 4);
		}
		catch (final KNXIllegalArgumentException e) {
			throw new KNXFormatException(e.getMessage());
		}
	}

	private void initBody(ByteArrayInputStream is) throws KNXFormatException
	{
		// read error information on negative response
		if (isNegativeResponse())
			data = new byte[] { (byte) is.read() };
		else if (mc == MC_PROPREAD_CON || mc == MC_PROPWRITE_REQ || mc == MC_PROPINFO_IND) {
			checkLength(is, 1);
			data = new byte[is.available()];
			is.read(data, 0, data.length);
		}
	}

	private void checkSetHeaderInfo(int objType, int objInstance, int propID,
		int startIndex, int elements)
	{
		if (objType < 0 || objType > 0xFFFF)
			throw new KNXIllegalArgumentException(
				"interface object type out of range [0..0xFFFF]");
		if (objInstance < 1 || objInstance > 0xFF)
			throw new KNXIllegalArgumentException(
				"object instance out of range [1..0xFF]");
		if (propID < 0 || propID > 0xFF)
			throw new KNXIllegalArgumentException("property ID out of range [0..0xFF]");
		if (startIndex < 0 || startIndex > 0xFFF)
			throw new KNXIllegalArgumentException("start index out of range [0..0xFFF]");
		if (elements < 0 || elements > 0xF)
			throw new KNXIllegalArgumentException("elements out of range [0..0xF]");
		if (mc == MC_PROPREAD_REQ || mc == MC_PROPWRITE_REQ || mc == MC_PROPINFO_IND)
			if (elements < 1)
				throw new KNXIllegalArgumentException("elements may not be 0");
		// NOTE: according to cEMI spec. start index should be > 0, but since we do
		// the check for write-enabled property on index 0, allow it
		// if (mc == MC_PROPWRITE_REQ || mc == MC_PROPWRITE_CON || mc == MC_PROPINFO_IND) {
		// 	if (startIndex < 1)
		// 		throw new KNXIllegalArgumentException("start index may not be 0");
		// }
		iot = objType;
		oi = (short) objInstance;
		pid = (short) propID;
		start = (short) startIndex;
		elems = (byte) elements;
	}
}
