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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.SVNDelegateService;
import org.openremote.beehive.api.service.WebscraperService;
import org.openremote.beehive.domain.Vendor;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;

/**
 * @author Tomsky
 * 
 */
public class WebscraperServiceImpl extends BaseAbstractService<Vendor> implements WebscraperService {

   private static Logger logger = Logger.getLogger(WebscraperServiceImpl.class.getName());
   private Configuration configuration;
   private SVNDelegateService svnDelegateService;

   public SVNDelegateService getSvnDelegateService() {
      return svnDelegateService;
   }

   public void setSvnDelegateService(SVNDelegateService svnDelegateService) {
      this.svnDelegateService = svnDelegateService;
   }

   public Configuration getConfiguration() {
      return configuration;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void scraperFiles() {
      try {
         File scrapDir = new File(configuration.getScrapDir());
         if(scrapDir.exists()){
            FileUtils.deleteDirectory(scrapDir);            
         }
         ScraperConfiguration config = new ScraperConfiguration(getClass().getResource("/remotes.xml").getPath());         
         Scraper scraper = new Scraper(config, configuration.getScrapDir());
         scraper.setDebug(true);
         // long startTime = System.currentTimeMillis();
         logger.info("Scraper LIRC files from http://lirc.sourceforge.net/remotes!");
         scraper.execute();
         logger.info("Success scrap LIRC files from web!");
         // System.out.println("time elapsed:"+ (System.currentTimeMillis() - startTime));
         logger.info("Copy LIRC files from " + configuration.getScrapDir() + " to workCopy "
               + configuration.getWorkCopyDir());
         svnDelegateService.copyFromScrapToWC(configuration.getScrapDir(), configuration.getWorkCopyDir());
         logger.info("Success copy LIRC files to workCopy!");
         logger.info("Success delete files of " + configuration.getScrapDir());
      } catch (FileNotFoundException e) {
         logger.error("The file of remotes.xml not found!",e);
      }catch (IOException e) {
         logger.error("Cae't delete the directory of "+configuration.getScrapDir(),e);
       }
   }
}
