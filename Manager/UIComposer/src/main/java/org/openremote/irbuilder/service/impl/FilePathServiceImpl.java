/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.openremote.irbuilder.service.impl;

import org.openremote.irbuilder.service.FilePathService;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@Repository
public class FilePathServiceImpl implements FilePathService {
   /**
    * {@inheritDoc}
    */
   public String tempFolder(HttpServletRequest req) {
      return req.getRealPath("/") + File.separator + "tmp";
   }

   /**
    * {@inheritDoc}
    */
   public String iPhoneXmlFilePath(HttpServletRequest req) {
      return tempFolder(req) + File.separator + "iphone.xml";
   }

   /**
    * {@inheritDoc}
    */
   public String controllerXmlFilePath(HttpServletRequest req) {
      return tempFolder(req) + File.separator + "controller.xml";
   }

   /**
    * {@inheritDoc}
    */
   public String lircFilePath(HttpServletRequest req) {
      return tempFolder(req) + File.separator + "lirc.conf";
   }

   /**
    * {@inheritDoc}
    */
   public String openremoteZipFilePath(HttpServletRequest req) {
      return tempFolder(req) + File.separator + "openremote.zip";
   }
}
