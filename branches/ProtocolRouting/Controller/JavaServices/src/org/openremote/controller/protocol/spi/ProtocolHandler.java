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

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;

import java.lang.reflect.Method;


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

  private KernelControllerContext serviceContext;



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

  // TODO : this should be annotation based instead

  public void startService()
  {
  }


  // Public Instance Methods ----------------------------------------------------------------------


  // Protected Instance Methods -------------------------------------------------------------------

  protected String createMessageHeaderBlock()
  {
    return messageFactory.createHeaderBlock(messageVersion); 
  }


  protected void registerControlProtocolGateway(String msg)
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


  protected String getComponentName()
  {
    return serviceContext.getBeanMetaData().getName();
  }
}
