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
package org.openremote.android.console.bindings;

import org.openremote.android.console.model.XMLEntityDataBase;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class Component extends BusinessEntity {

   private int componentId;
   public static Component buildWithXML(Node node) {
      Component component = null;
      if (LABEL.equals(node.getNodeName())) {
         component = new Label(node);
         XMLEntityDataBase.labels.put(component.getComponentId(), (Label) component);
      } else if(IMAGE.equals(node.getNodeName())) {
         component = new Image(node);
      } else {
         return Control.buildWithXML(node);
      }
      return component;
   }
   public int getComponentId() {
      return componentId;
   }
   public void setComponentId(int componentId) {
      this.componentId = componentId;
   }

}
