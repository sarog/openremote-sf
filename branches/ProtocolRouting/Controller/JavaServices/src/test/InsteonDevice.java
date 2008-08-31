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
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class InsteonDevice
{
  
  public static void main(String... args)
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

      message[3] = 0x00;    // TODO : device id broadcast
      message[4] = 0x00;
      message[5] = 0x00;

      // Flags...

      message[6] = (byte)0x80;    // broadcast message

      // Command...

      message[7] = 0x01;
      message[8] = 0x00;

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

  
}
