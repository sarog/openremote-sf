package org.openremote.controller.router;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Juha
 * Date: 27.8.2008
 * Time: 10:22:21
 * To change this template use File | Settings | File Templates.
 */
public class Fridge
{


// Fridge implementation ----


  class FridgeProtocolTranslator
  {
    Router router;

    public void setRouter(Router router)
    {
      this.router = router;
    }


    public void start()
    {

    }


    public String translateMessage(FridgeMessage msg)
    {
      return "no payload";
    }

    public FridgeMessage constructMessage(String messageFormat)
    {
      return new FridgeMessage();
    }
  }



  class FridgeProtocolHandler
  {

    Router router;

    public void setRouter(Router router)
    {
      this.router = router;
    }

    public void start()
    {

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

                    router.route("Fridge", message);
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
    }
  }

  class FridgeMessage implements Serializable
  {
    byte address = 0x10;
    byte command = 0x01;
  }

}
