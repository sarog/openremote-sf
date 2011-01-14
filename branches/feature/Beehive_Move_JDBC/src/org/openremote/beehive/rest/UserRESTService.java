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
import org.openremote.beehive.api.dto.UserDTO;
import org.openremote.beehive.api.service.UserService;
import org.openremote.beehive.domain.User;

/**
 * The class is for managing user account.
 */
@Path("/manageuser")
public class UserRESTService extends RESTBaseService {
   
   @Path("create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createUser(UserDTO userDTO) {
      User user = new User();
      user.setUsername(userDTO.getUsername());
      getUserService().saveUser(user);
      return buildResponse(user.toDTO());
   }
   
   @Path("get/{user_id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getUserById(@PathParam("user_id")  long userId) {
      User user = getUserService().getUserById(userId);
      return buildResponse(user.toDTO());
   }
   
   @Path("getbyname/{username}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response getUserByUsername(@PathParam("username")  String username) {
      User user = getUserService().getUserByUsername(username);
      if (user == null) return resourceNotFoundResponse();
      return buildResponse(user.toDTO());
   }
   
   @Path("update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response updateUser(UserDTO userDto) {
      User user = getUserService().getUserById(userDto.getId());
      user.setUsername(userDto.getUsername());
      user.setToken(userDto.getToken());
      user.setPendingRoleName(userDto.getPendingRoleName());
      getUserService().updateUser(user);
      return buildResponse(true);
   }
   
   @Path("delete/{user_id}")
   @DELETE
   public Response deleteUser(@PathParam("user_id") long userId) {
      getUserService().deleteUserById(userId);
      return buildResponse(true);
   }
   
   @Path("createinvitee/{account_id}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response createInvitee(UserDTO userDTO, @PathParam("account_id") long accountId,
                           @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      User invitee = userDTO.toUser();
      getUserService().saveInvitee(invitee, accountId);
      return buildResponse(invitee.toDTO());
   }
   
   @Path("loadall/{account_id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response loadUsersByAccount(@PathParam("account_id") long accountId,
                        @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<User> users = getUserService().loadUsersByAccount(accountId);
      List<UserDTO> userDTOs = new ArrayList<UserDTO>();
      for (User user : users) {
         userDTOs.add(user.toDTO());
      }
      
      if (userDTOs.size() == 0) return resourceNotFoundResponse();
      return buildResponse(userDTOs);
   }
   
   protected UserService getUserService() {
      return (UserService) getSpringContextInstance().getBean("userService");
   }
}
