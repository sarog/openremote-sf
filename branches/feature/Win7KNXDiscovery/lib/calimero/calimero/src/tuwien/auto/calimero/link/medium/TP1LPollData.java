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

package tuwien.auto.calimero.link.medium;

import java.io.ByteArrayInputStream;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.exception.KNXFormatException;

/**
 * L-polldata frame format on TP1 communication medium.
 * <p>
 * 
 * @author B. Malinowsky
 */
public class TP1LPollData extends RawFrameBase
{
	private static final int MIN_LENGTH = 7;

	private final int expData;

	/**
	 * Creates a new L-polldata frame out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing the L-polldata frame
	 * @param offset start offset of frame structure in <code>data</code>, offset &gt;=
	 *        0
	 * @throws KNXFormatException if length of data too short for frame, on no valid frame
	 *         structure
	 */
	public TP1LPollData(byte[] data, int offset) throws KNXFormatException
	{
		final ByteArrayInputStream is =
			new ByteArrayInputStream(data, offset, data.length - offset);
		final int avail = is.available();
		if (avail < MIN_LENGTH)
			throw new KNXFormatException("data too short for L-polldata frame", avail);
		final int ctrl = is.read();
		// parse control field and check if valid
		if (ctrl != 0xF0)
			throw new KNXFormatException("invalid control field", ctrl);
		type = LPOLLDATA_FRAME;
		p = Priority.get((ctrl >> 2) & 0x3);
		int addr = (is.read() << 8) | is.read();
		src = new IndividualAddress(addr);
		addr = (is.read() << 8) | is.read();
		dst = new GroupAddress(addr);
		final int len = is.read() & 0x0f;
		expData = len;
		fcs = is.read();
		// do we really get poll data response here? don't know for sure..
		if (expData <= is.available()) {
			tpdu = new byte[expData];
			is.read(tpdu, 0, expData);
		}
	}

	/**
	 * Returns the length of expected poll data.
	 * <p>
	 * 
	 * @return length of expected polldata bytes, 1 &lt;= length &lt;= 15
	 */
	public final int getExpectedDataLength()
	{
		return expData;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.medium.RawFrameBase#toString()
	 */
	public String toString()
	{
		return super.toString() + " exp. polldata " + expData;
	}
}
