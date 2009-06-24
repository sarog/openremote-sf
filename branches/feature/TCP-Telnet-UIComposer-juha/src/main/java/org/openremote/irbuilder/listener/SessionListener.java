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

package org.openremote.irbuilder.listener;

import org.openremote.irbuilder.configuration.PathConfig;
import org.openremote.irbuilder.exception.FileOperationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class SessionListener implements HttpSessionListener {
   private static final Logger logger = Logger.getLogger(SessionListener.class);
   public void sessionCreated(HttpSessionEvent httpSessionEvent) {
      httpSessionEvent.getSession().setMaxInactiveInterval(28800000);
   }

   public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
      File file = new File(PathConfig.getInstance().sessionFolder(httpSessionEvent.getSession().getId()));
      try {
         FileUtils.deleteDirectory(file);
      } catch (IOException e) {
         logger.error("Delete temp folder occur IOException", e);
         throw new FileOperationException("Create temp folder occur IOException", e);
      }
   }
}
