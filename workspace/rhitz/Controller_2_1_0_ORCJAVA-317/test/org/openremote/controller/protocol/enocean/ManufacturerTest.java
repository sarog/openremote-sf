/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.enocean;

import org.junit.Test;
import org.junit.Assert;

/**
 * Unit tests for {@link Manufacturer} class.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class ManufacturerTest
{
  @Test public void testKnownManufacturer() throws Exception
  {
    Manufacturer m = Manufacturer.fromID(0x00A);

    Assert.assertEquals(Manufacturer.KIEBACK_PETER, m);
    Assert.assertEquals("Kieback&Peter", m.getName());
    Assert.assertEquals(0x00A, m.getID());
  }

  @Test public void testUnknownManufacturer() throws Exception
  {
    int id = 0x7FE;

    Manufacturer m = Manufacturer.fromID(id);

    Assert.assertEquals(id, m.getID());
    Assert.assertEquals("Unknown", m.getName());
  }

  @Test public void testEquals() throws Exception
  {
    Manufacturer m1 = Manufacturer.ELTAKO;
    Manufacturer m2 = Manufacturer.ELTAKO;

    Manufacturer m3 = Manufacturer.fromID(0x7FE);
    Manufacturer m4 = Manufacturer.fromID(0x7FE);

    Assert.assertTrue(m1.equals(m1));

    Assert.assertTrue(m1.equals(m2));
    Assert.assertTrue(m2.equals(m1));

    Assert.assertFalse(m1.equals(m3));
    Assert.assertFalse(m3.equals(m1));

    Assert.assertTrue(m3.equals(m4));
    Assert.assertTrue(m4.equals(m3));

    Assert.assertFalse(m1.equals(null));
    Assert.assertFalse(m1.equals(new Object()));
  }

  @Test public void testHash() throws Exception
  {
    Manufacturer m1 = Manufacturer.fromID(0x7FE);
    Manufacturer m2 = Manufacturer.fromID(0x7FE);

    Assert.assertTrue(m1.hashCode() == m2.hashCode());
    Assert.assertTrue(m1.equals(m2));
  }
}
