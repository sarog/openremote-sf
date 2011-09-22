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
package org.openremote.web.console.entity.component;

/**
 * The image component can has sensor and can change status.
 * It can also has a linked label to display as a label. 
 */
@SuppressWarnings("serial")
public class Image extends Component {

   private String src;				/** The image source */
   private String style; 			/** The image's style, but now is unUsed. */
   private Label label; 			/** The linked label. */
   private int labelRefId = 0;	/** The linked label id. */
   
   public Image() {
   }
   
   public Image(String src) {
      this.src = src;
   }
   public String getSrc() {
      return src;
   }
   public String getStyle() {
      return style;
   }
   
   public Label getLabel() {
      return label;
   }

   public void setLabel(Label label) {
      this.label = label;
   }
   
   public void setLabelRefId(int labelRefId) {
   	this.labelRefId = labelRefId;
   }
   
   public int getLabelRefId() {
      return labelRefId;
   }
}
