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

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ScaleCategory} class.
 *
 * @author Rainer Hitz
 */
public class ScaleCategoryTest
{

  // Tests ----------------------------------------------------------------------------------------

  @Test public void testBasicConstruction() throws Exception
  {
    String name = "Test category name";
    int min = 10;
    int max = 20;
    String sensorStateValue = "State1";
    int sensorValue = 1;

    ScaleCategory category = new ScaleCategory(name, min, max, sensorStateValue, sensorValue);

    Assert.assertEquals(name, category.getName());
    Assert.assertEquals(sensorStateValue, category.getSensorStateValue());
    Assert.assertEquals(sensorValue, category.getSensorValue());
    Assert.assertEquals(min, category.getMinValue());
    Assert.assertEquals(max, category.getMaxValue());
  }

  @Test public void testFallsIntoCategory() throws Exception
  {
    String name = "Test category name";
    int min = 10;
    int max = 20;
    String sensorStateValue = "State1";
    int sensorValue = 1;

    ScaleCategory category = new ScaleCategory(name, min, max, sensorStateValue, sensorValue);

    Assert.assertFalse(category.fallsIntoCategory(9));
    Assert.assertTrue(category.fallsIntoCategory(10));
    Assert.assertTrue(category.fallsIntoCategory(20));
    Assert.assertFalse(category.fallsIntoCategory(21));
  }
}
