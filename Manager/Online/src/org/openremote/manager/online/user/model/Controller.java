/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.manager.online.user.model;

import java.security.PublicKey;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * An entity representing a registered controller.   <p>
 *
 * This entity contains values for the controller serial number, the public key of a registered
 * controller (used in authentication and encryption), registration status and a reference to the
 * owner of the box.  <p>
 *
 * A controller registration is done in multiple phases which are represented by the status field
 * of this entity. Initial phase includes serial number registration by the user when they first
 * login to the website and register the serial number of their controller. In the second phase the
 * controller calls back to the online manager with its serial number and providing its
 * public key for storage. In the final phase of the registration process the controller
 * acknowledges it has received the default profile and is fully functional.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
@Entity public class Controller
{

  // Enums ----------------------------------------------------------------------------------------

  public static enum RegistrationStatus
  {
    REGISTRATION_INITIALIZED, PUBLIC_KEY_REGISTERED, REGISTRATION_COMPLETE
  }


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
   * Public certificate of the controller stored as part of the second phase of the
   * registration process.
   *
   * TODO
   */
  private byte[] key;

  /**
   * TODO
   */
  @Enumerated(EnumType.STRING) private RegistrationStatus status;

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
    setStatus(RegistrationStatus.REGISTRATION_INITIALIZED);
  }



  // Protected Instance Methods -------------------------------------------------------------------

  protected void setPublicKey(PublicKey key)
  {
    this.key = key.getEncoded();
    setStatus(RegistrationStatus.PUBLIC_KEY_REGISTERED);
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

  private void setStatus(RegistrationStatus status)
  {
    this.status = status;
  }
}
