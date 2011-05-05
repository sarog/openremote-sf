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

import org.junit.Assert;

/**
 * State-testing mockup agent, to check that the update-controller command does what
 * we want.
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class UploadLogsMockupAgent extends MockupAgent {

   enum State {
      GET_COMMAND,
      ZIP_LOGS,
      UPLOAD_LOGS,
      ACK_COMMAND,
      SUCCESS
   }
   
   UploadLogsMockupAgent.State state = State.GET_COMMAND;
   
   public UploadLogsMockupAgent() throws AgentException {
      makeMockDir(logsPath);
   }

   @Override
   protected RESTCall makeRESTCall(String method, String url){
      switch(state){
      case ACK_COMMAND:
         Assert.assertEquals("DELETE", method);
         Assert.assertEquals("http://fake-backend/beehive/rest/command-queue/1", url);
         state = State.SUCCESS;
         return new MockRESTCall();
      case UPLOAD_LOGS:
         Assert.assertEquals("http://fake-backend/beehive/rest/user/stef/logs/1", url);
         state = State.ACK_COMMAND;
         return new MockRESTCall();
      }
      throw new AssertionError("Invalid state: "+state);
   }
   
   @Override
   protected RESTCall makeRESTCall(String url) throws AgentException {
      switch(state){
      case GET_COMMAND:
         Assert.assertEquals("http://fake-backend/beehive/rest/user/user/command-queue", url);
         state = State.ZIP_LOGS;
         return new MockRESTCall("{'commands':{'command':{'@resource':'http://fake-backend/beehive/rest/user/stef/logs/1','@type':'upload-logs','id':1}}}");
      }
      throw new AssertionError("Invalid state: "+state);
   }

   @Override
   protected File zipLogs() throws AgentException {
      if(state != State.ZIP_LOGS)
         throw new AssertionError("Invalid state: "+state);
      state = State.UPLOAD_LOGS;

      File zip = super.zipLogs();
      // make sure we have all the good stuff in there
      checkZippedMockDir(zip);
      return zip;
   }
}