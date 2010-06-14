package org.openremote.controller.service.impl;

import java.util.Map;
import java.util.Set;

import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.status_cache.StatusCache;

public class StatusCacheServiceImpl implements StatusCacheService{
   
   private StatusCache cache;
   
   @Override
   public String getStatusByComponentId(Integer controlId) {
      return cache.queryStatusByControlId(controlId);
   }

   @Override
   public Map<Integer, String> queryStatuses(Set<Integer> controlIds) {
      return cache.queryStatuses(controlIds);
   }

   @Override
   public void saveOrUpdateStatus(Integer controlId, String newStatus) {
      cache.saveOrUpdateStatus(controlId, newStatus);
   }

   public void setCache(StatusCache cache) {
      this.cache = cache;
   }
   
}
