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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.openremote.modeler.service.ProtocolParser;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolParserTest {
   
   @Test
   public void testCorrectKNXXml() {
      ProtocolParser protocolParser = new ProtocolParser();
      File xmlFile = new File(getClass().getResource("testXml/knx.xml").getPath());
      ProtocolDefinition definition = null;
      for (Method method : ProtocolParser.class.getDeclaredMethods()) {
         if (method.getName().equals("parse")) {
            method.setAccessible(true);
            try {
               definition = (ProtocolDefinition) method.invoke(protocolParser, xmlFile);
            } catch (IllegalArgumentException e) {
               e.printStackTrace();
            } catch (IllegalAccessException e) {
               e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
         }
      }
      Assert.assertNotNull(definition);
      Assert.assertEquals(2,definition.getAttrs().size());
      Assert.assertEquals(0,definition.getAttrs().get(0).getValidators().size());
//      Assert.assertEquals(definition, getCorrectProtocolDefinition());
      
   }
   
   private ProtocolDefinition getCorrectProtocolDefinition() {
      ProtocolDefinition definition = new ProtocolDefinition();
      definition.setName("KNX");
      
      ProtocolAttrDefinition groupAddressAttr = new ProtocolAttrDefinition();
      groupAddressAttr.setName("groupAddress");
      groupAddressAttr.setLabel("Group Address");
      
      
      ProtocolAttrDefinition commandAttr = new ProtocolAttrDefinition();
      commandAttr.setName("command");
      commandAttr.setLabel("KNX Command");
      return definition;
   }
}
