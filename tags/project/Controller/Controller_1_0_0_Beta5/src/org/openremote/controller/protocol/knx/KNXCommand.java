/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.controller.protocol.knx;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public enum KNXCommand
{
  /**
   * TODO
   */
  SWITCH_ON
  (
      new String[] { "ON" }
  ),

  /**
   * TODO
   */
  SWITCH_OFF
  (
      new String[] { "OFF" }
  );


  private String[] commandTranslations = null;

  private KNXCommand(String[] commandTranslations)
  {
    this.commandTranslations = commandTranslations;
  }

  /**
   * TODO
   *
   * @param command
   * @return
   */
  boolean isEqual(String command)
  {
    for (String translation : commandTranslations)
    {
      if (translation.equalsIgnoreCase(command))
        return true;
    }

    return false;
  }
}
