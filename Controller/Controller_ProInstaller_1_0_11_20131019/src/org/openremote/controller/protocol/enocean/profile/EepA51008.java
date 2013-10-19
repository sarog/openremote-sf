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

import org.openremote.controller.protocol.enocean.ConfigurationException;
import org.openremote.controller.protocol.enocean.DeviceID;

/**
 * Represents the EnOcean equipment profile (EEP) 'A5-10-08'. <p>
 *
 * <pre>
 *
 *     +------+------+---------------------------------------------------+
 *     | RORG |  A5  |                   4BS Telegram                    |
 *     +------+------+---------------------------------------------------+
 *     | FUNC |  10  |              Room Operating Panel                 |
 *     +------+------+---------------------------------------------------+
 *     | TYPE |  08  |Temperature Sensor, Fan Speed and Occupancy Control|
 *     +------+------+---------------------------------------------------+
 *
 * </pre>
 *
 * The 'A5-10-08' profile data is transmitted by means of 4BS radio telegrams.
 * The EnOcean Equipment Profiles (EEP) 2.1 specification defines the profile
 * structure as follows:
 *
 * <pre>
 *
 *
 *                                                              Learn Bit   Occupancy
 *                                                                   LRNB   OCC
 *                                                                    | |   | |
 *            +-------------------------------------------------------+ +---+ +
 *            |   Fan Speed   |    Not Used   |  Temperature  |       | |   | |
 *            |      FAN      |               |      TMP      |       | |   | |
 *            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *     bits   |7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|
 *            +---------------------------------------------------------------+
 *     offset |0              |8              |16             |24             |
 *            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *     byte   |      DB3      |      DB2      |      DB1      |      DB0      |
 *            +-------------------------------+-------------------------------+
 *
 *
 *     +-------------------------------------------------------------------------------------------------+
 *     |Offset|Size|  Bitrange  |    Data      |ShortCut|   Description      |Valid Range| Scale    |Unit|
 *     +-------------------------------------------------------------------------------------------------+
 *     |0     |8   |DB3.7..DB3.0|Turn-switch   |  FAN   |Turn-switch         |Enum:                      |
 *     |      |    |            |for fan speed |        |for fan speed       +---------------------------+
 *     |      |    |            |              |        |                    |210..255: Stage Auto       |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |190..209: Stage 0          |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |165..189: Stage 1          |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |145..164: Stage 2          |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |  0..144: Stage 3          |
 *     +-------------------------------------------------------------------------------------------------+
 *     |8     |8   |DB2.7..DB2.0|Not Used                                                                |
 *     +-------------------------------------------------------------------------------------------------+
 *     |16    |8   |DB1.7..DB1.0|Temperature   |  TMP   |Temperature(linear) |   255..0  | 0..+40   |°C  |
 *     +-------------------------------------------------------------------------------------------------+
 *     |24    |4   |DB0.7..DB0.4|Not Used                                                                |
 *     +-------------------------------------------------------------------------------------------------+
 *     |28    |1   |DB0.3       |Learn Bit     |  LRNB  |Learn bit           |Enum:                      |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |0: Teach-in telegram       |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |1: Data telegram           |
 *     +-------------------------------------------------------------------------------------------------+
 *     |29    |1   |DB0.2..DB0.1|Not Used                                                                |
 *     +-------------------------------------------------------------------------------------------------+
 *     |31    |1   |DB0.0       |Occupancy     |  OCC   |Occupancy button    |Enum:                      |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |0: Button pressed          |
 *     |      |    |            |              |        |                    +---------------------------+
 *     |      |    |            |              |        |                    |1: Button released         |
 *     +-------------------------------------------------------------------------------------------------+
 *
 * </pre>
 *
 * @see org.openremote.controller.protocol.enocean.packet.radio.Esp34BSTelegram
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class EepA51008 extends EepA510XX
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Bit offset of 'fan speed' data field.
   */
  static final int EEP_A51008_FAN_OFFSET = 0;

  /**
   * Bit offset of temperature data field.
   */
  static final int EEP_A51008_TMP_OFFSET = 16;

  /**
   * Bit offset of occupancy data field.
   */
  static final int EEP_A51008_OCC_OFFSET = 31;


  // Constructors ---------------------------------------------------------------------------------

  public EepA51008(DeviceID deviceID, String commandString) throws ConfigurationException
  {
    super(EepType.EEP_TYPE_A51008, deviceID);

    initCommand(commandString);

    this.fanSpeed = createFanSpeedRange(EEP_A51008_FAN_OFFSET);
    this.temperature = createTemperatureRange8Bit(EEP_A51008_TMP_OFFSET);
    this.occupancy = createOccupancyBool(EEP_A51008_OCC_OFFSET);

    this.sensorData = new EepData(EepType.EEP_TYPE_A51008, 4, fanSpeed, temperature, occupancy);
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void initCommand(String commandString) throws ConfigurationException
  {
    command = Command.toCommand(commandString, getType());

    if(command != Command.TEMPERATURE &&
       command != Command.OCCUPANCY   &&
       command != Command.FAN_SPEED)
    {
      throw new ConfigurationException(
          "Invalid command ''{0}'' in combination with " +
          "EnOcean equipment profile (EEP) ''{1}''.", commandString, getType()
      );
    }
  }
}
