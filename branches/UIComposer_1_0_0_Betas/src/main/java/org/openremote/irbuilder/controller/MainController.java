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

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.irbuilder.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author allen.wei
 */
@Controller
public class MainController {

   @Autowired
   private ResourceService resourceService;

   /**
    * Compress received data to zip and finally print a relative path to frontend.
    * 
    * @param iphone
    *           iphone part json
    * @param controller
    *           controller part json
    * @param panel
    *           UI interface description
    * @param restUrl
    *           rest API url
    * @param ids
    *           section ids, server need to get combined lircd.conf file from beehive
    * @param req
    *           HttpServletRequest
    * @param resp
    *           HttpServletResponse
    * @throws IOException
    */
   @RequestMapping(value = "/download.htm", method = RequestMethod.POST)
   public void download(String iphone, String controller, String panel, String restUrl, String ids,
         HttpServletRequest req, HttpServletResponse resp) throws IOException {
      String sessionId = req.getSession().getId();
      String fileName = resourceService.downloadZipResource(controller, iphone, panel, restUrl, ids,req.getSession().getId()).getName();
      resp.getOutputStream().print("tmp/" + sessionId + File.separator + fileName);
   }

   /**
    * User upload zip file, and server unzip the file then return the ui interface description file content as String
    * 
    * @param request
    * @param resp
    * @throws IOException
    */
   @RequestMapping(value = "/import.htm", method = RequestMethod.POST)
   public void importZip(HttpServletRequest request, HttpServletResponse resp) throws IOException {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      String result = resourceService.getIrbFileFromZip(multipartRequest.getFile("zip_file").getInputStream());
      resp.getWriter().print(result);
   }

}
