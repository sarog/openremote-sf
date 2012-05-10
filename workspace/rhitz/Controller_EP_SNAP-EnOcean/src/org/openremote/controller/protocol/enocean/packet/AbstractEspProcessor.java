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
package org.openremote.controller.protocol.enocean.packet;

import org.openremote.controller.protocol.enocean.ConfigurationException;
import org.openremote.controller.protocol.enocean.ConnectionException;
import org.openremote.controller.protocol.enocean.port.EspPort;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * A common superclass implementation for different versions of the EnOcean Serial Protocol
 * processor implementations to reuse code. <p>
 *
 * Subclasses should provide an implementation of {@link #getResponseTimeout()},
 * {@link #createPortReaderBuffer()} and {@link #dispatchPacket(EspPacket)}.
 *
 *
 * @author Rainer Hitz
 */
public abstract class AbstractEspProcessor<T extends EspPacket> implements EspProcessor<T>
{
  // TODO :
  //
  //    synchronization
  //

  // Class Members --------------------------------------------------------------------------------

  /**
   * TODO : Logger
   */


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Serial port for sending and receiving data.
   */
  private EspPort port;

  /**
   * Indicates the start state of the ESP processor.
   */
  private boolean started;

  /**
   * Thread implementation for reading data from the serial {@link #port}).
   */
  private PortReader portReader;

  /**
   * Queue for sending response packets from {@link #portReader port reader} thread to
   * the waiting thread.
   */
  protected SynchronousQueue<T> responseQueue = new SynchronousQueue<T>();


  // Constructors -------------------------------------------------------------------------------

  /**
   * Constructs a new processor with a given serial port.
   *
   * @param port serial port for sending and receiving data
   */
  public AbstractEspProcessor(EspPort port)
  {
    this.started = false;

    this.port = port;

    this.portReader = new PortReader();
  }


  // Implements EspProcessor ----------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void start() throws ConfigurationException, ConnectionException
  {
    if(started)
      return;

    responseQueue.clear();

    port.start();

    portReader.start();

    started = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override public void stop() throws ConnectionException
  {
    if(!started)
      return;

    portReader.stop();

    port.stop();

    started = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override public T sendRequest(T packet) throws ConnectionException, InterruptedException
  {
    responseQueue.clear();

    port.send(packet.asByteArray());

    T response = responseQueue.poll(getResponseTimeout(), TimeUnit.MILLISECONDS);

    if(response == null)
    {
      // TODO : exception
      return null;
    }

    return response;
  }

  /**
   * {@inheritDoc}
   */
  @Override public void sendResponse(T packet) throws ConnectionException
  {
    port.send(packet.asByteArray());
  }

  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Returns response timeout value. <p>
   *
   * This timeout value is the max time a request waits for it's response from the EnOcean module.
   *
   * @return response timeout value [milliseconds]
   */
  protected abstract long getResponseTimeout();

  /**
   * Returns a new buffer instance used by the {@link #portReader port reader} to store data
   * received from the {@link #port serial port}.
   *
   * @return new port reader buffer instance
   */
  protected abstract AbstractPortReaderBuffer<T> createPortReaderBuffer();

  /**
   * Distributes packets received from the EnOcean module based on the packet type to an
   * appropriate listener or in case of a response packet the packet is added to the
   * {@link #responseQueue response queue}.
   *
   * @param packet ESP packet to be dispatched
   *
   * @throws InterruptedException
   *           if interrupted while dispatching a response packet
   */
  protected abstract void dispatchPacket(T packet) throws InterruptedException;


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Retrieves as many as possible packets from the given receive buffer and distributes
   * them afterwards.
   *
   * @param buffer port reader receive buffer
   *
   * @throws InterruptedException
   *           if interrupted while dispatching a response packet
   */
  private void processPortReaderBuffer(AbstractPortReaderBuffer<T> buffer) throws InterruptedException
  {
    T packet;

    while((packet = buffer.getEspPacket()) != null)
    {
      dispatchPacket(packet);
    }
  }

  /**
   * TODO
   */
  private void handlePortReaderConnectionException(ConnectionException e)
  {
    // TODO
  }


  // Inner Classes --------------------------------------------------------------------------------

  /**
   * Reads data from serial port and stores it in a buffer for further processing.
   */
  private class PortReader implements Runnable
  {

    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Indicates the running state of the reader thread.
     */
    private volatile boolean readerThreadRunning = true;

    /**
     * The actual thread reference.
     */
    private  Thread readerThread;

    /**
     * Buffer used by the reader thread to store received data.
     */
    private AbstractPortReaderBuffer<T> readerBuffer;

    // Instance Methods ---------------------------------------------------------------------------

    /**
     * Starts the reader thread.
     */
    public void start()
    {
      readerThreadRunning = true;

      readerBuffer = AbstractEspProcessor.this.createPortReaderBuffer();

      // TODO : OpenRemoteRuntime.createThread

      readerThread = new Thread(
          this, "EnOcean Serial Protocol Port Reader"
      );

      readerThread.start();
    }

    /**
     * Stops the reader thread.
     */
    public void stop()
    {
      readerThreadRunning = false;

      readerThread.interrupt();

      try
      {
        readerThread.join();
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }
    }


    // Implements Runnable ------------------------------------------------------------------------

    /**
     * Reads received data blocks from the serial port and stores them in the buffer for further
     * processing.
     */
    @Override public void run()
    {
      while (readerThreadRunning)
      {
        try
        {
          readerBuffer.append(read());

          AbstractEspProcessor.this.processPortReaderBuffer(readerBuffer);
        }

        catch(ConnectionException e)
        {
          AbstractEspProcessor.this.handlePortReaderConnectionException(e);
        }

        catch (InterruptedException e)
        {
          readerThreadRunning = false;

          Thread.currentThread().interrupt();
        }
      }
    }


    // Private Methods ----------------------------------------------------------------------------

    /**
     * Reads data block from serial port.
     *
     * @return data block
     *
     * @throws ConnectionException
     *           if a connection error occurred for any reason
     *
     */
    private byte[] read() throws ConnectionException
    {
      return AbstractEspProcessor.this.port.receive();
    }
  }


  /**
   * Buffer implementation used by the {@link PortReader port reader} to store data blocks
   * received from the serial port. <p>
   *
   * The class interface offers methods to append data to the buffer and retrieve
   * completely assembled EnOcean Serial Protocol (ESP) packets from the buffer. <p>
   *
   * Subclasses should provide an implementation of {@link #getEspPacket()} which
   * scans the buffer content for valid ESP packets, removes the packet data from the beginning
   * of the buffer and returns a complete ESP packet if available.
   */
  protected abstract class AbstractPortReaderBuffer<T>
  {

    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Buffer implementation as a "double ended queue" to enable appending data to the end
     * and removing data from the beginning.
     */
    protected Deque<Byte> buffer = new LinkedList<Byte>();


    // Public Methods -----------------------------------------------------------------------------

    /**
     * Appends data to the buffer.
     *
     * @param data data block to be appended
     */
    public void append(byte[] data)
    {
      if(data == null)
        return;

      for (byte dataByte : data)
      {
        buffer.add(dataByte);
      }
    }

    /**
     * Scans the buffer for a valid ESP packet, removes the packet data from the beginning of
     * the buffer and returns the packet.
     *
     * @return complete ESP packet
     */
    public abstract T getEspPacket();


  }
}
