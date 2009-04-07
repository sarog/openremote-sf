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
package org.openremote.irbuilder.controller;

import org.openremote.irbuilder.service.XMLService;
import org.openremote.irbuilder.service.ZipService;
import org.openremote.irbuilder.service.FilePathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author allen.wei
 */
@Controller
public class ScreenController {

   @Autowired(required = true)
   private XMLService xmlService;
   @Autowired
   private ZipService zipService;
   @Autowired
   private FilePathService filePathService;

   @RequestMapping(value = "/download.htm", method = RequestMethod.POST)
   public void download(String iphone, String controller,String restUrl,String ids, HttpServletRequest req, HttpServletResponse resp)
         throws IOException {
      File iphoneXMLFile = zipService.writeStringToFile(filePathService.iPhoneXmlFilePath(req), xmlService
            .iPhoneXmlBuilder(iphone));
      File controllerXMLFile = zipService.writeStringToFile(filePathService.controllerXmlFilePath(req), xmlService
            .controllerXmlBuilder(controller));
      File lircFile = zipService.writeStringToFile(filePathService.lircFilePath(req), xmlService.readLircFile(restUrl+"?ids="+ids));
      zipService.compress(filePathService.openremoteZipFilePath(req), iphoneXMLFile, controllerXMLFile, lircFile);
      resp.getOutputStream().print("tmp/openremote.zip");
   }
}
