/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openremote.modeler.protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolDefinition implements Serializable{
   /**
    * 
    */
   private static final long serialVersionUID = -726881807822688804L;
   private String name;
   private List<ProtocolAttrDefinition> attrs = new ArrayList<ProtocolAttrDefinition>();;
   
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public List<ProtocolAttrDefinition> getAttrs() {
      return attrs;
   }
   public void setAttrs(List<ProtocolAttrDefinition> attrs) {
      this.attrs = attrs;
   }

   
   
   
}
