/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.protocol.enocean;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an EnOcean device manufacturer. <p>
 *
 * EnOcean manufacturer ID's are specified in section 3.13 'Manufacturer ID's of the
 * EnOcean Equipment Profiles (EEP) 2.1 specification. A more complete manufacturer ID list
 * can be found in the EnOcean Link eoManufacturer.h header file
 * (see http://www.enocean.com/fileadmin/redaktion/support/enocean-link/eo_manufacturer_8h.html).
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class Manufacturer
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * EnOcean manufacturer mapping.
   */
  private static Map<Integer, Manufacturer> manufacturers = new HashMap<Integer, Manufacturer>(100);

  /**
   * EnOcean device manufacturer Peha.
   */
  public static Manufacturer PEHA = new Manufacturer(0x001, "Peha");

  /**
   * EnOcean device manufacturer Thermokon.
   */
  public static Manufacturer THERMOKON = new Manufacturer(0x002, "Thermokon");

  /**
   * EnOcean device manufacturer Servodan.
   */
  public static Manufacturer SERVODAN = new Manufacturer(0x003, "Servodan");

  /**
   * EnOcean device manufacturer Echoflex Solutions.
   */
  public static Manufacturer ECHOFLEX = new Manufacturer(0x004, "Echoflex Solutions");

  /**
   * EnOcean device manufacturer Omnio AG.
   */
  public static Manufacturer OMNIO = new Manufacturer(0x005, "Omnio AG");

  /**
   * EnOcean device manufacturer Hardmaier Electronics.
   */
  public static Manufacturer HARDMEIER = new Manufacturer(0x006, "Hardmeier Electronics");

  /**
   * EnOcean device manufacturer Regulvar Inc.
   */
  public static Manufacturer REGULVAR = new Manufacturer(0x007, "Regulvar Inc");

  /**
   * EnOcean device manufacturer Ad Hoc Electronics.
   */
  public static Manufacturer AD_HOC = new Manufacturer(0x008, "Ad Hoc Electronics");

  /**
   * EnOcean device manufacturer Distech Controls.
   */
  public static Manufacturer DISTECH = new Manufacturer(0x009, "Distech Controls");

  /**
   * EnOcean device manufacturer Kieback&Peter .
   */
  public static Manufacturer KIEBACK_PETER = new Manufacturer(0x00A, "Kieback&Peter");

  /**
   * EnOcean device manufacturer EnOcean GmbH.
   */
  public static Manufacturer ENOCEAN = new Manufacturer(0x00B, "EnOcean GmbH");

  /**
   * EnOcean device manufacturer Probare.
   */
  public static Manufacturer PROBARE = new Manufacturer(0x00C, "Probare");

  /**
   * EnOcean device manufacturer Eltako.
   */
  public static Manufacturer ELTAKO = new Manufacturer(0x00D, "Eltako");

  /**
   * EnOcean device manufacturer Leviton.
   */
  public static Manufacturer LEVITON = new Manufacturer(0x00E, "Levition");

  /**
   * EnOcean device manufacturer Honeywell.
   */
  public static Manufacturer HONEYWELL = new Manufacturer(0x00F, "Honeywell");

  /**
   * EnOcean device manufacturer Spartan Peripheral Devices.
   */
  public static Manufacturer SPARTAN = new Manufacturer(0x010, "Spartan Peripheral Devices");

  /**
   * EnOcean device manufacturer Siemens.
   */
  public static Manufacturer SIEMENS = new Manufacturer(0x011, "Siemens");

  /**
   * EnOcean device manufacturer T-Mac.
   */
  public static Manufacturer T_MAC = new Manufacturer(0x012, "T-Mac");

  /**
   * EnOcean device manufacturer Reliable Controls Corporation.
   */
  public static Manufacturer RELIABLE = new Manufacturer(0x013, "Reliable Controls Corporation");

  /**
   * EnOcean device manufacturer Elsner Elektronik GmbH.
   */
  public static Manufacturer ELSNER = new Manufacturer(0x014, "Elsner Elektronik GmbH");

  /**
   * EnOcean device manufacturer Diehl Controls.
   */
  public static Manufacturer DIEHL = new Manufacturer(0x015, "Diehl Controls");

  /**
   * EnOcean device manufacturer BSC Computer.
   */
  public static Manufacturer BSC = new Manufacturer(0x016, "BSC Computer");

  /**
   * EnOcean device manufacturer S+S Regeltechnik GmbH.
   */
  public static Manufacturer S_S_REGELTECHNIK  = new Manufacturer(0x017, "S+S Regeltechnik GmbH");

  /**
   * EnOcean device manufacturer Masco Corporation.
   */
  public static Manufacturer MASCO = new Manufacturer(0x018, "Masco Corporation");

  /**
   * EnOcean device manufacturer Intensis Software SL.
   */
  public static Manufacturer INSTENSIS = new Manufacturer(0x019, "Intensis Software SL");

  /**
   * EnOcean device manufacturer Viessmann.
   */
  public static Manufacturer VIESSMANN = new Manufacturer(0x01A, "Viessmann");

  /**
   * EnOcean device manufacturer Lutuo Technology.
   */
  public static Manufacturer LUTUO = new Manufacturer(0x01B, "Lutuo Technology");

  /**
   * EnOcean device manufacturer Schneider Electric.
   */
  public static Manufacturer SCHNEIDER_ELECTRIC = new Manufacturer(0x01C, "Schneider Electric");

  /**
   * EnOcean device manufacturer Sauter.
   */
  public static Manufacturer SAUTER = new Manufacturer(0x01D, "Sauter");

  /**
   * EnOcean device manufacturer BootUp.
   */
  public static Manufacturer BOOT_UP = new Manufacturer(0x01E, "BootUp");

  /**
   * EnOcean device manufacturer OSRAM SYLVANIA.
   */
  public static Manufacturer OSRAM_SYLVANIA = new Manufacturer(0x01F, "OSRAM SYLVANIA");

  /**
   * EnOcean device manufacturer Unotech.
   */
  public static Manufacturer UNOTECH = new Manufacturer(0x020, "Unotech");

  /**
   * EnOcean device manufacturer Delta Controls Inc.
   */
  public static Manufacturer DELTA_CONTROLS_INC = new Manufacturer(0x021, "Delta Controls");

  /**
   * EnOcean device manufacturer UNITRONIC AG.
   */
  public static Manufacturer UNITRONIC_AG = new Manufacturer(0x022, "UNITRONIC AG");

  /**
   * EnOcean device manufacturer NanoSense.
   */
  public static Manufacturer NANOSENSE = new Manufacturer(0x023, "NanoSense");

  /**
   * EnOcean device manufacturer The S4 Group.
   */
  public static Manufacturer THE_S4_GROUP = new Manufacturer(0x024, "The S4 Group");

  /**
   * EnOcean device manufacturer MSR Solutions.
   */
  public static Manufacturer MSR_SOLUTIONS = new Manufacturer(0x025, "MSR Solutions");

  /**
   * EnOcean device manufacturer General Electric.
   */
  public static Manufacturer GE = new Manufacturer(0x026, "General Electric");

  /**
   * EnOcean device manufacturer MAICO.
   */
  public static Manufacturer MAICO = new Manufacturer(0x027, "MAICO");

  /**
   * EnOcean device manufacturer Ruskin Company.
   */
  public static Manufacturer RUSKIN_COMPANY = new Manufacturer(0x028, "Ruskin Company");

  /**
   * EnOcean device manufacturer Mangnum Energy Solutions.
   */
  public static Manufacturer MAGNUM_ENERGY_SOLUTIONS = new Manufacturer(0x029, "Magnum Energy Solutions");

  /**
   * EnOcean device manufacturer KMC Controls.
   */
  public static Manufacturer KMC_CONTROLS = new Manufacturer(0x02A, "KMC Controls");

  /**
   * EnOcean device manufacturer Ecologix Controls.
   */
  public static Manufacturer ECOLOGIX_CONTROLS = new Manufacturer(0x02B, "Ecologix Controls");

  /**
   * EnOcean device manufacturer TRIO2SYS.
   */
  public static Manufacturer TRIO_2_SYS = new Manufacturer(0x02C, "TRIO2SYS");

  /**
   * EnOcean device manufacturer AFRISO-EURO-INDEX GmbH.
   */
  public static Manufacturer AFRISO_EURO_INDEX = new Manufacturer(0x02D, "AFRISO-EURO-INDEX GmbH");

  /**
   * EnOcean device manufacturer NEC AccessTechnica.
   */
  public static Manufacturer NEC_ACCESSTECHNICA = new Manufacturer(0x030, "NEC AccessTechnica");

  /**
   * EnOcean device manufacturer ITEC Corporation.
   */
  public static Manufacturer ITEC_CORPOATION = new Manufacturer(0x031, "ITEC Corporation");

  /**
   * EnOcean device manufacturer SIMICS Co., Ltd.
   */
  public static Manufacturer SIMICX_CO_LTD = new Manufacturer(0x032, "SIMICS Co., Ltd.");

  /**
   * EnOcean device manufacturer EUROTRONIC Technology GmbH.
   */
  public static Manufacturer EUROTRONIC_TECHNOLOGY_GMBH = new Manufacturer(0x034, "EUROtronic Technology GmbH");

  /**
   * EnOcean device manufacturer ART JAPAN Co., Ltd.
   */
  public static Manufacturer ART_JAPAN_CO_LTD = new Manufacturer(0x035, "ART JAPAN Co., Ltd");

  /**
   * EnOcean device manufacturer TIANSU Automation Control System.
   */
  public static Manufacturer TIANSU_AUTOMATION_CONTROL_SYSTE_CO_LTD= new Manufacturer(0x036, "TIANSU Automation Control System");

  /**
   * EnOcean device manufacturer Gruppo Giordano IDEA S.p.A.
   */
  public static Manufacturer GRUPPO_GIORDANO_IDEA_SPA = new Manufacturer(0x038, "Gruppo Giordano IDEA S.p.A.");

  /**
   * EnOcean device manufacturer alphaEOS AG.
   */
  public static Manufacturer ALPHAEOS_AG = new Manufacturer(0x039, "alphaEOS AG");

  /**
   * EnOcean device manufacturer TAG Technologies.
   */
  public static Manufacturer TAG_TECHNOLOGIES = new Manufacturer(0x03A, "TAG Technologies");

  /**
   * EnOcean device manufacturer Cloud Buildings.
   */
  public static Manufacturer CLOUD_BUILDINGS_LTD = new Manufacturer(0x03C, "Cloud Buildings");

  /**
   * EnOcean device manufacturer Giga Concept.
   */
  public static Manufacturer GIGA_CONCEPT = new Manufacturer(0x03E, "Giga Concept");

  /**
   * EnOcean device manufacturer sensortec AG.
   */
  public static Manufacturer SENSORTEC = new Manufacturer(0x03F, "sensortec AG");

  /**
   * EnOcean device manufacturer Jaeger Direkt.
   */
  public static Manufacturer JAEGER_DIREKT = new Manufacturer(0x040, "Jaeger Direkt");

  /**
   * EnOcean device manufacturer Air System Components Inc.
   */
  public static Manufacturer AIR_SYSTEM_COMPONENTS_INC = new Manufacturer(0x041, "Air System Components Inc.");

  /**
   * Multi user EnOcean device Manufacturer. <p>
   *
   * A device manufacturers who does not have a unique manufacturer ID has to use the multi user
   * manufacturer ID.
   */
  public static Manufacturer MULTI_USER_MANUFACTURER = new Manufacturer(0x7FF, "Multi User Manufacturer ID");


  /**
   * Retrieves an EnOcean device manufacturer instance based on a manufacturer ID.
   *
   * @param   id  EnOcean device manufacturer ID.
   *
   * @return  Returns a manufacturer constant in case of a known manufacturer otherwise
   *          a new manufacturer instance with the unknown manufacture ID is returned.
   */
  public static Manufacturer fromID(int id)
  {
    Manufacturer m = manufacturers.get(id);

    if(m == null)
    {
      m = new Manufacturer(id);
    }

    return m;
  }


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Manufacturer ID.
   */
  private int id;


  /**
   * Manufacturer name.
   */
  private String name;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an EnOcean device manufacturer instance based on a manufacturer ID and name.
   *
   * @param   id    EnOcean device manufacturer ID.
   *
   * @param   name  EnOcean device manufacturer name.
   */
  private Manufacturer(int id, String name)
  {
    this.id = id;
    this.name = name;

    manufacturers.put(id, this);
  }

  /**
   * Constructs an EnOcean device manufacturer instance based on a manufacturer ID.
   *
   * @param   id    EnOcean device manufacturer ID.
   */
  private Manufacturer(int id)
  {
    this.id = id;
    this.name = "Unknown";
  }


  // Object Overrides -----------------------------------------------------------------------------

  /**
   * Tests manufacture object equality based on manufacturer ID value.
   *
   * @param   o   manufacturer object to compare to
   *
   * @return  true if equals, false otherwise
   */
  @Override public boolean equals(Object o)
  {
    if(o == null)
      return false;

    if(!o.getClass().equals(this.getClass()))
      return false;

    Manufacturer m = (Manufacturer)o;

    return this.id == m.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override public int hashCode()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override public String toString()
  {
    return String.format("Manufacturer (ID : 0x%03X, Name : '%s')", id, name);
  }

  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns the EnOcean device manufacturer ID.
   *
   * @return  manufacturer ID
   */
  public int getID()
  {
    return id;
  }

  /**
   * Returns the EnOcean device manufacture name.
   *
   * @return  manufacturer name
   */
  public String getName()
  {
    return name;
  }
}
