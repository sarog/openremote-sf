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

public class SliderComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "sliderComponent";
	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 40;
	public static final int MIN_SLIDE_BAR_HEIGHT = 20;
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
		protected static final int BORDER_WIDTH = 2;
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
		   	
			storeHandler(this.addHandler(this, DragStartEvent.getType()));
			storeHandler(this.addHandler(this, DragMoveEvent.getType()));
			storeHandler(this.addHandler(this, DragEndEvent.getType()));
			storeHandler(this.addHandler(this, DragCancelEvent.getType()));
		}
		
		public void onDragStart(DragStartEvent event) {
		}

		@Override
		public void onDragMove(DragMoveEvent event) {
			boolean sliderAppearsVertical = isVertical();
			
			if (!sliderAppearsVertical) {
				doHandleDrag(event.getXPos());
			} else {
				doHandleDrag(event.getYPos());
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
	
	class SlideBar extends SimplePanel implements TapHandler {
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
			
			storeHandler(this.addHandler(this, TapEvent.getType()));
		}

		@Override
		public void onTap(TapEvent event) {
			if (!clickable) {
				return;
			}
			boolean displayIsVertical = WebConsole.getConsoleUnit().getConsoleDisplay().getIsVertical();
			boolean sliderAppearsVertical = (isVertical && displayIsVertical) || (!isVertical && !displayIsVertical);
			
			if (!sliderAppearsVertical) {
				doHandleDrag(event.getXPos());
			} else {
				doHandleDrag(event.getYPos());
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

	private SliderComponent() {
		this(SliderComponent.DEFAULT_WIDTH, SliderComponent.DEFAULT_HEIGHT);
	}
	
	private SliderComponent(int width, int height) {
		super(new AbsolutePanel());
		container = (AbsolutePanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		
		this.width = width;
		this.height = height;
	}
	
	private void doHandleDrag(int absPos) {
		int relPos = calculateRelativePixelValue(absPos);
		int value = (int)(relPos * pixelValueDensity);
		setValue(value);
	}
	
	private int calculateRelativePixelValue(int absValue) {
		int value = absValue;
		int pixelMin = 0;
		boolean sliderAppearsVertical = isVertical();
		
		if (!sliderAppearsVertical) {		
			if (!isVertical) {
					pixelMin = (int)(slideBar.getAbsoluteLeft() + halfHandle);
					value = value - pixelMin;
			} else {
				pixelMin = (int)(slideBar.getAbsoluteLeft() + slideBar.getWidth() - halfHandle);
				value = pixelMin - value;
			}
		} else {
			pixelMin = (int)( slideBar.getAbsoluteTop() + slideBar.getWidth() - halfHandle);
			value = pixelMin - value;
		}
		return value;
	}
	
	public boolean isVertical() {
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
			int relPos = (int)Math.round((value-minValue)/pixelValueDensity);
			relPos = isVertical ? slideBar.getWidth() - relPos - handle.getSize() : relPos; 
			setHandlePosition(relPos);
			this.value = value;
		}
	}
	
	private void setHandlePosition(int pixelPos) {
		if (!isVertical) {
			DOM.setStyleAttribute(handle.getElement(), "left", pixelPos + "px");
		} else {
			DOM.setStyleAttribute(handle.getElement(), "top", pixelPos + "px");
		}
	}
	
	@Override
	public void onRender() {
		int handleSize = 0;
		int handleXPos = 0;
		int handleYPos = 0;
		int slideBarXPos = 0;
		int slideBarYPos = 0;
		
		// Determine the orientation of the slider
		if (height > width) {
			this.isVertical = true;
			width = width < MIN_SLIDE_BAR_HEIGHT ? MIN_SLIDE_BAR_HEIGHT : width;
		} else {
			height = height < MIN_SLIDE_BAR_HEIGHT ? MIN_SLIDE_BAR_HEIGHT : height;
		}
		
		container.setWidth(width + "px");
		container.setHeight(height + "px");
		DOM.setStyleAttribute(container.getElement(), "WebkitUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "overflow", "visible");
		
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
		
		slideBar = new SlideBar(slideBarWidth, slideBarHeight);
		handle = new Handle(handleSize);
		
		// Update size info from actual handle object
		handleSize = handle.getSize();
		halfHandle = (double)(handleSize / 2);
		
		if (isVertical) {
			handleXPos = (int)(((slideBarWidth/2) + slideBarXPos) - halfHandle);
			handleYPos = slideBarHeight - handleSize;
		} else {
			handleYPos = (int)(((slideBarHeight/2) + slideBarYPos) - halfHandle);
		}
		
		container.add(slideBar);
		container.setWidgetPosition(slideBar, slideBarXPos, slideBarYPos);
		
		container.add(handle);
		container.setWidgetPosition(handle, handleXPos, handleYPos);
		
		registerMouseAndTouchHandlers(slideBar);
		registerMouseAndTouchHandlers(handle);
		
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
	
	public void setStepSize(int size) {
		if (!isInitialised) {
			stepSize = size;
		}
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setMin(int min) {
		this.minValue = min;
	}

	public void setMax(int max) {
		this.maxValue = max;
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.SliderComponent entity) {
		SliderComponent component = new SliderComponent();
		component.setMax(entity.getMax().getValue());
		component.setMin(entity.getMin().getValue());
		return component;
	}
}
