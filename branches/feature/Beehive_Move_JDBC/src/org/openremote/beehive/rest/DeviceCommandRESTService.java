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

import java.util.ArrayList;
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
import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;
import org.openremote.beehive.api.service.DeviceCommandService;
import org.openremote.beehive.domain.modeler.DeviceCommand;

@Path("/devicecommand")
public class DeviceCommandRESTService extends RESTBaseService {

   @Path("load/{command_id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadById(@PathParam("command_id") long commandId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      DeviceCommandDTO deviceCommandDTO = getDeviceCommandService().loadDeviceCommandById(commandId);
      return buildResponse(deviceCommandDTO);
   }
   
   @Path("save")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response save(DeviceCommandDTO deviceCommandDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      DeviceCommandDTO newDeviceCommandDTO = getDeviceCommandService().save(deviceCommandDTO);
      return buildResponse(newDeviceCommandDTO);
   }
   
   @Path("delete/{command_id}")
   @DELETE
   public Response delete(@PathParam("command_id") long commandId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      return buildResponse(getDeviceCommandService().deleteCommandById(commandId));
   }
   
   @Path("update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response update(DeviceCommandDTO deviceCommandDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      DeviceCommand deviceCommand = getDeviceCommandService().update(deviceCommandDTO);
      return buildResponse(deviceCommand.toDTO());
   }
   
   @Path("saveall")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response saveAll(List<DeviceCommandDTO> deviceCommandDTOs,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceCommand> deviceCommands = getDeviceCommandService().saveAll(deviceCommandDTOs);
      List<DeviceCommandDTO> newDeviceCommandDTOs = new ArrayList<DeviceCommandDTO>();
      for (DeviceCommand deviceCommand : deviceCommands) {
         newDeviceCommandDTOs.add(deviceCommand.toDTO());
      }
      return buildResponse(newDeviceCommandDTOs);
   }
   
   @Path("loadbydevice/{device_id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadDeviceCommandsByDeviceId(@PathParam("device_id") long deviceId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceCommand> deviceCommands = getDeviceCommandService().loadCommandsByDeviceId(deviceId);
      List<DeviceCommandDTO> newDeviceCommandDTOs = new ArrayList<DeviceCommandDTO>();
      if (deviceCommands != null) {
         for (DeviceCommand deviceCommand : deviceCommands) {
            newDeviceCommandDTOs.add(deviceCommand.toDTO());
         }
      }
      return buildResponse(newDeviceCommandDTOs);
   }
   
   @Path("loadsamecommands")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadSameDeviceCommands(DeviceCommandDTO deviceCommandDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceCommand> deviceCommands = getDeviceCommandService().loadSameDeviceCommands(deviceCommandDTO);
      List<DeviceCommandDTO> newDeviceCommandDTOs = new ArrayList<DeviceCommandDTO>();
      for (DeviceCommand deviceCommand : deviceCommands) {
         newDeviceCommandDTOs.add(deviceCommand.toDTO());
      }
      return buildResponse(newDeviceCommandDTOs);
   }
   
   protected DeviceCommandService getDeviceCommandService() {
      return (DeviceCommandService) getSpringContextInstance().getBean("deviceCommandService");
   }
}
