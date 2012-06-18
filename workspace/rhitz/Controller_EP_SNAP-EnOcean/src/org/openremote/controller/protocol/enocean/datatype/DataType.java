package org.openremote.controller.protocol.enocean.datatype;


import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.enocean.packet.radio.EspRadioTelegram;

public interface DataType
{
  void setRadioData(byte[] data);
  byte[] getRadioData(int length);

  void updateSensor(Sensor sensor);
}
