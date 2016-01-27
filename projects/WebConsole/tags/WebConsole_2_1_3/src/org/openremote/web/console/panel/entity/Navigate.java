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
 * Forwards to screen or do other logical functions.
 * Includes to group, to screen, to previous screen , to next screen, back, login, logout and setting.
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public interface Navigate {
   String getTo();
   Integer getToGroup();
   Integer getToScreen();
   List<DataValuePairContainer> getData();
   
   void setTo(String to);
   void setToGroup(Integer toGroup);
   void setToScreen(Integer toScreen);
   void setData(List<DataValuePairContainer> dataValues);
}
