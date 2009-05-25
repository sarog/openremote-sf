package org.openremote.beehive.listener;

import java.io.File;

import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.spring.SpringContext;
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
      // TODO Auto-generated method stub

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
      if (command == Command.ADD || command == Command.COMMIT) {
         System.out.println(msg);
      }
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
      if(command == Command.COMMIT){
         System.out.println(StringUtil.systemTime());
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
