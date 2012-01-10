package org.openremote.modeler.shared.lutron;

import net.customware.gwt.dispatch.shared.Action;

import org.openremote.modeler.domain.Device;

public class ImportLutronConfigAction implements Action<ImportLutronConfigResult> {
  
  private Device device;
  private ImportConfig config;
  
  public ImportLutronConfigAction() {
  }
  
  public ImportLutronConfigAction(ImportConfig config) {
    this.config = config;
  }

  public ImportConfig getConfig() {
    return config;
  }

  public void setConfig(ImportConfig config) {
    this.config = config;
  }
  
  public Device getDevice() {
    return device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }

}
