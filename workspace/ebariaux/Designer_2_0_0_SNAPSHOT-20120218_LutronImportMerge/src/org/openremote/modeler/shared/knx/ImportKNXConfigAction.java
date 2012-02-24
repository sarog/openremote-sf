package org.openremote.modeler.shared.knx;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.data.ModelData;

public class ImportKNXConfigAction implements Action<ImportKNXConfigResult> {

  private Device device;
  private ArrayList<ModelData> config;
  
  public ImportKNXConfigAction() {
    super();
  }

  public ImportKNXConfigAction(Device device, ArrayList<ModelData> config) {
    super();
    this.device = device;
    this.config = config;
  }

  public Device getDevice() {
    return device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }

  public ArrayList<ModelData> getConfig() {
    return config;
  }

  public void setConfig(ArrayList<ModelData> config) {
    this.config = config;
  }
}