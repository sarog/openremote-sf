/**
 * 
 */
package org.openremote.beehive.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.openremote.beehive.api.dto.IconDTO;
import org.openremote.beehive.api.service.IconService;
import org.openremote.beehive.spring.SpringContext;

/**
 * Exports restful service of <code>Icon</code>
 * 
 * @author Tomsky 2009-4-21
 *
 */

@Path("/icons")
public class IconRESTService {
   
   /**
    * Shows icons identified by iconName Visits @ url "/icons/{icon_name}"
    * 
    * @param iconName
    * @return IconListing contain a list of Icons
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   @Path("{icon_name}")
   public IconListing getIcons(@PathParam("icon_name") String iconName){
      List<IconDTO> list = getIconService().findIconsByName(iconName);
      if(list == null){
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      if(list.size() == 0){
         throw new WebApplicationException(Response.Status.NO_CONTENT);
      }
      return new IconListing(list);
   }
   
   /**
    * Shows all icons Visits @ url "/icons"
    * 
    * @return IconListing contain a list of Icons
    */
   @GET
   @Produces( { "application/xml", "application/json" })
   public IconListing getAllIcons(){
      List<IconDTO> list = getIconService().loadAllIcons();
      if(list == null){
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      if(list.size() == 0){
         throw new WebApplicationException(Response.Status.NO_CONTENT);
      }
      return new IconListing(list);
   }
   
   public IconService getIconService(){
      return (IconService) SpringContext.getInstance().getBean("iconService");
   }
}
