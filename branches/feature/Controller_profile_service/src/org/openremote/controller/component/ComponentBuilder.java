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
package org.openremote.controller.component;
import org.jdom.Element;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.RemoteActionXMLParser;

/**
 * The Class ComponentBuilder.
 * 
 * @author Handy.Wang 2009-10-15
 */
public abstract class ComponentBuilder {
    
    /** The remote action xml parser. */
    protected RemoteActionXMLParser remoteActionXMLParser;
    
    /** The command factory. */
    protected CommandFactory commandFactory;
    
    /**
     * Builds the component.
     * 
     * @param componentElement the component element
     * @param commandParam the command param
     * 
     * @return the component
     */
    public abstract Component build(Element componentElement, String commandParam);

    /**
     * Sets the remote action xml parser.
     * 
     * @param remoteActionXMLParser the new remote action xml parser
     */
    public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser) {
        this.remoteActionXMLParser = remoteActionXMLParser;
    }

    /**
     * Sets the command factory.
     * 
     * @param commandFactory the new command factory
     */
    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

}
