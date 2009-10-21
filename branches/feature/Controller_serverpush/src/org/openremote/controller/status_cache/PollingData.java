package org.openremote.controller.status_cache;

import java.util.HashMap;
import java.util.Map;

public class PollingData {
   
   private String[] controlIDs;

   private Map<String, String> changedStatuses;

   public PollingData() {
      super();
   }
   
   public PollingData(String[] controlIDs) {
      super();
      this.controlIDs = controlIDs;
   }

   public Map<String, String> getChangedStatuses() {
      return changedStatuses;
   }

   public void setChangedStatuses(Map<String, String> changedStatuses) {
      this.changedStatuses = changedStatuses;
   }

   public String[] getControlIDs() {
      return controlIDs;
   }

   public void setControlIDs(String[] controlIDs) {
      this.controlIDs = controlIDs;
   }
   
}
