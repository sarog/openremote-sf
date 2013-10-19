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
import org.openremote.modeler.domain.Cell;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UIGridTest {
  
  @Test
  public void testGridsEqual() {
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    label.setColor("red");
    label.setFontSize(12);
    
    Cell cell = new Cell(IDUtil.nextID());
    cell.setPosX(1);
    cell.setPosY(2);
    cell.setColspan(1);
    cell.setRowspan(1);
    cell.setUiComponent(label);
    
    UIGrid grid1 = new UIGrid();
    grid1.setOid(IDUtil.nextID());
    grid1.setTop(10);
    grid1.setLeft(11);
    grid1.setWidth(12);
    grid1.setHeight(13);
    grid1.setColumnCount(2);
    grid1.setRowCount(3);
  
    UIGrid grid2 = new UIGrid();
    grid2.setOid(grid1.getOid());
    grid2.setTop(10);
    grid2.setLeft(11);
    grid2.setWidth(12);
    grid2.setHeight(13);
    grid2.setColumnCount(2);
    grid2.setRowCount(3);
    
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");

    grid1.addCell(cell);
    grid2.addCell(cell);

    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");

    // id not taken into account for equality test
    grid2.setOid(IDUtil.nextID());
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");
  }

  @Test
  public void testGridsNotEqual() {
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    label.setColor("red");
    label.setFontSize(12);
    
    Cell cell1 = new Cell(IDUtil.nextID());
    cell1.setPosX(1);
    cell1.setPosY(2);
    cell1.setColspan(1);
    cell1.setRowspan(1);
    cell1.setUiComponent(label);
    
    UIGrid grid1 = new UIGrid();
    grid1.setOid(IDUtil.nextID());
    grid1.setTop(10);
    grid1.setLeft(11);
    grid1.setWidth(12);
    grid1.setHeight(13);
    grid1.setColumnCount(2);
    grid1.setRowCount(3);
    grid1.addCell(cell1);
  
    UIGrid grid2 = new UIGrid();
    grid2.setOid(grid1.getOid());
    grid2.setTop(10);
    grid2.setLeft(11);
    grid2.setWidth(12);
    grid2.setHeight(13);
    grid2.setColumnCount(2);
    grid2.setRowCount(3);
    grid2.addCell(cell1);
    
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");
    
    grid2.setTop(20);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, top is different");
    
    grid2.setTop(10);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");
    
    grid2.setLeft(21);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, left is different");
    
    grid2.setLeft(11);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");

    grid2.setWidth(22);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, width is different");
    
    grid2.setWidth(12);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");
    
    grid2.setHeight(23);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, height is different");
    
    grid2.setHeight(13);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");

    grid2.setColumnCount(3);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, columnCount is different");
    
    grid2.setColumnCount(2);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");

    grid2.setRowCount(2);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, rowCount is different");
    
    grid2.setRowCount(3);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");

    grid2.removeCell(cell1);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, cells are different");
    
    Cell cell2 = new Cell(IDUtil.nextID());
    cell1.setPosX(2);
    cell1.setPosY(3);
    cell1.setColspan(1);
    cell1.setRowspan(1);
    cell1.setUiComponent(label);

    grid2.addCell(cell2);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, cells are different");
    
    grid2.removeCell(cell1);
    Assert.assertFalse(grid1.equals(grid2), "Expected the Grids to be different, cells are different");
  }
  
  @Test
  public void testCopyConstructor() {
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    label.setColor("red");
    label.setFontSize(12);
    
    Cell cell = new Cell(IDUtil.nextID());
    cell.setPosX(1);
    cell.setPosY(2);
    cell.setColspan(1);
    cell.setRowspan(1);
    cell.setUiComponent(label);
    
    UIGrid grid1 = new UIGrid();
    grid1.setOid(IDUtil.nextID());
    grid1.setTop(10);
    grid1.setLeft(11);
    grid1.setWidth(12);
    grid1.setHeight(13);
    grid1.setColumnCount(2);
    grid1.setRowCount(3);
    grid1.addCell(cell);

    UIGrid grid2 = new UIGrid(grid1);
    Assert.assertEquals(grid1, grid2, "Expected the Grids to be equal");
  }
}
