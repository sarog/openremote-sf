package org.openremote.modeler.server.lutron;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.shared.lutron.ImportLutronConfigAction;
import org.openremote.modeler.shared.lutron.ImportLutronConfigResult;
import org.openremote.modeler.shared.lutron.OutputImportConfig;
import org.openremote.modeler.shared.lutron.OutputType;

public class ImportLutronConfigActionHandler implements ActionHandler<ImportLutronConfigAction, ImportLutronConfigResult> {

  @Override
  public ImportLutronConfigResult execute(ImportLutronConfigAction action, ExecutionContext context) throws DispatchException {
    System.out.println("In ImportLutronConfigActionHandler");
    System.out.println("Areas " + action.getConfig().getOutputs());

    for (OutputImportConfig config : action.getConfig().getOutputs()) {
      if (OutputType.Dimmer == config.getType() || OutputType.QEDShade == config.getType()) {
        /*
        addDeviceCommand(device, output, "RAISE", NoScene, NoLevel, NoKey, "_Raise");
        addDeviceCommand(device, output, "LOWER", NoScene, NoLevel, NoKey, "_Lower");
        addDeviceCommand(device, output, "STOP", NoScene, NoLevel, NoKey, "_Stop");
        addDeviceCommand(device, output, "FADE", NoScene, NoLevel, NoKey, "_Fade");
        addDeviceCommand(device, output, "STATUS_DIMMER", NoScene, NoLevel, NoKey, "_LevelRead");
        */
      }
    }

    /*              if (OutputType.Dimmer.toString().equals(output.getType()) || OutputType.QEDShade.toString().equals(output.getType())) {
                  } else if (OutputType.GrafikEyeMainUnit.toString().equals(output.getType())) {
                    addDeviceCommand(device, output, "SCENE", "0", NoLevel, NoKey, "_SceneOff");
                    addDeviceCommand(device, output, "STATUS_SCENE", "0", NoLevel, NoKey, "_OffRead");
                    for (int sceneNumber = 1; sceneNumber <= 8; sceneNumber++) {
                      addDeviceCommand(device, output, "SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber));
                      addDeviceCommand(device, output, "STATUS_SCENE", Integer.toString(sceneNumber), NoLevel, NoKey, "_Scene" + Integer.toString(sceneNumber) + "Read");
                    }
                    addDeviceCommand(device, output, "STATUS_SCENE", NoScene, NoLevel, NoKey, "_SceneRead");
                  } else if (OutputType.Fan.toString().equals(output.getType())) {
                    addDeviceCommand(device, output, "FADE", NoScene, "0", NoKey, "_Off");
                    addDeviceCommand(device, output, "FADE", NoScene, "25", NoKey, "_Low");
                    addDeviceCommand(device, output, "FADE", NoScene, "50", NoKey, "_Medium");
                    addDeviceCommand(device, output, "FADE", NoScene, "75", NoKey, "_MediumHigh");
                    addDeviceCommand(device, output, "FADE", NoScene, "100", NoKey, "_Full");
        */

    return new ImportLutronConfigResult();
  }

  @Override
  public Class<ImportLutronConfigAction> getActionType() {
    return ImportLutronConfigAction.class;
  }

  @Override
  public void rollback(ImportLutronConfigAction action, ImportLutronConfigResult result, ExecutionContext context) throws DispatchException {
    // TODO Implementation only required for compound action

  }

}
