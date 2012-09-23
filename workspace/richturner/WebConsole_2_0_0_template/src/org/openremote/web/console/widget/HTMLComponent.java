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

import com.google.gwt.user.client.ui.HTML;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class HTMLComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "htmlComponent";
	
	protected HTMLComponent() {
		super(new HTML(), CLASS_NAME);
	}
	
	public void setHTML(String html) {
		((HTML)getWidget()).setHTML(html);
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {

	}
	
	@Override
	public void onUpdate(int width, int height) {
		
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.HTMLComponent entity) {
		HTMLComponent component = new HTMLComponent(); 
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setHTML(entity.getHtml());
		return component;
	}
}
