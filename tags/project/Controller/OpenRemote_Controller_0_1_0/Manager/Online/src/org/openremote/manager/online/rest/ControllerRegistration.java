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
