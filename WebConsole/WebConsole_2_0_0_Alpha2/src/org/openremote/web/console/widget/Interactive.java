package org.openremote.web.console.widget;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

public interface Interactive extends MouseDownHandler, TouchStartHandler, MouseUpHandler, MouseOutHandler, TouchEndHandler {
	public void onTouchStart(TouchStartEvent event);
	public void onMouseDown(MouseDownEvent event);
	public void onTouchEnd(TouchEndEvent event);
	public void onMouseUp(MouseUpEvent event);
}
