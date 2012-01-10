package org.openremote.modeler.shared.lutron;

import net.customware.gwt.dispatch.shared.Action;

public class ImportLutronConfigAction implements Action<ImportLutronConfigResult> {

  
  // TODO: device should be on here and not on the import config
  
  
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

}
