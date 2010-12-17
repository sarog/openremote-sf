/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.modeler.SliderDTO;
import org.openremote.beehive.api.service.SliderService;

/**
 * Exports restful service of <code>Slider</code>
 */
@Path("/slider")
public class SliderRESTService extends RESTBaseService {

   @Path("save/{account_id}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response save(@PathParam("account_id") long accountId, SliderDTO sliderDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SliderDTO newDTO = getSliderService().save(sliderDTO, accountId);
      newDTO.getSetValueCmd().getDeviceCommand().setDevice(sliderDTO.getDevice());
      newDTO.getSliderSensorRef().getSensor().setDevice(sliderDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   @Path("delete/{slider_id}")
   @DELETE
   public Response delete(@PathParam("slider_id") long sliderId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      getSliderService().deleteById(sliderId);
      return buildResponse(true);
   }
   
   @Path("update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response update(SliderDTO sliderDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SliderDTO newDTO = getSliderService().update(sliderDTO).toDTO();
      newDTO.getSetValueCmd().getDeviceCommand().setDevice(sliderDTO.getDevice());
      newDTO.getSliderSensorRef().getSensor().setDevice(sliderDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   @Path("loadall/{account_id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadAccountSwitchs(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SliderDTO> sliders = getSliderService().loadAccountSliders(accountId);
      if (sliders.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(sliders);
   }
   
   protected SliderService getSliderService() {
      return (SliderService) getSpringContextInstance().getBean("sliderService");
   }
}
