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

package test.tuwien.auto.calimero.datapoint;

import junit.framework.TestCase;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.xml.KNXMLException;
import tuwien.auto.calimero.xml.XMLFactory;
import tuwien.auto.calimero.xml.XMLReader;
import tuwien.auto.calimero.xml.XMLWriter;

/**
 * @author B. Malinowsky
 */
public class CommandDPTest extends TestCase
{
	private static final GroupAddress ga = new GroupAddress(3, 2, 1);
	private static final String dpFile = "./src/test/commandDP.xml";

	/**
	 * @param name
	 */
	public CommandDPTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.CommandDP#toString()}.
	 */
	public final void testToString()
	{
		final Datapoint dp = new CommandDP(ga, "test");
		System.out.println(dp);
		assertTrue(dp.toString().indexOf("test") >= 0);
		assertTrue(dp.toString().indexOf("command DP") >= 0);
		assertTrue(dp.toString().indexOf(ga.toString()) >= 0);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.CommandDP#CommandDP(
	 * tuwien.auto.calimero.GroupAddress, java.lang.String)}.
	 */
	public final void testCommandDPGroupAddressString()
	{
		final Datapoint dp = new CommandDP(ga, "test");
		assertEquals(ga, dp.getMainAddress());
		assertEquals("test", dp.getName());
		assertFalse(dp.isStateBased());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.CommandDP#CommandDP(
	 * tuwien.auto.calimero.GroupAddress, java.lang.String, int, java.lang.String)}.
	 */
	public final void testCommandDPGroupAddressStringIntString()
	{
		final Datapoint dp = new CommandDP(ga, "test", 1, "1.001");
		assertEquals(ga, dp.getMainAddress());
		assertEquals("test", dp.getName());
		assertFalse(dp.isStateBased());
		assertEquals(1, dp.getMainNumber());
		assertEquals("1.001", dp.getDPT());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.CommandDP#CommandDP(
	 * tuwien.auto.calimero.xml.XMLReader)}.
	 * 
	 * @throws KNXMLException
	 */
	public final void testCommandDPXMLReader() throws KNXMLException
	{
		Datapoint dp = new CommandDP(ga, "testSave", 4, "4.001");
		final XMLWriter w = XMLFactory.getInstance().createXMLWriter(dpFile);
		dp.save(w);
		w.close();

		final XMLReader r = XMLFactory.getInstance().createXMLReader(dpFile);
		dp = new CommandDP(r);
		r.close();
		assertEquals(ga, dp.getMainAddress());
		assertEquals("testSave", dp.getName());
		assertFalse(dp.isStateBased());
		assertEquals(4, dp.getMainNumber());
		assertEquals("4.001", dp.getDPT());
	}

	/**
	 * Test method for {@link Datapoint#create(XMLReader)}.
	 * 
	 * @throws KNXMLException
	 */
	public final void testCreate() throws KNXMLException
	{
		final XMLReader r = XMLFactory.getInstance().createXMLReader(dpFile);
		assertTrue(Datapoint.create(r) instanceof CommandDP);
		r.close();
	}
}
