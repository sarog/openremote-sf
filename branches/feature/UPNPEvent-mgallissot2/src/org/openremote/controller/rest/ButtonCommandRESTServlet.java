/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.rest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.controller.event.CommandType;
import org.openremote.controller.exception.ButtonCommandException;
import org.openremote.controller.exception.InvalidCommandTypeException;
import org.openremote.controller.service.ButtonCommandService;
import org.openremote.controller.spring.SpringContext;

/**
 * The Class Button Command REST Servlet.
 * 
 * @author Dan 2009-5-21
 */
@SuppressWarnings("serial")
public class ButtonCommandRESTServlet extends HttpServlet {
   
   private Logger logger = Logger.getLogger(ButtonCommandRESTServlet.class.getName());
   
   private static ButtonCommandService buttonCommandService = 
      (ButtonCommandService) SpringContext.getInstance().getBean("buttonCommandService");
   
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
      String commandTypeStr = null;
      Matcher matcher = pattern.matcher(url);
      if (matcher.find()) {
         buttonID = matcher.group(1);
         commandTypeStr = matcher.group(2);
         try{
            if (commandTypeStr != null) {
               CommandType commandType = null;
               if ("press".equalsIgnoreCase(commandTypeStr)) {
                  commandType = CommandType.SEND_START;
               } else if ("release".equalsIgnoreCase(commandTypeStr)) {
                  commandType = CommandType.SEND_STOP;
               } else if ("click".equalsIgnoreCase(commandTypeStr)) {
                  commandType = CommandType.SEND_ONCE;
               }
               if (commandType != null) {
                  buttonCommandService.trigger(buttonID, commandType);
               } else {
                  throw new InvalidCommandTypeException(commandTypeStr);
               }
            } else {
               buttonCommandService.trigger(buttonID);
            }
         } catch (ButtonCommandException e) {
            logger.error("ButtonCommandException occurs", e);
            response.sendError(e.getErrorCode(),e.getMessage());
         }
      } else {
         response.sendError(400,"Bad REST Request, should be /rest/button/{button_id}/{command_type}");
      }
   }

}
