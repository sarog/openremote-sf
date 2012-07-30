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
package org.openremote.controller.protocol.enocean;

import static org.openremote.controller.protocol.enocean.profile.EepConstants.*;

/**
 * Constant class for global constants.
 *
 * @author Rainer Hitz
 */
public final class Constants
{
  /**
   * Command string for configuring a command which receives temperature sensor values.
   */
  public static final String TEMPERATURE_STATUS_COMMAND = EEP_TEMPERATURE_DATA_FIELD_NAME;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Private constructor to prevent instantiation of constant class.
   */
  private Constants()
  {

  }
}
