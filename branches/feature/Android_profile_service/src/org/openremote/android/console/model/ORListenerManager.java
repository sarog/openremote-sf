/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.android.console.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ORListenerManager {

   private static ORListenerManager instance;
   private static Map<String, List<OREventListener>> eventListeners = new HashMap<String, List<OREventListener>>();
   
   private ORListenerManager() {
   }
   
   public void addOREventListener(String name, OREventListener listener) {
      List<OREventListener> listeners = null;
      if ((listeners = eventListeners.get(name)) == null) {
         listeners = new ArrayList<OREventListener>();
      }
      listeners.add(listener);

      eventListeners.put(name, listeners);
   }
   
   public void notifyOREventListener(String name, Object data) {
      if (eventListeners.get(name) == null) {
         return;
      }
      for (OREventListener listener : eventListeners.get(name)) {
         listener.handleEvent(new OREvent(data));
      }
   }
   
   public void deleteOREventListener(String name, OREventListener listener) {
      List<OREventListener> listeners = eventListeners.get(name);
      if (listeners != null) {
         listeners.remove(listener);
      }
   }
   
   public static synchronized ORListenerManager getInstance() {
      if ( instance == null) {
         instance = new ORListenerManager();
      }
      return instance;
   }
   
}
