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
import org.openremote.beehive.spring.SpringContext;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Exports restful service of <code>Model</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}")
public class ModelRESTService {

   public ModelRESTService() {
   }

   /**
    * Shows all models belongs to the vendor which name is {vendor_name} Visits @ url "/lirc/{vendor_name}"
    * 
    * @param vendorName
    * @return ModelListing contain a list of Models
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public ModelListing getModels(@PathParam("vendor_name") String vendorName) {
      List<ModelDTO> list = getModelService().findModelsByVendorName(vendorName);
      if (list == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      if (list.size() == 0) {
         throw new WebApplicationException(Response.Status.NO_CONTENT);
      }
      return new ModelListing(list);
   }

   /**
    * Retrieves instance of ModelService from spring IOC
    * 
    * @return ModelService instance
    */
   public ModelService getModelService() {
      return (ModelService) SpringContext.getInstance().getBean("modelService");
   }
}
