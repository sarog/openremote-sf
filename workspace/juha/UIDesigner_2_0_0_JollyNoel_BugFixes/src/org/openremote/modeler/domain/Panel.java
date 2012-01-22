/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import flexjson.JSON;

/**
 * TODO
 *
 * The Panel define the different device touch panel, such as iPhone panel, wall panel etc.
 * It includes name, groupRefs, global tabbarItems and touchPanelDefinition.
 */
public class Panel extends BusinessEntity
{

  // Serialization --------------------------------------------------------------------------------

  private static final long serialVersionUID = 6122936524433692761L;


  // Class Members --------------------------------------------------------------------------------

  private static int defaultNameIndex = 1;

  public static void increaseDefaultNameIndex()
  {
     defaultNameIndex++;
  }

  public static String getNewDefaultName()
  {
     return "panel" + defaultNameIndex;
  }


  // Instance Fields ------------------------------------------------------------------------------

  private String name;
  private List<GroupRef> groupRefs = new ArrayList<GroupRef>();
  private List<UITabbarItem> tabbarItems = new ArrayList<UITabbarItem>();
  private TouchPanelDefinition touchPanelDefinition;
  private UITabbar tabbar = null;


  // Instance Methods -----------------------------------------------------------------------------

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }


  public List<GroupRef> getGroupRefs()
  {
    return groupRefs;
  }

  public void setGroupRefs(List<GroupRef> groupRefs)
  {
    this.groupRefs = groupRefs;
  }

  public void addGroupRef(GroupRef groupRef)
  {
    groupRefs.add(groupRef);
  }

  public void insertGroupRef(GroupRef before, GroupRef target)
  {
    int index = groupRefs.indexOf(before);
    groupRefs.add(index, target);
  }

  public void removeGroupRef(GroupRef groupRef)
  {
    groupRefs.remove(groupRef);
  }

  public void clearGroupRefs()
  {
      groupRefs.clear();
  }


  public TouchPanelDefinition getTouchPanelDefinition()
  {
    return touchPanelDefinition;
  }

  public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition)
  {
    this.touchPanelDefinition = touchPanelDefinition;
  }



  public UITabbar getTabbar()
  {
    return tabbar;
  }

  public void setTabbar(UITabbar tabbar)
  {
    this.tabbar = tabbar;
  }

  public List<UITabbarItem> getTabbarItems()
  {
    return tabbarItems;
  }

  public void setTabbarItems(List<UITabbarItem> tabbarItems)
  {
    this.tabbarItems = tabbarItems;
  }


  /* (non-Javadoc)
   * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
   */
  @Transient public String getDisplayName()
  {
    TouchPanelCanvasDefinition canvas = touchPanelDefinition.getCanvas();

    return name + "(" + touchPanelDefinition.getName() + "," +
           canvas.getWidth() + "X" + canvas.getHeight() + ")";
  }


  public String getType()
  {
    if (touchPanelDefinition != null)
    {
      return touchPanelDefinition.getType();
    }

    return Constants.CUSTOM_PANEL;
  }

  /**
   * Gets the groups from groupRefs, a groupRef contain a group.
   *
   * @return the groups
   */
  @Transient @JSON(include = false)
  public List<Group> getGroups()
  {
    List<Group> groups = new ArrayList<Group>();

    for (GroupRef groupRef : groupRefs)
    {
       groups.add(groupRef.getGroup());
    }

    return groups;
  }

}
