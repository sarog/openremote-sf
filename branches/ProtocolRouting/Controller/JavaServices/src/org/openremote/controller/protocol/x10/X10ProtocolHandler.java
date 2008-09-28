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
import org.openremote.controller.protocol.spi.MessageFactory;
import org.openremote.controller.protocol.spi.MandatoryHeader;
import static org.openremote.controller.protocol.spi.MessageFactory.GATEWAY_REGISTRATION_MESSAGE_CLASS;
import static org.openremote.controller.protocol.spi.MessageFactory.X10_CONTROL_PROTOCOL;
import x10.net.SocketController;
import x10.Command;
import x10.Controller;
import x10.CM11ASerialController;
import x10.CM17ASerialController;

import java.io.IOException;
import java.net.ConnectException;
import java.util.StringTokenizer;
import java.util.List;

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
public class X10ProtocolHandler
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

  /**
   *
   */

  // TODO :
  public final static String MESSAGE_SERIALIZATION_VERSION = MessageFactory.VERSION_1;

  
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
   * Service context for this component. Service context can be used to access the microcontainer
   * and kernel that is used to deploy this component.
   */
  private KernelControllerContext serviceContext;

  /**
   * Remote host for Java X10 socket controller
   */
  private String remoteHost = "127.168.0.1";

  /**
   * Port number for Java X10 socket controller
   */
  private int remotePort = 9999;

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
   * The X10 gateway will be registered with the control protocol routing service and receive
   * a device address. The X10 commands may then be initiated from several different sources and
   * X10 events can be distributed and made accessible in a heterogeneous scene management
   * environment.  <p>
   *
   * The X10 controller will be instantiated here based on the configuration settings. Either
   * socket based IPC mode (default) or IN_VM direct serial using CM11A or CM17A can be configured.
   */
  public void start()
  {

    // TODO : create MessageFactory as part of the SPI

    String msg = createGatewayRegistrationMessage();

    registerGateway(msg);



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


  private String createGatewayRegistrationMessage()
  {
    StringBuilder builder = new StringBuilder(1024);

    builder.append(createMessageHeaderBlock());
    builder.append(createOpenRemoteComponentBlock());
    builder.append(createControlProtocolGatewayBlock());

    return builder.toString();
  }


  /**
   * TODO
   *
   * @return
   *
   * @throws Error    TODO
   */
  private String createMessageHeaderBlock()
  {
    // start block...

    StringBuilder builder = new StringBuilder(256);
    builder.append(MessageFactory.HEADER_BLOCK).append("\n{\n  ");

    // add mandatory headers...

    for (MandatoryHeader header : MessageFactory.getMandatoryHeaders(MESSAGE_SERIALIZATION_VERSION))
    {
      builder.append(header.name()).append(" = ");

      switch (header)
      {
        case VERSION:
          builder.append(MESSAGE_SERIALIZATION_VERSION).append("\n  ");
          break;

        case HOP:
          builder.append("1\n  ");
          break;

        case UID:
          builder.append(getDeviceUID()).append("\n  ");
          break;

        case SOURCE:
          builder.append(getAddress()).append(":").append(getComponentName()).append("\n  ");
          break;

        case CLASS:
          builder
              .append(GATEWAY_REGISTRATION_MESSAGE_CLASS).append(".")
              .append(X10_CONTROL_PROTOCOL).append("\n  ");
          break;

        default:
          throw new Error(
              "Implementation Error. Unexpected mandatory header for version " +
              MESSAGE_SERIALIZATION_VERSION + " of message serialization format: " + header
          );
      }
    }

    // end block....

    return builder.append("\n}\n").toString();
  }


  private String createOpenRemoteComponentBlock()
  {
    // start block...

    StringBuilder builder = new StringBuilder(256);
    builder.append(MessageFactory.OPENREMOTE_COMPONENT_BLOCK).append("\n{\n  ");

    // Add component name...

    builder
        .append(MessageFactory.COMPONENT_NAME_PROPERTY).append(" = ")
        .append(getComponentName()).append("\n");

    // End block...

    return builder.append("}\n\n").toString();
  }


  private String createControlProtocolGatewayBlock()
  {
    // start block...

    StringBuilder builder = new StringBuilder(256);
    builder.append(MessageFactory.PROTOCOL_GATEWAY_BLOCK).append("\n{\n  ");

    // Add IPC properties...

    builder
        .append(MessageFactory.PROTOCOL_GATEWAY_MODE_PROPERTY).append(" = ")
        .append(controllerMode.name()).append("\n  ");

    switch (controllerMode)
    {
      case IPC:
        builder
            .append(MessageFactory.IPC_PROTOCOL_GATEWAY_IPADDRESS_PROPERTY).append(" = ")
            .append(getRemoteControllerHost()).append("\n  ");

        builder
            .append(MessageFactory.IPC_PROTOCOL_GATEWAY_PORT_PROPERTY).append(" = ")
            .append(getRemoteControllerPort()).append("\n  ");
        break;

      case IN_VM:
        builder
            .append(MessageFactory.X10_INVM_PROTOCOL_GATEWAY_COMMS_PROPERTY).append(" = ")
            .append(getCommPortIdentifier()).append("\n  ");
        builder
            .append(MessageFactory.X10_INVM_PROTOCOL_GATEWAY_MODULE_PROPERTY).append(" = ")
            .append(getSerialPortModule()).append("\n  ");
        break;

      default:
        throw new Error(
            "Implementation Error: nothing known about controller mode: " + controllerMode + ". " +
            "The codebase must be updated to handle this case."
        );
        
    }

    // End block...

    return builder.append("}\n\n").toString();
  }

  /**
   * Asks for the address table component to assign a unique device ID to this control protocol
   * gateway
   *
   * @return    TODO
   */
  private String getDeviceUID()
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
      // TODO

      throw new Error(t);
    }
  }

  private String getAddress()
  {
    // TODO : AddressTable component name should be injected...

    try
    {
      return (String) serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "getNextFreeAddress",
          null,
          null
      );
    }
    catch (Throwable t)
    {
      // TODO

      throw new Error(t);
    }
  }

  private void registerGateway(String msg)
  {
    // TODO: AddressTable component name should be injected...

    try
    {
      serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "registerDevice",
          new Object[] { msg },
          new String[] { String.class.getName() }
      );
    }
    catch (Throwable t)
    {
      log.error("Unable to register X10 control protocol gateway: " + t);
    }    
  }

  private String getComponentName()
  {
    return serviceContext.getBeanMetaData().getName();
  }
}
