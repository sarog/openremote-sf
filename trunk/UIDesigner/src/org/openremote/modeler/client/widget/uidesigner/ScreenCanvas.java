/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.WidgetDeleteEvent;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.client.widget.component.ScreenSwitch;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.ScreenPropertyForm;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Background;
import org.openremote.modeler.domain.GridCellBounds;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Background.RelativeType;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
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
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Event;

/**
 * A layout container for create and dnd components.
 */
public class ScreenCanvas extends ComponentContainer {

   /** The absolute position. */
   private Point absolutePosition = null;

   /** The move back ground. */
   private LayoutContainer moveBackGround = new LayoutContainer();

   private Screen screen = null;

   private boolean hasTabbar;

   private LayoutContainer tabbarContainer;

   /**
    * Instantiates a new screen canvas.
    * 
    * @param screen
    *           the screen
    */
   public ScreenCanvas(Screen screen) {
      this.screen = screen;
      TouchPanelCanvasDefinition canvas = screen.getTouchPanelDefinition().getCanvas();
      setSize(canvas.getWidth(), canvas.getHeight());
      setBorders(true);
      setStyleAttribute("position", "relative");
      if (screen.getAbsolutes().size() > 0) {
         List<Absolute> absolutes = screen.getAbsolutes();
         for (Absolute absolute : absolutes) {
            AbsoluteLayoutContainer componentContainer = createAbsoluteLayoutContainer(screen, absolute,
                  ScreenComponent.build(this, absolute.getUiComponent()));
            componentContainer.setSize(absolute.getWidth(), absolute.getHeight());
            componentContainer.setPosition(absolute.getLeft(), absolute.getTop());
            this.add(componentContainer);
            Resizable resizable = new Resizable(componentContainer, Constants.RESIZABLE_HANDLES);
            resizable.setMinHeight(10);
            resizable.setMinWidth(10);
            createDragSource(this, componentContainer);
         }
      }
      if (screen.getGrids().size() > 0) {
         List<UIGrid> grids = screen.getGrids();
         for (UIGrid grid : grids) {
            GridLayoutContainerHandle gridContainer = createGridLayoutContainer(grid);
            this.add(gridContainer);
            gridContainer.setPosition(grid.getLeft() - GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH, grid.getTop()
                  - GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT);
            createGridDragSource(gridContainer, this);
         }
      }
      layout();

      addDropTargetDNDListener(screen);
      moveBackGround.addStyleName("move-background");
      moveBackGround.hide();
      add(moveBackGround);
      setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
      setStyleAttribute("backgroundRepeat", "no-repeat");
      setStyleAttribute("overflow", "hidden");
      updateGround();
      new DragSource(this);
      if (screen.isHasTabbar()) {
         addTabbar();
      }
   }

   public void updateGround() {
      Background bgd = screen.getBackground();
      if (bgd.isFillScreen()) {
         setStyleAttribute("backgroundPosition", Background.getRelativeMap().get(RelativeType.TOP_LEFT));
         return;
      } else if (bgd.isAbsolute()) {
         // setStyleAttribute("position", "absolute");
         setStyleAttribute("backgroundPosition", bgd.getLeft() + " " + bgd.getTop());
      } else {
         setStyleAttribute("backgroundPosition", Background.getRelativeMap().get(bgd.getRelatedType()));
      }
      layout();
   }

   public void hideBackground() {
      moveBackGround.hide();
   }

   /**
    * Adds the drop target dnd listener.
    * 
    * @param screen
    *           the screen
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
               Point position = getGridCellContainerPosition(e);
               moveBackGround.setPosition(position.x + container.getWidth(), position.y + container.getHeight());
               moveBackGround.show();
            } else if (data instanceof GridLayoutContainerHandle) {
               Point position = getGridPosition(e);
               moveBackGround.setPosition(position.x + GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH, position.y
                     + GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT);
               moveBackGround.show();
            }
         }

         @Override
         public void dragLeave(DNDEvent e) {
            moveBackGround.hide();
            Object data = e.getData();
            if (data instanceof GridCellContainer) {
               final GridCellContainer cellContainer = (GridCellContainer) data;
               MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                  public void handleEvent(MessageBoxEvent be) {
                     if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                        cellContainer.getGridContainer().getGrid().removeCell(cellContainer.getCell());
                        WidgetSelectionUtil.setSelectWidget(null);
                     } else if (be.getButtonClicked().getItemId().equals(Dialog.NO)){
                        cellContainer.getGridContainer().addGridCellContainer(cellContainer);
                     }
                  }
               });
            } else if (data instanceof LayoutContainer) {
               if (data instanceof GridLayoutContainerHandle) {
                  final GridLayoutContainerHandle gridContainer = (GridLayoutContainerHandle) e.getData();
                  gridContainer.show();
                  MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                     public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                           ScreenCanvas.this.getScreen().removeGrid(gridContainer.getGridlayoutContainer().getGrid());
                           gridContainer.removeFromParent();
                           WidgetSelectionUtil.setSelectWidget(null);
                        }
                     }
                  });
               } else {
                  final AbsoluteLayoutContainer controlContainer = (AbsoluteLayoutContainer) data;
                  MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                     public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                           ScreenCanvas.this.getScreen().removeAbsolute(controlContainer.getAbsolute());
                           controlContainer.removeFromParent();
                           WidgetSelectionUtil.setSelectWidget(null);
                        }
                     }
                  });
               }
            }
         }

         @SuppressWarnings("unchecked")
         @Override
         public void dragDrop(DNDEvent e) {
            if (absolutePosition == null) {
               absolutePosition = new Point(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop());
            }
            Object data = e.getData();
            Point mousePoint = e.getXY();
            boolean canDrop = false;
            int x = getWidth() - (mousePoint.x - absolutePosition.x);
            int y = getHeight() - (mousePoint.y - absolutePosition.y);
            if (x > 0 && x < getWidth() && y > 0 && y < getHeight()) {
               canDrop = true;
            }
            if (data instanceof GridCellContainer) {
               final GridCellContainer controlContainer = (GridCellContainer) data;
               if (!canDrop) {
                  MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                     public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                           controlContainer.getGridContainer().getGrid().removeCell(controlContainer.getCell());
                           WidgetSelectionUtil.setSelectWidget(null);
                        } else if (be.getButtonClicked().getItemId().equals(Dialog.NO)){
                           controlContainer.getGridContainer().addGridCellContainer(controlContainer);
                        }
                     }
                  });
               } else {
                  Point position = getPosition(e);
                  controlContainer.setPosition(position.x, position.y);
                  LayoutContainer componentContainer = new LayoutContainer();
                  GridCellBounds recorder = controlContainer.getData(GridLayoutContainer.BOUNDS_RECORD_NAME);
                  componentContainer = dragComponentFromGrid(screen, controlContainer, recorder);
                  createDragSource(canvas, componentContainer);
                  canvas.add(componentContainer);
                  componentContainer.setPosition(e.getClientX() - absolutePosition.x, e.getClientY() - absolutePosition.y);
                  Resizable resizable = new Resizable(componentContainer, Constants.RESIZABLE_HANDLES);
                  resizable.setMinHeight(10);
                  resizable.setMinWidth(10);
               }
            } else if (data instanceof LayoutContainer) {
               if (data instanceof GridLayoutContainerHandle) {
                  final GridLayoutContainerHandle gridContainer = (GridLayoutContainerHandle) data;
                  gridContainer.show();
                  if (!canDrop) {
                     MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                           if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                              ScreenCanvas.this.getScreen()
                                    .removeGrid(gridContainer.getGridlayoutContainer().getGrid());
                              gridContainer.removeFromParent();
                              WidgetSelectionUtil.setSelectWidget(null);
                           }
                        }
                     });
                  } else {
                     Point position = getGridPosition(e);
                     int gridX = position.x + GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH;
                     int gridY = position.y + GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT;
                     if (gridX < 0) {
                        gridX = 0;
                     } else if (gridX > getWidth() - moveBackGround.getWidth()) {
                        gridX = getWidth() - moveBackGround.getWidth();
                     }
                     if (gridY < 0) {
                        gridY = 0;
                     } else if (gridY > getHeight() - moveBackGround.getHeight()) {
                        gridY = getHeight() - moveBackGround.getHeight();
                     }
                     gridContainer.setPosition(gridX - GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH, gridY
                           - GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT);
                     WidgetSelectionUtil.setSelectWidget(gridContainer);
                  }
               } else {
                  Point position = getPosition(e);
                  final AbsoluteLayoutContainer controlContainer = (AbsoluteLayoutContainer) data;
                  if (canDrop) {
                     controlContainer.setPosition(position.x, position.y);
                  } else {
                     MessageBox.confirm("Delete", "Are you sure you want to delete?", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                           if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                              ScreenCanvas.this.getScreen().removeAbsolute(controlContainer.getAbsolute());
                              controlContainer.removeFromParent();
                              WidgetSelectionUtil.setSelectWidget(null);
                           }
                        }
                     });
                  }
               }
            } else if (data instanceof List) { // dnd from widgets tree.
               List<ModelData> models = (List<ModelData>) data;
               if (models.size() > 0) {
                  BeanModel dataModel = models.get(0).get("model");
                  ComponentContainer componentContainer = new ComponentContainer(ScreenCanvas.this);
                  if (dataModel.getBean() instanceof UIGrid) {
                     UIGrid grid = new UIGrid(e.getXY().x - getAbsoluteLeft()
                           + GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH, e.getXY().y - getAbsoluteTop()
                           + GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT, UIGrid.DEFALUT_WIDTH,
                           UIGrid.DEFAULT_HEIGHT, UIGrid.DEFALUT_ROW_COUNT, UIGrid.DEFAULT_COL_COUNT);
                     screen.addGrid(grid);
                     componentContainer = createGridLayoutContainer(grid);
                     createGridDragSource(componentContainer, canvas);
                  } else {
                     componentContainer = createNewAbsoluteLayoutContainer(screen, (UIComponent) dataModel.getBean());
                     createDragSource(canvas, componentContainer);
                     Resizable resizable = new Resizable(componentContainer, Constants.RESIZABLE_HANDLES);
                     resizable.setMinHeight(10);
                     resizable.setMinWidth(10);
                  }
                  WidgetSelectionUtil.setSelectWidget(componentContainer);
                  canvas.add(componentContainer);
                  componentContainer.setPosition(e.getClientX() - absolutePosition.x, e.getClientY()
                        - absolutePosition.y);
               }
            }

            moveBackGround.hide();
            if (screen.isHasTabbar()) {
               tabbarContainer.el().updateZIndex(1);
            }
            layout();
            super.dragDrop(e);
         }
      });
      target.setGroup(Constants.CONTROL_DND_GROUP);

   }

   /**
    * Creates the drag source.
    * 
    * @param canvas
    *           the canvas
    * @param layoutContainer
    *           the layout container
    */
   private void createDragSource(final ScreenCanvas canvas, final LayoutContainer layoutContainer) {
      DragSource source = new DragSource(layoutContainer) {
         @Override
         protected void onDragStart(DNDEvent event) {
            if (absolutePosition == null) {
               absolutePosition = new Point(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop());
            }
            absolutePosition.x = canvas.getAbsoluteLeft();
            absolutePosition.y = canvas.getAbsoluteTop();
            moveBackGround.setSize(layoutContainer.getWidth(), layoutContainer.getHeight());
            Point mousePoint = event.getXY();
            Point distance = new Point(mousePoint.x - layoutContainer.getAbsoluteLeft(), mousePoint.y
                  - layoutContainer.getAbsoluteTop());
            layoutContainer.setData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME, distance);
            event.setData(layoutContainer);
            event.getStatus().setStatus(true);
            event.getStatus().update("drop here");
            event.cancelBubble();
            ScreenComponent screenComponent = ((AbsoluteLayoutContainer)layoutContainer).getScreenComponent();
            if (screenComponent instanceof ScreenButton) {
               ((ScreenButton)screenComponent).setDefaultImage();
            }
            
         }
      };
      source.setGroup(Constants.CONTROL_DND_GROUP);
      source.setFiresEvents(false);
   }

   /**
    * Gets the position.
    * 
    * @param event
    *           the event
    * 
    * @return the position
    */
   private Point getPosition(DNDEvent event) {
      Point mousePoint = event.getXY();
      Point distance = ((LayoutContainer) event.getData()).getData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME);
      int left = mousePoint.x - distance.x - absolutePosition.x;
      int top = mousePoint.y - distance.y - absolutePosition.y;
      return new Point(left, top);
   }

   private Point getGridPosition(DNDEvent event) {
      Point mousePoint = event.getXY();
      Point distance = ((LayoutContainer) event.getData()).getData(GridLayoutContainerHandle.GRID_DISTANCE_NAME);
      int left = mousePoint.x - distance.x - absolutePosition.x;
      int top = mousePoint.y - distance.y - absolutePosition.y;
      return new Point(left, top);
   }

   private Point getGridCellContainerPosition(DNDEvent event) {
      GridCellContainer container = event.getData();
      Point mousePoint = event.getXY();
      Point distance = ((LayoutContainer) event.getData()).getData(AbsoluteLayoutContainer.ABSOLUTE_DISTANCE_NAME);
      GridCellBounds recorder = container.getData(GridLayoutContainer.BOUNDS_RECORD_NAME);
      int left = mousePoint.x - distance.x - absolutePosition.x + recorder.getWidth();
      int top = mousePoint.y - distance.y - absolutePosition.y + recorder.getHeight();
      return new Point(left, top);
   }

   private AbsoluteLayoutContainer createAbsoluteLayoutContainer(final Screen screen, final Absolute absolute,
         final ScreenComponent screenControl) {
      final AbsoluteLayoutContainer controlContainer = new AbsoluteLayoutContainer(this, absolute, screenControl) {
         @Override
         public void onComponentEvent(ComponentEvent ce) {
            if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
               WidgetSelectionUtil.setSelectWidget(this);
               if (screenControl instanceof ScreenButton) {
                  ((ScreenButton)screenControl).setPressedImage();
               } else if (screenControl instanceof ScreenSwitch) {
                  ((ScreenSwitch)screenControl).onStateChange();
               }
            } else if (ce.getEventTypeInt() == Event.ONMOUSEUP){
               if (screenControl instanceof ScreenButton) {
                  ((ScreenButton)screenControl).setDefaultImage();
               }
            }
            ce.cancelBubble();
            super.onComponentEvent(ce);
         }
      };
      controlContainer.sinkEvents(Event.ONMOUSEUP);
      controlContainer.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            screen.removeAbsolute(absolute);
            controlContainer.removeFromParent();
         }
         
      });
      new KeyNav<ComponentEvent>() {
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
                     WidgetSelectionUtil.setSelectWidget(null);
                  }
               }
            });
            box.show();
         }

         public void onBackspace(ComponentEvent ce) {
            super.onBackspace(ce);
            this.onDelete(ce);
         }

      }.bind(controlContainer);
      absolute.setBelongsTo(controlContainer);
      return controlContainer;
   }

   /**
    * Creates the new absolute layout container after drag from tree.
    * 
    */
   private AbsoluteLayoutContainer createNewAbsoluteLayoutContainer(Screen screen, UIComponent uiComponent) {
      AbsoluteLayoutContainer controlContainer = null;
      Absolute absolute = new Absolute(IDUtil.nextID());
      UIComponent component = UIComponent.createNew(uiComponent);
      absolute.setUiComponent(component);
      controlContainer = createAbsoluteLayoutContainer(screen, absolute, ScreenComponent.build(this, component));
      controlContainer.setSize(component.getPreferredWidth(), component.getPreferredHeight());
      screen.addAbsolute(absolute);
      return controlContainer;
   }

   private AbsoluteLayoutContainer dragComponentFromGrid(Screen screen, GridCellContainer cellContainer,
         GridCellBounds recorder) {
      cellContainer.getGridContainer().getGrid().removeCell(cellContainer.getCell()); // remove the old cell from grid.

      UIComponent uiComponent = cellContainer.getCell().getUiComponent();
      AbsoluteLayoutContainer controlContainer = null;
      Absolute absolute = new Absolute(IDUtil.nextID());
      UIComponent component = UIComponent.copy(uiComponent);
      absolute.setUiComponent(component);
      controlContainer = createAbsoluteLayoutContainer(screen, absolute, ScreenComponent.build(this, component));
      controlContainer.setSize(recorder.getWidth(), recorder.getHeight());
      screen.addAbsolute(absolute);
      return controlContainer;
   }

   private GridLayoutContainerHandle createGridLayoutContainer(final UIGrid grid) {
      GridLayoutContainer gridlayoutContainer = new GridLayoutContainer(this, grid);
      new DropTarget(gridlayoutContainer);

      final GridLayoutContainerHandle gridContainer = new GridLayoutContainerHandle(ScreenCanvas.this,
            gridlayoutContainer) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONMOUSEDOWN) {
               WidgetSelectionUtil.setSelectWidget(this);
            }
            event.stopPropagation();
            super.onBrowserEvent(event);
         }

         @Override
         protected void afterRender() {
            super.afterRender();
            this.setZIndex(100); // set z-index to make drop widget on grid cell is possible(after reopen the screen).
         }
         
      };
      gridContainer.addListener(WidgetDeleteEvent.WIDGETDELETE, new Listener<WidgetDeleteEvent>() {
         public void handleEvent(WidgetDeleteEvent be) {
            screen.removeGrid(grid);
            gridContainer.removeFromParent();
         }
         
      });
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
                     WidgetSelectionUtil.setSelectWidget(null);
                  }
               }
            });
            box.show();
         }

         public void onBackspace(ComponentEvent ce) {
            super.onBackspace(ce);
            this.onDelete(ce);
         }
      }.bind(gridContainer);

      return gridContainer;
   }

   public Screen getScreen() {
      return screen;
   }

   public LayoutContainer getMoveBackGround() {
      return moveBackGround;
   }

   public void setMoveBackGround(LayoutContainer moveBackGround) {
      this.moveBackGround = moveBackGround;
   }

   /**
    * @param componentContainer
    */
   private void createGridDragSource(final LayoutContainer componentContainer, final ScreenCanvas canvas) {
      DragSource gridSource = new DragSource(componentContainer) {
         @Override
         protected void onDragStart(DNDEvent event) {
            UIGrid grid = ((GridLayoutContainer) ((GridLayoutContainerHandle) componentContainer)
                  .getGridlayoutContainer()).getGrid();
            moveBackGround.setSize(grid.getWidth(), grid.getHeight());
            if (absolutePosition == null) {
               absolutePosition = new Point(canvas.getAbsoluteLeft(), canvas.getAbsoluteTop());
            }
            absolutePosition.x = canvas.getAbsoluteLeft();
            absolutePosition.y = canvas.getAbsoluteTop();
            Point mousePoint = event.getXY();
            int x = mousePoint.x - componentContainer.getAbsoluteLeft();
            int y = mousePoint.y - componentContainer.getAbsoluteTop();
            if (x < GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH) {
               x = GridLayoutContainerHandle.DEFALUT_HANDLE_WIDTH;
            }
            if (y < GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT) {
               y = GridLayoutContainerHandle.DEFAULT_HANDLE_HEIGHT;
            }
            Point distance = new Point(x, y);
            componentContainer.setData(GridLayoutContainerHandle.GRID_DISTANCE_NAME, distance);
            event.setData(componentContainer);
            componentContainer.hide();
            event.getStatus().setStatus(true);
            event.getStatus().update("drop here");
            event.cancelBubble();
         }
      };
      gridSource.setGroup(Constants.CONTROL_DND_GROUP);
      gridSource.setFiresEvents(false);
   }

   @Override
   public void onBrowserEvent(Event event) {
      if (event.getTypeInt() == Event.ONMOUSEDOWN) {
         WidgetSelectionUtil.setSelectWidget(this);
      }
      event.stopPropagation();
      super.onBrowserEvent(event);
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new ScreenPropertyForm(this);
   }

   public void addTabbar() {
      if (!hasTabbar) {
         hasTabbar = true;
         tabbarContainer = new LayoutContainer() {
            protected void afterRender() {
               super.el().updateZIndex(1);
               super.afterRender();
            }

         };
         tabbarContainer.setHeight(44);
         tabbarContainer.setWidth(screen.getTouchPanelDefinition().getCanvas().getWidth());
         tabbarContainer.addStyleName("tabbar-background");
         tabbarContainer.setPosition(0, screen.getTouchPanelDefinition().getCanvas().getHeight() - 44);
         tabbarContainer.setStyleAttribute("position", "absolute");
         this.add(tabbarContainer);
         layout();
      }
   }
   
   public void removeTabbar() {
      if (tabbarContainer != null) {
         tabbarContainer.removeFromParent();
         hasTabbar = false;
      }
   }

   public void setSizeToDefault(UIComponent component) {
      for (Absolute absolute : screen.getAbsolutes()) {
         if (absolute.getUiComponent().equals(component)) {
            absolute.getBelongsTo().setSize(component.getPreferredWidth(), component.getPreferredHeight());
         }
      }
      this.layout();
   }

}
