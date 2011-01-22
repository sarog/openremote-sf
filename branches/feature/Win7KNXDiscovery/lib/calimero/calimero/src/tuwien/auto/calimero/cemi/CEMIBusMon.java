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

import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 * A cEMI busmonitor indication message.
 * <p>
 * The data part of the busmonitor message structure contains the raw frame on medium (the
 * data link layer PDU + FCS), it accepts up to 23 bytes in length.<br>
 * So the raw frame consists of 22 byte LPDU + 1 byte FCS.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class CEMIBusMon implements CEMI
{
	/*
	 * NOTE: The overflow bit flag information is not available via this interface,
	 * since it seems not to be used in practice. This applies to the constructors and the
	 * method getOverflow().
	 */

	/**
	 * Message code for busmonitor indication, code = {@value #MC_BUSMON_IND}.
	 * <p>
	 */
	public static final short MC_BUSMON_IND = 0x2B;

	/**
	 * Additional information type ID for status info, ID = {@value #TYPEID_STATUSINFO}.
	 * <p>
	 */
	public static final short TYPEID_STATUSINFO = 0x03;

	/**
	 * Additional information type ID for timestamp, ID = {@value #TYPEID_TIMESTAMP}.
	 * <p>
	 */
	public static final short TYPEID_TIMESTAMP = 0x04;

	/**
	 * Additional information type ID for extended timestamp, ID =
	 * {@value #TYPEID_TIMESTAMP_EXT}.
	 * <p>
	 */
	public static final short TYPEID_TIMESTAMP_EXT = 0x06;

	private static final int MIN_ADDINFO_LENGTH = 7;
	private byte[] raw;
	private short status;
	private long tstamp;
	private short tstampType = TYPEID_TIMESTAMP;

	/**
	 * Creates a new busmonitor message.
	 * <p>
	 * 
	 * @param status the status information field (busmonitor error flags) as specified by
	 *        cEMI, located in the additional information type {@link #TYPEID_STATUSINFO},
	 *        0 &lt;= status &lt;= 0xFF
	 * @param timestamp relative timestamp as specified by cEMI, it might either be a
	 *        normal (2 byte range) timestamp for {@link #TYPEID_TIMESTAMP}, or an
	 *        extended (4 byte range) timestamp of type {@link #TYPEID_TIMESTAMP_EXT}
	 * @param extTimestamp <code>true</code> if <code>timestamp</code> is an extended
	 *        timestamp value, <code>false</code> otherwise
	 * @param rawFrame byte array holding the raw frame on the medium (inclusive FCS),
	 *        also referred to as the data part in the message structure,
	 *        <code>rawFrame.length</code> &lt;= 23
	 */
	public CEMIBusMon(int status, long timestamp, boolean extTimestamp, byte[] rawFrame)
	{
		if (status < 0 || status > 0xFF)
			throw new KNXIllegalArgumentException("status byte out of range [0..255]");
		this.status = (short) status;
		init(timestamp, extTimestamp, rawFrame);
	}

	/**
	 * Creates a new busmonitor message.
	 * <p>
	 * In the status field of the additional information block, only the sequence number
	 * can be chosen, all other status flags remain 0 (i.e. indicating no error, no frame
	 * loss)
	 * 
	 * @param seqNumber sequence number in the status field (bits 2..0), 0 &lt;=
	 *        <code>seqNumber</code> &lt;= 7
	 * @param timestamp relative timestamp as specified by cEMI, it might either be a
	 *        normal (2 byte range) timestamp for {@link #TYPEID_TIMESTAMP}, or an
	 *        extended (4 byte range) timestamp of type {@link #TYPEID_TIMESTAMP_EXT}
	 * @param extTimestamp <code>true</code> if <code>timestamp</code> is an extended
	 *        timestamp value, <code>false</code> otherwise
	 * @param rawFrame byte array holding the raw frame on the medium (inclusive FCS),
	 *        also referred to as the data part in the message structure
	 *        <code>rawFrame.length</code> &lt;= 23
	 */
	public CEMIBusMon(byte seqNumber, long timestamp, boolean extTimestamp,
		byte[] rawFrame)
	{
		// the possible constructor ambiguity with CEMIBusMon(int, ...) is not
		// a problem, since seqNumber maps directly to the status field
		// (i.e. it's absolutely legal if seqNumber is supplied for status)
		if (seqNumber < 0 || seqNumber > 7)
			throw new KNXIllegalArgumentException("sequence number out of range [0..7]");
		status = seqNumber;
		init(timestamp, extTimestamp, rawFrame);
	}

	/**
	 * Creates a new busmonitor message.
	 * <p>
	 * Allows to specify every status flag in detail.
	 * 
	 * @param frameError <code>true</code> if a frame error was detected in the message,
	 *        <code>false</code> otherwise
	 * @param bitError <code>true</code> if an invalid bit was detected in one or
	 *        several of the frame characters, <code>false</code> otherwise
	 * @param parityError <code>true</code> if an invalid parity bit was detected,
	 *        <code>false</code> otherwise
	 * @param lost <code>true</code> if at least one frame or frame piece was lost by
	 *        the Data Link Layer, <code>false</code> otherwise
	 * @param seqNumber sequence number in the status field (bits 2..0), 0 &lt;=
	 *        <code>seqNumber</code> &lt;= 7
	 * @param timestamp relative timestamp as specified by cEMI, it might either be a
	 *        normal (2 byte range) timestamp for {@link #TYPEID_TIMESTAMP}, or an
	 *        extended (4 byte range) timestamp of type {@link #TYPEID_TIMESTAMP_EXT}
	 * @param extTimestamp <code>true</code> if <code>timestamp</code> is an extended
	 *        timestamp value, <code>false</code> otherwise
	 * @param rawFrame byte array holding the raw frame on the medium (inclusive FCS),
	 *        also referred to as the data part in the message structure
	 *        <code>rawFrame.length</code> &lt;= 23
	 */
	public CEMIBusMon(boolean frameError, boolean bitError, boolean parityError,
		boolean lost, byte seqNumber, long timestamp, boolean extTimestamp,
		byte[] rawFrame)
	{
		if (seqNumber < 0 || seqNumber > 7)
			throw new KNXIllegalArgumentException("sequence number out of range [0..7]");
		// overflow bit in status is fixed to false, since there
		// seems to be no reason to set it true
		final boolean overflow = false;
		status |= frameError ? 0x80 : 0;
		status |= bitError ? 0x40 : 0;
		status |= parityError ? 0x20 : 0;
		status |= overflow ? 0x10 : 0;
		status |= lost ? 0x08 : 0;
		status |= seqNumber;
		init(timestamp, extTimestamp, rawFrame);
	}

	/**
	 * Creates a new busmonitor message from a byte stream.
	 * <p>
	 * 
	 * @param data byte stream containing a cEMI busmonitor message
	 * @param offset start offset of cEMI frame in <code>data</code>
	 * @param length length in bytes of the whole bus monitor message
	 * @throws KNXFormatException if no busmonitor frame found or invalid frame structure
	 */
	public CEMIBusMon(byte[] data, int offset, int length) throws KNXFormatException
	{
		final ByteArrayInputStream is = new ByteArrayInputStream(data, offset, length);
		// don't limit to maximum of 34 bytes so to allow extended frames
		if (is.available() < 10)
			throw new KNXFormatException("bus monitor frame length too short", is
				.available());
		final int mc = is.read();
		if (mc != MC_BUSMON_IND)
			throw new KNXFormatException("msg code indicates no bus monitor frame", mc);
		final short ail = (short) is.read();
		if (ail < MIN_ADDINFO_LENGTH)
			throw new KNXFormatException("bus monitor add.info length too short", ail);

		boolean statusRead = false;
		boolean timeRead = false;
		final int body = is.available() - ail;
		final byte[] addInfo = new byte[20];
		while (is.available() > body) {
			final int id = is.read();
			final int len = is.read();
			is.read(addInfo, 0, len);
			if (!statusRead)
				statusRead = readStatus(id, len, addInfo);
			if (!timeRead)
				timeRead = readTimestamp(id, len, addInfo);
		}
		if (!timeRead)
			throw new KNXFormatException("no additional info for timestamp");
		if (!statusRead)
			throw new KNXFormatException("no additional info for status info");

		raw = new byte[is.available()];
		is.read(raw, 0, raw.length);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#getMessageCode()
	 */
	public final short getMessageCode()
	{
		return MC_BUSMON_IND;
	}

	/**
	 * Returns the raw frame on medium (the data link PDU inclusive FCS) contained in
	 * this busmonitor indication message.
	 * <p>
	 * 
	 * @return a copy of the raw frame on medium as byte array
	 */
	public final byte[] getPayload()
	{
		return (byte[]) raw.clone();
	}

//	/**
//	 * Returns the frame check sum of the raw frame.
//	 * <p>
//	 * The FCS is the last byte contained in the raw frame after the TPDU.
//	 *
//	 * @return the frame check sum byte
//	 */
//	public final short getChecksum()
//	{
//		return (short) (raw[raw.length - 1] & 0xFF);
//	}

	/**
	 * Returns the bit error flag state set in the status information.
	 * <p>
	 * If <code>true</code>, an invalid bit was detected in one or several of the frame
	 * characters.
	 * 
	 * @return bit error flag as boolean
	 */
	public final boolean getBitError()
	{
		return (status & 0x40) == 0x40;
	}

	/**
	 * Returns the frame error flag state set in the status information.
	 * <p>
	 * If <code>true</code>, a frame error was detected in the message.
	 * 
	 * @return frame error flag as boolean
	 */
	public final boolean getFrameError()
	{
		return (status & 0x80) == 0x80;
	}

	/**
	 * Returns the lost flag state set in the status information.
	 * <p>
	 * If <code>true</code>, at least one frame (piece) was lost by the Data Link
	 * Layer.
	 * 
	 * @return lost flag as boolean
	 */
	public final boolean getLost()
	{
		return (status & 0x08) == 0x08;
	}

	/**
	 * Returns the parity error flag state set in the status information.
	 * <p>
	 * If <code>true</code>, an invalid parity bit was detected in the frame bits.
	 * 
	 * @return parity error flag as boolean
	 */
	public final boolean getParityError()
	{
		return (status & 0x20) == 0x20;
	}

	/**
	 * Returns the sequence number set in the status information.
	 * <p>
	 * 
	 * @return sequence in the range [0..7]
	 */
	public final byte getSequenceNumber()
	{
		return (byte) (status & 0x07);
	}

	/**
	 * Returns the timestamp value of the monitoring frame contained in the additional
	 * information.
	 * <p>
	 * The type of timestamp returned is according to {@link #getTimestampType()}.
	 * 
	 * @return the timestamp value as long
	 */
	public final long getTimestamp()
	{
		return tstamp;
	}

	/**
	 * Returns the type of timestamp contained in the additional information.
	 * <p>
	 * 
	 * @return timestamp type ID
	 */
	public final short getTimestampType()
	{
		return tstampType;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#getStructLength()
	 */
	public final short getStructLength()
	{
		return (short) (raw.length + 9);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.cemi.CEMI#toByteArray()
	 */
	public byte[] toByteArray()
	{
		final byte stampLen = (byte) (tstampType == TYPEID_TIMESTAMP ? 2 : 4);
		final byte[] buf = new byte[raw.length + 7 + stampLen];
		int i = 0;
		buf[i++] = MC_BUSMON_IND;
		buf[i++] = 7;
		buf[i++] = TYPEID_STATUSINFO;
		buf[i++] = 1;
		buf[i++] = (byte) status;
		buf[i++] = (byte) tstampType;
		buf[i++] = stampLen;
		if (tstampType == TYPEID_TIMESTAMP_EXT) {
			buf[i++] = (byte) (tstamp >> 24);
			buf[i++] = (byte) (tstamp >> 16);
		}
		buf[i++] = (byte) (tstamp >> 8);
		buf[i++] = (byte) tstamp;
		for (int k = 0; k < raw.length; ++k)
			buf[i++] = raw[k];
		return buf;
	}

//	REMOVED FROM INTERFACE
//	/**
//	 * Returns the overflow flag state set in the status information.
//	 * <p>
//	 * This bit is of type "don't care".
//	 *
//	 * @return overflow flag as boolean
//	 */
//	 public final boolean getOverflow()
//	 {
//	 return (status & 0x10) == 0x10;
//	 }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer buf = new StringBuffer(30);
		buf.append("Busmon.ind ");
		if (tstampType == TYPEID_TIMESTAMP_EXT)
			buf.append("ext.");
		buf.append("timestamp ");
		buf.append(tstamp);
		buf.append(" status 0x").append(Integer.toHexString(status));
		if ((status & ~0x07) == 0)
			buf.append(" (no error)");
		else {
			buf.append(" (");
			if (getBitError())
				buf.append("bit error ");
			if (getFrameError())
				buf.append("frame error ");
			if (getLost())
				buf.append("lost ");
			if (getParityError())
				buf.append("parity error");
			if (buf.charAt(buf.length() - 1) == ' ')
				buf.deleteCharAt(buf.length() - 1);
			buf.append(")");
		}
		buf.append(" seq ").append(getSequenceNumber());
		buf.append(" raw frame ").append(DataUnitBuilder.toHex(raw, " "));
		return buf.toString();
	}

	short getStatus()
	{
		return status;
	}
	
	private boolean readStatus(int typeID, int len, byte[] addInfo)
		throws KNXFormatException
	{
		if (typeID != TYPEID_STATUSINFO)
			return false;
		if (len != 1)
			throw new KNXFormatException("wrong status info length", len);
		status = (short) (addInfo[0] & 0xff);
		return true;
	}

	private boolean readTimestamp(int typeID, int len, byte[] addInfo)
		throws KNXFormatException
	{
		if (typeID != TYPEID_TIMESTAMP && typeID != TYPEID_TIMESTAMP_EXT)
			return false;
		if (len != 2 && len != 4)
			throw new KNXFormatException("wrong timestamp info length", len);
		for (int i = 0; i < len; ++i)
			tstamp = tstamp << 8 | addInfo[i] & 0xff;
		if (len == 4)
			tstampType = TYPEID_TIMESTAMP_EXT;
		return true;
	}

	private void init(long timestamp, boolean extTimestamp, byte[] rawFrame)
	{
		tstamp = timestamp;
		if (extTimestamp)
			tstampType = TYPEID_TIMESTAMP_EXT;
		final long max = extTimestamp ? 0xFFFFFFFFL : 0xFFFFL;
		if (tstamp < 0 || tstamp > max)
			throw new KNXIllegalArgumentException("timestamp out of range");
		raw = (byte[]) rawFrame.clone();
		if (raw.length == 0 || raw.length > 23)
			throw new KNXIllegalArgumentException("raw frame length out of range [1..23]");
	}
}
