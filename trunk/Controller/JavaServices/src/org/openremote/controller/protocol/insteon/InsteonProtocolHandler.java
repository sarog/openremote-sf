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
package org.openremote.controller.protocol.insteon;

import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.kernel.spi.dependency.KernelControllerContext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Sender and receiver of standard and extended INSTEON messages.  <p>
 *
 * Note that this implementation creates INSTEON messages as per the specification. Typical
 * INSTEON integration through devices such as the INSTEON PowerLine Modem (INSTEON PLM) seem
 * to have a different, slightly modified, write protocol. As such, this implementation is not
 * suitable for other than prototyping, or for Ethernet/IP network integration of INSTEON standard
 * protocol.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class InsteonProtocolHandler
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Default port this protocol handler binds to listen to incoming INSTEON messages: {@value}
   */
  public final static int DEFAULT_PORT = 11111;


  // Instance Fields ------------------------------------------------------------------------------
  
  private boolean running = true;

  private String uniqueDeviceIdentifier = null;

  private int serverPort = DEFAULT_PORT;

  private KernelControllerContext serviceCtx;


  // Microcontainer Component Methods -------------------------------------------------------------

  @Inject (fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    this.serviceCtx = ctx;
  }


  public void setPort(int port)
  {
    this.serverPort = port;
  }

  public int getPort()
  {
    return serverPort;
  }

  public void start()
  {
    System.out.println("INSTEON protocol handler starting...");

    // get unique device identifier for this protocol handler

    uniqueDeviceIdentifier = getUniqueDeviceIdentifier();

    System.out.println("UID = " + uniqueDeviceIdentifier);

    // listen for incoming INSTEON messages...

    Thread thread = new Thread(new Runnable()
    {
      public void run()
      {
        ServerSocket serverSocket = null;

        try
        {
          serverSocket = new ServerSocket(getPort());


          while (running)
          {
            final Socket socket = serverSocket.accept();

            Thread socketThread = new Thread(new Runnable()
            {
              public void run()
              {
                try
                {
                  BufferedInputStream input = new BufferedInputStream(socket.getInputStream());

                  // TODO : INSTEON extended message

                  byte[] message = new byte[10];

                  int bytesRead = input.read(message, 0, 10);

                  if (bytesRead == -1)
                  {
                    return;
                  }

                  if (bytesRead != 10)
                  {
                    // TODO: log error
                    System.out.println("Received incorrect number of bytes: " + bytesRead);
                    return;
                  }

                  String controlMessage = createMessage(message);

                  try
                  {
                    serviceCtx.getKernel().getBus().invoke(
                        "ControlProtocol/Router",
                        "route",
                        new Object[] { controlMessage },
                        new String[] { String.class.getName() }
                    );
                  }
                  catch (Throwable t)
                  {
                    System.out.println(t);
                  }
                }
                catch (IOException e)
                {
                  System.out.println("Socket Exception: " + e);
                }
                finally
                {
                  try
                  {
                    socket.close();
                  }
                  catch (IOException e)
                  {
                    System.out.println(e);
                  }
                }
              }
            });

            socketThread.start();
          }
        }
        catch (IOException e)
        {
          System.out.println("Server exception: " + e);
        }
        finally
        {
          try
          {
            if (serverSocket != null)
              serverSocket.close();
          }
          catch (IOException ignored) {}
        }
      }
    });

    thread.start();

    System.out.println("INSTEON protocol handler started.");
  }


  // Public Instance Methods ----------------------------------------------------------------------




  // Private Instance Methods ---------------------------------------------------------------------

  private String createMessage(byte[] insteonMessage)
  {
    if (isDeviceIdentificationBroadcast(insteonMessage))
    {
      return createDeviceRegistrationMessage(insteonMessage);
    }

    // TODO: regular insteon commands

    System.out.println("I THINK IT WAS A REGULAR INSTEON MSG");
    
    return "";
  }

  private String createDeviceRegistrationMessage(byte[] deviceIdentificationBroadcast)
  {
    StringBuilder builder = new StringBuilder(1024);

    builder.append("header\n");
    builder.append("{\n");
    builder.append("  version = 1\n");
    builder.append("  hop = 1\n");
    builder.append("  uid = FF").append(uniqueDeviceIdentifier).append("01\n");
    builder.append("  source = OpenRemote.").append(serviceCtx.getBeanMetaData().getName()).append(":").append(getPort()).append("\n");
    builder.append("  class = DeviceRegistration.INSTEON\n");
    builder.append("}\n");

    builder.append("INSTEON Device Identification Broadcast\n");
    builder.append("{\n");
    builder.append("  FromAddress = ").append(getInsteonFromAddress(deviceIdentificationBroadcast)).append("\n");
    builder.append("  DeviceCategory = ").append(getInsteonDeviceCategory(deviceIdentificationBroadcast)).append("\n");
    builder.append("  DeviceDescriptor = ").append(getInsteonDeviceDescriptor(deviceIdentificationBroadcast)).append("\n");
    builder.append("  FirmwareRevision = ").append(getInsteonFirmwareRevision(deviceIdentificationBroadcast)).append("\n");
    builder.append("  DeviceAttributes = ").append(getInsteonDeviceAttributes(deviceIdentificationBroadcast)).append("\n");
    builder.append("}");
    
    System.out.println(builder.toString());
    
    return builder.toString();
  }

  private boolean isDeviceIdentificationBroadcast(byte[] insteonMessage)
  {
    int flags = insteonMessage[6];

    if (flags < 0)
      flags *= -1;

    System.out.println("INSTEON MSG FLAGS: " + flags);

    flags = (flags >>> 4) & 0x000000FF;

    System.out.println("CHECK: " + Integer.toHexString(flags) );
    
    return flags == 0x08 && insteonMessage[7] == 0x01;
  }

  private String getUniqueDeviceIdentifier()
  {
    try
    {
      return (String) serviceCtx.getKernel().getBus().invoke(
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

  private String getInsteonFromAddress(byte[] insteonMessage)
  {
    int[] addressBytes = new int[3];

    for (int i = 0; i < 3; ++i)
    {
      addressBytes[i] = insteonMessage[i] & 0x000000FF;
    }

    StringBuilder builder = new StringBuilder(6);

    for (int address : addressBytes)
    {
      if (address <= 0x0F)
      {
        builder.append("0");
      }

      builder.append(Integer.toHexString(address).toUpperCase());
    }

    return builder.toString();
  }

  private String getInsteonDeviceCategory(byte[] insteonMessage)
  {
    int category = insteonMessage[3] & 0xF0;

    if (category <= 0x0F)
      return "0" + Integer.toHexString(category).toUpperCase();
    else
      return Integer.toHexString(category).toUpperCase();
  }

  private String getInsteonDeviceDescriptor(byte[] insteonMessage)
  {
    int descriptorMSB = insteonMessage[3] & 0x0F;
    int descriptorLSB = insteonMessage[4];

    String lsbValue;

    if (descriptorLSB <= 0x0F)
      lsbValue = "0" + Integer.toHexString(descriptorLSB & 0x000000FF).toUpperCase();
    else
      lsbValue = Integer.toHexString(descriptorLSB & 0x000000FF).toUpperCase();

    return Integer.toHexString(descriptorMSB) + lsbValue;
  }

  private String getInsteonFirmwareRevision(byte[] insteonMessage)
  {
    int revision = insteonMessage[5];

    return Integer.toHexString(revision & 0x000000FF).toUpperCase();
  }

  private String getInsteonDeviceAttributes(byte[] insteonMessage)
  {

    int attributes = insteonMessage[8];

    return Integer.toHexString(attributes & 0x000000FF).toUpperCase();
  }
}
