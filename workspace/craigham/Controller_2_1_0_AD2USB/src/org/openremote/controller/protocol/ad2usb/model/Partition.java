package org.openremote.controller.protocol.ad2usb.model;

public class Partition {

   private ArmedStatus mStatus;
   private int mId;

   public Partition(int id) {
      mId = id;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Partition)) return false;

      return this.mId == ((Partition) obj).mId;
   }

   public ArmedStatus getStatus() {
      return mStatus;
   }

   protected void setStatus(ArmedStatus status) {
      mStatus = status;
   }
}
