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
package org.openremote.controller.protocol.enocean.datatype;

import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.enocean.EnOceanCommandBuilder;
import org.openremote.controller.protocol.enocean.profile.EepData;
import org.openremote.controller.protocol.enocean.profile.EepDataField;
import org.openremote.controller.protocol.enocean.profile.EepDataListener;
import org.openremote.controller.utils.Logger;

/**
 * Represents an EnOcean equipment profile (EEP) data field type which
 * contains an ordinal value.
 *
 * Note that the EnOcean equipment profile (EEP) specification uses the
 * term 'Enum' for specifying ordinal data types.
 *
 *
 * @author Rainer Hitz
 */
public class Ordinal implements DataType, EepDataListener
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * EnOcean logger. Uses a common category for all EnOcean related logging.
   */
  private final static Logger log = Logger.getLogger(EnOceanCommandBuilder.ENOCEAN_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * EnOcean equipment profile (EEP) data field.
   */
  private EepDataField dataField;

  /**
   * Scale for converting raw EnOcean equipment profile (EEP) data field values to
   * an ordinal value.
   */
  private CategoricalScale scale;

  /**
   * Raw EnOcean equipment profile (EEP) data field value.
   */
  private Integer rawValue;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an ordinal instance with given data field and scale.
   *
   * @param dataField  EnOcean equipment profile (EEP) data field
   *
   * @param scale      scale for converting raw EnOcean equipment profile (EEP) data field
   *                   values to an ordinal value
   */
  public Ordinal(EepDataField dataField, CategoricalScale scale)
  {
    if(dataField == null)
    {
      throw new IllegalArgumentException("null data field");
    }

    if(scale == null)
    {
      throw new IllegalArgumentException("null scale");
    }

    this.dataField = dataField;
    this.scale = scale;
  }

  /**
   * Constructs an ordinal instance with given data field and ordinal value.
   *
   * @param dataField  EnOcean equipment profile (EEP) data field
   *
   * @param value      ordinal value
   */
  public Ordinal(EepDataField dataField, int value)
  {
    if(dataField == null)
    {
      throw new IllegalArgumentException("null data field");
    }

    this.dataField = dataField;
    this.rawValue = value;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the value of this <tt>Ordinal</tt> object as a primitive
   * value.
   *
   * @return  ordinal value. Returns <tt>null</tt> if there was no data update since creation of
   *          this <tt>Ordinal</tt> object
   */
  public Integer ordinalValue()
  {
    if(rawValue == null)
    {
      return null;
    }

    if(scale == null)
    {
      return rawValue;
    }

    Integer scaledValue = null;

    ScaleCategory category = scaleRawValue(rawValue);

    if(category != null)
    {
      scaledValue = category.getSensorValue();
    }

    return scaledValue;
  }


  // Implements EepDataListener --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void didUpdateData(EepData data)
  {
    int newRawValue = dataField.read(data);

    if(isInValidRange(newRawValue))
    {
      this.rawValue = newRawValue;
    }
    else
    {
      log.error(
          "Received out of range value ''{0}'' for data field ''{1}''",
           newRawValue, dataField
      );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override public void updateData(EepData data)
  {
    try
    {
      if(rawValue != null)
      {
        dataField.write(rawValue, data);
      }
    }
    catch (EepDataField.ValueOutOfRangeException e)
    {
      throw new RuntimeException(
          "Could not write EEP data field value: " + e.getMessage(), e
      );
    }
  }


  // Implements DataType --------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void updateSensor(Sensor sensor)
  {
    if(rawValue == null)
    {
      return;
    }

    ScaleCategory category = scaleRawValue(rawValue);

    if(category == null)
    {
      log.error(
          "Failed to update sensor ''{0}'' because value ''{1}'' " +
          "is out of valid range for data field ''{2}''",
          sensor, rawValue, dataField
      );

      return;
    }

    if(sensor instanceof StateSensor && !(sensor instanceof SwitchSensor))
    {
      sensor.update(category.getSensorStateValue());
    }

    else if(sensor instanceof RangeSensor)
    {
      sensor.update(String.valueOf(category.getSensorValue()));
    }

    else if(sensor instanceof SwitchSensor)
    {
      log.warn(
          "Failed to update sensor ''{0}'' because SWITCH sensor " +
          "cannot be linked with EEP data field ''{1}''", sensor, dataField
      );
    }

    else
    {
      throw new Error("Unrecognized sensor type: " + sensor.toString());
    }
  }

  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Determines the scale category for the raw data field value and returns it.
   *
   * @return scale category the raw value falls int or <tt>null</tt> if there is no scale
   *         object set or there is not matching scale category
   */
  private ScaleCategory scaleRawValue(Integer aRawValue)
  {
    ScaleCategory category = null;

    if(aRawValue == null || scale == null)
    {
      return null;
    }

    category = scale.scaleRawValue(aRawValue);

    return category;
  }

  /**
   * Checks if the given raw value is within valid value range.
   *
   * @param aRawValue  raw value
   *
   * @return <tt>true</tt> if value is within valid range, <tt>false</tt> otherwise
   */
  private boolean isInValidRange(int aRawValue)
  {
    if(scale == null)
    {
      return true;
    }

    return (scaleRawValue(aRawValue) != null);
  }
}
