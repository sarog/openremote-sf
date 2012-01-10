package org.openremote.modeler.shared.lutron;

import java.io.Serializable;
import java.util.HashSet;

@SuppressWarnings("serial")
public class ImportConfig implements Serializable {

  private HashSet<OutputImportConfig> outputs;

  public ImportConfig() {
    outputs = new HashSet<OutputImportConfig>();
  }

  public HashSet<OutputImportConfig> getOutputs() {
    return outputs;
  }

  public void setOutputs(HashSet<OutputImportConfig> outputs) {
    this.outputs = outputs;
  }
  
  public void addOutputConfig(OutputImportConfig config) {
    outputs.add(config);
  }

}
