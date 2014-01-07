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
package org.openremote.web.console.view;

import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.ext.SpinnerComponent;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class LoadingScreenView extends ScreenViewImpl {
	public static final String LOADING_MESSAGE = "LOADING....";
	
	public LoadingScreenView() {
		// Create a spinning HTML Component
		SpinnerComponent htmlSpinner = new SpinnerComponent();
		htmlSpinner.setSize(150);

		// Create a label component for the loading message text
		LabelComponent msgWidget = (LabelComponent) LabelComponent.build(null);
		msgWidget.setText(LOADING_MESSAGE);
		msgWidget.getElement().setAttribute("style", "color: #245E36; text-shadow: -1px -1px 0 #4FA800, 1px -1px 0 #4FA800, -1px 1px 0 #4FA800, 1px 1px 0 #4FA800; font-weight: bold; font-size: 25px; font-family: verdana, arial, sans-serif; letter-spacing: 1px; margin-top: 90px;");

		// Create container for spinner
		AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
		absPanel.setHeight("100%");
		absPanel.setWidth("100%");
		absPanel.setPosition(0, 0, null, null);
		absPanel.setComponent(htmlSpinner);
		
		// Create container for label
		AbsolutePanelComponent absPanel2 = new AbsolutePanelComponent();
		absPanel2.setHeight("100%");
		absPanel2.setWidth("100%");
		absPanel2.setPosition(0, 0, null, null);
		absPanel2.setComponent(msgWidget);
		
		// Add components to screen view
		super.addPanelComponent(absPanel);
		super.addPanelComponent(absPanel2);
	}
}
