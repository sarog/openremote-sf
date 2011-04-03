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
package org.openremote.controller.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.service.FileService;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * The controller for Configuration management.
 * 
 * @author Dan 2009-5-14
 */
public class ConfigManageController extends MultiActionController {
   
   /** The file service. */
   private FileService fileService;
   


   /**
    * Upload zip.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the model and view
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws ServletRequestBindingException the servlet request binding exception
    */
   public ModelAndView uploadZip(HttpServletRequest request, HttpServletResponse response) throws IOException,
         ServletRequestBindingException {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      fileService.uploadConfigZip(multipartRequest.getFile("zip_file").getInputStream());
      return null;
   }

   /**
    * Sets the file service.
    * 
    * @param fileService the new file service
    */
   public void setFileService(FileService fileService) {
      this.fileService = fileService;
   }
   

}
