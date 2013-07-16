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
package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Link;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public interface SliderComponent {
	Integer getId();
	String getThumbImage();
	Boolean getVertical();
	Boolean getPassive();
	Link getLink();
	SliderMinMax getMin();
	SliderMinMax getMax();
	
	void setId(Integer id);
	void setThumbImage(String thumb);
	void setVertical(Boolean vertical);
	void setPassive(Boolean passive);
	void setLink(Link link);
	void setMin(SliderMinMax min);
	void setMax(SliderMinMax max);
}
