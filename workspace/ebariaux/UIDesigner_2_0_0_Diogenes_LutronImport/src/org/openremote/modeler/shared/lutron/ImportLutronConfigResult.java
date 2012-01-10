package org.openremote.modeler.shared.lutron;

import net.customware.gwt.dispatch.shared.Result;

public class ImportLutronConfigResult implements Result {

  private String errorMessage;
  
  public ImportLutronConfigResult() {
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
}
