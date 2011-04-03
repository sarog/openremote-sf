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

package test.tuwien.auto.calimero.dptxlator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import tuwien.auto.calimero.dptxlator.PropertyTypes;
import tuwien.auto.calimero.dptxlator.PropertyTypes.DPTID;
import tuwien.auto.calimero.exception.KNXException;

/**
 * @author B. Malinowsky
 */
public class PropertyTypesTest extends TestCase
{
	/**
	 * @param name name of test case
	 */
	public PropertyTypesTest(String name)
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

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.dptxlator.PropertyTypes#getAllPropertyTypes()}.
	 */
	public final void testGetAllPropertyTypes()
	{
		final Map m = PropertyTypes.getAllPropertyTypes();
		m.put(new Integer(1000), new DPTID(1000, "1000.001"));
		m.remove(new Integer(1000));
		for (final Iterator i = m.keySet().iterator(); i.hasNext();) {
			final Integer type = (Integer) i.next();
			System.out.println(type);
		}
		for (final Iterator i = m.values().iterator(); i.hasNext();) {
			final DPTID type = (DPTID) i.next();
			System.out.println(type.getMainNumber() + ", " + type.getDPT());
		}
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.dptxlator.PropertyTypes#createTranslator(int)}.
	 * 
	 * @throws KNXException
	 */
	public final void testCreateTranslatorInt() throws KNXException
	{
		final DPTXlator t =
			PropertyTypes.createTranslator(PropertyTypes.PDT_BINARY_INFORMATION);
		assertTrue(t instanceof DPTXlatorBoolean);
		try {
			PropertyTypes.createTranslator(PropertyTypes.PDT_SHORT_CHAR_BLOCK);
			fail("we really have such a translator?");
		}
		catch (final KNXException e) {}
		try {
			PropertyTypes.createTranslator(-1);
			fail("invalid PDT");
		}
		catch (final KNXException e) {}
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.dptxlator.PropertyTypes#hasTranslator(int)}.
	 */
	public final void testHasTranslator()
	{
		assertTrue(PropertyTypes.hasTranslator(PropertyTypes.PDT_BINARY_INFORMATION));
		assertFalse(PropertyTypes.hasTranslator(1000));
		assertFalse(PropertyTypes.hasTranslator(PropertyTypes.PDT_CONTROL));
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.dptxlator.PropertyTypes#getValues(int, byte[])}.
	 * 
	 * @throws KNXException
	 */
	public final void testGetValues() throws KNXException
	{
		final String[] values =
			PropertyTypes.getValues(PropertyTypes.PDT_UNSIGNED_INT, new byte[] { 0, 1, 0,
				2, 3, 0 });
		assertEquals(3, values.length);
		assertEquals(1, Integer.parseInt(values[0]));
		assertEquals(2, Integer.parseInt(values[1]));
		assertEquals(0x300, Integer.parseInt(values[2]));
	}

	/**
	 * Test method for
	 * {@link tuwien.auto.calimero.dptxlator.PropertyTypes#createTranslator(int, byte[])}.
	 * 
	 * @throws KNXException
	 */
	public final void testCreateTranslatorIntByteArray() throws KNXException
	{
		final DPTXlator t =
			PropertyTypes.createTranslator(PropertyTypes.PDT_BINARY_INFORMATION,
				new byte[] { 1, 0, 1 });
		assertEquals(3, t.getItems());
		assertTrue(Arrays.equals(new byte[] { 1, 0, 1 }, t.getData()));
	}
}
