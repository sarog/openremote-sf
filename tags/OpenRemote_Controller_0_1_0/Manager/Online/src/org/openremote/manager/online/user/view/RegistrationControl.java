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
package org.openremote.manager.online.user.view;

import java.io.PrintWriter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.manager.online.user.model.Registration;

/**
 * A control component for user registration web user interface.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 * @version $Id: $
 */
public class RegistrationControl extends HttpServlet
{

  /**
   * Forwards GET requests for user registration form to the Registration.jsp
   *
   * @param request   see superclass
   * @param response  see superclass
   */
  @Override public void doGet(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
      RequestDispatcher dispatcher = request.getRequestDispatcher("/Register.jsp");
      dispatcher.forward(request, response);
    }
    catch (Throwable t)
    {
      // TODO
      throw new Error(t);
    }
  }

  /**
   * Handles the HTTP POST request from user registration form.   <p>
   *
   * The form validation and persistence is delegated to a
   * {@link org.openremote.manager.online.user.model.RegistrationSession} bean.
   *
   * @param request   see superclass
   * @param response  see superclass
   */
  @Override public void doPost(HttpServletRequest request, HttpServletResponse response)
  {
    final String REGISTRATION_SESSION = "OnlineManager/RegistrationSession/local";

    try
    {
      Registration registration = (Registration)new InitialContext().lookup(REGISTRATION_SESSION);
      Registration.FormValidation valid = registration.registerNewUser(request.getParameterMap());

      response.setContentType("text/plain");
      PrintWriter writer = response.getWriter();
      writer.write(valid.toString()); 
    }
    catch (NamingException e)
    {
      // TODO
      throw new Error(e);
    }
    catch (Throwable t)
    {
      // TODO
      throw new Error(t);
    }
  }
}
