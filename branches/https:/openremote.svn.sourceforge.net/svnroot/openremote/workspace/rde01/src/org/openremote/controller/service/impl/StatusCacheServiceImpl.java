package org.openremote.controller.service.impl;

import java.util.Map;
import java.util.Set;

import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.statuscache.StatusCache;

public class StatusCacheServiceImpl implements StatusCacheService{
   
   private StatusCache cache;
   
   @Override
   public String getStatusBySensorId(Integer sensorId) {
      return cache.queryStatusBySensorlId(sensorId);
   }

   @Override
   public Map<Integer, String> queryStatuses(Set<Integer> sensorIds) {
      return cache.queryStatuses(sensorIds);
   }

   @Override
   public void saveOrUpdateStatus(Integer sensorId, String newStatus) {
      cache.saveOrUpdateStatus(sensorId, newStatus);
   }
   
   @Override
   public void clearAllStatusCache() {
      cache.clear();
   }

   public void setCache(StatusCache cache) {
      this.cache = cache;
   }
   
}
