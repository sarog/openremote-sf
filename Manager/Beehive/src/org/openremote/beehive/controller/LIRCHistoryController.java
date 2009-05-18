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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.repo.LIRCEntry;
import org.openremote.beehive.repo.LogMessage;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * @author Tomsky
 *
 */
public class LIRCHistoryController extends MultiActionController {
   private String indexView;
   
   private SVNDelegateService svnDelegateService;
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
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
      String path = "";
      List<LogMessage> lms = svnDelegateService.getLogs(path);
      mav.addObject("headMessage", lms.get(lms.size() - 1));
      
      List<LIRCEntry> vendorEntries = svnDelegateService.getList(path, new Integer(lms.get(lms.size() - 1).getRevision()));
      mav.addObject("vendorEntries", vendorEntries);
      return mav;
   }
   
}
