package org.openremote.modeler.server.ir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.server.DeviceCommandController;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.shared.dto.DeviceCommandDetailsDTO;
import org.openremote.modeler.shared.ir.GenerateIRCommandsAction;
import org.openremote.modeler.shared.ir.GenerateIRCommandsResult;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class GenerateIRCommandsActionHandler implements ActionHandler<GenerateIRCommandsAction, GenerateIRCommandsResult> {

  private DeviceService deviceService;
  private DeviceCommandService deviceCommandService;
  private Configuration configuration;
  
  @Override
  public GenerateIRCommandsResult execute(GenerateIRCommandsAction action, ExecutionContext context) throws DispatchException {
    Representation r = new ClientResource(configuration.getIrServiceRESTRootUrl() + "GenerateDeviceCommands").post(new JsonRepresentation(new JSONSerializer().exclude("*.class").exclude("device").deepSerialize(action)));
    GenericResourceResultWithErrorMessage result = null;
    try {
      result = new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", ArrayList.class).use("result.values", DeviceCommandDetailsDTO.class).deserialize(r.getText());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    };
    
    // TODO: check for error and return
    // TODO: correctly handle return values -> in case of success for what to update and in error for error message 
    
    Device device = deviceService.loadById(action.getDevice().getOid());
    @SuppressWarnings("unchecked")
    List<DeviceCommandDetailsDTO> dtos = (List<DeviceCommandDetailsDTO>) result.getResult();
    for (DeviceCommandDetailsDTO dto : dtos) {
      DeviceCommand dc = DeviceCommandController.createDeviceCommandFromDTO(dto);
      dc.setDevice(device);
      deviceCommandService.save(dc);
    }
    
    return new GenerateIRCommandsResult();
  }
  
  @Override
  public Class<GenerateIRCommandsAction> getActionType() {
    return GenerateIRCommandsAction.class;
  }

  @Override
  public void rollback(GenerateIRCommandsAction action, GenerateIRCommandsResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action
  }

  public void setDeviceService(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

}
