/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.component;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

public class FlexSliderTable extends FlexTable {

   private Image minImage = new Image();
   private Image thumbImage = new Image();
   private Image maxImage = new Image();
   private boolean vertical = false;
   
   public FlexSliderTable() {
      setCellPadding(0);
      setCellSpacing(0);
      setBorderWidth(0);
      initHorizontalSlider();
   }

   public void setMinImageUrl(String minImageUrl) {
      minImage.setUrl(minImageUrl);
   }

   public void setThumbImageUrl(String thumbImageUrl) {
      thumbImage.setUrl(thumbImageUrl);
   }

   public void setMaxImageUrl(String maxImageUrl) {
      maxImage.setUrl(maxImageUrl);
   }
   
   public void setMinTrackImageUrl(String minTrackImageUrl) {
      if (!vertical) {
         getCellFormatter().getElement(0, 1).getStyle().setProperty("backgroundImage", "url(" + minTrackImageUrl + ")");
      } else {
         getCellFormatter().getElement(3, 0).getStyle().setProperty("backgroundImage", "url(" + minTrackImageUrl + ")");
      }
   }
   
   public void setMaxTrackImageUrl(String maxTrackImageUrl) {
      if (!vertical) {
         getCellFormatter().getElement(0, 3).getStyle().setProperty("backgroundImage", "url(" + maxTrackImageUrl + ")");
      } else {
         getCellFormatter().getElement(1, 0).getStyle().setProperty("backgroundImage", "url(" + maxTrackImageUrl + ")");
      }
   }

   public boolean isVertical() {
      return vertical;
   }

   public void setVertical(boolean vertical) {
      if (this.vertical != vertical) {
         if (this.vertical) {
            verticalToHorizontal();
         } else {
            horizontalToVertical();
         }
      }
      this.vertical = vertical;
   }
   
   private void horizontalToVertical() {
      this.removeRow(0);
      this.clear();
      initVerticalSlider();
   }
   
   private void verticalToHorizontal() {
      this.removeCell(0, 0);
      this.removeCell(1, 0);
      this.removeCell(2, 0);
      this.removeCell(3, 0);
      this.removeCell(4, 0);
      this.clear();
      initHorizontalSlider();
   }
   
   private void initHorizontalSlider() {
      setSize("150", "20");
      minImage.setUrl("./resources/images/custom/slider/left_handle.png");
      thumbImage.setUrl("./resources/images/custom/slider/slider_handle.png");
      maxImage.setUrl("./resources/images/custom/slider/right_handle.png");
      setWidget(0, 0, minImage);
      setWidget(0, 1, null);
      setWidget(0, 2, thumbImage);
      setWidget(0, 3, null);
      setWidget(0, 4, maxImage);
      getCellFormatter().addStyleName(0, 1, "slider-left-track");
      getCellFormatter().addStyleName(0, 3, "slider-right-track");
      
   }
   
   private void initVerticalSlider() {
      setSize("20", "150");
      minImage.setUrl("./resources/images/custom/slider/left_handle.png");
      thumbImage.setUrl("./resources/images/custom/slider/slider_handle.png");
      maxImage.setUrl("./resources/images/custom/slider/right_handle.png");
      setWidget(0, 0, maxImage);
      setWidget(1, 0, null);
      setWidget(2, 0, thumbImage);
      setWidget(3, 0, null);
      setWidget(4, 0, minImage);
      getCellFormatter().addStyleName(1, 0, "slider-top-track");
      getCellFormatter().addStyleName(3, 0, "slider-bottom-track");
   }
   
}
