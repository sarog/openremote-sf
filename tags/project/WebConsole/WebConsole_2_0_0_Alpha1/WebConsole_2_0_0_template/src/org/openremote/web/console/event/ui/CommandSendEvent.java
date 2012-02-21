package org.openremote.web.console.event.ui;

import org.openremote.web.console.widget.InteractiveConsoleComponent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author rich
 */
public class CommandSendEvent extends GwtEvent<CommandSendHandler> {
	private static final Type<CommandSendHandler> TYPE = new Type<CommandSendHandler>();
	private int commandId;
	private String command;
	private InteractiveConsoleComponent sender;
	
	public CommandSendEvent(int commandId, String command, InteractiveConsoleComponent sender) {
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
