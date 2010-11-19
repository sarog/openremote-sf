/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.android.controller.service;

import java.util.List;

import org.w3c.dom.Element;
import org.openremote.android.controller.command.RemoteActionXMLParser;
import org.openremote.android.controller.service.ControlCommandService;
import android.content.Context;
//import org.openremote.android.controller.utils.MacrosIrDelayUtil;
import org.openremote.android.controller.command.ExecutableCommand;
import org.openremote.android.controller.component.ComponentFactory;
import org.openremote.android.controller.component.control.Control;


/**
 * Local Control Command 
 * 
 * @author marcf
 */
public class ControlCommandService {
	
	private static ControlCommandService singleton; 

	private static RemoteActionXMLParser remoteActionXMLParser;
   
    private static ComponentFactory componentFactory;
 

   /**
    * Trigger is responsible for finding the actual command and calling it. Macros are implemented here
    */
  public void trigger(int controlID, String commandParam) {
      
	  Control control = getControl(controlID, commandParam);
      
	  // It may be a macro
      List<ExecutableCommand> executableCommands = control.getExecutableCommands();
      // IR delays?
      // MacrosIrDelayUtil.ensureDelayForIrCommand(executableCommands);
      
      //Execute all commands in this command macro
      for (ExecutableCommand executableCommand : executableCommands) {
         if (executableCommand != null) {
        	 System.out.println("GET TO THE SEND");
            executableCommand.send();
        	 System.out.println("GOT TO TEH SEND");
         } else {
   //         throw new Exception("ExecutableCommand is null");
        	 (new Exception("Executable Command is null")).printStackTrace();
         }
      }
   }

	
  private Control getControl(int controlID, String commandParam) {
		
      Element controlElement = remoteActionXMLParser.getElementById(controlID);
      
      if (controlElement == null) {
    	  (new Exception("NO CONTROL ELEMENT FOR :"+controlID)).printStackTrace();
     }
      return (Control) componentFactory.getComponent(controlElement, commandParam);
            
  }
   
   /**
    * Get singleton instance 
    */
   public static ControlCommandService getInstance(Context context)
   {
	   // Create singleton if necessary
	   if(singleton == null) { 
		   
		   singleton = new ControlCommandService();
		   
		   // Set a new component factory
		   componentFactory = new ComponentFactory(context);
		   
		   // Set a new RemoteAction XML parser
		   remoteActionXMLParser = new RemoteActionXMLParser(context);
	   }
	   // Return the singleton
	   return singleton;
   }
}
