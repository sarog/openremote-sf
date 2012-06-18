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

import java.math.BigDecimal;

/**
 * Unit tests for {@link LinearScale} class.
 *
 * @author Rainer Hitz
 */
public class LinearScaleTest
{

  // Tests ----------------------------------------------------------------------------------------

  @Test public void testBasicScaling() throws Exception
  {
    DataRange rawRange = new DataRange(100, 200);
    DataRange unitsRange = new DataRange(50.0, 100.0);
    int scale = 1;

    LinearScale ls = new LinearScale(rawRange, unitsRange, scale);

    BigDecimal scaledValue = ls.scaleRawValue(100);
    Assert.assertTrue(BigDecimal.valueOf(50).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(200);
    Assert.assertTrue(BigDecimal.valueOf(100).setScale(scale).compareTo(scaledValue) == 0);


    rawRange = new DataRange(200, 100);
    unitsRange = new DataRange(50.0, 100.0);

    ls = new LinearScale(rawRange, unitsRange, scale);

    scaledValue = ls.scaleRawValue(100);
    Assert.assertTrue(BigDecimal.valueOf(100).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(200);
    Assert.assertTrue(BigDecimal.valueOf(50).setScale(scale).compareTo(scaledValue) == 0);


    rawRange = new DataRange(0, 100);
    unitsRange = new DataRange(-50.0, 50.0);

    ls = new LinearScale(rawRange, unitsRange, scale);

    scaledValue = ls.scaleRawValue(0);
    Assert.assertTrue(BigDecimal.valueOf(-50).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(50);
    Assert.assertTrue(BigDecimal.valueOf(0).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(100);
    Assert.assertTrue(BigDecimal.valueOf(50).setScale(scale).compareTo(scaledValue) == 0);


    rawRange = new DataRange(0, 100);
    unitsRange = new DataRange(50.0, -50.0);

    ls = new LinearScale(rawRange, unitsRange, scale);

    scaledValue = ls.scaleRawValue(0);
    Assert.assertTrue(BigDecimal.valueOf(50).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(50);
    Assert.assertTrue(BigDecimal.valueOf(0).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(100);
    Assert.assertTrue(BigDecimal.valueOf(-50).setScale(scale).compareTo(scaledValue) == 0);
  }

  @Test public void testFractionalDigits() throws Exception
  {
    DataRange rawRange = new DataRange(0, 100);
    DataRange unitsRange = new DataRange(0.25, 1.25);
    int scale = 2;

    LinearScale ls = new LinearScale(rawRange, unitsRange, scale);

    BigDecimal scaledValue = ls.scaleRawValue(0);
    Assert.assertTrue(BigDecimal.valueOf(0.25).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(50);
    Assert.assertTrue(BigDecimal.valueOf(0.75).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(100);
    Assert.assertTrue(BigDecimal.valueOf(1.25).setScale(scale).compareTo(scaledValue) == 0);


    scale = 1;

    ls = new LinearScale(rawRange, unitsRange, scale);

    scaledValue = ls.scaleRawValue(0);
    Assert.assertTrue(BigDecimal.valueOf(0.3).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(50);
    Assert.assertTrue(BigDecimal.valueOf(0.8).setScale(scale).compareTo(scaledValue) == 0);

    scaledValue = ls.scaleRawValue(100);
    Assert.assertTrue(BigDecimal.valueOf(1.3).setScale(scale).compareTo(scaledValue) == 0);
  }
}
