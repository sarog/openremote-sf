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

import org.apache.commons.lang.StringUtils;
import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.spring.SpringContext;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Exports restful service of LIRC config file export
 * 
 * @author allen.wei 2009-2-15
 */
@Path("/lirc/{vendor_name}/{model_name}")
public class LIRCConfigFileRESTService {

   /**
    * Shows lirc config file according to vendor name and model name Visits @ url "/{vendor_name}/{model_name}"
    * 
    * @param vendorName
    * @return content of lirc configuration file
    */
   @GET
   @Produces("text/plain")
   public String getLIRCConfigFile(@PathParam("vendor_name") String vendorName,
         @PathParam("model_name") String modelName) {
      ModelDTO modelDTO = getModelService().loadByVendorNameAndModelName(vendorName, modelName);
      if (modelDTO == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      if (StringUtils.isBlank(getModelService().exportText(modelDTO.getOid()))) {
         throw new WebApplicationException(Response.Status.NO_CONTENT);
      }
      return getModelService().exportText(modelDTO.getOid());

   }

   /**
    * Retrieves instance of ModelService from spring IOC container
    * 
    * @return ModelService instance
    */
   private ModelService getModelService() {
      return (ModelService) SpringContext.getInstance().getBean("modelService");
   }
}
