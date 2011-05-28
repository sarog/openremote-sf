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
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.junit.Assert;

/**
 * Mockup for the agent so that it doesn't start/stop tomcat and uses fake properties
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class MockupAgent extends BackendCommandsAgent{
   protected String deployPath;
   protected String backupPath;
   protected String tmpPath;
   protected String logsPath;

   public MockupAgent() throws AgentException {
   }

   @Override
   protected void initLogging() {
      // let's not mess with logging
   }

   protected Properties getConfig() throws AgentException {
      Properties config = new Properties();
      config.setProperty("backend.rest.url", "http://fake-backend/beehive/rest");
      config.setProperty("backend.user", "user");
      config.setProperty("backend.password", "pass");
      config.setProperty("controller.command", "noop");
      config.setProperty("controller.url", "http://fake-controller");
      deployPath = makeTmpDir();
      config.setProperty("controller.deploy.path", deployPath);
      logsPath = makeTmpDir();
      config.setProperty("controller.logs.path", logsPath);
      backupPath = makeTmpDir();
      config.setProperty("controller.backup.path", backupPath);
      tmpPath = makeTmpDir();
      config.setProperty("tmp.path", tmpPath);
      return config;
   }

   private String makeTmpDir() throws AgentException {
      File deployFile;
      try {
         deployFile = File.createTempFile("test", "dir");
         deployFile.delete();
         deployFile.mkdir();
         deployFile.deleteOnExit();
         return deployFile.getAbsolutePath();
      } catch (IOException e) {
         throw new AgentException("Failed to create tmp dir", e);
      }
   }

   protected void makeMockDir(String path) {
      try {
         FileOutputStream os = new FileOutputStream(new File(path, "file3"));
         os.write(new byte[]{0,1,2,3,4});
         os.flush();
         os.close();
         new File(path, "dir2").mkdir();
         os = new FileOutputStream(new File(path, "dir2/file4"));
         os.write(new byte[]{0,1,2,3,4,5});
         os.flush();
         os.close();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   
   protected void checkZippedMockDir(File file){
      JarFile war;
      try {
         war = new JarFile(file);
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
   protected void runTomcat(String command) throws AgentException {
      // Do nothing
   }
   
   @Override
   protected void shutdownTomcat() throws AgentException {
      // Do nothing
   }
}