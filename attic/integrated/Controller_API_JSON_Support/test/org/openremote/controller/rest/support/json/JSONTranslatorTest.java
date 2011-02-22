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
package org.openremote.controller.rest.support.json;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.TestConstraint;

/**
 * 
 * @author handy 2010-07-01
 * 
 * This is responsible for translating xml data to json data.
 *
 */
public class JSONTranslatorTest extends TestCase {

//   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   @Test
   public void testTranslate() throws IOException {
      String expectedXMLFilePath = this.getClass().getClassLoader().getResource(
            TestConstraint.FIXTURE_DIR_OF_RESTFUL_SERVICE_JSON_SUPPORT + "expected.xml").getFile();
      File expectedXMLFile = new File(expectedXMLFilePath);
      String expectedXML = FileUtils.readFileToString(expectedXMLFile);
      String expectedJSONStr = JSONTranslator.doTransalteXMLToJSONString(Constants.HTTP_HEADER_ACCEPT_JSON_TYPE, null, expectedXML);
      
      String actualXMLFilePath = this.getClass().getClassLoader().getResource(
            TestConstraint.FIXTURE_DIR_OF_RESTFUL_SERVICE_JSON_SUPPORT + "actual.xml").getFile();
      File actualXMLFile = new File(actualXMLFilePath);
      String actualXML = FileUtils.readFileToString(actualXMLFile);
      String actualJSONStr = JSONTranslator.doTransalteXMLToJSONString(Constants.HTTP_HEADER_ACCEPT_JSON_TYPE, null, actualXML);
      
      Assert.assertEquals(expectedJSONStr, actualJSONStr);
   }
}
