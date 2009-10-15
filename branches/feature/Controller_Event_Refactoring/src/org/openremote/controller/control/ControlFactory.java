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
package org.openremote.controller.control;

import java.util.Properties;

import org.jdom.Element;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * A factory for creating Control objects.
 * 
 * @author Handy.Wang 2009-10-15
 */
public class ControlFactory extends ApplicationObjectSupport {

    /** The control builders. */
    private Properties controlBuilders;
    
    /**
     * Gets the control.
     * 
     * @param controlElement the control element
     * @param commandParam the command param
     * 
     * @return the control
     */
    public Control getControl(Element controlElement, String commandParam) {
        String controlType = controlElement.getName();
        String controlBuilderName = controlBuilders.getProperty(controlType);
        ControlBuilder controlBuilder = (ControlBuilder)getApplicationContext().getBean(controlBuilderName);
        return controlBuilder.build(controlElement, commandParam);
    }

    /**
     * Sets the control builders.
     * 
     * @param controlBuilders the new control builders
     */
    public void setControlBuilders(Properties controlBuilders) {
        this.controlBuilders = controlBuilders;
    }
    
}
