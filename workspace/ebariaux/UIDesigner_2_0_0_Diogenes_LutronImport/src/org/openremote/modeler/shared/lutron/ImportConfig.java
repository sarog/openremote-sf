package org.openremote.modeler.shared.lutron;

import java.io.Serializable;
import java.util.HashSet;

import org.openremote.modeler.domain.Device;

@SuppressWarnings("serial")
public class ImportConfig implements Serializable {

  private Device device;
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

  public Device getDevice() {
    return device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }
}
