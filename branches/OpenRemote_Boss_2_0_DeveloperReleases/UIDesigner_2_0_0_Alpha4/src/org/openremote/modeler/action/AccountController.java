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

package org.openremote.modeler.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.modeler.service.impl.UserServiceImpl;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * The Class AccountController.
 * 
 * @author Dan 2009-09-04
 */
public class AccountController extends MultiActionController {
   
   /** The user service. */
   private UserServiceImpl userService;
   

   /**
    * Creates the.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the model and view
    */
   public ModelAndView create(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView loginMav = new ModelAndView("login");
      ModelAndView registerMav = new ModelAndView("register");
      String username = request.getParameter("username");
      String password = request.getParameter("password");
      String password2 = request.getParameter("r_password");
      registerMav.addObject("username", username);
      registerMav.addObject("password", password);
      registerMav.addObject("r_password", password2);
      String roleStr = "";
      String[] roles = request.getParameterValues("role");
      if (roles != null) {
         for (String role : roles) {
            roleStr += role;
         }
      }
      if ("".equals(username)) {
         registerMav.addObject("username_blank", true);
         return registerMav;
      }
      if ("".equals(password)) {
         registerMav.addObject("password_blank", true);
         return registerMav;
      }
      if ("".equals(password2)) {
         registerMav.addObject("r_password_blank", true);
         return registerMav;
      }
      if (!password.equals(password2)) {
         registerMav.addObject("password_error", true);
         return registerMav;
      }
      if ("".equals(roleStr)) {
         registerMav.addObject("role_blank", true);
         return registerMav;
      }
      boolean success = userService.createAccount(username, password, roleStr);
      if (success) {
         loginMav.addObject("success", success);
         loginMav.addObject("username", username);
         return loginMav;
      } else {
         registerMav.addObject("success", success);
         return registerMav;
      }
   }


   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserServiceImpl userService) {
      this.userService = userService;
   }
}
