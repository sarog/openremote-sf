/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.manager.online.rest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.manager.online.user.model.Registration;

/**
 * Controller servlet which handles the REST API implementation for home controller registration.
 *
 * TODO : document the API
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 * @version $Id: $
 */
public class ControllerRegistration extends HttpServlet
{
  
  @Override public void doPut(HttpServletRequest request, HttpServletResponse response)
  {
    final String REGISTRATION_SESSION = "OnlineManager/RegistrationSession/local";

    String requestURI =  request.getRequestURI();
    String[] elements = requestURI.split("/");
    String serialNumber = elements[elements.length - 1];

    try
    {
      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
      Certificate certificate = certFactory.generateCertificate(new BufferedInputStream(request.getInputStream()));

      Registration registration = (Registration)new InitialContext().lookup(REGISTRATION_SESSION);
      Registration.AddCertificateResponse certResponse = registration.addCertificate(serialNumber, certificate);

      response.setStatus(certResponse.getHttpResponseCode());
    }
    catch (NamingException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (CertificateException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (IOException e)
    {
      // TODO
      throw new Error(e);
    }
 }
}
