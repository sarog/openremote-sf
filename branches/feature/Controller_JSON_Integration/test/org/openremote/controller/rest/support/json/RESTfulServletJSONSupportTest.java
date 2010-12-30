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

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.TestConstraint;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.FileUtilOnlyForTest;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.SecurityUtil;
import org.xml.sax.SAXException;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * This is responsible for testing if the requested data is valid JSONP-format data.
 * 
 * @author handy.wang 2010-06-29
 *
 */
public class RESTfulServletJSONSupportTest extends TestCase {
	
   private String panelXmlPath;
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
	@Before
   public void setup() {
      String panelXmlFixturePath = this.getClass().getClassLoader().getResource(
            TestConstraint.FIXTURE_DIR_OF_RESTFUL_SERVICE_JSON_SUPPORT + Constants.PANEL_XML).getFile();
      panelXmlPath = PathUtil.addSlashSuffix(ConfigFactory.getCustomBasicConfigFromDefaultControllerXML()
            .getResourcePath())
            + Constants.PANEL_XML;
      if (new File(panelXmlPath).exists()) {
         new File(panelXmlPath).renameTo(new File(panelXmlPath + ".bak"));
      }
      FileUtilOnlyForTest.copyFile(panelXmlFixturePath, panelXmlPath);

   }

   @After
   public void tearDown() {
      if (new File(panelXmlPath + ".bak").exists()) {
         new File(panelXmlPath + ".bak").renameTo(new File(panelXmlPath));
      } else {
         FileUtilOnlyForTest.deleteFile(panelXmlPath);
      }
   }

   @Test
   public void testGetPanelsJSONData() throws SAXException, Exception {
      doTest("/controller/rest/panels?" + Constants.CALLBACK_PARAM_NAME + "=fun", "/controller/rest/panels");
   }
   
   @Test
   public void testGetProfileJSONData() throws SAXException, Exception {
      doTest("/controller/rest/panel/father?" + Constants.CALLBACK_PARAM_NAME + "=fun", "/controller/rest/panel/father");
   }
   
   private void doTest(String actualJSONDataURL, String expectedXMLDataURL) throws Exception, SAXException {
      try {
         WebConversation wc = new WebConversation();
         
         WebRequest jsonDataRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + TestConstraint.WEBAPP_PORT
               + actualJSONDataURL);
         WebResponse jsonDataResponse = wc.getResponse(jsonDataRequest);
         String actual = jsonDataResponse.getText();
         
         WebRequest xmlDataRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + TestConstraint.WEBAPP_PORT
               + expectedXMLDataURL);
         WebResponse xmlDataResponse = wc.getResponse(xmlDataRequest);
         String xml = xmlDataResponse.getText();
         String expected = JSONTranslator.doTransalteXMLToJSONString("application/json", null, xml);
         expected = "fun" + " && " + "fun" + "(" + expected + ")";
         
         Assert.assertEquals(expected, actual);
      } catch (HttpException e) {
         logger.info(e);
      }
   }

}
