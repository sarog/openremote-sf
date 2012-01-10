package org.openremote.modeler.server.lutron;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import org.openremote.modeler.shared.lutron.ImportLutronConfigAction;
import org.openremote.modeler.shared.lutron.ImportLutronConfigResult;

public class ImportLutronConfigActionHandler implements ActionHandler<ImportLutronConfigAction, ImportLutronConfigResult> {

  @Override
  public ImportLutronConfigResult execute(ImportLutronConfigAction arg0, ExecutionContext arg1) throws DispatchException {
    System.out.println("In ImportLutronConfigActionHandler");
    return new ImportLutronConfigResult();
  }

  @Override
  public Class<ImportLutronConfigAction> getActionType() {
    return ImportLutronConfigAction.class;
  }

  @Override
  public void rollback(ImportLutronConfigAction arg0, ImportLutronConfigResult arg1, ExecutionContext arg2) throws DispatchException {
    // TODO Implementation only required for compound action
    
  }

}
