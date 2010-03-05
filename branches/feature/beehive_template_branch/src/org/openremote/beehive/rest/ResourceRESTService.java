package org.openremote.beehive.rest;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.spring.SpringContext;

/**
 * Resource restful service.
 * 
 * @author Dan Cong
 *
 */

@Path("/user/{username}")
public class ResourceRESTService {
   
   @Path("openremote.zip")
   @GET
   @Produces( { "application/zip" })
   public File getControllerResources(@PathParam("username") String username, 
         @HeaderParam(Constant.HTTP_BASIC_AUTH_HEADER_NAME) String credentials) {
      authorize(credentials);
      File file = getResourceService().getResourceZip(username);
      if (file != null) {
         return file;
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }
   
   private void authorize(String credentials) {
      if (!getAccountService().isHTTPBasicAuthorized(credentials)) {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }
   }
   
   protected ResourceService getResourceService() {
      return (ResourceService) SpringContext.getInstance().getBean("resourceService");
   }
   
   protected AccountService getAccountService() {
      return (AccountService) SpringContext.getInstance().getBean("accountService");
   }

}
