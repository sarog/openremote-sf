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
package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.ConfigurationFilesGenerationContext;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;
import org.openremote.modeler.utils.StringUtils;

/**
 * UILabel can change text, text color, font size.
 * It also can change text by sensor state.
 * 
 * @author Javen
 */
public class UILabel extends UIComponent implements SensorOwner, SensorLinkOwner {

   private static final long serialVersionUID = 1170515762454958893L;
   
   private String text = "Label Text";
   
   /** The color use the web format, default is white. */
   private String color = "FFFFFF";
   
   /** The default font size is 14. */
   private int fontSize = 14;

   /** The ui label can change display image by the sensor. */
   private Sensor sensor;

   private SensorWithInfoDTO sensorDTO;
   
   /** The sensor link is for recording the properties of the sensor. */
   private SensorLink sensorLink;

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
         this.sensorLink = new SensorLink(sensor);
      } else {
         sensorLink.clear();
      }
   }

   public UILabel(UILabel uiLabel) {
      setOid(uiLabel.getOid());
      this.text = uiLabel.text;
      this.fontSize = uiLabel.fontSize;
      this.color = uiLabel.color;
      this.sensor = uiLabel.sensor;
      this.sensorLink = uiLabel.sensorLink;
      this.sensorDTO = uiLabel.sensorDTO;
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
   }

   public void setSensorAndInitSensorLink(Sensor sensor) {
      this.sensor = sensor;
      if (sensor != null) {
         this.sensorLink = new SensorLink(sensor);
      } else {
         sensorLink.clear();
      }
   }
   
   public SensorLink getSensorLink() {
      return sensorLink;
   }

   public void setSensorLink(SensorLink sensorLinker) {
      this.sensorLink = sensorLinker;
   }

   
   public SensorWithInfoDTO getSensorDTO() {
    return sensorDTO;
  }

  public void setSensorDTO(SensorWithInfoDTO sensorDTO) {
    this.sensorDTO = sensorDTO;
  }

  public void setSensorDTOAndInitSensorLink(SensorWithInfoDTO sensorDTO) {
    this.sensorDTO = sensorDTO;
    if (sensorDTO != null) {
       this.sensorLink = new SensorLink();
       this.sensorLink.setSensorDTO(sensorDTO);
    } else {
       sensorLink.clear();
    }
 }

  
  @Transient
   @Override
   public String getPanelXml(ConfigurationFilesGenerationContext context) {
      StringBuilder sb = new StringBuilder();
      sb.append("<label id=\"" + getOid() + "\" fontSize=\"" + fontSize + "\" color=\"#" + color + "\" text=\"" + StringUtils.escapeXml(text)
            + "\">\n");
      if (getSensorDTO() != null) {
         sb.append("<link type=\"sensor\" ref=\"" + getSensorDTO().getOffsetId() + "\"/>");
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
      int maxLength = 20;
      if (text.length() > maxLength) {
         return text.substring(0, maxLength) + "...";
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + fontSize;
    result = prime * result + ((sensorDTO == null) ? 0 : sensorDTO.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    UILabel other = (UILabel) obj;
    if (color == null) {
      if (other.color != null)
        return false;
    } else if (!color.equals(other.color))
      return false;
    if (fontSize != other.fontSize)
      return false;
    if (sensorDTO == null) {
      if (other.sensorDTO != null)
        return false;
    } else if (!sensorDTO.equals(other.sensorDTO))
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    return true;
  }
   
}
