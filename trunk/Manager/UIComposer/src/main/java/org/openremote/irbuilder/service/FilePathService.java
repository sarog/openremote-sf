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

package org.openremote.irbuilder.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public interface FilePathService {
   /**
    * Gets temp folder
    * 
    * @param req
    *           HttpServletRequest
    * @return folder absolute path
    */
   String tempFolder(HttpServletRequest req);

   /**
    * Gets iphone xml path
    * 
    * @param req
    *           HttpServletRequest
    * @return file absolute path
    */
   String iPhoneXmlFilePath(HttpServletRequest req);

   /**
    * Gets controller xml file path
    * 
    * @param req
    *           HttpServletRequest
    * @return file absolute path
    */
   String controllerXmlFilePath(HttpServletRequest req);

   /**
    * Gets lirc.conf file path
    * 
    * @param req
    *           HttpServletRequest
    * @return file absolute path
    */
   String lircFilePath(HttpServletRequest req);

   /**
    * Gets compressed file path
    * 
    * @param req
    *           HttpServletRequest
    * @return file absolute path
    */
   String openremoteZipFilePath(HttpServletRequest req);

}
