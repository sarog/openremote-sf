/*
 * OpenRemote, the Home of the Digital Home.
 *
 * Copyright 2008-2014, OpenRemote Inc. All rights reserved.
 *
 * The content of this file is available under commercial licensing only. It is *not* distributed
 * with any open source license. Please contact www.openremote.com for licensing.
 */
package org.openremote.controller.service;

import java.util.Map;
import java.util.Set;

/**
 * Service interface that is used to retrieve sensor values from the cache by sensor name.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public interface StatusCommandByNameService
{
  /**
   * Returns sensor values from the cache by sensor name.
   *
   * @param sensorNames  sensor names
   *
   * @return  map that contains the sensor values for the requested sensor names. Note that if a
   *          sensor with the requested name does not exist the special sensor value
   *          {@link org.openremote.controller.model.sensor.Sensor#UNKNOWN_STATUS} is returned.
   *
   * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
   */
  Map<String, String> readFromCache(Set<String> sensorNames);
}
