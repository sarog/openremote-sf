/*
 * ParallelPort.java
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
 * This class models an IEEE 1284 parallel communications port.
 *
 * @author <a href='mailto:dog@gnu.org'>Chris Burdess</a>
 * @version 2.0.3
 */
public abstract class ParallelPort extends CommPort
{

  /**
   * Uses the best available mode.
   */
  public static final int LPT_MODE_ANY = 0;

  /**
   * Unidrectional compatibility mode.
   */
  public static final int LPT_MODE_SPP = 1;

  /**
   * Bidirectional byte-at-a-time mode.
   */
  public static final int LPT_MODE_PS2 = 2;

  /**
   * Extended Parallel Port mode.
   */
  public static final int LPT_MODE_EPP = 3;

  /**
   * Extended Capabilities Port mode.
   */
  public static final int LPT_MODE_ECP = 4;

  /**
   * Bidirectional 4-bits-at-a-time mode.
   */
  public static final int LPT_MODE_NIBBLE = 5;

  /**
   * Constructor.
   */
  public ParallelPort()
  {
    // TODO ?
  }

  /**
   * Adds a listener for parallel port events.
   * Only one listener per ParallelPort is allowed at a time.
   * @param listener the listener to add
   */
  public abstract void addEventListener(ParallelPortEventListener listener)
    throws TooManyListenersException;

  /**
   * Removes the parallel port event listener.
   * This method is automatically called when the port is closed.
   */
  public abstract void removeEventListener();

  /**
   * Instructs the port to notify its listener in the case of port errors.
   * @param notify true to notify in case of error, false otherwise
   */
  public abstract void notifyOnError(boolean notify);

  /**
   * Instructs the port to notify its listener in the case of the output
   * buffer being empty.
   * @param notify true to notify in case of empty buffer, false otherwise
   */
  public abstract void notifyOnBuffer(boolean notify);

  /**
   * Returns the number of available bytes in the output buffer.
   */
  public abstract int getOutputBufferFree();

  /**
   * Indicates if the port is in the Out Of Paper state.
   */
  public abstract boolean isPaperOut();

  /**
   * Indicates if the port is in the Printer Busy state.
   */
  public abstract boolean isPrinterBusy();

  /**
   * Indicates if the printer is in the selected state.
   */
  public abstract boolean isPrinterSelected();

  /**
   * Indicates if the printer has timed out.
   */
  public abstract boolean isPrinterTimedOut();

  /**
   * Indicates if the printer has encountered an error.
   */
  public abstract boolean isPrinterError();

  /**
   * Restart the printer after an error has occurred.
   */
  public abstract void restart();

  /**
   * Suspend output.
   */
  public abstract void suspend();

  /**
   * Returns the currently configured mode: one of LPT_MODE_ANY,
   * LPT_MODE_SPP, LPT_MODE_PS2, LPT_MODE_EPP, or LPT_MODE_ECP.
   */
  public abstract int getMode();

  /**
   * Sets the printer port mode.
   * @param mode one of LPT_MODE_ANY, LPT_MODE_SPP, LPT_MODE_PS2,
   * LPT_MODE_EPP, or LPT_MODE_ECP
   * @exception UnsupportedCommOperationException if the mode is not
   * supported by the driver
   */
  public abstract int setMode(int mode)
    throws UnsupportedCommOperationException;

}
