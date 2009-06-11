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
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.exception.SVNException;
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
   private SyncHistoryService syncHistoryService;
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
   }
   
   public void setScraperService(WebscraperService scraperService) {
      this.scraperService = scraperService;
   }
   
   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
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
      SyncHistory syncHistory = syncHistoryService.getLatest();
      ModelAndView mav = new ModelAndView(indexView);
      if(syncHistory != null){
         mav.addObject(syncHistory.getType(), syncHistory.getStatus());
      }
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
      SyncHistory syncHistory = new SyncHistory();
      syncHistory.setStartTime(new Date());
      syncHistory.setType("update");
      syncHistory.setStatus("running");
      syncHistoryService.save(syncHistory);
      try{
         scraperService.syncFiles();
      }catch(SVNException e){
         syncHistoryService.update("faild", new Date());
         throw e;
      }
      syncHistoryService.update("success", new Date());
      return null;
   }
   
   /**
    * Get the sync messages when update LIRC configuration files from http://lirc.sourceforge.net/remotes/ to workCopy
    * 
    * @param request
    * @param response
    * @throws IOException
    */
   public void getSyncProgress(HttpServletRequest request, HttpServletResponse response) throws IOException{
      Progress syncProgress = scraperService.getSyncProgress();
      response.getWriter().print(syncProgress.getJson());
   }
}
