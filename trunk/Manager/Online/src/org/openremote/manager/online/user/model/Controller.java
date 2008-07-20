/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
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
