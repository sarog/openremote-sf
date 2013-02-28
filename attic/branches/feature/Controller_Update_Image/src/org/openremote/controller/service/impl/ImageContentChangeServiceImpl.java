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
package org.openremote.controller.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openremote.controller.Constants;
import org.openremote.controller.image.ImageContentTimeStampData;
import org.openremote.controller.service.FileService;
import org.openremote.controller.service.ImageContentChangeService;
import org.openremote.controller.service.ProfileService;

/**
 * @author tomsky.wang 2010-11-3
 *
 */
public class ImageContentChangeServiceImpl implements ImageContentChangeService {

   private ProfileService profileService;
   private FileService fileService;
   private ImageContentTimeStampData imageContentTimeStampData;
   
   @Override
   public synchronized void refreshImageContentTimeStamps() {
      Map<String, Set<String>> imageNamesSortedByPanelName = profileService.getImageNamesSortByPanelName();
      if (imageNamesSortedByPanelName == null || imageNamesSortedByPanelName.size() == 0) {
         imageContentTimeStampData.clear();
         return;
      }
      
      Set<String> panelNames = imageNamesSortedByPanelName.keySet();
      
      for (String panelName : panelNames) {
         Set<String> imageNames = imageNamesSortedByPanelName.get(panelName);
         Map<String, Long> panelImagesTimeStamp = imageContentTimeStampData.getPanelImagesTimeStamp(panelName);
         if (panelImagesTimeStamp == null) {
            panelImagesTimeStamp = new HashMap<String, Long>();
         }
         for (String imageName : imageNames) {
            long timeStamp = fileService.getResourceTimeStamp(imageName);
            if (timeStamp != 0) {
               panelImagesTimeStamp.put(imageName, timeStamp);
            }
         }
         imageContentTimeStampData.putPanelImagesTimeStamp(panelName, panelImagesTimeStamp);
      }
      
   }

   @Override
   public String getChangedPanelConsoleImages(String panel, String console) {
      List<String> changedImages = imageContentTimeStampData.getChangedPanelConsoleImages(panel, console);
      if (changedImages == null || changedImages.size() == 0) {
         return "";
      }
      
      StringBuffer sb = new StringBuffer();
      sb.append(Constants.IMAGES_XML_HEADER);
      
      for (String changedImage : changedImages) {
         sb.append("<image>" + changedImage + "</image>\n");
      }
      
      sb.append(Constants.IMAGES_XML_TAIL);
      return sb.toString();
   }

   public void setProfileService(ProfileService profileService) {
      this.profileService = profileService;
   }


   public void setFileService(FileService fileService) {
      this.fileService = fileService;
   }

   public void setImageContentTimeStampData(ImageContentTimeStampData imageContentTimeStampData) {
      this.imageContentTimeStampData = imageContentTimeStampData;
   }

   
}
