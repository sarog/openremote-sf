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
package org.openremote.controller.console;

import java.io.IOException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A controller status servlet.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class HttpStatus extends HttpServlet
{

  @Override public void doGet(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
      Context naming = new InitialContext();

      String controllerStatus = (String)naming.lookup("Status/Controller");
      String registrationStatus = (String)naming.lookup("Status/Registration");

      request.setAttribute("Controller", controllerStatus);
      request.setAttribute("Registration", registrationStatus);

      request.getRequestDispatcher("Status.jsp").forward(request, response);
    }
    catch (NamingException e)
    {
      System.out.println(e);    // TODO
    }
    catch (ServletException e)
    {
      System.out.println(e);    // TODO
    }
    catch (IOException e)
    {
      System.out.println(e);    // TODO
    }
  }

  
}
