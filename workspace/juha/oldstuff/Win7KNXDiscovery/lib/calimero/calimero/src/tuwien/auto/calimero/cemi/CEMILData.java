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
import java.io.ByteArrayOutputStream;

import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.KNXAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 * A cEMI link layer data message (L-Data).
 * <p>
 * Only standard frame formats are supported, with a transport layer protocol data unit of
 * 16 bytes maximum. Additional information in the message structure is not supported.
 * <p>
 * Objects of this L-Data type are immutable.
 * 
 * @author B. Malinowsky
 */
public class CEMILData implements CEMI
{
	// Note: RF and PL frame type is not supported here at all, since additional info
	// fields are needed and RF is extended frame only, anyway

	/**
	 * Message code for L-Data request, code = {@value #MC_LDATA_REQ}.
	 * <p>
	 */
	public static final short MC_LDATA_REQ = 0x11;

	/**
	 * Message code for L-Data confirmation, code = {@value #MC_LDATA_CON}.
	 * <p>
	 */
	public static final short MC_LDATA_CON = 0x2E;

	/**
	 * Message code for L-Data indication, code = {@value #MC_LDATA_IND}.
	 * <p>
	 */
	public static final short MC_LDATA_IND = 0x29;

	static final int BASIC_LENGTH = 9;

	/**
	 * Message code of this message.
	 * <p>
	 */
	protected short mc;

	// all externally configurable ctrl parameters:
	// repeat priority ack confirm hop count (and broadcast in subtype)

	/**
	 * Control field 1, the lower 8 bits contain control information.
	 * <p>
	 */
	protected short ctrl1;

	/**
	 * Control field 2, the lower 8 bits contain control information.
	 * <p>
	 */
	protected short ctrl2;

	byte[] data;

	private volatile Priority p;
	private IndividualAddress source;
	private KNXAddress dst;

	/**
	 * Creates a new L-Data message from a byte stream.
	 * <p>
	 * 
	 * @param data byte stream containing a cEMI L-Data message
	 * @param offset start offset of cEMI frame in <code>data</code>
	 * @throws KNXFormatException if no (valid) frame was found or the provided frame is
	 *         not a standard frame
	 */
	public CEMILData(byte[] data, int offset) throws KNXFormatException
	{
		if (data.length - offset < BASIC_LENGTH + 1)
			throw new KNXFormatException("buffer too short for frame");
		final ByteArrayInputStream is =
			new ByteArrayInputStream(data, offset, data.length - offset);
		readMC(is);
		readAddInfo(is);
		readCtrlAndAddr(is);
		if ((ctrl1 & 0x80) == 0)
			throw new KNXFormatException("only cEMI standard frame supported");
		readPayload(is);
	}

	/**
	 * Creates a L-Data message with most control information set to default values.
	 * <p>
	 * The initialized message has send repetitions according to default medium behavior
	 * (for indication message this equals "not repeated frame"), broadcast is "don't
	 * care", acknowledge request is default medium behavior, hop count is 6 and
	 * confirmation request is "don't care" in the control field.<br>
	 * 
	 * @param msgCode a message code value specified in the L-Data type
	 * @param src individual address of source
	 * @param dst destination address
	 * @param tpdu data array, starting with the TPCI / APCI (transport / application
	 *        layer protocol control information), i.e. the NPDU without the length field,
	 *        tpdu.length &lt;= 16
	 * @param p message priority, priority set in the control field
	 */
	public CEMILData(int msgCode, IndividualAddress src, KNXAddress dst, byte[] tpdu,
		Priority p)
	{
		this(msgCode, src, dst, tpdu, p, true, true, false, 6);
	}

	/**
	 * Creates a L-Data message, mainly for confirmation.
	 * <p>
	 * The message hop count is set to 6, send repetitions according to default medium
	 * behavior, broadcast and acknowledge request are set to "don't care" in the control
	 * field.<br>
	 * 
	 * @param msgCode a message code value specified in the L-Data type
	 * @param src individual address of source
	 * @param dst destination address
	 * @param tpdu data array, starting with the TPCI / APCI (transport / application
	 *        layer protocol control information); i.e. the NPDU without the length field,
	 *        tpdu.length &lt;= 16
	 * @param p message priority, priority set in the control field
	 * @param confirm confirm flag in the control field, <code>true</code> to set error,
	 *        <code>false</code> for no error
	 */
	public CEMILData(int msgCode, IndividualAddress src, KNXAddress dst, byte[] tpdu,
		Priority p, boolean confirm)
	{
		this(msgCode, src, dst, tpdu, p, true, true, false, 6);
		setConfirmation(confirm);
	}

	/**
	 * Creates a L-Data message with full customization for control information.
	 * <p>
	 * The confirmation flag of the control field is left out, since it is mutual
	 * exclusive with the rest of the control information and set to "don't care" (refer
	 * to
	 * {@link #CEMILData(int, IndividualAddress, KNXAddress, byte[], Priority, boolean)}).
	 * 
	 * @param msgCode a message code value specified in the L-Data type
	 * @param src individual address of source
	 * @param dst destination address
	 * @param tpdu data array, starting with the TPCI / APCI (transport / application
	 *        layer protocol control information), i.e. the NPDU without the length field,
	 *        tpdu.length &lt;= 16
	 * @param p message priority, priority set in the control field
	 * @param repeat for request messages, send repetitions on the medium -
	 *        <code>false</code> for do not repeat if error, <code>true</code> for
	 *        default repeat behavior;<br>
	 *        meaning of default behavior on media:<br>
	 *        <ul>
	 *        <li>TP0, PL132, RF: no repetitions</li>
	 *        <li>TP1, PL110: repetitions allowed</li>
	 *        </ul>
	 *        for indication message - <code>true</code> if is repeated frame,
	 *        <code>false</code> otherwise
	 * @param broadcast system / domain broadcast behavior, applicable on open media only:
	 *        <code>false</code> for system broadcast, <code>true</code> for
	 *        broadcast; on closed media set <code>true</code> for "don't care"
	 * @param ack acknowledge request, <code>true</code> if acknowledge is requested,
	 *        <code>false</code> for default behavior;<br>
	 *        meaning of default behavior on media:<br>
	 *        <ul>
	 *        <li>TP0, PL132: no acknowledge requested</li>
	 *        <li>TP1, PL110: acknowledge requested</li>
	 *        </ul>
	 * @param hopCount hop count starting value set in control field, in the range 0 &lt;=
	 *        value &lt;= 7
	 */
	protected CEMILData(int msgCode, IndividualAddress src, KNXAddress dst,
		byte[] tpdu, Priority p, boolean repeat, boolean broadcast, boolean ack,
		int hopCount)
	{
		// ctor used for these kinds with relevant ctrl flags:
		// .ind on TP0: repeat priority ack hop count
		// .ind on PL110: repeat broadcast priority hop count
		// .ind on PL132: repeat broadcast priority ack hop count
		// .req TP0: priority ack hop count
		// .req PL132: broadcast priority ack hop count
		// .req on PL110: repeat broadcast priority hop count

		if (msgCode != MC_LDATA_REQ && msgCode != MC_LDATA_CON && msgCode != MC_LDATA_IND)
			throw new KNXIllegalArgumentException("unknown L-Data message code");
		mc = (short) msgCode;
		source = src;
		this.dst = dst;

		// set standard frame
		ctrl1 |= 0x80;
		// set address type
		if (dst instanceof GroupAddress)
			ctrl2 |= 0x80;
		if (!isValidTPDULength(tpdu))
			throw new KNXIllegalArgumentException(
				"maximum TPDU length is 16 in standard frame");
		data = (byte[]) tpdu.clone();
		setPriority(p);
		setRepeat(repeat);
		setBroadcast(broadcast);
		setAcknowledgeRequest(ack);
		setHopCount(hopCount);
	}

	/**
	 * Creates a L-Data message, mainly for TP1 media.
	 * <p>
	 * 
	 * @param msgCode a message code value specified in the L-Data type
	 * @param src individual address of source
	 * @param dst destination address
	 * @param tpdu data array, starting with the TPCI / APCI (transport / application
	 *        layer protocol control information), i.e. the NPDU without the length field,
	 *        tpdu.length &lt;= 16
	 * @param p message priority, priority set in the control field
	 * @param repeat for request message, send repetitions on the medium -
	 *        <code>false</code> for do not repeat if error, <code>true</code> for
	 *        default repeat behavior;<br>
	 *        meaning of default behavior on media:<br>
	 *        <ul>
	 *        <li>TP0, PL132, RF: no repetitions</li>
	 *        <li>TP1, PL110: repetitions allowed</li>
	 *        </ul>
	 *        for indication message - <code>true</code> if is repeated frame,
	 *        <code>false</code> otherwise
	 * @param hopCount hop count starting value set in control field, in the range 0 &lt;=
	 *        value &lt;= 7
	 */
	public CEMILData(int msgCode, IndividualAddress src, KNXAddress dst, byte[] tpdu,
		Priority p, boolean repeat, int hopCount)
	{
		// ctor used for these kinds with relevant ctrl flags:
		// .req on TP1: repeat priority hop count
		// .ind on TP1: repeat priority hop count

		this(msgCode, src, dst, tpdu, p, repeat, true, false, hopCount);
	}

	CEMILData()
	{}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#getMessageCode()
	 */
	public final short getMessageCode()
	{
		return mc;
	}

	/**
	 * Returns the L-Data TPDU.
	 * <p>
	 * The returned array is the NPDU without the length field of the message structure,
	 * starting with the TPCI / APCI field.
	 * 
	 * @return a copy of the TPDU as byte array
	 */
	public final byte[] getPayload()
	{
		return (byte[]) data.clone();
	}

	/**
	 * Returns the KNX individual source address.
	 * <p>
	 * 
	 * @return address as IndividualAddress
	 */
	public final IndividualAddress getSource()
	{
		return source;
	}

	/**
	 * Returns the KNX destination address.
	 * <p>
	 * 
	 * @return destination address as KNXAddress
	 */
	public final KNXAddress getDestination()
	{
		return dst;
	}

	/**
	 * Returns the hop count set in the control information.
	 * <p>
	 * The hop count value is in the range 0 &lt;= value &lt;= 7.
	 * 
	 * @return hop count as 3 bit value
	 */
	public final byte getHopCount()
	{
		return (byte) ((ctrl2 & 0x70) >> 4);
	}

	/**
	 * Returns the message priority.
	 * <p>
	 * 
	 * @return used {@link Priority}
	 */
	public final Priority getPriority()
	{
		return p;
	}

	/**
	 * Returns whether L2 acknowledge was requested.
	 * <p>
	 * This information is valid in L-Data requests and partially in L-Data indications;
	 * for L-Data confirmations the value behavior is undefined (it might have the same
	 * value like the corresponding request).
	 * <p>
	 * For requests the following returns apply:<br>
	 * If <code>true</code>, acknowledge was requested explicitly, <code>false</code>
	 * for "don't care" (default medium behavior).<br>
	 * Default behavior on media for L2 ack:
	 * <ul>
	 * <li>TP0, PL132: no acknowledge requested</li>
	 * <li>TP1, PL110: acknowledge requested</li>
	 * </ul>
	 * <p>
	 * For indication messages following media behavior applies:
	 * <ul>
	 * <li>TP0, PL132: value of ack is relayed from the bus</li>
	 * <li>TP1, PL110: unused, undefined value behavior</li>
	 * </ul>
	 * 
	 * @return acknowledge request as boolean
	 */
	public final boolean isAckRequested()
	{
		return (ctrl1 & 0x02) != 0;
	}

	/**
	 * Returns whether frame repetition is requested, or this is a repeated frame.
	 * <p>
	 * For request messages, returns <code>false</code> for do not repeat if error,
	 * <code>true</code> for default repeat behavior.<br>
	 * Meaning of default behavior on media:
	 * <ul>
	 * <li>TP0, PL132: no repetitions</li>
	 * <li>TP1, PL110: repetitions allowed</li>
	 * </ul>
	 * <p>
	 * For indication messages, returns <code>false</code> if this is not a repeated
	 * frame, <code>true</code> if repeated frame.
	 * <p>
	 * For L-Data confirmations the value behavior is undefined (it might have the same
	 * value like the corresponding request).
	 * 
	 * @return repeat state as boolean
	 */
	public final boolean isRepetition()
	{
		// ind: flag 0 = repeated frame, 1 = not repeated
		if (mc == MC_LDATA_IND)
			return (ctrl1 & 0x20) == 0;
		// req, (con): flag 0 = do not repeat, 1 = default behavior
		return (ctrl1 & 0x20) == 0x20;
	}

	/**
	 * Returns if confirmation indicates success or error in a confirmation message.
	 * <p>
	 * If return is <code>true</code> (confirmation bit in control field is 0 for no
	 * error), the associated request message to this confirmation was transmitted
	 * successfully, <code>false</code> otherwise (confirmation bit in control field is
	 * 1 for error).<br>
	 * On messages types other than confirmation, this information is "don't care" and
	 * always returns <code>true</code>.
	 * 
	 * @return the confirmation state as boolean
	 */
	public final boolean isPositiveConfirmation()
	{
		return (ctrl1 & 0x01) == 0;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#getStructLength()
	 */
	public short getStructLength()
	{
		return (short) (BASIC_LENGTH + data.length);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#toByteArray()
	 */
	public byte[] toByteArray()
	{
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(mc);
		writeAddInfo(os);
		setCtrlPriority();
		os.write(ctrl1);
		os.write(ctrl2);
		byte[] buf = source.toByteArray();
		os.write(buf, 0, buf.length);
		buf = dst.toByteArray();
		os.write(buf, 0, buf.length);
		writePayload(os);
		return os.toByteArray();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("L-Data");
		buf.append(mc == MC_LDATA_IND ? ".ind" : mc == MC_LDATA_REQ ? ".req" : ".con");
		if (mc == MC_LDATA_CON)
			buf.append(isPositiveConfirmation() ? " (pos)" : " (neg)");
		buf.append(" from ").append(source).append(" to ").append(dst);
		buf.append(", ").append(p).append(" priority");
		buf.append(" hop count ").append(getHopCount());
		if (mc != MC_LDATA_CON) {
			if (isAckRequested())
				buf.append(" ack-request");
			if (isRepetition())
				buf.append(" repeat");
		}
		buf.append(" tpdu ").append(DataUnitBuilder.toHex(data, " "));
		return buf.toString();
	}

	void readAddInfo(ByteArrayInputStream is) throws KNXFormatException
	{
		if (is.read() != 0)
			throw new KNXFormatException("cEMI frames with additional info not supported");
	}

	void readPayload(ByteArrayInputStream is) throws KNXFormatException
	{
		final int len = is.read() + 1;
		if (len > is.available())
			throw new KNXFormatException("length of tpdu exceeds available data", len);
		data = new byte[len];
		is.read(data, 0, len);
	}

	/**
	 * Writes additional information to <code>os</code>.
	 * <p>
	 * This type does not support additional information; the additional info length is
	 * set to 0, indicating no additional information.
	 * <p>
	 * 
	 * @param os the output stream
	 */
	void writeAddInfo(ByteArrayOutputStream os)
	{
		os.write(0);
	}

	void writePayload(ByteArrayOutputStream os)
	{
		os.write(data.length - 1);
		os.write(data, 0, data.length);
	}

	boolean isValidTPDULength(byte[] tpdu)
	{
		return tpdu.length <= 16;
	}

	void readCtrlAndAddr(ByteArrayInputStream is)
	{
		ctrl1 = (short) is.read();
		getCtrlPriority();
		ctrl2 = (short) is.read();
		final byte[] addr = new byte[2];
		is.read(addr, 0, 2);
		source = new IndividualAddress(addr);
		is.read(addr, 0, 2);
		if ((ctrl2 & 0x80) != 0)
			dst = new GroupAddress(addr);
		else
			dst = new IndividualAddress(addr);
	}

	void readMC(ByteArrayInputStream is) throws KNXFormatException
	{
		mc = (short) is.read();
		if (mc != MC_LDATA_REQ && mc != MC_LDATA_CON && mc != MC_LDATA_IND)
			throw new KNXFormatException("msg code indicates no L-data frame", mc);
	}

	void setHopCount(int hobbes)
	{
		if (hobbes < 0 || hobbes > 7)
			throw new KNXIllegalArgumentException("hop count out of range [0..7]");
		ctrl2 &= 0x8F;
		ctrl2 |= hobbes << 4;
	}

	void setPriority(Priority priority)
	{
		p = priority;
	}

	void setBroadcast(boolean domain)
	{
		if (domain)
			ctrl1 |= 0x10;
		else
			ctrl1 &= 0xEF;
	}

	private void getCtrlPriority()
	{
		final int bits = ctrl1 >> 2 & 0x03;
		p =
			bits == Priority.LOW.value ? Priority.LOW : bits == Priority.NORMAL.value
				? Priority.NORMAL : bits == Priority.SYSTEM.value ? Priority.SYSTEM
					: Priority.URGENT;
		// clear priority info in control field
		ctrl1 &= ~0xC;
	}

	private void setAcknowledgeRequest(boolean ack)
	{
		if (ack)
			ctrl1 |= 0x02;
		else
			ctrl1 &= 0xFD;
	}

	private void setConfirmation(boolean error)
	{
		if (error)
			ctrl1 |= 0x01;
		else
			ctrl1 &= 0xFE;
	}

	private void setCtrlPriority()
	{
		ctrl1 &= ~0xC;
		ctrl1 |= p.value << 2;
	}

	/**
	 * Set repeat flag in control field.
	 * <p>
	 * Note: uses message code type for decision.
	 * 
	 * @param repeat <code>true</code> for a repeat request or repeated frame,
	 *        <code>false</code> otherwise
	 */
	private void setRepeat(boolean repeat)
	{
		final boolean flag = mc == MC_LDATA_IND ? !repeat : repeat;
		if (flag)
			ctrl1 |= 0x20;
		else
			ctrl1 &= 0xDF;
	}
}
