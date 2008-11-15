/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3.0 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*
* You should have received a copy of the GNU General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.openremote.controller.output;

import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;
import org.openremote.controller.daemon.IOModule;
import org.openremote.controller.daemon.SerialProtocol;
import static org.openremote.controller.daemon.IOModule.RAW_SERIAL;

/**
 * This is the low level Java API for raw serial communications. Internally it delegates
 * actual I/O operations to I/O proxy (which in turns delegates to OS native layers).
 *
 *  TODO
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class SerialTransmitter
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Log category name for this component. The actual full log category can be constructed by
   * appending {@link org.openremote.controller.core.Bootstrap#ROOT_LOG_CATEGORY} and this
   * string using a dot '.' separator (ROOT_LOG_CATEGORY.LOG_CATEGORY).
   *
   * Value: {@value}
   */
  public final static String LOG_CATEGORY = "RAW SERIAL TRANSMITTER";


  // Enums ----------------------------------------------------------------------------------------



  // Class Members --------------------------------------------------------------------------------

  /**
   * Logger API for this component. Currently uses the JBoss logging API.
   */
  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Service context for this component. Service context can be used to access the microcontainer
   * and kernel that is used to deploy this component.
   */
  private KernelControllerContext serviceContext;

  // TODO
  private SerialProtocol.Parity parity;

  // TODO
  private SerialProtocol.DataBits databits;

  // TODO
  private SerialProtocol.StopBits stopbits;

  // TODO
  private int baudrate;


  // MC Component Methods -------------------------------------------------------------------------

  /**
   * Injects the microcontainer context (service context) at component deployment time (prior
   * to component start).
   *
   * @param ctx   a service context which allows access to other deployed services and their
   *              configuration and metadata via the microcontainer deployment framework (kernel)
   */
  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  /**
   * Start is invoked by the microcontainer before component deployment is complete and after
   * all configuration properties have been injected and/or set.  We can initialize the component
   * here and make it 'ready'.  <p>
   *
   * TODO
   */
  @Start public void start()
  {
    log.info("Starting Raw Serial Transmitter...");

    openPort("/dev/ttyS0", databits, parity, stopbits, getBaudRate());

    // TODO: inject the name
/*
    try
    {
      String optionsPayload =
          SerialProtocol.DataBits.EIGHT.getSerialFormat() +
          SerialProtocol.Parity.NONE.getSerialFormat() +
          SerialProtocol.StopBits.ONE.getSerialFormat();

      String serialCommand = "";

      String payload = optionsPayload + serialCommand;

      serviceContext.getKernel().getBus().invoke(
          "Output/IOProxy",
          "sendBytes",

          new Object[]
          {
              RAW_SERIAL,
              payload.getBytes()
          },
          new String[] { IOModule.class.getName(), new byte[] {}.getClass().getName() }
      );
    }
    catch (Throwable t)
    {
      throw new Error(t);
    }
*/    
  }

  // JavaBean Methods -----------------------------------------------------------------------------

  public void setBaudRate(int baudrate)
  {
    if (baudrate < 0)
      throw new IllegalArgumentException("Baud rate must be positive (got " + baudrate + ")");

    this.baudrate = baudrate;
  }

  public int getBaudRate()
  {
    return this.baudrate;
  }

  public void setParity(String parity)
  {
    if (parity == null)
      throw new IllegalArgumentException("Null parity");

    parity = parity.trim().toUpperCase();

    if (parity.equalsIgnoreCase("N") || parity.equalsIgnoreCase("NONE"))
      this.parity = SerialProtocol.Parity.NONE;
    else if (parity.equalsIgnoreCase("O") || parity.equalsIgnoreCase("ODD"))
      this.parity = SerialProtocol.Parity.ODD;
    else if (parity.equalsIgnoreCase("E") || parity.equalsIgnoreCase("EVEN"))
      this.parity = SerialProtocol.Parity.EVEN;
    else
      this.parity = SerialProtocol.Parity.valueOf(parity);
  }

  public String getParity()
  {
    return parity.toString();
  }

  public void setDataBits(String databits)
  {
    if (databits.equals("5"))
      this.databits = SerialProtocol.DataBits.FIVE;
    else if (databits.equals("6"))
      this.databits = SerialProtocol.DataBits.SIX;
    else if (databits.equals("7"))
      this.databits = SerialProtocol.DataBits.SEVEN;
    else if (databits.equals("8"))
      this.databits = SerialProtocol.DataBits.EIGHT;
    else
      throw new Error("Configuration error: unknown databit value '" + databits + "'.");  

      // TODO : service state
  }

  public String getDataBits()
  {
    return databits.getSerialFormat();
  }

  public void setStopBits(String stopbits)
  {
    if (stopbits == null)
      throw new IllegalArgumentException("Null stopbits");

    stopbits = stopbits.trim().toUpperCase();

    if (stopbits.equalsIgnoreCase("1") || stopbits.equalsIgnoreCase("ONE"))
      this.stopbits = SerialProtocol.StopBits.ONE;
    else if (stopbits.equalsIgnoreCase("2") || stopbits.equalsIgnoreCase("TWO"))
      this.stopbits = SerialProtocol.StopBits.TWO;
    else if (stopbits.equalsIgnoreCase("1.5") || stopbits.equalsIgnoreCase("ONE HALF") || stopbits.equalsIgnoreCase("ONE AND HALF"))
      this.stopbits = SerialProtocol.StopBits.ONE_HALF;
    else
      this.stopbits = SerialProtocol.StopBits.valueOf(stopbits);
  }

  public String getStopBits()
  {
    return stopbits.toString();
  }

  // Public Instance Methods ----------------------------------------------------------------------

  
  public void /* TODO: return */ openPort(String portID,
                                          SerialProtocol.DataBits databits,
                                          SerialProtocol.Parity parity,
                                          SerialProtocol.StopBits stopbits,
                                          int baudrate)
  {
    byte[] payload = SerialProtocol.createOpenPortMessage(portID, baudrate, databits, parity, stopbits);

    try
    {
      serviceContext.getKernel().getBus().invoke(
          "Output/IOProxy",
          "sendBytes",

          new Object[]
          {
              RAW_SERIAL,
              payload
          },
          new String[] { IOModule.class.getName(), new byte[] {}.getClass().getName() }
      );
    }
    catch (Throwable t)
    {
      System.out.println("openPort() error : " + t);
    }
  }
}
