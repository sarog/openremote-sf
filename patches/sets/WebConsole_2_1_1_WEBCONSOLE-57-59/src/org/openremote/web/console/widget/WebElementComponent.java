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

import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.panel.entity.Link;

import com.google.gwt.user.client.ui.Frame;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class WebElementComponent extends PassiveConsoleComponent implements SensorChangeHandler {
	public static final String CLASS_NAME = "webElementComponent";
	
	private String urlPrefix = "http://";
	private String url;
	private String username;
	private String password;

	protected WebElementComponent() {
		super(new Frame(), CLASS_NAME);
	}
	
	public void setURL(String url) {
		if (url.indexOf("https://") > 0) urlPrefix = "https://";
		url = url.replace(urlPrefix, "");
		this.url = url;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	private void loadUrl()
	{
		if (url == null) return;
		
		String loadUrl = urlPrefix;
		
		if(username != null && password != null && !username.equals("") && !password.equals(""))
		{
			loadUrl += username + ":" + password + "@"; 
		}
		
		loadUrl += url;

		((Frame)getWidget()).setUrl(loadUrl);
		
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		loadUrl();
	}
	
	@Override
	public void onUpdate(int width, int height) {
		
	}
	
	@Override
	public void onSensorAdd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sensorChanged(String value) {
		setURL(value);
		loadUrl();
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.WebElementComponent entity) {
		WebElementComponent component = new WebElementComponent(); 
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setURL(entity.getSrc());
		component.setUsername(entity.getUsername());
		component.setPassword(entity.getPassword());
		component.setSensor(new Sensor(entity.getLink()));
		return component;
	}
}
