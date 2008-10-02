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
package org.openremote.controller.protocol.spi;

import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class ProtocolHandler
{

  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "PROTOCOL HANDLER";


  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------

  private MessageFactory messageFactory;

  private MessageFactory.Version messageVersion;

  /**
   * Service context for this component. Service context can be used to access the microcontainer
   * and kernel that is used to deploy this component.
   */
  private KernelControllerContext serviceContext;

  private List<String> gatewayProperties = new ArrayList<String>(5);



  // Constructors ---------------------------------------------------------------------------------

  /**
   *
   * @param serializationVersion
   */
  public ProtocolHandler(MessageFactory.Version serializationVersion)
  {
    messageVersion = serializationVersion;
  }



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


  public void start()
  {
    messageFactory = new MessageFactory(serviceContext.getKernel());

    startService();
  }



  // Public Instance Methods ----------------------------------------------------------------------


  // Protected Instance Methods -------------------------------------------------------------------


  protected void startService()
  {
    // TODO : this should be annotation based instead
  }

/*

  ... This will be part of the input in the SPI...


  protected void sendMessage(String messageFormat)
  {
    // version should be the one embedded in the message, and match to the supported messageversion format...

    this.sendCommand(MessageFactory.createMessage(messageFormat, messageVersion));
  }

  protected void sendCommand(Message msg)
  {
    log.warn(getComponentName() + " does not implement sendCommand(), message ignored");
  }
*/
  

  protected void registerControlProtocolGateway()
  {
    StringBuilder builder = new StringBuilder(1024);

    builder.append(messageFactory.createHeaderBlock(messageVersion));
    builder.append(messageFactory.createOpenRemoteComponentBlock(getComponentName(), messageVersion));
    builder.append(createGatewayBlock());

    // TODO: AddressTable component name should be injected...

    try
    {
      serviceContext.getKernel().getBus().invoke(
          "ControlProtocol/AddressTable",
          "registerDevice",
          new Object[] { builder.toString() },
          new String[] { String.class.getName() }
      );
    }
    catch (Throwable t)
    {
      log.error("Unable to register X10 control protocol gateway: " + t);
    }
  }


  protected void addGatewayProperty(String name, String value)
  {
    gatewayProperties.add(name + " = " + value);
  }


  protected String getComponentName()
  {
    return serviceContext.getBeanMetaData().getName();
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private String createGatewayBlock()
  {
    if (gatewayProperties.isEmpty())
        return "";

    // start block...

    StringBuilder builder = new StringBuilder(256);
    builder.append(MessageFactory.PROTOCOL_GATEWAY_BLOCK).append("\n{\n  ");

    // Add gateway specific properties...

    for (String property : gatewayProperties)
    {
      builder.append("  ").append(property).append("\n");
    }

    // End block...

    return builder.append("}\n\n").toString();
  }

}
