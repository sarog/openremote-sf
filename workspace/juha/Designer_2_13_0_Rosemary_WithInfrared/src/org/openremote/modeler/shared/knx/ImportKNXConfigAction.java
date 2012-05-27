package org.openremote.modeler.shared.knx;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.shared.dto.DeviceDTO;

import com.extjs.gxt.ui.client.data.ModelData;

public class ImportKNXConfigAction implements Action<ImportKNXConfigResult> {

  private DeviceDTO device;
  private ArrayList<ModelData> config;
  
  public ImportKNXConfigAction() {
    super();
  }

  public ImportKNXConfigAction(DeviceDTO device, ArrayList<ModelData> config) {
    super();
    this.device = device;
    this.config = config;
  }

  public DeviceDTO getDevice() {
    return device;
  }

  public void setDevice(DeviceDTO device) {
    this.device = device;
  }

  public ArrayList<ModelData> getConfig() {
    return config;
  }

  public void setConfig(ArrayList<ModelData> config) {
    this.config = config;
  }
}