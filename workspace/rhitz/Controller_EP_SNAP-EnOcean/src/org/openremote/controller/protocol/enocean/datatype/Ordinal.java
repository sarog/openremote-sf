package org.openremote.controller.protocol.enocean.datatype;


import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.enocean.profile.EepDataField;

public class Ordinal implements DataType
{

  // Private Instance Fields ----------------------------------------------------------------------

  private EepDataField dataField;
  private CategoricalScale scale = null;
  private int rawValue = 0;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   *
   * @param dataField
   * @param scale
   */
  public Ordinal(EepDataField dataField, OrdinalScale scale)
  {
    this.dataField = dataField;
    this.scale = scale;
  }

  public Ordinal(EepDataField dataField, int value)
  {
    this.dataField = dataField;
    this.rawValue = value;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public int ordinalValue()
  {
    int scaledValue = rawValue;

    ScaleCategory category = getScaleCategory();

    if(category != null)
    {
      scaledValue = category.getSensorValue();
    }

    return scaledValue;
  }

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
    ScaleCategory category = getScaleCategory();

    if(category == null)
    {
      return;
    }

    if(sensor instanceof StateSensor && !(sensor instanceof SwitchSensor))
    {
      sensor.update(category.getSensorStringValue());
    }

    else if(sensor instanceof RangeSensor)
    {
      sensor.update(String.valueOf(category.getSensorValue()));
    }

    else
    {
      // TODO
    }
  }

  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * TODO
   *
   * @return
   */
  private ScaleCategory getScaleCategory()
  {
    ScaleCategory category = null;

    if(scale != null)
    {
      category = scale.scaleRawValue(rawValue);
    }

    return category;
  }
}
