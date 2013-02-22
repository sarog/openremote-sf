/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.controller.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is used to let the panels (such as iPhone, Android) logout the controller. 
 * HTTP based authentication doesn't support logout, a way (hack) to logout of HTTP-Basic-auth is to let the user follow a link to a special
 * 'logout' resource which will always return "401 Unauthorized", after that the panels will reset the user's credentials
 * and therefore stop sending them.
 * 
 * @author Javen
 * 
 */
@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {
   public static final int LOGOUT_ERROR_CODE = 401;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   response.sendError(LOGOUT_ERROR_CODE);
	}

}
