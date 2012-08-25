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
package org.openremote.controller.protocol.enocean.profile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.enocean.ConfigurationException;
import org.openremote.controller.protocol.enocean.Constants;
import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.packet.radio.Esp31BSTelegram;
import org.openremote.controller.protocol.enocean.packet.radio.Esp34BSTelegram;

/**
 * Unit tests for {@link EepA5100B} class.
 *
 * @author Rainer Hitz
 */
public class EepA5100BTest
{

  // Private Instance Fields ----------------------------------------------------------------------

  private DeviceID deviceID;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    deviceID = DeviceID.fromString("0xFF800001");
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testBasicConstruction() throws Exception
  {
    Eep eep = EepType.lookup("A5-10-0B").createEep(
        deviceID, Constants.TEMPERATURE_STATUS_COMMAND
    );

    Assert.assertTrue(eep instanceof EepA5100B);
    Assert.assertEquals(EepType.EEP_TYPE_A5100B, eep.getType());
  }

  @Test public void testUpdateTemperature() throws Exception
  {
    EepA5100B eep = (EepA5100B)EepType.lookup("A5-10-0B").createEep(
        deviceID, Constants.TEMPERATURE_STATUS_COMMAND
    );

    Assert.assertNull(eep.getTemperature());


    int rawTempValue = 255;
    boolean isContactClosed = false;
    boolean isTeachIn = false;

    Esp34BSTelegram telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    Boolean isUpdate = eep.update(telegram);

    Assert.assertTrue(isUpdate);
    Assert.assertEquals(Double.valueOf(0), eep.getTemperature());


    rawTempValue = 255;

    telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    isUpdate = eep.update(telegram);

    Assert.assertFalse(isUpdate);
    Assert.assertEquals(Double.valueOf(0), eep.getTemperature());


    rawTempValue = 0;

    telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    isUpdate = eep.update(telegram);

    Assert.assertTrue(isUpdate);
    Assert.assertEquals(Double.valueOf(40), eep.getTemperature());


    rawTempValue = 0;
    isTeachIn = true;

    telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    isUpdate = eep.update(telegram);

    Assert.assertFalse(isUpdate);
    Assert.assertEquals(Double.valueOf(40), eep.getTemperature());

  }

  @Test public void testUpdateContact() throws Exception
  {
    EepA5100B eep = (EepA5100B)EepType.lookup("A5-10-0B").createEep(
        deviceID, Constants.CONTACT_STATE_STATUS_COMMAND
    );

    Assert.assertNull(eep.isContactClosed());


    int rawTempValue = 0;
    boolean isContactClosed = false;
    boolean isTeachIn = false;

    Esp34BSTelegram telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    Boolean isUpdate = eep.update(telegram);

    Assert.assertTrue(isUpdate);
    Assert.assertFalse(eep.isContactClosed());


    isContactClosed = false;

    telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    isUpdate = eep.update(telegram);

    Assert.assertFalse(isUpdate);
    Assert.assertFalse(eep.isContactClosed());


    isContactClosed = true;

    telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    isUpdate = eep.update(telegram);

    Assert.assertTrue(isUpdate);
    Assert.assertTrue(eep.isContactClosed());


    isContactClosed = false;
    isTeachIn = true;

    telegram = createRadioTelegram(
        deviceID, rawTempValue, isContactClosed, isTeachIn
    );

    isUpdate = eep.update(telegram);

    Assert.assertFalse(isUpdate);
    Assert.assertTrue(eep.isContactClosed());

  }

  @Test (expected = ConfigurationException.class)
  public void testUnknownCommand() throws Exception
  {
    Eep eep = EepType.lookup("A5-10-0B").createEep(
        deviceID, "UNKONWN_COMMAND"
    );
  }

  @Test public void testInvalidRadioTelegramType() throws Exception
  {
    EepA5100B eep = (EepA5100B)EepType.lookup("A5-10-0B").createEep(
        deviceID, Constants.TEMPERATURE_STATUS_COMMAND
    );

    Esp31BSTelegram invalidTelegram = new Esp31BSTelegram(deviceID, (byte)0x00, (byte)0x00);

    boolean isUpdate = eep.update(invalidTelegram);

    Assert.assertFalse(isUpdate);
  }

  @Test public void testInvalidDeviceID() throws Exception
  {
    EepA5100B eep = (EepA5100B)EepType.lookup("A5-10-0B").createEep(
        deviceID, Constants.TEMPERATURE_STATUS_COMMAND
    );

    int rawTempValue = 0;
    boolean isContactClosed = false;
    boolean isTeachIn = false;
    DeviceID invalidDeviceID = DeviceID.fromString("0xFF800002");

    Esp34BSTelegram telegram = createRadioTelegram(
        invalidDeviceID, rawTempValue, isContactClosed, isTeachIn
    );

    Boolean isUpdate = eep.update(telegram);

    Assert.assertFalse(isUpdate);
  }


  // Helpers --------------------------------------------------------------------------------------

  private Esp34BSTelegram createRadioTelegram(DeviceID deviceID, int rawTempValue,
                                              boolean isContactClosed, boolean isTeachIn)
  {
    byte[] payload = new byte[4];
    payload[2] = (byte)rawTempValue;
    payload[3] |= (byte)(isTeachIn ? 0x00 : 0x08);
    payload[3] |= (byte)(isContactClosed ? 0x00 : 0x01);

    Esp34BSTelegram telegram = new Esp34BSTelegram(deviceID, payload, (byte)0x00);

    return telegram;
  }
}
