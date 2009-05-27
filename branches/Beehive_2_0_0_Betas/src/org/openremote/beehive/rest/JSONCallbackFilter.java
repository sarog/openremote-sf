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
package org.openremote.beehive.rest;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Au allen.wei
 */
public class JSONCallbackFilter implements Filter {
   private FilterConfig filterConfig;

   public void init(FilterConfig filterConfig) throws ServletException {
      this.filterConfig = filterConfig;
   }

   public void destroy() {
      this.filterConfig = null;
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
         throws IOException, ServletException {
      if (servletRequest.getParameter("callback") != null) {
         String callback = servletRequest.getParameter("callback").toString();
         JSONContentTypeRequestWrapper requestWrapper = new JSONContentTypeRequestWrapper(
               (HttpServletRequest) servletRequest, filterConfig);
         OutputStream out = servletResponse.getOutputStream();
         out.write((callback + " && " + callback + "(").getBytes());
         GenericResponseWrapper responseWrapper = new GenericResponseWrapper((HttpServletResponse) servletResponse);
         filterChain.doFilter(requestWrapper, responseWrapper);
         out.write(responseWrapper.getData());
         out.write(")".getBytes());
         out.close();
      } else {
         filterChain.doFilter(servletRequest, servletResponse);
      }

   }

   public FilterConfig getFilterConfig() {
      return this.filterConfig;
   }

   public void setFilterConfig(FilterConfig filterConfig) {
      this.filterConfig = filterConfig;
   }

}
