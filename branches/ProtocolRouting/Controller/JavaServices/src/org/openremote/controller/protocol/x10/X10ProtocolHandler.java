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
package org.openremote.controller.protocol.x10;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;
import x10.net.SocketController;
import x10.Command;

import java.io.IOException;
import java.net.ConnectException;
import java.util.StringTokenizer;

/**
 * Wrapper for SocketController in X10 Java project.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class X10ProtocolHandler
{

  // Constants ------------------------------------------------------------------------------------

  public final static int DEFAULT_PORT = 9999;

  public final static String DEFAULT_HOST = "127.168.0.1";

  public final static String LOG_CATEGORY = "X10 GATEWAY";


  // Class Members --------------------------------------------------------------------------------


  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private KernelControllerContext serviceContext;

  private int remotePort = DEFAULT_PORT;

  private String remoteHost = DEFAULT_HOST;


  // MC Component Methods -------------------------------------------------------------------------

  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceContext = ctx;
  }

  public void start()
  {
    String uniqueDeviceIdentifier = getUniqueDeviceIdentifier();

    // TODO: AddressTable component name should be injected...

    StringBuilder builder = new StringBuilder(1024);

    builder
        .append("header\n")
        .append("{\n")
        .append("  version = 1\n")
        .append("  hop = 1 \n")
        .append("  uid = FF").append(uniqueDeviceIdentifier).append("01 \n")
        .append("  source = OpenRemote.")
            .append(serviceContext.getBeanMetaData().getName()).append(":")
            .append(getRemoteControllerHost()).append(":").append(getRemoteControllerPort()).append("\n")
        .append("  class = Registration.Gateway.X10 \n")
        .append("} \n");

    String msg = builder.toString();

    try
    {
      serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "addDevice",
          new Object[] { msg, serviceContext },
          new String[] { String.class.getName(), KernelControllerContext.class.getName()}
      );
    }
    catch (Throwable t)
    {
      log.error("Unable to register X10 control protocol gateway: " + t);
    }

    try
    {
      SocketController socketController = new SocketController(remoteHost, remotePort);

      // just playing...

      socketController.addCommand(new Command("A1", Command.ALL_LIGHTS_ON));
    }
    catch (ConnectException e)
    {
      log.warn("Unable to connect to X10 daemon. Has it been started?");

      // TODO : service status
    }
    catch (IOException e)
    {
      log.warn(e);

      // TODO : service status
    }
  }


  // JavaBean Properties --------------------------------------------------------------------------

  public void setRemoteControllerPort(int port)
  {
    this.remotePort = port;
  }

  public int getRemoteControllerPort()
  {
    return remotePort;
  }

  public void setRemoteControllerHost(String host)
  {
    this.remoteHost = host;
  }

  public String getRemoteControllerHost()
  {
    return remoteHost;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public void sendCommand(String messageFormat)
  {
    StringTokenizer tokenizer = new StringTokenizer(messageFormat, "{}");

    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken().trim();

      if (token.equalsIgnoreCase("X10 Command"))
      {
        String properties = tokenizer.nextToken().trim();

        StringTokenizer propertyTokenizer = new StringTokenizer(properties, "=\n");

        propertyTokenizer.nextToken();  // should be X10 address field...

        String x10Address = propertyTokenizer.nextToken();

        propertyTokenizer.nextToken();  // should be X10 command field...

        String x10Command = propertyTokenizer.nextToken();


        System.out.println("!!! INVOKED " + x10Address + ":" + x10Command);
      }
    }
    
  }


  // Private Instance Methods ---------------------------------------------------------------------


  private String getUniqueDeviceIdentifier()
  {

    // TODO : AddressTable component name should be injected...

    try
    {
      return (String) serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "assignDeviceID",
          null,
          null
      );
    }
    catch (Throwable t)
    {
      throw new Error(t);
    }
  }

}
