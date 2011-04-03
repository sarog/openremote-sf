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

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.knxnetip.util.CRI;
import tuwien.auto.calimero.knxnetip.util.HPAI;

/**
 * Represents a KNXnet/IP connect request message.
 * <p>
 * Such request is used to open a logical connection to a server. The request is sent to
 * the control endpoint of the server. <br>
 * The connection request is answered with a connect response.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author B. Malinowsky
 * @see tuwien.auto.calimero.knxnetip.servicetype.ConnectResponse
 */
public class ConnectRequest extends ServiceType
{
	private final CRI cri;
	private final HPAI ctrlPt;
	private final HPAI dataPt;

	/**
	 * Creates a connect request out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing a connect request structure
	 * @param offset start offset of request in <code>data</code>
	 * @throws KNXFormatException if no connect request was found or invalid structure
	 */
	public ConnectRequest(byte[] data, int offset) throws KNXFormatException
	{
		super(KNXnetIPHeader.CONNECT_REQ);
		ctrlPt = new HPAI(data, offset);
		final int i = offset + ctrlPt.getStructLength();
		dataPt = new HPAI(data, i);
		cri = CRI.createRequest(data, i + dataPt.getStructLength());
	}

	/**
	 * Creates a connect request with the specific information of the CRI, and the
	 * endpoint information of the client.
	 * <p>
	 * The control and data endpoint specified are allowed to be equal, i.e all
	 * communication is handled through the same endpoint at the client.
	 * 
	 * @param requestInfo connection specific options, depending on connection type
	 * @param ctrlEndpoint return address information of the client's control endpoint
	 * @param dataEndpoint address information of the client's data endpoint for the
	 *        requested connection
	 */
	public ConnectRequest(CRI requestInfo, HPAI ctrlEndpoint, HPAI dataEndpoint)
	{
		super(KNXnetIPHeader.CONNECT_REQ);
		cri = requestInfo;
		ctrlPt = ctrlEndpoint;
		dataPt = dataEndpoint;
	}

	/**
	 * Creates a connect request for UDP communication, done on the specified local port
	 * and the system default local host.
	 * <p>
	 * 
	 * @param requestInfo connection specific options, depending on connection type
	 * @param localPort local port of client used for connection, 0 &lt;= port &lt;=
	 *        0xFFFF
	 * @see CRI
	 */
	public ConnectRequest(CRI requestInfo, int localPort)
	{
		super(KNXnetIPHeader.CONNECT_REQ);
		cri = requestInfo;
		ctrlPt = new HPAI((InetAddress) null, localPort);
		dataPt = ctrlPt;
	}

	/**
	 * Returns the connect request information used in the request.
	 * <p>
	 * 
	 * @return connection specific CRI
	 */
	public final CRI getCRI()
	{
		return cri;
	}

	/**
	 * Returns the local control endpoint used for the connection.
	 * <p>
	 * 
	 * @return control endpoint in a HPAI
	 */
	public final HPAI getControlEndpoint()
	{
		return ctrlPt;
	}

	/**
	 * Returns the local data endpoint used for the connection.
	 * <p>
	 * 
	 * @return data endpoint in a HPAI
	 */
	public final HPAI getDataEndpoint()
	{
		return dataPt;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#getStructLength()
	 */
	short getStructLength()
	{
		return (short) (ctrlPt.getStructLength() + dataPt.getStructLength() + cri
			.getStructLength());
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.knxnetip.servicetype.ServiceType#toByteArray
	 *      (java.io.ByteArrayOutputStream)
	 */
	byte[] toByteArray(ByteArrayOutputStream os)
	{
		byte[] buf = ctrlPt.toByteArray();
		os.write(buf, 0, buf.length);
		buf = dataPt.toByteArray();
		os.write(buf, 0, buf.length);
		buf = cri.toByteArray();
		os.write(buf, 0, buf.length);
		return os.toByteArray();
	}
}
