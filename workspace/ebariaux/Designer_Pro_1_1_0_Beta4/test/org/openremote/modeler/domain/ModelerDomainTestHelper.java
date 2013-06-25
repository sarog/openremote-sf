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
package org.openremote.modeler.domain;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.component.UIComponent;

/**
 * When setting up object model (and validating results) for tests, same code
 * comes over and over again.
 * It is centralized in this helper class to avoid duplication in individual test classes.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ModelerDomainTestHelper {

  public static Absolute createAbsolute(int baseValue, UIComponent uiComponent) {
    Absolute abs = new Absolute(IDUtil.nextID());
    abs.setPosition(baseValue, baseValue + 1);
    abs.setSize(baseValue + 2, baseValue + 3);
    abs.setUiComponent(uiComponent);
    return abs;
  }
  
  public static Cell createCell(int left, int right, UIComponent uiComponent) {
    Cell cell = new Cell();
    cell.setPosX(left);
    cell.setPosY(right);
    cell.setColspan(1);
    cell.setRowspan(1);
    cell.setUiComponent(uiComponent);
    return cell;
  }

}
