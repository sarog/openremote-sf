package org.openremote.web.console.event.ui;

import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.InteractiveConsoleComponent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author rich
 */
public class BindingDataChangeEvent extends GwtEvent<BindingDataChangeHandler> {
	private static final Type<BindingDataChangeHandler> TYPE = new Type<BindingDataChangeHandler>();
	private String dataSource;
	
	public BindingDataChangeEvent(String dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BindingDataChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BindingDataChangeHandler handler) {
		handler.onBindingDataChange(this);
	}

	public static Type<BindingDataChangeHandler> getType() {
		return TYPE;
	}
	
	public String getDataSource() {
		return dataSource;
	}
}
