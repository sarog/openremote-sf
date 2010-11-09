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
package org.openremote.web.console.client.widget;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.event.OREvent;
import org.openremote.web.console.client.listener.OREventListener;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.domain.Image;
import org.openremote.web.console.domain.Label;

import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.http.client.URL;

/**
 * The ScreenImage is for showing image component in screen.
 * 
 */
public class ScreenImage extends ScreenComponent implements SensoryDelegate {

   protected com.google.gwt.user.client.ui.Image imageView = new com.google.gwt.user.client.ui.Image();
   
   /** The linked label. */
   private Text linkedLabel;
   
   /**
    * Instantiates a new screen image.
    * 
    * @param image the image
    */
   public ScreenImage(Image image) {
      setComponent(image);
      if (image != null) {
         initImage(image);
         if (image.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
   }
   
   private void initImage(Image image) {
      setSize(image.getFrameWidth(), image.getFrameHeight());
      imageView.setStyleName("image-style");
      if (image.getSrc() != null) {
         setImageUrl(image.getSrc());
      }
      add(imageView);
   }
   
   /**
    * Sets the image url.
    * 
    * @param imageSrc the new image url
    */
   private void setImageUrl(String imageSrc) {
      imageView.setUrl(ClientDataBase.getResourceRootPath() + URL.encode(imageSrc));
   }
   
   /* (non-Javadoc)
    * @see org.openremote.web.console.client.widget.SensoryDelegate#addPollingSensoryListener()
    */
   public void addPollingSensoryListener() {
      Image image = (Image)getComponent();
      Integer id = image.getSensor().getSensorId();
      initLinkedLabel(image);
      if (id <= 0) {
         if (image.getLabel() != null && image.getLabel().getSensor() != null) {
            id = image.getLabel().getSensor().getSensorId();
         }
      }
      final Integer sensorId = id;
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(Constants.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String newStatus = ClientDataBase.statusMap.get(sensorId.toString());
               String newValue = ((Image)getComponent()).getSensor().getStateValue(newStatus);
               if (newValue != null) {
                  if (linkedLabel != null) {
                     linkedLabel.hide();
                  }
                  setImageUrl(newValue);
                  imageView.setVisible(true);
               } else if (((Image)getComponent()).getLabel() != null) {
                  Label label = ((Image)getComponent()).getLabel();
                  imageView.setVisible(false);
                  linkedLabel.show();
                  if (label.getSensor() != null) {
                     newValue = label.getSensor().getStateValue(newStatus);
                     if (newValue != null) {
                        linkedLabel.setText(newValue);
                     }
                  }
               }
            }
         });
      }
   }

   /**
    * Inits the linked label.
    * 
    * @param image the image
    */
   private void initLinkedLabel(Image image) {
      if (image.getLabel() != null) {
         Label label = image.getLabel();
         linkedLabel = new Text();
         linkedLabel.setStyleName("label-style");
         linkedLabel.setSize(image.getFrameWidth(), image.getFrameHeight());
         if (label.getFontSize() > 0) {
            linkedLabel.setStyleAttribute("fontSize", label.getFontSize() + "px");
         }
         if (label.getColor() != null) {
            linkedLabel.setStyleAttribute("color", label.getColor());
         }
         if (label.getText() != null) {
            linkedLabel.setText(label.getText());
         }
         add(linkedLabel);
         linkedLabel.hide();
      }
   }

}
