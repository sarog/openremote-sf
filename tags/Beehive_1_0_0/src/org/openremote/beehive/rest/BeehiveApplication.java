/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Registers all the REST services User: allenwei Date: 2009-2-9 Time: 14:50:35
 */
public class BeehiveApplication extends Application {
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public BeehiveApplication() {
      singletons.add(new VendorRESTService());
      singletons.add(new ModelRESTService());
      singletons.add(new LIRCConfigFileRESTService());
      singletons.add(new RemoteSectionRESTService());
      singletons.add(new RemoteOptionRESTService());
      singletons.add(new CodeRESTService());

   }

   @Override
   public Set<Class<?>> getClasses() {
      return empty;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
