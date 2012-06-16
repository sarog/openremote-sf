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

import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.enocean.*;
import org.openremote.controller.protocol.enocean.packet.radio.EspRadioTelegram;
import org.openremote.controller.utils.Logger;

/**
 * EnOcean equipment profile (EEP) F6-02-01 implementation.
 *
 *
 * @see org.openremote.controller.protocol.enocean.packet.radio.Esp3RPSTelegram
 *
 * @author Rainer Hitz
 */
public class EepF60201 implements EepTransceive
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Payload value which indicates that the energy bow of the switch has been pressed.
   */
  private static final int ENERGY_BOW_PRESS = 0x10;

  /**
   * Payload value which indicates that the energy bow of the switch has been released.
   */
  private static final int ENERGY_BOW_RELEASE = 0x00;

  /**
   * Payload value which indicates that the bottom left button has been activated.
   */
  private static final int ROCKER_AI = 0x00;

  /**
   * Payload value which indicates that the upper left button has been activated.
   */
  private static final int ROCKER_AO = 0x20;

  /**
   * Payload value which indicates that the bottom right button has been activated.
   */
  private static final int ROCKER_BI = 0x40;

  /**
   * Payload value which indicates that upper right button has been activated.
   */
  private static final int ROCKER_BO = 0x60;

  /**
   * Status byte value which indicates that the radio telegram is a N-Message. <p>
   *
   * The payload field of a N-Message contains information about which button has
   * been activated.
   */
  private static final int N_MESSAGE = 0x10;

  /**
   * Status byte value which indicates that the radio telegram is a U-Message. <p>
   *
   * The payload field of a U-Message contains the number of simultaneously activated
   * buttons.
   */
  private static final int U_MESSAGE = 0x00;

  /**
   * Status byte value which indicates that the message is related to a switch with
   * 2 rockers (as opposed to a switch with 4 rockers).
   */
  private static final int T21 = 0x20;


  // Class Members --------------------------------------------------------------------------------


  /**
   * EnOcean logger. Uses a common category for all EnOcean related logging.
   */
  private final static Logger log = Logger.getLogger(EnOceanCommandBuilder.ENOCEAN_LOG_CATEGORY);


  // Enums ----------------------------------------------------------------------------------------

  /**
   * Represents a command which can be applied to this EEP.
   */
  private enum CommandType
  {

    /**
     * Press bottom left button.
     */
    PRESS_ROCKER_AI("PRESS_ROCKER_AI", ENERGY_BOW_PRESS | ROCKER_AI, T21 | N_MESSAGE, null),

    /**
     * Release bottom left button.
     */
    RELEASE_ROCKER_AI("PRESS_ROCKER_AI", ENERGY_BOW_RELEASE, T21 | U_MESSAGE, null),

    /**
     * Press upper left button.
     */
    PRESS_ROCKER_AO("PRESS_ROCKER_AO", ENERGY_BOW_PRESS | ROCKER_AO, T21 | N_MESSAGE, null),

    /**
     * Release upper left button.
     */
    RELEASE_ROCKER_AO("PRESS_ROCKER_AO", ENERGY_BOW_RELEASE, T21 | U_MESSAGE, null),

    /**
     * Press and release bottom left button.
     */
    ON_ROCKER_A("ON_ROCKER_A", ENERGY_BOW_PRESS | ROCKER_AI, T21 | N_MESSAGE, RELEASE_ROCKER_AI),

    /**
     * Press and release upper left button.
     */
    OFF_ROCKER_A("OFF_ROCKER_A", ENERGY_BOW_PRESS | ROCKER_AO, T21 | N_MESSAGE, RELEASE_ROCKER_AO),

    /**
     * Read ON/OFF status of left rocker.
     */
    STATUS_ROCKER_A("STATUS_ROCKER_A", 0x00, 0x00, null),

    /**
     * Press bottom right button.
     */
    PRESS_ROCKER_BI("PRESS_ROCKER_BI", ENERGY_BOW_PRESS | ROCKER_BI, T21 | N_MESSAGE, null),

    /**
     * Release bottom right button.
     */
    RELEASE_ROCKER_BI("PRESS_ROCKER_BI", ENERGY_BOW_RELEASE, T21 | U_MESSAGE, null),

    /**
     * Press upper right button.
     */
    PRESS_ROCKER_BO("PRESS_ROCKER_BO", ENERGY_BOW_PRESS | ROCKER_BO, T21 | N_MESSAGE, null),

    /**
     * Release upper right button.
     */
    RELEASE_ROCKER_BO("PRESS_ROCKER_BO", ENERGY_BOW_RELEASE, T21 | U_MESSAGE, null),

    /**
     * Press and release bottom right button.
     */
    ON_ROCKER_B("ON_ROCKER_B", ENERGY_BOW_PRESS | ROCKER_BI, T21 | N_MESSAGE, RELEASE_ROCKER_BI),

    /**
     * Press and release upper right button.
     */
    OFF_ROCKER_B("OFF_ROCKER_B", ENERGY_BOW_PRESS | ROCKER_BO, T21 | N_MESSAGE, RELEASE_ROCKER_BO),

    /**
     * Read ON/OFF status of right rocker.
     */
    STATUS_ROCKER_B("STATUS_ROCKER_B", 0x00, 0x00, null);


    // Members ------------------------------------------------------------------------------------

    public static CommandType resolve(String commandString) throws ConfigurationException
    {
      commandString = commandString.toUpperCase().trim();

      CommandType[] allTypes = CommandType.values();

      for (CommandType commandType : allTypes)
      {
        if (commandType.commandString.equals(commandString))
        {
          return commandType;
        }
      }

      throw new ConfigurationException(
          "Command '" + commandString + "' is an unknown EEP F6-02-01 command."
      );
    }

    private String commandString;
    private byte[] payload;
    private byte statusByte;
    private CommandType releaseCommand;

    private CommandType(String commandString, int dataByte, int statusByte, CommandType releaseCommand)
    {
      this.commandString = commandString;
      this.payload = new byte[] {(byte)dataByte};
      this.statusByte = (byte)statusByte;

      this.releaseCommand = releaseCommand;
    }

    public byte[] getPayload()
    {
      return payload;
    }

    private byte getStatusByte()
    {
      return statusByte;
    }

    private CommandType getReleaseCommand()
    {
      return releaseCommand;
    }
  }


  // Private Instance Fields --------------------------------------------------------------------


  // TODO

  private boolean statusRockerA = false;
  private boolean statusRockerB = false;

  /**
   * EnOcean equipment profile (EEP) type.
   */
  private EepType eepType;

  /**
   * The command from the command configuration.
   */
  private CommandType command;

  /**
   * The device ID used as a radio telegram sender ID or as a filter for receiving radio
   * telegrams depending on the {@link #command}.
   */
  private DeviceID deviceID;

  /**
   * Payload content of the {@link EspRadioTelegram.RORG#RPS} radio telegram type which is
   * used to transmit EEP F6-02-01 related radio telegrams.
   */
  private byte payloadByte = 0x00;

  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an EnOcean equipment profile (EEP) F6-02-01 instance with given EEP type,
   * device ID and command.
   *
   * @param  eepType   the EnOcean equipment profile (EEP) type
   *
   * @param  deviceID  EnOcean device ID
   *
   * @param  command   command string
   *
   * @throws ConfigurationException
   *           if the command is unknown
   */
  public EepF60201(EepType eepType, DeviceID deviceID, String command) throws ConfigurationException
  {
    this.eepType = eepType;
    this.deviceID = deviceID;
    this.command = CommandType.resolve(command);
  }


  // Implements EepTransceive ---------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void send(RadioInterface radioInterface) throws ConfigurationException, ConnectionException
  {
    if(command == CommandType.STATUS_ROCKER_A ||
       command == CommandType.STATUS_ROCKER_B)
    {
      // TODO : log
      return;
    }

    CommandType releaseCommand = command.releaseCommand;


    radioInterface.sendRadio(
        eepType.getRORG(), deviceID, command.getPayload(), command.getStatusByte()
    );

    if(releaseCommand != null)
    {
      radioInterface.sendRadio(
          eepType.getRORG(), deviceID, releaseCommand.getPayload(), releaseCommand.getStatusByte()
      );
    }
  }


  @Override public boolean update(EspRadioTelegram telegram)
  {

    if(!deviceID.equals(telegram.getSenderID()))
    {
      return false;
    }

    if(eepType.getRORG() != telegram.getRORG())
    {
      // TODO: log

      return false;
    }

    byte telePayload = telegram.getPayload()[0];

    boolean oldStatusRockerA = statusRockerA;
    boolean oldStatusRockerB = statusRockerB;

    if(isEnergyBowPressed(telePayload))
    {
      if(isButtonPressed(telePayload, ROCKER_AI))
      {
        statusRockerA = true;
      }

      else if(isButtonPressed(telePayload, ROCKER_AO))
      {
        statusRockerA = false;
      }

      else if(isButtonPressed(telePayload, ROCKER_BI))
      {
        statusRockerB = true;
      }

      else if(isButtonPressed(telePayload, ROCKER_BO))
      {
        statusRockerB = false;
      }
    }

    return (statusRockerA != oldStatusRockerA) ||
           (statusRockerB != oldStatusRockerB);
  }

  @Override public void updateSensor(Sensor sensor) throws ConfigurationException
  {
    if(sensor instanceof StateSensor)
    {
      sensor.update(getStateValue());
    }

    else
    {
      // TODO
      throw new ConfigurationException("");
    }
  }

  public boolean getBoolValue() throws ConfigurationException
  {
    if(command == CommandType.STATUS_ROCKER_A)
    {
      return statusRockerA;
    }

    else if(command == CommandType.STATUS_ROCKER_B)
    {
      return statusRockerB;
    }

    else
    {
      // TODO
      throw new ConfigurationException("");
    }
  }

  public String getStateValue() throws ConfigurationException
  {
    return getBoolValue() == true ? "on" : "off";
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private boolean isEnergyBowPressed(byte value)
  {
    return ((value & (byte)ENERGY_BOW_PRESS) > 0);
  }

  private boolean isButtonPressed(int value, int button)
  {
    int maskedValue = value & 0xE0;

    return ((maskedValue & button) == button) &&
           ((maskedValue | button) == button);
  }
}
