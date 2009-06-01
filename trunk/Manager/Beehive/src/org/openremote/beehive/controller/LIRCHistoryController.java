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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.repo.DiffResult;
import org.openremote.beehive.repo.LIRCEntry;
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
public class LIRCHistoryController extends MultiActionController {
   private String indexView;
   private String modelView;
   private String revisionView;
   private String contentView;
   private String fileCompareView;
   
   private SVNDelegateService svnDelegateService;
   private ModelService modelService;
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
   }
   public void setModelView(String modelView) {
      this.modelView = modelView;
   }
   public void setRevisionView(String revisionView) {
      this.revisionView = revisionView;
   }
   public void setContentView(String contentView) {
      this.contentView = contentView;
   }   
   public void setFileCompareView(String fileCompareView) {
      this.fileCompareView = fileCompareView;
   }
   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
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
      LogMessage headMessage = svnDelegateService.getHeadLog(path);
      mav.addObject("headMessage", headMessage);
      
      List<LIRCEntry> vendorEntries = svnDelegateService.getList(path, Long.valueOf(headMessage.getRevision()));
      mav.addObject("vendorEntries", vendorEntries);
      return mav;
   }
   
   /**
    * Gets the models.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the models
    * 
    * @throws ServletRequestBindingException the servlet request binding exception
    */
   public ModelAndView getModels(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException{
      ModelAndView mav = new ModelAndView(modelView);
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      if(!path.startsWith("/")){
         path = "/" + path;
      }
      mav.addObject("breadcrumbPath", path);
      mav.addObject("isFile", modelService.isFile(path));
      LogMessage vendorMessage = svnDelegateService.getHeadLog(path);
      mav.addObject("vendorMessage", vendorMessage);
      
      List<LIRCEntry> modelEntries = svnDelegateService.getList(path, Long.valueOf(vendorMessage.getRevision()));
      mav.addObject("modelEntries", modelEntries);
      return mav;
   }
   
   /**
    * Gets the revisions.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the revisions
    * 
    * @throws ServletRequestBindingException the servlet request binding exception
    */
   public ModelAndView getRevisions(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException{
      ModelAndView mav = new ModelAndView(revisionView);
      mav.addObject("headRevision", svnDelegateService.getHeadRevision());
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      if(!path.startsWith("/")){
         path = "/" + path;
      }
      mav.addObject("isFile", modelService.isFile(path));
      List<LogMessage> lms = svnDelegateService.getLogs(path);
      mav.addObject("logMessages", lms);
      mav.addObject("breadcrumbPath", path);
      return mav;
   }
   
   /**
    * Gets the content.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the content
    * 
    * @throws ServletRequestBindingException the servlet request binding exception
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public ModelAndView getContent(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException{
      ModelAndView mav = new ModelAndView(contentView);
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      Long revision = ServletRequestUtils.getLongParameter(request, "revision");      
      LogMessage modelMessage = svnDelegateService.getHeadLog(path);
      mav.addObject("modelMessage", modelMessage);
      mav.addObject("breadcrumbPath", path);
      if(revision == null){
         mav.addObject("lines", svnDelegateService.getFileContent(path,0));
      }else{
         mav.addObject("lines", svnDelegateService.getFileContent(path,revision));
      }

      return mav;
   }
   
   /**
    * Compare.
    * 
    * @param request the request
    * @param response the response
    * 
    * @return the model and view
    * 
    * @throws ServletRequestBindingException the servlet request binding exception
    */
   public ModelAndView compare(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException{
      ModelAndView mav = new ModelAndView(fileCompareView);
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      mav.addObject("breadcrumbPath", path);
      long oldRevision = ServletRequestUtils.getRequiredLongParameter(request, "rev1");
      long newRevision = ServletRequestUtils.getRequiredLongParameter(request, "rev2");
      LogMessage oldLogMessage = svnDelegateService.getLogByRevision(path, oldRevision);
      LogMessage newLogMessage = svnDelegateService.getLogByRevision(path, newRevision);
      mav.addObject("oldLogeMessage", oldLogMessage);
      mav.addObject("newLogeMessage", newLogMessage);
      DiffResult dr = svnDelegateService.diff(path,oldRevision,newRevision);
      List<Line> leftLines = dr.getLeft();
      List<Line> rightLines = dr.getRight();     
      mav.addObject("leftLines", leftLines);
      mav.addObject("rightLines", rightLines);
      mav.addObject("changeCount", dr.getChangeCount());
      return mav;
   }
   
   public ModelAndView rollBack(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, SVNException{
      String path = ServletRequestUtils.getRequiredStringParameter(request, "path");
      long revision = ServletRequestUtils.getRequiredLongParameter(request, "revision");
      svnDelegateService.rollback(path, revision);
      return null;
   }
}
