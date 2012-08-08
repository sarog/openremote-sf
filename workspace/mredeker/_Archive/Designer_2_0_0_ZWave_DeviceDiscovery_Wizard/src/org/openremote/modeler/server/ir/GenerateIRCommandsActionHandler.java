package org.openremote.modeler.server.ir;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.domain.Device;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.IRFileParserService;
import org.openremote.modeler.shared.ir.GenerateIRCommandsAction;
import org.openremote.modeler.shared.ir.GenerateIRCommandsResult;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONSerializer;

public class GenerateIRCommandsActionHandler implements ActionHandler<GenerateIRCommandsAction, GenerateIRCommandsResult> {

  private DeviceService deviceService;
  private IRFileParserService iRFileParserService;
  
  @Override
  public GenerateIRCommandsResult execute(GenerateIRCommandsAction action, ExecutionContext context) throws DispatchException {
    
    Representation r = new ClientResource("http://localhost:8080/irservice/rest/GenerateDeviceCommands").post(new JsonRepresentation(new JSONSerializer().exclude("*.class").exclude("device").deepSerialize(action)));
    System.out.println(r);
    
    Device device = deviceService.loadById(action.getDevice().getOid());
    /*
    try {
      iRFileParserService.saveCommands(device, action.getGlobalCache(), action.getIrTrans(), action.getCommands());
    } catch (IrFileParserException e) {
      
      
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    */
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

  public void setiRFileParserService(IRFileParserService iRFileParserService) {
    this.iRFileParserService = iRFileParserService;
  }

}
