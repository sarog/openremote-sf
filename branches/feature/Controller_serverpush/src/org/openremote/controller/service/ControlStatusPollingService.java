package org.openremote.controller.service;

import org.openremote.controller.status_cache.PollingData;

public interface ControlStatusPollingService {
   
   public String getChangedStatuses(String unParsedcontrolIDs);

   public String parsePollingResult(PollingData pollingResult);
   
}
