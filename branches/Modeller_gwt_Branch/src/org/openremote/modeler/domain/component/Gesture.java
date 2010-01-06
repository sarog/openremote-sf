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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.UICommand;

/**
 * The Gesture defined the gesture on screen.
 */
@SuppressWarnings("serial")
public class Gesture extends UIControl {

   private GestureType type;
   private Navigate navigate = new Navigate();
   private UICommand uiCommand;

   public Gesture() {
   }
   public Gesture(GestureType type) {
      this.type = type;
   }
   public GestureType getType() {
      return type;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public UICommand getUiCommand() {
      return uiCommand;
   }

   public void setType(GestureType type) {
      this.type = type;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }

   public void setUiCommand(UICommand uiCommand) {
      this.uiCommand = uiCommand;
   }


   @Override
   public String getPanelXml() {
      // TODO Auto-generated method stub
      return null;
   }
   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      // TODO Auto-generated method stub
      
   }
   @Override
   public List<UICommand> getCommands() {
      return new ArrayList<UICommand>(){
         {add(uiCommand);} 
      };
   }
   public static enum GestureType {
      swipe_bottom_to_top, swipe_top_to_bottom, swipe_left_to_right, swipe_right_to_left;
      
      @Override
      public String toString() {
         return super.toString().replaceAll("_", "-");
      }
      
   }
}
