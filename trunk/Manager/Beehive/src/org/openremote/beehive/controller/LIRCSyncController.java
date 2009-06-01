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

import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.file.Progress;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * @author Tomsky
 *
 */
public class LIRCSyncController extends MultiActionController {
   private String indexView;
   private WebscraperService scraperService;
   private SVNDelegateService svnDelegateService;
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
   }
   
   public void setScraperService(WebscraperService scraperService) {
      this.scraperService = scraperService;
   }
   
   
   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

   /**
    * Default method in controller
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    */
   public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView mav = new ModelAndView(indexView);      
      return mav;
   }
   
   /**
    * Update all the LIRC configuration files which in workCopy with http://lirc.sourceforge.net/remotes/
    * 
    * @param request
    * @param response
    * @return
    */
   public ModelAndView update(HttpServletRequest request, HttpServletResponse response) {
      request.getSession().setAttribute("isUpdating", "true");
      scraperService.scraperFiles();
      request.getSession().removeAttribute("isUpdating");
      return null;
   }
   
   /**
    * Get the scraper messages when scrap LIRC configuration files from http://lirc.sourceforge.net/remotes/ to local temp directory
    * 
    * @param request
    * @param response
    * @throws IOException
    */
   public void getScraperProgress(HttpServletRequest request, HttpServletResponse response) throws IOException{
      Progress scraperProgress = scraperService.getScraperProgress();
      response.getWriter().print(scraperProgress.getJson());
   }
   
   /**
    * Get copy messages when copy LIRC configuration files from local temp directory to svn workCopy
    * @param request
    * @param response
    * @throws IOException
    */
   public void getCopyProgress(HttpServletRequest request, HttpServletResponse response) throws IOException{
      Progress copyProgress = svnDelegateService.getCopyProgress();      
      response.getWriter().print(copyProgress.getJson());
   }
}
