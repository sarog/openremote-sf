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

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.DiffStatus;
import org.openremote.beehive.repo.LogMessage;
import org.openremote.beehive.repo.DiffResult.Line;
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
   private String indexView;
   private String changeView;
   private String commitView;
   public void setIndexView(String indexView) {
      this.indexView = indexView;
   }

   public void setCommitView(String commitView) {
      this.commitView = commitView;
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
      ModelAndView mav = new ModelAndView(indexView);
      String path = "";
      List<LogMessage> lms = svnDelegateService.getLogs(path);
      mav.addObject("headMessage", lms.get(lms.size() - 1));
      if(svnDelegateService.isBlankSVN()){
         request.setAttribute("isBlankSVN", true);
      }else{
         request.setAttribute("isBlankSVN", false);
      }
      DiffStatus ds = svnDelegateService.getDiffStatus(path);
      mav.addObject("diffStatus", ds.getDiffStatus());
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
      if(!"A".equals(action)){
         List<LogMessage> lms = svnDelegateService.getLogs(path);
         mav.addObject("repoMessage", lms.get(lms.size() - 1));         
      }
      request.setAttribute("path",path);
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
   public ModelAndView commit(HttpServletRequest request, HttpServletResponse response ) throws SVNException{
      String[] items = request.getParameterValues("items");
      ModelAndView mav = new ModelAndView(commitView);
      if(items != null){
         modelService.update(items, "commit", "admin");
         mav.addObject("commitStatus", "commit success,you have commit "+items.length+" items!");
      }else{
         mav.addObject("commitStatus", "commit falure,maybe you have not select any items!");
      }
      return mav;
   }
}
