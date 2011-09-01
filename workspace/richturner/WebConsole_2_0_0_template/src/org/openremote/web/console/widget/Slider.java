package org.openremote.web.console.widget;

import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.Tappable;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Slider extends ConsoleWidgetImpl {
	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 40;
	public static final int MIN_HANDLE_SIZE = 40;
	private Handle handle;
	private SlideBar slideBar;
	private int width;
	private int height;
	private AbsolutePanel container;
	private boolean isVertical = false;
	private int minValue = 0;
	private int maxValue = 100;
	private double pixelValueDensity = 0;
	private int lastValue = 0;
	private int value = 0;
	private double halfHandle = 0;
	private int stepSize = 1;
	private int slideBarWidth = 0;
	private int slideBarHeight = 0;
	
	class Handle extends SimplePanel implements Draggable {
		protected static final int BORDER_WIDTH = 1;
		private int size;
		private Element element;
		
		public Handle(int size) {
			size = size < MIN_HANDLE_SIZE ? MIN_HANDLE_SIZE : size;		
			int innerSize = size - (2 * Handle.BORDER_WIDTH);
			this.size = size;
			setWidth(innerSize + "px");
			setHeight(innerSize + "px");
			
			element = getElement();
			DOM.setStyleAttribute(element, "WebkitBorderRadius", size + "px");
			DOM.setStyleAttribute(element, "MozBorderRadius", size + "px");
			DOM.setStyleAttribute(element, "borderRadius", size + "px");
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
			if (isVertical) {
				doHandleDrag(event.getYPos());
			} else {
				doHandleDrag(event.getXPos());
			}
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
		
		public int getSize() {
			return size;
		}
	}
	
	class SlideBar extends SimplePanel implements Tappable {
		protected static final double HEIGHT_RATIO = 0.4;
		protected static final double WIDTH_RATIO = 1.0;
		private boolean clickable = true;
		private int width;
		private int height;
		
		public SlideBar(int width, int height) {
			this.width = width;
			this.height = height;
			Element element = getElement();
			
			setStylePrimaryName("slider_bar");
			setHeight(height + "px");
			setWidth(width + "px");
			
			DOM.setStyleAttribute(element, "MozBorderRadius", height + "px");
			DOM.setStyleAttribute(element, "WebkitBorderRadius", height + "px");
			DOM.setStyleAttribute(element, "borderRadius", height + "px");
			DOM.setStyleAttribute(element, "WebkitUserSelect", "none");
			
			this.addHandler(this, TapEvent.getType());
		}

		@Override
		public void onTap(TapEvent event) {
			if (!clickable) {
				return;
			}
			if (isVertical) {
				doHandleDrag(event.getYPos());
			} else {
				doHandleDrag(event.getXPos());
			}
			doValueChange();			
		}
		
		public int getHeight() {
			if (isVertical) {
				return width;
			} else {
				return height;
			}
		}
		
		public int getWidth() {
			if (isVertical) {
				return height;
			} else {
				return width;
			}
		}
	}

	public Slider() {
		this(Slider.DEFAULT_WIDTH, Slider.DEFAULT_HEIGHT);
	}
	
	public Slider(int width, int height) {
		int handleSize = 0;
		int handleXPos = 0;
		int handleYPos = 0;
		int slideBarXPos = 0;
		int slideBarYPos = 0;
		
		this.width = width;
		this.height = height;
		
		// Determine the orientation of the slider
		if (height > width) {
			this.isVertical = true;
		}
		
		container = new AbsolutePanel();
		container.setWidth(width + "px");
		container.setHeight(height + "px");
		
		if (!isVertical) {
			handleSize = height;
			slideBarWidth = (int)Math.round(SlideBar.WIDTH_RATIO * width);
			slideBarHeight = (int)Math.round(SlideBar.HEIGHT_RATIO * height);
		} else {
			handleSize = width;
			slideBarWidth = (int)Math.round(SlideBar.HEIGHT_RATIO * width);
			slideBarHeight = (int)Math.round(SlideBar.WIDTH_RATIO * height);
		}
		
		slideBarXPos = (int)(width - slideBarWidth) / 2;
		slideBarYPos = (int)(height - slideBarHeight) / 2;
		handleYPos = (slideBarHeight + slideBarYPos) - handleSize;
		handleYPos = handleYPos < 0 ? 0 : handleYPos;
		
		slideBar = new SlideBar(slideBarWidth, slideBarHeight);
		handle = new Handle(handleSize);
		
		// Update size info from actual handle object
		handleSize = handle.getSize();
		halfHandle = (double)(handleSize / 2);
		
		container.add(slideBar);
		container.setWidgetPosition(slideBar, slideBarXPos, slideBarYPos);
		
		container.add(handle);
		container.setWidgetPosition(handle, handleXPos, handleYPos);
		
		container.setVisible(false);
		
		registerMouseAndTouchHandlers(slideBar);
		registerMouseAndTouchHandlers(handle);
		
		this.initWidget(container);
		
		DOM.setStyleAttribute(this.getElement(), "WebkitUserSelect", "none");
		this.addStyleName("consoleWidget");
	}
	
	private void doHandleDrag(int absPos) {
		int relPos = calculateRelativePixelValue(absPos);
		int value = (int)(relPos * pixelValueDensity);
		setValue(value);
	}
	
	private int calculateRelativePixelValue(int absValue) {
		int value = absValue;
		int pixelMin = 0;
		
		if (!isVertical) {
			pixelMin = (int)(slideBar.getAbsoluteLeft() + halfHandle);
			value = value - pixelMin;
		} else {
			pixelMin = (int)( slideBar.getAbsoluteTop() + slideBar.getWidth() - halfHandle);
			value = pixelMin - value;
		}
		return value;
	}
	
	private void setHandlePosition(int pixelPos) {
		if (!isVertical) {
			DOM.setStyleAttribute(handle.getElement(), "left", pixelPos + "px");
		} else {
			DOM.setStyleAttribute(handle.getElement(), "top", pixelPos + "px");
		}
	}
	
	public void configure() {
		int pixelRange = (int)(slideBar.getWidth() - (2 * halfHandle));
		pixelValueDensity = (double) (maxValue - minValue) / pixelRange;
	}
	
	private void doValueChange() {
		if (value != lastValue) {
			// Fire value change event on console unit event bus
			Window.alert("TELL THE WORLD NEW VALUE IS: " + value);
			lastValue = value;
		}
	}
	
	public void setMinMax(int min, int max) {
		if (!isInitialised) {
			minValue = min;
			maxValue = max;
		}
	}
	
	public void setValue(int value) {
		value = value < minValue ? minValue : value;
		value = value > maxValue ? maxValue : value;
		value = Math.round(value/stepSize) * stepSize;
		
		if (value != this.value) {
			int relPos = (int)Math.round(value/pixelValueDensity);
			relPos = isVertical ? slideBar.getWidth() - relPos - handle.getSize() : relPos; 
			setHandlePosition(relPos);
			this.value = value;
		}
	}
	
	public void setStepSize(int size) {
		if (!isInitialised) {
			stepSize = size;
		}
	}
}
