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
package org.openremote.android.console.model;

import java.io.Serializable;
import org.openremote.android.console.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class UserCache implements Serializable {

   private static final long serialVersionUID = Constants.CACHE_VERSION;
   private static final String USER_CACHE = "userCache";
   private static final String LAST_GROUP_ID = "lastGroupId";
   private static final String LAST_Screen_ID = "lastScreenId";
   
   public static void saveLastGroupIdAndScreenId(Context context, int lastGroupId, int lastScreenId) {
      SharedPreferences.Editor editor = context.getSharedPreferences(USER_CACHE, 0).edit();
      editor.putInt(LAST_GROUP_ID, lastGroupId);
      editor.putInt(LAST_Screen_ID, lastScreenId);
      editor.commit();
   }
   
   public static int getLastGroupId(Context context) {
      return context.getSharedPreferences(USER_CACHE, 0).getInt(LAST_GROUP_ID, 0);
   }
   
   public static int getLastScreenId(Context context) {
      return context.getSharedPreferences(USER_CACHE, 0).getInt(LAST_Screen_ID, 0);
   }
}
