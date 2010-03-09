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
package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.domain.Sensor;

public class UIImage extends UIComponent implements SensorOwner {

   private static final long serialVersionUID = -4114009124680167066L;

   public static String DEFAULT_IMAGE_URL = "image/OpenRemote.Logo.30x32.png";
   
   private ImageSource imageSource = new ImageSource(DEFAULT_IMAGE_URL);

   private Sensor sensor = null;

   private UILabel label = null;

   private SensorLink sensorLinker;

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
      if (sensor != null) {
         this.sensorLinker = new SensorLink(sensor);
      } else {
         sensorLinker.clear();
      }
   }

   public UILabel getLabel() {
      return label;
   }

   public void setLabel(UILabel label) {
      this.label = label;
   }

   public SensorLink getSensorLinker() {
      return sensorLinker;
   }

   public void setSensorLinker(SensorLink sensorLinker) {
      this.sensorLinker = sensorLinker;
   }

   @Override
   public String getName() {
      return "Image";
   }

   @Transient
   @Override
   public String getPanelXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<image id=\"" + getOid() + "\" src=\"" + imageSource.getImageFileName() + "\"> ");
      if (sensor != null) {
         sb.append(sensorLinker.getXMLString());
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
}
