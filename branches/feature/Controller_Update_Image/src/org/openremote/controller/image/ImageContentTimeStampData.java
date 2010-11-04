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
package org.openremote.controller.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tomsky.wang 2010-11-3
 * 
 */
public class ImageContentTimeStampData {

   /** The key is panel, and the value is imagesTimeStamp. */
   private Map<String, Map<String, Long>> panelImagesTimeStamp = new HashMap<String, Map<String, Long>>();
   
   /** The key is panel + "/" + console (like: "MyiPhone/481230dfoas2"), and the value is imagesTimeStamp. */
   private Map<String, Map<String, Long>> panelConsoleImagesTimeStamp = new HashMap<String, Map<String, Long>>();
   
   public void putPanelImagesTimeStamp(String panel, Map<String, Long> imagesTimeStamp) {
      this.panelImagesTimeStamp.put(panel, imagesTimeStamp);
   }
   
   public Map<String, Long> getPanelImagesTimeStamp(String panel) {
      return this.panelImagesTimeStamp.get(panel);
   }
   
   public void clearPanelImagesTimeStamp() {
      this.panelImagesTimeStamp.clear();
   }
   
   public void clearPanelConsoleImagesTimeStamp() {
      this.panelConsoleImagesTimeStamp.clear();
   }
   
   public void clear() {
      this.clearPanelImagesTimeStamp();
      this.clearPanelConsoleImagesTimeStamp();
   }
   
   /**
    * Gets the changed panel console images.
    * 
    * @param panel
    *           the panel name
    * @param console
    *           the console id
    * 
    * @return the changed panel console images
    */
   public List<String> getChangedPanelConsoleImages(String panel, String console) {
      List<String> imageNames = new ArrayList<String>();
      Map<String, Long> imagesTimeStamps = panelImagesTimeStamp.get(panel);
      if (imagesTimeStamps == null || imagesTimeStamps.size() == 0) return imageNames;
      
      String panelConsole = panel + "/" + console;
      Map<String, Long> consoleImagesTimeStamps = panelConsoleImagesTimeStamp.get(panelConsole);
      if (consoleImagesTimeStamps == null) {
         Map<String, Long> newPanelImageTimeStamps = new HashMap<String, Long>();
         newPanelImageTimeStamps.putAll(imagesTimeStamps);
         panelConsoleImagesTimeStamp.put(panelConsole, newPanelImageTimeStamps);
         return imageNames;
      }
      
      Set<String> images = imagesTimeStamps.keySet();
      for (String image : images) {
         Long newStamp = imagesTimeStamps.get(image);
         Long oldStamp = consoleImagesTimeStamps.get(image);
         if (newStamp == null || oldStamp == null) {
            continue;
         }
         if (newStamp > oldStamp) {
            imageNames.add(image);
         }
      }
      
      consoleImagesTimeStamps.clear();
      consoleImagesTimeStamps.putAll(imagesTimeStamps);
      panelConsoleImagesTimeStamp.put(panelConsole, consoleImagesTimeStamps);
      return imageNames;
   }
   
}
