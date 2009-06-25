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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.serviceHibernateImpl.WebscraperThread;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Tomsky
 *
 */
public class LIRCSyncController extends LIRController {
   private String indexView;
   
   public void setIndexView(String indexView) {
      this.indexView = indexView;
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
      super.addStatus(mav);
      SyncHistory lastUpdate = syncHistoryService.getLastSyncByType("update");
      if(lastUpdate != null){
         mav.addObject("lastUpdate", lastUpdate);
      }
      return mav;
   }
   
   /**
    * Update all the LIRC configuration files which in workCopy with http://lirc.sourceforge.net/remotes/.
    * 
    * Start the new Thread to release the long connection from ajax call. 
    * 
    * @param request
    * @param response
    * @return
    * @throws Exception 
    */
   public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
      new Thread(new WebscraperThread()).start();
      return null;
   }
}
