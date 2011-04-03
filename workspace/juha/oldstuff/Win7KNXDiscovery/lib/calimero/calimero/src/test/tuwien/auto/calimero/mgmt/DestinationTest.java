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

package test.tuwien.auto.calimero.mgmt;

import junit.framework.TestCase;
import test.tuwien.auto.calimero.Util;
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.log.LogManager;
import tuwien.auto.calimero.mgmt.Destination;
import tuwien.auto.calimero.mgmt.KNXDisconnectException;
import tuwien.auto.calimero.mgmt.TransportLayer;
import tuwien.auto.calimero.mgmt.TransportLayerImpl;
import tuwien.auto.calimero.mgmt.TransportListener;

/**
 * @author B. Malinowsky
 */
public class DestinationTest extends TestCase
{
	private KNXNetworkLink lnk;
	private TransportLayer tl;
	private Destination dst;
	private TLListener tll;

	private final class TLListener implements TransportListener
	{
		volatile int disconnected;

		TLListener()
		{}

		public void broadcast(FrameEvent e)
		{}

		public void dataConnected(FrameEvent e)
		{}

		public void dataIndividual(FrameEvent e)
		{}

		public void disconnected(Destination d)
		{
			++disconnected;
		}

		public void group(FrameEvent e)
		{}

		public void linkClosed(CloseEvent e)
		{}

		public void detached(DetachEvent e)
		{}
	};

	/**
	 * @param name name for test case
	 */
	public DestinationTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		LogManager.getManager().addWriter(null, Util.getLogWriter());
		
		lnk =
			new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, null, Util.getServer(), false,
				TPSettings.TP1);
		tl = new TransportLayerImpl(lnk);
		dst = tl.createDestination(new IndividualAddress("2.2.2"), true, false, false);
		tll = new TLListener();
		tl.addTransportListener(tll);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		if (lnk != null)
			lnk.close();
		
		LogManager.getManager().removeWriter(null, Util.getLogWriter());
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.mgmt.Destination#Destination
	 * (tuwien.auto.calimero.mgmt.Destination.AggregatorProxy, tuwien.auto.calimero.IndividualAddress, boolean)}.
	 * 
	 * @throws KNXFormatException
	 */
	public final void testDestinationAggregatorProxyIndividualAddressBoolean()
		throws KNXFormatException
	{
		final Destination d =
			new Destination(new Destination.AggregatorProxy(tl), new IndividualAddress(
				"2.2.2"), true);
		assertFalse(d.isKeepAlive());
		assertFalse(d.isVerifyMode());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.mgmt.Destination#destroy()}.
	 */
	public final void testDestroy()
	{
		dst.destroy();
		assertEquals(Destination.DESTROYED, dst.getState());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.mgmt.Destination#getAddress()}.
	 * 
	 * @throws KNXFormatException
	 */
	public final void testGetAddress() throws KNXFormatException
	{
		assertEquals(new IndividualAddress("2.2.2"), dst.getAddress());
		dst.destroy();
		assertEquals(new IndividualAddress("2.2.2"), dst.getAddress());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.mgmt.Destination#getState()}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 * @throws InterruptedException
	 */
	public final void testGetState() throws KNXLinkClosedException, KNXTimeoutException,
		InterruptedException
	{
		assertEquals(Destination.DISCONNECTED, dst.getState());
		assertEquals(0, tll.disconnected);
		tl.connect(dst);
		assertEquals(0, tll.disconnected);
		tl.disconnect(dst);
		assertEquals(Destination.DISCONNECTED, dst.getState());
		assertEquals(1, tll.disconnected);
		tl.connect(dst);
		try {
			tl.sendData(dst, Priority.LOW, new byte[] { 0 });
			fail("we should've been disconnected");
		}
		catch (final KNXDisconnectException e) {
			assertEquals(dst, e.getDestination());
		}
		assertEquals(Destination.DISCONNECTED, dst.getState());
		assertEquals(2, tll.disconnected);

		tl.connect(dst);
		assertEquals(Destination.OPEN_IDLE, dst.getState());
		Thread.sleep(6100);
		assertEquals(Destination.DISCONNECTED, dst.getState());
		assertEquals(3, tll.disconnected);

		dst.destroy();
		assertEquals(3, tll.disconnected);
		assertEquals(Destination.DESTROYED, dst.getState());
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.mgmt.Destination#isConnectionOriented()}.
	 * 
	 * @throws KNXFormatException
	 */
	public final void testIsConnectionOriented() throws KNXFormatException
	{
		assertTrue(dst.isConnectionOriented());
		final Destination d =
			new Destination(new Destination.AggregatorProxy(tl), new IndividualAddress(
				"2.2.2"), false);
		assertFalse(d.isConnectionOriented());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.mgmt.Destination#isKeepAlive()}.
	 * 
	 * @throws KNXFormatException
	 */
	public final void testIsKeepAlive() throws KNXFormatException
	{
		assertFalse(dst.isKeepAlive());
		final Destination d =
			new Destination(new Destination.AggregatorProxy(tl), new IndividualAddress(
				"2.2.2"), true, true, false);
		assertTrue(d.isKeepAlive());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.mgmt.Destination#isVerifyMode()}.
	 * 
	 * @throws KNXFormatException
	 */
	public final void testIsVerifyMode() throws KNXFormatException
	{
		assertFalse(dst.isVerifyMode());
		final Destination d =
			new Destination(new Destination.AggregatorProxy(tl), new IndividualAddress(
				"2.2.2"), true, true, true);
		assertTrue(d.isVerifyMode());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.mgmt.Destination#toString()}.
	 */
	public final void testToString()
	{
		System.out.println(dst.toString());
		dst.destroy();
		System.out.println(dst.toString());
	}
}
