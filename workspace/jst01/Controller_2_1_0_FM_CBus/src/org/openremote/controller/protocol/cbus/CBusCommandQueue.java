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
package org.openremote.controller.protocol.cbus;

import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 *  CBus command queue
 *  
 *  Based on Eric Bariaux's MessageQueueWithPriorityandTTL for the Lutron protocol
 *  
 *  @author Jamie Turner
 */
public class CBusCommandQueue<E> 
{

  /**
   * The actual queue
   */
  private LinkedList<QueueEntry> queue;
  
 
  /**
   * Creates an empty queue with no overall queue TTL.
   */
  public CBusCommandQueue() 
  {
    super();
    queue = new LinkedList<QueueEntry>();
  }

  
  /**
   * Adds an entry to the queue, at the end of the queue.
   * 
   * @param e entry to add to the queue
   */
  public void add(E e) {
     synchronized (queue) {
        queue.add(new QueueEntry(e));
        queue.notify();
      }
  }

  /**
   * Returns and removes the entry at the head of the queue.
   * This method never waits and returns null if the queue is empty.
   * 
   * @return Entry at the head of the queue or null if queue is empty
   */
  public E poll() 
  {
    QueueEntry e = null;
    
    synchronized (queue) 
    {
      while (e == null && !queue.isEmpty()) 
      {
        e = queue.poll();
      }
    }
    
    return (e != null) ? e.getEntry() : null;
  }

  /**
   * Returns and removes the entry at the head of the queue.
   * If the queue is currently empty, this methods waits until there is an entry added before returning.
   * 
   * @return Entry at the head of the queue
   */
  public E blockingPoll() 
  {
    synchronized (queue) 
    {
      while (queue.isEmpty()) {
        // Queue is empty, wait
        try 
        {
          queue.wait();
        } 
        catch (InterruptedException e) 
        {
          // Just let it go through, we'll return null if queue is empty
        }
      }
    }
    
    return poll();
  }

  /**
   * Encapsulates queue entry.
   * 
   * Based on Eric Bariaux's MessageQueueWithPriorityandTTL in the Lutron protocol
   * 
   * @author Jamie Turner
   */
  private class QueueEntry 
  {

    private E entry;

    public QueueEntry(E entry) 
    {
      super();
      this.entry = entry;
    }

    
    public E getEntry() 
    {
      return entry;
    }

    public String toString() 
    {
      return entry.toString();
    }
  }

  
}

