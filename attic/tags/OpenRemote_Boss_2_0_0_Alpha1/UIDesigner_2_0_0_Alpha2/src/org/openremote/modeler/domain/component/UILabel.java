/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.Sensor;

/**
 * 
 * @author Javen
 * 
 */
@SuppressWarnings("serial")
public class UILabel extends UIComponent implements SensorOwner {

   private String text = "Label Text";
   private String color = "FFFFFF";
   private int fontSize = 10;

   private Sensor sensor;

   private SensorLink sensorLinker;

   public UILabel(long oid) {
      super(oid);
   }

   public UILabel() {
   }

   public UILabel(String text, String color, int fontSize, Sensor sensor) {
      this.text = text;
      this.color = color;
      this.fontSize = fontSize;
      this.sensor = sensor;
      if (sensor != null) {
         this.sensorLinker = new SensorLink(sensor);
      } else {
         sensorLinker.clear();
      }
   }

   public UILabel(UILabel uiLabel) {
      setOid(uiLabel.getOid());
      this.text = uiLabel.text;
      this.fontSize = uiLabel.fontSize;
      this.color = uiLabel.color;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getColor() {
      return color;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public int getFontSize() {
      return fontSize;
   }

   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
   }

   public Sensor getSensor() {
      return sensor;
   }

   public void setSensor(Sensor sensor) {
      this.sensor = sensor;
      if (sensor != null) {
         this.sensorLinker = new SensorLink(sensor);
      } else {
         sensorLinker.clear();
      }
   }

   public SensorLink getSensorLinker() {
      return sensorLinker;
   }

   public void setSensorLinker(SensorLink sensorLinker) {
      this.sensorLinker = sensorLinker;
   }

   @Transient
   @Override
   public String getPanelXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<label id=\"" + getOid() + "\" font-size=\"" + fontSize + "\" color=\"#" + color + "\" text=\"" + text
            + "\">\n");
      if (sensor != null) {
         sb.append(sensorLinker.getXMLString());
      }
      sb.append("</label>");
      return sb.toString();
   }

   @Override
   public String getName() {
      return "Label";
   }

   @Transient
   public String getDisplayName() {
      int maxLength = 10;
      if (text.length() > maxLength) {
         return text.substring(0, text.length() - maxLength) + "...";
      }
      return text;
   }

   @Override
   public int getPreferredWidth() {
      return 150;
   }

   @Override
   public int getPreferredHeight() {
      return 50;
   }
}
