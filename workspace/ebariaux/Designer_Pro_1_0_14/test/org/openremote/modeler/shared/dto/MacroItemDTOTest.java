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
package org.openremote.modeler.shared.dto;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class MacroItemDTOTest {

  @Test
  public void testMacroItemDTOsEqual() {
    MacroItemDTO item1 = new MacroItemDTO();
    MacroItemDTO item2 = new MacroItemDTO();
    
    Assert.assertEquals(item1, item2, "Expected the MacroItemDTO to be equal");
    
    item1.setDisplayName("Name");
    item2.setDisplayName("Name");

    Assert.assertEquals(item1, item2, "Expected the MacroItemDTO to be equal");

    item1.setType(MacroItemType.Command);
    item2.setType(MacroItemType.Command);

    Assert.assertEquals(item1, item2, "Expected the MacroItemDTO to be equal");
  }
  
  @Test
  public void testMacroItemDTOsNotEqual() {
    MacroItemDTO item1 = new MacroItemDTO();
    item1.setDisplayName("Name");
    item1.setType(MacroItemType.Command);

    MacroItemDTO item2 = new MacroItemDTO();
    item2.setDisplayName("Name");
    item2.setType(MacroItemType.Command);

    Assert.assertEquals(item1, item2, "Expected the MacroItemDTO to be equal");
    
    item2.setDisplayName(null);
    Assert.assertFalse(item1.equals(item2), "Expected the MacroItemDTO to be different, second displayName is not set");

    item2.setDisplayName("Name 2");
    Assert.assertFalse(item1.equals(item2), "Expected the MacroItemDTO to be different, displayName is different");
   
    item2.setDisplayName("Name");
    Assert.assertEquals(item1, item2, "Expected the MacroItemDTO to be equal");
    
    item2.setType(null);
    Assert.assertFalse(item1.equals(item2), "Expected the MacroItemDTO to be different, second type is not set");

    item2.setType(MacroItemType.Delay);
    Assert.assertFalse(item1.equals(item2), "Expected the MacroItemDTO to be different, type is different");
  }
  
  @Test
  public void testConstructor() {
    MacroItemDTO item1 = new MacroItemDTO();
    item1.setDisplayName("Name");
    item1.setType(MacroItemType.Command);

    MacroItemDTO item2 = new MacroItemDTO("Name", MacroItemType.Command);

    Assert.assertEquals(item1, item2, "Expected the MacroItemDTO to be equal");
  }

}
