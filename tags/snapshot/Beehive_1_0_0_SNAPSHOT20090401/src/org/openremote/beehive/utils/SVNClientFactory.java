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
package org.openremote.beehive.utils;

import org.apache.log4j.Logger;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;

/**
 * @author Tomsky
 * 
 */
public class SVNClientFactory {
   private static Logger logger = Logger.getLogger(SVNClientFactory.class.getName());

   private static ISVNClientAdapter svnClient;

   public synchronized static ISVNClientAdapter getSVNClient() {
      if (svnClient == null) {
         try {
            CmdLineClientAdapterFactory.setup();
         } catch (SVNClientException e1) {
            logger.error("Can't register the cmdline adapter!");
         }
         try {
            String bestClientType = SVNClientAdapterFactory.getPreferredSVNClientType();
            svnClient = SVNClientAdapterFactory.createSVNClient(bestClientType);
         } catch (SVNClientException e) {
            logger.error("Can't create svnclient!");
         }
      }
      return svnClient;
   }
}
