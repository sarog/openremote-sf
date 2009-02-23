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

import java.util.Map;
import java.security.cert.Certificate;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.NoResultException;

/**
 * Manages user registration.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
@Stateless public class RegistrationSession implements Registration
{

  @PersistenceContext(unitName = "OpenRemote Online Manager User Registration")
  private EntityManager persistence;


  // Implements Registration ----------------------------------------------------------------------

  public FormValidation registerNewUser(Map<String, String[]> values)
  {
    Object loginNameParameter = values.get("loginname");

    if (loginNameParameter == null)
      return FormValidation.MISSING_LOGIN_NAME;

    Object passwordParameter = values.get("password");

    if (passwordParameter == null)
      return FormValidation.MISSING_PASSWORD;

    Object verifyPasswordParameter = values.get("verifypassword");

    if (verifyPasswordParameter == null)
      return FormValidation.MISSING_PASSWORD_VERIFICATION;

    Object emailParameter = values.get("email");

    if (emailParameter == null)
      return FormValidation.MISSING_EMAIL;

    Object verifyEmailParameter = values.get("verifyemail");

    if (verifyEmailParameter == null)
      return FormValidation.MISSING_EMAIL_VERIFICATION;

    Object serialNumberParameter = values.get("serialnumber");

    if (serialNumberParameter == null)
      return FormValidation.MISSING_SERIAL_NUMBER;


    String loginName = ((String[])loginNameParameter)[0];
    String password = ((String[])passwordParameter)[0];
    String verifyPassword = ((String[])verifyPasswordParameter)[0];
    String email = ((String[])emailParameter)[0];
    String verifyEmail = ((String[])verifyEmailParameter)[0];
    String serialNumber = ((String[])serialNumberParameter)[0];


    FormValidation valid = validateLoginName(loginName);

    if (valid != FormValidation.OK)
      return valid;

    valid = validatePassword(password, verifyPassword);

    if (valid != FormValidation.OK)
      return valid;

    valid = validateEmail(email, verifyEmail);

    if (valid != FormValidation.OK)
      return valid;

    valid = validateSerialNumber(serialNumber);

    if (valid != FormValidation.OK)
      return valid;

    User user = new User();
    user.setLoginName(loginName);
    user.setEmail(email);

    persistence.persist(user);

    Controller controller = new Controller(user, serialNumber);

    persistence.persist(controller);

    // System.out.println("Added box SN: " + serialNumber + " to user " + loginName);

    return FormValidation.OK;
  }


  public AddCertificateResponse addCertificate(String serialNumber, Certificate certificate)
  {
    try
    {
      Query findControllerWithSerial = persistence.createQuery(
          "select controller from Controller controller where controller.serialNumber = '" + serialNumber + "'"
      );

      Controller controller = (Controller)findControllerWithSerial.getSingleResult();
      controller.setCertificate(certificate);

      persistence.persist(controller);

      System.out.println(certificate);

      return AddCertificateResponse.OK;
    }
    catch (NoResultException e)
    {
      // thrown by query.getSingleResult() -- Note that the Hibernate EM 3.2.1 (in JBAS 4.2.2)
      // omits this exception in the Javadocs although the exception does get raised in case
      // of empty result set (fixed in the SVN trunk of the latest Hibernate jpa-api).

      return AddCertificateResponse.CONTROLLER_NOT_REGISTERED;
    }
    catch (EntityNotFoundException e)
    {
      // Hibernate EntityManager 3.2.1 (used by JBoss 4.2.2.GA) query.getSingleResult() Javadoc
      // claims to throw this exception although this is probably a mistake, see the comment
      // above on NoResultException -- handling this regardless just in case.

      return AddCertificateResponse.CONTROLLER_NOT_REGISTERED;
    }
    catch (NonUniqueResultException e)
    {
      System.out.println("Implementation error, controller serial is not unique: " + e);

      return AddCertificateResponse.SYSTEM_ERROR;
    }
    catch (IllegalStateException e)
    {
      System.out.println("ImplementationError: " + e);

      return AddCertificateResponse.SYSTEM_ERROR;
    }
    catch (IllegalArgumentException e)
    {
      System.out.println("ImplementationError: " + e);

      return AddCertificateResponse.SYSTEM_ERROR;
    }
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private FormValidation validateLoginName(String loginName)
  {
    if (loginName.length() < 3)
      return FormValidation.LOGIN_NAME_TOO_SHORT;

    if (loginName.length() > 255)
      return FormValidation.LOGIN_NAME_TOO_LONG;

    return FormValidation.OK;
  }

  private FormValidation validateEmail(String email, String verifyEmail)
  {
    if (email.length() < 5)
      return FormValidation.INVALID_EMAIL;

    if (!email.contains("@"))
      return FormValidation.INVALID_EMAIL;

    if (email.equalsIgnoreCase(verifyEmail))
      return FormValidation.OK;
    else
      return FormValidation.EMAIL_MISMATCH;
  }

  private FormValidation validateSerialNumber(String serialNumber)
  {
    if (serialNumber.length() > 20)
      return FormValidation.INVALID_SERIAL_NUMBER;

    if (serialNumber.length() < 5)
      return FormValidation.INVALID_SERIAL_NUMBER;

    for (int index = 0; index < serialNumber.length(); index++)
    {
      int codepoint = serialNumber.codePointAt(index);

      if (!( (codepoint >= 0x41 && codepoint <= 0x5A) ||
             (codepoint >= 0x61 && codepoint <= 0x7A) ||
             (codepoint >= 0x30 && codepoint <= 0x39)))
      {
        return FormValidation.INVALID_SERIAL_NUMBER;
      }
    }

    return FormValidation.OK;
  }

  private FormValidation validatePassword(String password, String verifyPassword)
  {
    if (!password.equals(verifyPassword))
      return FormValidation.PASSWORD_MISMATCH;

    if (password.length() < 8)
      return FormValidation.PASSWORD_TOO_SHORT;

    if (password.length() > 255)
      return FormValidation.PASSWORD_TOO_LONG;

    int hasLowerCase = 0;
    int hasUpperCase = 0;
    int hasNumber = 0;
    int hasSpecialCharacter = 0;

    for (int index = 0; index < password.length(); ++index)
    {
      int codepoint = password.codePointAt(index);

      if (!((codepoint >= 0x20 && codepoint <= 0x7E) ||
           (codepoint >= 0xA1 && codepoint <= 0xFF)))
      {
        return FormValidation.INVALID_PASSWORD_CHARACTER;
      }

      else if ( (codepoint >= 0x41 && codepoint <= 0x5A) ||
           (codepoint >= 0xC0 && codepoint <= 0xDD))
      {
        hasUpperCase = 1;
      }

      else if ( (codepoint >= 0x61 && codepoint <= 0x7A) ||
           (codepoint >= 0xDE && codepoint <= 0xFF))
      {
        hasLowerCase = 1;
      }

      else if (codepoint >= 0x30 && codepoint <= 0x39)
      {
        hasNumber = 1;
      }

      else
      {
        hasSpecialCharacter = 1;
      }
    }

    if (hasUpperCase + hasLowerCase + hasNumber + hasSpecialCharacter <= 1)
      return FormValidation.WEAK_PASSWORD;

    return FormValidation.OK;
  }

}
