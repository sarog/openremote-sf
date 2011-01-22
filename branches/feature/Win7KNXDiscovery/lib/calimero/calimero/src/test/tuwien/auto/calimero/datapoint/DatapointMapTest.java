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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.datapoint.Datapoint;
import tuwien.auto.calimero.datapoint.DatapointMap;
import tuwien.auto.calimero.datapoint.DatapointModel;
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.xml.KNXMLException;
import tuwien.auto.calimero.xml.XMLFactory;
import tuwien.auto.calimero.xml.XMLReader;
import tuwien.auto.calimero.xml.XMLWriter;

/**
 * @author B. Malinowsky
 */
public class DatapointMapTest extends TestCase
{
	private static final String dpFile = "./src/test/datapointMap.xml";

	private DatapointModel m;
	private final GroupAddress ga1 = new GroupAddress(1, 1, 1);
	private final GroupAddress ga2 = new GroupAddress(2, 2, 2);
	private final GroupAddress ga3 = new GroupAddress(3, 3, 3);
	private final Datapoint dp1 = new StateDP(ga1, "test1");
	private final Datapoint dp2 = new CommandDP(ga2, "test2");
	private final Datapoint dp3 = new StateDP(ga3, "test3");

	/**
	 * @param name name of test case
	 */
	public DatapointMapTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		m = new DatapointMap();

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#DatapointMap(
	 * java.util.Collection)}.
	 */
	public final void testDatapointMapCollection()
	{
		final List l = new ArrayList();
		l.add(dp1);
		l.add(dp2);
		l.add(dp3);
		final DatapointModel dpm = new DatapointMap(l);
		assertTrue(dpm.contains(ga1));
		assertTrue(dpm.contains(ga2));
		assertTrue(dpm.contains(ga3));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#add(
	 * tuwien.auto.calimero.datapoint.Datapoint)}.
	 */
	public final void testAdd()
	{
		assertFalse(m.contains(ga1));
		assertFalse(m.contains(dp1));
		m.add(dp1);
		assertTrue(m.contains(ga1));
		assertTrue(m.contains(dp1));
		assertEquals(dp1, m.get(ga1));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#remove(
	 * tuwien.auto.calimero.datapoint.Datapoint)}.
	 */
	public final void testRemove()
	{
		m.add(dp1);
		assertTrue(m.contains(ga1));
		m.remove(dp1);
		assertNull(m.get(ga1));
		assertFalse(m.contains(dp1));
		assertFalse(m.contains(ga1));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#get(
	 * tuwien.auto.calimero.GroupAddress)}.
	 */
	public final void testGet()
	{
		assertNull(m.get(ga3));
		m.add(dp3);
		assertEquals(dp3, m.get(ga3));
		assertNull(m.get(ga2));
		m.removeAll();
		assertNull(m.get(ga3));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#removeAll()}.
	 */
	public final void testRemoveAll()
	{
		final List l = new ArrayList();
		l.add(dp1);
		l.add(dp2);
		l.add(dp3);
		final DatapointModel dpm = new DatapointMap(l);
		dpm.removeAll();
		assertFalse(dpm.contains(ga1));
		assertFalse(dpm.contains(ga2));
		assertFalse(dpm.contains(ga3));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#getDatapoints()}.
	 */
	public final void testGetDatapoints()
	{
		Collection c = ((DatapointMap) m).getDatapoints();
		assertEquals(0, c.size());
		m.add(dp2);
		c = ((DatapointMap) m).getDatapoints();
		assertEquals(1, c.size());
		assertTrue(c.contains(dp2));

		try {
			c.add(dp3);
			fail("unmodifiable");
		}
		catch (final UnsupportedOperationException e) {}
		m.add(dp1);
		m.add(dp3);
		assertEquals(3, c.size());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#load(
	 * tuwien.auto.calimero.xml.XMLReader)}.
	 * 
	 * @throws KNXMLException
	 */
	public final void testLoad() throws KNXMLException
	{
		final XMLWriter w = XMLFactory.getInstance().createXMLWriter(dpFile);
		m.add(dp1);
		m.add(dp2);
		m.add(dp3);
		m.save(w);
		w.close();
		final XMLReader r = XMLFactory.getInstance().createXMLReader(dpFile);
		try {
			m.load(r);
			fail("datapoints already in map");
		}
		catch (final KNXMLException e) {}
		r.close();

		m.removeAll();
		assertEquals(0, ((DatapointMap) m).getDatapoints().size());
		final XMLReader r2 = XMLFactory.getInstance().createXMLReader(dpFile);
		m.load(r2);
		r2.close();
		assertEquals(3, ((DatapointMap) m).getDatapoints().size());
		assertTrue(m.contains(dp1));
		assertTrue(m.contains(dp2));
		assertTrue(m.contains(dp3));

		// save empty file
		final XMLWriter w2 = XMLFactory.getInstance().createXMLWriter(dpFile);
		new DatapointMap().save(w2);
		w2.close();
		// load empty file
		final XMLReader r3 = XMLFactory.getInstance().createXMLReader(dpFile);
		final DatapointMap dpm = new DatapointMap();
		dpm.load(r3);
		r3.close();
		assertEquals(0, dpm.getDatapoints().size());

		// load empty file into nonempty map
		final XMLReader r4 = XMLFactory.getInstance().createXMLReader(dpFile);
		m.load(r4);
		r4.close();
		assertEquals(3, ((DatapointMap) m).getDatapoints().size());

	}

	/**
	 * Test method for {@link tuwien.auto.calimero.datapoint.DatapointMap#save(
	 * tuwien.auto.calimero.xml.XMLWriter)}.
	 * 
	 * @throws KNXMLException
	 */
	public final void testSave() throws KNXMLException
	{
		final XMLWriter w = XMLFactory.getInstance().createXMLWriter(dpFile);
		m.save(w);
		w.close();
		final XMLWriter w2 = XMLFactory.getInstance().createXMLWriter(dpFile);
		m.add(dp1);
		m.add(dp2);
		m.add(dp3);
		m.save(w2);
		w2.close();
	}
}
