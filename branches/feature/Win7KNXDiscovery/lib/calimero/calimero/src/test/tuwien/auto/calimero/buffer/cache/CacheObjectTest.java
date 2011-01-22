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

package test.tuwien.auto.calimero.buffer.cache;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import tuwien.auto.calimero.buffer.cache.CacheObject;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;

/**
 * @author B. Malinowsky
 */
public class CacheObjectTest extends TestCase
{
	private final Object key = new Object();
	private final Object value = new Object();

	/**
	 * @param name name of test case
	 */
	public CacheObjectTest(String name)
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
	 * Test method for {@link tuwien.auto.calimero.buffer.cache.CacheObject#CacheObject
	 * (java.lang.Object, java.lang.Object)}.
	 */
	public void testCacheObject()
	{
		CacheObject o = null;
		try {
			o = new CacheObject(null, null);
		}
		catch (final KNXIllegalArgumentException e) {}
		assertNull(o);

		try {
			o = new CacheObject("CacheObject", null);
		}
		catch (final KNXIllegalArgumentException e) {}
		assertNull(o);

		o = new CacheObject(key, value);
		assertEquals(key, o.getKey());
		assertEquals(value, o.getValue());

		final String skey = "CacheObject";
		final List vvalue = new Vector();
		o = new CacheObject(skey, vvalue);
		assertEquals(skey, o.getKey());
		assertEquals(vvalue, o.getValue());
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.buffer.cache.CacheObject#getTimestamp()}.
	 */
	public void testGetTimestamp()
	{
		final long time = System.currentTimeMillis();
		final CacheObject o = new CacheObject(key, value);
		assertTrue(o.getTimestamp() != 0);
		assertTrue("wrong timestamp", o.getTimestamp() >= time
			&& o.getTimestamp() <= time + 2);
	}

}
