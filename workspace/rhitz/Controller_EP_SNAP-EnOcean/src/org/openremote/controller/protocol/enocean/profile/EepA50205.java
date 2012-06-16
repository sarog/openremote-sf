package org.openremote.controller.protocol.enocean.profile;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.enocean.ConfigurationException;
import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.EnOceanCommandBuilder;
import org.openremote.controller.protocol.enocean.packet.radio.EspRadioTelegram;
import org.openremote.controller.utils.Logger;

public class EepA50205 implements EepReceive
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * EnOcean logger. Uses a common category for all EnOcean related logging.
   */
  private final static Logger log = Logger.getLogger(EnOceanCommandBuilder.ENOCEAN_LOG_CATEGORY);


  private int temperature = 0;

  /**
   * EnOcean equipment profile (EEP) type.
   */
  private EepType eepType;

  /**
   * The command from the command configuration.
   */
  //private CommandType command;

  /**
   * TODO
   */
  private DeviceID deviceID;

  public EepA50205(EepType eepType, DeviceID deviceID, String command) throws ConfigurationException
  {
    this.eepType = eepType;
    this.deviceID = deviceID;
    //this.command = CommandType.resolve(command);
  }


  @Override public boolean update(EspRadioTelegram telegram)
  {
    log.debug("update profile A50205 instance with radio telegram");

    if(!deviceID.equals(telegram.getSenderID()))
    {
      return false;
    }

    if(eepType.getRORG() != telegram.getRORG())
    {
      // TODO: log

      return false;
    }

    int oldTemperature = temperature;

    byte[] payload = telegram.getPayload();

    temperature = payload[2] & 0xFF;

    return temperature != oldTemperature;
  }

  @Override public void updateSensor(Sensor sensor)
  {

  }

  //@Override public boolean getBoolValue() throws ConfigurationException
  //{
    // TODO
  //  throw new ConfigurationException("");
  //}

  //@Override public int getRangeValue() throws ConfigurationException
  //{
  //  log.debug("get range value from profile A50205 instance");

  //  return (int)((1.0 - (temperature/255.0)) * 40.0);
  //}

  //@Override public int getLevelValue() throws ConfigurationException
  //{
  //  return ((1 - (temperature/255)) * 100);
  //}

  //@Override public String getStateValue() throws ConfigurationException
  //{
  //  log.debug("get state value from profile A50205 instance");

  //  return String.format("%.1f", ((1.0 - (temperature/255.0)) * 40.0));
  //}
}
