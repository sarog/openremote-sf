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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class Image extends BusinessEntity {

   private String src;
   private String style;
   
   public Image(Node node) {
      NamedNodeMap nodeMap = node.getAttributes();
      if (nodeMap.getNamedItem("src") != null) {
         this.src = nodeMap.getNamedItem("src").getNodeValue();
      }
      // TODO: parse sub nodes(sensory/include).
   }
   public String getSrc() {
      return src;
   }
   public String getStyle() {
      return style;
   }
   
   
}
