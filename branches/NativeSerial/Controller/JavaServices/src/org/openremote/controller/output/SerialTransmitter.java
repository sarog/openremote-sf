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
import static org.openremote.controller.daemon.IOModule.RAW_SERIAL;

/**
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

  enum Parity
  {
    EVEN ("E"),
    ODD  ("O"),
    NONE ("N");

    private String serialFormat;

    private Parity(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  enum StopBit
  {
    ONE ("1"),
    TWO ("2"),
    ONE_HALF ("9");

    private String serialFormat;

    private StopBit(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  enum DataBit
  {
    FIVE  ("5"),
    SIX   ("6"),
    SEVEN ("7"),
    EIGHT ("8");

    private String serialFormat;

    private DataBit(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  enum FlowControl
  {
    SOFTWARE,
    HARDWARE
  }
  

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
    
    // TODO: inject the name

    try
    {
      String optionsPayload =
          DataBit.EIGHT.getSerialFormat() +
          Parity.NONE.getSerialFormat() +
          StopBit.ONE.getSerialFormat();

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
  }
}
