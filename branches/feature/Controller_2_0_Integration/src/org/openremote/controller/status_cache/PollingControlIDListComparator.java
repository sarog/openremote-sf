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
package org.openremote.controller.status_cache;

import java.util.Comparator;

/**
 * Comparator for polling control id List.
 * 
 * @author Handy.Wang 2009-10-23
 */

public class PollingControlIDListComparator implements Comparator<String> {

   /* (non-Javadoc)
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(String controlIDStr1, String controlIDStr2) {
      // Avoid overflow in case of "( Integer.parseInt(obj1.toString()).intValue() - Integer.parseInt(obj2.toString()).intValue() )"
      Integer controlID1 = Integer.parseInt(controlIDStr1);
      Integer controlID2 = Integer.parseInt(controlIDStr2);
      if (controlID1.intValue() > controlID2.intValue()) {
         return 1;
      }
      if (controlID1.intValue() < controlID2.intValue()) {
         return -1;
      }
      return 0;
   }

}
