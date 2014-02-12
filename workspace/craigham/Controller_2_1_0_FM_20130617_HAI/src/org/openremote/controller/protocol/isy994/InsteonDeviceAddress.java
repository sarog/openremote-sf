package org.openremote.controller.protocol.isy994;

/**
 * Ensure format is followed for Insteon Device addresses
 * 
 * @author craigh
 * 
 */
public class InsteonDeviceAddress {

   private String mAddress;
// TODO: Make sure this doesn't parse 'Light: 1F 5 6F 1'
   public InsteonDeviceAddress(String address) {
      // TODO implement validation logic
      mAddress = address;
   }

   public String toString() {
      return mAddress;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof InsteonDeviceAddress)) return false;
      return mAddress.equals(((InsteonDeviceAddress) obj).mAddress);
   }

   @Override
   public int hashCode() {
      return mAddress.hashCode();
   }
}
