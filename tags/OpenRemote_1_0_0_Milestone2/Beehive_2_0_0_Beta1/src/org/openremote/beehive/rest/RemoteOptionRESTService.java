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
import org.openremote.beehive.api.dto.RemoteOptionDTO;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.RemoteSectionService;
import org.openremote.beehive.api.service.RemoteOptionService;
import org.openremote.beehive.spring.SpringContext;
import org.openremote.beehive.domain.RemoteSection;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

/**
 * Exports restful service of <code>RemoteOption</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}/{model_name}/{section_id}/options")
public class RemoteOptionRESTService {

   public RemoteOptionRESTService() {
   }

   /**
    * Shows remoteSecrtions by {section_id} Visits @ url "/lirc/{vendor_name}/{model_name}/{section_id}/options"
    * 
    * @param vendorName
    * @param modelName
    * @param sectionId
    * @return RemoteOptionListing contain a list of RemoteOptions
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public RemoteOptionListing getRemoteOptions(@PathParam("vendor_name") String vendorName,
         @PathParam("model_name") String modelName, @PathParam("section_id") long sectionId) {
      List<RemoteOptionDTO> list = getRemoteOptionService().findByRemoteSectionId(sectionId);
      if (list.size() == 0) {
         throw new WebApplicationException(Response.Status.NO_CONTENT);
      }
      return new RemoteOptionListing(getRemoteOptionService().findByRemoteSectionId(sectionId));
   }

   /**
    * Retrieves instance of RemoteSectionService from spring IOC
    * 
    * @return getRemoteOptionService instance
    */
   public RemoteOptionService getRemoteOptionService() {
      return (RemoteOptionService) SpringContext.getInstance().getBean("remoteOptionService");
   }
}