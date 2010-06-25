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
package org.openremote.controller.protocol.test.mockup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;

/**
 * 
 * @author handy.wang 2010-03-18
 *
 */
public class MockupExecutableCommand extends MockupCommand implements ExecutableCommand {
   
   private Logger logger = Logger.getLogger(this.getClass().getName());
   
   @Override
   public void send() {
      BufferedReader in = null;
      StringBuffer result = new StringBuffer();
      try {
         URL url = new URL(getUrl());
//         logger.info("Had send executable command : " + getUrl());
         in = new BufferedReader(new InputStreamReader(url.openStream()));
         String str;
         while ((str = in.readLine()) != null) {
            result.append(str);
         }         
//         logger.info("Received message: " + result);
      } catch (Exception e) {
         logger.error("MockupExecutableCommand could not execute", e);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               logger.error("BufferedReader could not be closed", e);
            }
         }
      }
      
      
      
   }
   
}
