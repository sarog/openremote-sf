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

package tuwien.auto.calimero.knxnetip.servicetype;

import java.io.ByteArrayOutputStream;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.knxnetip.util.HPAI;

/**
 * Represents a KNXnet/IP connection state request.
 * <p>
 * Connection state requests are sent by a client during an established client / server
 * communication connection to check the connection state, i.e. they are part of a
 * heartbeat monitoring procedure.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 * @author Bernhard Erb
 * @see tuwien.auto.calimero.knxnetip.servicetype.ConnectionstateResponse
 */
public class ConnectionstateRequest extends ServiceType
{
	private final short channelid;
	private final HPAI endpt;

	/**
	 * Creates a connection state request out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing a connection state request structure
	 * @param offset start offset of request in <code>data</code>
	 * @throws KNXFormatException if no connection state request was found or invalid
	 *         structure
	 */
	public ConnectionstateRequest(byte[] data, int offset) throws KNXFormatException
	{
		super(KNXnetIPHeader.CONNECTIONSTATE_REQ);
		if (data.length - offset < 3)
			throw new KNXFormatException("buffer too short for request");
		channelid = (short) (data[offset] & 0xFF);
		endpt = new HPAI(data, offset + 2);
	}

	/**
	 * Creates a new connection state request.
	 * <p>
	 * 
	 * @param channelID communication channel ID of the open connection, 0 &lt;= id &lt;=
	 *        255
	 * @param ctrlEP control endpoint of the client
	 */
	public ConnectionstateRequest(short channelID, HPAI ctrlEP)
	{
		super(KNXnetIPHeader.CONNECTIONSTATE_REQ);
		if (channelID < 0 || channelID > 0xFF)
			throw new KNXIllegalArgumentException("channel ID out of range [0..255]");
		channelid = channelID;
		endpt = ctrlEP;
	}

	/**
	 * Returns the communication channel ID of the connection this connection state
	 * request belongs to.
	 * <p>
	 * 
	 * @return channel ID as unsigned byte
	 */
	public final short getChannelID()
	{
		return channelid;
	}

	/**
	 * Returns the client control endpoint the server replies to.
	 * <p>
	 * 
	 * @return control endpoint in a HPAI
	 */
	public final HPAI getControlEndpoint()
	{
		return endpt;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#getStructLength()
	 */
	short getStructLength()
	{
		return (short) (2 + endpt.getStructLength());
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#toByteArray
	 *      (java.io.ByteArrayOutputStream)
	 */
	byte[] toByteArray(ByteArrayOutputStream os)
	{
		os.write(channelid);
		os.write(0);
		final byte[] buf = endpt.toByteArray();
		os.write(buf, 0, buf.length);
		return os.toByteArray();
	}
}
