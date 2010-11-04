/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openremote.controller.exception.ControlCommandException;
import org.openremote.controller.service.ImageContentChangeService;
import org.openremote.controller.spring.SpringContext;

/**
 * @author tomsky.wang 2010-11-3
 *
 */
@SuppressWarnings("serial")
public class ImageChangeRESTServlet extends HttpServlet {

   private ImageContentChangeService imageContentChangeService = 
      (ImageContentChangeService) SpringContext.getInstance().getBean("imageContentChangeService");
   
   public ImageChangeRESTServlet() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doPost(request, response);
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.setCharacterEncoding("utf8");
      String url = request.getRequestURL().toString();
      String regexp = "rest\\/imagechanges\\/(.*?)\\/(.*)";
      Pattern pattern = Pattern.compile(regexp);
      Matcher matcher = pattern.matcher(url);
      
      String panelName = null;
      String deviceID = null;
      
      if (matcher.find()) {
         panelName = matcher.group(1);
         deviceID = matcher.group(2);
         
         if (panelName == null || "".equals(panelName)) {
            throw new NullPointerException("panel name was null");
         }
         
         if (deviceID == null || "".equals(deviceID)) {
            throw new NullPointerException("Device id was null");
         }
         
         PrintWriter writer = response.getWriter();
         String changedImages = imageContentChangeService.getChangedPanelConsoleImages(panelName, deviceID);
         if (!"".equals(changedImages)) {
            writer.write(changedImages);
         } else {
            response.sendError(ControlCommandException.NO_IMAGE_CHANGE, "Image content not changed.");
         }
      } else {
         response.sendError(ControlCommandException.INVALID_IMAGE_CHANGE_URL, "Invalid image changes url:" + url);
      }
   }
   
   
}
