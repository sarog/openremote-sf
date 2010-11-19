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
package org.openremote.android.controller.component;

import java.util.Hashtable;
import org.w3c.dom.Element;
import android.content.Context;
import org.openremote.android.controller.component.control.button.ButtonBuilder;
import org.openremote.android.controller.component.Component;
import org.openremote.android.controller.component.ComponentBuilder;

/**
 * A factory for creating Component objects.
 * 
 * We hardcode the support here.  Eventually we will want to refactor to have an external list, with a IoC type file. 
 * 
 * @author marcf@openremote.org
 */
public class ComponentFactory {

    /** The component builders. */
    private Hashtable<String,ComponentBuilder> componentBuilders;
    
    
    /*
     * This constructor populates the componentBuilders.  It is hard coded with current support here and should be externalized with IoC
     */
    public ComponentFactory(Context context) {
    	
    	componentBuilders = new Hashtable();
    
    	componentBuilders.put("button", new ButtonBuilder(context));
    }
    
    /**
     * Gets the component.
     * 
     * @param componentElement the component element
     * @param commandParam the command param
     * 
     * @return the component
     */
    public Component getComponent(Element componentElement, String commandParam) {
    	            
    	String componentType = componentElement.getTagName();

        // We should have a component builder for this protocol
        ComponentBuilder componentBuilder = componentBuilders.get(componentType);
        
//        System.out.println("TYPE TYPE TYPE:"+componentBuilder);
        
        if(componentBuilder == null) {
        	
        	System.out.println("No Such component builder with the component "+componentElement.getTagName());
        }
 
        return (Component) componentBuilder.build(componentElement, commandParam);
    }    
}
