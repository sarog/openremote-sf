package org.openremote.controller.protocol.knx;

import java.util.Map;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.protocol.knx.datatype.DataPointType;
import org.openremote.controller.protocol.knx.ip.IpTunnelClientListener;
import org.openremote.controller.protocol.knx.ip.IpTunnelClientListener.Status;

public class IpInterfaceMonitor extends KNXCommand implements StatusCommand {
   private KNXIpConnectionManager connectionManager;

   IpInterfaceMonitor(KNXIpConnectionManager connectionManager, GroupAddress address, ApplicationProtocolDataUnit apdu,
         DataPointType dpt) {
      super(connectionManager, address, apdu, dpt);
      this.connectionManager = connectionManager;
   }

   @Override
   public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
      IpTunnelClientListener.Status o = Status.unknown;
      try {
         o = this.connectionManager.getConnection().getInterfaceStatus();
      } catch (ConnectionException e) {
      }
      switch (o) {
      case connected:
         return "OK";
      case disconnected:
         return "NOK";
      default:
         return "UNKNOWN";
      }
   }

   static IpInterfaceMonitor createCommand(String name, KNXIpConnectionManager mgr, GroupAddress address, DataPointType dpt) {
      if ("MONITOR".equals(name)) return new IpInterfaceMonitor(mgr, address, null, dpt);

      return null;
   }

   @Override
   public String toString() {
      return "IpInterfaceMonitor";
   }
}
