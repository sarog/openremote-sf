/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.manager.online.user.model;

import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Manages user registration.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
@Stateless public class RegistrationSession implements Registration
{

  @PersistenceContext(unitName = "OpenRemote Online Manager User Registration")
  private EntityManager persistence;


  // Implements Registration --------------------------------------------------------------------------

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

    System.out.println("Added box SN: " + serialNumber + " to user " + loginName);

    return FormValidation.OK;
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
