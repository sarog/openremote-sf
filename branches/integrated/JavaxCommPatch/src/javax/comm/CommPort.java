/*
 * CommPort.java
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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class models a communications port.
 * <p>
 * The application should use
 * <code>CommPortIdentifier.getPortIdentifiers</code> to retrieve a list of
 * available port identifiers. It should then call
 * <code>CommPortIdentifier.open</code> to retrieve the corresponding
 * CommPort object. When it has finished with the port, it should call
 * <code>close</code> to release any underlying resources.
 *
 * @author <a href='mailto:dog@gnu.org'>Chris Burdess</a>
 * @version 2.0.3
 */
public abstract class CommPort
{

  /**
   * The port name.
   */
  protected String name;
  
  /**
   * Returns the name of this port.
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * Returns a string representation of this port.
   */
  public String toString()
  {
    return name;
  }
  
  /**
   * Returns an input stream that can be used to read data from the
   * communications port. If the port doesn't support reading data, this
   * method returns null.
   * <p>
   * The blocking behaviour of this input stream depends on the threshold
   * and timeout values, as shown below for a read buffer size of <i>n</i>
   * bytes:
   * <table>
   * <tr>
   * <th colspan='2'>Threshold</th>
   * <th colspan='2'>Timeout</th>
   * <th rowspan='2'>Blocking behaviour</th>
   * </tr>
   * <tr>
   * <th>State</th><th>Value</th>
   * <th>State</th><th>Value</th>
   * </tr>
   * <tr>
   * <td>disabled</td><td></td>
   * <td>disabled</td><td></td>
   * <td>block until data is available</td>
   * </tr>
   * <tr>
   * <td>enabled</td><td><i>m</i> bytes</td>
   * <td>disabled</td><td></td>
   * <td>block until min(<i>m</i>, <i>n</i>) bytes are available</td>
   * </tr>
   * <tr>
   * <td>disabled</td><td></td>
   * <td>enabled</td><td><i>t</i> ms</td>
   * <td>block for <i>t</i> ms or until data is available</td>
   * </tr>
   * <tr>
   * <td>enabled</td><td><i>m</i> bytes</td>
   * <td>enabled</td><td><i>t</i> ms</td>
   * <td>block for <i>t</i> ms or until min(<i>m</i>, <i>n</i>) bytes are
   * available</td>
   * </tr>
   * </table>
   */
  public abstract InputStream getInputStream()
    throws IOException;
  
  /**
   * Returns an output stream that can be used to send data to the
   * communications port. If the port doesn't support sending data, this
   * method returns null.
   */
  public abstract OutputStream getOutputStream()
    throws IOException;
  
  /**
   * Closes this communications port. Further methods on this object will
   * throw IllegalStateException. All PortOwnershipListeners will be
   * notified of this change of ownership.
   */
  public void close()
  {
    // Notify the corresponding CommPortIdentifier
    try
    {
      CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(this);
      id.portClosed();
    }
    catch (NoSuchPortException e)
    {
      // This shouldn't happen...
    }
  }
  
  /**
   * Enables receive threshold.
   * When the specified threshold number of bytes are retrieved, read will
   * return immediately.
   * @param thresh the number of bytes in the input buffer
   * @exception UnsupportedCommOperationException if receive threshold is
   * not supported by the driver
   * @see #disableReceiveThreshold
   */
  public abstract void enableReceiveThreshold(int thresh)
    throws UnsupportedCommOperationException;
  
  /**
   * Disables receive threshold.
   * @see #enableReceiveThreshold
   */
  public abstract void disableReceiveThreshold();
  
  /**
   * Indicates whether receive threshold is enabled.
   * @see #enableReceiveThreshold
   */
  public abstract boolean isReceiveThresholdEnabled();
  
  /**
   * Returns the receive threshold value. If receive threshold is diabled
   * or not supported, this value is meaningless.
   * @see #enableReceiveThreshold
   */
  public abstract int getReceiveThreshold();
  
  /**
   * Enables receive timeout.
   * When the specified number of milliseconds have elapsed, read will
   * return immediately.
   * @param rcvTimeout the number of milliseconds to block for
   * @exception UnsupportedCommOperationException if receive timeout is
   * not supported by the driver
   * @see #disableReceiveTimeout
   */
  public abstract void enableReceiveTimeout(int rcvTimeout)
    throws UnsupportedCommOperationException;
  
  /**
   * Disables receive timeout.
   * @see #enableReceiveTimeout
   */
  public abstract void disableReceiveTimeout();
  
  /**
   * Indicates whether receive timeout is enabled.
   * @see #enableReceiveTimeout
   */
  public abstract boolean isReceiveTimeoutEnabled();
  
  /**
   * Returns the receive timeout value. If receive timeout is diabled
   * or not supported, this value is meaningless.
   * @see #enableReceiveTimeout
   */
  public abstract int getReceiveTimeout();
  
  /**
   * Enables receive framing.
   * The supplied byte value in the input suggests the end of the received
   * frame, and read returns immediately. Only the low 8 bits of the
   * supplied value are significant.
   * @param framingByte the frame delimiter
   * @exception UnsupportedCommOperationException if receive framing is
   * not supported by the driver
   * @see #disableReceiveFraming
   */
  public abstract void enableReceiveFraming(int framingByte)
    throws UnsupportedCommOperationException;
  
  /**
   * Disables receive framing.
   * @see #enableReceiveFraming
   */
  public abstract void disableReceiveFraming();
  
  /**
   * Indicates whether receive framing is enabled.
   * @see #enableReceiveFraming
   */
  public abstract boolean isReceiveFramingEnabled();
  
  /**
   * Returns the receive framing byte. If receive framing is diabled
   * or not supported, this value is meaningless.
   * @see #enableReceiveFraming
   */
  public abstract int getReceiveFramingByte();
  
  /**
   * Sets the input buffer size.
   * The driver may choose to ignore this method.
   */
  public abstract void setInputBufferSize(int size);
  
  /**
   * Returns the input buffer size.
   * The driver may choose not to report correct values.
   */
  public abstract int getInputBufferSize();
  
  /**
   * Sets the output buffer size.
   * The driver may choose to ignore this method.
   */
  public abstract void setOutputBufferSize(int size);
  
  /**
   * Returns the output buffer size.
   * The driver may choose not to report correct values.
   */
  public abstract int getOutputBufferSize();
  
}
