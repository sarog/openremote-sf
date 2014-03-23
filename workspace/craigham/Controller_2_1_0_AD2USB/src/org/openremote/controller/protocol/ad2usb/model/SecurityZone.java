package org.openremote.controller.protocol.ad2usb.model;

public class SecurityZone {

   public enum ZoneStatus {
      READY("Ready"), FAULT("Fault"), NA("N/A");
      private String mDisplayString;

      ZoneStatus(String displayString) {
         mDisplayString = displayString;
      }

      public String toString() {
         return mDisplayString;
      }
   };

   private int mId;
   private String mName;
   private ZoneStatus mStatus = ZoneStatus.NA;

   public ZoneStatus getStatus() {
      return mStatus;
   }

   public void setStatus(ZoneStatus status) {
      mStatus = status;
   }

   protected int getId() {
      return mId;
   }

   public String getName() {
      return mName;
   }

   public SecurityZone(int id, String name) {
      mId = id;
      mName = name;
   }

   public boolean isFaulted() {
      return mStatus == ZoneStatus.FAULT;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + mId;
      result = prime * result + ((mName == null) ? 0 : mName.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      SecurityZone other = (SecurityZone) obj;
      if (mId != other.mId) return false;
      if (mName == null) {
         if (other.mName != null) return false;
      } else if (!mName.equals(other.mName)) return false;
      return true;
   }

   public String toString() {
      return "Security Zone: " + mId + ", desc: " + mName + ", status: " + mStatus;
   }

}
