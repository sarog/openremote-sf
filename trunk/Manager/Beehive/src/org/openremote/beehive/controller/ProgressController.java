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
package org.openremote.beehive.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.ProgressService;
import org.openremote.beehive.file.Progress;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Controller for importing the LIRC configuration files from http://lirc.sourceforge.net/remotes/, and exporting a
 * specified LIRC configuration file.
 * 
 * @author Dan 2009-2-6
 */
public class ProgressController extends MultiActionController {
   
   /** The progress service. */
   private ProgressService progressService;
   private ModelService modelService;
   /**
    * Sets the progress service.
    * 
    * @param progressService the new progress service
    */
   public void setProgressService(ProgressService progressService) {
      this.progressService = progressService;
   }
   
   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }


   /**
    * Scraps and imports all the LIRC configuration files.
    * 
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * 
    * @return null
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public void getProgress(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Double count = 1D;
      String type = request.getParameter("type");
      if("update".equals(type)){
         count =  (Double) request.getSession().getAttribute("modelCount");
         if(count == null){
            count = Double.valueOf(modelService.count());
            request.getSession().setAttribute("modelCount", count);
         }
      }
      Progress progress = progressService.getProgress(type,count);
      response.getWriter().print(progress.getJson());
   }

}
