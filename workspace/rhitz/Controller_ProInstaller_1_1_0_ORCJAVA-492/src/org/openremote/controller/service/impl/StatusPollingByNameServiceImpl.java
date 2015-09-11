/*
 * OpenRemote, the Home of the Digital Home.
 *
 * Copyright 2008-2014, OpenRemote Inc. All rights reserved.
 *
 * The content of this file is available under commercial licensing only. It is *not* distributed
 * with any open source license. Please contact www.openremote.com for licensing.
 */
package org.openremote.controller.service.impl;

import org.openremote.controller.service.StatusPollingByNameService;
import org.openremote.controller.statuscache.StatusCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service interface implementation that is used when polling sensor values by sensor names.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class StatusPollingByNameServiceImpl implements StatusPollingByNameService
{

  // Private Instance Fields ----------------------------------------------------------------------

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
  public StatusPollingByNameServiceImpl(StatusCache statusCache)
  {
    this.statusCache = statusCache;
  }


  // Implements StatusPollingByNameService --------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public Map<String, Integer> getSensorIDsFromNames(Set<String> sensorNames)
  {
    Map<String, Integer> map = new HashMap<String, Integer>(sensorNames.size());

    for (String curSensorName : sensorNames)
    {
      Integer sensorID = statusCache.sensorIDFromName(curSensorName);

      map.put(curSensorName, sensorID);
    }

    return map;
  }
}
