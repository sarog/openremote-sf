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

package tuwien.auto.calimero.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Basic API for communication with a logical serial port connection.
 * <p>
 * The implementation of this API contains platform dependent code. It is used as a
 * fallback to enable serial communication on a RS-232 port in case default access
 * mechanism are not available or there is no protocol support.
 * 
 * @author B. Malinowsky
 */
class SerialCom extends LibraryAdapter
{
	// Notes:
	// Using CDC ME connector/comm interfaces and subtype them in Calimero prevents
	// usage on J2SE platforms, which would be a serious drawback.
	// Taking the SE comm interfaces would require availability of the comm API
	// packages which isn't preferable either.
	// So we use this internal interface similar to the one of the ME platform,
	// to provide Calimero internal access to serial ports.

	// ctrl identifiers
	static final int BAUDRATE = 1;
	static final int PARITY = 2;
	static final int DATABITS = 3;
	static final int STOPBITS = 4;
	static final int FLOWCTRL = 5;
	
	// flow control modes
	static final int FLOWCTRL_NONE = 0;
	static final int FLOWCTRL_CTSRTS = 1;
	
	// according DCB #defines in winbase.h
	static final int PARITY_NONE = 0;
	static final int PARITY_ODD = 1;
	static final int PARITY_EVEN = 2;
	static final int PARITY_MARK = 3;
	
	static final int ONE_STOPBIT = 1;
	static final int TWO_STOPBITS = 2;

	static final int AVAILABLE_INPUT_STATUS = 2;
	private static final int ERROR_STATUS = 1;
	private static final int LINE_STATUS = 3;
	
	// according EV #defines in winbase.h
	// CTS, DSR, RLSD and RING are voltage level change events

	// Any Character received
	private static final int EVENT_RXCHAR = 0x0001;
	// Received certain character
	private static final int EVENT_RXFLAG = 0x0002;
	// Transmit Queue Empty
	private static final int EVENT_TXEMPTY = 0x0004;
	// CTS changed state
	private static final int EVENT_CTS = 0x0008;
	// DSR changed state
	private static final int EVENT_DSR = 0x0010;
	// RLSD changed state
	private static final int EVENT_RLSD = 0x0020;
	// BREAK received
	private static final int EVENT_BREAK = 0x0040;
	// Line status error occurred
	private static final int EVENT_ERR = 0x0080;
	// Ring signal detected
	private static final int EVENT_RING = 0x0100;
	
	// error flags
	// #define CE_RXOVER           0x0001  // Receive Queue overflow
	// #define CE_OVERRUN          0x0002  // Receive Overrun Error
	// #define CE_RXPARITY         0x0004  // Receive Parity Error
	// #define CE_FRAME            0x0008  // Receive Framing error
	// #define CE_BREAK            0x0010  // Break Detected
	// #define CE_TXFULL           0x0100  // TX Queue is full
	// #define CE_MODE             0x8000  // Requested mode unsupported
	
	private static final boolean loaded;
	private static final int INVALID_HANDLE = -1;
	
	private long fd = INVALID_HANDLE;

	static final class Timeouts
	{
		final int readInterval;
		final int readTotalMultiplier;
		final int readTotalConstant;
		final int writeTotalMultiplier;
		final int writeTotalConstant;
	
		Timeouts(int readInterval, int readTotalMultiplier, int readTotalConstant,
			int writeTotalMultiplier, int writeTotalConstant)
		{
			this.readInterval = readInterval;
			this.readTotalMultiplier = readTotalMultiplier;
			this.readTotalConstant = readTotalConstant;
			this.writeTotalMultiplier = writeTotalMultiplier;
			this.writeTotalConstant = writeTotalConstant;
		}
	
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return "read " + readInterval + " read total " + readTotalMultiplier
				+ " constant " + readTotalConstant + " write total "
				+ writeTotalMultiplier + " write constant " + writeTotalConstant;
		}
	}

	static {
		boolean b = false;
		try {
			System.loadLibrary("serialcom");
			b = true;
		}
		catch (final SecurityException e) {}
		catch (final UnsatisfiedLinkError e) {}
		loaded = b;
	}

	SerialCom(String portID) throws IOException
	{
		if (portID == null)
			throw new NullPointerException("port ID");
		if (!loaded)
			throw new IOException("no serial I/O communication support");
		open(portID);
	}

	static boolean isLoaded()
	{
		return loaded;
	}

	static native boolean portExists(String portID);

	native int writeBytes(byte[] b, int off, int len) throws IOException;

	native int write(int b) throws IOException;

	native int readBytes(byte[] b, int off, int len) throws IOException;

	// return of -1 indicates timeout
	native int read() throws IOException;

	native void setTimeouts(Timeouts times) throws IOException;

	native Timeouts getTimeouts() throws IOException;

	/* (non-Javadoc)
	 * @see javax.microedition.io.CommConnection#setBaudRate(int)
	 */
	public final void setBaudRate(int baudrate)
	{
		try {
			setControl(BAUDRATE, baudrate);
		}
		catch (final IOException e) {}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.CommConnection#getBaudRate()
	 */
	public final int getBaudRate()
	{
		try {
			return getControl(BAUDRATE);
		}
		catch (final IOException e) {}
		return 0;
	}

	// will only set a supported parity, check result with getParity()
	// return previous parity mode
	final int setParity(int parity)
	{
		try {
			return setControl(PARITY, parity);
		}
		catch (final IOException e) {}
		return 0;
	}

	// returns 0 if getting parity failed
	final int getParity()
	{
		try {
			return getControl(PARITY);
		}
		catch (final IOException e) {}
		return 0;
	}

	// clear communication error and get device status
	native int getStatus(int type);

	native int setControl(int control, int newValue) throws IOException;

	native int getControl(int control) throws IOException;

	public InputStream getInputStream()
	{
		if (fd == INVALID_HANDLE)
			// throw new IOException();
			return null;
		return new PortInputStream(this);
	}

	public OutputStream getOutputStream()
	{
		if (fd == INVALID_HANDLE)
			// throw new IOException();
			return null;
		return new PortOutputStream(this);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openDataInputStream()
	 */
	DataInputStream openDataInputStream()
	{
		final InputStream is = getInputStream();
		if (is instanceof DataInputStream) {
			return (DataInputStream) is;
		}
		return new DataInputStream(is);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openDataOutputStream()
	 */
	DataOutputStream openDataOutputStream()
	{
		final OutputStream os = getOutputStream();
		if (os instanceof DataOutputStream) {
			return (DataOutputStream) os;
		}
		return new DataOutputStream(os);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.Connection#close()
	 */
	// any open input/output stream accessing this port becomes unusable
	public final void close() throws IOException
	{
		if (fd != INVALID_HANDLE)
			close0();
		fd = INVALID_HANDLE;
	}

	private native void setEvents(int eventMask, boolean enable) throws IOException;

	private native int waitEvent() throws IOException;

	private native void open(String portID) throws IOException;

	private native void close0() throws IOException;
}
