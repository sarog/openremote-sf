/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx.ip.tunnel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.protocol.knx.ConnectionException;

/**
 * Represents a channel in KNXnet/IP connections. Manages channel identifiers and sequence
 * counters. <p>
 *
 * New channel instances are created through the nested {@link Channel.Factory} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Channel
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * The minimum value used for a sequence counter in a channel.
   */
  public final static int MIN_SEQUENCE_VALUE = 1;

  /**
   * The maximum value used for a sequence counter in a channel.
   */
  public final static int MAX_SEQUENCE_VALUE = 254;



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Reference to the factory that was used to create this channel.
   */
  private Factory factory;

  /**
   * Channel identifier. Value range must be limited to an unsigned byte range of [0...255].
   */
  private int id;

  /**
   * A sequence counter value used in the CEMI frames for this channel.
   */
  private int currentSequenceValue = MIN_SEQUENCE_VALUE;



  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new channel with given identifier and a reference to the managing channel
   * factory instance.
   *
   * @param id                  Channel identifier. Must be within range of [0...255]. Note that
   *                            we are reserving values 0 and 255 for special cases. So avoid
   *                            using them.
   *
   * @param managingFactory     the factory instance that manages this channel in its collection
   *                            of channels
   */
  private Channel(int id, Factory managingFactory)
  {
    if (id < 0 || id > 255)
    {
      throw new IllegalArgumentException("Channel identifier must be in range [0...255].");
    }

    this.id = id;
    this.factory = managingFactory;
  }



  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns a channel identifier. The identifier value is always within range of [0...255].
   *
   * @return  channel identifier integer
   */
  public int getIdentifier()
  {
    return id;
  }

  /**
   * Returns a new sequence counter value for a KNXnet/IP connection. The sequence counter
   * returns a value in range of [{@link #MIN_SEQUENCE_VALUE}...{@link #MAX_SEQUENCE_VALUE}].
   * The sequence counter is automatically rotated back to minimum value when it reaches its
   * maximum.
   *
   * @return  next sequence value in this counter
   */
  public int nextSequenceValue()
  {
    if (currentSequenceValue > MAX_SEQUENCE_VALUE)
    {
      currentSequenceValue = MIN_SEQUENCE_VALUE;

      return currentSequenceValue++;
    }

    else
    {
      return currentSequenceValue++;
    }
  }

  /**
   * Release a channel. This releases the channel ID for reuse in the managing channel
   * factory.
   */
  public void release()
  {
    factory.release(this);
  }


  // Object Overrides -----------------------------------------------------------------------------

  /**
   * Compares the channel IDs of two channel instances. Two channels with same ID are
   * considered equal and return true. Note that the sequence counter in the channel
   * may differ between two compared instances while still considered equal.
   *
   * @param channel     channel to compare to
   * @return            true if equal, false otherwise
   */
  @Override public boolean equals(Object channel)
  {
    if (channel == null)
    {
      return false;
    }

    if (!channel.getClass().equals(this.getClass()))
    {
      return false;
    }

    Channel c = (Channel)channel;

    return c.id == this.id;
  }

  @Override public int hashCode()
  {
    return id;
  }


  // Nested Classes -------------------------------------------------------------------------------

  /**
   * This is a factory class to create new channel instances. Use the
   * {@link #newInstance()} to create a new instance of this factory. <p>
   *
   * A channel factory manages a collection of channels that logically belong into the same
   * channel group. A channel group is related to the channel identifier that belongs to a
   * KNX connection. Each KNX connection can have a number of channels (up to 255) that are
   * rotated for that connection. One channel factory is used to manage one such connection. <p>
   *
   * The methods of this factory are thread safe.
   */
  public static class Factory
  {

    // Constants ----------------------------------------------------------------------------------

    /**
     * The low boundary value for channel identifiers allocated by this factory.
     */
    public final static int MINIMUM_CHANNEL_ID = 1;

    // We're using a special reserved channel value 255 in error cases
    // where connections cannot be created due to no more free channel
    // IDs. The regular channel ID allocation will not use this value
    // otherwise.
    //
    // TODO:
    //   Check the spec if there's a specific channel ID that should be used for errors
    //   related to selecting channel ids...
    //
    public final static int RESERVED_ERROR_CHANNEL_IDENTIFIER = 255;



    // Class Members ------------------------------------------------------------------------------

    /**
     * Creates a new instance of this factory class.
     *
     * @return  a new channel factory instance
     */
    public static Factory newInstance()
    {
      return new Factory();
    }


    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Reserved channel and a reserved channel identifier that we use as a value on KNX frame
     * when for some reason the KNXnet IP connection cannot be created due to no available
     * channels.
     */
    private Channel errorChannel;

    /**
     * A sequence this factory uses to allocate an identifier to a newly created
     * channel.
     */
    private int channelIDSequence = 1;

    /**
     * Keep track of channel identifiers in use. The key in the map is the channel's id.
     */
    private Map<Integer, Channel> openChannels = new ConcurrentHashMap<Integer, Channel>();

    /**
     * Mutex to synchronize access to openChannels collection when atomic operations are
     * desired.
     */
    private final Object CHANNELS_MUTEX = new Object();



    // Constructors -------------------------------------------------------------------------------

    private Factory()
    {
      errorChannel = new Channel(RESERVED_ERROR_CHANNEL_IDENTIFIER, this);
    }


    // Public Instance Methods --------------------------------------------------------------------

    /**
     * Returns a specific error channel that is used in special cases where the KNX connection
     * cannot be established due to failure to allocate a channel. In those cases this reserved
     * channel is used instead.
     *
     * @return    a reserved error channel for KNX connections
     */
    public Channel getErrorChannel()
    {
      return errorChannel;
    }

    /**
     * Returns an existing, allocated channel from this factory, or <tt>null</tt> if one does not
     * exist with the given identifier. Channels should first be created with a
     * {@link #newChannel()} call.
     *
     * @param channelID   the channel ID used for a lookup operation to find the channel instance
     *
     * @return  the channel matching the channel identifier, or <tt>null</tt> if not found
     */
    public Channel getChannel(int channelID)
    {
      // synchronize on mutex for atomic contains() & get() call...

      synchronized (CHANNELS_MUTEX)
      {
        if (openChannels.keySet().contains(channelID))
        {
          return openChannels.get(channelID);
        }

        else
        {
          return null;
        }
      }
    }


    /**
     * Returns a new, unallocated channel from this factory instance.
     *
     * @return a newly created unallocated channel instance managed by this factory
     *
     * @throws ConnectionException    if a new channel cannot be created
     */
    public Channel newChannel() throws ConnectionException
    {
      final int MAX_CHANNEL_ID = 255;

      // synchronize on mutex for atomic collection iteration, contains() & put() call...

      synchronized (CHANNELS_MUTEX)
      {
        // Far from ideal way of finding the next channel but will do for now.
        // In the simple cases it's unlikely that there are so many channels
        // that iterating over the possibilities would make a huge difference.
        // Likelihood that next id is free should be fairly good.

        for (int index = MINIMUM_CHANNEL_ID; index <= MAX_CHANNEL_ID; ++index)
        {
          if (channelIDSequence == RESERVED_ERROR_CHANNEL_IDENTIFIER)
          {
            channelIDSequence++;

            continue;
          }

          if (channelIDSequence > MAX_CHANNEL_ID)
          {
            channelIDSequence = MINIMUM_CHANNEL_ID;
          }

          boolean hasChannelID = openChannels.keySet().contains(channelIDSequence);

          if (!hasChannelID)
          {
            Channel channel = new Channel(channelIDSequence, this);

            openChannels.put(channelIDSequence, channel);

            channelIDSequence++;

            return channel;
          }

          else
          {
            channelIDSequence++;
          }
        }
      }

      throw new ConnectionException("Out of available channels.");
    }


    /**
     * Releases a channel identifier for re-use.
     *
     * @param channel     channel to release
     */
    private void release(Channel channel)
    {
      // no need to synchronize as long as the collection is thread safe...

      openChannels.remove(channel.getIdentifier());
    }
  }


}

