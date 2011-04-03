/*
* OpenRemote, the Home of the Digital Home.
* Copyright 2008, OpenRemote Inc.
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
package org.openremote.controller.daemon;

/**
 * Defines the various I/O module identifiers that have implementations on the native I/O
 * daemon.  <p>
 *
 * The IOModule field is also a header on the I/O protocol message defined by
 * {@link org.openremote.controller.daemon.IOProtocol} class.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 *
 * @see org.openremote.controller.daemon.IOProtocol
 */
public enum IOModule
{
  /**
   * I/O protocol header identifying the payload as targeted for undefined (raw) serial
   * device.
   */
  RAW_SERIAL  ("R_SERIAL"),

  /**
   * I/O protocol header identifying the payload as targeted for controlling the native I/O
   * daemon instance itself.
   */
  CONTROL     ("DCONTROL");


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Contains the exact byte serialization format for a given I/O module header. This must
   * match to the protocol handler implementation on the native I/O daemon side.
   */
  private String moduleID;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Stores the given byte serialization format of a I/O module header.
   *
   * @param moduleID  string serialization format of the I/O module header
   */
  private IOModule(String moduleID)
  {
    this.moduleID = moduleID;
  }


  // Instance Methods -----------------------------------------------------------------------------

  /**
   * Returns the string serialization format of a given I/O module header. <p>
   *
   * The returned string is used as a header sent as part of the I/O protocol message to the
   * native I/O daemon (which must have an I/O module implementation mapped to this exact string
   * header).
   *
   * @return  I/O module identifier string serialization format
   */
  protected String getModuleID()
  {
    return moduleID;
  }

}
