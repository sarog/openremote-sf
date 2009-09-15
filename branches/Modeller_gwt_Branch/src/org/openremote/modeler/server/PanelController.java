package org.openremote.modeler.server;

import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.rpc.TouchPanelRPCService;
import org.openremote.modeler.service.TouchPanelParser;
import org.openremote.modeler.touchpanel.TouchPanelContainer;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

public class PanelController extends BaseGWTSpringController implements TouchPanelRPCService {

   /**
    * 
    */
   private static final long serialVersionUID = -1064152509394362895L;

   public Map<String, List<TouchPanelDefinition>> getPanels() {
      if (TouchPanelContainer.getInstance().getPanels().size() == 0) {
         TouchPanelParser parser = new TouchPanelParser();
         TouchPanelContainer.getInstance().setPanels(parser.parseXmls());
      }
      return TouchPanelContainer.getInstance().getPanels();
   }

}
