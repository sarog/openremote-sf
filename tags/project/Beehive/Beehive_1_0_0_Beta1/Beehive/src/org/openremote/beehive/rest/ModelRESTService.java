package org.openremote.beehive.rest;

import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.spring.SpringContext;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Exports restful service of <code>Model</code>
 * User: allenwei
 * Date: 2009-2-10
 */
@Path("/lirc/{vendor_name}")
public class ModelRESTService {

    public ModelRESTService() {
    }

    /**
     * Shows all models belongs to the vendor which name is {vendor_name}
     * Visits @ url  "/vendors/{vendor_name}"
     *
     * @param vendorName
     * @return ModelListing contain a list of Models
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public ModelListing getModels(@PathParam("vendor_name") String vendorName) {
        List<ModelDTO> list = getModelService().findModelsByVendorName(vendorName);
        if (list == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (list.size() == 0) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        return new ModelListing(getModelService().findModelsByVendorName(vendorName));
    }


    /**
     * Retrieves instance of  ModelService   from spring IOC
     *
     * @return ModelService instance
     */
    public ModelService getModelService() {
        return (ModelService) SpringContext.getInstance().getBean("modelService");
    }
}
