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
package test;

/**
 * Mock INSTEON device sending standard messages.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class InsteonDevice
{
  
  public static void main(String... args)
  {
    if (args.length >= 1)
      startReceiver(Integer.parseInt(args[0]));
    else
      startReceiver(2222);

    sendDeviceIDBroadcast();
  }


  private static void sendDeviceIDBroadcast()
  {
    BufferedOutputStream output = null;

    try
    {
      Socket socket = new Socket("localhost", 11111);

      output = new BufferedOutputStream(socket.getOutputStream());

      byte[] message = new byte[10];

      // From Address...

      message[0] = 0x7F;
      message[1] = 0x00;
      message[2] = (byte)0xFF;

      // To Address ...

      message[3] = (byte)0xA0;    // device id broadcast
      message[4] = (byte)0x81;
      message[5] = (byte)0xCC;

      // Flags...

      message[6] = (byte)0x80;    // broadcast message

      // Command...

      message[7] = 0x01;
      message[8] = (byte)0xFF;

      // CRC...

      message[9] = 0x00;          // would be set by firmware

      output.write(message, 0, 10);
      output.flush();
    }
    catch (UnknownHostException e)
    {
      System.out.println(e);
    }
    catch (IOException e)
    {
      System.out.println(e);
    }
    finally
    {
      try
      {
        if (output != null)
          output.close();
      }
      catch (IOException e)
      {
        System.out.println(e);
      }
    }
  }


  private static void startReceiver(int port)
  {
    try
    {
      final ServerSocket serverSocket = new ServerSocket(port);

      Thread listener = new Thread(new Runnable()
      {
        public void run()
        {
          while (true)
          {
            try
            {
              final Socket socket = serverSocket.accept();

              Thread socketThread = new Thread(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    BufferedInputStream input = new BufferedInputStream(socket.getInputStream());

                    byte[] message = new byte[10];

                    int bytesRead = input.read(message);

                    if (bytesRead != 10)
                    {
                      System.out.println("was expecting 10 bytes");
                      return;
                    }

                    handleMessage(message);
                  }
                  catch (IOException e)
                  {
                    System.out.println(e);
                  }
                }
              });

              socketThread.run();
            }
            catch (IOException e)
            {
              System.out.println(e);
            }
          }
        }
      });

      listener.start();
    }
    catch (IOException e)
    {
      System.out.println(e);
    }
  }

  private static void handleMessage(byte[] message)
  {
    System.out.println("GOT MESSAGE!");
  }
}
