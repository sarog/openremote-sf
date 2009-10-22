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
package org.openremote.modeler.domain.control;

import org.openremote.modeler.domain.UICommand;

@SuppressWarnings("serial")
public class UIButton extends UIControl {

   /** The label. */
   private String name;

   private boolean repeate;
   
   private UImage image;
   
   private UImage pressImage;
   
   private Navigate navigate;

   /** The ui command. */
   private UICommand uiCommand;

   /**
    * Instantiates a new uI button.
    */
   public UIButton() {
      super();
   }
   
   public UIButton(String name) {
      this();
      setName(name);
   }
   
   /**
    * Instantiates a new uI button.
    * 
    * @param id the id
    */
   public UIButton(long id) {
      super(id);
   }

   /**
    * Gets the ui command.
    * 
    * @return the ui command
    */
   public UICommand getUiCommand() {
      return uiCommand;
   }

   /**
    * Sets the ui command.
    * 
    * @param uiCommand the new ui command
    */
   public void setUiCommand(UICommand uiCommand) {
      this.uiCommand = uiCommand;
   }

   public String getName() {
      return name;
   }

   public boolean isRepeate() {
      return repeate;
   }

   public UImage getImage() {
      return image;
   }

   public UImage getPressImage() {
      return pressImage;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setRepeate(boolean repeate) {
      this.repeate = repeate;
   }

   public void setImage(UImage image) {
      this.image = image;
   }

   public void setPressImage(UImage pressImage) {
      this.pressImage = pressImage;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }
   
   

}
