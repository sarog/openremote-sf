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

import java.util.ArrayList;

import org.openremote.modeler.client.utils.IDUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class MacroDTOTest {

  @Test
  public void testMacroDTOsEqual() {
    MacroDTO macro1 = new MacroDTO();
    MacroDTO macro2 = new MacroDTO();
    
    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");

    macro1.setOid(IDUtil.nextID());
    macro2.setOid(macro1.getOid());

    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");

    macro1.setDisplayName("Name");
    macro2.setDisplayName("Name");

    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");

    MacroItemDTO item = new MacroItemDTO("Item", MacroItemType.Command);
    ArrayList<MacroItemDTO> items = new ArrayList<MacroItemDTO>();
    items.add(item);
    
    macro1.setItems(items);
    macro2.setItems(items);
    
    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");
  }
  
  @Test
  public void testMacroDTOsNotEqual() {
    MacroItemDTO item = new MacroItemDTO("Item", MacroItemType.Command);
    ArrayList<MacroItemDTO> items = new ArrayList<MacroItemDTO>();
    items.add(item);

    MacroDTO macro1 = new MacroDTO();
    macro1.setOid(IDUtil.nextID());
    macro1.setDisplayName("Name");
    macro1.setItems(items);

    MacroDTO macro2 = new MacroDTO();
    macro2.setOid(macro1.getOid());
    macro2.setDisplayName("Name");
    macro2.setItems(items);
    
    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");

    macro2.setOid(null);
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, second id is not set");

    macro2.setOid(IDUtil.nextID());
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, id is different");

    macro2.setOid(macro1.getOid());
    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");
    
    macro2.setDisplayName(null);
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, second displayName is not set");

    macro2.setDisplayName("Name 2");
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, displayName is different");

    macro2.setDisplayName("Name");
    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");
    
    MacroItemDTO item2 = new MacroItemDTO("Item 2", MacroItemType.Command);
    ArrayList<MacroItemDTO> items2 = new ArrayList<MacroItemDTO>();
    
    macro2.setItems(null);
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, second items is not set");

    macro2.setItems(items2);
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, items are different");

    items2.add(item2);
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, items are different");
    
    items2.add(item);
    Assert.assertFalse(macro1.equalityEquals(macro2), "Expected the MacroDTO to be different, items are different");

    items2.remove(item2);
    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");
  }

  @Test
  public void testConstructor() {
    MacroDTO macro1 = new MacroDTO();
    macro1.setOid(IDUtil.nextID());
    macro1.setDisplayName("Name");

    MacroDTO macro2 = new MacroDTO(macro1.getOid(), "Name");

    Assert.assertTrue(macro1.equalityEquals(macro2), "Expected the MacroDTO to be equal");
  }
}
