package org.openremote.web.console.event.press;

import java.util.Date;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 * @author rich
 *
 */
public abstract class PressEvent<H extends EventHandler> extends GwtEvent<H> {
	protected int clientXPos;
	protected int clientYPos;
	protected int screenXPos;
	protected int screenYPos;
	protected long time;
	protected Widget source;
	
	public PressEvent(GwtEvent<? extends EventHandler> sourceEvent) {
		time = new Date().getTime();
		source = (Widget)sourceEvent.getSource();
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
	
	public Widget getSource() {
		return source;
	}
	
	public long getTime() {
		return time;
	}
}
