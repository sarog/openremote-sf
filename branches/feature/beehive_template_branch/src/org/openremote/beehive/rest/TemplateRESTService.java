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

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;
import org.openremote.beehive.spring.SpringContext;
/**
 * UI Template restful service.
 * 
 * @author Dan Cong
 *
 */

@Path("/account/{account_id}")
public class TemplateRESTService {
   
   protected TemplateService getTemplateService() {
      return (TemplateService) SpringContext.getInstance().getBean("templateService");
   }
   
   protected AccountService getAccountService() {
      return (AccountService) SpringContext.getInstance().getBean("accountService");
   }
   

   @GET
   @Produces( { "application/xml", "application/json" })
   @Path("templates")
   public TemplateListing getTemplates(@PathParam("account_id") long accountId) {
      List<TemplateDTO> list = null;
      if (accountId == Template.PUBLIC_ACCOUNT_OID) {
         list = getTemplateService().loadAllPublicTemplate();
      } else {
         list = getTemplateService().loadAllTemplatesByAccountOid(accountId);
      }
      if (list.size() > 0) {
         return new TemplateListing(list);
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   @GET
   @Produces( { "application/xml", "application/json" })
   @Path("template/{template_id}")
   public TemplateDTO getTemplateById(@PathParam("template_id") long templateId) {
      TemplateDTO t = getTemplateService().loadTemplateByOid(templateId);
      if (t != null) {
         return t;
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   @POST
   @Produces( { "application/xml", "application/json" })
   @Path("template")
   public TemplateDTO addTemplateIntoAccount(@PathParam("account_id") long accountId, @FormParam("name") String name,
         @FormParam("content") String content, @HeaderParam(Constant.HTTP_BASIC_AUTH_HEADER_NAME) String credentials) {

      if (!getAccountService().isHTTPBasicAuthorized(credentials)) {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }
      Template t = new Template();
      if (accountId > 0) {
         Account a = new Account();
         a.setOid(accountId);
         t.setAccount(a);
      } 
      t.setName(name);
      t.setContent(content);
      long newId = getTemplateService().save(t);
      TemplateDTO newTemp = getTemplateService().loadTemplateByOid(newId);
      if (newTemp != null) {
         return newTemp;
      }
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

   }
   
   @DELETE
   @Path("template/{template_id}")
   public void deleteTemplate(@PathParam("template_id") long templateId,
         @HeaderParam(Constant.HTTP_BASIC_AUTH_HEADER_NAME) String credentials) {
      
      if (!getAccountService().isHTTPBasicAuthorized(credentials)) {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }
      if (templateId > 0) {
         getTemplateService().delete(templateId);
         return;
      }
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
   }

   
}
