/*
 * SerialPortEvent.java
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

import java.util.EventObject;

/**
 * An event on a serial port.
 *
 * @author <a href='mailto:dog@gnu.org'>Chris Burdess</a>
 * @version 2.0.3
 */
public class SerialPortEvent extends EventObject
{

  /**
   * @deprecated Replaced by <code>getEventType</code> method.
   * For compatibility only.
   */
  public int eventType;

  /**
   * Data is available at the serial port.
   */
  public static final int DATA_AVAILABLE = 1;

  /**
   * The output buffer is empty.
   */
  public static final int OUTPUT_BUFFER_EMPTY = 2;

  /**
   * Clear to send.
   */
  public static final int CTS = 3;

  /**
   * Data set ready.
   */
  public static final int DSR = 4;

  /**
   * Ring indicator.
   */
  public static final int RI = 5;

  /**
   * Carrier detect.
   */
  public static final int CD = 6;

  /**
   * Overrun error.
   */
  public static final int OE = 7;

  /**
   * Parity error.
   */
  public static final int PE = 8;

  /**
   * Framing error.
   */
  public static final int FE = 9;

  /**
   * Break interrupt.
   */
  public static final int BI = 10;

  int eventtype;
  boolean oldvalue;
  boolean newvalue;

  /**
   * Constructor.
   * This should only be called by the port driver.
   * @param srcport the source port
   * @param eventtype the event type, one of: BI, CD, CTS, DATA_AVAILABLE,
   * DSR, FE, OE, OUTPUT_BUFFER_EMPTY, PE or RI.
   * @param oldvalue the old value
   * @param newvalue the new value
   */
  public SerialPortEvent(SerialPort srcport, int eventtype,
      boolean oldvalue, boolean newvalue)
  {
    super(srcport);
    this.eventtype = eventtype;
    this.oldvalue = oldvalue;
    this.newvalue = newvalue;
    // Compatibility
    eventType = eventtype;
  }

  /**
   * Returns the type of event, one of: BI, CD, CTS, DATA_AVAILABLE, DSR,
   * FE, OE, OUTPUT_BUFFER_EMPTY, PE or RI.
   * @since CommAPI 1.1
   */
  public int getEventType()
  {
    return eventtype;
  }

  /**
   * Returns the new value of the state change that caused this event.
   */
  public boolean getNewValue()
  {
    return newvalue;
  }

  /**
   * Returns the old value of the state change that caused this event.
   */
  public boolean getOldValue()
  {
    return oldvalue;
  }

}
