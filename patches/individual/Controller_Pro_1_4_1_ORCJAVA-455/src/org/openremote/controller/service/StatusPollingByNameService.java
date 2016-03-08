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
 * Service interface that is used used when polling sensor values by sensor name.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public interface StatusPollingByNameService
{
  /**
   * Converts sensor names to sensor ID's.
   *
   * @param   sensorNames  sensor names
   *
   * @return  map that contains the sensor ID for each sensor name. Note that if a
   *          sensor with the given name does not exist, the map contains null
   *          as sensor ID
   */
  public Map<String, Integer> getSensorIDsFromNames(Set<String> sensorNames);
}
