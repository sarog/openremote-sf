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
package org.openremote.modeler.server.ir;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openremote.ir.domain.BrandInfo;
import org.openremote.ir.domain.CodeSetInfo;
import org.openremote.ir.domain.DeviceInfo;
import org.openremote.ir.domain.IRCommandInfo;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.rpc.IRRPCService;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.server.BaseGWTSpringController;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.ir.IRServiceException;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.google.gwt.user.client.rpc.AsyncCallback;

import flexjson.JSONDeserializer;

/**
 * Service that handles IR import and conversion.
 * This implementation delegates to the IRService through REST calls.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class IRServiceController extends BaseGWTSpringController implements IRRPCService {

  private static final long serialVersionUID = 1L;
  
  private static Logger log = Logger.getLogger(IRServiceController.class);

  private Configuration configuration;
  private UserService userService;

  @SuppressWarnings("unchecked")
  public ArrayList<String> getBrands(String prontoHandle) throws IRServiceException {
    return (ArrayList<String>)restCallToIRService(configuration.getIrServiceRESTRootUrl() + prontoHandle + "/brands", null);
  }

  @SuppressWarnings("unchecked")
  public ArrayList<String> getDevices(String prontoHandle, String brandName) throws IRServiceException {
    try {
      return (ArrayList<String>)restCallToIRService(configuration.getIrServiceRESTRootUrl() + prontoHandle + "/brand/" + URLEncoder.encode(brandName, "UTF8") + "/devices", null);
    } catch (UnsupportedEncodingException e) {
      log.error("Failed to encode parameters for REST call", e);
      throw new IRServiceException("Call to server failed");
    }
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<CodeSetInfo> getCodeSets(String prontoHandle, String brandName, String deviceName) throws IRServiceException {
    try {
      return (ArrayList<CodeSetInfo>)restCallToIRService(configuration.getIrServiceRESTRootUrl() + prontoHandle + "/brand/" + URLEncoder.encode(brandName, "UTF8") + "/device/" + URLEncoder.encode(deviceName, "UTF8") + "/codeSets",
              new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class)
              .use("result", ArrayList.class).use("result.values", CodeSetInfo.class)
              .use("result.deviceInfo", DeviceInfo.class).use("result.deviceInfo.brandInfo", BrandInfo.class));
    } catch (UnsupportedEncodingException e) {
      log.error("Failed to encode parameters for REST call", e);
      throw new IRServiceException("Call to server failed");
    }
  }

  @SuppressWarnings("unchecked")
  public ArrayList<IRCommandInfo> getIRCommands(String prontoHandle, String brandName, String deviceName, int index) throws IRServiceException {
    try {
      return (ArrayList<IRCommandInfo>)restCallToIRService(configuration.getIrServiceRESTRootUrl() + prontoHandle + "/brand/" + URLEncoder.encode(brandName, "UTF8") + "/device/" + URLEncoder.encode(deviceName, "UTF8") + "/codeSet/" + index + "/IRCommands",
              new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class)
              .use("result", ArrayList.class).use("result.values", IRCommandInfo.class)
              .use("result.codeSetInfo", CodeSetInfo.class).use("result.codeSetInfo.deviceInfo", DeviceInfo.class)
              .use("result.codeSetInfo.deviceInfo.brandInfo", BrandInfo.class));
    } catch (UnsupportedEncodingException e) {
      log.error("Failed to encode parameters for REST call", e);
      throw new IRServiceException("Call to server failed");
    }
  }

  public void unregisterFile(String prontoHandle) {
    User currentUser = userService.getCurrentUser();
    ClientResource cr = new ClientResource(configuration.getIrServiceRESTRootUrl() + "ProntoFile/" + prontoHandle);
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    cr.delete();
    cr.release();
  }

  private Object restCallToIRService(String url, JSONDeserializer<GenericResourceResultWithErrorMessage> deserializer) throws IRServiceException {
    User currentUser = userService.getCurrentUser();
    ClientResource cr = new ClientResource(url);
    cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, currentUser.getUsername(), currentUser.getPassword());
    Representation r = cr.get();
    
    String str;
    try { 
      str = r.getText();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      cr.release();
    }
    
    if (deserializer == null) {
      deserializer = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class);
    }
    GenericResourceResultWithErrorMessage res = deserializer.deserialize(str);
    if (res.getErrorMessage() != null) {
      throw new IRServiceException(res.getErrorMessage());
    }
    
    return res.getResult();
  }
  
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }
 
}