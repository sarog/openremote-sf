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
package org.openremote.android.controller.component.control.button;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import android.content.Context;
import org.openremote.android.controller.command.DelayCommand;
import org.openremote.android.controller.command.ExecutableCommand;
import org.openremote.android.controller.component.Component;
import org.openremote.android.controller.component.ComponentBuilder;
import org.openremote.android.controller.component.control.Control;

/**
 * The Class ButtonBuilder.
 * Port to Android by marcf
 * 
 * @author Handy.Wang 2009-10-15
 * @author marcf@openremote.org
 */
public class ButtonBuilder extends ComponentBuilder {
   
	public ButtonBuilder(Context context)
	{
		super(context);
	}
    /**
     * Build Button with button xml element.
     * 
     * Button instance has been initialized with property status = Status(new NoStatusCommand()). 
     * So, if commandParam is non-executable command(e.g: status),
     * the Button instance will get default status with read method in the StatusCommand. 
     */
    @SuppressWarnings("unchecked")
   @Override
    public Component build(Element componentElement, String commandParam) {
       Button button = new Button();
       if (button.isValidActionWith(commandParam)) {
    	   
          NodeList commandRefElements = componentElement.getChildNodes();
          Node commandRefElement = null;
          
          for (int i = 0; i< commandRefElements.getLength() ; i++) {
    		
        	  	commandRefElement = commandRefElements.item(i);
    		   	
        	  	// In case we have a delay element for commands, build it here
    		    if (Control.DELAY_ELEMENT_NAME.equalsIgnoreCase(commandRefElement.getNodeName())) {
                  button.addExecutableCommand(new DelayCommand(commandRefElement.getTextContent().trim()));
                  continue;
    		    }
    		    
    		    // The include tag in the XML contains the reference. 
    		    if (Control.INCLUDE_ELEMENT_NAME.equalsIgnoreCase(commandRefElement.getNodeName())) {
    		    	// The command is reference in the "ref" attribute of the include tag
    		    	String commandID = ((Element) commandRefElement).getAttribute(Control.REF_ATTRIBUTE_NAME);
    		    	
    		    	// XPath will find the corresponding line
    		        Element commandElement = remoteActionXMLParser.getElementById(commandID);    		    	
	    		    ExecutableCommand command = (ExecutableCommand) commandFactory.getCommand(commandElement);
	    		    
	    		    System.out.println("I have a command "+ command);
	    		    button.addExecutableCommand(command);
    		    }
    	   }
       }
       return button;
    }
}
