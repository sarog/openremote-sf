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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.touchpanel.TouchPanelGridDefinition;

import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * The Class ScreenCanvas.
 */
public class ScreenCanvas extends LayoutContainer {

   /** The absolute position. */
   private Point absolutePosition = null;
   
   /** The move back ground. */
   private LayoutContainer moveBackGround = new LayoutContainer();
   
   /**
    * Instantiates a new screen canvas.
    * 
    * @param screen the screen
    */
   public ScreenCanvas(UIScreen screen) {
      TouchPanelGridDefinition touchPanelGridDefinition = screen.getTouchPanelDefinition().getGrid();
      setSize(touchPanelGridDefinition.getWidth(), touchPanelGridDefinition.getHeight());
      setBorders(true);
      addWidgets(screen);
      moveBackGround.setStyleAttribute("background-color", "yellow");
      moveBackGround.setStyleAttribute("position", "relative");
      moveBackGround.hide();
      add(moveBackGround);
      
   }
   
   /**
    * Adds the widgets.
    * 
    * @param screen the screen
    */
   private void addWidgets(UIScreen screen) {
      final ScreenCanvas canvas = this;
      DropTarget target = new DropTarget(canvas);
      target.addDNDListener(new DNDListener() {
         
         @Override
         public void dragMove(DNDEvent e) {
            Point position = getPosition(e);
            moveBackGround.setPosition(position.x, position.y);
            moveBackGround.show();
            super.dragMove(e);
         }
         @Override
         public void dragLeave(DNDEvent e) {
            moveBackGround.hide();
            super.dragLeave(e);
         }
         @Override
         public void dragDrop(DNDEvent e) {
            if (absolutePosition == null) {
               absolutePosition = new Point(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop());
            }
            LayoutContainer lay = e.getData();
            Point position = getPosition(e);
            lay.setPosition(position.x, position.y);
            canvas.add(lay);
            moveBackGround.hide();
            layout();
            super.dragDrop(e);
         }
      });
      target.setGroup(Constants.CONTROL_DND_GROUP);
      if(screen.isAbsoluteLayout()) {
         LayoutContainer absoluteLayoutContainer = new AbsoluteLayoutContainer();
         absoluteLayoutContainer.setBorders(true);
         absoluteLayoutContainer.setSize(20, 20);
         add(absoluteLayoutContainer);
         createDragSource(canvas, absoluteLayoutContainer);
      } else {
         LayoutContainer gridLayoutContainer = new GridLayoutContainer(screen);
         add(gridLayoutContainer);
//         createDragSource(canvas, gridLayoutContainer);
      }
   }

   /**
    * Creates the drag source.
    * 
    * @param canvas the canvas
    * @param layoutContainer the layout container
    */
   private void createDragSource(final ScreenCanvas canvas, final LayoutContainer layoutContainer) {
      DragSource source = new DragSource(layoutContainer){
         @Override
         protected void onDragStart(DNDEvent event) {
            if (absolutePosition == null) {
               absolutePosition = new Point(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop());
            }
            moveBackGround.setSize(layoutContainer.getWidth(), layoutContainer.getHeight());
            Point mousePoint = event.getXY();
            Point distance = new Point(mousePoint.x - layoutContainer.getAbsoluteLeft(), mousePoint.y - layoutContainer.getAbsoluteTop());
            layoutContainer.setData("distance", distance);
            event.setData(layoutContainer);
            event.getStatus().setStatus(true);
            event.getStatus().update("drop here");
         }
      };
      source.setGroup(Constants.CONTROL_DND_GROUP);
   }
   
   /**
    * Gets the position.
    * 
    * @param event the event
    * 
    * @return the position
    */
   private Point getPosition(DNDEvent event) {
      Point mousePoint = event.getXY();
      Point distance = ((LayoutContainer)event.getData()).getData("distance");
      int left = mousePoint.x - distance.x - absolutePosition.x;
      int top = mousePoint.y - distance.y - absolutePosition.y;
      return new Point(left, top);
   }
}
