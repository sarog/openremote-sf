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
import org.openremote.controller.protocol.enocean.profile.EepDataField;
import org.openremote.controller.utils.Logger;

/**
 * Represents a boolean EnOcean equipment profile (EEP) data field type.
 *
 * @author Rainer Hitz
 */
public class Bool extends Ordinal
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
    super(dataField, scale);
  }

  /**
   * Constructs a bool instance with given data field and raw value.
   *
   * @param dataField  EnOcean equipment profile (EEP) data field.
   *
   * @param rawValue   raw value (<tt>true</tt> --> 1, <tt>false</tt> --> 0)
   */
  public Bool(EepDataField dataField, boolean rawValue)
  {
    super(dataField, rawValue ? 1 : 0);
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the value of this <tt>Bool</tt> object as a boolean
   * primitive.
   *
   * @return <tt>true</tt> if the object represents the boolean primitive value <tt>true</tt>,
   *         otherwise <tt>false</tt>
   */
  public Boolean boolValue()
  {
    if(category != null)
    {
      return ((BoolScale)scale).isTrue(category);
    }

    else if(rawValue != null)
    {
      return (rawValue == 0 ? false : true);
    }

    else
    {
      return null;
    }
  }


  // Implements DataType --------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void updateSensor(Sensor sensor)
  {
    if(category == null)
    {
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
      throw new RuntimeException("Unrecognized sensor type: " + sensor.toString());
    }
  }
}
