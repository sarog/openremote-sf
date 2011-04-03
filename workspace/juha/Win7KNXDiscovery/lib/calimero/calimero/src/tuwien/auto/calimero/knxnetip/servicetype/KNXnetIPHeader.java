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

/**
 * The common KNXnet/IP header used at the first position in every KNXnet/IP frame.
 * <p>
 * It contains the protocol version, length of this header, the total frame length and the
 * service type identifier of the service carried by the frame. The header, most
 * certainly, is followed by a KNXnet/IP body, depending on the service type, i.e. every
 * KNXnet/IP frame consists at least of this header.
 * <p>
 * The header itself, however, only consists of the pure header structure, it will not
 * store or read any frame body content.<br>
 * It is used for KNXnet/IP frame to retrieve general information in the first place,
 * followed by processing of the frame through more specific service types.<br>
 * Most service type identifiers are listed as public constants in this header interface.
 * <p>
 * The KNXnet/IP implementation status is 1.0.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 */
public class KNXnetIPHeader
{
	/**
	 * Service type identifier for a connect request.
	 * <p>
	 */
	public static final int CONNECT_REQ = 0x0205;

	/**
	 * Service type identifier for a connect response.
	 * <p>
	 */
	public static final int CONNECT_RES = 0x0206;

	/**
	 * Service type identifier for a connection state request.
	 * <p>
	 */
	public static final int CONNECTIONSTATE_REQ = 0x0207;

	/**
	 * Service type identifier for a connection state response.
	 * <p>
	 */
	public static final int CONNECTIONSTATE_RES = 0x0208;

	/**
	 * Service type identifier for a disconnect request.
	 * <p>
	 */
	public static final int DISCONNECT_REQ = 0x0209;

	/**
	 * Service type identifier for a disconnect response.
	 * <p>
	 */
	public static final int DISCONNECT_RES = 0x020A;

	/**
	 * Service type identifier for a description request.
	 * <p>
	 */
	public static final int DESCRIPTION_REQ = 0x0203;

	/**
	 * Service type identifier for a description response.
	 * <p>
	 */
	public static final int DESCRIPTION_RES = 0x204;

	/**
	 * Service type identifier for a search request.
	 * <p>
	 */
	public static final int SEARCH_REQ = 0x201;

	/**
	 * Service type identifier for a search response.
	 * <p>
	 */
	public static final int SEARCH_RES = 0x202;

	/**
	 * Service type identifier for configuration request (read / write device
	 * configuration data, interface properties).
	 * <p>
	 */
	public static final int DEVICE_CONFIGURATION_REQ = 0x0310;

	/**
	 * Service type identifier to confirm the reception of the configuration request.
	 * <p>
	 */
	public static final int DEVICE_CONFIGURATION_ACK = 0x0311;

	/**
	 * Service type identifier to send and receive single KNX frames between client and
	 * server.
	 * <p>
	 */
	public static final int TUNNELING_REQ = 0x0420;

	/**
	 * Service type identifier to confirm the reception of a tunneling request.
	 * <p>
	 */
	public static final int TUNNELING_ACK = 0x0421;

	/**
	 * Service type identifier for sending KNX telegrams over IP networks with multicast.
	 * <p>
	 */
	public static final int ROUTING_IND = 0x0530;

	/**
	 * Service type identifier to indicate the loss of routing messages with multicast.
	 * <p>
	 */
	public static final int ROUTING_LOST_MSG = 0x0531;

	/**
	 * Version identifier for KNXnet/IP protocol version 1.0.
	 * <p>
	 */
	public static final short KNXNETIP_VERSION_10 = 0x10;

	private static final short HEADER_SIZE_10 = 0x06;

	private final short headersize;
	private final int service;
	private final int totalsize;
	private final short version;

	/**
	 * Creates a new KNXnet/IP header by reading in the header of a KNXnet/IP frame.
	 * <p>
	 * 
	 * @param frame byte array with contained KNXnet/IP frame
	 * @param offset start offset of KNXnet/IP header structure in <code>frame</code>
	 * @throws KNXFormatException if <code>frame</code> is too short for header, on
	 *         wrong header size or not supported KNXnet/IP protocol version
	 */
	public KNXnetIPHeader(byte[] frame, int offset) throws KNXFormatException
	{
		if (frame.length - offset < HEADER_SIZE_10)
			throw new KNXFormatException("buffer too short for KNXnet/IP header");
		int i = offset;
		headersize = (short) (frame[i++] & 0xFF);
		version = (short) (frame[i++] & 0xFF);

		int high = frame[i++] & 0xFF;
		int low = frame[i++] & 0xFF;
		service = (high << 8) | low;
		high = frame[i++] & 0xFF;
		low = frame[i++] & 0xFF;
		totalsize = (high << 8) | low;

		if (headersize != HEADER_SIZE_10)
			throw new KNXFormatException("unsupported header size, expected "
				+ HEADER_SIZE_10, headersize);
		if (version != KNXNETIP_VERSION_10)
			throw new KNXFormatException("unsupported KNXnet/IP protocol "
				+ "version, expected " + KNXNETIP_VERSION_10, version);
	}

	/**
	 * Creates a new KNXnet/IP header for the given service and frame body length.
	 * <p>
	 * 
	 * @param serviceType service type identifier specifying the service followed after
	 *        the header, 0 &lt;= type &lt;= 0xFFFF
	 * @param bodyLength length of the frame body, i.e. byte length of the following
	 *        service structure
	 */
	public KNXnetIPHeader(int serviceType, int bodyLength)
	{
		if (bodyLength < 0)
			throw new IllegalArgumentException("negative length of message body");
		if (serviceType < 0 || serviceType > 0xFFFF)
			throw new KNXIllegalArgumentException("service type out of range [0..0xFFFF]");
		headersize = HEADER_SIZE_10;
		version = KNXNETIP_VERSION_10;
		service = serviceType;
		totalsize = headersize + bodyLength;
	}

	/**
	 * Returns the service type identifier.
	 * <p>
	 * 
	 * @return service type as unsigned short
	 */
	public final int getServiceType()
	{
		return service;
	}

	/**
	 * Returns the KNXnet/IP protocol version of the frame.
	 * <p>
	 * 
	 * @return protocol version as unsigned byte
	 */
	public int getVersion()
	{
		return KNXNETIP_VERSION_10;
	}

	/**
	 * Returns the length of the KNXnet/IP header structure.
	 * <p>
	 * 
	 * @return the length in bytes
	 */
	public int getStructLength()
	{
		return HEADER_SIZE_10;
	}

	/**
	 * Returns the total length of the KNXnet/IP frame the header is part of.
	 * <p>
	 * The total length is calculated by adding the header length and the length of the
	 * service contained in the frame.
	 * 
	 * @return the total length in bytes
	 */
	public final int getTotalLength()
	{
		return totalsize;
	}

	/**
	 * Returns the byte representation of the KNXnet/IP header structure.
	 * <p>
	 * 
	 * @return byte array containing structure
	 */
	public byte[] toByteArray()
	{
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(headersize);
		os.write(version);
		os.write(service >> 8);
		os.write(service);
		os.write(totalsize >> 8);
		os.write(totalsize);
		return os.toByteArray();
	}

	/**
	 * Returns a textual representation of this KNXnet/IP header.
	 * <p>
	 * 
	 * @return a string representation of the object
	 */
	public String toString()
	{
		return "KNXnet/IP " + (version / 0x10) + "." + (version % 0x10) + ", "
			+ getSvcName(service) + " (0x" + Integer.toHexString(service)
			+ "), header size " + headersize + " total " + totalsize;
	}

	static String getSvcName(int svcType)
	{
		switch (svcType) {
		case CONNECT_REQ:
			return "connect.req";
		case CONNECT_RES:
			return "connect.res";
		case CONNECTIONSTATE_REQ:
			return "connectionstate.req";
		case CONNECTIONSTATE_RES:
			return "connectionstate.res";
		case DISCONNECT_REQ:
			return "disconnect.req";
		case DISCONNECT_RES:
			return "disconnect.res";
		case DESCRIPTION_REQ:
			return "description.req";
		case DESCRIPTION_RES:
			return "description.res";
		case SEARCH_REQ:
			return "search.req";
		case SEARCH_RES:
			return "search.res";
		case DEVICE_CONFIGURATION_REQ:
			return "device-configuration.req";
		case DEVICE_CONFIGURATION_ACK:
			return "device-configuration.ack";
		case TUNNELING_REQ:
			return "tunneling.req";
		case TUNNELING_ACK:
			return "tunneling.ack";
		case ROUTING_IND:
			return "routing.ind";
		case ROUTING_LOST_MSG:
			return "routing-lost.msg";
		default:
			return "unknown service";
		}
	}
}
