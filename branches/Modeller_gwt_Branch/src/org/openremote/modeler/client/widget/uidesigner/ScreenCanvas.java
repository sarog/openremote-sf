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
import org.openremote.modeler.client.utils.SelectedWidgetContainer;
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.client.widget.component.ScreenSwitch;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.BoundsRecorder;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Event;

/**
 * A layout container for create and dnd components.
 */
public class ScreenCanvas extends LayoutContainer {

   /** The absolute position. */
   private Point absolutePosition = null;
   
   /** The move back ground. */
   private LayoutContainer moveBackGround = new LayoutContainer();
   
   private Screen screen = null;
   
   /**
    * Instantiates a new screen canvas.
    * 
    * @param screen the screen
    */
   public ScreenCanvas(Screen screen) {
      this.screen = screen;
      TouchPanelCanvasDefinition canvas = screen.getTouchPanelDefinition().getCanvas();
      setSize(canvas.getWidth(), canvas.getHeight());
      setBorders(true);
      setStyleAttribute("position", "relative");
      if (screen.isAbsoluteLayout()) {
         if (screen.getAbsolutes().size() > 0) {
            List<Absolute> absolutes = screen.getAbsolutes();
            for (Absolute absolute : absolutes) {
               AbsoluteLayoutContainer controlContainer = createAbsoluteLayoutContainer(screen, absolute,
                     ScreenComponent.build(this, absolute.getUIComponent()));
               controlContainer.setSize(absolute.getWidth(), absolute.getHeight());
               controlContainer.setPosition(absolute.getLeft(), absolute.getTop());
               this.add(controlContainer);
               new Resizable(controlContainer, Constants.RESIZABLE_HANDLES);
               createDragSource(this, controlContainer);
            }
         }
         if (screen.getGrids().size() > 0) {
            List<UIGrid> grids = screen.getGrids();
            for (UIGrid grid : grids) {
               GridLayoutContainer gridContainer = createGridLayoutContainer(grid);
               this.add(gridContainer);
            }
         }
         layout();

         addDropTargetDNDListener(screen);
      } else {
         /*
          * LayoutContainer gridLayoutContainer = createGridLayoutContainer(); add(gridLayoutContainer);
          */
      }
      moveBackGround.setStyleAttribute("background-color", "yellow");
      moveBackGround.setStyleAttribute("position", "absolute");
      moveBackGround.hide();
      add(moveBackGround);
      setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
      setStyleAttribute("backgroundRepeat", "no-repeat");
      setStyleAttribute("overflow", "hidden");
   }
   
   public void hideBackground(){
      moveBackGround.hide();
   }
   /**
    * Adds the drop target dnd listener.
    * 
    * @param screen the screen
    */
   private void addDropTargetDNDListener(final Screen screen) {
      final ScreenCanvas canvas = this;
      DropTarget target = new DropTarget(canvas);
      target.addDNDListener(new DNDListener() {

         @Override
         public void dragMove(DNDEvent e) {
            Object data = e.getData();
            if (e.getData() instanceof AbsoluteLayoutContainer) {
               Point position = getPosition(e);
               moveBackGround.setPosition(position.x, position.y);
               moveBackGround.show();
            } else if (data instanceof GridCellContainer) {
               GridCellContainer container = (GridCellContainer) data;
               Point position = getGridCellContainerPostiono(e);
               moveBackGround.setPosition(position.x + container.getWidth(), position.y + container.getHeight());
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
            if (data instanceof GridCellContainer) {
               Point position = getPosition(e);
               GridCellContainer controlContainer = (GridCellContainer) data;
               controlContainer.setPosition(position.x, position.y);
               LayoutContainer componentContainer = new LayoutContainer();
               componentContainer = createNewAbsoluteLayoutContainer(screen, controlContainer.getCell()
                     .getUiComponent());
               createDragSource(canvas, componentContainer);
               canvas.add(componentContainer);
               componentContainer.setPosition(e.getClientX() - absolutePosition.x, e.getClientY() - absolutePosition.y);
               new Resizable(componentContainer, Constants.RESIZABLE_HANDLES);
            } else if (data instanceof LayoutContainer) {
               Point position = getPosition(e);
               LayoutContainer controlContainer = (LayoutContainer) data;
               controlContainer.setPosition(position.x, position.y);
            } else if (data instanceof List) { // dnd from widgets tree.
               List<ModelData> models = (List<ModelData>) data;
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  LayoutContainer componentContainer = new LayoutContainer();
                  if (dataModel.getBean() instanceof UIGrid) {
                     UIGrid grid = new UIGrid(e.getXY().x - 200, e.getXY().y - 200, 200, 200, 4, 4);
                     screen.addGrid(grid);
                     componentContainer = createGridLayoutContainer(grid);
                  } else {
                     componentContainer = createNewAbsoluteLayoutContainer(screen, (UIComponent) dataModel.getBean());
                     createDragSource(canvas, componentContainer);
                  }
                  SelectedWidgetContainer.setSelectWidget(componentContainer);
                  canvas.add(componentContainer);
                  componentContainer.setPosition(e.getClientX() - absolutePosition.x, e.getClientY()
                        - absolutePosition.y);
                  new Resizable(componentContainer, Constants.RESIZABLE_HANDLES);

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
            layoutContainer.setData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME, distance);
            event.setData(layoutContainer);
            event.getStatus().setStatus(true);
            event.getStatus().update("drop here");
         }
      };
      source.setGroup(Constants.CONTROL_DND_GROUP);
      System.out.println("");
      source.setFiresEvents(false);
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
      Point distance = ((LayoutContainer)event.getData()).getData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME);
      int left = mousePoint.x - distance.x - absolutePosition.x;
      int top = mousePoint.y - distance.y - absolutePosition.y;
      return new Point(left, top);
   }
   
   private Point getGridCellContainerPostiono(DNDEvent event){
      GridCellContainer container = event.getData();
      Point mousePoint = event.getXY();
      Point distance = ((LayoutContainer)event.getData()).getData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME);
      BoundsRecorder recorder =container.getData(GridLayoutContainer.BOUNDS_RECORD_NAME);
      int left = mousePoint.x - distance.x - absolutePosition.x+recorder.getWidth();
      int top = mousePoint.y - distance.y - absolutePosition.y+recorder.getHeight();
      return new Point(left, top);
   }
   private AbsoluteLayoutContainer createAbsoluteLayoutContainer(final Screen screen, Absolute absolute, ScreenComponent screenControl) {
      final AbsoluteLayoutContainer controlContainer = new AbsoluteLayoutContainer(this,absolute, screenControl) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               SelectedWidgetContainer.setSelectWidget((LayoutContainer) this);
            }
            super.onBrowserEvent(event);
         }
         
      };
      new KeyNav<ComponentEvent>(){
         @Override
         public void onDelete(ComponentEvent ce) {
            super.onDelete(ce);
            MessageBox box = new MessageBox();
            box.setButtons(MessageBox.YESNO);
            box.setIcon(MessageBox.QUESTION);
            box.setTitle("Delete");
            box.setMessage("Are you sure you want to delete?");
            box.addCallback(new Listener<MessageBoxEvent>() {
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                       screen.removeAbsolute(controlContainer.getAbsolute());
                       controlContainer.removeFromParent();
                       SelectedWidgetContainer.setSelectWidget(null);
                    }
                }
            });
            box.show();
         }
         
      }.bind(controlContainer);
      return controlContainer;
   }
   
   /**
    * Creates the new absolute layout container after drag from tree.
    * 
    */
   private AbsoluteLayoutContainer createNewAbsoluteLayoutContainer(Screen screen, UIComponent uiComponent) {
      AbsoluteLayoutContainer controlContainer = null;
      Absolute absolute = new Absolute(IDUtil.nextID());
      if (uiComponent instanceof UIButton) {
         UIButton uiButton = new UIButton(IDUtil.nextID());
         absolute.setUIComponent(uiButton);
         controlContainer = createAbsoluteLayoutContainer(screen, absolute, new ScreenButton(this,uiButton));
         controlContainer.setSize(50, 50); // set the button's default size after drag from widget tree.

      } else if (uiComponent instanceof UISwitch) {
         UISwitch uiSwitch = new UISwitch(IDUtil.nextID());
         absolute.setUIComponent(uiSwitch);
         controlContainer = createAbsoluteLayoutContainer(screen, absolute, new ScreenSwitch(this,uiSwitch));
         controlContainer.setSize(50, 50); // set the switch's default size after drag from widget tree.
      }
      screen.addAbsolute(absolute);
      return controlContainer;
   }
   
   private GridLayoutContainer createGridLayoutContainer(final UIGrid grid) {
      final GridLayoutContainer gridContainer = new GridLayoutContainer(this, grid) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               SelectedWidgetContainer.setSelectWidget((GridLayoutContainer) this);
            }
            super.onBrowserEvent(event);
         }
      };
      new DropTarget(gridContainer);
      new KeyNav<ComponentEvent>(gridContainer) {
         @Override
         public void onDelete(ComponentEvent ce) {
            super.onDelete(ce);
            MessageBox box = new MessageBox();
            box.setButtons(MessageBox.YESNO);
            box.setIcon(MessageBox.QUESTION);
            box.setTitle("Delete");
            box.setMessage("Are you sure you want to delete?");
            box.addCallback(new Listener<MessageBoxEvent>() {
               public void handleEvent(MessageBoxEvent be) {
                  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                     ScreenCanvas.this.getScreen().removeGrid(grid);
                     gridContainer.removeFromParent();
                     SelectedWidgetContainer.setSelectWidget(null);
                  }
               }
            });
            box.show();
         }

      }.bind(gridContainer);
      return gridContainer;
   }

   public Screen getScreen() {
      return screen;
   }
   
}
