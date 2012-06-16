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

import org.openremote.controller.protocol.enocean.ConfigurationException;
import org.openremote.controller.protocol.enocean.DeviceID;
import org.openremote.controller.protocol.enocean.packet.radio.EspRadioTelegram;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the EnOcean equipment profile (EEP) number as defined in the EnOcean equipment
 * profiles specification 2.1 chapter: 1.2) General. <p>
 *
 * <pre>{@code
 *
 *            +------+------+------+
 * EEP 2.0    | ORG  | FUNC | TYPE |
 *            +------+------+------+
 * EEP 2.1    | RORG | FUNC | TYPE |
 *            +------+------+------+
 * Range(hex) |00..FF|00..3F|00..7F|
 *            +------+------+------+
 *             8 bit  6 bit  7 bit
 *
 * }</pre>
 *
 * Each EnOcean equipment profile number consists of 3 parts. The EEP number starts with
 * the radio telegram type ({@link EspRadioTelegram.RORG} followed by the "basic functionality
 * of the data content (FUNC)". The last part denotes the "type of device in its individual
 * characteristics (TYPE)".
 *
 *
 * @author Rainer Hitz
 */
public abstract class EepType
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Lookup table for all supported EnOcean equipment profiles.
   */
  private final static Map<String, EepType> lookup = new HashMap<String, EepType>(100);

  /**
   * EEP F6-02-01 type.
   */
  public final static EepType EEP_TYPE_F60201 = new EepTypeF60201();

  public final static EepType EEP_TYPE_A50205 = new EepTypeA50205();

  /**
   * End of FUNC value range: {@value}
   */
  public final static int EEP_MAX_FUNC = 0x3F;

  /**
   * End of TYPE value range: {@value}
   */
  public final static int EEP_MAX_TYPE = 0x7F;

  /**
   * Regex pattern for parsing EEP numbers.
   */
  private final static Pattern EEP_NUMBER = Pattern.compile("^([0-9A-F]{2})-([0-9A-F]{2})-([0-9A-F]{2})$");


  /**
   * Returns an EEP type instance if the given EnOcean equipment profile is supported.
   *
   * @param  eep  EnOcean equipment profile number with following format: XX-XX-XX
   *
   * @return EEP type instance if profile is supported otherwise <tt>null</tt>
   *
   * @throws InvalidEepTypeExpception
   *           if the EEP parameter cannot be parsed
   *
   */
  public static EepType lookup(String eep) throws InvalidEepTypeExpception
  {
    if(eep == null)
    {
      throw new IllegalArgumentException("null EEP");
    }

    eep = eep.toUpperCase().trim();

    EepType eepType;

    Matcher matcher = EEP_NUMBER.matcher(eep);

    if(matcher.matches())
    {
      eepType = lookup.get(eep);
    }

    else
    {
      throw new InvalidEepTypeExpception(
          "Cannot parse EEP number '" + eep +
          "' (assuming hexadecimal representation - e.g. A5-02-1A)"
      );
    }

    return eepType;
  }



  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * First part of the EEP number: RORG-XX-XX.
   */
  private final EspRadioTelegram.RORG rorg;

  /**
   * Second part of the EEP number: XX-FUNC-XX.
   */
  private final int func;

  /**
   * Third part of the EEP number: XX-XX-TYPE.
   */
  private final int type;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a new EEP type instance and puts it into the EEP lookup table of all supported
   * profiles.
   *
   * @param rorg  first part (RORG-XX-XX) of the EEP number
   *
   * @param func  second part (XX-FUNC-XX) of the EEP number
   *
   * @param type  third part (XX-XX-TYPE) of the EEP number
   */
  protected EepType(EspRadioTelegram.RORG rorg, int func, int type)
  {
    this.rorg = rorg;
    this.func = func;
    this.type = type;

    lookup.put(getEepTypeAsString(), this);
  }

  // Object Overrides -----------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public boolean equals(Object o)
  {
    if(o == null)
      return false;

    if(!o.getClass().equals(this.getClass()))
      return false;

    EepType eep = (EepType)o;

    return this.rorg == eep.rorg &&
           this.func == eep.func &&
           this.type == eep.type;
  }

  /**
   * {@inheritDoc}
   */
  @Override public int hashCode()
  {
    return ((rorg.getValue() & 0xFF) << 16) +
           (func << 8) + type;
  }

  /**
   * Returns string representation of this EEP type.
   *
   * @return EEP number with following format: RORG-FUNC-TYPE (e.g. F6-02-01)
   */
  @Override public String toString()
  {
    return getEepTypeAsString();
  }

  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Creates an EnOcean equipment profile instance which relates to this EEP type.
   *
   * @param  deviceID  EnOcean device ID
   *
   * @param  command   command string from the command configuration this EEP type relates to
   *
   * @return new EnOcean equipment profile instance
   *
   * @throws ConfigurationException
   *           if the command is unknown
   */
  public abstract Eep createEep(DeviceID deviceID, String command) throws ConfigurationException;

  /**
   * Returns the RORG-XX-XX part of the EEP type.
   *
   * @return the RORG-XX-XX part
   */
  public EspRadioTelegram.RORG getRORG()
  {
    return rorg;
  }

  /**
   * Returns the XX-FUNC-XX part of the EEP type.
   *
   * @return the value of the XX-FUNC-XX part
   */
  public int getFunc()
  {
    return func;
  }

  /**
   * Returns the XX-XX-TYPE part of the EEP type.
   *
   * @return the value of the XX-XX-TYPE part
   */
  public int getType()
  {
    return type;
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Returns string representation of EEP type.
   *
   * @return string representation of EEP type
   */
  private String getEepTypeAsString()
  {
    return String.format("%02X-%02X-%02X", rorg.getValue(), func, type);
  }

  // Nested Classes -------------------------------------------------------------------------------

  /**
   * Indicates an invalid EnOcean equipment profile (EEP) string.
   */
  public static class InvalidEepTypeExpception extends Exception
  {
    public InvalidEepTypeExpception(String msg)
    {
      super(msg);
    }
  }

  /**
   * Represents the EEP number F6-02-01.
   */
  private static class EepTypeF60201 extends EepType
  {
    /**
     * Constructs a new instance for the EEP number F6-02-01 and puts it into the
     * lookup table of all supported profiles.
     */
    public EepTypeF60201()
    {
      super(EspRadioTelegram.RORG.RPS, 0x02, 0x01);
    }

    /**
     * {@inheritDoc}
     */
    @Override public Eep createEep(DeviceID deviceID, String command) throws ConfigurationException
    {
      return new EepF60201(this, deviceID, command);
    }
  }

  /**
   * Represents the EEP number A5-02-05.
   */
  private static class EepTypeA50205 extends EepType
  {
    /**
     * Constructs a new instance for the EEP number A5-02-05 and puts it into the
     * lookup table of all supported profiles.
     */
    public EepTypeA50205()
    {
      super(EspRadioTelegram.RORG.BS4, 0x02, 0x05);
    }

    /**
     * {@inheritDoc}
     */
    @Override public Eep createEep(DeviceID deviceID, String command) throws ConfigurationException
    {
      return new EepA50205(this, deviceID, command);
    }
  }
}
