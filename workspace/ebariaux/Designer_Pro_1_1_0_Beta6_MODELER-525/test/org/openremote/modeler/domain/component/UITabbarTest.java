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
package org.openremote.modeler.domain.component;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.component.UITabbar.Scope;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UITabbarTest {

  @Test
  public void testTabarsEqual() {
    UITabbar tabbar1 = new UITabbar();
    UITabbar tabbar2 = new UITabbar();
    
    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");
    
    tabbar1.setOid(IDUtil.nextID());
    tabbar2.setOid(tabbar1.getOid());
    
    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");
    
    tabbar1.setScope(Scope.PANEL);
    tabbar2.setScope(Scope.PANEL);
    
    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");

    Navigate navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToGroup(1L);
    navigate.setToScreen(2L);

    UITabbarItem item = new UITabbarItem();
    item.setNavigate(navigate);
    
    tabbar1.addTabbarItem(item);
    tabbar2.addTabbarItem(item);
    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");
  }
  
  @Test
  public void testTabarsNotEqual() {
    Navigate navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToGroup(1L);
    navigate.setToScreen(2L);

    UITabbarItem item1 = new UITabbarItem();
    item1.setName("Item 1");
    item1.setNavigate(navigate);

    UITabbar tabbar1 = new UITabbar();
    tabbar1.setOid(IDUtil.nextID());
    tabbar1.setScope(Scope.PANEL);
    tabbar1.addTabbarItem(item1);
    
    UITabbar tabbar2 = new UITabbar();
    tabbar2.setOid(tabbar1.getOid());
    tabbar2.setScope(Scope.PANEL);
    tabbar2.addTabbarItem(item1);

    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");

    tabbar2.setOid(IDUtil.nextID());
    Assert.assertFalse(tabbar1.equals(tabbar2), "Expected the Tabbars to be different, id is different");

    tabbar2.setOid(tabbar1.getOid());
    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");
    
    tabbar2.setScope(null);
    Assert.assertFalse(tabbar1.equals(tabbar2), "Expected the Tabbars to be different, second scope is not set");

    tabbar2.setScope(Scope.GROUP);
    Assert.assertFalse(tabbar1.equals(tabbar2), "Expected the Tabbars to be different, scope is different");
    
    tabbar2.setScope(Scope.PANEL);
    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");

    UITabbarItem item2 = new UITabbarItem();
    item2.setName("Item 2");
    item2.setNavigate(navigate);
    
    tabbar2.addTabbarItem(item2);
    Assert.assertFalse(tabbar1.equals(tabbar2), "Expected the Tabbars to be different, items are different");
    
    tabbar2.removeTabarItem(item1);
    Assert.assertFalse(tabbar1.equals(tabbar2), "Expected the Tabbars to be different, items are different");

    tabbar2.addTabbarItem(item1);
    tabbar1.addTabbarItem(item2);
    Assert.assertFalse(tabbar1.equals(tabbar2), "Expected the Tabbars to be different, items are different (order)");
  }

  @Test
  public void testCopyConstructor() {
    Navigate navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToGroup(1L);
    navigate.setToScreen(2L);

    UITabbarItem item1 = new UITabbarItem();
    item1.setName("Item 1");
    item1.setNavigate(navigate);

    UITabbar tabbar1 = new UITabbar();
    tabbar1.setOid(IDUtil.nextID());
    tabbar1.setScope(Scope.PANEL);
    tabbar1.addTabbarItem(item1);

    UITabbar tabbar2 = new UITabbar(tabbar1);

    Assert.assertEquals(tabbar1, tabbar2, "Expected the Tabbars to be equal");
  }

}