/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.rest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class Button Command REST Servlet.
 * 
 * @author Dan 2009-5-21
 */
@SuppressWarnings("serial")
public class ButtonCommandRESTServlet extends HttpServlet {
   
   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      doPost(req, resp);
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String url = request.getRequestURL().toString();
      String regexp = "rest\\/button\\/(\\d+)\\/(\\w+)";
      Pattern pattern = Pattern.compile(regexp);
      String buttonID = null;
      String commandType = null;
      Matcher matcher = pattern.matcher(url);
      if (matcher.find()) {
         buttonID = matcher.group(1);
         commandType = matcher.group(2);
         request.getSession().getServletContext().getRequestDispatcher("/cmd.htm?id=" + buttonID + "&commandType=" + commandType).forward(request,response);
      } else {
         response.sendError(400,"Bad REST Request, should be /rest/button/{button_id}/{command_type}");
      }
   }

}
