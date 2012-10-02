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
package org.openremote.controller.device.protocol;

import java.util.LinkedHashSet;

/**
 * Abstract Protocol implementation, actual protocols need to extend this
 * and implement either Active or Passive or Passive Responsive Protocol
 * interfaces
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public abstract class ProtocolImpl implements Protocol {
   private ProtocolStatus status;
   private ProtocolParameters protocolParameters;
   
   public ProtocolImpl(ProtocolParameters protocolParameters) {
      this.protocolParameters = protocolParameters;
   }
   
   public ProtocolParameters getParameters() {
      return protocolParameters;
   }
   
   public String getParameterValue(String parameterName) {
      return protocolParameters.getParameterValue(parameterName);
   }
   
   public LinkedHashSet<String> getParameterValues(String parameterName) {
      return protocolParameters.getParameterValues(parameterName);
   }
   
   protected void setStatus(ProtocolStatus status) {
      this.status = status;
   }
   
   public ProtocolStatus getStatus() {
      return status;
   }   
}
