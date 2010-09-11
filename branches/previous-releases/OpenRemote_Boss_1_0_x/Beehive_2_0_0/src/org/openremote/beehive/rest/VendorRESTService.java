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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.openremote.beehive.api.service.VendorService;
import org.openremote.beehive.api.dto.VendorDTO;
import org.openremote.beehive.spring.SpringContext;

import java.util.List;

/**
 * Exports restful service of <code>Vendor</code> User: allenwei Date: 2009-2-9
 */
@Path("/lirc")
public class VendorRESTService {

   public VendorRESTService() {
   }

   /**
    * Shows all vendors Visits @ url "/lirc"
    * 
    * @return VendorListing contains a list of Vendors
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public VendorListing getAllVendors() {
      List<VendorDTO> list = getVendorService().loadAllVendors();
      if (list.size() > 0) {
         return new VendorListing(getVendorService().loadAllVendors());
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   /**
    * Retrieves instance of VendorDAO from spring IOC
    * 
    * @return VendorService instance
    */
   private VendorService getVendorService() {
      return (VendorService) SpringContext.getInstance().getBean("vendorService");

   }
}
