package org.openremote.controller.protocol.enocean.datatype;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.protocol.enocean.profile.EepDataField;

public class Bool implements DataType
{

  // Private Instance Fields ----------------------------------------------------------------------

  private EepDataField dataField;
  private BoolScale scale = null;
  private int rawValue = 0;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   *
   * @param dataField
   * @param scale
   */
  public Bool(EepDataField dataField, BoolScale scale)
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
  public Bool(EepDataField dataField, boolean value)
  {
    this.dataField = dataField;
    this.rawValue = value ? 1 : 0;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public boolean boolValue()
  {
    int scaledValue = rawValue;

    ScaleCategory category = getScaleCategory();

    if(category != null)
    {
      scaledValue = category.getSensorValue();
    }

    return (scaledValue == 0 ? false : true);
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
    ScaleCategory category = getScaleCategory();

    if(category == null)
    {
      return;
    }

    if(sensor instanceof StateSensor)
    {
      sensor.update(category.getSensorStringValue());
    }

    else
    {
      // TODO : log
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
