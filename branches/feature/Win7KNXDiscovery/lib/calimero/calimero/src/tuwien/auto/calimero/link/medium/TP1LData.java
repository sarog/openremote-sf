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

import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.exception.KNXFormatException;

/**
 * L-data frame format on TP1 communication medium.
 * <p>
 * Supports standard and extended frame format.
 * 
 * @author B. Malinowsky
 */
public class TP1LData extends RawFrameBase
{
	private static final int MIN_LENGTH = 7;

	/**
	 * Creates a new L-data frame out of a byte array.
	 * <p>
	 * 
	 * @param data byte array containing the L-data frame
	 * @param offset start offset of frame structure in <code>data</code>, offset &gt;=
	 *        0
	 * @throws KNXFormatException if length of data too short for frame, on no valid frame
	 *         structure
	 */
	public TP1LData(byte[] data, int offset) throws KNXFormatException
	{
		final ByteArrayInputStream is =
			new ByteArrayInputStream(data, offset, data.length - offset);
		final int avail = is.available();
		if (avail < MIN_LENGTH)
			throw new KNXFormatException("data too short for L-data frame", avail);
		final int ctrl = is.read();
		// parse control field and check if valid
		if ((ctrl & 0x53) != 0x10)
			throw new KNXFormatException("invalid control field", ctrl);

		type = LDATA_FRAME;
		ext = (ctrl & 0x80) == 0;
		repetition = (ctrl & 0x20) == 0;
		p = Priority.get((ctrl >> 2) & 0x3);

		final int ctrle = ext ? readCtrlEx(is) : 0;
		src = new IndividualAddress((is.read() << 8) | is.read());
		final int addr = (is.read() << 8) | is.read();
		final int npci = is.read();
		final int len;
		if (ext) {
			hopcount = (ctrle & 0x70) >> 4;
			setDestination(addr, (ctrle & 0x80) != 0);
			len = npci;
			if (len == 255)
				throw new KNXFormatException("escape-code in length field not supported");
		}
		else {
			hopcount = (npci & 0x70) >> 4;
			setDestination(addr, (npci & 0x80) != 0);
			len = npci & 0x0f;
		}
		tpdu = new byte[len + 1];
		if (is.read(tpdu, 0, tpdu.length) != tpdu.length)
			throw new KNXFormatException("data too short for L-data TPDU");
		fcs = is.read();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.link.medium.RawFrameBase#toString()
	 */
	public String toString()
	{
		return super.toString() + ", tpdu " + DataUnitBuilder.toHex(tpdu, " ");
	}
}
