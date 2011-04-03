/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package tuwien.auto.calimero.buffer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.KNXAddress;
import tuwien.auto.calimero.buffer.Configuration.NetworkFilter;
import tuwien.auto.calimero.buffer.Configuration.RequestFilter;
import tuwien.auto.calimero.buffer.LDataObjectQueue.QueueItem;
import tuwien.auto.calimero.buffer.LDataObjectQueue.QueueListener;
import tuwien.auto.calimero.buffer.cache.Cache;
import tuwien.auto.calimero.buffer.cache.CacheObject;
import tuwien.auto.calimero.cemi.CEMI;
import tuwien.auto.calimero.cemi.CEMIFactory;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.datapoint.DatapointModel;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalStateException;

/**
 * Predefined filter for filtering KNX messages of command based datapoints into the
 * associated network buffer configuration and handling requests for this kind of
 * messages.
 * <p>
 * Command based messages are buffered using a {@link LDataObjectQueue} (an object of this
 * type is also expected when the request method or getNextIndication method is invoked).
 * 
 * @author B. Malinowsky
 */
public class CommandFilter implements NetworkFilter, RequestFilter
{
	private final class QueueListenerImpl implements QueueListener
	{
		QueueListenerImpl()
		{}

		public void queueFilled(LDataObjectQueue queue)
		{
			if (userListener != null)
				try {
					userListener.queueFilled(queue);
				}
				catch (final RuntimeException e) {
					NetworkBuffer.logger.error(
						"L-Data queue listener unexpected behavior", e);
				}
		}
	}

	// stores LDataObjectQueues objects
	private final List indicationKeys = new LinkedList();
	private final QueueListener ql = new QueueListenerImpl();
	private volatile QueueListener userListener;

	/**
	 * Creates a new command filter.
	 * <p>
	 */
	public CommandFilter()
	{}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.buffer.Configuration.NetworkFilter#init
	 * (tuwien.auto.calimero.buffer.Configuration)
	 */
	public void init(Configuration c)
	{}
	
	/**
	 * Sets the specified listener to receive notification of queue events of buffered
	 * queue objects.
	 * <p>
	 * The listener will replace any previously set listener.
	 * 
	 * @param l the listener to set, use <code>null</code> for no listener
	 */
	public void setQueueListener(QueueListener l)
	{
		userListener = l;
	}

	/**
	 * Returns whether new indications are available.
	 * <p>
	 * 
	 * @return <code>true</code> if at least one indication is available,
	 *         <code>false</code> otherwise
	 */
	public boolean hasNewIndication()
	{
		synchronized (indicationKeys) {
			return !indicationKeys.isEmpty();
		}
	}

	/**
	 * Returns the next available indication.
	 * <p>
	 * The items with the cEMI frame indications are returned in FIFO order according the
	 * time they were supplied to the filter and buffered in the first place, i.e. an
	 * earlier accepted frame is returned before an frame accepted at some later time.<br>
	 * Every item is only returned once by this method, after that it is no longer marked
	 * as new and will not cause {@link #hasNewIndication()} to return <code>true</code>
	 * for it.<br>
	 * If no indication is available, throws {@link KNXIllegalStateException}.
	 * <p>
	 * Nevertheless, the queued item might be retrieved directly through the used cache
	 * (which is obtained by {@link Configuration#getCache()}). Whether or not an
	 * returned item is completely consumed from the queue in the cache, i.e. removed from
	 * the cache object in the cache, is specified in the {@link LDataObjectQueue}
	 * containing the item at creation time (which is a task of the network filter).
	 * <p>
	 * Note, if the accessed queue in the cache was modified between the time the
	 * indication was added and this method call in such a way, that the original
	 * indication is not available anymore (for example by removal or emptied queue), that
	 * indication might be skipped or an empty QueueItem is returned.
	 * 
	 * @return queue item containing cEMI frame indication
	 */
	public QueueItem getNextIndication()
	{
		synchronized (indicationKeys) {
			if (indicationKeys.isEmpty())
				throw new KNXIllegalStateException("no indications");
			return ((LDataObjectQueue) indicationKeys.remove(0)).getItem();
		}
	}

	/**
	 * Returns the next available indication for the specified KNX address.
	 * <p>
	 * See {@link #getNextIndication()} for more details. In contrast to that method, this
	 * method does not throw.
	 */
	public CEMILData request(KNXAddress dst, Configuration c)
	{
		if (!(dst instanceof GroupAddress))
			return null;
		final DatapointModel m = c.getDatapointModel();
		final Datapoint dp;
		if (m != null && ((dp = m.get((GroupAddress) dst)) == null || dp.isStateBased()))
			return null;
		synchronized (indicationKeys) {
			for (final Iterator i = indicationKeys.iterator(); i.hasNext();) {
				final CacheObject co = (CacheObject) i.next();
				if (co.getKey().equals(dst)) {
					i.remove();
					return ((LDataObjectQueue) co).getItem().getFrame();
				}
			}
		}
		return null;
	}
	
	/**
	 * Applies command based filter rules on frame.
	 * <p>
	 * Criteria for accept:<br>
	 * <ul>
	 * <li>the KNX message destination address is a group address</li>
	 * <li>there is <b>no</b> datapoint model available in the configuration, or</li>
	 * <li>there is a datapoint model available with a datapoint identified by the
	 * destination address <b>and</b> the datapoint is command based</li>
	 * <li>the message is an application layer group write or group response</li>
	 * </ul>
	 * On acceptance, the frame is stored into the configuration cache using a
	 * {@link LDataObjectQueue} with consuming read behavior and a maximum queue size of
	 * 10 items.<br>
	 * For uniform handling an accepted frame is always buffered with the L-data
	 * indication message code.
	 * 
	 * @param frame {@inheritDoc}
	 * @param c {@inheritDoc}
	 */
	public void accept(CEMI frame, Configuration c)
	{
		final Cache cache = c.getCache();
		if (cache == null || !(frame instanceof CEMILData))
			return;
		final CEMILData f = (CEMILData) frame;
		if (!(f.getDestination() instanceof GroupAddress))
			return;
		final GroupAddress dst = (GroupAddress) f.getDestination();
		// check if we have a datapoint model, whether it contains the address,
		// and datapoint is command based
		final DatapointModel m = c.getDatapointModel();
		final Datapoint dp;
		if (m != null && ((dp = m.get(dst)) == null || dp.isStateBased()))
			return;
		final byte[] d = f.getPayload();
		// filter for A-Group.write (0x80) and A-Group.res (0x40) services
		final int svc = d[0] & 0x03 | d[1] & 0xC0;
		if (svc != 0x40 && svc != 0x80)
			return;
		CEMILData copy;
		try {
			copy = (CEMILData) CEMIFactory.create(CEMILData.MC_LDATA_IND, d, f);
		}
		catch (final KNXFormatException e) {
			NetworkBuffer.logger.error("preparing message for buffer failed", e);
			return;
		}
		CacheObject co = cache.get(dst);
		if (co == null)
			co = new LDataObjectQueue(dst, true, 10, false, ql);
		cache.put(co);
		synchronized (indicationKeys) {
			indicationKeys.add(co);
		}
		// this might invoke queue listener, so do it after put and add
		((LDataObjectQueue) co).setFrame(copy);
	}
}
