package org.openremote.controller.service;

import java.util.Map;
import java.util.Set;


public interface StatusCacheService {
   
   void saveOrUpdateStatus(Integer componentId,String newStatus);
   String getStatusByComponentId(Integer componentId);//TODO String to Integer.
   Map<Integer,String> queryStatuses(Set<Integer> componentIds);
}
