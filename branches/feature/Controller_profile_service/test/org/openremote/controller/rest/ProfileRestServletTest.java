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
package org.openremote.controller.rest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openremote.controller.TestConstraint;
import org.openremote.controller.utils.SecurityUtil;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;


public class ProfileRestServletTest {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   @Test
   public void requestAllPanels() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + TestConstraint.WEBAPP_PORT + "/controller/rest/panels");
      try {
         WebResponse wr = wc.getResponse(request);
         System.out.println(wr.getText());
      } catch (HttpException e) {
         if (e.getResponseCode() == 504) {
            logger.info("Polling request was  timeout.");
         }
      }
   }

}
