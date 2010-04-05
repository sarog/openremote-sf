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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.TemplateService;

@Path("/templates")
public class PublicTemplatesRestService extends RESTBaseService {
   
   @Path("keywords/{keywords}/page/{page}")
   @GET
   @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public TemplateListing getTemplates(@PathParam("keywords") String keywords,
         @PathParam("page")int page,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      authorize(credentials);
      String newKeywords = keywords;
      if (keywords.equals(TemplateService.NO_KEYWORDS)) {
         newKeywords = "";
      }
      List<TemplateDTO> list = getTemplateService().loadPublicTemplatesByKeywordsAndPage(newKeywords, page);
      if (list !=null) {
         return new TemplateListing(list);
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   
   /*
    * If the user was not validated, fail with a
    * 401 status code (UNAUTHORIZED) and
    * pass back a WWW-Authenticate header for
    * this servlet.
    *  
    */
   private void authorize(String credentials) {
      if (!getAccountService().isHTTPBasicAuthorized(credentials)) {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }
   }
   
   protected TemplateService getTemplateService() {
      return (TemplateService) getSpringContextInstance().getBean("templateService");
   }
   
   protected AccountService getAccountService() {
      return (AccountService) getSpringContextInstance().getBean("accountService");
   }
   
   
}
