/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.enocean.port;

import org.openremote.controller.protocol.port.Message;
import org.openremote.controller.protocol.port.Port;
import org.openremote.controller.protocol.port.PortException;
import org.openremote.controller.protocol.port.pad.AbstractPort;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TODO
 *
 * @author Rainer Hitz
 */
public class RXTXPort implements Port
{

  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * TODO
   */
  private CommPortIdentifier portIdentifier;

  /**
   * TODO
   */
  private SerialPort serialPort;

  /**
   * TODO
   */
  private InputStream inputStream;

  /**
   * TODO
   */
  private OutputStream outputStream;

  /**
   * TODO
   */
  private BlockingQueue<Byte> inputQueue;

  /**
   * TODO
   */
  private BlockingQueue<Byte> outputQueue;

  /**
   * TODO
   */
  private Thread writerThread;

  /**
   * TODO
   */
  private Map<String, Object> configuration;


  // Implements Port ------------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void configure(Map<String, Object> configuration) throws IOException, PortException
  {
    this.configuration = configuration;
  }

  /**
   * {@inheritDoc}
   */
  @Override public void start() throws IOException, PortException
  {
    String portName = (String)configuration.get(AbstractPort.PORT_ID);
    String speedAsString = (String)configuration.get(AbstractPort.PORT_SPEED);

    try
    {
      portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    }
    catch (NoSuchPortException e)
    {
      // TODO : log
      System.out.println("Exception while configuring TXRX port: " + e.getMessage());

      throw new PortException(PortException.INVALID_CONFIGURATION);
    }

    CommPort commPort = null;

    try
    {
      // TODO : timeout constant
      commPort = portIdentifier.open(this.getClass().getName(), 2000);
    }
    catch (PortInUseException e)
    {
      // TODO : log
      System.out.println("Exception while configuring TXRX port: " + e.getMessage());

      throw new PortException(PortException.INVALID_CONFIGURATION);
    }

    if(commPort instanceof SerialPort)
    {
      serialPort = (SerialPort)commPort;
    }
    else
    {
      // TODO : log
      throw new PortException(PortException.INVALID_CONFIGURATION);
    }

    try
    {
      serialPort.setSerialPortParams(
          Integer.parseInt(speedAsString), SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1, SerialPort.PARITY_NONE
      );
    }
    catch (UnsupportedCommOperationException e)
    {
      // TODO : log
      System.out.println("Exception while configuring TXRX port: " + e.getMessage());

      throw new PortException(PortException.INVALID_CONFIGURATION);
    }

    // TODO : CONSTANTS
    inputQueue = new LinkedBlockingQueue<Byte>(1024);
    outputQueue = new LinkedBlockingQueue<Byte>(1024);

    inputStream = serialPort.getInputStream();
    outputStream = serialPort.getOutputStream();


    try
    {
      serialPort.addEventListener(new SerialReader(inputStream, inputQueue));
      serialPort.notifyOnDataAvailable(true);
    }
    catch (TooManyListenersException e)
    {
      // TODO : log
      System.out.println("Exception while starting TXRX port: " + e.getMessage());

      throw new PortException(PortException.INVALID_CONFIGURATION);
    }

    writerThread = new Thread(new SerialWriter(outputStream, outputQueue));
    //writerThread.setDaemon(true);
    writerThread.start();
  }

  /**
   * {@inheritDoc}
   */
  @Override public void stop() throws IOException, PortException
  {
    if(writerThread != null)
    {
      writerThread.interrupt();

      try
      {
        writerThread.join();
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }
    }

    outputStream.close();
    inputStream.close();

    // Hangs on Mac OS X
    //serialPort.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override public void send(Message message) throws IOException, PortException
  {
    byte[] data = message.getContent();

    if(data == null)
    {
      return;
    }

    for(byte dataByte : data)
    {
      try
      {
        outputQueue.put(dataByte);
      }

      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override public Message receive() throws IOException
  {
    byte[] data = null;
    int size = inputQueue.size();

    try
    {
      if(size > 0)
      {
        data = new byte[size];

        for(int i = 0; i < size; i++)
        {
          data[i] = inputQueue.take();
        }
      }

      else
      {
        data = new byte[1];

        data[0] = inputQueue.take();
      }
    }

    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();

      return new Message(new byte[0]);
    }

    Message msg = new Message(data);

    return msg;
  }


  /**
   * TODO
   */
  private static class SerialWriter implements Runnable
  {
    /**
     * TODO
     */
    private OutputStream outputStream;

    /**
     * TODO
     */
    private BlockingQueue<Byte> outputQueue;


    /**
     * TODO
     *
     * @param stream
     * @param queue
     */
    public SerialWriter(OutputStream stream, BlockingQueue<Byte> queue)
    {
      this.outputStream = stream;
      this.outputQueue = queue;
    }


    @Override public void run()
    {
      byte dataByte;

      while(true)
      {
        try
        {
          dataByte = outputQueue.take();
        }

        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();
          break;
        }

        try
        {
          outputStream.write(dataByte);
        }
        catch (IOException e)
        {
          // TODO : log
          System.out.println("IOException in TXT SerialWriter:" + e.getMessage());

          break;
        }
      }
    }
  }

  /**
   * TODO
   */
  private static class SerialReader implements SerialPortEventListener
  {
    /**
     * TODO
     */
    private InputStream inputStream;

    /**
     * TODO
     */
    private BlockingQueue<Byte> inputQueue;

    /**
     * TODO
     *
     * @param stream
     * @param queue
     */
    public SerialReader(InputStream stream, BlockingQueue<Byte> queue)
    {
      this.inputStream = stream;
      this.inputQueue = queue;
    }


    @Override public void serialEvent(SerialPortEvent serialPortEvent)
    {
      int data;

      if(serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
      {
        try
        {
          while((data = inputStream.read()) > -1)
          {
            try
            {
              inputQueue.put((byte)data);
            }

            catch (InterruptedException e)
            {
              Thread.currentThread().interrupt();
              break;
            }
          }
        }

        catch (IOException e)
        {
          // TODO : log
          System.out.println("IOException in TXT SerialReader:" + e.getMessage());
        }
      }
    }
  }
}
