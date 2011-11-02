package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SliderComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "sliderComponent";
	public static final String HANDLE_CLASS_NAME = "sliderComponentHandle";
	public static final String BAR_CLASS_NAME = "sliderComponentBar";
	public static final String TRACK_CLASS_NAME = "sliderComponentBarTrack";
	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 40;
	public static final int SLIDE_BAR_HEIGHT = 10;
	public static final int HANDLE_SIZE = 26;
	public static final int HANDLE_CLICK_AREA_SIZE = 40;
	private Handle handle;
	private SlideBar slideBar;
	private int width;
	private int height;
	private AbsolutePanel container;
	private boolean isVertical = false;
	private int minValue = 0;
	private int maxValue = 100;
	private int pixelRange = 0;
	private int lastValue = 0;
	private int value = 0;
	private int halfHandle = 0;
	private int handleFixedPos = 0;
	private int handleInternalMargin = 0;
	private int stepSize = 1;
	private int slideBarWidth = 0;
	private int slideBarHeight = 0;
	
	class Handle extends SimplePanel implements Draggable {
		protected static final int BORDER_WIDTH = 2;
		
		public Handle() {
			SimplePanel visibleHandle = new SimplePanel();
			int innerSize = HANDLE_SIZE - (2 * Handle.BORDER_WIDTH);
			Element visibleElem = visibleHandle.getElement();
			Element touchElem = getElement();
			
			visibleHandle.setWidth(innerSize + "px");
			visibleHandle.setHeight(innerSize + "px");
			visibleHandle.setStylePrimaryName(HANDLE_CLASS_NAME);
			DOM.setStyleAttribute(visibleElem, "WebkitBorderRadius", HANDLE_SIZE/2 + "px");
			DOM.setStyleAttribute(visibleElem, "MozBorderRadius", HANDLE_SIZE/2 + "px");
			DOM.setStyleAttribute(visibleElem, "borderRadius", HANDLE_SIZE/2 + "px");
			DOM.setStyleAttribute(visibleElem, "borderWidth", BORDER_WIDTH + "px");
			DOM.setStyleAttribute(visibleElem, "borderStyle", "solid");
			DOM.setStyleAttribute(visibleElem, "marginTop", handleInternalMargin + "px");
			//DOM.setStyleAttribute(visibleElem, "marginLeft", handleInternalMargin + "px");
			
			this.setWidth(HANDLE_CLICK_AREA_SIZE + "px");
			this.setHeight(HANDLE_CLICK_AREA_SIZE + "px");
			this.setWidget(visibleHandle);
			
			DOM.setStyleAttribute(touchElem, "WebkitBorderRadius", HANDLE_SIZE/2 + "px");
			DOM.setStyleAttribute(touchElem, "MozBorderRadius", HANDLE_SIZE/2 + "px");
			DOM.setStyleAttribute(touchElem, "borderRadius", HANDLE_SIZE/2 + "px");
			
			storeHandler(this.addHandler(this, DragStartEvent.getType()));
			storeHandler(this.addHandler(this, DragMoveEvent.getType()));
			storeHandler(this.addHandler(this, DragEndEvent.getType()));
			storeHandler(this.addHandler(this, DragCancelEvent.getType()));
		}
		
		public void onDragStart(DragStartEvent event) {
		}

		@Override
		public void onDragMove(DragMoveEvent event) {
			if (appearsVertical()) {
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
	}
	
	class SlideBar extends SimplePanel implements TapHandler {
		private boolean clickable = true;

		
		public SlideBar() {
			Element element = getElement();
			
			if (isVertical) {
				slideBarWidth = SLIDE_BAR_HEIGHT;
				slideBarHeight = height;
			} else {
				slideBarWidth = width;
				slideBarHeight = SLIDE_BAR_HEIGHT;
			}
			
			setHeight(slideBarHeight + "px");
			setWidth(slideBarWidth + "px");
			this.setStylePrimaryName(BAR_CLASS_NAME);
			
			int rad = (height/2);
			DOM.setStyleAttribute(element, "MozBorderRadius", rad + "px");
			DOM.setStyleAttribute(element, "WebkitBorderRadius", rad + "px");
			DOM.setStyleAttribute(element, "borderRadius", rad + "px");
			DOM.setStyleAttribute(element, "borderStyle", "1px solid");
			DOM.setStyleAttribute(element, "overflow", "hidden");
			
			SimplePanel track = new SimplePanel();
			track.setStylePrimaryName(TRACK_CLASS_NAME);
			Element trackElem = track.getElement();
			if (isVertical) {
				track.setWidth("100%");
				track.setHeight("100%");
				DOM.setStyleAttribute(trackElem, "marginTop", height + "px");
			} else {
				track.setHeight("100%");
				DOM.setStyleAttribute(trackElem, "marginRight", width + "px");
			}			
			DOM.setStyleAttribute(trackElem, "MozBorderRadius", rad + "px");
			DOM.setStyleAttribute(trackElem, "WebkitBorderRadius", rad + "px");
			DOM.setStyleAttribute(trackElem, "borderRadius", rad + "px");
			DOM.setStyleAttribute(trackElem, "borderStyle", "1px solid");
			
			this.setWidget(track);
			storeHandler(this.addHandler(this, TapEvent.getType()));
		}

		@Override
		public void onTap(TapEvent event) {
			if (!clickable) {
				return;
			}
			
			if (appearsVertical()) {
				doHandleDrag(event.getYPos());
			} else {
				doHandleDrag(event.getXPos());
			}
			
			doValueChange();
		}
		
		protected Widget getTrack() {
			return this.getWidget();
		}
	}
	
	private SliderComponent() {
		super(new AbsolutePanel());
		container = (AbsolutePanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		container.setWidth("100%");
		container.setHeight("100%");
		DOM.setStyleAttribute(container.getElement(), "overflow", "visible");
		handleInternalMargin = (HANDLE_CLICK_AREA_SIZE - HANDLE_SIZE) / 2;
	}
	
	@Override
	public void onRender(int width, int height) {
		if (!isInitialised) {
			int relSlideBarXPos = 0;
			int relSlideBarYPos = 0;
			
			this.width = width;
			this.height = height;
			
			// Create Slide Bar
			slideBar = new SlideBar();
			container.add(slideBar);
			relSlideBarXPos = (int)(((double)width - slideBarWidth) / 2);
			relSlideBarYPos = (int)(((double)height - slideBarHeight) / 2);
			container.setWidgetPosition(slideBar, relSlideBarXPos, relSlideBarYPos);
			
			// Create Handle
			handle = new Handle();
			halfHandle = (int)(Math.floor((double)HANDLE_SIZE / 2));
			
			if (isVertical) {
				handleFixedPos = (int)(((double)width - HANDLE_CLICK_AREA_SIZE)/2);
			} else {
				handleFixedPos = (int)(((double)height - HANDLE_CLICK_AREA_SIZE)/2);
			}
			container.add(handle);
			setHandlePosition(halfHandle);
			
			// Calculate pixel value density
			pixelRange = (int)(getWidth() - (2 * halfHandle));
		}
		
		registerMouseAndTouchHandlers(slideBar);
		registerMouseAndTouchHandlers(handle);
	}
	
	private void doHandleDrag(int absPos) {
		int relPos = calculateRelativePixelValue(absPos);
		int limitedPos = setHandlePosition(relPos);
		setTrackLength(limitedPos);
		int value = (int)Math.round((((double)(limitedPos - halfHandle) / pixelRange) * (maxValue - minValue)) + minValue);
		setValue(value);
	}
	
	private int calculateRelativePixelValue(int absValue) {
		int value = absValue;
		int pixelMin = 0;
		
		if (appearsVertical()) {
			pixelMin = (int)(slideBar.getAbsoluteTop() + getWidth());
			value = pixelMin - value;
		} else {
			if (isVertical) {
				pixelMin = (int)(container.getAbsoluteLeft() + getWidth());
				value = pixelMin - value;		
			} else {
				pixelMin = container.getAbsoluteLeft();
				value = value - pixelMin;				
			}
		}
		
		return value;
	}
	
	public boolean appearsVertical() {
		String unitIsVerticalString = WebConsole.getConsoleUnit().getOrientation();
		boolean unitIsVertical = unitIsVerticalString.equalsIgnoreCase("portrait") ? true : false;
		boolean displayIsVertical = WebConsole.getConsoleUnit().getConsoleDisplay().getIsVertical();
		return (isVertical && (unitIsVertical && displayIsVertical) || (!isVertical && (!unitIsVertical && displayIsVertical)));
	}
	
	public void setValue(int value) {
		value = value < minValue ? minValue : value;
		value = value > maxValue ? maxValue : value;
		value = Math.round(value/stepSize) * stepSize;
		
		if (value != this.value) {
			this.value = value;
		}
	}
	
	// Relative Pos to centre of handle
	private int setHandlePosition(int pos) {
		pos = pos < (0 + halfHandle) ? (int)(0 + halfHandle) : pos;
		pos = pos > (getWidth() - halfHandle) ? (int)(getWidth() - halfHandle) : pos;
		if (isVertical) {
			container.setWidgetPosition(handle, handleFixedPos, (int)(getWidth() - pos - halfHandle - handleInternalMargin));			
		} else {
			container.setWidgetPosition(handle, (int)(pos - halfHandle - handleInternalMargin), handleFixedPos);
		}
		return pos;
	}
	
	private void setTrackLength(int size) {
		int length = 0;
		if (isVertical) {
			length = height-size;
			DOM.setStyleAttribute(slideBar.getTrack().getElement(), "marginTop", length + "px");
		} else {
			length = width-size;
			DOM.setStyleAttribute(slideBar.getTrack().getElement(), "marginRight", length + "px");
		}
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
	
	public void setStepSize(int size) {
		if (!isInitialised) {
			stepSize = size;
		}
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
	
	public void setMin(int min) {
		this.minValue = min;
	}

	public void setMax(int max) {
		this.maxValue = max;
	}
	
	public void setIsVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.SliderComponent entity) {
		SliderComponent component = new SliderComponent();
		component.setMax(entity.getMax().getValue());
		component.setMin(entity.getMin().getValue());
		component.setIsVertical(entity.getVertical());
		return component;
	}
}
