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

import nl.captcha.Captcha;

import org.apache.commons.lang.StringUtils;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.service.impl.UserServiceImpl;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * The Class AccountController.
 * 
 * @author Dan 2009-09-04
 */
public class AccountController extends MultiActionController {
   
   private UserService userService;
   
   /**
    * Creates the account.
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
      String email = request.getParameter("email");
      registerMav.addObject("username", username);
      registerMav.addObject("password", password);
      registerMav.addObject("r_password", password2);
      registerMav.addObject("email", email);
      if (StringUtils.isEmpty(username)) {
         registerMav.addObject("username_blank", true);
         return registerMav;
      }
      if (!username.matches("^[A-Za-z0-9\\.]+$")) {
         registerMav.addObject("username_invalid", true);
         return registerMav;
      }
      if (username.length() < 4 || username.length() > 30) {
         registerMav.addObject("username_length", true);
         return registerMav;
      }
      if (StringUtils.isEmpty(password)) {
         registerMav.addObject("password_blank", true);
         return registerMav;
      }
      if (password.length() < 6 || password.length() > 16) {
         registerMav.addObject("password_length", true);
         return registerMav;
      }
      if (StringUtils.isEmpty(password2)) {
         registerMav.addObject("r_password_blank", true);
         return registerMav;
      }
      if (!password.equals(password2)) {
         registerMav.addObject("password_error", true);
         return registerMav;
      }
      if (StringUtils.isEmpty(email)) {
         registerMav.addObject("email_blank", true);
         return registerMav;
      }
      if (!email.matches("^[a-zA-Z0-9_\\.]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$")) {
         registerMav.addObject("email_invalid", true);
         return registerMav;
      }
      
      //Captcha help us prevent spam and fake registrations
      Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
      //request.setCharacterEncoding("UTF-8"); // Do this so we can capture non-Latin chars
      String code = request.getParameter("code");
      if (code == null || !captcha.isCorrect(code)) { 
         registerMav.addObject("code_dismatch", true);
         return registerMav;
      }
      
      boolean success = userService.createUserAccount(username, password, email);
      if (success) {
         loginMav.addObject("needActivation", true);
         loginMav.addObject("username", username);
         loginMav.addObject("email", email);
         return loginMav;
      } else {
         registerMav.addObject("success", success);
         return registerMav;
      }
   }
   
   public ModelAndView activate(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView loginMav = new ModelAndView("login");
      String userOid = request.getParameter("uid");
      String aid = request.getParameter("aid");
      
      boolean success = userService.activateUser(userOid, aid);
      if (success) {
         User u = userService.getUserById(Long.valueOf(userOid));
         loginMav.addObject("username", u.getUsername());
      }
      loginMav.addObject("isActivated", success);
      return loginMav;
   }
   
   public ModelAndView accept(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView acceptMav = new ModelAndView("accept");
      String userOid = request.getParameter("uid");
      String hostOid = request.getParameter("cid");
      String aid = request.getParameter("aid");
      boolean success = userService.checkInvitation(userOid, hostOid, aid);
      if (success) {
         User u = userService.getUserById(Long.valueOf(userOid));
         acceptMav.addObject("email", u.getEmail());
         acceptMav.addObject("uid", u.getOid());
      }
      acceptMav.addObject("isChecked", success);
      return acceptMav;
   }
   
   public ModelAndView acceptInvition(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView loginMav = new ModelAndView("login");
      ModelAndView acceptMav = new ModelAndView("accept");
      String uid = request.getParameter("uid");
      String username = request.getParameter("username");
      String password = request.getParameter("password");
      String password2 = request.getParameter("r_password");
      String email = request.getParameter("email");
      acceptMav.addObject("uid", uid);
      acceptMav.addObject("username", username);
      acceptMav.addObject("password", password);
      acceptMav.addObject("r_password", password2);
      acceptMav.addObject("email", email);
      acceptMav.addObject("isChecked", true);
      if (StringUtils.isEmpty(username)) {
         acceptMav.addObject("username_blank", true);
         return acceptMav;
      }
      if (!username.matches("^[A-Za-z0-9\\.]+$")) {
         acceptMav.addObject("username_invalid", true);
         return acceptMav;
      }
      if (username.length() < 4 || username.length() > 30) {
         acceptMav.addObject("username_length", true);
         return acceptMav;
      }
      if (StringUtils.isEmpty(password)) {
         acceptMav.addObject("password_blank", true);
         return acceptMav;
      }
      if (password.length() < 6 || password.length() > 16) {
         acceptMav.addObject("password_length", true);
         return acceptMav;
      }
      if (StringUtils.isEmpty(password2)) {
         acceptMav.addObject("r_password_blank", true);
         return acceptMav;
      }
      if (!password.equals(password2)) {
         acceptMav.addObject("password_error", true);
         return acceptMav;
      }
      if (StringUtils.isEmpty(email)) {
         acceptMav.addObject("email_blank", true);
         return acceptMav;
      }
      if (!email.matches("^[a-zA-Z0-9_\\.]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$")) {
         acceptMav.addObject("email_invalid", true);
         return acceptMav;
      }
      
      //Captcha help us prevent spam and fake registrations
      Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
      //request.setCharacterEncoding("UTF-8"); // Do this so we can capture non-Latin chars
      String code = request.getParameter("code");
      if (code == null || !captcha.isCorrect(code)) { 
         acceptMav.addObject("code_dismatch", true);
         return acceptMav;
      }
      
      boolean success = userService.createInviteeAccount(uid, username, password, email);
      if (success) {
         loginMav.addObject("username", username);
         loginMav.addObject("email", email);
         loginMav.addObject("isAccepted", true);
         return loginMav;
      } else {
         acceptMav.addObject("success", success);
         return acceptMav;
      }
   }
   

   public void setUserService(UserServiceImpl userService) {
      this.userService = userService;
   }

}
