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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import org.junit.Test;
import org.openremote.controller.protocol.knx.ConnectionException;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.ip.tunnel.Channel} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ChannelTest
{

  // Factory.newChannel Tests ---------------------------------------------------------------------

  /**
   * Basic test for allocating a new channel.
   *
   * @throws Exception    if test fails
   */
  @Test public void testChannelFactory() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Channel c = factory.newChannel();

    // We assume here that channel ID count starts at 1...

    Assert.assertTrue(c.getIdentifier() == 1);
  }

  /**
   * Test allocating all possible channel ids...
   *
   * @throws Exception  if test fails
   */
  @Test public void testChannelIDIteration() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    // We assume here that channel ID count start at 1...
    //
    // Note that max channel ID 255 is already reserved as a special error channel...

    for (int index = 1; index <= 254; index++)
    {
      Channel c = factory.newChannel();

      Assert.assertTrue(c.getIdentifier() == index);
    }
  }

  /**
   * Test high number of channel id allocations, release some, re-allocate, and finally
   * run out of available channels.
   *
   * @throws Exception  if test fails
   */
  @Test public void testChannelIDIterationRollover() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Map<Integer, Channel> channels = new HashMap<Integer, Channel>(200);

    // Note that max channel ID 255 is already reserved as a special error channel...

    for (int index = Channel.Factory.MINIMUM_CHANNEL_ID; index <= 254; index++)
    {
      Channel c = factory.newChannel();

      channels.put(c.getIdentifier(), c);

      Assert.assertTrue(c.getIdentifier() == index);
    }

    Channel channel1 = channels.get(1);
    Channel channel10 = channels.get(10);
    Channel channel101 = channels.get(101);
    Channel channel254 = channels.get(254);

    channel1.release();
    channel10.release();
    channel101.release();
    channel254.release();

    // we should have four free channels now...

    channel1 = factory.newChannel();
    channel10 = factory.newChannel();
    channel101 = factory.newChannel();
    channel254 = factory.newChannel();


    Assert.assertTrue(
        channel1.getIdentifier() == 1 || channel1.getIdentifier() == 10 ||
        channel1.getIdentifier() == 101 || channel1.getIdentifier() == 254
    );

    Assert.assertTrue(
        channel10.getIdentifier() == 1 || channel10.getIdentifier() == 10 ||
        channel10.getIdentifier() == 101 || channel10.getIdentifier() == 254
    );

    Assert.assertTrue(
        channel101.getIdentifier() == 1 || channel101.getIdentifier() == 10 ||
        channel101.getIdentifier() == 101 || channel101.getIdentifier() == 254
    );

    Assert.assertTrue(
        channel254.getIdentifier() == 1 || channel254.getIdentifier() == 10 ||
        channel254.getIdentifier() == 101 || channel254.getIdentifier() == 254
    );

    try
    {
      factory.newChannel();

      Assert.fail("Should not get here...");
    }

    catch (ConnectionException e)
    {
      // expected...
    }
  }

  /**
   * Allocate a full range of channels until no more are available.
   */
  @Test public void testChannelIDIterationOverflow()
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    // Note that max channel ID 255 is already reserved as a special error channel...

    try
    {
      for (int index = Channel.Factory.MINIMUM_CHANNEL_ID; index <= 255; index++)
      {
        Channel c = factory.newChannel();

        Assert.assertTrue(c.getIdentifier() == index);
      }

      Assert.fail("Should not get here....");
    }

    catch (ConnectionException e)
    {
      // expected
    }
  }


  // GetChannel Tests -----------------------------------------------------------------------------

  /**
   * Basic getChannel() test...
   *
   * @throws Exception  if test fails
   */
  @Test public void testGetChannel() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Channel c = factory.newChannel();

    Assert.assertTrue(factory.getChannel(c.getIdentifier()) != null);
    Assert.assertTrue(factory.getChannel(c.getIdentifier()).equals(c));
    Assert.assertTrue(factory.getChannel(c.getIdentifier()).getIdentifier() == c.getIdentifier());

    c.release();

    // As per Javadoc, we should get a null reference after release...

    Assert.assertTrue(factory.getChannel(c.getIdentifier()) == null);
  }


  /**
   * Test getChannel after some allocations and releases...
   *
   * @throws Exception  if test fails
   */
  @Test public void testGetChannel2() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Channel c;

    for (int i = 0; i < 200; i++)
    {
      c = factory.newChannel();
      c.release();
    }

    c = factory.newChannel();

    Assert.assertTrue(factory.getChannel(c.getIdentifier()) != null);
    Assert.assertTrue(factory.getChannel(c.getIdentifier()).equals(c));
    Assert.assertTrue(factory.getChannel(c.getIdentifier()).getIdentifier() == c.getIdentifier());

    c.release();

    // As per Javadoc, we should get a null reference after release...

    Assert.assertTrue(factory.getChannel(c.getIdentifier()) == null);

    for (int i = 0; i < 200; i++)
    {
      c = factory.newChannel();
    }

    Assert.assertTrue(factory.getChannel(c.getIdentifier()) != null);
    Assert.assertTrue(factory.getChannel(c.getIdentifier()).equals(c));
    Assert.assertTrue(factory.getChannel(c.getIdentifier()).getIdentifier() == c.getIdentifier());

    c.release();

    // As per Javadoc, we should get a null reference after release...

    Assert.assertTrue(factory.getChannel(c.getIdentifier()) == null);
  }


  // NextSequenceValue Tests ----------------------------------------------------------------------

  /**
   * Basic test for nextSequenceValue() call.
   *
   * @throws Exception    if test fails
   */
  @Test public void testNextSequenceValue() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Channel c = factory.newChannel();

    Assert.assertTrue(c.nextSequenceValue() == Channel.MIN_SEQUENCE_VALUE);
  }

  /**
   * Go through one range of sequence values...
   *
   * @throws Exception  if test fails
   */
  @Test public void testNextSequenceValue2() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Channel c = factory.newChannel();

    for (int index = Channel.MIN_SEQUENCE_VALUE; index <= Channel.MAX_SEQUENCE_VALUE; index++)
    {
      int seqCounter = c.nextSequenceValue();

      Assert.assertTrue("Expected " + index + ", got " + seqCounter, seqCounter == index);
    }
  }

  /**
   * Go over three rounds of sequence values to make sure the count stays correct.
   *
   * @throws Exception  if test fails
   */
  @Test public void testNextSequenceValueRollover() throws Exception
  {
    Channel.Factory factory = Channel.Factory.newInstance();

    Channel c = factory.newChannel();

    for (int index = Channel.MIN_SEQUENCE_VALUE; index <= Channel.MAX_SEQUENCE_VALUE; index++)
    {
      int seqCounter = c.nextSequenceValue();

      Assert.assertTrue("Round 1: Expected " + index + ", got " + seqCounter, seqCounter == index);
    }

    for (int index = Channel.MIN_SEQUENCE_VALUE; index <= Channel.MAX_SEQUENCE_VALUE; index++)
    {
      int seqCounter = c.nextSequenceValue();

      Assert.assertTrue("Round 2: Expected " + index + ", got " + seqCounter, seqCounter == index);
    }

    for (int index = Channel.MIN_SEQUENCE_VALUE; index <= Channel.MAX_SEQUENCE_VALUE; index++)
    {
      int seqCounter = c.nextSequenceValue();

      Assert.assertTrue("Round 3: Expected " + index + ", got " + seqCounter, seqCounter == index);
    }

  }

  // Equals and hashCode Tests --------------------------------------------------------------------


  /**
   * Test equality.
   *
   * @throws Exception if test fails
   */
  @Test public void testEquals() throws Exception
  {
    Channel.Factory factory1 = Channel.Factory.newInstance();
    Channel c1 = factory1.newChannel();

    Channel.Factory factory2 = Channel.Factory.newInstance();
    Channel c2 = factory2.newChannel();

    Assert.assertTrue(c2.getIdentifier() == Channel.Factory.MINIMUM_CHANNEL_ID);
    Assert.assertTrue(c2.getIdentifier() == Channel.Factory.MINIMUM_CHANNEL_ID);

    Channel c3 = factory1.newChannel();
    Channel c4 = factory2.newChannel();

    Assert.assertTrue("Expecting C1 to equals C2", c1.equals(c2));
    Assert.assertTrue("Expecting C2 to equal C1", c2.equals(c1));     // reflective

    Assert.assertFalse("Expecting C1 NOT to equal C3", c1.equals(c3));
    Assert.assertFalse("Expecting C3 NOT to equal C1", c3.equals(c1));

    Assert.assertTrue("Expecting C3 equals C4", c3.equals(c4));
    Assert.assertTrue("Expecting C4 equals C3", c4.equals(c3));

    Assert.assertFalse(c1.equals(null));
    Assert.assertFalse(c1.equals(new Object()));
  }

  /**
   * Test hashes...
   *
   * @throws Exception if test fails
   */
  @Test public void testHash() throws Exception
  {
    Channel.Factory factory1 = Channel.Factory.newInstance();
    Channel c1 = factory1.newChannel();

    Channel.Factory factory2 = Channel.Factory.newInstance();
    Channel c2 = factory2.newChannel();

    Assert.assertTrue(c2.getIdentifier() == Channel.Factory.MINIMUM_CHANNEL_ID);
    Assert.assertTrue(c2.getIdentifier() == Channel.Factory.MINIMUM_CHANNEL_ID);

    Assert.assertTrue(c1.hashCode() == c2.hashCode());
    Assert.assertTrue(c1.equals(c2));
  }

}

