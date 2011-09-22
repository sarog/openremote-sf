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
package org.openremote.web.console.entity;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The label can set font size and color, change text by polling status.
 */
@SuppressWarnings("serial")
public class Link extends Entity {

   private String type;
   private String ref;
   
   public Link() {
   }
   
   public Link(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      setType(nodeMap.getNamedItem(TYPE).getNodeValue());
      setRef(nodeMap.getNamedItem(REF).getNodeValue());
   }
   
   public String getType() {
      return type;
   }
   
   public String getRef() {
      return ref;
   }
   
   public void setType(String type) {
      this.type = type;
   }

   public void setRef(String ref) {
      this.ref = ref;
   }
}
