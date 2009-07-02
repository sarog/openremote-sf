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

package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.client.rpc.MyService;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Screen;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class MyServiceManager  extends BaseAbstractService<Activity> implements MyService{

   public List<Activity> getString() {
      List<Activity> activities = loadAll();
      for (Activity activity : activities) {
         System.out.println("------activity id : "+activity.getOid()+" screen count : "+activity.getScreens().size());
         genericDAO.initialize(activity);
      }
      System.out.println("activities size : "+activities.size());
      return activities;
   }
   
   public void addScreen() {
      Screen screen = new Screen();
      screen.setLabel("screen");
      genericDAO.save(screen);
      screen.setActivity(loadAll().get(0));
      loadAll().get(0).getScreens().add(screen);
   }
}
