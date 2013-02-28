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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.util.GenericType;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.TemplateDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.api.service.TemplateService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;
/**
 * Panel UI Template restful service.
 * 
 * UI designer can save some common screens of a panel as a template. 
 * Other UI designers can use this template to reuse UI.
 * 
 * @author Dan Cong
 *
 */

@Path("/account/{account_id}")
public class TemplateRESTService extends RESTBaseService {
   
   /**
    * Get all templates by account id.
    * 
    * @param accountId
    *           account id
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * 
    * @return template list
    */
   @Path("templates")
   @GET
   @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public TemplateListing getTemplates(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
      List<TemplateDTO> list = null;
      if (accountId == Template.PUBLIC_ACCOUNT_OID) {
         list = getTemplateService().loadAllPublicTemplate();
      } else {
         list = getTemplateService().loadAllTemplatesByAccountOid(accountId);
      }
      if (list != null) {
         return new TemplateListing(list);
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   /**
    * Get template resources : template.zip (images included).
    * 
    * @param templateId
    *           template id
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return template.zip
    */
   @GET
   @Produces( { "application/zip"})
   @Path("template/{template_id}/resource")
   public File getTemplateResources(@PathParam("template_id") long templateId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
      return getTemplateService().getTemplateResourceZip(templateId);
   }
   
   /**
    * Get template UI component info.
    * 
    * @param templateId
    *           template id
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return template UI component info
    */
   @Path("template/{template_id}")
   @GET
   @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public TemplateDTO getTemplateById(@PathParam("template_id") long templateId, 
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
      TemplateDTO t = getTemplateService().loadTemplateByOid(templateId);
      if (t != null) {
         return t;
      }
      throw new WebApplicationException(Response.Status.NOT_FOUND);
   }

   /**
    * Save a template.
    * 
    * @param accountId
    *           account id.
    * @param name
    *           template name
    * @param content
    *           template content (XML or JSON string)
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return new template
    */
   @Path("template")
   @POST
   @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public TemplateDTO addTemplateIntoAccount(@PathParam("account_id") long accountId, @FormParam("name") String name,
         @FormParam("content") String content, @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
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
   
   /**
    * Deletes a template.
    * 
    * This requires web server to support HTTP DELETE method.
    * 
    * @param templateId
    *           template id
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return ture if success.
    */
   
   @Path("template/{template_id}")
   @DELETE
   public boolean deleteTemplate(@PathParam("template_id") long templateId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
      if (templateId > 0) {
         return getTemplateService().delete(templateId);
      }
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
   }

   /**
    * Save related resources(images, icons) into the template.
    * 
    * @param accountId
    *           account id.
    * @param templateId
    *           template id.
    * @param input
    *           Multipart Form Data Input
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return true if success.
    */
   @Path("template/{template_id}/resource")
   @POST
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public boolean saveTemplateResource(@PathParam("account_id") long accountId,
         @PathParam("template_id") long templateId, MultipartFormDataInput input, 
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
      List<InputPart> parts = input.getParts();
      InputStream in = null;
      try {
         if (parts.size() > 0) {
            in = parts.get(0).getBody(new GenericType<InputStream>() {});
            return getTemplateService().saveTemplateResourceZip(templateId, in);
         } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
   }
   
   /**
    * Save account resources (openremote.zip).
    * 
    * @param accountId
    *           account id.
    * @param input
    *           Multipart Form Data Input
    * @param credentials
    *           HTTP basic header credentials : "Basic base64(username:md5(password,username))"
    * @return true if success.
    */
   @Path(Constant.ACCOUNT_RESOURCE_ZIP_NAME)
   @POST
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public boolean saveResource(@PathParam("account_id") long accountId, 
         MultipartFormDataInput input,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      
      authorize(credentials);
      List<InputPart> parts = input.getParts();
      InputStream in = null;
      try {
         if (parts.size() > 0) {
            in = parts.get(0).getBody(new GenericType<InputStream>() {});
            return getResourceService().saveResource(accountId, in);
         } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
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
   
   
   protected ResourceService getResourceService() {
      return (ResourceService) getSpringContextInstance().getBean("resourceService");
   }
   
   protected TemplateService getTemplateService() {
      return (TemplateService) getSpringContextInstance().getBean("templateService");
   }
   
   protected AccountService getAccountService() {
      return (AccountService) getSpringContextInstance().getBean("accountService");
   }
   
   
}
