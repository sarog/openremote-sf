/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link org.openremote.controller.protocol.knx.DataLink} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DataLinkTest
{

  /**
   * Test resolution of currently defined message codes.
   */
  @Test public void testMessageCodeResolve()
  {
    DataLink.MessageCode mc = DataLink.resolveMessageCode(DataLink.MessageCode.DATA_REQUEST_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.DATA_REQUEST);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.DATA_REQUEST_BYTE);


    mc = DataLink.resolveMessageCode(DataLink.MessageCode.DATA_CONFIRM_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.DATA_CONFIRM);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.DATA_CONFIRM_BYTE);



    mc = DataLink.resolveMessageCode(DataLink.MessageCode.DATA_INDICATE_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.DATA_INDICATE);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.DATA_INDICATE_BYTE);



    mc = DataLink.resolveMessageCode(DataLink.MessageCode.POLL_REQUEST_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.POLL_REQUEST);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.POLL_REQUEST_BYTE);


    mc = DataLink.resolveMessageCode(DataLink.MessageCode.POLL_CONFIRM_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.POLL_CONFIRM);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.POLL_CONFIRM_BYTE);


    mc = DataLink.resolveMessageCode(DataLink.MessageCode.RAW_CONFIRM_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.RAW_CONFIRM);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.RAW_CONFIRM_BYTE);



    mc = DataLink.resolveMessageCode(DataLink.MessageCode.RAW_INDICATE_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.RAW_INDICATE);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.RAW_INDICATE_BYTE);



    mc = DataLink.resolveMessageCode(DataLink.MessageCode.RAW_REQUEST_BYTE);

    // test identity, not equality
    Assert.assertTrue(mc == DataLink.RAW_REQUEST);
    Assert.assertTrue(mc.getByteValue() == DataLink.MessageCode.RAW_REQUEST_BYTE);
  }


  /**
   * Tests behavior with unknown message code
   */
  @Test public void testMessageCodeResolveUnknownCode()
  {
    DataLink.MessageCode mc = DataLink.resolveMessageCode(0);

    Assert.assertTrue(mc.getByteValue() == 0);
    Assert.assertTrue(mc.getPrimitiveName().equals(DataLink.UnknownMessageCode.UNKNOWN_PRIMITIVE));
  }
}

