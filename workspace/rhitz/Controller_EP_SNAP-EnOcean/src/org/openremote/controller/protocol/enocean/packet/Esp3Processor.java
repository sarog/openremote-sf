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

import org.openremote.controller.protocol.enocean.port.Esp3ComPortAdapter;

import java.util.concurrent.TimeUnit;

/**
 * EnOcean Serial Protocol 3.0 processor implementation.
 *
 * @author Rainer Hitz
 */
public class Esp3Processor extends AbstractEspProcessor<Esp3Packet>
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * ESP 3.0 response timeout value [milliseconds].
   */
  public static final long ESP3_RESPONSE_TIMEOUT = 500;


  // Constructors -------------------------------------------------------------------------------

  /**
   * Constructs a new processor instance with a given serial COM port adapter which is
   * used to send and receive data.
   *
   * @param port  ESP 3.0 COM port adapter
   */
  public Esp3Processor(Esp3ComPortAdapter port)
  {
    super(port);
  }


  // Implements AbstractEspProcessor ------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override protected long getResponseTimeout()
  {
    return ESP3_RESPONSE_TIMEOUT;
  }

  /**
   * {@inheritDoc}
   */
  @Override protected Esp3PortReaderBuffer createPortReaderBuffer()
  {
    return new Esp3PortReaderBuffer();
  }

  /**
   * {@inheritDoc}
   */
  @Override protected void dispatchPacket(Esp3Packet packet) throws InterruptedException
  {
    Esp3PacketHeader.PacketType type = packet.getPacketType();

    if(type == Esp3PacketHeader.PacketType.RESPONSE)
    {
      dispatchResponse(packet);
    }

    else if(type == Esp3PacketHeader.PacketType.RADIO)
    {
      // TODO
    }

    else if(type == Esp3PacketHeader.PacketType.EVENT)
    {
      // TODO
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Adds response packet to the response queue.
   *
   * @param packet ESP 3.0 response packet
   */
  private void dispatchResponse(Esp3Packet packet) throws InterruptedException
  {
    responseQueue.offer(
        packet, Esp3Processor.ESP3_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS
    );
  }


  // Inner Classes --------------------------------------------------------------------------------

  /**
   * ESP 3.0 port reader buffer implementation.
   */
  private class Esp3PortReaderBuffer extends AbstractPortReaderBuffer<Esp3Packet>
  {

    // Private Instance Fields ----------------------------------------------------------------------

    /**
     * ESP 3.0 packet header.
     */
    private Esp3PacketHeader header = null;


    // Implements AbstractPortReaderBuffer ----------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override public Esp3Packet getEspPacket()
    {
      if(header == null)
      {
        header = readHeader();
      }

      if(header == null)
      {
        return null;
      }

      int packetSizeWithoutHeader = header.getPacketSize() - Esp3PacketHeader.ESP3_HEADER_SIZE;

      if(buffer.size() < packetSizeWithoutHeader)
      {
        return null;
      }

      byte[] data = readDataGroup(header.getDataLength());

      byte[] optionalData = readDataGroup(header.getOptionalDataLength());

      byte crc8 = buffer.removeFirst();

      Esp3Packet packet = null;

      if(verifyCRC8DataValue(crc8, data, optionalData))
      {
        packet = createPacket(header, data, optionalData);
      }
      else
      {
        // TODO : log
      }

      header = null;

      return packet;
    }


    /**
     * Reads a ESP 3.0 packet header from the buffer and returns it. <p>
     *
     * The implementation tries to find the beginning of the header which is indicated
     * by the {@link Esp3PacketHeader#ESP3_SYNC_BYTE} value. If the CRC-8 header
     * verification fails it's assumed that the sync. byte was not the beginning of
     * a valid header and the data is discarded.
     *
     * @return returns a packet header if available or null otherwise
     */
    private Esp3PacketHeader readHeader()
    {
      Esp3PacketHeader retHeader = null;

      while(retHeader == null && buffer.size() >= Esp3PacketHeader.ESP3_HEADER_SIZE)
      {
        while(buffer.size() > 0 && buffer.getFirst() != Esp3PacketHeader.ESP3_SYNC_BYTE)
        {
          buffer.removeFirst();
        }

        if(buffer.size() >= Esp3PacketHeader.ESP3_HEADER_SIZE)
        {
          byte[] headerBytes = new byte[Esp3PacketHeader.ESP3_HEADER_SIZE];

          for(int i = 0; i < Esp3PacketHeader.ESP3_HEADER_SIZE; i++)
          {
            headerBytes[i] = buffer.removeFirst();
          }

          try
          {
            retHeader = new Esp3PacketHeader(headerBytes);
          }

          catch (Esp3PacketHeader.UnknownPacketTypeException e)
          {
            // TODO : log - new ProtocolException ?
          }

          catch (Esp3PacketHeader.CRC8Exception e)
          {
            // TODO : log
          }
        }
      }

      return retHeader;
    }

    /**
     * Reads a data group of given size from the buffer and returns it.
     *
     * @param  size  data group size
     * @return data group as byte array
     */
    private byte[] readDataGroup(int size)
    {
      byte[] dataGroup = new byte[size];

      for(int i = 0; i < size; i++)
      {
        dataGroup[i] = buffer.removeFirst();
      }

      return dataGroup;
    }

    private boolean verifyCRC8DataValue(byte expectedCRC8, byte[] data, byte[] optionalData)
    {
      int allDataSize = data.length + optionalData.length;

      byte[] allData = new byte[allDataSize];

      // Concatenate data blocks

      System.arraycopy(
          data, 0, allData, 0, data.length
      );

      System.arraycopy(
          optionalData, 0, allData, data.length, optionalData.length
      );

      byte calculatedCRC8 = Esp3Packet.CRC8.calculate(allData);

      return expectedCRC8 == calculatedCRC8;
    }

    /**
     * Creates a ESP 3.0 packet with given header, data and optional data groups. <p>
     *
     * Packet creation is based on the header packet type attribute.
     *
     * @return ESP 3.0 packet
     */
    private Esp3Packet createPacket(Esp3PacketHeader header, byte[] data, byte[] optionalData)
    {
      Esp3Packet packet = null;

      Esp3PacketHeader.PacketType type = header.getPacketType();

      if(type == Esp3PacketHeader.PacketType.RESPONSE)
      {
        packet = createResponsePacket(data, optionalData);
      }

      else if(type == Esp3PacketHeader.PacketType.RADIO)
      {
        // TODO
      }

      else if(type == Esp3PacketHeader.PacketType.EVENT)
      {
        // TODO
      }

      return packet;
    }

    /**
     * Creates a ESP 3.0 response packet with given data and optional data groups.
     *
     * @return ESP 3.0 response packet
     */
    private Esp3Packet createResponsePacket(byte[] data, byte[] optionalData)
    {
      Esp3Packet responsePacket = null;

      responsePacket = new Esp3Packet(Esp3PacketHeader.PacketType.RESPONSE, data, optionalData);

      return responsePacket;
    }
  }
}
