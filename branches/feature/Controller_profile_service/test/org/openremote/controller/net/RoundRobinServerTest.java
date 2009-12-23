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
package org.openremote.controller.net;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * All usecase test for RoundRobin Server. <br /><br />
 * 
 * This RoundRobinServer is responsible for <b>START UP UDP SERVER</b> firstly,<br /> 
 * and then <b>RESPONSE CONTROLLER URL</b> to RoundRobin client.
 * 
 * @author Handy.Wang 2009-12-22
 */
public class RoundRobinServerTest {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());

   @Before
   public void setUp() throws Exception {
      new Thread(new RoundRobinServer("A")).start();
      new Thread(new RoundRobinServer("A")).start();
      new Thread(new RoundRobinServer("A")).start();
      new Thread(new RoundRobinServer("A")).start();
      new Thread(new RoundRobinServer("B")).start();
   }
   
   @After
   public void tearDown() {
   }
   
   /**
    * Test if RoundRobin server is alive.
    */
   @Test
   public void testIsRoundRobinSeverALive() {
      RoundRobinClient rrc = new RoundRobinClient("A");
      int acturalGroupMembersSize = rrc.getGroupMemberURLs().size();
      Assert.assertTrue("expected groupmembers size = 4 but size = " + acturalGroupMembersSize , acturalGroupMembersSize == 4);
      for (String groupName : rrc.getGroupMemberURLs()) {
         logger.info(groupName);
      }
   }
}
