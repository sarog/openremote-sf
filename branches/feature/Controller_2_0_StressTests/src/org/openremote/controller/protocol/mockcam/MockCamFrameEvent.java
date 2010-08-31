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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class MockCamFrameEvent implements Runnable
{

  private boolean running = true;
  private int frameCounter = 0;
  private long frameTimeSnapshot = System.currentTimeMillis();

  private ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private Lock readLock = rwLock.readLock();
  private Lock writeLock = rwLock.writeLock();

  public void run()
  {

    try
    {
      while (running)
      {
        Thread.sleep(200);

        try
        {
          writeLock.lock();

          frameCounter++;
          frameTimeSnapshot = System.currentTimeMillis();
        }
        finally
        {
          writeLock.unlock();
        }
      }
    }
    catch (InterruptedException e)
    {
      // if we get interrupted, we'll just wind down to finish
    }
  }

  public Frame getCurrentFrame()
  {
    try
    {
      readLock.lock();

      return new Frame(frameCounter, frameTimeSnapshot);
    }
    finally
    {
      readLock.unlock();
    }
  }


  public class Frame
  {

    public int counter;
    public long timeSnapshot;

    Frame(int counter, long timeSnapshot)
    {
      this.counter = counter;
      this.timeSnapshot = timeSnapshot;
    }
  }
}

