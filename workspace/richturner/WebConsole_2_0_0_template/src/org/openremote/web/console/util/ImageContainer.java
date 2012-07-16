package org.openremote.web.console.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class ImageContainer implements LoadHandler, ErrorHandler {
	private boolean exists = false;
	private boolean existCheckDone = false;
	private boolean loadAttempted = false;
	private Image image = null;
	private int nativeWidth = 0;
	private int nativeHeight = 0;
	private List<ImageLoadedCallback> loadedCallbacks = new ArrayList<ImageLoadedCallback>();
	
	public ImageContainer(String imageUrl) {
		this(new Image(imageUrl), true);
	}
	
	public ImageContainer(Image image) {
		this(image, true);
	}
	
	public ImageContainer(Image image, boolean checkExists) {
		this(image, null, checkExists);
	}
	
	public ImageContainer(Image image, ImageLoadedCallback loadedCallback) {
		this(image, loadedCallback, true);
	}
	
	public ImageContainer(Image image, ImageLoadedCallback loadedCallback, boolean checkExists) {
		this.loadedCallbacks.add(loadedCallback);
		image.addLoadHandler(this);
		image.addErrorHandler(this);
		Image.prefetch(image.getUrl());
		image.setVisible(false);
		this.image = image;
		
		if (!image.getUrl().equalsIgnoreCase("")) {
			// Add image to Root Panel so that loaded callback gets fired if image does exist
			RootPanel.get().add(image);
		}
	}
	
	public void setUrl(String url) {
		if (image != null) {
			image.setUrl(url);
		}
	}
	
	public String getUrl() {
		String url = "";
		if (image != null) {
			url = image.getUrl();
		}
		return url;
	}
	
	public void addCallback(ImageLoadedCallback loadedCallback) {
		this.loadedCallbacks.add(loadedCallback);
	}
	
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
	public boolean getExists() {
		return exists;
	}
	
	public boolean getLoadAttempted() {
		return loadAttempted;
	}
	
	public void setVisible(boolean visible) {
		if (image != null && exists) {
			image.setVisible(visible);
		}
	}
	
	public Image getImage() {
		return image;
	}
	
	public int getNativeWidth() {
		return nativeWidth;
	}
	
	public int getNativeHeight() {
		return nativeHeight;
	}

	@Override
	public void onLoad(LoadEvent event) {
		if (loadAttempted) return;
		
		nativeWidth = image.getWidth();
		nativeHeight = image.getHeight();
		exists = true;
		loadAttempted = true;
		if (!existCheckDone) {
			RootPanel.get().remove(image);
			existCheckDone = false;
		}
		for (ImageLoadedCallback loadedCallback : loadedCallbacks) {
			if (loadedCallback != null) {
				loadedCallback.onImageLoaded(this);
			}
		}
		loadedCallbacks.clear();
	}

	@Override
	public void onError(ErrorEvent event) {
		loadAttempted = true;		
	}
}
