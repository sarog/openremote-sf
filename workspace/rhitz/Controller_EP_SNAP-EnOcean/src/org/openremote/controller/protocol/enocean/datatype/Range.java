package org.openremote.controller.protocol.enocean.datatype;


import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.enocean.profile.EepDataField;

import java.math.BigDecimal;

public class Range implements DataType
{

  // Private Instance Fields ----------------------------------------------------------------------

  private EepDataField dataField;
  private LinearScale scale = null;
  private int rawValue = 0;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   *
   * @param dataField
   * @param scale
   */
  public Range(EepDataField dataField, LinearScale scale)
  {
    this.dataField = dataField;
    this.scale = scale;
  }

  /**
   * TODO
   *
   * @param dataField
   * @param value
   */
  public Range(EepDataField dataField, int value)
  {
    this.dataField = dataField;
    this.rawValue = value;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public double rangeValue()
  {
    BigDecimal scaledValue = getScaledValue();

    return scaledValue.doubleValue();
  }


  // Implements DataType --------------------------------------------------------------------------

  @Override public void setRadioData(byte[] data)
  {
    rawValue = dataField.read(data);
  }

  @Override public byte[] getRadioData(int length)
  {
    // TODO

    return null;
  }

  @Override public void updateSensor(Sensor sensor)
  {
    BigDecimal scaledValue = getScaledValue();

    if(sensor instanceof RangeSensor)
    {
      sensor.update(scaledValue.toString());
    }

    else
    {
      // TODO
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private BigDecimal getScaledValue()
  {
    BigDecimal decValue;

    if(scale != null)
    {
      decValue = scale.scaleRawValue(rawValue);
    }

    else
    {
      decValue = BigDecimal.valueOf(rawValue);
    }

    return decValue;
  }
}
