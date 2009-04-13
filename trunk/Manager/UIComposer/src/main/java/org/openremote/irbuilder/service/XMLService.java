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

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public interface XMLService {
   /**
    * Generates Controller part XML
    * 
    * @param data
    *           JSON String
    * @return Generated XML String
    */
   String controllerXmlBuilder(String data);

   /**
    * Generates iPhone part XML
    * 
    * @param data
    *           JSON String
    * @return Generated XML String
    */
   String iPhoneXmlBuilder(String data);

   /**
    * Reads lirc.conf from beehive
    * @param url lirc.conf REST API url
    * @return a String contain combined lirc.conf file 
    */
   String readLircFile(String url);

   /**
    * Converts uploaded xml file to JSON which will pass to UIInterface.
    * the JSON will looks like:
    * <pre>
    * {"ui":{"controller":{"buttons":{"button":[{"id":6,"event":1},{"id":7,"event":4}]},"events":{"irEvents":{"irEvent":[{"id":1,"name":"AccessMedia_ThinBox","command":8,"sectionId":0},{"id":3,"name":"AccessMedia_ThinBox","command":"mute","sectionId":0},{"id":4,"name":"AVC-2410","command":"Rewind","sectionId":0},{"id":5,"name":"AVC-2410","command":4,"sectionId":0}]}}},"iphone":{"activities":{"activity":[{"id":1,"name":"activity1","screen":{"id":1,"name":"screen1","button":[{"id":6,"label":8,"x":2,"y":1,"width":1,"height":1},{"id":7,"label":"Rewind","x":2,"y":3,"width":1,"height":1}],"row":6,"col":4}}]}}}}
    * </pre>
    * @param iphoneXmlFileInputStream iphone part xml InputStream
    * @param controllerXmlInputStream controller part xml InputStream
    * @return JSON String
    */
   public String xmlImportedToJSON(InputStream iphoneXmlFileInputStream,InputStream controllerXmlInputStream) ;
}
