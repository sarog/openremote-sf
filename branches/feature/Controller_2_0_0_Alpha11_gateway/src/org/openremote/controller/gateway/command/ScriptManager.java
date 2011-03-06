/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway.command;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.Invocable;
import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.GatewayScriptException;
import org.openremote.controller.utils.PathUtil;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
/**
 * 
 * @author Rich Turner 2011-02-27
 */
public class ScriptManager
{
   /* The logger. */
   private Logger logger = Logger.getLogger(ScriptManager.class);

   /* The controller configuration properties */
   private Configuration configuration;
   
   /* Script engine manager for creating script engines */
   private ScriptEngineManager engineManager = new ScriptEngineManager();
   
   /* Script engine map */
   private Map<String, ScriptEngine> invocableEngines = new HashMap<String,ScriptEngine>();
   
   /* Script file names */
   private List<String> scriptNames = new ArrayList<String>();
   
   
   /**
    * Adds a new script to the manager by first checking that the script
    * extension is supported and validating the script code, returns true
    * if successfully added, otherwise returns false
    */
   public void addScript(String scriptName) {

      // Check to see if script already added
      if (invocableEngines.containsKey(scriptName)) {
         return;
      }
      
      int pos = scriptName.lastIndexOf('.');
      if (pos <= 0) {
           throw new GatewayScriptException("Invalid script file name '" + scriptName + "'");
      } else {
         String ext = scriptName.substring(pos+1);
         try {
            String scriptFilePath = PathUtil.addSlashSuffix(configuration.getResourcePath()) + "scripts/" + scriptName;
            InputStream is = new FileInputStream(new File(scriptFilePath));
            Reader reader = new InputStreamReader(is);
            ScriptEngine engine = engineManager.getEngineByExtension(ext);
            engine.eval(reader);
            invocableEngines.put(scriptName, engine);
         } catch (IOException e) {
            throw new GatewayScriptException("Cannot open script file '" + scriptName + "' " + e.getMessage(), e);
         } catch (NullPointerException e) {
            throw new GatewayScriptException("Extension not supported '" + ext + "' " + e.getMessage(), e);
         } catch (ScriptException e) {
            throw new GatewayScriptException("Syntax error in '" + scriptName + "' " + e.getMessage(), e);
         } catch (Exception e) {
            throw new GatewayScriptException(e);
         }
      }
   } 
   
   public String executeScript(String scriptName, Map<String, String> args, String currentCommandValue) {
      String result = "";
      
      try {
         ScriptEngine engine = getScriptEngine(scriptName);
         if (engine != null) {
            
            for(String key: args.keySet())
            {
                engine.put(key, args.get(key)); 
            }
            Invocable invokeEngine = (Invocable) engine;
            result = (String) invokeEngine.invokeFunction("run", currentCommandValue);
         }
      } catch (ScriptException e) {
         throw new GatewayScriptException("Syntax error in '" + scriptName + "' " + e.getMessage(), e);
      } catch (NoSuchMethodException e) {
         throw new GatewayScriptException("Script doesn't contain 'run' function '" + scriptName + "'", e);
      } catch (NullPointerException e) {
         throw new GatewayScriptException(e.getMessage(),e);
      }
      return result;   
   }
   
   public ScriptEngine getScriptEngine(String scriptName) {
      ScriptEngine engine = null;
      return this.invocableEngines.get(scriptName);
   }
   
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
   /* Remove registered scripts */
   public void reset() {
      this.invocableEngines.clear();
   }
}