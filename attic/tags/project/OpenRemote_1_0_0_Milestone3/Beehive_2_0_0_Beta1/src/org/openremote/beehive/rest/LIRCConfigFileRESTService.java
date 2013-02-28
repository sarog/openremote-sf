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
package org.openremote.beehive.rest;

import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.RemoteSectionService;
import org.openremote.beehive.spring.SpringContext;
import org.openremote.beehive.utils.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Exports restful service of LIRC config file export
 * 
 * @author allen.wei 2009-2-15
 */
@Path("/lirc.conf")
public class LIRCConfigFileRESTService {

   /**
    * Shows lirc config file according to vendor name and model name Visits @ url "/{vendor_name}/{model_name}"
    * 
    * @param sectionIds
    * @return content of lirc configuration file
    */
   @GET
   @Produces("text/plain")
   public String getLIRCConfigFile(@QueryParam("ids") String sectionIds) {
      ArrayList<Long> ids = StringUtil.parseStringIds(sectionIds,",");
      if (ids.size() == 0) {
         return "";
      }
      StringBuffer lircStr = new StringBuffer();
      for (long id : ids) {
         lircStr.append(getRemoteSectionService().exportText(id));
         lircStr.append(System.getProperty("line.separator"));
      }
      return lircStr.toString();

   }

   /**
    * Retrieves instance of RemoteSectionService from spring IOC container
    * 
    * @return RemoteSectionService instance
    */
   private RemoteSectionService getRemoteSectionService() {
      return (RemoteSectionService) SpringContext.getInstance().getBean("remoteSectionService");
   }
}
