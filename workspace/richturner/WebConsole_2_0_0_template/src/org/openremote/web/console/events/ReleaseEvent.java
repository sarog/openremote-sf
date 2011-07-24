package org.openremote.web.console.events;

import java.util.Date;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ReleaseEvent extends GwtEvent<ReleaseHandler> {
	private static final Type<ReleaseHandler> TYPE = new Type<ReleaseHandler>();
	private int clientXPos;
	private int clientYPos;
	private int screenXPos;
	private int screenYPos;
	private Object source;
	private long time;
	
	public ReleaseEvent(HumanInputEvent<? extends EventHandler> sourceEvent) {
		if (sourceEvent.getClass().equals(MouseUpEvent.class) || sourceEvent.getClass().equals(MouseOutEvent.class)) {
			MouseEvent event = (MouseEvent)sourceEvent;
			clientXPos = event.getClientX();
			clientYPos = event.getClientY();
			screenXPos = event.getScreenX();
			screenYPos = event.getScreenY();
		} else if (sourceEvent.getClass().equals(TouchEndEvent.class)) {
			// Cannot get position info from touch end
		}
		time = new Date().getTime();
		source = sourceEvent.getSource();
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ReleaseHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ReleaseHandler handler) {
		handler.onRelease(this);
	}

	public static Type<ReleaseHandler> getType() {
		return TYPE;
	}
	
	public int getClientX() {
		return clientXPos;
	}
	
	public int getClientY() {
		return clientYPos;
	}
	
	public int getScreenX() {
		return screenXPos;
	}
	
	public int getScreenY() {
		return screenYPos;
	}
	
	public Object getSource() {
		return source;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setClientX(int xPos) {
		clientXPos = xPos;
	}
	
	public void setClientY(int yPos) {
		clientYPos = yPos;
	}
	
	public void setScreenX(int xPos) {
		screenXPos = xPos;
	}
	
	public void setScreenY(int yPos) {
		screenYPos = yPos;
	}
}
