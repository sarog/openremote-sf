/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
 * The label can set font size and color, change text by polling status.
 */
@SuppressWarnings("serial")
public class Label extends Component {

   private int fontSize;
   private String color;
   private String text;
   private Link link;
   
   public Label() {
   }
   
   public int getFontSize() {
      return fontSize;
   }
   
   public void setLink(int fontSize) {
   	this.fontSize = fontSize;
   }
   
   public String getColor() {
      return color;
   }
   
   public void setColour(String color) {
   	this.color = color;
   }
   
   public String getText() {
      return text;
   }

   public void setText(String text) {
   	this.text = text;
   }
   
   public Link getLink() {
   	return link;
   }
   
   public void setLink(Link link) {
   	this.link = link;
   }
}
