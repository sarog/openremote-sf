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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.ProgressService;
import org.openremote.beehive.api.service.SyncHistoryService;
import org.openremote.beehive.exception.SVNException;
import org.openremote.beehive.file.Progress;

/**
 * The Class ProgressServiceImpl.
 * 
 * @author Tomsky
 */
public class ProgressServiceImpl implements ProgressService {
   
   /** The model service. */
   private ModelService modelService;
   
   /** The sync history service. */
   private SyncHistoryService syncHistoryService;
   
   /** The configuration. */
   private Configuration configuration;
   
   /**
    * Sets the model service.
    * 
    * @param modelService the new model service
    */
   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }
   
   /**
    * Sets the sync history service.
    * 
    * @param syncHistoryService the new sync history service
    */
   public void setSyncHistoryService(SyncHistoryService syncHistoryService) {
      this.syncHistoryService = syncHistoryService;
   }
   
   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    */
   public Progress getProgress(String type) {
      double count = modelService.count();
      String logPath = syncHistoryService.getLatestByType(type).getLogPath();
      File progressFile = new File(configuration.getSyncHistoryDir()+File.separator+logPath);
      return getProgressFromFile(progressFile, "Completed!", count);
   }
   
   /**
    * Gets the progress from file.
    * 
    * @param progressFile the progress file
    * @param endTag the end tag
    * @param count the count
    * 
    * @return the progress from file
    */
   private Progress getProgressFromFile(File progressFile, String endTag, double count){
      Progress progress = new Progress();
      String message = "";
      if(progressFile.exists()){
         try {
            message = FileUtils.readFileToString(progressFile, "UTF8");
            double percent = FileUtils.readLines(progressFile, "UTF8").size()/count;
            progress.setPercent(percent);
            progress.setMessage(message);
            if(message.trim().endsWith(endTag)){
               progress.setStatus("isEnd");
            }
         } catch (IOException e) {
            SVNException ee = new SVNException("Read "+progressFile.getName()+" to string occur error!",e);
            ee.setErrorCode(SVNException.SVN_IO_ERROR);
            throw ee;
         }
      }
      return progress;
   }
}
