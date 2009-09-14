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


import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * The Class FileUploadController.
 * 
 * @author handy.wang
 */
public class FileUploadController extends MultiActionController {
   
   /** The resource service. */
   private ResourceService resourceService;
   
   /** The user service. */
   private UserService userService;

   /**
    * Creates the.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the model and view
    */
   @SuppressWarnings("finally")
   public ModelAndView importFile(HttpServletRequest request, HttpServletResponse response) {
      try {     
         String importJson = resourceService.getDotImportFileForRender(resourceService.getMultipartFileFromRequest(request, "file").getInputStream());
         response.getWriter().write(importJson);
      } catch (Exception e) {
         e.printStackTrace();
         response.getWriter().write("");
      } finally {
         return null;
      }
   }
   
   /**
    * Upload image.
    * 
    * @param request the request
    * @param response the response
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public void uploadImage(HttpServletRequest request, HttpServletResponse response) throws IOException{
      String userId = String.valueOf(userService.getAccount().getUser().getOid());
      MultipartFile multipartFile = resourceService.getMultipartFileFromRequest(request, "uploadImage");
      File file = resourceService.uploadImage(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), userId);
      response.getWriter().print(resourceService.getRelativeResourcePath(userId, file.getName()));
   }

   /**
    * Sets the resource service.
    * 
    * @param resourceService the new resource service
    */
   public void setResourceService(ResourceService resourceService) {
      this.resourceService = resourceService;
   }

   /**
    * Sets the user service.
    * 
    * @param userService the new user service
    */
   public void setUserService(UserService userService) {
      this.userService = userService;
   }
}
