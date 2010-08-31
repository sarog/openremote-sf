/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.service.impl;

import java.util.Map;
import java.util.Set;

import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.statuscache.StatusCache;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StatusCacheServiceImpl2 implements StatusCacheService
{

     private StatusCache cache;
     private ControlCommandService ccs;

     @Override
     public String getStatusBySensorId(Integer sensorId) {
        return cache.queryStatusBySensorlId(sensorId);
     }

     @Override
     public Map<Integer, String> queryStatuses(Set<Integer> sensorIds) {
        return cache.queryStatuses(sensorIds);
     }

     @Override
     public void saveOrUpdateStatus(Integer sensorId, String newStatus)
     {
        cache.saveOrUpdateStatus(sensorId, newStatus);

        if (newStatus.startsWith("[testcase]"));
          ccs.trigger("199", "click");
     }

     @Override
     public void clearAllStatusCache() {
        cache.clear();
     }


     public void setCache(StatusCache cache) {
        this.cache = cache;
     }

     public void setCommandControl(ControlCommandService ccs)
     {
       System.out.println("---------------- Hooked CCS");
       this.ccs = ccs;
     }

}

