/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.rpc.LinkControllerRPCService;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.ControllerManagementException;
import org.openremote.modeler.service.UserService;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.openremote.useraccount.domain.AccountDTO;
import org.openremote.useraccount.domain.ControllerDTO;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * The Class is for managing linked controller
 */
public class LinkControllerService extends BaseGWTSpringController implements LinkControllerRPCService {

  private static final long serialVersionUID = -7208047346538344305L;
  private static Logger log = Logger.getLogger(LinkControllerService.class);
  
  private Configuration configuration;
  private UserService userService;

  public void setConfiguration(Configuration configuration)
  {
    this.configuration = configuration;
  }

  public void setUserService(UserService userService)
  {
    this.userService = userService;
  }


  @Override
  public void deleteController(long oid) throws ControllerManagementException
  {
    User currentUser = userService.getCurrentUser();
    ClientResource cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "controller/"+oid);
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation result = cr.delete();
    cr.release();
    String str;
    try
    {
      str = result.getText();
    } catch (IOException e)
    {
      log.error("Error calling UserAccount rest service while deleting controller", e);
      throw new ControllerManagementException(e.getMessage());
    }
    GenericResourceResultWithErrorMessage res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", String.class).deserialize(str);
    if (res.getErrorMessage() != null) {
      log.error("Error calling UserAccount rest service while deleting controller: " + res.getErrorMessage());
      throw new ControllerManagementException(res.getErrorMessage());
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public ArrayList<ControllerDTO> getLinkedControllerDTOs() throws ControllerManagementException
  {
    User currentUser = userService.getCurrentUser();
    ClientResource cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "controller/find");
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation r = cr.get();
    cr.release();
    String str;
    try
    {
      str = r.getText();
    } catch (IOException e) 
    {
      log.error("Error calling UserAccount rest service while loading linked controller", e);
      throw new ControllerManagementException(e.getMessage());
    }
    GenericResourceResultWithErrorMessage res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", ControllerDTO.class).deserialize(str); 
    if (res.getErrorMessage() != null) {
      log.error("Error calling UserAccount rest service while loading linked controller: " + res.getErrorMessage() );
      throw new ControllerManagementException(res.getErrorMessage());
    } 
    ArrayList<ControllerDTO> result = (ArrayList<ControllerDTO>)res.getResult();
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ControllerDTO linkController(String macAddress) throws ControllerManagementException
  {
    User currentUser = userService.getCurrentUser();
    ClientResource cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "controller/find/" + macAddress);
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation r = cr.get();
    cr.release();
    String str;
    try
    {
      str = r.getText();
    } catch (IOException e)
    {
      log.error("Error calling UserAccount rest service while linking controller", e);
      throw new ControllerManagementException(e.getMessage());
    }
    GenericResourceResultWithErrorMessage res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", ControllerDTO.class).deserialize(str); 
    if (res.getErrorMessage() != null) {
      log.error("Error calling UserAccount rest service while linking controller: " + res.getErrorMessage());
      throw new ControllerManagementException(res.getErrorMessage());
    }
    List<ControllerDTO> tempList = (List<ControllerDTO>)res.getResult();
    if (tempList.size() != 1) {
      throw new ControllerManagementException("No controller with the given MAC address was found.\n" +
              "Please start your controller before linking it and make sure it has internet access.");
    }
    ControllerDTO controllerToLink = tempList.get(0);
    controllerToLink.setLinked(true);
    controllerToLink.setAccount(new AccountDTO(currentUser.getAccount().getOid()));
    
    cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "controller");
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(controllerToLink));
    r = cr.put(rep);
    cr.release();
    try
    {
      str = r.getText();
    } catch (IOException e)
    {
      log.error("Error calling UserAccount rest service while linking controller", e);
      throw new ControllerManagementException(e.getMessage());
    }
    res = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ControllerDTO.class).deserialize(str); 
    if (res.getErrorMessage() != null) {
      log.error("Error calling UserAccount rest service while linking controller: " + res.getErrorMessage());
      throw new ControllerManagementException(res.getErrorMessage());
    }
    ControllerDTO result = (ControllerDTO)res.getResult();
    return result;
  }

}
