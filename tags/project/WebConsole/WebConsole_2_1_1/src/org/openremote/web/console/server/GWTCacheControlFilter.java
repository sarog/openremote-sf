/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

/**
 * This filter is used to prevent caching of GWT no-cache.js files that
 * are used by the bootstrapper; some browsers will cache this file causing
 * issues with new version startup.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
import java.io.IOException;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link Filter} to add cache control headers for GWT generated files to ensure
 * that the correct files get cached.
 * 
 * @author See Wah Cheng
 * @created 24 Feb 2009
 */
public class GWTCacheControlFilter implements Filter {

 public void destroy() {
 }

 public void init(FilterConfig config) throws ServletException {
 }

 public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
   ServletException {

  HttpServletRequest httpRequest = (HttpServletRequest) request;
  String requestURI = httpRequest.getRequestURI();

  if (requestURI.contains(".nocache.")) {
   Date now = new Date();
   HttpServletResponse httpResponse = (HttpServletResponse) response;
   httpResponse.setDateHeader("Date", now.getTime());
   // one day old
   httpResponse.setDateHeader("Expires", now.getTime() - 86400000L);
   httpResponse.setHeader("Pragma", "no-cache");
   httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
  }

  filterChain.doFilter(request, response);
 }
}
