/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.web.console.event.ui;

import org.openremote.web.console.widget.InteractiveConsoleComponent;

import com.google.gwt.event.shared.GwtEvent;
/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class CommandSendEvent extends GwtEvent<CommandSendHandler> {
	private static final Type<CommandSendHandler> TYPE = new Type<CommandSendHandler>();
	private Integer commandId;
	private String command;
	private InteractiveConsoleComponent sender;
	
	public CommandSendEvent(Integer commandId, String command, InteractiveConsoleComponent sender) {
		this.commandId = commandId;
		this.command = command;
		this.sender = sender;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommandSendHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CommandSendHandler handler) {
		handler.onCommandSend(this);
	}

	public static Type<CommandSendHandler> getType() {
		return TYPE;
	}
	
	public String getCommand() {
		return command;
	}
	
	public int getCommandId() {
		return commandId;
	}
	
	public InteractiveConsoleComponent getSender() {
		return sender;
	}
}
