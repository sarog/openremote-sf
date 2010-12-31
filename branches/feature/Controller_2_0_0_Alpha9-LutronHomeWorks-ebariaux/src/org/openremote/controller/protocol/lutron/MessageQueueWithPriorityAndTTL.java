package org.openremote.controller.protocol.lutron;

import java.util.Date;
import java.util.LinkedList;

/**
 * A queue that implements several specific requirements:
 * - inserted messages have a TTL, they're dropped from the queue (on read operations) if they exceed there TTL
 * - there is the possibility of priority messages, they are inserted at the head of the queue (after the existing priority messages), they don't have TTL
 * - inserted messages are coalesced (if the message class implements the Coalescable interface), older message is kept
 * - it is thread safe
 * 
 * @author Eric Bariaux
 */
public class MessageQueueWithPriorityAndTTL<E> {

	private LinkedList<QueueEntry> queue;
	private int firstNonPriorityMessage;
	
	private int ttl;
	
	public MessageQueueWithPriorityAndTTL() {
		super();
		queue = new LinkedList<QueueEntry>();
		firstNonPriorityMessage = 0;
	}

	public void priorityAdd(E e) {
		synchronized(queue) {
			queue.add(firstNonPriorityMessage, new QueueEntry(e));
			firstNonPriorityMessage++;
			queue.notify();
		}
	}
	
	public void add(E e) {
		synchronized(queue) {
			queue.add(new QueueEntry(e));
			queue.notify();
		}
	}
	
	public E poll() {
		QueueEntry e = null;
		synchronized(queue) {
			if (firstNonPriorityMessage > 0) {
				firstNonPriorityMessage--;
			}
			e = queue.poll();
		}
		return (e != null)?e.getEntry():null;
	}

	/**
	 * Same as poll but waits for an entry if nothing is present in the queue yet.
	 * @return
	 */
	public E blockingPoll() {
		synchronized(queue) {
			if (queue.isEmpty()) {
				// Queue is empty, wait
				try {
					queue.wait();
				} catch (InterruptedException e) {
					// Just let it go through, we'll return null if queue is empty
				}
			}
		}
		return poll();		
	}
	
	/**
	 * Encapsulates queue elements, adding timestamp to implement TTL.
	 * 
	 * @author Eric Bariaux
	 */
	private class QueueEntry {
		
		private long timestamp;
		private E entry;
		
		public QueueEntry(E entry) {
			super();
			this.timestamp = new Date().getTime();
			this.entry = entry;
		}
		
		public long getTimestamp() {
			return timestamp;
		}

		public E getEntry() {
			return entry;
		}
		
		public String toString() {
			return new Date(timestamp) + ": " + entry;
		}
	}
	
}
