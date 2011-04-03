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
import java.net.HttpURLConnection;
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

  public enum AddCertificateResponse
  {
    /*
     * 10.2.5 - 204 No Content
     *
     * The server has fulfilled the request but does not need to return an entity-body, and might
     * want to return updated metainformation. The response MAY include new or updated meta-
     * information in the form of entity-headers, which if present SHOULD be associated with the
     * requested variant.
     *
     * The 204 response MUST NOT include a message-body, and thus is always terminated by the first 
     * empty line after the header fields.
     */
    OK(HttpURLConnection.HTTP_NO_CONTENT),

    /*
     * 10.4.5 - 404 Not Found
     *
     * The server has not found anything matching the Request-URI. No indication is given of
     * whether the condition is temporary or permanent. The 410 (Gone) status code SHOULD be used
     * if the server knows, through some internally configurable mechanism, that an old resource
     * is permanently unavailable and has no forwarding address. This status code is commonly used
     * when the server does not wish to reveal exactly why the request has been refused, or when
     * no other response is applicable.
     */
    CONTROLLER_NOT_REGISTERED(HttpURLConnection.HTTP_NOT_FOUND),

    /*
     * 10.5.1 - 500 Internal Server Error
     *
     * The server encountered an unexpected condition which prevented it from fulfilling
     * the request.
     */
    SYSTEM_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR);


    // Enum Implementation ------------------------------------------------------------------------

    private int httpResponseCode = HttpURLConnection.HTTP_OK;

    AddCertificateResponse(int httpResponseCode)
    {
      this.httpResponseCode = httpResponseCode;
    }

    public int getHttpResponseCode()
    {
      return httpResponseCode;
    }
  }


  // Interface Methods ----------------------------------------------------------------------------

  FormValidation registerNewUser(Map<String, String[]> values);

  AddCertificateResponse addCertificate(String serialNumber, Certificate certificate);

}
