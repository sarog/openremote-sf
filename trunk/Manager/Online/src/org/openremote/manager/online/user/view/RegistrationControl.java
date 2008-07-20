/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
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
