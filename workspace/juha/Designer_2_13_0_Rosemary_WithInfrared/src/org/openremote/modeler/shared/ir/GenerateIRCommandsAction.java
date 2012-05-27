package org.openremote.modeler.shared.ir;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.irfileparser.GlobalCache;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.irfileparser.IRTrans;
import org.openremote.modeler.shared.dto.DeviceDTO;

public class GenerateIRCommandsAction implements Action<GenerateIRCommandsResult> {
  
  private DeviceDTO device;
  private ArrayList<IRCommandInfo> commands;
  private GlobalCache globalCache;
  private IRTrans irTrans;
  
  public GenerateIRCommandsAction() {
    super();
  }
  
  public GenerateIRCommandsAction(DeviceDTO device, ArrayList<IRCommandInfo> commands, GlobalCache globalCache, IRTrans irTrans) {
    super();
    this.device = device;
    this.commands = commands;
    this.globalCache = globalCache;
    this.irTrans = irTrans;
  }
  
  public DeviceDTO getDevice() {
    return device;
  }
  
  public void setDevice(DeviceDTO device) {
    this.device = device;
  }
  
  public ArrayList<IRCommandInfo> getCommands() {
    return commands;
  }
  
  public void setCommands(ArrayList<IRCommandInfo> commands) {
    this.commands = commands;
  }
  
  public GlobalCache getGlobalCache() {
    return globalCache;
  }
  
  public void setGlobalCache(GlobalCache globalCache) {
    this.globalCache = globalCache;
  }
  
  public IRTrans getIrTrans() {
    return irTrans;
  }
  
  public void setIrTrans(IRTrans irTrans) {
    this.irTrans = irTrans;
  } 

}
