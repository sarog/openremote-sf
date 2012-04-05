package org.openremote.ir.domain;

import java.io.Serializable;

/**
 * contains informations for generating ir trans commands
 * @author wbalcaen
 *
 */
public class IRTrans implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String ipAdress;
   private String udpPort;
   private String irLed;

   public IRTrans() {
   }

   public IRTrans(String ipAdress, String udpPort, String irLed) {
      super();
      this.ipAdress = ipAdress;
      this.udpPort = udpPort;
      this.irLed = irLed;
   }

   public String getIpAdress() {
      return ipAdress;
   }

   public void setIpAdress(String ipAdress) {
      this.ipAdress = ipAdress;
   }

   public String getUdpPort() {
      return udpPort;
   }

   public void setUdpPort(String udpPort) {
      this.udpPort = udpPort;
   }

   public String getIrLed() {
      return irLed;
   }

   public void setIrLed(String irLed) {
      this.irLed = irLed;
   }

}
