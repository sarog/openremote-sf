/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.enocean.datatype;

import org.junit.Test;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for {@link CategoricalScale} class.
 *
 * @author Rainer Hitz
 */
public class CategoricalScaleTest
{

  // Tests ----------------------------------------------------------------------------------------

  @Test public void testScaling() throws Exception
  {
    ScaleCategory cat1 = new ScaleCategory("Category 1", 0, 49, "CAT1", 1);
    ScaleCategory cat2 = new ScaleCategory("Category 2", 50, 99, "CAT2", 2);
    ScaleCategory cat3 = new ScaleCategory("Category 3", 100, 150, "CAT3", 3);

    Set<ScaleCategory> categories = new HashSet<ScaleCategory>();
    categories.add(cat1);
    categories.add(cat2);
    categories.add(cat3);

    CategoricalScale scale = new CategoricalScale(categories);


    ScaleCategory cat = scale.scaleRawValue(49);

    Assert.assertEquals(cat1, cat);


    cat = scale.scaleRawValue(50);

    Assert.assertEquals(cat2, cat);


    cat = scale.scaleRawValue(99);

    Assert.assertEquals(cat2, cat);


    cat = scale.scaleRawValue(151);

    Assert.assertNull(cat);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCategories() throws Exception
  {
    CategoricalScale scale = new CategoricalScale(null);
  }
}
