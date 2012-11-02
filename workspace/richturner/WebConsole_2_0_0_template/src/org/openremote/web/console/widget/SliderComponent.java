/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
import org.openremote.web.console.panel.entity.component.SliderMinMax;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.util.ImageContainer;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class SliderComponent extends InteractiveConsoleComponent implements SensorChangeHandler {
	public static final String CLASS_NAME = "sliderComponent";
	public static final String THUMB_CLASS_NAME = CLASS_NAME + "Thumb";
	public static final String BAR_CLASS_NAME = CLASS_NAME + "Bar";
	public static final String TRACK_CLASS_NAME = CLASS_NAME + "Track";
	public static int TRACK_HEIGHT = 20;
	public static int TRACK_BORDER = 4;
	public static int THUMB_SIZE = 30;
	public static int MIN_THUMB_CLICK_AREA_SIZE = 44;
	public static final double MAX_MIN_IMAGE_SIZE_RATIO_LIMIT = 0.2;
	public static final int MAX_MIN_IMAGE_TRACK_SPACING = 2;
	private Thumb thumb;
	private SlideBar slideBar;
	private MinButton minButton;
	private MaxButton maxButton;
	private TrackMinMax minTrack;
	private TrackMinMax maxTrack;
	private int width;
	private int height;
	private boolean isVertical = false;
	private int minValue = 0;
	private int maxValue = 100;
	private int valueRange = 100;
	private ImageContainer thumbImage = null;
	private ImageContainer minImage = null;
	private ImageContainer maxImage = null;
	private ImageContainer minTrackImage = null;
	private ImageContainer maxTrackImage = null;
	private Grid track = null;
	private int minThumbPos = 0;
	private int thumbRange = 0;
	private int lastValue = 0;
	private int value = 0;
	private int stepSize = 1;
	private boolean trackHidden = false;
	
	static {
		int[] size;
		size = BrowserUtils.getSizeFromStyle(THUMB_CLASS_NAME);
		THUMB_SIZE = size[1] == 0 ? THUMB_SIZE : size[1];
		size = BrowserUtils.getSizeFromStyle(THUMB_CLASS_NAME + "Touch");
		MIN_THUMB_CLICK_AREA_SIZE = size[1] == 0 ? MIN_THUMB_CLICK_AREA_SIZE : size[1];
		size = BrowserUtils.getSizeFromStyle(TRACK_CLASS_NAME);
		TRACK_HEIGHT = size[1] == 0 ? TRACK_HEIGHT : size[1] < TRACK_HEIGHT ? TRACK_HEIGHT : size[1];
		TRACK_BORDER = size[1] - size[3];
	}
	
	class Thumb extends SimplePanel implements Draggable {
		Widget visibleThumb;
		int width = 0;
		int height = 0;
			
		public Thumb() {
			visibleThumb = new SimplePanel();
			Element visibleElem = visibleThumb.getElement();
			Element touchElem = getElement();
			
			visibleThumb.setStylePrimaryName(THUMB_CLASS_NAME);
			
			BrowserUtils.setStyleAttributeAllBrowsers(visibleElem, "boxSizing", "border-box");
			BrowserUtils.setStyleAttributeAllBrowsers(touchElem, "boxSizing", "border-box");

			this.setWidget(visibleThumb);
		}
		
		@Override
		public void onDragStart(DragStartEvent event) {
		}

		@Override
		public void onDragMove(DragMoveEvent event) {
			if (appearsVertical()) {
				setValueFromAbsPos(event.getYPos(), false);
			} else {
				setValueFromAbsPos(event.getXPos(), false);
			}
		}

		@Override
		public void onDragEnd(DragEndEvent event) {
			updateSensor();
		}

		/*
		 *  This event occurs when press moves off the console display(non-Javadoc)
		 * @see org.openremote.web.console.event.drag.DragCancelHandler#onDragCancel(org.openremote.web.console.event.drag.DragCancelEvent)
		 */
		@Override
		public void onDragCancel(DragCancelEvent event) {
			updateSensor();
		}
		
		public void setSize(int visibleWidth, int visibleHeight) {
			if (visibleWidth < MIN_THUMB_CLICK_AREA_SIZE && visibleHeight < MIN_THUMB_CLICK_AREA_SIZE) {
				width = MIN_THUMB_CLICK_AREA_SIZE;
				height = MIN_THUMB_CLICK_AREA_SIZE;
				visibleThumb.setWidth(visibleWidth + "px");
				visibleThumb.setHeight(visibleHeight + "px");
				DOM.setStyleAttribute(visibleThumb.getElement(), "marginLeft", ((int)Math.round((double)width - visibleWidth) / 2) + "px");
				DOM.setStyleAttribute(visibleThumb.getElement(), "marginTop", ((int)Math.round((double)height - visibleHeight) / 2) + "px");
			} else {
				width = visibleWidth;
				height = visibleHeight;
				visibleThumb.setWidth("100%");
				visibleThumb.setHeight("100%");
				DOM.setStyleAttribute(visibleThumb.getElement(), "marginLeft", "0");
				DOM.setStyleAttribute(visibleThumb.getElement(), "marginTop", "0");
			}
			this.setWidth(width + "px");
			this.setHeight(height + "px");
		}
		
		public void setImage(ImageContainer image) {
			if (image != null && image.getExists()) {
				setSize(image.getNativeWidth(), image.getNativeHeight());
				
				Element elem = visibleThumb.getElement();
				DOM.setStyleAttribute(elem, "border", "none");
				DOM.setStyleAttribute(elem, "backgroundImage", "url(" + image.getUrl() + ")");
				DOM.setStyleAttribute(elem, "backgroundRepeat", "no-repeat");
				DOM.setStyleAttribute(elem, "backgroundPosition", "center center");
				DOM.setStyleAttribute(elem, "backgroundColor", "transparent");
			}
		}
		
		public int getHeight() {
			if (isVertical) {
				return width;// + 2;
			} else {
				return height;// + 2;
			}
		}
		
		public int getWidth() {
			if (isVertical) {
				return height;// + 2;
			} else {
				return width;// + 2;
			}
		}
	}
	
	class SlideBar extends AbsolutePanel {
		private boolean isClickable = true;
		private int length = 0;
		
		public SlideBar() {
			Element element = getElement();
			this.setWidth("100%");
			this.setHeight("100%");
			this.setStylePrimaryName(BAR_CLASS_NAME);
			DOM.setStyleAttribute(element, "overflow", "hidden");
			BrowserUtils.setStyleAttributeAllBrowsers(element, "boxSizing", "border-box");
			
			track = new Grid();

			track.setWidth("100%");
			track.setHeight("100%");
			track.setStylePrimaryName(TRACK_CLASS_NAME);

			BrowserUtils.setStyleAttributeAllBrowsers(track.getElement(), "boxSizing", "border-box");
			minTrack = new TrackMinMax();
			minTrack.setHeight("100%");
			minTrack.setWidth("100%");
			minTrack.setStylePrimaryName(TRACK_CLASS_NAME + "Min");
			maxTrack = new TrackMinMax();
			maxTrack.setHeight("100%");
			maxTrack.setWidth("100%");
			maxTrack.setStylePrimaryName(TRACK_CLASS_NAME + "Max");
			
			this.add(track,0,0);
		}
		
		public int getLength() {
			return length;
//			if (isVertical) {
//				return height;// + 2;
//			} else {
//				return width;// + 2;
//			}
		}
		
		public void setLength(int length) {
			this.length = length;
			if (isVertical) {
				this.setHeight(length + "px");// + 2;
			} else {
				this.setWidth(length + "px");// + 2;
			}
		}
		
		protected Widget getTrack() {
			return track;
		}
	}
	
	class MinButton extends SimplePanel implements TapHandler {
		@Override
		public void onTap(TapEvent event) {
			setValue(value-1,true, true);
		}		
	}
	
	class MaxButton extends SimplePanel implements TapHandler {
		@Override
		public void onTap(TapEvent event) {
			setValue(value+1,true, true);
		}		
	}
	
	class TrackMinMax extends SimplePanel implements TapHandler {
		@Override
		public void onTap(TapEvent event) {
			if (!slideBar.isClickable) {
				return;
			}
			int absPos = 0;
			
			if (appearsVertical()) {
				absPos = event.getYPos();
			} else {
				absPos = event.getXPos();
			}
			
			setValueFromAbsPos(absPos, true);
		}
	}
	
	private SliderComponent() {
		// Define container widget
		super(new Grid(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		
		// Define child components
		slideBar = new SlideBar();
		this.setWidth("100%");
		this.setHeight("100%");
		thumb = new Thumb();
		((AbsolutePanel)slideBar).add(thumb,0,0);
		minButton = new MinButton();
		maxButton = new MaxButton();
		
		addInteractiveChild(thumb);
		addInteractiveChild(minTrack);
		addInteractiveChild(maxTrack);
		addInteractiveChild(minButton);
		addInteractiveChild(maxButton);	
		
	}
	
	private int calculateRelativePosValue(int absValue) {
		int value = absValue;
		boolean appearsVertical = appearsVertical();
		
		if (isVertical) {
			if (appearsVertical) {
				value = (slideBar.getAbsoluteTop() + slideBar.getLength()) - absValue;
			} else {
				// Two orientations this can occur
				value = absValue - slideBar.getAbsoluteLeft();
				if (WebConsole.getConsoleUnit().getOrientation().equalsIgnoreCase("landscape")) {
					value = (slideBar.getAbsoluteLeft() + slideBar.getLength()) - absValue;
				}
			}
		} else {
			if (appearsVertical) {
				// Two orientations this can occur
				value = absValue - slideBar.getAbsoluteTop();
				if (WebConsole.getConsoleUnit().getOrientation().equalsIgnoreCase("landscape")) {
					value = (slideBar.getAbsoluteTop() + slideBar.getLength()) - absValue;
				}
			} else {
				value = absValue - slideBar.getAbsoluteLeft();
			}
		}

//		int pixelMin = 0;
//		int consoleCentreX = (int)Math.round(((double)BrowserUtils.getWindowWidth()/2));
//		int consoleCentreY = (int)Math.round(((double)BrowserUtils.getWindowHeight()/2));
//		if (((appearsVertical && !isVertical) || (!appearsVertical && isVertical)) && (BrowserUtils.isCssDodgy)) {
//			if (!isVertical) {
//				//Window.alert(absValue +"");
//				pixelMin = consoleCentreY + (consoleCentreX - getAbsoluteLeft());
//			} else {
//				pixelMin = consoleCentreX + (consoleCentreY - (slideBar.getAbsoluteTop() + slideBar.getLength()));
//			}
//		} else {
//			if (appearsVertical) {
//				pixelMin = slideBar.getAbsoluteTop() + slideBar.getLength();
//			} else {
//				if (isVertical) {
//					pixelMin = getAbsoluteLeft() + slideBar.getLength();
//				} else {
//					pixelMin = getAbsoluteLeft();
//				}
//			}
//		}		
//
//		if (appearsVertical) {
//			value = pixelMin - value;
//		} else {
//			if (isVertical) {
//				value = pixelMin - value;		
//			} else {
//				value = value - pixelMin;				
//			}
//		}

		value = value < minThumbPos ? minThumbPos : value > minThumbPos + thumbRange ? minThumbPos + thumbRange : value;  

		return value;
	}
	
	public boolean appearsVertical() {
		ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
		boolean displayUnitOrientationMatch = consoleUnit.getOrientation().equals(consoleUnit.getConsoleDisplay().getOrientation());
		return ((isVertical && displayUnitOrientationMatch) || (!isVertical && !displayUnitOrientationMatch));
	}
	
	public void setValue(int value, boolean updateSensor, boolean updateThumb) {
		if (value == this.value) return;

		value = value < minValue ? minValue : value;
		value = value > maxValue ? maxValue : value;
		value = Math.round(value/stepSize) * stepSize;
		
		if (value != this.value) {
			if (updateThumb) {
				int relPos = convertValueToRelativePos(value);
				setThumbPosition(relPos);
			}
		
			this.value = value;
			
			if (updateSensor) {
				updateSensor();
			}
		}
	}
	
	private void updateSensor() {
		if (hasControlCommand && lastValue != value) {
			System.out.println("SENSOR VALUE: " + value);
			eventBus.fireEvent(new CommandSendEvent(getId(), new Integer(value).toString(), this));
			lastValue = value;
		}
	}
	
	private void setValueFromAbsPos(int absPos, boolean updateSensor) {
		int relPos = calculateRelativePosValue(absPos);
		setValueFromRelPos(relPos, updateSensor);
	}
	
	private void setValueFromRelPos(int relPos, boolean updateSensor) {
		// Get the value corresponding to this position
		int value = convertRelativePosToValue(relPos);
		setThumbPosition(relPos);
		setValue(value, updateSensor, false);
	}
	
	private int convertRelativePosToValue(int relPos) {
		double valuePerPixel = (double)valueRange / thumbRange;
		int value = (int)Math.round(valuePerPixel * (relPos)) + minValue;
		return value;
	}
	
	private int convertValueToRelativePos(int value) {
		double pixelPerValue = (double)thumbRange / valueRange;
		int relPos = (int)Math.round(pixelPerValue * (value - minValue));
		return relPos;		
	}
	
	// Position in pixels of thumb along slider
	private void setThumbPosition(int pos) {
		pos = pos < minThumbPos ? minThumbPos : pos > minThumbPos + thumbRange ? minThumbPos + thumbRange : pos;
		
		if (isVertical) {
			DOM.setStyleAttribute(thumb.getElement(), "top", (slideBar.getLength() - pos - minThumbPos) + "px");			
		} else {
			DOM.setStyleAttribute(thumb.getElement(), "left", (pos - minThumbPos) + "px");
		}
		setTrackMinMaxPosition(pos);
	}
	
	// Adjusts the min/max track widgets so they line up with thumb
	private void setTrackMinMaxPosition(int pos) {
		int availableSpace = trackHidden ? slideBar.getLength() : slideBar.getLength() - TRACK_BORDER;
		
		if (isVertical) {
			maxTrack.setHeight((availableSpace - pos) + "px");
			minTrack.setHeight((pos) + "px");
		} else {
			minTrack.setWidth((pos) + "px");
			maxTrack.setWidth((availableSpace - pos) + "px");
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
	
	private void hideTrack() {
		if (isVertical) {
			track.setWidth(width + "px");
			minTrack.setWidth(width + "px");
			maxTrack.setWidth(width + "px");
		} else {
			track.setHeight(height + "px");
			minTrack.setHeight(height + "px");
			maxTrack.setHeight(height + "px");
		}
		Widget track1;
		Widget track2;
		DOM.setStyleAttribute(track.getElement(),"border", "none");
		DOM.setStyleAttribute(track.getElement(),"overflow", "visible");
		DOM.setStyleAttribute(track.getElement(),"margin", "0");
		if (isVertical)
		{
			track1 = track.getWidget(0,0);
			track2 = track.getWidget(1,0);
		} else {
			track1 = track.getWidget(0,0);
			track2 = track.getWidget(0,1);			
		}
		track1.setStylePrimaryName(TRACK_CLASS_NAME + "Invisible");
		track2.setStylePrimaryName(TRACK_CLASS_NAME + "Invisible");
		DOM.setStyleAttribute(track1.getElement(),"backgroundColor", "transparent");
		DOM.setStyleAttribute(track1.getElement(),"border", "none");
		DOM.setStyleAttribute(track2.getElement(),"backgroundColor", "transparent");
		DOM.setStyleAttribute(track2.getElement(),"border", "none");
		trackHidden = true;
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHODS BELOW
	// ---------------------------------------------------------------------------------
	
	public void setThumbImage(String src) {
		if (src != null && !src.equals("")) {
			ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
			String url = consoleUnit.getControllerService().getController().getUrl();
			url += src;
			this.thumbImage = consoleUnit.getImageFromCache(url);
		}
	}
	
	public void setMin(SliderMinMax min) {
		this.minValue = min.getValue();
		
		String minImage = min.getImage();
		if (minImage != null && !minImage.equals("")) {
			ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
			String url = consoleUnit.getControllerService().getController().getUrl();
			url += minImage;
			this.minImage = consoleUnit.getImageFromCache(url);
		}
		String minTrackImage = min.getTrackImage();
		if (minTrackImage != null && !minTrackImage.equals("")) {
			ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
			String url = consoleUnit.getControllerService().getController().getUrl();
			url += minTrackImage;
			this.minTrackImage = consoleUnit.getImageFromCache(url);
		}
	}

	public void setMax(SliderMinMax max) {
		this.maxValue = max.getValue();
		
		String maxImage = max.getImage();
		if (maxImage != null && !maxImage.equals("")) {
			ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
			String url = consoleUnit.getControllerService().getController().getUrl();
			url += maxImage;
			this.maxImage = consoleUnit.getImageFromCache(url);
		}
		String maxTrackImage = max.getTrackImage();
		if (maxTrackImage != null && !maxTrackImage.equals("")) {
			ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
			String url = consoleUnit.getControllerService().getController().getUrl();
			url += maxTrackImage;
			this.maxTrackImage = consoleUnit.getImageFromCache(url);
		}
	}
	
	public void setIsVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}
	
	
	public void sizeMinMaxImage(ImageContainer imageContainer) {
		int imageWidth = imageContainer.getNativeWidth();
		int imageHeight = imageContainer.getNativeHeight();
		double imageRatio = (double)imageWidth / imageHeight;
		
		if (isVertical) {
			if (imageHeight > (MAX_MIN_IMAGE_SIZE_RATIO_LIMIT * height)) {
				imageHeight = (int) Math.round(MAX_MIN_IMAGE_SIZE_RATIO_LIMIT * height);
				imageWidth = (int) Math.round(imageRatio * imageHeight); 
			}
			if (imageWidth > width) {
				imageWidth = width;
				imageHeight = (int) Math.round(imageWidth / imageRatio);
			}
		} else {
			if (imageWidth > (MAX_MIN_IMAGE_SIZE_RATIO_LIMIT * width)) {
				imageWidth = (int) Math.round(MAX_MIN_IMAGE_SIZE_RATIO_LIMIT * width);
				imageHeight = (int) Math.round(imageWidth / imageRatio); 
			}
			if (imageHeight > height) {
				imageHeight = height;
				imageWidth = (int) Math.round(imageHeight * imageRatio);
			}			
		}
		
		imageContainer.getImage().setWidth(imageWidth + "px");
		imageContainer.getImage().setHeight(imageHeight + "px");
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		if (!isInitialised) {
			int slideBarSize = 0;
			Grid container = (Grid)getWidget();
			CellFormatter formatter = container.getCellFormatter();
			CellFormatter trackFormatter = track.getCellFormatter();
			
			this.width = width;
			this.height = height;
			
			// Configure the widget container and track
			if (isVertical) {
				container.resize(3, 1);
				formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				formatter.setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				formatter.setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				formatter.setVisible(0, 0, false);
				formatter.setVisible(2, 0, false);
				container.setWidget(1, 0, slideBar);
				track.resize(2, 1);
				trackFormatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				trackFormatter.setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
			} else {
				container.resize(1, 3);
				formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				formatter.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				formatter.setAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				formatter.setVisible(0, 0, false);
				formatter.setVisible(0, 2, false);
				container.setWidget(0, 1, slideBar);
				track.resize(1, 2);
				trackFormatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				trackFormatter.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
			}
			
			// Configure value range
			valueRange = maxValue - minValue;
			
			
			// Configure Min/Max Images and Slide Bar Container
			if (minImage != null && minImage.getExists())
			{
				minButton.setWidth(minImage.getNativeWidth() + "px");
				minButton.setHeight(minImage.getNativeHeight() + "px");
				minButton.setStylePrimaryName(CLASS_NAME + "Min");
				Element elem = minButton.getElement();
				DOM.setStyleAttribute(elem, "backgroundImage", "url(" + minImage.getUrl() + ")");
				DOM.setStyleAttribute(elem, "backgroundRepeat", "no-repeat");
				DOM.setStyleAttribute(elem, "backgroundPosition", "center center");
				DOM.setStyleAttribute(elem, "backgroundColor", "transparent");
				DOM.setStyleAttribute(elem, "border", "none");
				
				minButton.setVisible(true);
				DOM.setStyleAttribute(minButton.getElement(), isVertical ? "marginTop" : "marginRight", MAX_MIN_IMAGE_TRACK_SPACING + "px");
				sizeMinMaxImage(minImage);
				
				// Set Margin to centralise image
//				int margin = isVertical ? (int)(((double)width - minImg.getWidth()) / 2) : (int)(((double)height - minImg.getHeight()) / 2);
//				DOM.setStyleAttribute(minImg.getElement(), isVertical ? "marginLeft" : "marginTop", margin + "px");
				if (isVertical) {
					container.setWidget(2, 0, minButton);
					formatter.setVisible(2, 0, true);
				} else {
					container.setWidget(0, 0, minButton);
					formatter.setVisible(0, 0, true);
				}
			}
			
			if (maxImage != null && maxImage.getExists())
			{
				maxButton.setWidth(maxImage.getNativeWidth() + "px");
				maxButton.setHeight(maxImage.getNativeHeight() + "px");
				maxButton.setStylePrimaryName(CLASS_NAME + "Max");
				Element elem = maxButton.getElement();
				DOM.setStyleAttribute(elem, "backgroundImage", "url(" + maxImage.getUrl() + ")");
				DOM.setStyleAttribute(elem, "backgroundRepeat", "no-repeat");
				DOM.setStyleAttribute(elem, "backgroundPosition", "center center");
				DOM.setStyleAttribute(elem, "backgroundColor", "transparent");
				DOM.setStyleAttribute(elem, "border", "none");

				maxButton.setVisible(true);
				DOM.setStyleAttribute(maxButton.getElement(), isVertical ? "marginBottom" : "marginLeft", MAX_MIN_IMAGE_TRACK_SPACING + "px");
				sizeMinMaxImage(maxImage);
				
				// Set Margin to centralise image vertically				
//				int margin = isVertical ? (int)(((double)width - maxImg.getWidth()) / 2) : (int)(((double)height - maxImg.getHeight()) / 2);
//				DOM.setStyleAttribute(maxImg.getElement(), isVertical ? "marginLeft" : "marginTop", margin + "px");
				if (isVertical) {
					container.setWidget(0, 0, maxButton);
					formatter.setVisible(0, 0, true);
				} else {
					container.setWidget(0, 2, maxButton);
					formatter.setVisible(0, 2, true);
				}
			}
			
			
			// Configure Slide Bar			
			if (isVertical) {
				slideBarSize = (height -
						(minImage != null ? (minImage.getNativeHeight() + MAX_MIN_IMAGE_TRACK_SPACING) : 0) -
						(maxImage != null ? (maxImage.getNativeHeight() + MAX_MIN_IMAGE_TRACK_SPACING) : 0));
			} else {
				slideBarSize = (width -
						(minImage != null ? (minImage.getNativeWidth() + MAX_MIN_IMAGE_TRACK_SPACING) : 0) -
						(maxImage != null ? (maxImage.getNativeWidth() + MAX_MIN_IMAGE_TRACK_SPACING) : 0));
			}
			if (isVertical) {
				slideBar.setWidth(width + "px");
				track.setHeight(slideBarSize + "px");
			} else {
				slideBar.setHeight(height + "px");
				track.setWidth(slideBarSize + "px");
			}
			slideBar.setLength(slideBarSize);
			
			
			// Configure Thumb
			if (thumbImage != null && thumbImage.getExists()) {
				thumb.setImage(thumbImage); // This will call setSize also
			} else {
				thumb.setSize(THUMB_SIZE, THUMB_SIZE);
			}
			int thumbWidth = thumb.getWidth();
			minThumbPos = (int)Math.round((double)thumbWidth/2);
			thumbRange = slideBarSize - thumbWidth;
			
			// Set margin on static orientation (top for horizontal slider and left for vertical slider)
			int thumbMargin = (int)Math.round(((double)getHeight() - thumb.getHeight()) / 2);
			DOM.setStyleAttribute(thumb.getElement(), isVertical ? "marginLeft" : "marginTop" , thumbMargin + "px");
			
			// Configure Track
			int availableSpace = TRACK_HEIGHT - TRACK_BORDER;
			if (isVertical) {
				track.setWidth(TRACK_HEIGHT + "px");
				minTrack.setWidth(TRACK_HEIGHT + "px");
				maxTrack.setWidth(TRACK_HEIGHT + "px");
				track.setWidget(0, 0, maxTrack);
				track.setWidget(1, 0, minTrack);
				//track.getWidget(0).setWidth(availableSpace + "px");
				//track.getWidget(1).setWidth(availableSpace + "px");
			} else {
				 track.setHeight(TRACK_HEIGHT + "px");
				 minTrack.setHeight(TRACK_HEIGHT + "px");
				 maxTrack.setHeight(TRACK_HEIGHT + "px");
				 track.setWidget(0, 0, minTrack);
				 track.setWidget(0, 1, maxTrack);
				 //track.getWidget(0).setHeight(availableSpace + "px");
				 //track.getWidget(1).setHeight(availableSpace + "px");
			}
			
			// Set margin to vertically centre align track (top for horizontal slider and left for vertical slider)
			int trackMargin = (int)Math.round(((double)getHeight() - TRACK_HEIGHT) / 2);
			DOM.setStyleAttribute(track.getElement(), isVertical ? "marginLeft" : "marginTop" , trackMargin + "px");
			
			if (minTrackImage != null && minTrackImage.getExists()) {
				if (!trackHidden) {
					hideTrack();
				}
				Element elem = minTrack.getElement();
				DOM.setStyleAttribute(elem, "backgroundImage", "url(" + minTrackImage.getUrl() + ")");
				DOM.setStyleAttribute(elem, "backgroundRepeat", isVertical ? "repeat-y" : "repeat-x");
				DOM.setStyleAttribute(elem, "backgroundPosition", isVertical ? "center bottom" : "left center");
				DOM.setStyleAttribute(elem, "backgroundColor", "transparent");
			}
			
			if (maxTrackImage != null && maxTrackImage.getExists()) {
				if (!trackHidden) {
					hideTrack();
				}
				Element elem = maxTrack.getElement();
				DOM.setStyleAttribute(elem, "backgroundImage", "url(" + maxTrackImage.getUrl() + ")");
				DOM.setStyleAttribute(elem, "backgroundRepeat", isVertical ? "repeat-y" : "repeat-x");
				DOM.setStyleAttribute(elem, "backgroundPosition", isVertical ? "center top" : "right center");
				DOM.setStyleAttribute(elem, "backgroundColor", "transparent");
			}

			// Initialise Thumb and Track
			setThumbPosition(minThumbPos);
		}
	}
	
	@Override
	public void onUpdate(int width, int height) {

	}
	
	@Override
	public void onSensorAdd() {}
	
	@Override
	public void sensorChanged(String newValue) {
		try {
			int value = Integer.parseInt(newValue);
			value = value < minValue ? minValue : value;
			value = value > maxValue ? maxValue : value;
			setValue(value, false, true);
//			doValueChange(true);
		} catch(Exception e) {}
	}

	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.SliderComponent entity) {
		SliderComponent component = new SliderComponent();
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setMax(entity.getMax());
		component.setMin(entity.getMin());
		component.setThumbImage(entity.getThumbImage());
		component.setSensor(new Sensor(entity.getLink()));
		component.setIsVertical(entity.getVertical());
		component.setHasControlCommand(true);
		return component;
	}
}
