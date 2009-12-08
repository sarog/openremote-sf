/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import org.openremote.modeler.domain.BusinessEntity;

/**
 * parent for UIButton,UISwich,UIGrid... 
 * @author Javen
 *
 */
@SuppressWarnings("serial")
public abstract class UIComponent extends BusinessEntity {
   public UIComponent() {
   }

   public UIComponent(long id) {
      super(id);
   }

   public String getName() {
      return "UIComponent";
   }
   public abstract void transImagePathToRelative(String relativeSessionFolderPath);

   /*
    * Generate the xml content which used in panel.xml
    */
   public abstract String getPanelXml();

}
