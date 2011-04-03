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
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkFT12;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.PLSettings;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.log.LogManager;

/**
 * Test for KNXNetworkLinkFT12.
 * <p>
 * 
 * @author B. Malinowsky
 */
public class KNXNetworkLinkFT12Test extends TestCase
{
	private KNXNetworkLink lnk;
	private NLListenerImpl nll;
	private CEMILData frame;
	private CEMILData frame2;
	private CEMILData frame3;

	private final class NLListenerImpl implements NetworkLinkListener
	{
		volatile CEMILData ind;
		volatile CEMILData con;
		volatile boolean closed;

		public void indication(FrameEvent e)
		{
			assertNotNull(e);
			assertEquals(lnk, e.getSource());
			final CEMILData f = (CEMILData) e.getFrame();
			ind = f;
			assertEquals(CEMILData.MC_LDATA_IND, ind.getMessageCode());
			System.out.println("indication");
			Debug.printLData(ind);
		}

		public void confirmation(FrameEvent e)
		{
			assertNotNull(e);
			assertEquals(lnk, e.getSource());
			final CEMILData f = (CEMILData) e.getFrame();
			con = f;
			assertEquals(CEMILData.MC_LDATA_CON, f.getMessageCode());
			assertTrue(f.isPositiveConfirmation());
			System.out.println("confirmation");
			Debug.printLData(f);
		}

		public void linkClosed(CloseEvent e)
		{
			assertNotNull(e);
			assertEquals(lnk, e.getSource());
			if (closed)
				fail("already closed");
			closed = true;
		}
	}

	/**
	 * @param name
	 */
	public KNXNetworkLinkFT12Test(String name)
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
		try {
			// prevents access problems with a just previously closed port
			Thread.sleep(50);
			lnk = new KNXNetworkLinkFT12(Util.getSerialPort(), TPSettings.TP1);
		}
		catch (final Exception e) {
			LogManager.getManager().removeWriter(null, Util.getLogWriter());
			throw e;
		}
		nll = new NLListenerImpl();
		lnk.addLinkListener(nll);

		frame = new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
			new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 1) },
			Priority.LOW);
		frame2 = new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
			new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 0) },
			Priority.URGENT, true, 3);
		frame3 = new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
			new GroupAddress(0, 0, 3), new byte[] { 0, (byte) (0x80 | 0) },
			Priority.NORMAL);
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
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#KNXNetworkLinkFT12
	 * (java.lang.String, tuwien.auto.calimero.link.medium.KNXMediumSettings)}.
	 * 
	 * @throws KNXException
	 */
	public final void testKNXNetworkLinkFT12StringKNXMediumSettings() throws KNXException
	{
		lnk.close();
		lnk = new KNXNetworkLinkFT12(Util.getSerialPortID(), TPSettings.TP1);
		lnk.close();
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#KNXNetworkLinkFT12 (int,
	 * tuwien.auto.calimero.link.medium.KNXMediumSettings)}.
	 */
	public final void testKNXNetworkLinkFT12IntKNXMediumSettings()
	{
		try {
			lnk = new KNXNetworkLinkFT12(1055, TPSettings.TP1);
			fail("should fail");
		}
		catch (final KNXException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#setKNXMedium
	 * (tuwien.auto.calimero.link.medium.KNXMediumSettings)}.
	 */
	public final void testSetKNXMedium()
	{
		try {
			lnk.setKNXMedium(new PLSettings(true));
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
		lnk.setKNXMedium(new TPSettingsSubClass());
		// replace subtype with its supertype
		lnk.setKNXMedium(new TPSettings(true));

		lnk.setKNXMedium(new TPSettings(new IndividualAddress(200), true));
		assertEquals(200, lnk.getKNXMedium().getDeviceAddress().getRawAddress());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#getKNXMedium()}.
	 */
	public final void testGetKNXMedium()
	{
		assertTrue(lnk.getKNXMedium() instanceof TPSettings);
		assertEquals(0, lnk.getKNXMedium().getDeviceAddress().getRawAddress());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#addLinkListener
	 * (tuwien.auto.calimero.link.event.NetworkLinkListener)}.
	 */
	public final void testAddLinkListener()
	{
		lnk.addLinkListener(nll);
		lnk.addLinkListener(nll);
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#removeLinkListener
	 * (tuwien.auto.calimero.link.event.NetworkLinkListener)}.
	 */
	public final void testRemoveLinkListener()
	{
		lnk.removeLinkListener(nll);
		lnk.removeLinkListener(nll);
		// should do nothing
		lnk.removeLinkListener(nll);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#getHopCount()}.
	 */
	public final void testGetHopCount()
	{
		assertEquals(6, lnk.getHopCount());
		lnk.setHopCount(7);
		assertEquals(7, lnk.getHopCount());
		try {
			lnk.setHopCount(-1);
			fail("negative hop count");
		}
		catch (final KNXIllegalArgumentException e) {}
		try {
			lnk.setHopCount(8);
			fail("hop count too big");
		}
		catch (final KNXIllegalArgumentException e) {}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#sendRequest
	 * (tuwien.auto.calimero.KNXAddress, tuwien.auto.calimero.Priority, byte[])}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 * @throws InterruptedException
	 */
	public final void testSendRequest() throws KNXTimeoutException,
		KNXLinkClosedException, InterruptedException
	{
		doSend(new byte[] { 0, (byte) (0x80 | 1) });
		doSend(new byte[] { 0, (byte) (0x80 | 0) });
		doSend(new byte[] { 0, (byte) (0x80 | 1) });
		doSend(new byte[] { 0, (byte) (0x80 | 0) });

		// send an extended PL frame
		try {
			lnk.sendRequestWait(new GroupAddress(0, 0, 1), Priority.LOW, new byte[] { 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) (0x80 | 0) });
		}
		catch (final KNXIllegalArgumentException e) {}
	}

	private void doSend(byte[] nsdu) throws KNXLinkClosedException, InterruptedException,
		KNXTimeoutException
	{
		nll.con = null;
		lnk.sendRequest(new GroupAddress(0, 0, 1), Priority.LOW, nsdu);
		Thread.sleep(200);
		assertNotNull(nll.con);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#sendRequestWait
	 * (tuwien.auto.calimero.KNXAddress, tuwien.auto.calimero.Priority, byte[])}.
	 * 
	 * @throws KNXTimeoutException
	 * @throws KNXLinkClosedException
	 */
	public final void testSendRequestWait() throws KNXLinkClosedException,
		KNXTimeoutException
	{
		doSendWait(new byte[] { 0, (byte) (0x80 | 1) });
		doSendWait(new byte[] { 0, (byte) (0x80 | 0) });
		doSendWait(new byte[] { 0, (byte) (0x80 | 1) });
		doSendWait(new byte[] { 0, (byte) (0x80 | 0) });
	}

	private void doSendWait(byte[] nsdu) throws KNXLinkClosedException,
		KNXTimeoutException
	{
		nll.con = null;
		lnk.sendRequestWait(new GroupAddress(0, 0, 1), Priority.LOW, nsdu);
		// even in router mode, we still get tunnel ind., so always wait
		try {
			Thread.sleep(200);
		}
		catch (final InterruptedException e) {}
		assertNotNull(nll.con);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#send
	 * (tuwien.auto.calimero.cemi.CEMILData, boolean)}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testSend() throws KNXTimeoutException, KNXLinkClosedException
	{
		nll.con = null;
		lnk.send(frame2, false);
		try {
			Thread.sleep(150);
		}
		catch (final InterruptedException e) {}
		assertNotNull(nll.con);

		lnk.send(frame3, false);
		try {
			Thread.sleep(150);
		}
		catch (final InterruptedException e) {}
		lnk.send(frame3, false);
		try {
			Thread.sleep(150);
		}
		catch (final InterruptedException e) {}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#getName()}.
	 */
	public final void testGetName()
	{
		String n = lnk.getName();
		assertTrue(Util.getSerialPortID(), n.indexOf(Util.getSerialPortID()) > -1);
		assertTrue(n.indexOf("link") > -1);
		lnk.close();
		n = lnk.getName();
		assertNotNull(n);
		assertTrue(n.indexOf("link") > -1);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#isOpen()}.
	 */
	public final void testIsOpen()
	{
		assertTrue(lnk.isOpen());
		lnk.close();
		assertFalse(lnk.isOpen());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.link.KNXNetworkLinkFT12#close()}.
	 * 
	 * @throws InterruptedException
	 * @throws KNXTimeoutException
	 */
	public final void testClose() throws InterruptedException, KNXTimeoutException
	{
		System.out.println(lnk.toString());
		assertTrue(lnk.isOpen());
		lnk.close();
		System.out.println(lnk.toString());
		// time for link event notifier
		Thread.sleep(50);
		assertTrue(nll.closed);
		assertFalse(lnk.isOpen());
		lnk.close();
		try {
			lnk.send(frame, false);
			fail("we are closed");
		}
		catch (final KNXLinkClosedException e) {}
		try {
			lnk.send(frame, false);
			fail("we are closed");
		}
		catch (final KNXLinkClosedException e) {}
	}
}
