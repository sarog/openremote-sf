/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.mockcam;

import java.util.Set;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class MockCamFrameEvent implements Runnable
{

  public void run()
  {
    int iterations = 1000 /* 1000 times per second */ * 5; /* for 5 seconds */
    long[] measuredDelays = new long[iterations];

    try
    {
      for (int i = 0; i < iterations; ++i)
      {

        long time = System.currentTimeMillis();

        Thread.sleep(1);

        long end = System.currentTimeMillis();

        measuredDelays[i] = end - time;
      }
    }
    catch (InterruptedException e)
    {
      // if we get interrupted, we'll just wind down to finish
    }

    // calculate the measured delay averages

    long sum = 0;
    int linenum = 0;

    for (long value : measuredDelays)
    {
      sum += value;

      System.out.println("" + linenum++ + "\tValue:\t" + value + "\tms");

    }

    System.out.println("========== Recorded avg measured delay of " + (double)sum/iterations + " ms");
  }
}

