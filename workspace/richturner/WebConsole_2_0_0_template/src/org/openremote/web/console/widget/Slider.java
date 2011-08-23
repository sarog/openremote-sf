package org.openremote.web.console.widget;

import java.awt.Event;

import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.press.PressStartHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.Tappable;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Slider extends ConsoleWidget {
	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 40;
	private Handle handle;
	private SlideBar slideBar;
	private int width;
	private int height;
	private AbsolutePanel container;
	private boolean isVertical = false;
	private int minValue = 0;
	private int maxValue = 100;
	private int pixelRange = 0;
	private double pixelValueDensity = 0;
	private int lastValue = 0;
	private int value = 0;
	private int halfHandle = 0;
	private int stepSize = 1;
	private boolean isInitialised = false;
	
	class Handle extends SimplePanel implements Draggable {
		protected static final int BORDER_WIDTH = 1;
		private int height;
		private int width;
		private Element element;
		
		public Handle(int size) {
			element = getElement();
			int innerSize = size - (2 * Handle.BORDER_WIDTH);
			width = innerSize;
			height = innerSize;
			setWidth(width + "px");
			setHeight(height + "px");
			
			DOM.setStyleAttribute(element, "WebkitBorderRadius", height + "px");
			DOM.setStyleAttribute(element, "MozBorderRadius", height + "px");
			DOM.setStyleAttribute(element, "borderRadius", height + "px");
			DOM.setStyleAttribute(element, "borderWidth", BORDER_WIDTH + "px");
			DOM.setStyleAttribute(element, "borderStyle", "solid");
			setStylePrimaryName("slider_handle");
		   	
			this.addHandler(this, DragStartEvent.getType());
			this.addHandler(this, DragMoveEvent.getType());
			this.addHandler(this, DragEndEvent.getType());
			this.addHandler(this, DragCancelEvent.getType());
		}
		
		public void onDragStart(DragStartEvent event) {
		}

		@Override
		public void onDragMove(DragMoveEvent event) {
			doHandleDrag(event.getXPos());
		}

		@Override
		public void onDragEnd(DragEndEvent event) {
			doValueChange();
		}

		@Override
		public void onDragCancel(DragCancelEvent event) {
			// This event occurs when press moves off the console display
			doValueChange();
		}
	}
	
	class SlideBar extends SimplePanel implements Tappable {
		protected static final double HEIGHT_RATIO = 0.4;
		protected static final double WIDTH_RATIO = 1.0;
		private boolean clickable = true;
		
		public SlideBar(long width, long height) {
			Element element = getElement();
			
			setStylePrimaryName("slider_bar");
			setHeight(height + "px");
			setWidth(width + "px");
			DOM.setStyleAttribute(element, "MozBorderRadius", "4px");
			DOM.setStyleAttribute(element, "WebkitBorderRadius", "4px");
			DOM.setStyleAttribute(element, "borderRadius", "4px");
			DOM.setStyleAttribute(element, "WebkitUserSelect", "none");
			
			this.addHandler(this, TapEvent.getType());
		}

		@Override
		public void onTap(TapEvent event) {
			if (!clickable) {
				return;
			}
			doHandleDrag(event.getXPos());
			doValueChange();			
		}
	}
	
	public Slider() {
		this(Slider.DEFAULT_WIDTH, Slider.DEFAULT_HEIGHT);
	}
	
	public Slider(int width, int height) {
		this.width = width;
		this.height = height;
		
		int handleSize = height;
		int slideBarXPos = (int)((1-SlideBar.WIDTH_RATIO) * width) / 2;
		int slideBarYPos = (int)((1-SlideBar.HEIGHT_RATIO) * height) / 2;
		
		container = new AbsolutePanel();
		container.setWidth(width + "px");
		container.setHeight(height + "px");
		
		slideBar = new SlideBar(Math.round(SlideBar.WIDTH_RATIO * width), Math.round(SlideBar.HEIGHT_RATIO * height));

		handle = new Handle(handleSize);
		halfHandle = handleSize/2;
		
		container.add(slideBar);
		container.setWidgetPosition(slideBar, slideBarXPos, slideBarYPos);
		
		container.add(handle);
		container.setWidgetPosition(handle, 0, 0);
		
		registerPressHandlers(slideBar);
		registerPressHandlers(handle);
		
		this.initWidget(container);
		
		DOM.setStyleAttribute(this.getElement(), "WebkitUserSelect", "none");
		this.addStyleName("consoleComponent");
	}
	
	private void doHandleDrag(int absPos) {
		int relPos = calculateRelativePixelValue(absPos);
		int value = (int)Math.round(relPos * pixelValueDensity);
		value = Math.round(value/stepSize) * stepSize;
		
		if (value != this.value) {
			relPos = (int)Math.round(value/pixelValueDensity);
			setHandlePosition(relPos);
			this.value = value;
		}
	}
	
	private int calculateRelativePixelValue(int absValue) {
		int value = absValue;
		int pixelMin = this.getAbsoluteLeft() + halfHandle;
		int pixelMax = pixelMin + width - (2 * halfHandle);
		
		// Initialise rendered screen values if not already done
		if (!isInitialised) {
			initialise();
		}
		
		// Normalise the pixel value
		value = value > pixelMax ? pixelMax : value;
		value = value < pixelMin ? pixelMin : value;
		
		value = value - pixelMin;
		
		return value;
	}
	
	private void setHandlePosition(int pixelPos) {
		if (!isVertical) {
			DOM.setStyleAttribute(handle.getElement(), "left", pixelPos + "px");
		} else {
			//DOM.setStyleAttribute(handle.getElement(), "bottom", pixelPos + "px");
		}
	}
	
	private void initialise() {
		int pixelMin = this.getAbsoluteLeft() + halfHandle;
		int pixelMax = pixelMin + width - (2 * halfHandle);
		pixelRange = pixelMax-pixelMin;
		pixelValueDensity = (double) (maxValue - minValue) / pixelRange;
		isInitialised = true;
	}
	
	private void doValueChange() {
		if (value != lastValue) {
			// Fire value change event on console unit event bus
			Window.alert("TELL THE WORLD NEW VALUE IS: " + value);
			lastValue = value;
		}
	}
}
