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
package org.openremote.controller.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.protocol.knx.KNXCommandBuilder;
import org.openremote.controller.spring.SpringContext;

/**
 * The Class ButtonCommandServiceTest.
 * 
 * @author Dan 2009-4-3
 */
public class PollingTest extends TestCase {

   /** The status command service. */
   private KNXCommandBuilder knxCommandBuilder = (KNXCommandBuilder) SpringContext.getInstance().getBean("knxCommandBuilder");;
   private StatusCommandService statusCommandService = (StatusCommandService) SpringContext.getInstance().getBean("statusCommandService");
   /**
    * Test trigger.
    */
   public void testExec(){
//       statusCommandService.trigger("1,2,3");
       String xml = ((StatusCommand)knxCommandBuilder.build(null)).read();
       System.out.println(xml);
       xml = ((StatusCommand)knxCommandBuilder.build(null)).read();
       System.out.println(xml);
       try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
   }
   
   public void testRegex() {
//     String urlButton = "http://localhost:8080/controller/rest/button/3/click";
     String urlControl = "http://localhost:8080/controller/rest/control/1/next";
     String urlStatus = "http://localhost:8080/controller/rest/status/1,2,3";
     
//     String regexpButton = "rest\\/button\\/(\\d+)\\/(\\w+)";
     String regexpControl = "rest\\/control\\/(\\d+)\\/(\\w+)";
     String regexpStatus = "rest\\/status\\/(.*)";
     
//     Pattern patternButton = Pattern.compile(regexpButton);
     Pattern patternControl = Pattern.compile(regexpControl);
     Pattern patternnStatus = Pattern.compile(regexpStatus);
     
     String buttonID = null;
     String commandTypeStr = null;
     
//     Matcher matcherButton = patternButton.matcher(urlButton);
     Matcher matcherControl = patternControl.matcher(urlControl);
     Matcher matcherStatus = patternnStatus.matcher(urlStatus);
     
//     if (matcherButton.find()) {
//         buttonID = matcherButton.group(1);
//         commandTypeStr = matcherButton.group(2);
//         System.out.println(buttonID + ", " + commandTypeStr);
//      }
     
     if (matcherControl.find()) {
        buttonID = matcherControl.group(1);
        commandTypeStr = matcherControl.group(2);
        System.out.println(buttonID + ", " + commandTypeStr);
     }
     
     String buttonIDs = null;
     if (matcherStatus.find()) {
         buttonIDs = matcherStatus.group(1);
         System.out.println(buttonIDs);
     }
     
     
   }
   
   public void testIsNumberCommand() {
     String number = "121";     
     String regexpNumber = "^\\d+$";     
     Pattern patternNumber = Pattern.compile(regexpNumber);     
     Matcher matcherNumber = patternNumber.matcher(number);
     
     if (matcherNumber.find()) {
        String rst = matcherNumber.group(0);
        System.out.println(rst);
     } else {
         System.out.println("It's not......");
     }
   }
   
}
