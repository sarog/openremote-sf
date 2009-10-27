/*
 * SerialPort.java
 * Copyright (C) 2004 The Free Software Foundation
 *
 * This file is part of GNU CommAPI, a library.
 *
 * GNU CommAPI is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GNU CommAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 */
package javax.comm;

import java.util.TooManyListenersException;

/**
 * This class models an RS-232 serial communications port.
 *
 * @author <a href='mailto:dog@gnu.org'>Chris Burdess</a>
 * @version 2.0.3
 */
public abstract class SerialPort extends CommPort
{

  /**
   * 5 data bit format.
   */
  public static final int DATABITS_5 = 0x05;

  /**
   * 6 data bit format.
   */
  public static final int DATABITS_6 = 0x06;

  /**
   * 7 data bit format.
   */
  public static final int DATABITS_7 = 0x07;

  /**
   * 8 data bit format.
   */
  public static final int DATABITS_8 = 0x08;

  /**
   * 1 stop bit.
   */
  public static final int STOPBITS_1 = 0x01;

  /**
   * 2 stop bits.
   */
  public static final int STOPBITS_2 = 0x02;

  /**
   * 1-1½ stop bits.
   */
  public static final int STOPBITS_1_5 = 0x03;

  /**
   * No parity.
   */
  public static final int PARITY_NONE = 0;

  /**
   * Odd parity.
   */
  public static final int PARITY_ODD = 1;

  /**
   * Even parity.
   */
  public static final int PARITY_EVEN = 2;

  /**
   * MARK parity scheme.
   */
  public static final int PARITY_MARK = 3;

  /**
   * SPACE parity scheme.
   */
  public static final int PARITY_SPACE = 4;

  /**
   * No flow control.
   */
  public static final int FLOWCONTROL_NONE = 0x00;

  /**
   * RTS/CTS flow control on input.
   */
  public static final int FLOWCONTROL_RTSCTS_IN = 0x01;

  /**
   * RTS/CTS flow control on output.
   */
  public static final int FLOWCONTROL_RTSCTS_OUT = 0x02;

  /**
   * XON/XOFF flow control on input.
   */
  public static final int FLOWCONTROL_XONXOFF_IN = 0x04;

  /**
   * XON/XOFF flow control on output.
   */
  public static final int FLOWCONTROL_XONXOFF_OUT = 0x08;

  /**
   * Constructor.
   */
  public SerialPort()
  {
    // TODO ?
  }

  /**
   * Returns the current baud rate.
   */
  public abstract int getBaudRate();

  /**
   * Returns the current number of data bits: DATABITS_5, DATABITS_6,
   * DATABITS_7, or DATABITS_8.
   */
  public abstract int getDataBits();

  /**
   * Returns the current number of stop bits: STOPBITS_1, STOPBITS_2, or
   * STOPBITS_1_5.
   */
  public abstract int getStopBits();

  /**
   * Returns the current parity scheme: PARITY_NONE, PARITY_ODD,
   * PARITY_EVEN, PARITY_MARK or PARITY_SPACE.
   */
  public abstract int getParity();

  /**
   * Sends a break of the specified duration.
   * @param millis the duration in milliseconds
   */
  public abstract void sendBreak(int millis);

  /**
   * Sets the flow control mode.
   * @param flowcontrol a bitmask combination of FLOWCONTROL_NONE,
   * FLOWCONTROL_RTSCTS_IN, FLOWCONTROL_RTSCTS_OUT,
   * FLOWCONTROL_XONXOFF_IN, and/or FLOWCONTROL_XONXOFF_OUT.
   * @exception UnsupportedCommOperationException if any of the flow
   * control mode was not supported by the underlying driver, or if input
   * and output flow control are set to different values, i.e. one hardware
   * and one software
   */
  public abstract void setFlowControlMode(int flowcontrol)
    throws UnsupportedCommOperationException;

  /**
   * Returns the current flow control mode as a bitmask of
   * FLOWCONTROL_NONE, FLOWCONTROL_RTSCTS_IN, FLOWCONTROL_RTSCTS_OUT,
   * FLOWCONTROL_XONXOFF_IN, and/or FLOWCONTROL_XONXOFF_OUT.
   */
  public abstract int getFlowControlMode();

  /**
   * Set the Receive Fifo trigger level.
   * If the UART has a FIFO and if it can have programmable trigger
   * levels, then this method will cause the UART to raise an interrupt
   * after trigger bytes have been received.
   * @deprecated
   * @param trigger the trigger level
   */
  public void setRcvFifoTrigger(int trigger)
  {
    // TODO ?
  }

  /**
   * Sets the serial port parameters.
   * @param baudRate the baud rate
   * @param dataBits the number of data bits: DATABITS_5,
   * DATABITS_6, DATABITS_7, or DATABITS_8
   * @param stopBits the number of stop bits: STOPBITS_1,
   * STOPBITS_2, or STOPBITS_1_5
   * @param parity the parity schema: PARITY_NONE, PARITY_ODD,
   * PARITY_EVEN, PARITY_MARK or PARITY_SPACE
   * @exception UnsupportedCommOperationException if parameters are
   * specified incorrectly or the baud rate is not supported by the driver
   */
  public abstract void setSerialPortParams(int baudrate, int dataBits,
      int stopBits, int parity)
    throws UnsupportedCommOperationException;

  /**
   * Sets or clears the DTR bit in the UART.
   * @param dtr the Data Terminal Ready bit value
   * @see #isDTR
   */
  public abstract void setDTR(boolean dtr);

  /**
   * Indicates if the DTR bit is set.
   * @see #setDTR
   */
  public abstract boolean isDTR();

  /**
   * Sets or clears the RTS bit in the UART.
   * @param rts the Request To Send bit value
   * @see #isRTS
   */
  public abstract void setRTS(boolean rts);

  /**
   * Indicates if the RTS bit is set.
   * @see #setRTS
   */
  public abstract boolean isRTS();

  /**
   * Indicates if the CTS bit is set.
   */
  public abstract boolean isCTS();

  /**
   * Indicates if the DSR bit is set.
   */
  public abstract boolean isDSR();

  /**
   * Indicates if the RI bit is set.
   */
  public abstract boolean isRI();

  /**
   * Indicates if the CD bit is set.
   */
  public abstract boolean isCD();

  /**
   * Adds the specified serial port listener to receive serial events.
   * Only one listener is allowed per serial port.
   * @param listener the listener to add
   */
  public abstract void addEventListener(SerialPortEventListener listener)
    throws TooManyListenersException;

  /**
   * Removes the current serial port listener.
   * This is performed automatically when the port is closed.
   */
  public abstract void removeEventListener();

  /**
   * Instructs the port to notify its listener when input data is
   * available.
   * @param enable true if asynchronous events are desired, false
   * otherwise
   */
  public abstract void notifyOnDataAvailable(boolean enable);

  /**
   * Instructs the port to notify its listener when the output buffer is
   * empty.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnOutputEmpty(boolean enable);

  /**
   * Instructs the port to notify its listener when the CTS bit changes.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnCTS(boolean enable);

  /**
   * Instructs the port to notify its listener when the DSR bit changes.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnDSR(boolean enable);

  /**
   * Instructs the port to notify its listener when the RI bit changes.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnRingIndicator(boolean enable);

  /**
   * Instructs the port to notify its listener when the CD bit changes.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnCarrierDetect(boolean enable);

  /**
   * Instructs the port to notify its listener when an overrun error
   * occurs.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnOverrunError(boolean enable);

  /**
   * Instructs the port to notify its listener when a parity error occurs.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnParityError(boolean enable);

  /**
   * Instructs the port to notify its listener when a framing error
   * occurs.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnFramingError(boolean enable);

  /**
   * Instructs the port to notify its listener when there is a break
   * interrupt on the line.
   * @param enable true to enable notification, false otherwise
   */
  public abstract void notifyOnBreakInterrupt(boolean enable);

}
