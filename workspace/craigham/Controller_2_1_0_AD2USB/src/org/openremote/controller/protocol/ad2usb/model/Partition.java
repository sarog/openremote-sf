package org.openremote.controller.protocol.ad2usb.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.openremote.controller.protocol.ad2usb.model.SecurityZone.ZoneStatus;

public class Partition {

   private ArmedStatus mStatus;
   private int mId;
   private Map<Integer, SecurityZone> mSecurityZones = new HashMap<Integer, SecurityZone>();
   private Set<SecurityZone> mByPassedZones = new HashSet<SecurityZone>();

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

   public void bypass(SecurityZone zone) {
      // if (!mSecurityZones.contains(zone)) throw new IllegalArgumentException("Security Zone not valid: " + zone);
      // mByPassedZones.add(zone);
   }

   public void restore(SecurityZone zone) {
      // if (!mSecurityZones.contains(zone)) throw new IllegalArgumentException("Security Zone not valid: " + zone);
      // mByPassedZones.remove(zone);
   }

   public Collection<SecurityZone> getSecurityZones() {
      return mSecurityZones.values();
   }

   public void setZoneStatus(int id, String zoneDescription, ZoneStatus fault) {
      SecurityZone zone = mSecurityZones.get(id);
      if (zone == null) {
         zone = new SecurityZone(id, zoneDescription);
         mSecurityZones.put(id, zone);
      }
      zone.setStatus(fault);
   }

   public SecurityZone getZone(int id) {
      return mSecurityZones.get(id);
   }

   public Set<SecurityZone> getNotReadyZones() {
      Set<SecurityZone> returnValue = new LinkedHashSet<SecurityZone>();
      for (SecurityZone zone : mSecurityZones.values()) {
         if (zone.isFaulted() && !mByPassedZones.contains(zone)) returnValue.add(zone);
      }
      return returnValue;
   }

}
