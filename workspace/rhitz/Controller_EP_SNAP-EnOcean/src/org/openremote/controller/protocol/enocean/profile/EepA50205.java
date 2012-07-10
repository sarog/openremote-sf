package org.openremote.controller.protocol.enocean.profile;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.enocean.ConfigurationException;
import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.EnOceanCommandBuilder;
import org.openremote.controller.protocol.enocean.datatype.*;
import org.openremote.controller.protocol.enocean.packet.radio.EspRadioTelegram;
import org.openremote.controller.utils.Logger;

public class EepA50205 implements EepReceive
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * EnOcean logger. Uses a common category for all EnOcean related logging.
   */
  private final static Logger log = Logger.getLogger(EnOceanCommandBuilder.ENOCEAN_LOG_CATEGORY);


  /**
   * TODO
   */
  private Range temperature;

  /**
   * TODO
   */
  private Bool learn;

  /**
   * EnOcean equipment profile (EEP) type.
   */
  private EepType eepType;

  /**
   * The command from the command configuration.
   */
  //private CommandType command;


  private EepData eepData;

  /**
   * TODO
   */
  private DeviceID deviceID;

  public EepA50205(EepType eepType, DeviceID deviceID, String command) throws ConfigurationException
  {
    this.eepType = eepType;
    this.deviceID = deviceID;
    //this.command = CommandType.resolve(command);

    EepDataField tempDataField = new EepDataField("TMP", 16, 8);
    LinearScale tempScale = new LinearScale(new DataRange(255, 0), new DataRange(0, 40), 1);
    this.temperature = new Range(tempDataField, tempScale);

    EepDataField learnDataField = new EepDataField("LRNB", 28, 1);
    BoolScale learnScale = new BoolScale(
        new ScaleCategory("Teach-in telegram", 0, 0, "on", 1),
        new ScaleCategory("Data telegram", 1, 1, "off", 0)
    );
    this.learn = new Bool(learnDataField, learnScale);

    this.eepData = new EepData(EepType.EEP_TYPE_A50205, 4, this.temperature, this.learn);
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

    double oldTempValue = temperature.rangeValue();

    this.eepData.update(telegram.getPayload());

    double newTempValue = temperature.rangeValue();


    return oldTempValue != newTempValue;
  }

  @Override public void updateSensor(Sensor sensor)
  {
    // TODO : evaluate learn bit

    temperature.updateSensor(sensor);
  }
}
