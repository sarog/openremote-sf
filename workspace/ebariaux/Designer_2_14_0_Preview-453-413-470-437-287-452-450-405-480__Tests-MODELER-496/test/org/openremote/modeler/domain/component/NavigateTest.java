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

// EBR - 11-Apr-2013 : All tests are currently disabled as Navigate does not implement equality test

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class NavigateTest {

  @Test(enabled=false)
  public void testNavigatesEqual() {
    Navigate navigate1 = new Navigate();
    navigate1.setOid(IDUtil.nextID());
    
    Navigate navigate2 = new Navigate();
    navigate2.setOid(navigate1.getOid());
    
    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");

    navigate1.setToGroup(1L);
    navigate2.setToGroup(1L);

    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");

    navigate1.setToScreen(2L);
    navigate2.setToScreen(2L);

    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");
  }

  @Test(enabled=false)
  public void testLogicalNavigatesEqual() {
    Navigate navigate1 = new Navigate();
    navigate1.setOid(IDUtil.nextID());
    navigate1.setToLogical(ToLogicalType.login);
    
    Navigate navigate2 = new Navigate();
    navigate2.setOid(navigate1.getOid());
    navigate2.setToLogical(ToLogicalType.login);

    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");
  }

  @Test(enabled=false)
  public void testNavigatesNotEqual() {
    Navigate navigate1 = new Navigate();
    navigate1.setOid(IDUtil.nextID());
    navigate1.setToGroup(1L);
    navigate1.setToScreen(2L);
    
    Navigate navigate2 = new Navigate();
    navigate2.setOid(navigate1.getOid());
    navigate2.setToGroup(1L);
    navigate2.setToScreen(2L);

    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");

    navigate2.setOid(IDUtil.nextID());
    Assert.assertFalse(navigate1.equals(navigate2), "Expected the Navigates to be different, id is different");

    navigate2.setOid(navigate1.getOid());
    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");

    navigate2.setToGroup(2L);
    Assert.assertFalse(navigate1.equals(navigate2), "Expected the Navigates to be different, toGroup is different");
    
    navigate2.setToGroup(1L);
    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");

    navigate2.setToScreen(1L);
    Assert.assertFalse(navigate1.equals(navigate2), "Expected the Navigates to be different, toScreen is different");

    Navigate emptyNavigate = new Navigate();
    emptyNavigate.setOid(navigate1.getOid());
    Assert.assertFalse(navigate1.equals(emptyNavigate), "Expected the Navigates to be different, second navigate is empty");
    
    Navigate logicalNavigate = new Navigate();
    logicalNavigate.setOid(navigate1.getOid());
    logicalNavigate.setToLogical(ToLogicalType.login);
    Assert.assertFalse(navigate1.equals(logicalNavigate), "Expected the Navigates to be different, second navigate is logical");    
  }
  
  @Test(enabled=false)
  public void testLogicalNavigatesNotEqual() {
    Navigate navigate1 = new Navigate();
    navigate1.setOid(IDUtil.nextID());
    navigate1.setToLogical(ToLogicalType.login);
    
    Navigate navigate2 = new Navigate();
    navigate2.setOid(navigate1.getOid());
    navigate2.setToLogical(ToLogicalType.login);

    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");
    
    navigate2.setOid(IDUtil.nextID());
    Assert.assertFalse(navigate1.equals(navigate2), "Expected the Navigates to be different, id is different");

    navigate2.setOid(navigate1.getOid());
    Assert.assertEquals(navigate1, navigate2, "Expected the Navigates to be equal");
    
    navigate2.setToLogical(ToLogicalType.nextScreen);
    Assert.assertFalse(navigate1.equals(navigate2), "Expected the Navigates to be different, toLogical is different");
    
    Navigate emptyNavigate = new Navigate();
    emptyNavigate.setOid(navigate1.getOid());
    Assert.assertFalse(navigate1.equals(emptyNavigate), "Expected the Navigates to be different, second navigate is empty");
  }

}
