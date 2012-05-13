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
package org.openremote.controller.protocol.enocean.packet;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.packet.command.Esp3RdIDBaseCommand;
import org.openremote.controller.protocol.enocean.port.Esp3ComPortAdapter;
import org.openremote.controller.protocol.enocean.port.EspPortConfiguration;
import org.openremote.controller.protocol.enocean.port.MockPort;

/**
 * Unit tests for {@link Esp3Processor} class.
 *
 * @author Rainer Hitz
 */
public class Esp3ProcessorTest
{
  // Class Members --------------------------------------------------------------------------------

  private static final String COM_PORT = "/dev/cu.usbserial-FTUOKF2Q";


  // Instance Fields ------------------------------------------------------------------------------

  private EspPortConfiguration portConfig;
  private MockPort mockPort;
  private Esp3ComPortAdapter portAdapter;

  private byte[] readBaseIDCommand;
  private byte[] readBaseIDResponse;

  private byte[] rpsRadioTelegram;
  private byte[] rpsRadioTelegramResponse;

  // Test Lifecycle -------------------------------------------------------------------------------

  @Before
  public void setUp()
  {
    portConfig = new EspPortConfiguration();
    portConfig.setComPort(COM_PORT);

    mockPort = new MockPort();

    portAdapter = new Esp3ComPortAdapter(mockPort, portConfig);

    readBaseIDCommand = new byte[] {
        (byte)0x55, (byte)0x00, (byte)0x01, (byte)0x00,
        (byte)0x05, (byte)0x70, (byte)0x08, (byte)0x38
    };

    readBaseIDResponse = new byte[] {
        (byte)0x55, (byte)0x00, (byte)0x05, (byte)0x00,
        (byte)0x02, (byte)0xCE, (byte)0x00, (byte)0xFF,
        (byte)0x80, (byte)0x00, (byte)0x00, (byte)0xDA
    };
  }


  @Test public void testBasicSendCommand() throws Exception
  {
    mockPort.addExpectedDataToSend(readBaseIDCommand);
    mockPort.addDataToReturn(readBaseIDResponse);
    mockPort.setRequestResponseMode();

    Esp3Processor processor = new Esp3Processor(portAdapter);

    processor.start();
    processor.stop();
    processor.start();

    Esp3RdIDBaseCommand cmd = new Esp3RdIDBaseCommand();

    cmd.send(processor);

    DeviceID expectedID = DeviceID.fromByteArray(
        new byte[] {(byte)0xFF, (byte)0x80, (byte)0x00, (byte)0x00}
    );

    DeviceID receivedID = cmd.getBaseID();

    Assert.assertEquals(expectedID, receivedID);
  }
}
