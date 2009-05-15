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
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.serviceHibernateImpl.SVNDelegateServiceImpl;
import org.openremote.beehive.utils.SVNClientFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Tomsky
 *
 */
public class SVNListener implements ServletContextListener {
   private ISVNClientAdapter svnClient = SVNClientFactory.getSVNClient();
   private ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-context.xml" });
   private Configuration configuration = (Configuration)ctx.getBean("configuration");
   private static Logger logger = Logger.getLogger(SVNDelegateServiceImpl.class.getName());
   
   /**
    * Create svn repo and work copy if not exist
    */
   @Override
   public void contextInitialized(ServletContextEvent arg0) {
      String svnDir = configuration.getSvnDir();
      String fileSvnPath = "file:///";
      String svnRepoPath = svnDir.substring(svnDir.indexOf(fileSvnPath) + fileSvnPath.length(),svnDir.indexOf("/trunk"));
      File svnRepo = new File(svnRepoPath);
      SVNUrl svnUrl = null;
      File workCopyDir = new File(configuration.getWorkCopyDir());
      svnUrl = checkRepoExists(svnDir, svnRepo, svnUrl, workCopyDir);
      checkWorkCopyExists(svnUrl, workCopyDir);
   }

   private SVNUrl checkRepoExists(String svnDir, File svnRepo, SVNUrl svnUrl, File workCopyDir) {
      try {
         svnUrl = new SVNUrl(svnDir);
         svnClient.getInfo(svnUrl);
      } catch (MalformedURLException e) {
         logger.error("Create SVNUrl "+svnDir+" error", e);
      } catch (SVNClientException e) {
        try {
           if(svnRepo.exists()){
              FileUtils.cleanDirectory(svnRepo);
           }else{
              svnRepo.mkdirs();
           }
           svnClient.createRepository(svnRepo, ISVNClientAdapter.REPOSITORY_BDB);
           svnClient.mkdir(svnUrl, true, "create /trunk");
           if(workCopyDir.exists()){
              FileUtils.cleanDirectory(workCopyDir);
           }else{
              workCopyDir.mkdirs();
           }
           svnClient.checkout(svnUrl, workCopyDir, SVNRevision.HEAD, true);
           logger.info("Create svn repos "+svnDir+" success!");
        } catch (SVNClientException e1) {
           logger.error("Create svn repos "+svnDir+" failure!", e);
        } catch (IOException e2) {
           logger.error("Can't clean dir " + svnRepo + " or " + workCopyDir, e2);
        }
     }
      return svnUrl;
   }

   private void checkWorkCopyExists(SVNUrl svnUrl, File workCopyDir) {
      try {
         svnClient.getInfo(workCopyDir);
      } catch (SVNClientException e) {
         try {
            if(workCopyDir.exists()){
               FileUtils.cleanDirectory(workCopyDir);
            }else{
               workCopyDir.mkdirs();
            }
            svnClient.checkout(svnUrl, workCopyDir, SVNRevision.HEAD, true);
         } catch (SVNClientException e1) {
            logger.error("Can't checkout "+svnUrl+" to "+workCopyDir, e1);
         } catch (IOException e1) {
            logger.error("Can't clean dir " + workCopyDir, e1);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void contextDestroyed(ServletContextEvent arg0) {
      // do nothing
      
   }
}
