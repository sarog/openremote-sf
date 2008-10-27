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

import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;
import org.openremote.controller.protocol.spi.MessageFactory;
import org.openremote.controller.protocol.spi.ProtocolHandler;
import x10.CM11ASerialController;
import x10.CM17ASerialController;
import x10.Command;
import x10.Controller;
import x10.net.SocketController;

import java.io.IOException;
import java.net.ConnectException;
import java.util.StringTokenizer;

/**
 * This is a control protocol gateway for X10. It's a wrapper around The Java X10 Project API
 * for physical X10 serial I/O and implements a control protocol gateway SPI for
 * OpenRemote Controller.
 *
 * The X10 gateway can be run in IPC mode where we connect to a separate process via IP socket
 * to handle serial I/O. This is recommended for the highest level of stability for the entire
 * system. However, it does use more system resources so 'IN_VM' mode can also be used to handle
 * serial I/O directly within this same Java process.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class X10ProtocolHandler extends ProtocolHandler
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Log category name for this component. The actual full log category can be constructed by
   * appending {@link org.openremote.controller.core.Bootstrap#ROOT_LOG_CATEGORY} and this
   * string using a dot '.' separator (ROOT_LOG_CATEGORY.LOG_CATEGORY).
   *
   * Value: {@value}
   */
  public final static String LOG_CATEGORY = "X10-GATEWAY";

  
  // Enums ----------------------------------------------------------------------------------------

  /**
   * Defines the process mode for this X10 Gateway.
   */
  public enum Mode
  {
    /**
     * Inter-process mode. This is the default. The serial I/O is done in a separate process
     * from the host process for extra bit of stability (in case of process failures).
     */
    IPC,

    /**
     * In VM does all the serial I/O in the same process -- this can save some system resources
     * but exposes the running process to potential failures stemming from I/O drivers.
     */
    IN_VM
  }

  /**
   * Defines the serial port protocol used by this X10 Gateway.
   */
  public enum SerialPortModule
  {
    /**
     * X10 CM11A serial module.
     */
    CM11A,

    /**
     * X10 CM17A ("Firecracker" RF) serial module.
     */
    CM17A
  }


  // Class Members --------------------------------------------------------------------------------


  /**
   * Logger API for this component. Currently uses the JBoss logging API.
   */
  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Remote host for Java X10 socket controller
   */
  private String remoteHost = "127.168.0.1";

  /**
   * Port number for Java X10 socket controller
   */
  private int remotePort = 10000;

  /**
   * X10 controller. This is initialized based on configuration at component deployment phase --
   * socket controller in IPC mode or specific serial module protocol (CM11A, CM17A) in IN_VM
   * mode.
   */
  private Controller x10Controller;

  /**
   * Process mode for this X10 Gateway.
   *
   * @see X10ProtocolHandler.Mode
   */
  private Mode controllerMode = Mode.IPC;

  /**
   * X10 serial port protocol used in case of 'IN_VM' mode. This field is not relevant in the
   * default 'IPC' mode.
   */
  private SerialPortModule serialModule = SerialPortModule.CM11A;

  /**
   * System specific serial port identifier used to locate the serial port in case of 'IN_VM' mode.
   * E.g. for MS Windows based systems this could be "COM1" or "COM9" depending on configuration.
   *
   * This field is not relevant in the default 'IPC' mode.
   */
  private String commPortIdentifier = "COM1";



  // Constructors ---------------------------------------------------------------------------------

  public X10ProtocolHandler()
  {
    super(MessageFactory.Version.VERSION_1_0_0);
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

  public String getMode()
  {
    return controllerMode.name();
  }

  public void setMode(String mode)
  {
    this.controllerMode = Mode.valueOf(mode);
  }

  public String getCommPortIdentifier()
  {
    return commPortIdentifier;
  }

  public void setCommPortIdentifier(String portIdentifier)
  {
    this.commPortIdentifier = portIdentifier;
  }

  public String getSerialPortModule()
  {
    return serialModule.name();
  }

  public void setSerialPortModule(String serialModule)
  {
    this.serialModule = SerialPortModule.valueOf(serialModule);
  }



  // ProtocolHandler Overrides --------------------------------------------------------------------

  /**
   * TODO
   *
   * Start is invoked by the microcontainer before component deployment is complete and after
   * all configuration properties have been injected and/or set.  We can initialize the component
   * here and make it 'ready'.  <p>
   *
   * The X10 gateway will be registered with the control protocol routing service and receive
   * a device address. The X10 commands may then be initiated from several different sources and
   * X10 events can be distributed and made accessible in a heterogeneous scene management
   * environment.  <p>
   *
   * The X10 controller will be instantiated here based on the configuration settings. Either
   * socket based IPC mode (default) or IN_VM direct serial using CM11A or CM17A can be configured.
   */
  @Override public void startService()
  {
    // Add properties specific to this X10 protocol handler for registration with router...

    addGatewayProperties();

    // Send registration message, this will assign an address to this X10 gateway...

    registerControlProtocolGateway();

    // Start X10 controller in either IPC or IN_VM mode depending on configuration...

    startX10Controller();
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public void sendCommand(String messageFormat)
  {
    // TODO : Add Message to SPI...

    StringTokenizer tokenizer = new StringTokenizer(messageFormat, "{}");

    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken().trim();

      if (token.equalsIgnoreCase("X10 Command"))
      {
        String properties = tokenizer.nextToken().trim();

        StringTokenizer propertyTokenizer = new StringTokenizer(properties, "=\n");

        propertyTokenizer.nextToken();  // should be X10 address field...

        String x10Address = propertyTokenizer.nextToken().trim();

        propertyTokenizer.nextToken();  // should be X10 command field...

        String x10Command = propertyTokenizer.nextToken().trim();

        // TODO : should be debug level logging...

        log.info("Sending command " + x10Command + " to " + x10Address);

        if (x10Controller != null)
        {
          x10Controller.addCommand(new Command(x10Address, Byte.valueOf(x10Command)));
        }

        else
        {
          log.error("X10 Controller has not been initialized. Unable to send command");
        }
      }
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void startX10Controller()
  {
    if (controllerMode == Mode.IPC)
    {
      try
      {
          x10Controller = new SocketController(remoteHost, remotePort);
      }
      catch (ConnectException e)
      {
        log.warn("Unable to connect to X10 daemon. Has it been started?");

        // TODO : service status
      }
      catch (IOException e)
      {
        log.warn("I/O error from X10 daemon: " + e, e);

        // TODO : service status
      }
    }

    else
    {
      try
      {
        if (serialModule == SerialPortModule.CM11A)
        {
          x10Controller = new CM11ASerialController(commPortIdentifier);
        }

        else
        {
          x10Controller = new CM17ASerialController(commPortIdentifier);
        }
      }
      catch (IOException e)
      {
        log.error("Unable to initialize in-vm X10 serial module: " + e, e);
      }
    }
  }


  private void addGatewayProperties()
  {
    super.addGatewayProperty(MessageFactory.PROTOCOL_GATEWAY_MODE_PROPERTY, controllerMode.name());

    switch (controllerMode)
    {
      case IPC:
        super.addGatewayProperty(
            MessageFactory.IPC_PROTOCOL_GATEWAY_IPADDRESS_PROPERTY,
            getRemoteControllerHost()
        );

        super.addGatewayProperty(
            MessageFactory.IPC_PROTOCOL_GATEWAY_PORT_PROPERTY,
            String.valueOf(getRemoteControllerPort())
        );

        break;

      case IN_VM:
        super.addGatewayProperty(
            MessageFactory.X10_INVM_PROTOCOL_GATEWAY_COMMS_PROPERTY,
            getCommPortIdentifier()
        );

        super.addGatewayProperty(
            MessageFactory.X10_INVM_PROTOCOL_GATEWAY_MODULE_PROPERTY,
            getSerialPortModule()
        );

        break;

      default:
        throw new Error(
            "Implementation Error: nothing known about controller mode: " + controllerMode + ". " +
            "The codebase must be updated to handle this case."
        );
    }
  }

}
