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
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UITabbarItemTest {

  @Test
  public void testTabbarItemsEqual() {
    UITabbarItem item1 = new UITabbarItem();
    UITabbarItem item2 = new UITabbarItem();
    
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal 1");
   
    item1.setOid(IDUtil.nextID());
    item2.setOid(item1.getOid());

    // id not taken into account for equality test
    item2.setOid(IDUtil.nextID());
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal 2");
    
    item1.setName("Item");
    item2.setName("Item");
    
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal 3");

    ImageSource imageSource = new ImageSource("Image");
    item1.setImage(imageSource);
    item2.setImage(imageSource);

    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal 4");
    
    Navigate navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToGroup(1L);
    navigate.setToScreen(2L);
    
    item1.setNavigate(navigate);
    item2.setNavigate(navigate);
    
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal 5");
  
    navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToLogical(ToLogicalType.login);

    item1.setNavigate(navigate);
    item2.setNavigate(navigate);
    
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal 6");
  }

  @Test
  public void testTabbarItemsNotEqual() {
    ImageSource imageSource1 = new ImageSource("Image 1");

    Navigate navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToGroup(1L);
    navigate.setToScreen(2L);

    UITabbarItem item1 = new UITabbarItem();
    item1.setOid(IDUtil.nextID());
    item1.setName("Item 1");
    item1.setImage(imageSource1);
    item1.setNavigate(navigate);
    
    UITabbarItem item2 = new UITabbarItem();
    item2.setOid(item1.getOid());
    item2.setName("Item 1");
    item2.setImage(imageSource1);
    item2.setNavigate(navigate);

    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal");

    item2.setName(null);
    Assert.assertFalse(item1.equals(item2), "Expected the TabbarItems to be different, name not set on second item");
    
    item2.setName("Item 2");
    Assert.assertFalse(item1.equals(item2), "Expected the TabbarItems to be different, name is different");
    
    item2.setName("Item 1");
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal");

    item2.setImage(null);
    Assert.assertFalse(item1.equals(item2), "Expected the TabbarItems to be different, image not set on second item");

    ImageSource imageSource2 = new ImageSource("Image 2");    
    item2.setImage(imageSource2);
    Assert.assertFalse(item1.equals(item2), "Expected the TabbarItems to be different, name is different");

    item2.setImage(imageSource1);
    Assert.assertEquals(item1, item2, "Expected the TabbarItems to be equal");
    
    item2.setNavigate(null);
    Assert.assertFalse(item1.equals(item2), "Expected the TabbarItems to be different, navigate not set on second item");
    
    Navigate navigate2 = new Navigate();
    navigate2.setOid(IDUtil.nextID());
    navigate2.setToGroup(2L);
    navigate2.setToScreen(3L);
    
    item2.setNavigate(navigate2);
    Assert.assertFalse(item1.equals(item2), "Expected the TabbarItems to be different, navigate is different");
  }

}
