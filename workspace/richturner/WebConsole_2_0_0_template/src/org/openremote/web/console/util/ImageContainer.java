package org.openremote.web.console.util;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class ImageContainer implements LoadHandler {
	private boolean exists = false;
	private Image image = null;
	private ImageLoadedCallback loadedCallback = null;
	
	public ImageContainer(Image image) {
		this(image, null);
	}
	
	public ImageContainer(Image image, ImageLoadedCallback loadedCallback) {
		this.loadedCallback = loadedCallback;
		image.addLoadHandler(this);
		image.setVisible(false);
		this.image = image;
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
	
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
	public boolean getExists() {
		return exists;
	}
	
	public void setVisible(boolean visible) {
		if (image != null && exists) {
			image.setVisible(visible);
		}
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return image;
	}

	@Override
	public void onLoad(LoadEvent event) {
		exists = true;
		if (loadedCallback != null) {
			loadedCallback.onImageLoaded(this);
		}
	}
}
