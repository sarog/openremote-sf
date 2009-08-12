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
package org.openremote.modeler.client.widget.UIDesigner;

import org.openremote.modeler.client.gxtExtends.ScreenDropTarget;
import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class ScreenPanel.
 */
public class ScreenPanel extends LayoutContainer {

   /** The screen. */
   private Screen screen;

   /** The designer images. */
   private UIDesignerImages designerImages = GWT.create(UIDesignerImages.class);

   /**
    * Instantiates a new screen panel.
    * 
    * @param s the s
    */
   public ScreenPanel(Screen s) {
      screen = s;
      setLayout(new FlowLayout());
      createToolBar();

      createScreenBackground();

   }

   /**
    * Creates the tool bar.
    */
   private void createToolBar() {

   }

   /**
    * Creates the screen background.
    */
   private void createScreenBackground() {

      Image image = designerImages.iphone_background().createImage();
      VerticalPanel panel = new VerticalPanel();
      panel.add(image);
      ScreenDropTarget dropTarget = new ScreenDropTarget(panel);
      
      dropTarget.addDNDListener(new DNDListener(){

         @Override
         public void dragDrop(DNDEvent e) {
            Object data = e.getData();

            add(new Button("haha"));
            layout();
            super.dragDrop(e);
         }
         
      });
      add(panel);
   }

}
