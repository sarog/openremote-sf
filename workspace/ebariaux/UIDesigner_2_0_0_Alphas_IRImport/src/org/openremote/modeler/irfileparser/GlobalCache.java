package org.openremote.modeler.irfileparser;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * contains informations for generating global caché commands
 * 
 * @author wbalcaen
 * 
 */
public class GlobalCache implements IsSerializable {

   private String IpAddress;
   private String tcpPort;
   private String connector;

   public GlobalCache(String ipAddress, String tcpPort, String connector) {
      super();
      IpAddress = ipAddress;
      this.tcpPort = tcpPort;
      this.connector = connector;
   }

   public GlobalCache() {
   }

   public String getIpAddress() {
      return this.IpAddress;
   }

   public String getTcpPort() {
      return this.tcpPort;
   }

   public String getConnector() {
      return this.connector;
   }

   public void setIpAddress(String ipAddress) {
      IpAddress = ipAddress;
   }

   public void setTcpPort(String tcpPort) {
      this.tcpPort = tcpPort;
   }

   public void setConnector(String connector) {
      this.connector = connector;
   }

}
