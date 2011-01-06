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
import org.openremote.beehive.api.dto.modeler.SwitchDTO;
import org.openremote.beehive.api.service.SwitchService;

/**
 * Exports restful service of <code>Switch</code>
 */
@Path("/switch")
public class SwitchRESTService extends RESTBaseService {

   @Path("save/{account_id}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response save(@PathParam("account_id") long accountId, SwitchDTO switchDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SwitchDTO newDTO = getSwitchService().save(switchDTO, accountId);
      newDTO.getSwitchCommandOnRef().getDeviceCommand().setDevice(switchDTO.getDevice());
      newDTO.getSwitchCommandOffRef().getDeviceCommand().setDevice(switchDTO.getDevice());
      newDTO.getSwitchSensorRef().getSensor().setDevice(switchDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   @Path("delete/{switch_id}")
   @DELETE
   public Response delete(@PathParam("switch_id") long switchId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      getSwitchService().deleteSwitchById(switchId);
      return buildResponse(true);
   }
   
   @Path("update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response update(SwitchDTO switchDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SwitchDTO newDTO = getSwitchService().updateSwitch(switchDTO).toDTO();
      newDTO.getSwitchCommandOnRef().getDeviceCommand().setDevice(switchDTO.getDevice());
      newDTO.getSwitchCommandOffRef().getDeviceCommand().setDevice(switchDTO.getDevice());
      newDTO.getSwitchSensorRef().getSensor().setDevice(switchDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   @Path("loadall/{account_id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadAccountSwitchs(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SwitchDTO> switchs = getSwitchService().loadAccountSwitchs(accountId);
      if (switchs.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(switchs);
   }
   
   @Path("loadsameswitchs")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadSameSwitchs(SwitchDTO switchDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SwitchDTO> switchs = getSwitchService().loadSameSwitchs(switchDTO);
      if (switchs.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(switchs);
   }
   protected SwitchService getSwitchService() {
      return (SwitchService) getSpringContextInstance().getBean("switchService");
   }
}
