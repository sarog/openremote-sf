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
package org.openremote.controller.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.event.CommandType;
import org.openremote.controller.exception.InvalidCommandTypeException;
import org.openremote.controller.service.ButtonCommandService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * The Class ButtonCommandController.
 */
public class ButtonCommandController extends AbstractController {

   /** The button command service. */
   private ButtonCommandService buttonCommandService;
   
   @Override
   protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
      String buttonID = request.getParameter("id");
      String commandTypeStr = request.getParameter("commandType");
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
      return null;
   }
   

   /**
    * Sets the button command service.
    * 
    * @param buttonCommandService the new button command service
    */
   public void setButtonCommandService(ButtonCommandService buttonCommandService) {
      this.buttonCommandService = buttonCommandService;
   }
   

}
