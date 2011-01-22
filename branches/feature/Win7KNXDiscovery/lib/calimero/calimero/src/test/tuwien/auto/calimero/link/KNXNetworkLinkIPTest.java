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

package test.tuwien.auto.calimero.link;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;
import test.tuwien.auto.calimero.Util;
import test.tuwien.auto.calimero.knxnetip.Debug;
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.KNXnetIPRouter;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.PLSettings;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.log.LogManager;

/**
 * @author B. Malinowsky
 */
public class KNXNetworkLinkIPTest extends TestCase
{
	private KNXNetworkLink tnl;
	private KNXNetworkLink rtr;
	private NLListenerImpl ltnl, lrtr;
	private CEMILData frame;
	private CEMILData frame2;
	private CEMILData frameInd;

	private final class NLListenerImpl implements NetworkLinkListener
	{
		volatile CEMILData ind;
		volatile CEMILData con;
		volatile boolean closed;

		public void indication(FrameEvent e)
		{
			assertNotNull(e);
			assertEquals(this == ltnl ? tnl : rtr, e.getSource());
			final CEMILData f = (CEMILData) e.getFrame();
			ind = f;
			assertEquals(CEMILData.MC_LDATA_IND, ind.getMessageCode());
			System.out.println((this == ltnl ? "tunnel " : "router ") + "indication");
			Debug.printLData(ind);
		}

		public void confirmation(FrameEvent e)
		{
			assertNotNull(e);
			assertEquals(this == ltnl ? tnl : rtr, e.getSource());
			final CEMILData f = (CEMILData) e.getFrame();
			con = f;
			assertEquals(CEMILData.MC_LDATA_CON, f.getMessageCode());
			assertTrue(f.isPositiveConfirmation());
			System.out.println((this == ltnl ? "tunnel " : "router ") + "confirmation");
			Debug.printLData(f);
		}

		public void linkClosed(CloseEvent e)
		{
			assertNotNull(e);
			assertEquals(this == ltnl ? tnl : rtr, e.getSource());
			if (closed)
				fail("already closed");
			closed = true;
		}
	}

	/**
	 * @param name name of test case
	 */
	public KNXNetworkLinkIPTest(String name)
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

		tnl =
			new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, Util.getLocalHost(), Util
				.getServer(), false, TPSettings.TP1);
		rtr =
			new KNXNetworkLinkIP(KNXNetworkLinkIP.ROUTER, Util.getLocalHost(),
				new InetSocketAddress(InetAddress
					.getByName(KNXnetIPRouter.DEFAULT_MULTICAST), 0), false,
				TPSettings.TP1);
		ltnl = new NLListenerImpl();
		lrtr = new NLListenerImpl();
		tnl.addLinkListener(ltnl);
		rtr.addLinkListener(lrtr);

		frame =
			new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
				new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 1) },
				Priority.LOW);
		frame2 =
			new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
				new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 0) },
				Priority.URGENT);
		frameInd =
			new CEMILData(CEMILData.MC_LDATA_IND, new IndividualAddress(0),
				new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 0) },
				Priority.NORMAL);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		if (tnl != null)
			tnl.close();
		if (rtr != null)
			rtr.close();

		LogManager.getManager().removeWriter(null, Util.getLogWriter());
		super.tearDown();
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#KNXNetworkLinkIP
	 * (int, java.net.InetSocketAddress, java.net.InetSocketAddress, boolean,
	 * tuwien.auto.calimero.link.medium.KNXMediumSettings)}.
	 * 
	 * @throws KNXException
	 */
	public final void testKNXNetworkLinkIPConstructor() throws KNXException
	{
		tnl.close();
		try {
			new KNXNetworkLinkIP(100, new InetSocketAddress(0), Util.getServer(), false,
				TPSettings.TP1);
			fail("illegal arg");
		}
		catch (final KNXIllegalArgumentException e) {}
		try {
			new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, new InetSocketAddress(0), Util
				.getServer(), false, TPSettings.TP1);
			fail("wildcard no supported");
		}
		catch (final KNXIllegalArgumentException e) {}
		// use default local host
		KNXNetworkLink lnk =
			new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, null, Util.getServer(), false,
				TPSettings.TP1);
		lnk.close();
		// try easy to use ctor
		lnk = new KNXNetworkLinkIP(Util.getServer().getHostName(), TPSettings.TP1);
		lnk.close();
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#KNXNetworkLinkIP(java.net.NetworkInterface, java.net.InetAddress, tuwien.auto.calimero.link.medium.KNXMediumSettings)}.
	 * 
	 * @throws KNXException
	 * @throws UnknownHostException
	 */
	public final void testKNXNetworkLinkIPNetworkInterfaceInetAddressKNXMediumSettings()
		throws UnknownHostException, KNXException
	{
		final KNXNetworkLink lnk =
			new KNXNetworkLinkIP(null, InetAddress.getByName("224.0.23.14"),
				TPSettings.TP1);
		lnk.close();
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#addLinkListener
	 * (tuwien.auto.calimero.link.event.NetworkLinkListener)}.
	 */
	public final void testAddLinkListener()
	{
		tnl.addLinkListener(ltnl);
		tnl.addLinkListener(ltnl);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#setKNXMedium
	 * (tuwien.auto.calimero.link.medium.KNXMediumSettings)}.
	 */
	public final void testSetKNXMedium()
	{
		try {
			tnl.setKNXMedium(new PLSettings(true));
			fail("different medium");
		}
		catch (final KNXIllegalArgumentException e) {}
		final class TPSettingsSubClass extends TPSettings
		{
			TPSettingsSubClass()
			{
				super(false);
			}
		}
		// replace basetype with subtype
		tnl.setKNXMedium(new TPSettingsSubClass());
		// replace subtype with its supertype
		tnl.setKNXMedium(new TPSettings(true));

		tnl.setKNXMedium(new TPSettings(new IndividualAddress(200), true));
		assertEquals(200, tnl.getKNXMedium().getDeviceAddress().getRawAddress());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#getKNXMedium()}.
	 */
	public final void testGetKNXMedium()
	{
		assertTrue(tnl.getKNXMedium() instanceof TPSettings);
		assertEquals(0, tnl.getKNXMedium().getDeviceAddress().getRawAddress());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#close()}.
	 * 
	 * @throws InterruptedException
	 * @throws KNXTimeoutException
	 */
	public final void testClose() throws InterruptedException, KNXTimeoutException
	{
		assertTrue(tnl.isOpen());
		tnl.close();
		// time for link event notifier
		Thread.sleep(50);
		assertTrue(ltnl.closed);
		assertFalse(tnl.isOpen());
		tnl.close();
		try {
			tnl.send(frame, false);
			fail("we are closed");
		}
		catch (final KNXLinkClosedException e) {}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#getHopCount()}.
	 */
	public final void testGetHopCount()
	{
		assertEquals(6, rtr.getHopCount());
		rtr.setHopCount(7);
		assertEquals(7, rtr.getHopCount());
		try {
			rtr.setHopCount(-1);
			fail("negative hop count");
		}
		catch (final KNXIllegalArgumentException e) {}
		try {
			rtr.setHopCount(8);
			fail("hop count too big");
		}
		catch (final KNXIllegalArgumentException e) {}
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#removeLinkListener
	 * (tuwien.auto.calimero.link.event.NetworkLinkListener)}.
	 */
	public final void testRemoveLinkListener()
	{
		tnl.removeLinkListener(ltnl);
		tnl.removeLinkListener(ltnl);
		// should do nothing
		tnl.removeLinkListener(lrtr);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#sendRequest
	 * (tuwien.auto.calimero.KNXAddress, tuwien.auto.calimero.Priority, byte[])}.
	 * 
	 * @throws InterruptedException
	 * @throws KNXException
	 * @throws UnknownHostException
	 */
	public final void testSendRequestKNXAddressPriorityByteArray()
		throws InterruptedException, UnknownHostException, KNXException
	{
		doSend(true, new byte[] { 0, (byte) (0x80 | 1) });
		doSend(true, new byte[] { 0, (byte) (0x80 | 0) });
		doSend(false, new byte[] { 0, (byte) (0x80 | 1) });
		doSend(false, new byte[] { 0, (byte) (0x80 | 0) });

		// send an extended PL frame
		final KNXNetworkLink plrtr =
			new KNXNetworkLinkIP(KNXNetworkLinkIP.ROUTER, Util.getLocalHost(),
				new InetSocketAddress(InetAddress
					.getByName(KNXnetIPRouter.DEFAULT_MULTICAST), 0), false,
				new PLSettings(true));
		plrtr.sendRequest(new GroupAddress(0, 0, 1), Priority.LOW, new byte[] { 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) (0x80 | 0) });
	}

	private void doSend(boolean tunnel, byte[] nsdu) throws KNXLinkClosedException,
		InterruptedException, KNXTimeoutException
	{
		final KNXNetworkLink lnk = tunnel ? tnl : rtr;
		final NLListenerImpl l = tunnel ? ltnl : lrtr;
		l.con = null;
		lnk.sendRequest(new GroupAddress(0, 0, 1), Priority.LOW, nsdu);
		Thread.sleep(100);
		if (tunnel) {
			assertNotNull(l.con);
		}
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#send(CEMILData, boolean)}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testSendRequestCEMILData() throws KNXLinkClosedException,
		KNXTimeoutException
	{
		ltnl.con = null;
		tnl.send(frame2, false);
		try {
			Thread.sleep(100);
		}
		catch (final InterruptedException e) {}
		assertNotNull(ltnl.con);

		rtr.send(frameInd, false);
		try {
			Thread.sleep(100);
		}
		catch (final InterruptedException e) {}
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#sendRequestWait(tuwien.auto.calimero.KNXAddress, tuwien.auto.calimero.Priority, byte[])}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testSendRequestWaitKNXAddressPriorityByteArray()
		throws KNXTimeoutException, KNXLinkClosedException
	{
		doSendWait(true, new byte[] { 0, (byte) (0x80 | 1) });
		doSendWait(true, new byte[] { 0, (byte) (0x80 | 0) });
		doSendWait(false, new byte[] { 0, (byte) (0x80 | 1) });
		doSendWait(false, new byte[] { 0, (byte) (0x80 | 0) });
	}

	private void doSendWait(boolean tunnel, byte[] nsdu) throws KNXLinkClosedException,
		KNXTimeoutException
	{
		final KNXNetworkLink lnk = tunnel ? tnl : rtr;
		final NLListenerImpl l = tunnel ? ltnl : lrtr;
		l.con = null;
		lnk.sendRequestWait(new GroupAddress(0, 0, 1), Priority.LOW, nsdu);
		// even in router mode, we still get tunnel ind., so always wait
		try {
			Thread.sleep(100);
		}
		catch (final InterruptedException e) {}
		if (tunnel) {
			assertNotNull(l.con);
		}
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#send(tuwien.auto.calimero.cemi.CEMILData, boolean)}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testSendRequestWaitCEMILData() throws KNXTimeoutException,
		KNXLinkClosedException
	{
		ltnl.con = null;
		try {
			tnl.send(frameInd, true);
			fail("should get timeout cos of wrong frame");
		}
		catch (final KNXTimeoutException e) {}
		assertNull(ltnl.con);
		tnl.send(frame, true);
		assertNotNull(ltnl.con);
		rtr.send(frameInd, true);
		try {
			Thread.sleep(100);
		}
		catch (final InterruptedException e) {}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkIP#getName()}.
	 * 
	 * @throws KNXException
	 */
	public final void testGetName() throws KNXException
	{
		String n = tnl.getName();
		assertTrue(n.indexOf(Util.getServer().getAddress().getHostAddress()) > -1);
		assertTrue(n.indexOf("link") > -1);
		tnl.close();
		n = tnl.getName();
		assertNotNull(n);
		assertTrue(n.indexOf("link") > -1);

		n = rtr.getName();
		assertTrue(n.indexOf(KNXnetIPRouter.DEFAULT_MULTICAST) > -1);
		assertTrue(n.indexOf("link") > -1);
		rtr.close();
		n = rtr.getName();
		assertNotNull(n);
		assertTrue(n.indexOf("link") > -1);
	}
}
