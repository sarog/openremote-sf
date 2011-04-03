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
package org.openremote.manager.online.user.model;

import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * An entity representing a registered controller.   <p>
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
@Entity public class Controller
{


  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Private key. Auto-generated.
   */
  @Id @GeneratedValue private long id;

  /**
   * Serial number of the controller. The serial number registered by the user must match the
   * one the controller will be automatically sending back as part of the registration process.
   */
  private String serialNumber;

  /**
   * The owner of this controller. User's primary key is stored as a foreign key reference for this
   * entity.
   */
  @ManyToOne private User user;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * No-arg constructor required by the ORM framework.
   */
  protected Controller()   {}

  /**
   * Creates a new controller entity with a given user name and controller serial number.
   *
   * @param user          reference to user entity
   * @param serialNumber  controller serial number as a string
   */
  protected Controller(User user, String serialNumber)
  {
    setUser(user);
    setSerialNumber(serialNumber);
  }



  // Protected Instance Methods -------------------------------------------------------------------

  protected void setCertificate(Certificate certificate)
  {
    // TODO : certificate should go into trust store, key alias into database

  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void setUser(User user)
  {
    this.user = user;
  }

  private void setSerialNumber(String serialNumber)
  {
    this.serialNumber = serialNumber;
  }

}
