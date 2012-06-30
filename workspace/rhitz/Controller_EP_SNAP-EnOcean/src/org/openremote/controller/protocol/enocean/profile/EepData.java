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

import java.util.HashSet;
import java.util.Set;

/**
 * EnOcean equipment profile (EEP) data. <p>
 *
 * The class is used to assemble an EnOcean equipment profile data block with values from related
 * data fields or in the opposite direction push updated profile data to related data fields. <p>
 *
 * Data field instances, or more precisely their related data types, are linked as listeners
 * to instances of this class. These listeners are notified if the profile data has been
 * updated or in the opposite direction if the profile data has to be updated. <p>
 *
 * This class contains methods for retrieving and modifying data at the byte level.
 *
 *
 * @see EepDataField
 * @see EepDataListener
 *
 *
 * @author Rainer Hitz
 */
public class EepData
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Converts a given data listener array to a data listener set.
   *
   * @param  listenerArray  array of data listeners
   *
   * @return data listener set
   */
  private static Set<EepDataListener> createListenerSet(EepDataListener...listenerArray)
  {
    Set<EepDataListener> listenerSet = new HashSet<EepDataListener>(listenerArray.length);

    for(EepDataListener listener : listenerArray)
    {
      if(listener != null)
      {
        listenerSet.add(listener);
      }
    }

    return listenerSet;
  }


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * EnOcean equipment profile (EEP) data.
   */
  private byte[] data;

  /**
   * Data listeners for notifying that the profile data has been updated or that the profile
   * data has to be updated respectively.
   */
  private Set<EepDataListener> listeners;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an EnOcean equipment profile (EEP) data instance with given data length and
   * data listener set.
   *
   * @param length          length of EnOcean equipment profile (EEP) data block
   *
   * @param dataListeners   data listeners for notifying that the profile data has been updated
   *                        or that the profile data has to be updated respectively
   */
  public EepData(int length, Set<EepDataListener> dataListeners)
  {
    this.data = new byte[length];

    if(dataListeners != null)
    {
      this.listeners = dataListeners;
    }
    else
    {
      this.listeners = new HashSet<EepDataListener>();
    }
  }

  /**
   * Constructs an EnOcean equipment profile (EEP) data instance with given data length and
   * data listeners.
   *
   * @param length          length of EnOcean equipment profile (EEP) data block
   *
   * @param dataListeners   data listeners for notifying that the profile data has been updated
   *                        or that the profile data has to be updated respectively
   */
  public EepData(int length, EepDataListener...dataListeners)
  {
    this(length, EepData.createListenerSet(dataListeners));
  }

  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns length of EnOcean equipment profile (EEP) data block.
   *
   * @return data length of profile data
   */
  public int length()
  {
    return data.length;
  }

  /**
   * Sets the data byte value at the specified position.
   *
   * @param index  index of the data byte
   *
   * @param value  the value to be stored at the specified position
   */
  public void setValue(int index, int value)
  {
    data[index] = (byte)value;
  }

  /**
   * Returns the data byte value at the specified position.
   *
   * @param  index  index of the data byte
   *
   * @return the value at the specified position
   */
  public int getValue(int index)
  {
    return data[index] & 0xFF;
  }

  /**
   * Returns a copy of the EnOcean equipment profile (EEP) data as a byte array.
   * Before returning a data copy all data listeners are notified to update the
   * profile data.
   *
   * @return array containing the EnOcean equipment profile (EEP) data bytes
   */
  public byte[] asByteArray()
  {
    for(EepDataListener listener : listeners)
    {
      listener.updateData(this);
    }

    return data.clone();
  }

  /**
   * Replaces the data content of this <tt>EepData</tt> instance with the given
   * data and notifies all data listeners that the data has been updated. <p>
   *
   * @param eepData  new EEP data which replaces the old EEP data
   */
  public void update(byte[] eepData)
  {
    if(eepData == null)
    {
      throw new IllegalArgumentException("null EEP data");
    }

    if(this.data.length != eepData.length)
    {
      throw new IllegalArgumentException(
          "Invalid EEP data length, expected " + this.data.length +
          ", got " + eepData.length);
    }

    this.data = eepData.clone();

    for(EepDataListener listener : listeners)
    {
      listener.didUpdateData(this);
    }
  }
}
