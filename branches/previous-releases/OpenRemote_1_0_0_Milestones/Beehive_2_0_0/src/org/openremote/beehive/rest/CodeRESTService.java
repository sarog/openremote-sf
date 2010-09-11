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

import org.openremote.beehive.api.dto.CodeDTO;
import org.openremote.beehive.api.service.CodeService;
import org.openremote.beehive.spring.SpringContext;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Exports restful service of <code>Code</code> User: allenwei Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}/{model_name}/{section_id}/codes")
public class CodeRESTService {

   public CodeRESTService() {
   }

   /**
    * Shows codes by {section_id} Visits @ url "/lirc/{vendor_name}/{model_name}/{section_id}/codes"
    * 
    * @param vendorName
    * @param modelName
    * @param sectionId
    * @return RemoteOptionListing contain a list of Codes
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public CodeListing getRemoteOptions(@PathParam("vendor_name") String vendorName,
         @PathParam("model_name") String modelName, @PathParam("section_id") long sectionId) {
      List<CodeDTO> list = getCodeService().findByRemoteSectionId(sectionId);
      if (list.size() == 0) {
         throw new WebApplicationException(Response.Status.NO_CONTENT);
      }
      return new CodeListing(getCodeService().findByRemoteSectionId(sectionId));
   }

   /**
    * Retrieves instance of CodeService from spring IOC
    * 
    * @return CodeService instance
    */
   public CodeService getCodeService() {
      return (CodeService) SpringContext.getInstance().getBean("codeService");
   }
}