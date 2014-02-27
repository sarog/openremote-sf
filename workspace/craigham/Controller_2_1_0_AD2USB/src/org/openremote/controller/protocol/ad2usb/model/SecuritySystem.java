package org.openremote.controller.protocol.ad2usb.model;


public class SecuritySystem {

   public enum ChimeStatus {
      ON("On"), OFF("Off"), NA("N/A");
      private String mDisplayString;

      ChimeStatus(String displayString) {
         mDisplayString = displayString;
      }

      public String toString() {
         return mDisplayString;
      }
   };

   private Partition mPartition;
   private ChimeStatus mChimeStatus = ChimeStatus.NA;

   public SecuritySystem() {
      mPartition = new Partition(1);
   }

   public ChimeStatus getChimeStatus() {
      return mChimeStatus;
   }

   public void setChimeStatus(ChimeStatus chimeStatus) {
      mChimeStatus = chimeStatus;
   }

   public void setArmedStatus(ArmedStatus status) {
      mPartition.setStatus(status);
   }

   public ArmedStatus getArmedStatus() {
      return mPartition.getStatus();
   }
}
