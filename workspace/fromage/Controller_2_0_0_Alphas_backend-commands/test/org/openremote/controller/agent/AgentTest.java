/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.agent;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.agent.UpdateMockupAgent.State;

/**
 * Test some agent functionalities
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class AgentTest {
   
   @Test
   public void testCommand() throws AgentException, JSONException{
      CommandQueueMockupAgent agent = new CommandQueueMockupAgent();

      // works with one command
      agent.contents = "{'commands':{'command':{'@resource':'http://fake-backend/beehive/rest/user/stef/resources/update-1','@type':'update-controller','id':1}}}";
      JSONObject nextCommand = agent.getNextCommand();
      Assert.assertNotNull(nextCommand);
      Assert.assertEquals("update-controller", nextCommand.getString("@type"));
      Assert.assertEquals("http://fake-backend/beehive/rest/user/stef/resources/update-1", nextCommand.getString("@resource"));
      Assert.assertEquals("1", nextCommand.getString("id"));

      // works with multiple commands
      agent.contents = "{'commands':{'command':["
      +"{'@resource':'http://fake-backend/beehive/rest/user/stef/resources/update-1','@type':'update-controller','id':1},"
      +"{'@resource':'http://fake-backend/beehive/rest/user/stef/resources/update-2','@type':'update-controller','id':2}"
      +"]}}";
      nextCommand = agent.getNextCommand();
      Assert.assertNotNull(nextCommand);
      Assert.assertEquals("update-controller", nextCommand.getString("@type"));
      Assert.assertEquals("http://fake-backend/beehive/rest/user/stef/resources/update-1", nextCommand.getString("@resource"));
      Assert.assertEquals("1", nextCommand.getString("id"));
   }
   
   @Test
   public void testUpdate() throws AgentException{
      UpdateMockupAgent agent = new UpdateMockupAgent();
      agent.runOnce();
      Assert.assertEquals(UpdateMockupAgent.State.SUCCESS, agent.state);
      // make sure the tmp dir is clean as well
      File tmp = new File(agent.tmpPath);
      Assert.assertEquals(0, tmp.list().length);
   }

   @Test
   public void testUploadLogs() throws AgentException{
      UploadLogsMockupAgent agent = new UploadLogsMockupAgent();
      agent.runOnce();
      Assert.assertEquals(UploadLogsMockupAgent.State.SUCCESS, agent.state);
      // make sure the tmp dir is clean as well
      File tmp = new File(agent.tmpPath);
      Assert.assertEquals(0, tmp.list().length);
   }
}
