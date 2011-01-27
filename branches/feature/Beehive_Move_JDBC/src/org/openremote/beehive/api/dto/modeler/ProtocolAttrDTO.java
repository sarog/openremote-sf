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
package org.openremote.beehive.api.dto.modeler;

import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.modeler.Protocol;
import org.openremote.beehive.domain.modeler.ProtocolAttr;

/**
 * The Class is used for transmitting protocol attribute info.
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "attribute")
public class ProtocolAttrDTO extends BusinessEntityDTO {

   private String name;
   private String value;
   
   public String getName() {
      return name;
   }
   public String getValue() {
      return value;
   }
   public void setName(String name) {
      this.name = name;
   }
   public void setValue(String value) {
      this.value = value;
   }
   
   public ProtocolAttr toProtocolAttr(Protocol protocol) {
      ProtocolAttr protocolAttr = new ProtocolAttr();
      protocolAttr.setOid(getId());
      protocolAttr.setName(name);
      protocolAttr.setValue(value);
      protocolAttr.setProtocol(protocol);
      return protocolAttr;
   }
}
