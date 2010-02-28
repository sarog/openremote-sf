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

import org.apache.log4j.Logger;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.utils.MultipartFileUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * The Class FileUploadController.
 * 
 * @author handy.wang
 */
public class FileUploadController extends MultiActionController {
   
   private static final Logger LOGGER = Logger.getLogger(FileUploadController.class);
   
   /** The resource service. */
   private ResourceService resourceService;
   
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
         String importJson = resourceService.getDotImportFileForRender(request.getSession().getId(), 
               MultipartFileUtil.getMultipartFileFromRequest(request, "file").getInputStream());
         response.getWriter().write(importJson);
      } catch (Exception e) {
         e.printStackTrace();
         response.getWriter().write("");
      } finally {
         return null;
      }
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
    * upload an image.<br />
    * your action should be : fileUploadController.htm?method=uploadImage&uploadFieldName=<b>your
    * upload Field Name</b> .you can find the image in {os.webapps.root}/../tmp/[user_session_id].<br />
    * {os.webapps.root} is defined in /config/config.properties.
    * 
    * @param request
    * @param response
    * @throws IOException
    */
   public void uploadImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
//      String sessionId = request.getSession().getId();
      String uploadFieldName = request.getParameter("uploadFieldName");

      if (uploadFieldName == null || uploadFieldName.trim().length() == 0) {
         LOGGER.error("The action must have a parameter 'uploadFieldName'");
         return;
      }

      long maxImageSize = 1024 * 1024 * 5;
      MultipartFile multipartFile = MultipartFileUtil.getMultipartFileFromRequest(request, uploadFieldName);
      if (multipartFile.getSize() == 0 || multipartFile.getSize() > maxImageSize) {
         return;
      }
      /*File file = resourceService.uploadImage(multipartFile.getInputStream(), multipartFile.getOriginalFilename(),
            sessionId);*/
      File file = resourceService.uploadImage(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
//      response.getWriter().print(resourceService.getRelativeResourcePath(sessionId,file.getName()));
      response.getWriter().print(resourceService.getRelativeResourcePathByCurrentAccount(file.getName()));
   }

}
