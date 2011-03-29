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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;

/**
 * State-testing mockup agent, to check that the update-controller command does what
 * we want.
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class UpdateMockupAgent extends MockupAgent {

   enum State {
      GET_COMMAND,
      DOWNLOAD_UPDATE,
      SHUTDOWN_TOMCAT,
      BACKUP_WAR,
      DELETE_PREVIOUS_WAR,
      INSTALL_WAR,
      START_TOMCAT,
      ACK_COMMAND,
      SUCCESS
   }
   
   UpdateMockupAgent.State state = State.GET_COMMAND;
   
   public UpdateMockupAgent() throws AgentException {
      makeMockDeploy();
   }

   @Override
   protected RESTCall makeRESTCall(String method, String url){
      switch(state){
      case ACK_COMMAND:
         Assert.assertEquals("DELETE", method);
         Assert.assertEquals("http://fake-backend/beehive/rest/command-queue/1", url);
         state = State.SUCCESS;
         return new MockRESTCall();
      }
      throw new AssertionError("Invalid state: "+state);
   }
   
   @Override
   protected RESTCall makeRESTCall(String url) throws AgentException {
      switch(state){
      case GET_COMMAND:
         Assert.assertEquals("http://fake-backend/beehive/rest/user/user/command-queue", url);
         state = State.DOWNLOAD_UPDATE;
         return new MockRESTCall("{'commands':{'update-command':{'@resource':'http://fake-backend/beehive/rest/user/stef/resources/update-1','@type':'update-controller','id':1}}}");
      case DOWNLOAD_UPDATE:
         Assert.assertEquals("http://fake-backend/beehive/rest/user/stef/resources/update-1", url);
         state = State.SHUTDOWN_TOMCAT;
         File war = makeMockWar();
         return new MockRESTCall(war);
      }
      throw new AssertionError("Invalid state: "+state);
   }
   
   private File makeMockWar() {
      try {
         File war = File.createTempFile("test", ".war");
         war.deleteOnExit();
         ZipOutputStream os = new ZipOutputStream(new FileOutputStream(war));
         ZipEntry entry = new ZipEntry("file1");
         os.putNextEntry(entry);
         os.write(new byte[]{0,1,2});
         entry = new ZipEntry("dir/file2");
         os.putNextEntry(entry);
         os.write(new byte[]{0,1,2,3});
         os.flush();
         os.close();
         return war;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void makeMockDeploy() {
      try {
         FileOutputStream os = new FileOutputStream(new File(deployPath, "file3"));
         os.write(new byte[]{0,1,2,3,4});
         os.flush();
         os.close();
         new File(deployPath, "dir2").mkdir();
         os = new FileOutputStream(new File(deployPath, "dir2/file4"));
         os.write(new byte[]{0,1,2,3,4,5});
         os.flush();
         os.close();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   protected void shutdownTomcat() throws AgentException {
      if(state != State.SHUTDOWN_TOMCAT)
         throw new AssertionError("Invalid state: "+state);
      state = State.BACKUP_WAR;
   }

   @Override
   protected void startTomcat() throws AgentException {
      if(state != State.START_TOMCAT)
         throw new AssertionError("Invalid state: "+state);
      state = State.ACK_COMMAND;
   }

   @Override
   protected void backupPreviousWar() throws AgentException {
      if(state != State.BACKUP_WAR)
         throw new AssertionError("Invalid state: "+state);
      state = State.DELETE_PREVIOUS_WAR;
      
      super.backupPreviousWar();
      // make sure the backup worked
      File backupDir = new File(backupPath);
      Assert.assertEquals(1, backupDir.listFiles().length);
      File backupWar = backupDir.listFiles()[0];
      JarFile war;
      try {
         war = new JarFile(backupWar);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      Assert.assertEquals(2, war.size());
      // first file
      ZipEntry entry = war.getEntry("file3");
      Assert.assertNotNull(entry);
      Assert.assertEquals(5, entry.getSize());
      // second file
      entry = war.getEntry("dir2/file4");
      Assert.assertNotNull(entry);
      Assert.assertEquals(6, entry.getSize());
   }
   
   @Override
   protected void deletePreviousWar() throws AgentException {
      if(state != State.DELETE_PREVIOUS_WAR)
         throw new AssertionError("Invalid state: "+state);
      state = State.INSTALL_WAR;

      super.deletePreviousWar();
      
      // make sure we deleted every deployed entry
      File deployDir = new File(deployPath);
      Assert.assertEquals(0, deployDir.listFiles().length);
   }

   @Override
   protected void installWar(File war) throws AgentException {
      if(state != State.INSTALL_WAR)
         throw new AssertionError("Invalid state: "+state);
      state = State.START_TOMCAT;
      
      super.installWar(war);
      // make sure the install  worked
      File deployDir = new File(deployPath);
      Assert.assertEquals(2, deployDir.listFiles().length);
      
      // first file
      File entry = new File(deployDir, "file1");
      Assert.assertNotNull(entry);
      Assert.assertEquals(3, entry.length());
      // second file
      entry = new File(deployDir, "dir/file2");
      Assert.assertNotNull(entry);
      Assert.assertEquals(4, entry.length());
   }
}