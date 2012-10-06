package org.openremote.controller.protocol.domintell;

import org.apache.commons.lang.StringUtils;

public class DomintellAddress {

   private int address;
   
   public DomintellAddress(String address) throws InvalidDomintellAddressException {
      if (address.startsWith("0x")) {
         if (address.length() > 2) {
            this.address = Integer.parseInt(address.substring(2).trim(), 16);
         } else {
            throw new InvalidDomintellAddressException("Could not parse Domintell address", address);
         }
      } else {
         this.address = Integer.parseInt(address);
      }
   }
   
   public DomintellAddress(int address) {
      this.address = address;
   }
   
   public String toString() {
      return StringUtils.right("      " + Integer.toString(address, 16).toUpperCase(), 6);
   }
}
