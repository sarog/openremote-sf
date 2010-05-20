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
package org.openremote.android.console.view;

import org.openremote.android.console.bindings.Component;
import org.openremote.android.console.bindings.Image;
import org.openremote.android.console.bindings.Label;

import android.content.Context;
import android.widget.FrameLayout;

public class ComponentView extends FrameLayout {

   private Component component;
   
   protected ComponentView(Context context) {
      super(context);
   }
   
   public static ComponentView buildWithComponent(Context context, Component component) {
      ComponentView componentView = null;
      if (component instanceof Label) {
         componentView = new LabelView(context, (Label)component);
      } else if (component instanceof Image) {
         componentView = new ORImageView(context, (Image)component);
      } else {
         componentView = ControlView.buildWithControl(context, component);
      }
      return componentView;
   }

   public Component getComponent() {
      return component;
   }

   public void setComponent(Component component) {
      this.component = component;
   }

}
