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
import org.openremote.controller.protocol.enocean.EnOceanCommandBuilder;
import org.openremote.controller.protocol.enocean.profile.EepData;
import org.openremote.controller.protocol.enocean.profile.EepDataField;
import org.openremote.controller.protocol.enocean.profile.EepDataListener;
import org.openremote.controller.utils.Logger;

/**
 * Represents a boolean EnOcean equipment profile (EEP) data field type.
 *
 *
 * @author Rainer Hitz
 */
public class Bool implements DataType, EepDataListener
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
   * a boolean value.
   */
  private BoolScale scale;

  /**
   * Raw EnOcean equipment profile (EEP) data field value.
   */
  private int rawValue;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a bool instance with given data field and scale.
   *
   * @param dataField  EnOcean equipment profile (EEP) data field
   *
   * @param scale      scale for converting raw EnOcean equipment profile (EEP) data field
   *                   values to a boolean value
   */
  public Bool(EepDataField dataField, BoolScale scale)
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
    this.rawValue = scale.getFalseCategory().getMinValue();
  }

  /**
   * Constructs a bool instance with given data field and primitive value.
   *
   * @param dataField  EnOcean equipment profile (EEP) data field.
   *
   * @param value      initial primitive value
   */
  public Bool(EepDataField dataField, boolean value)
  {
    this.dataField = dataField;
    this.rawValue = value ? 1 : 0;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the value of this <tt>Bool</tt> object as a boolean
   * primitive.
   *
   * @return <tt>true</tt> if the object represents the boolean primitive value <tt>true</tt>,
   *         otherwise <tt>false</tt>
   */
  public boolean boolValue()
  {
    if(scale != null)
    {
      ScaleCategory category = scaleRawValue();

      if(category != null)
      {
        return scale.isTrue(category);
      }
      else
      {
        log.error(
            "Failed to scale value ''{0}'' for data field ''{1}'' " +
            "because value is out of valid range.",
            rawValue, dataField
        );

        return false;
      }
    }

    else
    {
      return (rawValue == 0 ? false : true);
    }
  }


  // Implements EepDataListener --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void didUpdateData(EepData data)
  {
    rawValue = dataField.read(data);
  }

  /**
   * {@inheritDoc}
   */
  @Override public void updateData(EepData data)
  {
    try
    {
      dataField.write(rawValue, data);
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
    ScaleCategory category = scaleRawValue();

    if(category == null)
    {
      log.error(
          "Failed to update sensor ''{0}'' because value ''{1}'' " +
          "is out of valid range for data field ''{2}''",
          sensor, rawValue, dataField
      );

      return;
    }


    if(sensor instanceof StateSensor)
    {
      sensor.update(category.getSensorStateValue());
    }

    else if(sensor instanceof RangeSensor)
    {
      sensor.update("" + category.getSensorValue());
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
   * @return scale category which represents a boolean primitive value or <tt>null</tt>
   *         if there is no scale object set or there is no matching scale category for the
   *         raw value
   */
  private ScaleCategory scaleRawValue()
  {
    ScaleCategory category = null;

    if(scale != null)
    {
      category = scale.scaleRawValue(rawValue);
    }

    return category;
  }
}
