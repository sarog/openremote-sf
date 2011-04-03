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

package tuwien.auto.calimero.mgmt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.KNXAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.cemi.CEMI;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.exception.KNXIllegalStateException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.log.LogManager;
import tuwien.auto.calimero.log.LogService;
import tuwien.auto.calimero.mgmt.Destination.AggregatorProxy;

/**
 * Implementation of the transport layer protocol.
 * <p>
 * All sending is done blocking on the attached network link, so an eventual confirmation
 * response is implicit by return of a send method, there are no explicit confirmation
 * notifications.
 * <p>
 * Once this transport layer has been {@link TransportLayer#detach()}ed, it can't be used
 * for any further layer 4 communication, and it can't be attached to a new network link.
 * <br>
 * All methods invoked after a detach of the network link used for communication are
 * allowed to throw {@link KNXIllegalStateException}.
 * 
 * @author B. Malinowsky
 */
public class TransportLayerImpl implements TransportLayer
{
	private final class NLListener implements NetworkLinkListener
	{
		NLListener()
		{}

		public void confirmation(FrameEvent e)
		{}

		public void indication(FrameEvent e)
		{
			final CEMILData f = (CEMILData) e.getFrame();
			final int ctrl = f.getPayload()[0] & 0xfc;
			if (ctrl == 0) {
				final KNXAddress dst = f.getDestination();
				if (dst instanceof GroupAddress)
					// check for broadcast or group
					fireFrameType(f, dst.getRawAddress() == 0 ? 0 : 1);
				else
					// individual
					fireFrameType(f, 2);
			}
			else {
				final IndividualAddress src = f.getSource();
				// are we waiting for ack?
				synchronized (indications) {
					if (active != null
						&& active.getDestination().getAddress().equals(src)) {
						indications.add(e);
						indications.notify();
						return;
					}
				}
				try {
					AggregatorProxy ap = null;
					synchronized (proxies) {
						ap = (AggregatorProxy) proxies.get(f.getSource());
					}
					handleConnected(f, ap);
				}
				catch (final KNXLinkClosedException ignore) {
					// we get notified with link-closed event
				}
				catch (final KNXTimeoutException ignore) {
					// possible timeouts on sending ack
				}
			}
		}

		public void linkClosed(CloseEvent e)
		{
			logger.info("attached link was closed");
			closeDestinations(true);
			fireLinkClosed(e);
		}
	}

	private static final short CONNECT = 0x80;
	private static final short DISCONNECT = 0x81;
	private static final short ACK = 0xC2;
	private static final short NACK = 0xC3;
	private static final short DATA_CONNECTED = 0x40;
	// group, broadcast and individual request service codes are 0

	// acknowledge timeout in seconds
	private static final short ACK_TIMEOUT = 3;
	// maximum repetitions of send in connected mode
	private static final int MAX_REPEAT = 3;

	private static final GroupAddress broadcast = new GroupAddress(0);
	// used as default on incoming conn.oriented messages from unknown remote devices
	private final Destination unknownPartner =
		new Destination(new AggregatorProxy(this), new IndividualAddress(0), true);

	private final LogService logger;

	private volatile boolean detached;
	private final KNXNetworkLink lnk;
	private final NetworkLinkListener lnkListener = new NLListener();
	private final List indications = new LinkedList();
	private final EventListeners listeners;

	// holds the mapping of connection destination address to proxy
	private final Map proxies = new HashMap();
	private AggregatorProxy active;
	private volatile int repeated;
	private final Object lock = new Object();

	/**
	 * Creates a new transport layer attached to the supplied KNX network link.
	 * <p>
	 * 
	 * @param link network link used for communication with a KNX network
	 * @throws KNXLinkClosedException if the network link is closed
	 */
	public TransportLayerImpl(KNXNetworkLink link) throws KNXLinkClosedException
	{
		if (!link.isOpen())
			throw new KNXLinkClosedException();
		lnk = link;
		lnk.addLinkListener(lnkListener);
		logger = LogManager.getManager().getLogService(getName());
		listeners = new EventListeners(logger);
	}

	/**
	 * {@inheritDoc} Only one destination can be created per remote address. If a
	 * destination with the supplied remote address already exists for this transport
	 * layer, a {@link KNXIllegalArgumentException} is thrown.<br>
	 * A transport layer can only handle one connection per destination, because it can't
	 * distinguish incoming messages between more than one connection.
	 */
	public Destination createDestination(IndividualAddress remote,
		boolean connectionOriented)
	{
		return createDestination(remote, connectionOriented, false, false);
	}

	/**
	 * {@inheritDoc} Only one destination can be created per remote address. If a
	 * destination with the supplied remote address already exists for this transport
	 * layer, a {@link KNXIllegalArgumentException} is thrown.<br>
	 * A transport layer can only handle one connection per destination, because it can't
	 * distinguish incoming messages between more than one connection.
	 */
	public Destination createDestination(IndividualAddress remote,
		boolean connectionOriented, boolean keepAlive, boolean verifyMode)
	{
		if (detached)
			throw new KNXIllegalStateException("TL detached");
		synchronized (proxies) {
			if (proxies.containsKey(remote))
				throw new KNXIllegalArgumentException("destination already created: "
					+ remote);
			final AggregatorProxy p = new AggregatorProxy(this);
			final Destination d =
				new Destination(p, remote, connectionOriented, keepAlive, verifyMode);
			proxies.put(remote, p);
			logger.trace("destination " + remote + " ready for use");
			return d;
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#destroyDestination
	 * (tuwien.auto.calimero.mgmt.Destination)
	 */
	public void destroyDestination(Destination d)
	{
		// method invocation is idempotent
		synchronized (proxies) {
			final AggregatorProxy p = (AggregatorProxy) proxies.get(d.getAddress());
			if (p == null)
				return;
			if (p.getDestination() == d) {
				proxies.remove(d.getAddress());
				d.destroy();
			}
			else
				logger.warn("not owner of " + d.getAddress());
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#addTransportListener
	 * (tuwien.auto.calimero.mgmt.TransportListener)
	 */
	public void addTransportListener(TransportListener l)
	{
		listeners.add(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#removeTransportListener
	 * (tuwien.auto.calimero.mgmt.TransportListener)
	 */
	public void removeTransportListener(TransportListener l)
	{
		listeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#connect
	 * (tuwien.auto.calimero.mgmt.Destination)
	 */
	public void connect(Destination d) throws KNXTimeoutException, KNXLinkClosedException
	{
		final AggregatorProxy p = getProxy(d);
		if (!d.isConnectionOriented()) {
			logger.error("destination not connection oriented: " + d.getAddress());
			return;
		}
		if (d.getState() != Destination.DISCONNECTED)
			return;
		p.setState(Destination.CONNECTING);
		final byte[] tpdu = new byte[] { (byte) CONNECT };
		lnk.sendRequestWait(d.getAddress(), Priority.SYSTEM, tpdu);
		p.setState(Destination.OPEN_IDLE);
		logger.trace("connected with " + d.getAddress());
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#disconnect
	 * (tuwien.auto.calimero.mgmt.Destination)
	 */
	public void disconnect(Destination d) throws KNXLinkClosedException
	{
		if (detached)
			throw new KNXIllegalStateException("TL detached");
		if (d.getState() != Destination.DESTROYED
			&& d.getState() != Destination.DISCONNECTED)
			disconnectIndicate(getProxy(d), true);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#sendData
	 * (tuwien.auto.calimero.mgmt.Destination, tuwien.auto.calimero.Priority, byte[])
	 */
	public void sendData(Destination d, Priority p, byte[] tsdu)
		throws KNXDisconnectException, KNXLinkClosedException
	{
		final AggregatorProxy ap = getProxy(d);
		if (d.getState() == Destination.DISCONNECTED) {
			final KNXDisconnectException e = new KNXDisconnectException(
				"no connection opened for " + d.getAddress(), d);
			logger.warn("send failed", e);
			throw e;
		}
		tsdu[0] = (byte) (tsdu[0] & 0x03 | DATA_CONNECTED | ap.getSeqSend() << 2);
		// the entry lock guards between send and return (only one at a time)
		synchronized (lock) {
			// on indications we do wait() for incoming messages
			synchronized (indications) {
				try {
					active = ap;
					for (repeated = 0; repeated < MAX_REPEAT + 1; ++repeated) {
						try {
							logger.trace("sending data connected to " + d.getAddress()
								+ ", attempt " + (repeated + 1));
							// set state and timer
							ap.setState(Destination.OPEN_WAIT);
							lnk.sendRequestWait(d.getAddress(), p, tsdu);
							if (waitForAck())
								return;
						}
						catch (final KNXTimeoutException e) {}
						// cancel repetitions if detached or destroyed
						if (detached || d.getState() == Destination.DESTROYED)
							throw new KNXDisconnectException(
								"send data connected failed", d);
					}
				}
				finally {
					active = null;
					repeated = 0;
				}
			}
		}
		disconnectIndicate(ap, true);
		throw new KNXDisconnectException("send data connected failed", d);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#sendData
	 * (tuwien.auto.calimero.KNXAddress, tuwien.auto.calimero.Priority, byte[])
	 */
	public void sendData(KNXAddress addr, Priority p, byte[] tsdu)
		throws KNXTimeoutException, KNXLinkClosedException
	{
		if (detached)
			throw new KNXIllegalStateException("TL detached");
		tsdu[0] &= 0x03;
		lnk.sendRequestWait(addr, p, tsdu);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#broadcast
	 * (boolean, tuwien.auto.calimero.Priority, byte[])
	 */
	public void broadcast(boolean system, Priority p, byte[] tsdu)
		throws KNXTimeoutException, KNXLinkClosedException
	{
		sendData(system ? null : broadcast, p, tsdu);
	}

	/**
	 * {@inheritDoc} If {@link #detach()} was invoked for this layer, "TL (detached)" is
	 * returned.
	 */
	public String getName()
	{
		return "TL " + (detached ? "(detached)" : lnk.getName());
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.TransportLayer#detach()
	 */
	public synchronized KNXNetworkLink detach()
	{
		if (detached)
			return null;
		closeDestinations(false);
		lnk.removeLinkListener(lnkListener);
		detached = true;
		fireDetached();
		logger.info("detached from " + lnk.getName());
		LogManager.getManager().removeLogService(logger.getName());
		return lnk;
	}

	private AggregatorProxy getProxy(Destination d)
	{
		if (detached)
			throw new KNXIllegalStateException("TL detached");
		synchronized (proxies) {
			final AggregatorProxy p = (AggregatorProxy) proxies.get(d.getAddress());
			// check identity, too, to prevent destination with only same address
			if (p == null || p.getDestination() != d)
				throw new KNXIllegalArgumentException("not the owner of " + d.toString());
			return p;
		}
	}

	private void handleConnected(CEMILData frame, AggregatorProxy p)
		throws KNXLinkClosedException, KNXTimeoutException
	{
		final IndividualAddress sender = frame.getSource();
		final byte[] tpdu = frame.getPayload();
		final int ctrl = tpdu[0] & 0xFF;
		final int seq = (tpdu[0] & 0x3C) >>> 2;
	
		// on proxy null (no destination found for sender) use 'no partner' placeholder
		final Destination d = p != null ? p.getDestination() : unknownPartner;
	
		if (ctrl == CONNECT) {
			if (d.getState() == Destination.DISCONNECTED)
				sendDisconnect(sender);
		}
		else if (ctrl == DISCONNECT) {
			if (d.getState() != Destination.DISCONNECTED && sender.equals(d.getAddress()))
				disconnectIndicate(p, false);
		}
		else if ((ctrl & 0xC0) == DATA_CONNECTED) {
			if (d.getState() == Destination.DISCONNECTED
				|| !sender.equals(d.getAddress()))
				sendDisconnect(sender);
			else {
				p.restartTimeout();
				if (seq == p.getSeqReceive()) {
					lnk.sendRequest(sender, Priority.SYSTEM,
						new byte[] { (byte) (ACK | p.getSeqReceive() << 2) });
					p.incSeqReceive();
					fireFrameType(frame, 3);
				}
				else if (seq == (p.getSeqReceive() - 1 & 0xF))
					lnk.sendRequest(sender, Priority.SYSTEM,
						new byte[] { (byte) (ACK | seq << 2) });
				else
					lnk.sendRequest(sender, Priority.SYSTEM,
						new byte[] { (byte) (NACK | seq << 2) });
			}
		}
		else if ((ctrl & 0xC3) == ACK) {
			if (d.getState() == Destination.DISCONNECTED
				|| !sender.equals(d.getAddress()))
				sendDisconnect(sender);
			else if (d.getState() == Destination.OPEN_WAIT && seq == p.getSeqSend()) {
				p.incSeqSend();
				p.setState(Destination.OPEN_IDLE);
				logger.trace("positive ack by " + d.getAddress());
			}
			else
				disconnectIndicate(p, true);
		}
		else if ((ctrl & 0xC3) == NACK)
			if (d.getState() == Destination.DISCONNECTED
				|| !sender.equals(d.getAddress()))
				sendDisconnect(sender);
			else if (d.getState() == Destination.OPEN_WAIT && seq == p.getSeqSend()
				&& repeated < MAX_REPEAT) {
				; // do nothing, we will send message again
			}
			else
				disconnectIndicate(p, true);
	}

	private boolean waitForAck() throws KNXTimeoutException, KNXDisconnectException,
		KNXLinkClosedException
	{
		long remaining = ACK_TIMEOUT * 1000;
		final long end = System.currentTimeMillis() + remaining;
		final Destination d = active.getDestination();
		while (remaining > 0) {
			try {
				while (indications.size() > 0)
					handleConnected((CEMILData) ((FrameEvent) indications.remove(0))
						.getFrame(), active);
				if (d.getState() == Destination.DISCONNECTED)
					throw new KNXDisconnectException(d.getAddress()
						+ " disconnected while awaiting ACK", d);
				if (d.getState() == Destination.OPEN_IDLE)
					return true;
				indications.wait(remaining);
				if (d.getState() == Destination.DISCONNECTED)
					throw new KNXDisconnectException(d.getAddress()
						+ " disconnected while awaiting ACK", d);
			}
			catch (final InterruptedException e) {}
			remaining = end - System.currentTimeMillis();
		}
		return false;
	}

	private void closeDestinations(boolean skipSendDisconnect)
	{
		// we can't use proxies default iterator due to concurrent modifications in
		// destroyDestination(), called by d.destroy()
		AggregatorProxy[] allProxies = new AggregatorProxy[proxies.size()];
		synchronized (proxies) {
			allProxies = (AggregatorProxy[]) proxies.values().toArray(allProxies);
		}
		for (int i = 0; i < allProxies.length; i++) {
			final AggregatorProxy p = allProxies[i];
			final Destination d = p.getDestination();
			if (skipSendDisconnect && d.getState() != Destination.DISCONNECTED) {
				p.setState(Destination.DISCONNECTED);
				fireDisconnected(d);
			}
			d.destroy();
		}
	}

	private void disconnectIndicate(AggregatorProxy p, boolean sendDisconnectReq)
		throws KNXLinkClosedException
	{
		p.setState(Destination.DISCONNECTED);
		try {
			if (sendDisconnectReq)
				sendDisconnect(p.getDestination().getAddress());
		}
		finally {
			fireDisconnected(p.getDestination());
			logger.trace("disconnected from " + p.getDestination().getAddress());
		}
	}

	private void sendDisconnect(IndividualAddress addr) throws KNXLinkClosedException
	{
		final byte[] tpdu = new byte[] { (byte) DISCONNECT };
		try {
			lnk.sendRequest(addr, Priority.SYSTEM, tpdu);
		}
		catch (final KNXTimeoutException ignore) {
			// do a warning, but otherwise can be ignored
			logger.warn("disconnected not gracefully (timeout)", ignore);
		}
	}

	private void fireDisconnected(Destination d)
	{
		for (final Iterator i = listeners.iterator(); i.hasNext();) {
			final TransportListener l = (TransportListener) i.next();
			try {
				l.disconnected(d);
			}
			catch (final RuntimeException rte) {
				removeTransportListener(l);
				logger.error("removed event listener", rte);
			}
		}
	}

	// type 0 = broadcast, 1 = group, 2 = individual, 3 = connected
	private void fireFrameType(CEMI frame, int type)
	{
		final FrameEvent e = new FrameEvent(this, frame);
		for (final Iterator i = listeners.iterator(); i.hasNext();) {
			final TransportListener l = (TransportListener) i.next();
			try {
				if (type == 0)
					l.broadcast(e);
				else if (type == 1)
					l.group(e);
				else if (type == 2)
					l.dataIndividual(e);
				else if (type == 3)
					l.dataConnected(e);
			}
			catch (final RuntimeException rte) {
				removeTransportListener(l);
				logger.error("removed event listener", rte);
			}
		}
	}

	private void fireDetached()
	{
		final DetachEvent e = new DetachEvent(this);
		for (final Iterator i = listeners.iterator(); i.hasNext();) {
			final TransportListener l = (TransportListener) i.next();
			try {
				l.detached(e);
			}
			catch (final RuntimeException rte) {
				removeTransportListener(l);
				logger.error("removed event listener", rte);
			}
		}
	}

	private void fireLinkClosed(CloseEvent e)
	{
		for (final Iterator i = listeners.iterator(); i.hasNext();) {
			final TransportListener l = (TransportListener) i.next();
			try {
				l.linkClosed(e);
			}
			catch (final RuntimeException rte) {
				removeTransportListener(l);
				logger.error("removed event listener", rte);
			}
		}
	}
}
