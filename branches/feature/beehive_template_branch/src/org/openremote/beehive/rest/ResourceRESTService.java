/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
