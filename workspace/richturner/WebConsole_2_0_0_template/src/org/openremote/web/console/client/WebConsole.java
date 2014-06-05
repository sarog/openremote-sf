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
package org.openremote.web.console.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.panel.entity.PanelSizeInfo;
import org.openremote.web.console.panel.entity.WelcomeFlag;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.EnumDataMap;
import org.openremote.web.console.service.LocalDataServiceImpl;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * This class configures the Window for mobile and desktop environments
 * and determines the size of the window we have to work with:
 * windowHeight = Longest window dimension (i.e. portrait)
 * windowWidth = Shortest window dimension
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class WebConsole implements EntryPoint {
	private static ConsoleUnit consoleUnit;
	public static final String WELCOME_MESSAGE_STRING = "Welcome to the latest Web Console!\n\nClick <a href=\"http://openremote.org/display/docs/Web+Console\" target=\"_blank\">here</a> for release notes and help on using the Web Console.";
	public static final String COOKIE_WARNING_MESSAGE_STRING = "Cookies / Local Storage must be enabled for the Web Console to work correctly!";
	private Logger logger = Logger.getLogger("");
	
	public void onModuleLoad() {
		
		// Create Exception alert
		GWT.setUncaughtExceptionHandler(new   
	      GWT.UncaughtExceptionHandler() {  
	      public void onUncaughtException(Throwable e) {
	      	Throwable unwrapped = unwrap(e);
	      	logger.log(Level.SEVERE, "Ex caught!", e);
	    }
		});
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {  
      public void execute() {  
        onModuleLoad2();  
      }  
    });
		
	}
	
	public void onModuleLoad2() {		
		// Export method to hide alert window from native JS
		BrowserUtils.exportStaticMethod();
		
		BrowserUtils.initWindow();
		
		BrowserUtils.setupHistory();

    // Display warning if cookies disabled
    if (!LocalDataServiceImpl.getInstance().isAvailable())
    {
      BrowserUtils.showAlert(COOKIE_WARNING_MESSAGE_STRING);
      return;
    }
    
    // Display welcome message
    if (BrowserUtils.showWelcomeMessage()) {
      // Show welcome message
      BrowserUtils.showAlert(WELCOME_MESSAGE_STRING);      
      WelcomeFlag welcomeFlag = (WelcomeFlag) AutoBeanService.getInstance().getFactory().create(EnumDataMap.WELCOME_FLAG.getClazz()).as();
      welcomeFlag.setWelcomeVersion(BrowserUtils.getBuildVersion());      
      LocalDataServiceImpl.getInstance().setObject(EnumDataMap.WELCOME_FLAG.getDataName(), AutoBeanService.getInstance().toJsonString(welcomeFlag));
    }
    
    PanelSizeInfo sizeInfo = BrowserUtils.getDefaultPanelSize();
    
    // Show sliding toolbar if not disabled
    if (BrowserUtils.showToolbar()) {
      SlidingToolbar.initialise(sizeInfo);
    }
    
    // Configure the default panel and controller
    // Check for GET variables
    String cUrl = Window.Location.getParameter("controllerURL");
    String pName = Window.Location.getParameter("panelName");
    
    if (cUrl == null || cUrl.isEmpty())
    {
      cUrl = BrowserUtils.getControllerUrlString();
    }
    
    if (pName == null || pName.isEmpty())
    {
      pName = BrowserUtils.getPanelNameString();
    }
    
    if (cUrl != null && !cUrl.isEmpty() && pName != null && !pName.isEmpty())
    {
      ControllerCredentials controllerCreds = AutoBeanService.getInstance().getFactory().create(ControllerCredentials.class).as();
      controllerCreds.setUrl(cUrl);
      controllerCreds.setDefaultPanel(pName);
      LocalDataServiceImpl.getInstance().setLastControllerCredentials(controllerCreds);
    }
    
    // Check if console frame should be shown
    String showFrameStr = Window.Location.getParameter("showConsoleFrame");
    if (showFrameStr == null || (!showFrameStr.equalsIgnoreCase("true") && !showFrameStr.equalsIgnoreCase("false"))) {
      showFrameStr = BrowserUtils.getShowFrameString();
    }
    
    if (showFrameStr != null && (showFrameStr.equalsIgnoreCase("true") || showFrameStr.equalsIgnoreCase("false"))) {
      boolean showFrame = Boolean.parseBoolean(showFrameStr);
      if (!showFrame) {
        ConsoleUnit.FRAME_WIDTH_BOTTOM = 0;
        ConsoleUnit.FRAME_WIDTH_TOP = 0;
        ConsoleUnit.FRAME_WIDTH_LEFT = 0;
        ConsoleUnit.FRAME_WIDTH_RIGHT = 0;
        ConsoleUnit.BOSS_WIDTH = 0;
      }
    }

    // Initialise the Console Unit
    boolean requestFullscreen = sizeInfo.getPanelSizeType().equals("fullscreen");
    if (BrowserUtils.isMobile || requestFullscreen) {
      // load in full screen mode with fixed orientation
      consoleUnit = new ConsoleUnit(true);
    } else {
      consoleUnit = new ConsoleUnit(sizeInfo.getPanelSizeWidth(), sizeInfo.getPanelSizeHeight());
      
      // Check orientation setting
      String orientation = Window.Location.getParameter("orientation");
      if (orientation == null || (!orientation.equalsIgnoreCase("portrait") && !orientation.equalsIgnoreCase("landscape"))) {
        orientation = BrowserUtils.getOrientationString();
      }
      
      if (orientation != null && (orientation.equalsIgnoreCase("portrait") || orientation.equalsIgnoreCase("landscape"))) {
        consoleUnit.setOrientation(orientation);
      }
    }
		
		if (consoleUnit != null) {
			RootPanel.get().add(consoleUnit, 0, 0);
			getConsoleUnit().onAdd();
		} else {
			BrowserUtils.showAlert("Failed to create Console Unit!");
		}
	}
	
  public Throwable unwrap(Throwable e) {   
    if(e instanceof UmbrellaException) {   
      UmbrellaException ue = (UmbrellaException) e;  
      if(ue.getCauses().size() == 1) {   
        return unwrap(ue.getCauses().iterator().next());  
      }  
    }  
    return e;  
  }
	
	public static ConsoleUnit getConsoleUnit() {
		return consoleUnit;
	}
}
