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
package org.openremote.controller.statuscache;

import org.apache.log4j.Logger;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

import junit.framework.TestCase;

/**
 * This class is mainly used to test the <b>SkipStateTrack</b>.<br /><br />
 * 
 * There is a <b>ods file</b> named <b>SkipStateTrackTest.ods</b> in current directory.<br />
 * The file <b>SkipStateTrackTest.ods</b> contains several situations of skip-state tracking.<br />
 * So, the following methods depend on these situations in SkipStateTrackTest.ods descriped.<br /><br />
 * 
 * <b>NOTE: Start tomcat firstly.</b>
 * 
 * 
 */
public class StatusCacheTest extends TestCase {

   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   /**
    * <b>Situation1:</b><br />
    * 
    *  Not found time out record in TIME_OUT_TABLE during polling operation,<br />
    *  not timeout while observing and Getting the changed status at last.
    */
   public void testCase1() throws Exception {
      WebConversation wc = new WebConversation();
      for (int i = 1; i <= 6; i++) {
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1");
         logger.info("The " + i + " result was : \n" + wr.getText());
      }
   }

   /**
    * <b>Situation2:</b><br />
    * 
    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br /> 
    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
    * client gets 503 error at last.<br /><br />
    * 
    * <b>Second Polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br /> 
    * gets the changed status with <b>the value of column STATUS_CHANGED_IDS in TIME_OUT_TABLE</b> from<br /> 
    * CachedStatus table(currently it's simulated).<br /><br />
    * 
    * <b>NOTE:</b> This situation must work with method <b>simulateSkipStateTrackTestCase2</b> which was called<br />
    * while <b>InitCachedStatusDBListener</b> starting.
    */
   public void testCase2() throws Exception {
      for(int i = 1; i <= 6; i++) {
         WebConversation wc = new WebConversation();
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/2");
         logger.info("The " + i + " result was : \n" + wr.getText());
         Thread.sleep(10000);
      }
   }
   
   /**
    * <b>Situation3:</b><br /><br />
    * 
    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
    * client gets 503 error at last.<br /><br />
    * 
    * <b>Second polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
    * but the statuses which previous polling request care about didn't change.<br />
    * So, current polling request observes the change of statuses and gets the changed status at last.<br /><br />
    * 
    * <b>NOTE:</b> This situation must work with method <b>simulateSkipStateTrackTestCase3</b> which was called<br />
    * while <b>InitCachedStatusDBListener</b> starting.
    */
   public void testCase3() throws Exception {
      for(int i = 1; i <= 6; i++) {
         WebConversation wc = new WebConversation();
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/3");
         logger.info("The " + i + " result was : \n" + wr.getText());
      }
   }

   /**
    * <b>Situation4:</b><br /><br />
    * 
    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
    * client gets 503 error at last.<br /><br />
    * 
    * <b>Second polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
    * but the statuses which previous polling request care about didn't change.<br />
    * So, current polling request observes the change of statuses but timeout,<br />
    * client gets 503 error at last.<br /><br />
    */
   public void testCase4() throws Exception {
      for(int i = 1; i <= 6; i++) {
         WebConversation wc = new WebConversation();
         WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/4");
         logger.info("The " + i + " result was : \n" + wr.getText());
      }
   }
}
