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
package org.openremote.web.console.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ImageContainer implements LoadHandler, ErrorHandler {
	private boolean exists = false;
	private boolean existCheckDone = false;
	private boolean loadAttempted = false;
	private Image image = null;
	private int nativeWidth = 0;
	private int nativeHeight = 0;
	private List<ImageLoadedCallback> loadedCallbacks = new ArrayList<ImageLoadedCallback>();
	
	public ImageContainer(String imageUrl) {
		this(imageUrl, false);
	}
	public ImageContainer(String imageUrl, boolean isSecure) {
		image = new Image(imageUrl);
		//this.loadedCallbacks.add(loadedCallback);
		image.addLoadHandler(this);
		image.addErrorHandler(this);
		image.setVisible(false);
		
		if (!image.getUrl().equalsIgnoreCase("")) {
			Image.prefetch(image.getUrl());
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

	private void onLoaded() {
		if (loadAttempted) return;
		
		nativeWidth = image.getWidth();
		nativeHeight = image.getHeight();
		exists = true;
		loadAttempted = true;
		if (!existCheckDone) {
			RootPanel.get().remove(image);
			existCheckDone = true;
		}
		for (ImageLoadedCallback loadedCallback : loadedCallbacks) {
			if (loadedCallback != null) {
				loadedCallback.onImageLoaded(this);
			}
		}
		loadedCallbacks.clear();
	}
	
	@Override
	public void onLoad(LoadEvent event) {
		onLoaded();
	}

	@Override
	public void onError(ErrorEvent event) {
		loadAttempted = true;		
	}
	
	private void setImage(Image image)
	{
		this.image = image;
	}
	
	public ImageContainer clone()
	{
		ImageContainer container = null;
		container = new ImageContainer(this.getUrl());
		container.existCheckDone = existCheckDone;
		container.exists = exists;
		container.loadAttempted = loadAttempted;
		container.nativeWidth = nativeWidth;
		container.nativeHeight = nativeHeight;
		
		if (!loadAttempted) {
			addCallback(new ImageLoadedCallback() {
				@Override
				public void onImageLoaded(ImageContainer container) {
					container.onLoaded();
				}				
			});
		}
		
		return container;
	}
}
