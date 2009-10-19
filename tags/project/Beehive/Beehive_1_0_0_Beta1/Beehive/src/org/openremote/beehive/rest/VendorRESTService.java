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
 * Exports restful service of <code>Vendor</code>
 * User: allenwei
 * Date: 2009-2-9
 */
@Path("/lirc")
public class VendorRESTService {

    public VendorRESTService() {
    }


    /**
     * Shows all vendors
     * Visits @ url  "/vendors"
     *
     * @return VendorListing contains a list of Vendors
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public VendorListing getAllVendors() {
        List<VendorDTO> list=  getVendorService().loadAllVendors();
        if (list.size() >0 ) {
            return new VendorListing(getVendorService().loadAllVendors());
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * Retrieves instance of  VendorDAO from spring IOC
     *
     * @return VendorService instance
     */
    private VendorService getVendorService() {
        return (VendorService) SpringContext.getInstance().getBean("vendorService");

    }
}
