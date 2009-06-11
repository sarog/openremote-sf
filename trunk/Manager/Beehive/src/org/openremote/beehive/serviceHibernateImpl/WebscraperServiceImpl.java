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
package org.openremote.beehive.serviceHibernateImpl;

import java.io.File;
import java.util.Date;

import org.openremote.beehive.Constant;
import org.openremote.beehive.PathConfig;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.domain.SyncHistory;
import org.openremote.beehive.domain.Vendor;
import org.openremote.beehive.file.LIRCElement;
import org.openremote.beehive.file.Progress;
import org.openremote.beehive.repo.Actions;
import org.openremote.beehive.repo.DateFormatter;
import org.openremote.beehive.utils.DateUtil;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.LIRCrawler;
import org.openremote.beehive.utils.StringUtil;

/**
 * @author Tomsky
 * 
 */
public class WebscraperServiceImpl extends BaseAbstractService<Vendor> implements WebscraperService {

   private SVNDelegateService svnDelegateService;
   private ModelService modelService;
   private SyncHistoryService syncHistoryService;
   
   public SVNDelegateService getSvnDelegateService() {
      return svnDelegateService;
   }

   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }
   
   
   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
   }

   public void syncFiles() {
      Date date = new Date();
//      SyncHistory syncHistory = new SyncHistory();
//      syncHistory.setStartTime(date);
      
//      String[] time = DateUtil.getTimeFormat(date, "yyyy-MM-dd.HH-mm").split("\\.");
//      String logPath = time[0]+"/syncProgress."+time[1]+".txt";
//      syncHistory.setLogPath(logPath);
      
//      syncHistory.setType("update");
//      syncHistory.setStatus("running");
//      syncHistoryService.save(syncHistory);
      
      String syncFilePath = PathConfig.getInstance().syncProgressFilePath();
      FileUtil.deleteFileOnExist(new File(syncFilePath));
      crawl(Constant.LIRC_ROOT_URL);
      FileUtil.writeLineToFile(syncFilePath, DateFormatter.format(date)+" Sync completed!");
   }
  /**
   * {@inheritDoc}
   */
   public Progress getSyncProgress(){
      File progressFile = new File(PathConfig.getInstance().syncProgressFilePath());
      return FileUtil.getProgressFromFile(progressFile, "Sync completed!", modelService.count());
   }
   
   private void crawl(String lircUrl) {
      for (LIRCElement lirc : LIRCrawler.list(lircUrl)) {
         if (lirc.isModel()) {
            String actionType = svnDelegateService.compareFileByLastModifiedDate(lirc);
            String progressFilePath = PathConfig.getInstance().syncProgressFilePath();
            FileUtil.writeLineToFile(progressFilePath, " ["+StringUtil.systemTime()+"]  "+actionType + "  "+lirc.getRelativePath());
            if(!actionType.equals(Actions.NORMAL.getValue())){
               LIRCrawler.writeModel(lirc);
            }
         } else {
            crawl(lirc.getPath());
         }
      }
   }
}
