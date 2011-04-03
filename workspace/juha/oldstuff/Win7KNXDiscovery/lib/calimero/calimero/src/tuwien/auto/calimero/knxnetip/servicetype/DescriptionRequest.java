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
import java.net.InetAddress;
import java.net.InetSocketAddress;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.knxnetip.util.HPAI;

/**
 * Represents a KNXnet/IP description request.
 * <p>
 * A request for self description is mainly used by a client after discovery of a new
 * remote device endpoint. It is sent to the control endpoint of the server device. The
 * counterpart to this request is the description response.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author Bernhard Erb
 * @author B. Malinowsky
 * @see tuwien.auto.calimero.knxnetip.servicetype.DescriptionResponse
 * @see tuwien.auto.calimero.knxnetip.Discoverer
 */
public class DescriptionRequest extends ServiceType
{
	private final HPAI endpoint;

	/**
	 * Creates a description request out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing a description request structure
	 * @param offset start offset of request in <code>data</code>
	 * @throws KNXFormatException if no description request was found or invalid structure
	 */
	public DescriptionRequest(byte[] data, int offset) throws KNXFormatException
	{
		super(KNXnetIPHeader.DESCRIPTION_REQ);
		endpoint = new HPAI(data, offset);
	}

	/**
	 * Creates a new description request with the given client control endpoint for a
	 * description response.
	 * <p>
	 * 
	 * @param ctrlEndpoint client control endpoint used for response
	 */
	public DescriptionRequest(HPAI ctrlEndpoint)
	{
		super(KNXnetIPHeader.DESCRIPTION_REQ);
		endpoint = ctrlEndpoint;
	}

	/**
	 * Creates a new description request with the client address used for a description
	 * response.
	 * <p>
	 * This request uses the UDP transport protocol.
	 * 
	 * @param responseAddr address of client control endpoint used for response, use
	 *        <code>null</code> if NAT is used on the IP network
	 */
	public DescriptionRequest(InetSocketAddress responseAddr)
	{
		super(KNXnetIPHeader.DESCRIPTION_REQ);
		endpoint = new HPAI(HPAI.IPV4_UDP, responseAddr);
	}

	/**
	 * Convenience constructor to create a new description request using the UDP transport
	 * protocol and the system default local host with the supplied client port.
	 * 
	 * @param responsePort port number of the client control endpoint used for response, 0
	 *        &lt;= port &lt;= 0xFFFF
	 */
	public DescriptionRequest(int responsePort)
	{
		super(KNXnetIPHeader.DESCRIPTION_REQ);
		endpoint = new HPAI((InetAddress) null, responsePort);
	}

	/**
	 * Returns the client control endpoint.
	 * <p>
	 * 
	 * @return control endpoint in a HPAI
	 */
	public final HPAI getEndpoint()
	{
		return endpoint;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#getStructLength()
	 */
	short getStructLength()
	{
		return endpoint.getStructLength();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#toByteArray
	 *      (java.io.ByteArrayOutputStream)
	 */
	byte[] toByteArray(ByteArrayOutputStream os)
	{
		final byte[] buf = endpoint.toByteArray();
		os.write(buf, 0, buf.length);
		return os.toByteArray();
	}
}
