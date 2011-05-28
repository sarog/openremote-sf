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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openremote.controller.Constants;
import org.openremote.controller.bootstrap.Startup;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.ZipUtil;

/**
 * Main class for the agent which reads remote commands from Beehive.
 *  
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class BackendCommandsAgent {

   static private Logger log = Logger.getLogger(Constants.AGENT_LOG_CATEGORY);

   private int checkInterval;
   private String backendURL;
   private String backendUser;
   private String backendPassword;
   private String controllerIP;
   private int controllerPort;
   private String controllerCommand;
   private String controllerURL;
   private int controllerRetryInterval;
   private int controllerRetryAttemps;
   private String controllerDeployPath;
   private String controllerBackupPath;
   private String controllerLogsPath;
   private String tmpPath;

   public BackendCommandsAgent() throws AgentException{
      // first set up logging
      initLogging();
      loadConfig();
   }

   protected void initLogging() {
      Startup.redirectJULtoLog4j();
      Startup.doNotDelegateControllerLogsToParentHandlers();
   }

   //
   // Deal with configuration
   
   protected void loadConfig() throws AgentException {
      Properties config = getConfig();
      // initialise defaults and do some checks
      checkInterval = getIntProperty(config, "check.interval", 5000);
      backendURL = getRequiredProperty(config, "backend.rest.url");
      backendUser = getRequiredProperty(config, "backend.user");
      backendPassword = getRequiredProperty(config, "backend.password");
      controllerCommand = getRequiredProperty(config, "controller.command");
      controllerURL = getRequiredProperty(config, "controller.url");
      controllerDeployPath = getRequiredProperty(config, "controller.deploy.path");
      controllerLogsPath = getRequiredProperty(config, "controller.logs.path");
      checkPath(controllerDeployPath, "controller.deploy.path");
      controllerRetryInterval = getIntProperty(config, "controller.retry.interval", 5000);
      controllerRetryAttemps = getIntProperty(config, "controller.retry.attemps", 10);
      controllerBackupPath = config.getProperty("controller.backup.path", System.getProperty("java.io.tmpdir"));
      checkPath(controllerBackupPath, "controller.backup.path");
      tmpPath = config.getProperty("tmp.path", System.getProperty("java.io.tmpdir"));
      checkPath(tmpPath, "tmp.path");
      try {
         controllerIP = config.getProperty("controller.ip", InetAddress.getLocalHost().getHostAddress());
      } catch (UnknownHostException e) {
         throw new AgentException("Failed to get localhost IP", e);
      }
      controllerPort = getIntProperty(config, "controller.port", 8080);
   }

   protected Properties getConfig() throws AgentException {
      Properties config = new Properties();
      String configPath = "/agent-config.properties";
      InputStream configStream = getClass().getResourceAsStream(configPath );
      if(configStream == null)
         throw new AgentException("Configuration file not found in classpath: "+configPath);
      try {
         config.load(configStream);
      } catch (IOException e) {
         throw new AgentException("Failed to read configuration from "+configPath, e);
      }
      return config;
   }

   private void checkPath(String path, String what) throws AgentException {
      File file = new File(path);
      if(!file.exists())
         throw new AgentException(what+" path does not exist");
      if(!file.isDirectory())
         throw new AgentException(what+" path not a directory");
      if(!file.canWrite())
         throw new AgentException(what+" path not writable");
   }

   private String getRequiredProperty(Properties config, String key) throws AgentException {
      String val = config.getProperty(key);
      if(val == null)
         throw new AgentException("Missing required property: "+key);
      return val;
   }

   private int getIntProperty(Properties config, String key, int defaultValue) throws AgentException {
      String val = config.getProperty(key);
      if(val == null)
         return defaultValue;
      try{
         return Integer.parseInt(val);
      }catch(NumberFormatException x){
         throw new AgentException("Failed to read int value from property "+key+": "+x);
      }
   }

   //
   // Our main loop
   
   protected void run() throws AgentException {
      while(true){
         runOnce();
      }
   }

   protected void runOnce() throws AgentException {
      log.info("getting new command");
      JSONObject command = getNextCommand();
      if(command == null){
         log.info("sleeping for "+checkInterval+"ms");
         sleep(checkInterval);
         return;
      }
      String type;
      try {
         type = command.getString("@type");
      } catch (JSONException e) {
         throw new AgentException("JSON error", e);
      }
      if("update-controller".equals(type)){
         updateController(command);
      }else if("upload-logs".equals(type)){
            uploadLogs(command);
      }else if(type == null){
         throw new AgentException("Missing command type");
      }else{
         throw new AgentException("Unknown command type: "+type);
      }
   }

   //
   // Commands
   
   protected void updateController(JSONObject command) throws AgentException{
      String id = getID(command);
      String resource = getResource(command);
      
      File tmpFile = downloadUpdate(resource);
      
      // now let's say where we put it
      log.info("Saved resource at "+tmpFile.getAbsolutePath());
      
      shutdownTomcat();
      backupPreviousWar();
      deletePreviousWar();
      installWar(tmpFile);
      startTomcat();
      
      ackCommand(id);
   }

   protected void uploadLogs(JSONObject command) throws AgentException {
      String id = getID(command);
      String resource = getResource(command);
      
      File zip = zipLogs();
      try{
         uploadLogs(zip, resource);
      }finally{
         zip.delete();
      }
      
      ackCommand(id);
   }

   protected void uploadLogs(File zip, String resource) throws AgentException {
      log.info("uploading logs to "+resource);
      RESTCall call = makeRESTCall("POST", resource);
      FileInputStream is;
      try {
         is = new FileInputStream(zip);
      } catch (FileNotFoundException e) {
         throw new AgentException("Failed to open zip file for output", e);
      }
      try {
         call.invoke(is);
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            // we don't care do we?
            log.warn("Failed to close logs zip after reading", e);
         }
      }
   }

   protected File zipLogs() throws AgentException {
      File zip;
      try {
         zip = File.createTempFile("controller-logs-", ".zip", new File(tmpPath));
      } catch (IOException e) {
         throw new AgentException("Failed to create temporary file", e);
      }
      try {
         log.info("Saving logs from "+controllerLogsPath+" to "+zip.getAbsolutePath());
         ZipUtil.zip(controllerLogsPath, zip);
      } catch (IOException e) {
         // cleanup
         zip.delete();
         throw new AgentException("Failed to zip logs", e);
      }
      return zip;
   }

   protected void ackCommand(String id) throws AgentException {
      log.info("Acking command "+id);
      RESTCall call = makeRESTCall("DELETE", backendURL+"/command-queue/"+id);
      call.invoke();
   }

   protected void sleep(int millis) throws AgentException {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         throw new AgentException("Interrupted from sleep", e);
      }
   }

   //
   // Tomcat start/stop
   
   protected void runTomcat(String command) throws AgentException {
      try {
         ProcessBuilder builder = new ProcessBuilder(controllerCommand, command);
         Process process = builder.start();
         int exit = process.waitFor();
         if(exit != 0)
            throw new AgentException("Failed to "+command+" controller. Exit code "+exit);
      } catch (IOException e) {
         throw new AgentException("Failed to "+command+" controller", e);
      } catch (InterruptedException e) {
         throw new AgentException("Failed to "+command+" controller", e);
      }
   }

   protected void startTomcat() throws AgentException {
      // FIXME: make sure tomcat is not already up
      log.info("Starting up tomcat");
      runTomcat("start");
      log.info("Tomcat up");
      // wait for an OK from the controller
      int attempts = controllerRetryAttemps;
      while(attempts-- > 0){
         log.info("Trying to connect to controller at "+controllerURL);
         try{
            RESTCall call = makeRESTCall(controllerURL);
            call.invoke();
            // all good to go
            log.info("Controller up");
            return;
         }catch(AgentException x){
            // not up yet
         }
         log.info("Controller not up yet, trying "+attempts+" times in 5 seconds");
         sleep(controllerRetryInterval);
      }
      throw new AgentException("Controller failed to start");
   }

   protected void shutdownTomcat() throws AgentException {
      log.info("Shutting down tomcat");
      runTomcat("stop");
      // all went well
      // wait for tomcat to die
      int attempts = controllerRetryAttemps;
      while(attempts-- > 0){
         log.info("Trying to connect to tomcat at "+controllerIP+":"+controllerPort);
         try{
            new Socket(controllerIP, controllerPort);
         }catch(IOException x){
            log.info("Tomcat dead");
            return;
         }
         log.info("Tomcat not dead yet, trying "+attempts+" times in 5 seconds");
         sleep(controllerRetryInterval);
      }
      throw new AgentException("Tomcat failed to stop");
   }

   //
   // Beehive calls
   
   protected File downloadUpdate(String resource) throws AgentException {
      // now get the contents of the update
      RESTCall call = makeRESTCall(resource);
      call.invoke();
      // store it somewhere safe
      File tmpFile;
      try {
         tmpFile = File.createTempFile("controller-update-", ".war", new File(tmpPath));
      } catch (IOException e) {
         throw new AgentException("Failed to create temporary file", e);
      }
      InputStream is;
      try {
         is = call.getInputStream();
      } catch (IOException e) {
         throw new AgentException("Failed to open input stream", e);
      }
      try{
         FileOutputStream os;
         try {
            os = new FileOutputStream(tmpFile);
         } catch (FileNotFoundException e) {
            throw new AgentException("Failed to open temporary file for writing", e);
         }
         boolean success = false;
         try {
            IOUtils.copy(is, os);
            success = true;
         } catch (IOException e) {
            throw new AgentException("Failed to download update", e);
         }finally{
            try {
               os.flush();
               os.close();
            } catch (IOException e) {
               // if not success, we're already throwing
               if(!success)
                  throw new AgentException("Failed to flush to disk", e);
            }
         }
      }finally{
         try {
            is.close();
         } catch (IOException e) {
            // this can't be that bad
            log.error("Failed to close input stream", e);
         }
         call.disconnect();
      }
      return tmpFile;
   }

   protected JSONObject getNextCommand() throws AgentException {
      String contents;
      try{
         RESTCall connection = makeRESTCall(backendURL+"/user/"+backendUser+"/command-queue");
         connection.addHeader("Accept", "application/json");
         connection.invoke();
         contents = connection.getResponse();
         log.info("contents: "+contents);
      }catch(AgentException x){
         log.error("Failed to connecto to beehive to get next command", x);
         // let's retry later on
         return null;
      }
      
      try{
         JSONObject object = new JSONObject(contents);
         Object root = object.get("commands");
         // this is how beehive serialises an empty list, this is nuts
         if("".equals(root)){
            log.info("no command");
            return null;
         }
         if(!(root instanceof JSONObject)){
            throw new AgentException("Invalid JSON output: unknown 'commands' object type: "+root);
         }
         Object commands = ((JSONObject)root).get("command");
         log.info("this is what we get: "+commands);
         if(commands == null
               || ((commands instanceof JSONArray
                     && ((JSONArray)commands).length() == 0))){
            log.info("no command");
            return null;
         }
         if(!(commands instanceof JSONObject)
               && !(commands instanceof JSONArray)){
            throw new AgentException("Invalid JSON output: unknown 'command' object type: "+commands);
         }
         JSONObject command;
         if (commands instanceof JSONArray)
            command = ((JSONArray)commands).getJSONObject(0);
         else
            command = (JSONObject)commands;
         log.info("got a command: "+command);
         return command;
      }catch(JSONException x){
         throw new AgentException("JSON parsing error", x);
      }
   }

   private String getResource(JSONObject command) throws AgentException {
      return getNonEmptyString(command, "@resource");
   }

   private String getID(JSONObject command) throws AgentException {
      return getNonEmptyString(command, "id");
   }

   private String getNonEmptyString(JSONObject command, String key) throws AgentException {
      try{
         String resource = command.getString(key);
         if(StringUtils.isEmpty(resource))
            throw new AgentException("Missing "+key+" parameter");
         return resource;
      }catch(JSONException x){
         throw new AgentException("JSON error", x);
      }
   }

   protected RESTCall makeRESTCall(String url) throws AgentException {
      return new RESTCall(url, backendUser, backendPassword);
   }

   protected RESTCall makeRESTCall(String method, String url) throws AgentException {
      return new RESTCall(method, url, backendUser, backendPassword);
   }

   //
   // Deal with wars
   
   protected void installWar(File warFile) throws AgentException {
      log.info("Installing new war");
      if(!ZipUtil.unzip(warFile, controllerDeployPath))
         throw new AgentException("Failed to install new war");
      // we can remove the downloaded war
      if(!warFile.delete())
         throw new AgentException("Failed to delete downloaded war file");
   }

   protected void backupPreviousWar() throws AgentException {
      File deployFolder = new File(controllerDeployPath);
      if(!deployFolder.exists()){
         throw new AgentException("Deploy folder does not exist: "+controllerDeployPath);
      }
      // make sure it has something to back up
      if(deployFolder.listFiles().length == 0){
         log.info("Nothing to back up");
         return;
      }
      // let's back it up in a zip file
      log.info("Backing up controller from "+controllerDeployPath);
      File backupFile;
      try {
         backupFile = File.createTempFile("controller-backup-", ".war", new File(controllerBackupPath));
      } catch (IOException e) {
         throw new AgentException("Failed to create temp file", e);
      }
      try {
         ZipUtil.zip(controllerDeployPath, backupFile);
      } catch (IOException e) {
         throw new AgentException("Failed to zip previous controller", e);
      }
      log.info("Saved backup at "+backupFile.getAbsolutePath());
   }

   protected void deletePreviousWar() throws AgentException {
      log.info("Deleting previous war");

      File deployFolder = new File(controllerDeployPath);
      for(File file : deployFolder.listFiles()){
         if(file.isFile()){
            if(!file.delete())
               throw new AgentException("Failed to delete directory: "+file.getAbsolutePath());
         }else{
            try {
               FileUtils.deleteDirectory(file);
            } catch (IOException e) {
               throw new AgentException("Failed to delete directory: "+file.getAbsolutePath(), e);
            }
         }
      }
   }

   //
   // Main
   
   public static void main(String[] args) throws AgentException{
      BackendCommandsAgent agent = new BackendCommandsAgent();
      try{
         agent.run();
      }catch(AgentException x){
         log.error("Fatal agent error, exiting", x);
      }
   }
}
