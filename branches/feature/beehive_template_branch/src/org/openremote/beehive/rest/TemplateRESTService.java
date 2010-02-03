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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.spring.SpringContext;


@Path("/account/{account_id}")
public class TemplateRESTService {

   @GET
   @Produces( { "application/xml", "application/json" })
   @Path("templates")
   public TemplateListing getTemplates(@PathParam("account_id") long accountId) {
      List<TemplateDTO> list = getTemplateService().loadAllTemplatesByAccountOid(accountId);
      if (list.size() > 0) {
         return new TemplateListing(list);
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }
   
   @GET
   @Produces( { "application/xml", "application/json" })
   @Path("template/{template_id}")
   public TemplateListing getTemplateById(@PathParam("template_id") long templateId) {
      List<TemplateDTO> list = getTemplateService().loadTemplateByOid(templateId);
      if (list.size() > 0) {
         return new TemplateListing(list);
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   protected TemplateService getTemplateService() {
      return (TemplateService) SpringContext.getInstance().getBean("templateService");
   }

}
