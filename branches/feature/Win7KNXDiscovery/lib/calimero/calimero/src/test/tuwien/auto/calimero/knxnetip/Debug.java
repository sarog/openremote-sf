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

package test.tuwien.auto.calimero.knxnetip;

import test.tuwien.auto.calimero.Util;
import tuwien.auto.calimero.DataUnitBuilder;
import tuwien.auto.calimero.cemi.CEMIBusMon;
import tuwien.auto.calimero.cemi.CEMIDevMgmt;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.link.medium.RawAckBase;
import tuwien.auto.calimero.link.medium.RawFrame;
import tuwien.auto.calimero.link.medium.RawFrameBase;
import tuwien.auto.calimero.link.medium.TP1Ack;
import tuwien.auto.calimero.link.medium.TP1LData;
import tuwien.auto.calimero.link.medium.TP1LPollData;

/**
 * @author B. Malinowsky
 */
public final class Debug
{
	private Debug()
	{}

	public static void printLData(CEMILData f)
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("hopcount=" + f.getHopCount());
		buf.append(", dst=" + f.getDestination());
		buf.append(", prio=" + f.getPriority());
		buf.append(", mc=" + f.getMessageCode());
		buf.append(", src=" + f.getSource());
		buf.append(", len=" + f.getStructLength());
		buf.append(", ackreq=" + f.isAckRequested());
		buf.append(", pos.con=" + f.isPositiveConfirmation());
		buf.append(", rep=" + f.isRepetition());
		buf.append("L-data").append(Util.toHexDec(f.getPayload()));
		buf.append("\ntoString(): ");
		buf.append(f.toString());
		buf.append(": ").append(DataUnitBuilder.decode(f.getPayload(), f.getDestination()));
		synchronized (System.out) {
			System.out.println(buf);
			System.out.println();
		}
	}

	public static void printMData(CEMIDevMgmt f)
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("mc=" + f.getMessageCode());
		buf.append(", neg.res=" + f.isNegativeResponse());
		buf.append(", errmsg=" + f.getErrorMessage());
		buf.append(", no elems=" + f.getElementCount());
		buf.append(", oi=" + f.getObjectInstance());
		buf.append(", ot=" + f.getObjectType());
		buf.append(", pid=" + f.getPID());
		buf.append(", start=" + f.getStartIndex());
		buf.append("DevMgmt-data").append(Util.toHexDec(f.getPayload()));
		buf.append("\ntoString(): ");
		buf.append(f.toString());
		synchronized (System.out) {
			System.out.println(buf);
			System.out.println();
		}
	}

	public static void printMonData(CEMIBusMon ind)
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("seq=" + ind.getSequenceNumber());
		buf.append(", timestamp=" + ind.getTimestamp());
		buf.append(", tstamptype=" + ind.getTimestampType());
		buf.append(", lost=" + ind.getLost());
		buf.append(", biterr=" + ind.getBitError());
		buf.append(", frameerr=" + ind.getFrameError());
		buf.append(", parityerr=" + ind.getParityError());
		buf.append("Mon-data").append(Util.toHexDec(ind.getPayload()));
		buf.append("\ntoString(): ");
		buf.append(ind.toString());
		synchronized (System.out) {
			System.out.println(buf);
			System.out.println();
		}
	}

	public static void printTP1Frame(RawFrame tp1)
	{
		final StringBuffer buf = new StringBuffer();
		String type;
		switch (tp1.getFrameType()) {
		case RawFrame.ACK_FRAME:
			type = "ack";
			break;
		case RawFrame.LDATA_FRAME:
			type = "L-data";
			break;
		case RawFrame.LPOLLDATA_FRAME:
			type = "poll-data";
			break;
		default:
			type = "unknown";
		}
		buf.append("frametype=" + type);
		if (tp1 instanceof TP1LData) {
			final TP1LData f = (TP1LData) tp1;
			buf.append(", src=" + f.getSource());
			buf.append(", dst=" + f.getDestination());
			buf.append(", prio=" + f.getPriority());
			buf.append(", rep=" + f.isRepetition());
			buf.append(", chksum=" + f.getChecksum());
		}
		else if (tp1 instanceof TP1Ack) {
			final TP1Ack f = (TP1Ack) tp1;
			final int acktype = f.getAckType();
			String ack;
			if (acktype == RawAckBase.ACK)
				ack = "ACK";
			else if (acktype == RawAckBase.NAK)
				ack = "NAK";
			else if (acktype == TP1Ack.BUSY)
				ack = "BUSY";
			else if (acktype == TP1Ack.NAK_BUSY)
				ack = "NAK_BUSY";
			else
				ack = "unknown";
			buf.append(", ack=" + ack);
		}
		else if (tp1 instanceof TP1LPollData) {
			final TP1LPollData f = (TP1LPollData) tp1;
			buf.append(", src=" + f.getSource());
			buf.append(", dst=" + f.getDestination());
			buf.append(", prio=" + f.getPriority());
			buf.append(", rep=" + f.getExpectedDataLength());
			buf.append(", chksum=" + f.getChecksum());
		}
		synchronized (System.out) {
			System.out.print(buf);
			if (tp1 instanceof RawFrameBase)
				Util.out("tpdu", ((TP1LData) tp1).getTPDU());
			System.out.println();
			System.out.print("toString: " + tp1.toString());
			if (tp1 instanceof RawFrameBase)
				System.out.println(": "
					+ DataUnitBuilder.decode(((TP1LData) tp1).getTPDU(), ((TP1LData) tp1)
						.getDestination()));
			System.out.println();
		}
	}
}
