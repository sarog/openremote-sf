/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.port;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * A factory for <code>PhysicalBus</code> instances.
 */
public class PortFactory {
   /**
    * A common log category name intended to be used across all classes related to Physical Bus implementation.
    */
   public final static String PHYSICALBUS_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "knx.ip";

   private final static Logger log = Logger.getLogger(PHYSICALBUS_LOG_CATEGORY);

   public static Port createPhysicalBus(String clazz) {
      try {
         Class<?> c = Class.forName(clazz);
         if (Port.class.isAssignableFrom(c)) {
            return (Port) c.newInstance();
         } else {
            return null;
         }
      } catch (Exception e) {
         log.error("Could not instantiate PhysicalBus", e);
         return null;
      }
   }
}
