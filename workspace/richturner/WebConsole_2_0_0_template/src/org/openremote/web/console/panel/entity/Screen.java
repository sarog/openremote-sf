/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.web.console.panel.entity;

import java.util.List;

/**
 * Screen contains id, name, layouts, background, gestures and pollingComponentsIds.
 */
public interface Screen {
   Integer getId();
   String getName();
   List<AbsoluteLayout> getAbsolute();
   List<GridLayout> getGrid();
   List<FormLayout> getForm();
   List<ListLayout> getList();
   List<Gesture> getGesture();
   Background getBackground();
   Integer getInverseScreenId();
   Boolean getLandscape();
   
   void setId(Integer id);
   void setName(String name);
   void setAbsolute(List<AbsoluteLayout> layouts);
   void setGrid(List<GridLayout> layouts);
   void setForm(List<FormLayout> layouts);
   void setList(List<ListLayout> layouts);
   void setGesture(List<Gesture> gestures);
   void setBackground(Background background);
   void setInverseScreenId(Integer id);
   void setLandscape(Boolean bool);
}
