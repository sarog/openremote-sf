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

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.control.ScreenButton;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.domain.control.UIButton;
import org.openremote.modeler.touchpanel.TouchPanelGridDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Event;

/**
 * A layout container for create and dnd components.
 */
public class ScreenCanvas extends LayoutContainer {

   /** The absolute position. */
   private Point absolutePosition = null;
   
   /** The move back ground. */
   private LayoutContainer moveBackGround = new LayoutContainer();
   
   private LayoutContainer selectedComponet;
   /**
    * Instantiates a new screen canvas.
    * 
    * @param screen the screen
    */
   public ScreenCanvas(UIScreen screen) {
      TouchPanelGridDefinition touchPanelGridDefinition = screen.getTouchPanelDefinition().getGrid();
      setSize(touchPanelGridDefinition.getWidth(), touchPanelGridDefinition.getHeight());
      setBorders(true);
      setStyleAttribute("position", "relative");
      if (screen.isAbsoluteLayout()) {
         addDropTargetDNDListener(screen);
      } else {
         LayoutContainer gridLayoutContainer = new GridLayoutContainer(screen);
         add(gridLayoutContainer);
      }
      moveBackGround.setStyleAttribute("background-color", "yellow");
      moveBackGround.setStyleAttribute("position", "absolute");
      moveBackGround.hide();
      add(moveBackGround);
      
   }
   
   /**
    * Adds the drop target dnd listener.
    * 
    * @param screen the screen
    */
   private void addDropTargetDNDListener(final UIScreen screen) {
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
         @SuppressWarnings("unchecked")
         @Override
         public void dragDrop(DNDEvent e) {
            if (absolutePosition == null) {
               absolutePosition = new Point(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop());
            }
            AbsoluteLayoutContainer controlContainer = null;
            Object data = e.getData();
            if (data instanceof AbsoluteLayoutContainer) {
               controlContainer = (AbsoluteLayoutContainer) data;
               Point position = getPosition(e);
               controlContainer.setPosition(position.x, position.y);
            } else if (data instanceof List) {
               List<ModelData> models = (List<ModelData>) data;
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  Absolute absolute = new Absolute(IDUtil.nextID());
                  screen.addAbsolute(absolute);
                  if(dataModel.getBean() instanceof UIButton) {
                     absolute.setUiControl(new UIButton("Button"));
                     controlContainer = new AbsoluteLayoutContainer(absolute, new ScreenButton()) {
                        @Override
                        public void onBrowserEvent(Event event) {
                           if (event.getTypeInt() == Event.ONMOUSEDOWN) {
                              this.addStyleName("button-border");
                              if (selectedComponet != null && (LayoutContainer) this != selectedComponet) {
                                 selectedComponet.removeStyleName("button-border");
                              }
                              selectedComponet = (LayoutContainer) this;
                              PropertyPanel.getInstance().update(selectedComponet);
                           }
                           super.onBrowserEvent(event);
                        }
                     
                     };
                     controlContainer.setSize(50, 30);
                     
                     if (selectedComponet != null) {
                        selectedComponet.removeStyleName("button-border");
                     }
                     selectedComponet = controlContainer;
                     selectedComponet.addStyleName("button-border");
                     PropertyPanel.getInstance().update(selectedComponet);
                     canvas.add(controlContainer);
                     createDragSource(canvas, controlContainer);
                  }
               }
            }
            
            moveBackGround.hide();
            layout();
            super.dragDrop(e);
         }
      });
      target.setGroup(Constants.CONTROL_DND_GROUP);
      
   }

   /**
    * Creates the drag source.
    * 
    * @param canvas the canvas
    * @param layoutContainer the layout container
    */
   private void createDragSource(final ScreenCanvas canvas, final AbsoluteLayoutContainer layoutContainer) {
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
