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
package org.openremote.controller.protocol.enocean.port;

import org.openremote.controller.Configuration;

/**
 * EnOcean Serial Protocol (ESP) port configuration.
 *
 * @author  Rainer Hitz
 */
public class EspPortConfiguration extends Configuration
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * COM port identifier
   */
  private String comPort = null;


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Sets COM port identifier
   *
   * @param comPort  COM port identifier
   */
  public void setComPort(String comPort)
  {
    this.comPort = comPort;
  }

  /**
   * Returns COM port identifier
   *
   * @return  COM port identifier
   */
  public String getComPort()
  {
    return comPort;
  }

}
