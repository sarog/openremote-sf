/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.android.console.util;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Image Utility class.
 * 
 * @author Dan Cong
 */
public class ImageUtil {

   private ImageUtil() {
   }
   
   /**
    * Call native Drawable.createFromPath(pathName), but catch OutOfMemoryError and do nothing.
    * 
    * @param pathName
    *           path name
    * @return Drawable instance
    */
   public static Drawable createFromPathQuietly(String pathName) {
      Drawable d = null;
      try {
         d = Drawable.createFromPath(pathName);
      } catch (OutOfMemoryError e) {
         Log.e("OutOfMemoryError", pathName + ": bitmap size exceeds VM budget");
      }
      return d;
   }

}
