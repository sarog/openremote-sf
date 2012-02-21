package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.CommandSendEvent;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SliderComponent extends InteractiveConsoleComponent implements SensorChangeHandler {
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
	private boolean isVertical = false;
	private int minValue = 0;
	private int maxValue = 100;
	private int pixelRange = 0;
	private int lastValue = 0;
	private int value = 0;
	private int halfHandle = 0;
	private int handleFixedPos = 0;
	private int handleInternalMargin = (HANDLE_CLICK_AREA_SIZE - HANDLE_SIZE) / 2;
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
			DOM.setStyleAttribute(visibleElem, "marginTop", handleInternalMargin + "px");
			
			this.setWidth(HANDLE_CLICK_AREA_SIZE + "px");
			this.setHeight(HANDLE_CLICK_AREA_SIZE + "px");
			this.setWidget(visibleHandle);

//			DOM.setStyleAttribute(visibleElem, "WebkitBorderRadius", HANDLE_SIZE/2 + "px");
//			DOM.setStyleAttribute(visibleElem, "MozBorderRadius", HANDLE_SIZE/2 + "px");
//			DOM.setStyleAttribute(visibleElem, "borderRadius", HANDLE_SIZE/2 + "px");
//			DOM.setStyleAttribute(visibleElem, "borderWidth", BORDER_WIDTH + "px");
//			DOM.setStyleAttribute(visibleElem, "borderStyle", "solid");
//			DOM.setStyleAttribute(touchElem, "WebkitBorderRadius", HANDLE_SIZE/2 + "px");
//			DOM.setStyleAttribute(touchElem, "MozBorderRadius", HANDLE_SIZE/2 + "px");
//			DOM.setStyleAttribute(touchElem, "borderRadius", HANDLE_SIZE/2 + "px");
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

		/*
		 *  This event occurs when press moves off the console display(non-Javadoc)
		 * @see org.openremote.web.console.event.drag.DragCancelHandler#onDragCancel(org.openremote.web.console.event.drag.DragCancelEvent)
		 */
		@Override
		public void onDragCancel(DragCancelEvent event) {
			doValueChange();
		}
	}
	
	class SlideBar extends SimplePanel implements TapHandler {
		private boolean clickable = true;

		
		public SlideBar() {
			Element element = getElement();
			this.setStylePrimaryName(BAR_CLASS_NAME);
			DOM.setStyleAttribute(element, "overflow", "hidden");
			
			SimplePanel track = new SimplePanel();
			track.setStylePrimaryName(TRACK_CLASS_NAME);
			if (isVertical) {
				track.setWidth("100%");
				track.setHeight("100%");
			} else {
				track.setHeight("100%");
			}
			this.setWidget(track);
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
		// Define container widget
		super(new AbsolutePanel(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "overflow", "visible");
		
		// Define child components
		slideBar = new SlideBar();
		((AbsolutePanel)getWidget()).add(slideBar);
		handle = new Handle();
		((AbsolutePanel)getWidget()).add(handle);
		addInteractiveChild(slideBar);
		addInteractiveChild(handle);
	}
	
	@Override
	public void onRender(int width, int height) {
		if (!isInitialised) {
			int relSlideBarXPos = 0;
			int relSlideBarYPos = 0;
			
			this.width = width;
			this.height = height;
			
			// Configure Slide Bar
			if (isVertical) {
				slideBarWidth = SLIDE_BAR_HEIGHT;
				slideBarHeight = height;
			} else {
				slideBarWidth = width;
				slideBarHeight = SLIDE_BAR_HEIGHT;
			}
			relSlideBarXPos = (int)(((double)width - slideBarWidth) / 2);
			relSlideBarYPos = (int)(((double)height - slideBarHeight) / 2);
			((AbsolutePanel)getWidget()).setWidgetPosition(slideBar, relSlideBarXPos, relSlideBarYPos);
			slideBar.setHeight(slideBarHeight + "px");
			slideBar.setWidth(slideBarWidth + "px");
			setTrackLength(0);
			
			// Configure Handle
			halfHandle = (int)(Math.floor((double)HANDLE_SIZE / 2));
			
			if (isVertical) {
				handleFixedPos = (int)(((double)width - HANDLE_CLICK_AREA_SIZE)/2);
			} else {
				handleFixedPos = (int)(((double)height - HANDLE_CLICK_AREA_SIZE)/2);
			}
			setHandlePosition(halfHandle);
			
			// Calculate pixel value density
			pixelRange = (int)(getWidth() - (2 * halfHandle));
		}
	}
	
	@Override
	public void onSensorAdd() {}
	
	@Override
	public void sensorChanged(String newValue) {
		try {
			int value = Integer.parseInt(newValue);
			value = value < minValue ? minValue : value;
			value = value > maxValue ? maxValue : value;
			doHandleDragUsingValue(value);
			doValueChange(true);
		} catch(Exception e) {}
	}
	
	private void doHandleDragUsingValue(int value) {
		int limitedPos = (int)Math.round(((((double)value - minValue) / (maxValue - minValue)) * pixelRange) + halfHandle);
		limitedPos = setHandlePosition(limitedPos);
		setTrackLength(limitedPos);
		setValue(value);
	}
	
	private void doHandleDrag(int absPos) {
		int relPos = calculateRelativePixelValue(absPos);
		int limitedPos = setHandlePosition(relPos);
		setTrackLength(limitedPos);
		int value = (int)Math.round((((double)(limitedPos - halfHandle) / pixelRange) * (maxValue - minValue)) + minValue);
		value = value < minValue ? minValue : value;
		value = value > maxValue ? maxValue : value;
		setValue(value);
	}
	
	private int calculateRelativePixelValue(int absValue) {
		int value = absValue;
		int pixelMin = 0;
		boolean appearsVertical = appearsVertical();
		int consoleCentreX = (int)Math.round(((double)BrowserUtils.getWindowWidth()/2));
		int consoleCentreY = (int)Math.round(((double)BrowserUtils.getWindowHeight()/2));
		
		if (((appearsVertical && !isVertical) || (!appearsVertical && isVertical)) && (BrowserUtils.isCssDodgy)) {
			if (!isVertical) {
				//Window.alert(absValue +"");
				pixelMin = consoleCentreY + (consoleCentreX - getAbsoluteLeft());
			} else {
				pixelMin = consoleCentreX + (consoleCentreY - (slideBar.getAbsoluteTop() + getWidth()));
			}
		} else {
			if (appearsVertical) {
				pixelMin = slideBar.getAbsoluteTop() + getWidth();
			} else {
				if (isVertical) {
					pixelMin = getAbsoluteLeft() + getWidth();
				} else {
					pixelMin = getAbsoluteLeft();
				}
			}
		}		
		
		if (appearsVertical) {
			value = pixelMin - value;
		} else {
			if (isVertical) {
				value = pixelMin - value;		
			} else {
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
		pos = pos < (0 + halfHandle) ? (0 + halfHandle) : pos;
		pos = pos > (getWidth() - halfHandle) ? (getWidth() - halfHandle) : pos;
		if (isVertical) {
			((AbsolutePanel)getWidget()).setWidgetPosition(handle, handleFixedPos, (getWidth() - pos - halfHandle - handleInternalMargin));			
		} else {
			((AbsolutePanel)getWidget()).setWidgetPosition(handle, (pos - halfHandle - handleInternalMargin), handleFixedPos);
		}
		return pos;
	}
	
	private void setTrackLength(int size) {
		int length = 0;
		if (isVertical) {
			length = height-size;
			DOM.setStyleAttribute(slideBar.getTrack().getElement(), "marginTop", length + "px");
			slideBar.getTrack().setHeight(size + "px");
		} else {
			length = width-size;
			DOM.setStyleAttribute(slideBar.getTrack().getElement(), "marginRight", length + "px");
		}
	}
	
	private void doValueChange() {
		doValueChange(false);
	}
	
	private void doValueChange(boolean changeFromSensor) {
		if (value != lastValue) {
			if (!changeFromSensor) {
				lastValue = value;
				if (hasControlCommand) {
					eventBus.fireEvent(new CommandSendEvent(getId(), new Integer(value).toString(), this));
				}
			} else {
				
			}
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
			return width + 2;
		} else {
			return height + 2;
		}
	}
	
	public int getWidth() {
		if (isVertical) {
			return height + 2;
		} else {
			return width + 2;
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
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setMax(entity.getMax().getValue());
		component.setMin(entity.getMin().getValue());
		component.setSensor(new Sensor(entity.getLink()));
		component.setIsVertical(entity.getVertical());
		component.setHasControlCommand(true);
		return component;
	}
}
