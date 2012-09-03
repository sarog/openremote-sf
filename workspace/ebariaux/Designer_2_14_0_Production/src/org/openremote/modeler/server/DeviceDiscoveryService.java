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

import org.apache.log4j.Logger;
import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.rpc.DeviceDiscoveryRPCService;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.DeviceDiscoveryException;
import org.openremote.modeler.service.UserService;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONDeserializer;

/**
 * The Class is for managing linked controller
 */
public class DeviceDiscoveryService extends BaseGWTSpringController implements DeviceDiscoveryRPCService {

  private static final long serialVersionUID = -7208047346538344305L;
  private static Logger log = Logger.getLogger(DeviceDiscoveryService.class);
  
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

  @SuppressWarnings("unchecked")
  @Override
  public ArrayList<DiscoveredDeviceDTO> loadNewDevices() throws DeviceDiscoveryException
  {
    User currentUser = userService.getCurrentUser();
    ClientResource cr = new ClientResource(configuration.getDeviceDiscoveryServiceRESTRootUrl() + "discoveredDevices?used=false");
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation r = cr.get();
    cr.release();
    String str;
    try
    {
      str = r.getText();
    } catch (IOException e) 
    {
      log.error("Error calling DeviceDiscovery rest service while loading new discovered devices", e);
      throw new DeviceDiscoveryException(e.getMessage());
    }
    GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", DiscoveredDeviceDTO.class).deserialize(str); 
    ArrayList<DiscoveredDeviceDTO> result = (ArrayList<DiscoveredDeviceDTO>)res.getResult(); 
    if (res.getErrorMessage() != null) {
      log.error("Error calling DeviceDiscovery rest service while loading new discovered devices " + res.getErrorMessage() );
      throw new DeviceDiscoveryException(res.getErrorMessage());
    } 
    return result;
  }


}
