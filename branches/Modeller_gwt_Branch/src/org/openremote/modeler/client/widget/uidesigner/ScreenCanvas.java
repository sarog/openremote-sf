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
import org.openremote.modeler.client.widget.control.ScreenControl;
import org.openremote.modeler.client.widget.control.ScreenSwitch;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.domain.control.UIButton;
import org.openremote.modeler.domain.control.UIControl;
import org.openremote.modeler.domain.control.UISwitch;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.fx.Resizable;
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
   
   private LayoutContainer selectedComponent;
   /**
    * Instantiates a new screen canvas.
    * 
    * @param screen the screen
    */
   public ScreenCanvas(UIScreen screen) {
      TouchPanelCanvasDefinition canvas = screen.getTouchPanelDefinition().getCanvas();
      setSize(canvas.getWidth(), canvas.getHeight());
      setBorders(true);
      setStyleAttribute("position", "relative");
      if (screen.isAbsoluteLayout()) {
         if (screen.getAbsolutes().size() > 0) {
            List<Absolute> absolutes = screen.getAbsolutes();
            for (Absolute absolute : absolutes) {
               AbsoluteLayoutContainer controlContainer = createAbsoluteLayoutContainer(absolute, ScreenControl.build(absolute.getUiControl()));
               controlContainer.setSize(absolute.getWidth(), absolute.getHeight());
               controlContainer.setPosition(absolute.getLeft(), absolute.getTop());
               this.add(controlContainer);
               new Resizable(controlContainer, Constants.RESIZABLE_HANDLES);
               createDragSource(this, controlContainer);
            }
            layout();
         }
         addDropTargetDNDListener(screen);
      } else {
         LayoutContainer gridLayoutContainer = new GridLayoutContainer(screen);
         add(gridLayoutContainer);
      }
      moveBackGround.setStyleAttribute("background-color", "yellow");
      moveBackGround.setStyleAttribute("position", "absolute");
      moveBackGround.hide();
      add(moveBackGround);
      setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
      setStyleAttribute("backgroundRepeat", "no-repeat");
      setStyleAttribute("overflow", "hidden");
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
            if(e.getData() instanceof AbsoluteLayoutContainer) {
               Point position = getPosition(e);
               moveBackGround.setPosition(position.x, position.y);
               moveBackGround.show();
            }
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
            Object data = e.getData();
            if (data instanceof AbsoluteLayoutContainer) {
               AbsoluteLayoutContainer controlContainer = (AbsoluteLayoutContainer) data;
               Point position = getPosition(e);
               controlContainer.setPosition(position.x, position.y);
            } else if (data instanceof List) {    // dnd from widgets tree.
               List<ModelData> models = (List<ModelData>) data;
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  AbsoluteLayoutContainer controlContainer = createNewAbsoluteLayoutContainer(screen, (UIControl)dataModel.getBean());
                  if (selectedComponent != null) {
                     selectedComponent.removeStyleName("button-border");
                  }
                  selectedComponent = controlContainer;
                  selectedComponent.addStyleName("button-border");
                  PropertyPanel.getInstance().update(selectedComponent);
                  canvas.add(controlContainer);
                  controlContainer.setPosition(e.getClientX() - absolutePosition.x, e.getClientY() - absolutePosition.y);
                  new Resizable(controlContainer, Constants.RESIZABLE_HANDLES);
                  createDragSource(canvas, controlContainer);
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

   private AbsoluteLayoutContainer createAbsoluteLayoutContainer(Absolute absolute, ScreenControl screenControl) {
      AbsoluteLayoutContainer controlContainer = new AbsoluteLayoutContainer(absolute, screenControl) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               this.addStyleName("button-border");
               if (selectedComponent != null && (LayoutContainer) this != selectedComponent) {
                  selectedComponent.removeStyleName("button-border");
               }
               selectedComponent = (LayoutContainer) this;
               PropertyPanel.getInstance().update(selectedComponent);
            }
            super.onBrowserEvent(event);
         }
      };
      return controlContainer;
   }
   
   /**
    * Creates the new absolute layout container after drag from tree.
    * 
    */
   private AbsoluteLayoutContainer createNewAbsoluteLayoutContainer(UIScreen screen, UIControl uiControl){
      AbsoluteLayoutContainer controlContainer = null;
      Absolute absolute = new Absolute(IDUtil.nextID());
      if(uiControl instanceof UIButton) {
         UIButton uiButton = new UIButton(IDUtil.nextID());
         absolute.setUiControl(uiButton);
         controlContainer = createAbsoluteLayoutContainer(absolute, new ScreenButton(uiButton));
         controlContainer.setSize(50, 50);   // set the button's default size after drag from widget tree.
         
      } else if(uiControl instanceof UISwitch) {
         UISwitch uiSwitch = new UISwitch(IDUtil.nextID());
         absolute.setUiControl(uiSwitch);
         controlContainer = createAbsoluteLayoutContainer(absolute, new ScreenSwitch(uiSwitch));
         controlContainer.setSize(50, 50);   // set the switch's default size after drag from widget tree.
      }
      screen.addAbsolute(absolute);
      return controlContainer;
   }
}
