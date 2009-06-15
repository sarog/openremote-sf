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

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.LogMessage;
import org.openremote.beehive.repo.DiffResult.Line;
import org.openremote.beehive.repo.DiffStatus.Element;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * @author Tomsky
 * 
 */
public class LIRCRevisionChangesController extends MultiActionController {
   private SVNDelegateService svnDelegateService;
   private ModelService modelService;
   private SyncHistoryService syncHistoryService;
   private String indexView;
   private String changeView;
   
   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
   }
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
   }

   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

   public void setChangeView(String changeView) {
      this.changeView = changeView;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
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
      
      LogMessage headMessage = svnDelegateService.getHeadLog(Constant.ROOT_PATH);
      mav.addObject("headMessage", headMessage);
      request.getSession().setAttribute("headRevision", headMessage.getRevision().toString());
      if(svnDelegateService.isBlankSVN()){
         request.setAttribute("isBlankSVN", true);
      }else{
         request.setAttribute("isBlankSVN", false);
      }
      List<Element> ds = svnDelegateService.getDiffStatus(Constant.ROOT_PATH);
      mav.addObject("diffStatus", ds);
      return mav;
   }
   
   /**
    * Show one file's difference between workCopy with svnrepo
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    * @throws ServletRequestBindingException
    *            Exception occured when missing path or action parameter
    */
   public ModelAndView change(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
      ModelAndView mav = new ModelAndView(changeView);
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      String action = ServletRequestUtils.getRequiredStringParameter(request, "action");
      System.out.println(action);
      if(!"UNVERSIONED".equals(action) && !"ADDED".equals(action)){
         System.out.println(action);
         LogMessage repoMessage = svnDelegateService.getHeadLog(path);
         mav.addObject("repoMessage", repoMessage);     
      }
      mav.addObject("breadcrumbPath",path);
      mav.addObject("isFile", modelService.isFile(path));
      mav.addObject("action", action);
      DiffResult dr = svnDelegateService.diff(path);
      List<Line> leftLines = dr.getLeft();
      List<Line> rightLines = dr.getRight();      
      mav.addObject("leftLines", leftLines);
      mav.addObject("rightLines", rightLines);
      mav.addObject("changeCount", dr.getChangeCount());
      return mav;
   }
   
   /**
    * @param request
    *          HttpServletRequest
    * @param response
    *          HttpServletResponse
    * @return ModelAndView
    * @throws SVNException
    *          Exception occured when modelService.update() failed
    */
   public ModelAndView commit(HttpServletRequest request, HttpServletResponse response ){
      String[] items = request.getParameterValues("items");
      String comment = request.getParameter("comment");
      
      if(items != null){
         try{
            svnDelegateService.commit(items,comment, "admin");
         }catch(SVNException e){
            syncHistoryService.update("faild", new Date());
            throw e;
         }
         syncHistoryService.update("success", new Date());
      }
      return null;
   }
}
