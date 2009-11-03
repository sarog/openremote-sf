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
package org.openremote.controller.status_cache;

import org.apache.log4j.Logger;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Handy.Wang 2009-10-28
 */
public class StatusAndPollingTest extends TestCase {

   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   public void testCase1() throws Exception {
      WebConversation wc = new WebConversation();
      WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/status/1");
      logger.info("The status result was : \n" + wr.getText());
      for (int i = 1; i <= 6; i++) {
         wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1");
         logger.info("The " + i + " result was : \n" + wr.getText());
      }
   }

   public void testCase2() throws Exception {
      for(int i = 1; i <= 6; i++) {
         WebConversation wc = new WebConversation();
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/2");
         logger.info("The " + i + " result was : \n" + wr.getText());
         Thread.sleep(10000);
      }
   }
   
   public void testCase3() throws Exception {
      for(int i = 1; i <= 6; i++) {
         WebConversation wc = new WebConversation();
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/3");
         logger.info("The " + i + " result was : \n" + wr.getText());
      }
   }

   public void testCase4() throws Exception {
      for(int i = 1; i <= 6; i++) {
         WebConversation wc = new WebConversation();
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/4");
         logger.info("The " + i + " result was : \n" + wr.getText());
      }
   }
}
