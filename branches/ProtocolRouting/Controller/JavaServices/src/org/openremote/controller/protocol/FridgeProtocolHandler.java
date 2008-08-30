package org.openremote.controller.protocol;

import org.openremote.controller.router.Router;
import org.jboss.kernel.Kernel;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 */
public class FridgeProtocolHandler
{

  private Kernel kernel = null;


  public void setKernel(Kernel kernel)
  {
    this.kernel = kernel;
  }

  public void start()
  {

    System.out.println("Fridge protocol handler starting...");


    Thread thread = new Thread(new Runnable()
    {
      public void run()
      {
        ServerSocket serverSocket = null;

        try
        {
          while (true)
          {
            serverSocket = new ServerSocket(1111);

            final Socket socket = serverSocket.accept();

            Thread socketThread = new Thread(new Runnable()
            {
              public void run()
              {
                try
                {
                  ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

                  FridgeMessage message = (FridgeMessage)input.readObject();

                  try
                  {
                    kernel.getBus().invoke(
                        "Router",
                        "route",
                        new Object[] { "Fridge", message },
                        new String[] { String.class.getName(), Object.class.getName() }
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
                catch (ClassNotFoundException e)
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

    System.out.println("Fridge protocol handler started.");
  }


}

