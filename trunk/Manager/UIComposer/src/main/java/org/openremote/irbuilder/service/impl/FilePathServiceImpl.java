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
import java.io.IOException;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.Properties;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@Repository
public class FilePathServiceImpl implements FilePathService {
   /**
    * {@inheritDoc}
    */
   public String tempFolder() {
      Properties properties = new Properties();
      try {
         File pFile = new File(getClass().getResource("/directoryConfig.properties").getFile());
         properties.load(new FileInputStream(pFile));
      } catch (IOException e) {
         e.printStackTrace();
         throw new  IllegalStateException("Can't read directoryConfig.properties file.",e);
      }
      if (properties.get("tmp.dir") != null) {
         return properties.get("tmp.dir").toString();
      } else {
         throw new IllegalStateException("Can't find tmp.dir in properties, system initialize erro.");
      }
   }

   /**
    * {@inheritDoc}
    */
   public String iPhoneXmlFilePath() {
      return tempFolder() + File.separator + "iphone.xml"+"_"+UUID.randomUUID();
   }

   /**
    * {@inheritDoc}
    */
   public String controllerXmlFilePath() {
      return tempFolder() + File.separator + "controller.xml"+"_"+UUID.randomUUID();
   }

    /**
    * {@inheritDoc}
    */
   public String panelDescFilePath() {
       return tempFolder() + File.separator + "panel.irb"+"_"+UUID.randomUUID();
   }

   /**
    * {@inheritDoc}
    */
   public String lircFilePath() {
      return tempFolder() + File.separator + "lirc.conf"+"_"+UUID.randomUUID();
   }

   /**
    * {@inheritDoc}
    */
   public String openremoteZipFilePath() {
      return tempFolder() + File.separator + "openremote."+UUID.randomUUID()+".zip";
   }
}
