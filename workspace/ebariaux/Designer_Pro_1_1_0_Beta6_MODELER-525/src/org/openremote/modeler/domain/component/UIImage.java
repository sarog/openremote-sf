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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.utils.SensorLink.LinkerChild;
import org.openremote.modeler.domain.ConfigurationFilesGenerationContext;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import flexjson.JSON;

/**
 * UIImage defines a image source, a sensor and a linked label.
 * The default image source is OpenRemote logo.
 * 
 * The ui image can change image by sensor state, if there is no image 
 * corresponding with the sensor state and the label has been specified,
 * show the label's state on the component.
 */
public class UIImage extends UIComponent implements SensorOwner, SensorLinkOwner, ImageSourceOwner {

   private static final long serialVersionUID = -4114009124680167066L;

   public static String DEFAULT_IMAGE_URL = "image/OpenRemote.Logo.30x32.png";
   
   private ImageSource imageSource = new ImageSource(DEFAULT_IMAGE_URL);

   /** The ui image can change display image by the sensor. */
   private Sensor sensor = null;
   
   private SensorWithInfoDTO sensorDTO;

   /** The ui image can display the label's sensor text. */
   private UILabel label = null;

   /** The sensor link is for recording the properties of the sensor. */
   private SensorLink sensorLink;

   public UIImage() {
   }

   public UIImage(long oid) {
      super(oid);
   }

   public UIImage(UIImage uiImage) {
      this.setOid(uiImage.getOid());
      this.imageSource = uiImage.imageSource;
      this.sensor = uiImage.sensor;
      this.label = uiImage.label;
      this.sensorLink = uiImage.sensorLink;
      this.sensorDTO = uiImage.sensorDTO;
   }

   public ImageSource getImageSource() {
      return imageSource;
   }

   public void setImageSource(ImageSource imageSource) {
      this.imageSource = imageSource;
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

  public UILabel getLabel() {
      return label;
   }

   public void setLabel(UILabel label) {
      this.label = label;
   }

   public SensorLink getSensorLink() {
      return sensorLink;
   }

   public void setSensorLink(SensorLink sensorLinker) {
      this.sensorLink = sensorLinker;
   }

   @Override
   public String getName() {
      return "Image";
   }

   @Transient
   @Override
   public String getPanelXml(ConfigurationFilesGenerationContext context) {
      StringBuilder sb = new StringBuilder();
      sb.append("<image id=\"" + getOid() + "\" src=\"" + imageSource.getImageFileName() + "\"> ");
      if (getSensorDTO() != null) {
        sb.append("<link type=\"sensor\" ref=\"" + getSensorDTO().getOffsetId() + "\"/>");
     }
      if (label != null && label.isRemoved()==false) {
         sb.append("<include type=\"label\" ref=\"" + label.getOid() + "\"/>\n");
      }
      sb.append("</image>");
      return sb.toString();
   }

   @Override
   public int getPreferredWidth() {
      return 30;
   }

   @Override
   public int getPreferredHeight() {
      return 32;
   }

   @Override
   @JSON(include = false)
   public Collection<ImageSource> getImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(2);
      if (this.imageSource != null && !this.imageSource.isEmpty()) {
         imageSources.add(imageSource);
      } else {
         imageSources.add(new ImageSource(DEFAULT_IMAGE_URL));
      }
      if (sensor != null && sensorLink != null
            && (sensor.getType() == SensorType.SWITCH || sensor.getType() == SensorType.CUSTOM)) {
         Collection<LinkerChild> linkChildren = sensorLink.getLinkerChildren();
         if (linkChildren != null && !linkChildren.isEmpty()) {
            for (LinkerChild child : linkChildren) {
               Map<String,String> imageMap = child.getAttributes();
               if (imageMap.containsKey("value")){
                  ImageSource image = new ImageSource(imageMap.get("value"));
                  imageSources.add(image);
               }
            }
         }
      }
      return imageSources;
   }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((imageSource == null) ? 0 : imageSource.hashCode());
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    result = prime * result + ((sensorDTO == null) ? 0 : sensorDTO.equalityHashCode());
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
    UIImage other = (UIImage) obj;
    if (imageSource == null) {
      if (other.imageSource != null)
        return false;
    } else if (!imageSource.equals(other.imageSource))
      return false;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    if (sensorDTO == null) {
      if (other.sensorDTO != null)
        return false;
    } else if (!sensorDTO.equalityEquals(other.sensorDTO))
      return false;
    return true;
  }

}
