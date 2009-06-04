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
package org.openremote.beehive.listener;

import java.io.File;

import org.openremote.beehive.Configuration;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.spring.SpringContext;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.StringUtil;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;

/**
 * The listener interface for receiving SVNCommitNotify events. The class that is interested in processing a
 * SVNCommitNotify event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addSVNCommitNotifyListener<code> method. When
 * the SVNCommitNotify event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SVNCommitNotifyEvent
 * @author Dan 2009-5-24
 */
public class SVNCommitNotifyListener implements ISVNNotifyListener {
   
   private int command;
   
   /** The vendor service. */
   private static VendorService vendorService = (VendorService) SpringContext.getInstance().getBean("vendorService");;

   /** The model service. */
   private static ModelService modelService = (ModelService) SpringContext.getInstance().getBean("modelService");
   
   /** The configuration. */
   private static Configuration configuration = (Configuration) SpringContext.getInstance().getBean("configuration");
   
   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logCommandLine(java.lang.String)
    */
   public void logCommandLine(String msg) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logCompleted(java.lang.String)
    */
   public void logCompleted(String msg) {
   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logError(java.lang.String)
    */
   public void logError(String msg) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logMessage(java.lang.String)
    */
   public void logMessage(String msg) {
   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#logRevision(long, java.lang.String)
    */
   public void logRevision(long revision, String msg) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#onNotify(java.io.File, org.tigris.subversion.svnclientadapter.SVNNodeKind)
    */
   public void onNotify(File file, SVNNodeKind kind) {
      if(command == Command.ADD){
         FileUtil.writeStringToFile(StringUtil.appendFileSeparator(configuration.getScrapDir())+Constant.COMMIT_PROGRESS_FILE, "Adding        "+FileUtil.relativeWorkcopyPath(file));
      }else if(command == Command.COMMIT){
         FileUtil.writeStringToFile(StringUtil.appendFileSeparator(configuration.getScrapDir())+Constant.COMMIT_PROGRESS_FILE, "Committing    "+FileUtil.relativeWorkcopyPath(file));
         if(kind == SVNNodeKind.DIR){
            vendorService.syncWith(file);
         }else if(kind == SVNNodeKind.FILE){
            modelService.syncWith(file);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.tigris.subversion.svnclientadapter.ISVNNotifyListener#setCommand(int)
    */
   public void setCommand(int cmd) {
      this.command = cmd;
   }

   
}
