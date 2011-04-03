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

package test.tuwien.auto.calimero.cemi;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.cemi.CEMILDataEx;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 * @author B. Malinowsky
 */
public class CEMILDataExTest extends TestCase
{
	private CEMILDataEx f;
	private final IndividualAddress src = new IndividualAddress(1, 2, 3);
	private final GroupAddress dst = new GroupAddress(2, 4, 4);
	private final byte[] tpdu = new byte[] { 0, (byte) 129 };
	private final byte[] plinfo = new byte[] { 0x10, 0x20 };
	private final byte[] extts = new byte[] { (byte) 0x80, 0x2, 0x3, 0x4 };

	/**
	 * @param name name of test case
	 */
	public CEMILDataExTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		f = new CEMILDataEx(CEMILData.MC_LDATA_REQ, src, dst, tpdu, Priority.LOW);
		f.addAdditionalInfo(CEMILDataEx.ADDINFO_PLMEDIUM, plinfo);
		f.addAdditionalInfo(CEMILDataEx.ADDINFO_TIMESTAMP_EXT, extts);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#getStructLength()}.
	 */
	public final void testGetStructLength()
	{
		assertEquals(11 + 2 + 2 + 2 + 4, f.getStructLength());
		f.removeAdditionalInfo(CEMILDataEx.ADDINFO_PLMEDIUM);
		f.removeAdditionalInfo(CEMILDataEx.ADDINFO_TIMESTAMP_EXT);
		assertEquals(11, f.getStructLength());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#toString()}.
	 */
	public final void testToString()
	{
		System.out.println(f.toString());
		System.out.println(new CEMILDataEx(CEMILData.MC_LDATA_REQ, src, dst, tpdu,
			Priority.LOW).toString());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#setHopCount(int)}.
	 */
	public final void testSetHopCount()
	{
		assertEquals(6, f.getHopCount());
		f.setHopCount(2);
		assertEquals(2, f.getHopCount());
		f.setHopCount(7);
		assertEquals(7, f.getHopCount());
		try {
			f.setHopCount(8);
			fail("out of range");
		}
		catch (final KNXIllegalArgumentException e) {}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#setPriority
	 * (tuwien.auto.calimero.Priority)}.
	 */
	public final void testSetPriority()
	{
		assertEquals(Priority.LOW, f.getPriority());
		f.setPriority(Priority.SYSTEM);
		assertEquals(Priority.SYSTEM, f.getPriority());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#setBroadcast(boolean)}.
	 */
	public final void testSetBroadcast()
	{
		assertTrue(f.isDomainBroadcast());
		f.setBroadcast(false);
		assertFalse(f.isDomainBroadcast());
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.cemi.CEMILDataEx#addAdditionalInfo(int, byte[])}.
	 */
	public final void testAddAdditionalInfo()
	{
		try {
			f.addAdditionalInfo(CEMILDataEx.ADDINFO_PLMEDIUM, new byte[] { 1 });
			fail("wrong length");
		}
		catch (final KNXIllegalArgumentException e) {}
		final byte[] getPL = f.getAdditionalInfo(CEMILDataEx.ADDINFO_PLMEDIUM);
		assertTrue(Arrays.equals(plinfo, getPL));
		f.addAdditionalInfo(CEMILDataEx.ADDINFO_TIMESTAMP_EXT, new byte[] { 4, 4, 4, 4 });
		final byte[] getTS = f.getAdditionalInfo(CEMILDataEx.ADDINFO_TIMESTAMP_EXT);
		assertTrue(Arrays.equals(new byte[] { 4, 4, 4, 4 }, getTS));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#getAdditionalInfo()}.
	 */
	public final void testGetAdditionalInfo()
	{
		final List l = f.getAdditionalInfo();
		assertEquals(2, l.size());
		assertEquals(CEMILDataEx.ADDINFO_PLMEDIUM, ((CEMILDataEx.AddInfo) l.get(0))
			.getType());
		assertEquals(2, ((CEMILDataEx.AddInfo) l.get(0)).getInfo().length);
		assertEquals(CEMILDataEx.ADDINFO_TIMESTAMP_EXT, ((CEMILDataEx.AddInfo) l.get(1))
			.getType());
		assertEquals(4, ((CEMILDataEx.AddInfo) l.get(1)).getInfo().length);
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.cemi.CEMILDataEx#getAdditionalInfo(int)}.
	 */
	public final void testGetAdditionalInfoInt()
	{
		assertNull(f.getAdditionalInfo(CEMILDataEx.ADDINFO_RFMEDIUM));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#isExtendedFrame()}.
	 */
	public final void testIsExtendedFrame()
	{
		assertFalse(f.isExtendedFrame());
		final CEMILDataEx f2 =
			new CEMILDataEx(CEMILData.MC_LDATA_REQ, src, dst, new byte[] { 0, 1, 2, 3, 4,
				5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, Priority.LOW);
		assertTrue(f2.isExtendedFrame());
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.cemi.CEMILDataEx#removeAdditionalInfo(int)}.
	 */
	public final void testRemoveAdditionalInfo()
	{
		f.removeAdditionalInfo(CEMILDataEx.ADDINFO_TIMESTAMP_EXT);
		assertNull(f.getAdditionalInfo(CEMILDataEx.ADDINFO_TIMESTAMP_EXT));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.cemi.CEMILDataEx#clone()}.
	 */
	public final void testClone()
	{
		final CEMILDataEx f2 = (CEMILDataEx) f.clone();
		final List l = f.getAdditionalInfo();
		final List l2 = f2.getAdditionalInfo();
		for (int i = 0; i < l.size(); ++i)
			assertNotSame(l.get(i), l2.get(i));
	}
}
