/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.manager.online.user.model;


import java.util.Map;
import java.security.cert.Certificate;
import javax.ejb.Local;

/**
 * Local interface for user registration process.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
@Local public interface Registration
{

  // Enums ----------------------------------------------------------------------------------------

  public enum FormValidation
  {
    OK, LOGIN_NAME_TOO_SHORT, LOGIN_NAME_TOO_LONG, PASSWORD_MISMATCH,
    PASSWORD_TOO_SHORT, PASSWORD_TOO_LONG, WEAK_PASSWORD, MISSING_LOGIN_NAME,
    MISSING_PASSWORD, MISSING_PASSWORD_VERIFICATION, MISSING_EMAIL, MISSING_EMAIL_VERIFICATION,
    MISSING_SERIAL_NUMBER, INVALID_PASSWORD_CHARACTER, EMAIL_MISMATCH, INVALID_SERIAL_NUMBER,
    INVALID_EMAIL, MISSING_MANDATORY_FIELD
  }


  // Interface Methods ----------------------------------------------------------------------------

  FormValidation registerNewUser(Map<String, String[]> values);

  void addCertificate(String serialNumber, Certificate certificate);

}
