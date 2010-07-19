/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import org.junit.Test;
import org.junit.Assert;

/**
 * KNX GroupAddress unit tests
 * {@link org.openremote.controller.protocol.knx.GroupAddress}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class GroupAddressTest
{

  @Test public void testGroupAddressFormattingBasic()
  {
    byte one = 0x00;
    byte two = 0x00;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(new byte[] { one, two }).equals("0/0/0"));

    one = 0x00;
    two = 0x01;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(new byte[] { one, two }).equals("0/0/1"));

    one = 0x01;
    two = 0x01;

    Assert.assertTrue(GroupAddress.formatToMainMiddleSub(new byte[] { one, two }).equals("0/1/1"));
  }

  @Test public void testGroupAddressFormattingHibits()
  {
    byte one = 0;
    byte two = -1;

    String address = GroupAddress.formatToMainMiddleSub(new byte[] { one, two });

    Assert.assertTrue(
        "Was expecting 0/0/255, got '" + address + "'.",
        address.equals("0/0/255")
    );

    one = -1;
    two = -1;

    address = GroupAddress.formatToMainMiddleSub(new byte[] { one, two });

    Assert.assertTrue(
        "Was expecting 31/7/255, got '" + address + "'.",
        address.equals("31/7/255")
    );
  }

}
