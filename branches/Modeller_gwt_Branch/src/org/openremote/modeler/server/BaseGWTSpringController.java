/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.server;

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

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = 8359963960220818310L;
   
   /** The servlet context. */
   private ServletContext servletContext;

   /* (non-Javadoc)
    * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
    */
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   /* (non-Javadoc)
    * @see javax.servlet.GenericServlet#getServletContext()
    */
   public ServletContext getServletContext() {
      return servletContext;
   }

   /* (non-Javadoc)
    * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
      super.doPost(request, response);
      return null;
   }

}
