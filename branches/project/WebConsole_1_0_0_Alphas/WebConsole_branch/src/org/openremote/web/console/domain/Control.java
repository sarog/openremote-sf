/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.domain;

import org.w3c.dom.Node;

/**
 * The super class of control component, which include button, switch and slider.
 */
@SuppressWarnings("serial")
public class Control extends Component {

   /**
    * Builds the control component by parse component node, 
    * which include button, switch and slider.
    * 
    * @param node the node
    * 
    * @return the control component
    */
   public static Component buildWithXML(Node node) {
      Component component = null;
      if (node == null) {
         return null;
      }
      if (BUTTON.equals(node.getNodeName())) {
         component =  new Button(node);
      } else if (SWITCH.equals(node.getNodeName())) {
         component = new Switch(node);
      } else if (SLIDER.equalsIgnoreCase(node.getNodeName())) {
         component = new Slider(node);
      }
      return component;
   }
}
