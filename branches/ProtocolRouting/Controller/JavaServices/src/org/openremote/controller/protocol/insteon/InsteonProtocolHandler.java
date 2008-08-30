package org.openremote.controller.protocol.insteon;

import org.jboss.kernel.Kernel;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedInputStream;

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


  // Instance Fields ------------------------------------------------------------------------------
  
  /**
   * We will get to the control protocol router via kernel's invocation bus. This reference
   * will be injected to this bean by the microcontainer upon deployment.
   */
  private Kernel kernel = null;

  private boolean running = true;


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * TODO
   *
   * @param kernel
   */
  public void setKernel(Kernel kernel)
  {
    this.kernel = kernel;
  }

  public void start()
  {

    System.out.println("INSTEON protocol handler starting...");

    // listen for incoming INSTEON messages...

    Thread thread = new Thread(new Runnable()
    {
      public void run()
      {
        ServerSocket serverSocket = null;

        try
        {
          while (running)
          {
            serverSocket = new ServerSocket(1111);

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

                  String controlMessage = createInsteonMessage(message);

                  try
                  {
                    kernel.getBus().invoke(
                        "Router",
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
                  System.out.println(e);
                }
              }
            });

            socketThread.start();
          }
        }
        catch (IOException e)
        {
          System.out.println(e);
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


  // Private Instance Methods ---------------------------------------------------------------------

  private String createInsteonMessage(byte[] insteonMessage)
  {
    if (isDeviceIdentificationBroadcast(insteonMessage))
    {
      return createInsteonDeviceRegistrationMessage(insteonMessage);
    }

    // TODO: regular insteon commands

    return "";
  }

  private String createInsteonDeviceRegistrationMessage(byte[] deviceIdentificationBroadcast)
  {

    return "";
  }

  private boolean isDeviceIdentificationBroadcast(byte[] insteonMessage)
  {
    int flags = insteonMessage[6];

    flags = flags >> 4 & 0x000000FF;

    return flags == 0x1000;
  }
}
