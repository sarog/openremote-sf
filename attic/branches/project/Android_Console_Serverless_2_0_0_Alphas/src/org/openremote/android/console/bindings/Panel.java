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
package org.openremote.android.console.bindings;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import org.openremote.android.console.model.XMLEntityDataBase;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Panel represents a Panel from panel.xml.  It parses itself from xml
 *
 *      <a href="mailto:marcf@openremote.org">Marc Fleury</a>
 */

@SuppressWarnings("serial")
public class Panel extends BusinessEntity{

   private int panelId;
   private String name;
   private LinkedList<Group> groups;
   
   /*
    * A Panel holds Groups
    * 
    * The groups in XMLEntityDatabase have been initialized before we get here
    * 
    */
   public Panel(Node node) {
      groups = new LinkedList<Group>();
      
      NamedNodeMap nodeMap = node.getAttributes();
      this.panelId = Integer.valueOf(nodeMap.getNamedItem("id").getNodeValue());
      this.name = nodeMap.getNamedItem("name").getNodeValue();
      NodeList nodeList = node.getChildNodes();
      int childNum = nodeList.getLength();
      for (int i = 0; i < childNum; i++) {
         Node childNode = nodeList.item(i);
         if(childNode.getNodeType() == Node.ELEMENT_NODE) {
        	 if ("include".equals(childNode.getNodeName())) {
        		 groups.add(XMLEntityDataBase.groups.get(Integer.valueOf(childNode.getAttributes().getNamedItem("ref").getNodeValue())));
            }
         }
      }
   }
   
   public LinkedList<Group> getGroups() {
	   return groups;
   }
   
   public int getPanelId() { return panelId;}
   
   public String getPanelName() { return name;}
   
}

