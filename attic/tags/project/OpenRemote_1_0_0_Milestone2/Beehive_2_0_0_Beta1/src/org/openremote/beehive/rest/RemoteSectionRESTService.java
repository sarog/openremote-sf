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
import org.openremote.beehive.domain.RemoteSection;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Exports restful service of <code>RemoteSection</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}/{model_name}")
public class RemoteSectionRESTService {

   public RemoteSectionRESTService() {
   }

   /**
    * Shows remoteSecrtions by {vendor_name} and {model_name} Visits @ url "/lirc/{vendor_name}/{model_name}"
    * 
    * @param vendorName
    * @param modelName
    * @return RemoteSectionListing contain a list of RemoteSections
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public RemoteSectionListing getRemoteSections(@PathParam("vendor_name") String vendorName,
         @PathParam("model_name") String modelName) {
      ModelDTO model = getModelService().loadByVendorNameAndModelName(vendorName, modelName);
      if (model == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      return new RemoteSectionListing(getRemoteSectionService().findByModelId(model.getOid()));
   }

   /**
    * Retrieves instance of ModelService from spring IOC
    * 
    * @return ModelService instance
    */
   public ModelService getModelService() {
      return (ModelService) SpringContext.getInstance().getBean("modelService");
   }

   /**
    * Retrieves instance of RemoteSectionService from spring IOC
    * 
    * @return RemoteSectionService instance
    */
   public RemoteSectionService getRemoteSectionService() {
      return (RemoteSectionService) SpringContext.getInstance().getBean("remoteSectionService");
   }
}