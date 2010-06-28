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
package org.openremote.web.console.server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The Class BaseGWTSpringController is a supper class of rpc implement. the subclass can only return common pojo.
 */
public class BaseGWTSpringController extends RemoteServiceServlet implements Controller, ServletContextAware {
   
   private static final long serialVersionUID = 5701580528620434959L;
   
   /** The servlet context. */
   private ServletContext servletContext;

   /**
    * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
    * @param servletContext servletContext
    */
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   /**
    * @see javax.servlet.GenericServlet#getServletContext()
    * @return servletContext
    */
   public ServletContext getServletContext() {
      return servletContext;
   }

   /**
    * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    * @param request request
    * @param response response
    * @throws Exception exception
    * @return null
    */
   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//      String firefox = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.4) Gecko/20100611 Firefox/3.6.4 GTB7.0";
//      String ie = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727)";
//      if (ie.equals(request.getHeader("User-Agent"))) {
//         super.doPost(request, response);
//      } else {
//         return new ModelAndView();
//      }
      super.doPost(request, response);
      return null;
   }

}
