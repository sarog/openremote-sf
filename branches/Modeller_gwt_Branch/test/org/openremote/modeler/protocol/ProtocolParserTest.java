/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.openremote.modeler.protocol;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openremote.modeler.exception.ParseProtocolException;
import org.openremote.modeler.service.ProtocolParser;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ProtocolParserTest {

   @Test(expected = ParseProtocolException.class)
   public void testSchemaValidateFail() {
      ProtocolParser protocolParser = new ProtocolParser();
      protocolParser.setPath(getClass().getResource("testXml").getPath() + "/schemaValidateFail");
      protocolParser.parseXmls();
   }

   @Test
   public void testParseXmls() {
      ProtocolParser protocolParser = new ProtocolParser();
      protocolParser.setPath(getClass().getResource("testXml").getPath());
      Map<String, ProtocolDefinition> pros = new HashMap<String, ProtocolDefinition>();
      ProtocolDefinition definition = getCorrectProtocolDefinition();
      pros.put(definition.getName(), definition);

      Assert.assertEquals(pros, protocolParser.parseXmls());
   }

   private ProtocolDefinition getCorrectProtocolDefinition() {
      ProtocolDefinition definition = new ProtocolDefinition();
      definition.setName("KNX");

      ProtocolAttrDefinition groupAddressAttr = new ProtocolAttrDefinition();
      groupAddressAttr.setName("groupAddress");
      groupAddressAttr.setLabel("Group Address");
      ProtocolValidator allowBlank = new ProtocolValidator(ProtocolValidator.ALLOW_BLANK_TYPE, "false", null);
      ProtocolValidator regex = new ProtocolValidator(ProtocolValidator.REGEX_TYPE, "(\\d\\.){3}\\d",
            "group address should be 1.4.1.4");
      groupAddressAttr.getValidators().add(allowBlank);
      groupAddressAttr.getValidators().add(regex);
      definition.getAttrs().add(groupAddressAttr);

      ProtocolAttrDefinition commandAttr = new ProtocolAttrDefinition();
      commandAttr.setName("command");
      commandAttr.setLabel("KNX Command");
      ProtocolValidator allowBlank2 = new ProtocolValidator(ProtocolValidator.ALLOW_BLANK_TYPE, "false", null);
      ProtocolValidator maxLength2 = new ProtocolValidator(ProtocolValidator.MAX_LENGTH_TYPE, "10", null);
      ProtocolValidator regex2 = new ProtocolValidator(ProtocolValidator.REGEX_TYPE, "\\w*", null);
      commandAttr.getValidators().add(allowBlank2);
      commandAttr.getValidators().add(maxLength2);
      commandAttr.getValidators().add(regex2);
      definition.getAttrs().add(commandAttr);

      return definition;
   }
}
