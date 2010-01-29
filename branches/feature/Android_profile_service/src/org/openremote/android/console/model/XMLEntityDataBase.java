package org.openremote.android.console.model;

import java.util.HashMap;

import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.TabBar;
import org.openremote.android.console.bindings.XScreen;

public class XMLEntityDataBase {
   public static TabBar globalTabBar = new TabBar();
   public static final HashMap<Integer, Group> groups = new HashMap<Integer, Group>();
   public static final HashMap<Integer, XScreen> screens = new HashMap<Integer, XScreen>();
   
   public static Group getFirstGroup() {
      if (!groups.isEmpty()) {
         return groups.get(groups.keySet().iterator().next());
      }
      return null;
   }
}
