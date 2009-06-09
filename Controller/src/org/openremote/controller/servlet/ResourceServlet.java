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
package org.openremote.controller.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.Configuration;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;
import org.springframework.util.FileCopyUtils;

/**
 * The Servlet to get the files in resource folder.
 * 
 * @author Dan 2009-6-9
 */
@SuppressWarnings("serial")
public class ResourceServlet extends HttpServlet {

   /** The configuration. */
   private Configuration configuration = ConfigFactory.getConfig();
   
   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String resourcePath = configuration.getResourcePath();
      String url = request.getRequestURL().toString();
      String fileName = url.substring(url.lastIndexOf("/") + 1);
      String filePath = PathUtil.appendFileSeparator(resourcePath) + fileName;
      File file = new File(filePath);
      if (file.exists()) {
         FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
      } else {
         response.sendError(HttpServletResponse.SC_NOT_FOUND, fileName);
      }
   }
   

}
