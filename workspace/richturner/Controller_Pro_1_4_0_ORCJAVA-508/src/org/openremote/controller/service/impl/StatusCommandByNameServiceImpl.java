/*
 * OpenRemote, the Home of the Digital Home.
 *
 * Copyright 2008-2014, OpenRemote Inc. All rights reserved.
 *
 * The content of this file is available under commercial licensing only. It is *not* distributed
 * with any open source license. Please contact www.openremote.com for licensing.
 */
package org.openremote.controller.service.impl;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.service.StatusCommandByNameService;
import org.openremote.controller.statuscache.StatusCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service interface implementation that is used to request sensor values by name.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class StatusCommandByNameServiceImpl implements StatusCommandByNameService
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Sensor value cache.
   */
  private StatusCache statusCache;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs a service instance.
   *
   * @param statusCache  sensor value cache.
   */
  public StatusCommandByNameServiceImpl(StatusCache statusCache)
  {
    this.statusCache = statusCache;
  }

  /**
   * {@inheritDoc}
   */
  @Override public Map<String, String> readFromCache(Set<String> sensorNames)
  {
    Map<String, String> valueMap = new HashMap<String, String>(sensorNames.size());

    for (String curSensorName : sensorNames)
    {
      Integer sensorID = statusCache.sensorIDFromName(curSensorName);

      String value = Sensor.UNKNOWN_STATUS;

      if (sensorID != null)
      {
        value = statusCache.queryStatus(sensorID);
      }

      valueMap.put(curSensorName, value);
    }

    return valueMap;
  }
}
