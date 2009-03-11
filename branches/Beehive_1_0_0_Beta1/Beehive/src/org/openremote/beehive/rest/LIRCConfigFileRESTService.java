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
     * Shows lirc config file according to vendor name and model name
     * Visits @ url  "/{vendor_name}/{model_name}"
     *
     * @param vendorName
     * @return content of lirc configuration file
     */
    @GET
    @Produces("text/plain")
    public String getLIRCConfigFile(@PathParam("vendor_name") String vendorName, @PathParam("model_name") String modelName) {
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
     * Retrieves instance of  ModelService   from spring IOC container
     *
     * @return ModelService instance
     */
    private ModelService getModelService() {
        return (ModelService) SpringContext.getInstance().getBean("modelService");
    }
}
