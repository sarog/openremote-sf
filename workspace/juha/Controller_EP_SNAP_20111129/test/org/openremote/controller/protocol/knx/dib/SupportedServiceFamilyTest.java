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
package org.openremote.controller.protocol.knx.dib;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.protocol.knx.ServiceTypeIdentifier;

/**
 * Unit test for {@link SupportedServiceFamily} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SupportedServiceFamilyTest
{

  @Test public void testConstructor()
  {
    SupportedServiceFamily ssf = new SupportedServiceFamily(
        new HashMap<ServiceTypeIdentifier.Family, Integer>());

    Assert.assertTrue(ssf.getStructureSize() == 2);
    Assert.assertTrue(ssf.type.equals(DescriptionInformationBlock.TypeCode.SUPPORTED_SERVICE_FAMILIES));

    byte[] struct = ssf.getFrameStructure();

    Assert.assertTrue(struct[0] == 2);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.SUPPORTED_SERVICE_FAMILIES.getValue());
    Assert.assertTrue(struct.length == 2);

    // test with couple of families...

    Map<ServiceTypeIdentifier.Family, Integer> families = new HashMap<ServiceTypeIdentifier.Family, Integer>();

    families.put(ServiceTypeIdentifier.Family.CORE, 10);
    families.put(ServiceTypeIdentifier.Family.DEVICE_MANAGEMENT, 10);
    families.put(ServiceTypeIdentifier.Family.ROUTING, 10);
    families.put(ServiceTypeIdentifier.Family.TUNNELING, 10);

    ssf = new SupportedServiceFamily(families);

    Assert.assertTrue(ssf.getStructureSize() == 10);

    struct = ssf.getFrameStructure();

    Assert.assertTrue(struct.length == 10);
    Assert.assertTrue(struct[0] == 10);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.SUPPORTED_SERVICE_FAMILIES.getValue());


    // test core family..

    families = new HashMap<ServiceTypeIdentifier.Family, Integer>();
    families.put(ServiceTypeIdentifier.Family.CORE, 10);
    ssf = new SupportedServiceFamily(families);

    Assert.assertTrue(ssf.getStructureSize() == 4);
    struct = ssf.getFrameStructure();

    Assert.assertTrue(struct.length == 4);
    Assert.assertTrue(struct[0] == 4);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.SUPPORTED_SERVICE_FAMILIES.getValue());
    Assert.assertTrue(struct[2] == ServiceTypeIdentifier.Family.CORE.getValue());
    Assert.assertTrue(struct[3] == 10);


    // test larger version value just to check we don't trip on Java's signed byte...

    families = new HashMap<ServiceTypeIdentifier.Family, Integer>();
    families.put(ServiceTypeIdentifier.Family.ROUTING, 255);
    ssf = new SupportedServiceFamily(families);

    Assert.assertTrue(ssf.getStructureSize() == 4);
    struct = ssf.getFrameStructure();
    int version = struct[3] & 0xFF;

    Assert.assertTrue(struct.length == 4);
    Assert.assertTrue(struct[0] == 4);
    Assert.assertTrue(struct[1] == DescriptionInformationBlock.TypeCode.SUPPORTED_SERVICE_FAMILIES.getValue());
    Assert.assertTrue(struct[2] == ServiceTypeIdentifier.Family.ROUTING.getValue());
    Assert.assertTrue(version == 255);

  }


}

