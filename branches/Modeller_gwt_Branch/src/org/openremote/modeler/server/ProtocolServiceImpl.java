package org.openremote.modeler.server;

import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolService;
import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.service.ProtocolParser;

public class ProtocolServiceImpl extends BaseGWTSpringController implements ProtocolService {

   private static final long serialVersionUID = 8057648010410493998L;

   public Map<String, ProtocolDefinition> getProtocolContainer() {
      if (ProtocolContainer.getInstance().getProtocols().size() == 0) {
         ProtocolParser parser = new ProtocolParser();
         ProtocolContainer.getInstance().setProtocols(parser.parseXmls());
      }
      return ProtocolContainer.getInstance().getProtocols();
   }

   private ProtocolDefinition getCorrectProtocolDefinition() {
      ProtocolDefinition definition = new ProtocolDefinition();
      definition.setName("KNX");

      ProtocolAttrDefinition groupAddressAttr = new ProtocolAttrDefinition();
      groupAddressAttr.setName("groupAddress");
      groupAddressAttr.setLabel("Group Address");

      ProtocolAttrDefinition commandAttr = new ProtocolAttrDefinition();
      commandAttr.setName("command");
      commandAttr.setLabel("KNX Command");
      return definition;
   }
}
