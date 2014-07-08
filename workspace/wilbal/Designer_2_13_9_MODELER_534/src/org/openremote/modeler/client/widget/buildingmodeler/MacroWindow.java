/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.buildingmodeler;

import gwtquery.plugins.draggable.client.DraggableOptions.AxisOption;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellList;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellTree;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceMacroGWTProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.DeviceCommandTreeModel;
import org.openremote.modeler.client.widget.MacroTreeModel;
import org.openremote.modeler.client.widget.utils.DeviceCommandDTOCell;
import org.openremote.modeler.client.widget.utils.DialogBoxCaptionWithCancel;
import org.openremote.modeler.client.widget.utils.DraggableHelper;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemDetailsDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;


/**
 * The window to creates or updates a macro.
 */
public class MacroWindow extends DialogBox implements ClickHandler {

  interface MacroWindowUiBinder extends UiBinder<Widget, MacroWindow> {
  }
  private static MacroWindowUiBinder uiBinder = GWT.create(MacroWindowUiBinder.class);
  private static DialogBoxCaptionWithCancel caption = new DialogBoxCaptionWithCancel();

  @UiField
  DockLayoutPanel mainPanel;

  @UiField
  TextBox macroName;
  
  @UiField
  Button submitButton;
  
  @UiField
  ScrollPanel devicePanel;
  
  @UiField
  ScrollPanel macroPanel;
  
  @UiField
  ScrollPanel currentCommandsPanel;

  @UiField
  FlowPanel currentCommandsButtonsFlowPanel;
  
  Button addDelayBtn;
  Button editDelayBtn;
  Button deleteBtn;
  
  
  private MacroDTO macro;
  private MacroDetailsDTO macroDetails;
  private boolean edit;

  private ArrayList<Integer> selectedCommandCellsVerticalCenter;
  private int dropIndex;
  /** The Constant MACRO_DND_GROUP. */
  private static final String MACRO_DND_GROUP = "macro";

  /** The icons. */
  private Icons icons = GWT.create(Icons.class);

  /** The device command tree. */
    private DragAndDropCellTree deviceCommandTree = null;

  /** The left macro list. */
  private DragAndDropCellTree macroTree = null;

  /** The selection service. */
  private SelectionServiceExt<BeanModel> selectionService;
  private DragAndDropCellList<MacroItemDetailsDTO> selectedCommands;
  private boolean selectionChanged;
  private ProvidesKey<MacroItemDetailsDTO> keyProvider = new ProvidesKey<MacroItemDetailsDTO>() {
     public Object getKey(MacroItemDetailsDTO item) {
     return (item == null) ? null : item.getDto();
     }
     };
  private MultiSelectionModel<MacroItemDetailsDTO> selectedCommandsSelectionModel = new MultiSelectionModel<MacroItemDetailsDTO>();
  private DeviceCommandDTOCell deviceCommandDTOCell;
  private DraggableHelper<MacroItemDetailsDTO> reorderingHelper = new DraggableHelper<MacroItemDetailsDTO>();
  private int selectCommandsScrollPosition;
  MacroItemDetailsDTO editedCommand;

  protected MacroDTO getMacro() {
   return macro;
}

  protected void setMacro(MacroDTO macro) {
   this.macro = macro;
  }

/**
   * Instantiates a macro window to create a new macro.
   */
  public MacroWindow() {
     super(false, true, caption);
     setWidget(uiBinder.createAndBindUi(this));
     caption.addClickHandler(this);
     this.macro = new MacroDTO();
    init();
    setText("New Macro");
    setup();
    macroDetails = new MacroDetailsDTO();
    mainPanel.setSize("650px", "520px");
    this.getElement().setId("macroDialogBox");
    this.center();
  }

  /**
   * Instantiates a macro window to edit a macro.
   * 
   * @param deviceMacroModel the device macro
   */
  public MacroWindow(MacroDTO macro) {
     super(false, true, caption);
    setWidget(uiBinder.createAndBindUi(this));
    this.hide();
    caption.addClickHandler(this);
    this.macro = macro;
    edit = true;
    mainPanel.setSize("650px", "520px");
    this.getElement().setId("macroDialogBox");
    init();
    setText("Edit Macro");
    DeviceMacroGWTProxy.loadMacroDetails(macro, new AsyncSuccessCallback<MacroDetailsDTO>() {
      public void onSuccess(MacroDetailsDTO result) {
        MacroWindow.this.macroDetails = result;
        setup();
        center();
        for (int i = 0; i < selectedCommands.getRowCount(); i++) {
          Element command = selectedCommands.getRowElement(i);
          if (selectedCommandCellsVerticalCenter==null) {
            selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
          }
          selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
      }
      }
    });
  }

  private void init() {
    deviceCommandDTOCell = new DeviceCommandDTOCell();
    selectedCommands = new DragAndDropCellList<MacroItemDetailsDTO>(deviceCommandDTOCell,keyProvider);
    
    selectedCommands.setEmptyListWidget(new Label("No commands found"));
    
    selectedCommands.setWidth("100%");
    selectedCommands.setHeight("100%");
    
    selectedCommands.setPageSize(20);
    selectedCommands.getDraggableOptions().setHelper(reorderingHelper.getElement());
    selectedCommands.getDraggableOptions().setHelper(HelperType.ELEMENT);
    selectedCommands.getDraggableOptions().setAxis(AxisOption.Y_AXIS);
    selectedCommands.addDragStopHandler(new DragStopEventHandler() {
      
      @Override
      public void onDragStop(DragStopEvent event) {
         if (event.getDraggableData() instanceof MacroItemDetailsDTO) {
            final List<MacroItemDetailsDTO> currentList = new ArrayList<MacroItemDetailsDTO>(selectedCommands.getVisibleItems());
         currentList.removeAll(reorderingHelper.getDraggedData());
         if (dropIndex>0) {
            dropIndex = dropIndex-reorderingHelper.getDraggedData().size();
         }
         currentList.addAll(dropIndex, reorderingHelper.getDraggedData());
         selectedCommands.setRowData(currentList);
         selectedCommands.redraw();
         selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
         for (int i = 0; i < selectedCommands.getRowCount(); i++) {
            Element command = selectedCommands.getRowElement(i);
            selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
        }
         }
      }
   });
    
    DroppableWidget<DragAndDropCellList<MacroItemDetailsDTO>> selectedCommandsContainer = new DroppableWidget<DragAndDropCellList<MacroItemDetailsDTO>>(selectedCommands);
    selectedCommandsContainer.setTolerance(DroppableTolerance.TOUCH);
    currentCommandsPanel.add(selectedCommandsContainer);
    currentCommandsPanel.addScrollHandler(new ScrollHandler() {
      
      @Override
      public void onScroll(ScrollEvent event) {
         selectCommandsScrollPosition= currentCommandsPanel.getVerticalScrollPosition();
         GWT.log("scroll position "+selectCommandsScrollPosition);
      }
   });
    selectedCommandsContainer.addDropHandler(new DropEventHandler() {
      
      @Override
      public void onDrop(final DropEvent event) {
      final List<MacroItemDetailsDTO> currentList = new ArrayList<MacroItemDetailsDTO>(selectedCommands.getVisibleItems());
          if (event.getDraggableData() instanceof DeviceCommandDTO) {
            for (DeviceCommandDTO deviceCommandDTO : ((DeviceCommandTreeModel)deviceCommandTree.getTreeViewModel()).getHelperLabel().getDraggedData()) {
              MacroItemDetailsDTO newDetail = new MacroItemDetailsDTO(null, MacroItemType.Command, deviceCommandDTO.getDisplayName(), new DTOReference(deviceCommandDTO.getOid()));
              currentList.add(dropIndex, newDetail);
            }
            selectedCommands.setRowData(currentList);
            selectedCommands.redraw();
            selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
            for (int i = 0; i < selectedCommands.getRowCount(); i++) {
               Element command = selectedCommands.getRowElement(i);
               selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
           }
            ((DeviceCommandTreeModel)deviceCommandTree.getTreeViewModel()).clearSelections();
        } else if (event.getDraggableData() instanceof MacroDTO) {
           for (MacroDTO macroDTO : ((MacroTreeModel)macroTree.getTreeViewModel()).getHelperLabel().getDraggedData()) {
              MacroItemDetailsDTO newDetail = new MacroItemDetailsDTO(null, MacroItemType.Macro, macroDTO.getDisplayName(), new DTOReference(macroDTO.getOid()));
              currentList.add(dropIndex, newDetail);
            }
           selectedCommands.setRowData(currentList);
           selectedCommands.redraw();
           selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
           for (int i = 0; i < selectedCommands.getRowCount(); i++) {
              Element command = selectedCommands.getRowElement(i);
              selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
          }
           ((MacroTreeModel)macroTree.getTreeViewModel()).clearSelections();
        }
      }
    });
    selectedCommandsContainer.addOutDroppableHandler(new OutDroppableEventHandler() {
      
      @Override
      public void onOutDroppable(OutDroppableEvent event) {
         event.getDragHelper().getStyle().setBackgroundColor("red");
         
      }
   });
    
    selectedCommandsContainer.addOverDroppableHandler(new OverDroppableEventHandler() {
      
      @Override
      public void onOverDroppable(OverDroppableEvent event) {
         event.getDragHelper().getStyle().setBackgroundColor("white");
         
      }
   });
    
    selectedCommandsContainer.setAccept(new AcceptFunction() {
      
      @Override
      public boolean acceptDrop(DragAndDropContext context) {
       if (context.getDraggableData() instanceof MacroDTO) {
         MacroDTO droppableMacro = (MacroDTO)context.getDraggableData();
         if (droppableMacro.getOid()==macro.getOid()) {
           return false;
         }
       }
        return true;
      }
    });
    selectedCommands.addBeforeDragHandler( new BeforeDragStartEventHandler() {
      
      @Override
      public void onBeforeDragStart(BeforeDragStartEvent event) {
        //selectedCommandsSelectionModel.setSelected((MacroItemDetailsDTO) event.getDraggableData(), true);
        reorderingHelper.setDraggedData(selectedCommandsSelectionModel.getSelectedSet());
        reorderingHelper.setText(""+selectedCommandsSelectionModel.getSelectedSet().size()+" items selected");
        
      }
    });
    selectedCommands.addDragHandler(new DragEventHandler() {
       
       @Override
       public void onDrag(DragEvent event) {
         computeDropPosition(event);
       }


    });
    
  }

  /**
   * Sets the window style and initializes the form.
   */
  private void setup() {

    selectedCommands.setSelectionModel(selectedCommandsSelectionModel);
    selectedCommandsSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      
      @Override
      public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
         if (selectedCommandsSelectionModel.getSelectedSet() !=null && selectedCommandsSelectionModel.getSelectedSet().size()>0) {
            if (selectedCommandsSelectionModel.getSelectedSet().size()==1 && selectedCommandsSelectionModel.getSelectedSet().iterator().next().getType()==MacroItemType.Delay) {
               editDelayBtn.setEnabled(true);               
            } else {
               editDelayBtn.setEnabled(false);
            }
            deleteBtn.setEnabled(true);
         } else {
            editDelayBtn.setEnabled(false);
            deleteBtn.setEnabled(false);            
         }
         selectionChanged = true;
      }
   });
    if (edit) {
      selectedCommands.setRowData(this.macroDetails.getItems());
    }
    createFormElement();
  }

  /**
   * Creates the form element.
   */
  private void createFormElement() {
    
    
    if (edit) {
      macroName.setValue(macroDetails.getName());
    }
    createSelectCommandContainer();
  }

  /**
   * Creates the select command container.
   */
  private void createSelectCommandContainer() {
    createLeftCommandMacroTab();
    createRightMacroList();

  }


/**
   * Creates the left command macro tab.
   */
  private void createLeftCommandMacroTab() {
    createDeviceCommandTree();
    createMacroTree();
  }

  /**
   * Creates the device command tree.
   * 
   * @return the layout container
   */
    private void createDeviceCommandTree() {


    deviceCommandTree = new DragAndDropCellTree(new DeviceCommandTreeModel(), null);
    deviceCommandTree.setHeight("100px;");
    deviceCommandTree.setWidth("100%");
    deviceCommandTree.addCellBeforeDragHandler(new BeforeDragStartEventHandler() {
      
      @Override
      public void onBeforeDragStart(BeforeDragStartEvent event) {
        ((DeviceCommandTreeModel)deviceCommandTree.getTreeViewModel()).getHelperLabel().setDraggedData(((DeviceCommandTreeModel)deviceCommandTree.getTreeViewModel()).getSelectedDeviceCommands());
        ((DeviceCommandTreeModel)deviceCommandTree.getTreeViewModel()).getHelperLabel().setText(""+((DeviceCommandTreeModel)deviceCommandTree.getTreeViewModel()).getSelectedDeviceCommands().size()+"  items selected");
      }
    });
    deviceCommandTree.addDragHandler(new DragEventHandler() {
       
       @Override
       public void onDrag(DragEvent event) {
          computeDropPosition(event);
       }


    });
    
    devicePanel.add(deviceCommandTree);
    deviceCommandTree.setVisible(true);
  }
 
    protected void computeDropPosition(DragEvent event) {
      int currentIndex = 0;
      for (Integer cellCenter : selectedCommandCellsVerticalCenter) {
        if (currentIndex == 0 && event.getHelper().getAbsoluteTop()<(cellCenter-selectCommandsScrollPosition)) {
           highlightBorders(selectedCommands.getRowElement(currentIndex),true,false);
           dropIndex = currentIndex;
        } else if (currentIndex==selectedCommandCellsVerticalCenter.size()-1 && event.getHelper().getAbsoluteTop()>(cellCenter-selectCommandsScrollPosition)) {
           highlightBorders(selectedCommands.getRowElement(currentIndex),false,true);
           dropIndex = currentIndex+1;
        } else if (event.getHelper().getAbsoluteTop()>(cellCenter-selectCommandsScrollPosition) && event.getHelper().getAbsoluteTop()<=(selectedCommandCellsVerticalCenter.get(currentIndex+1)-selectCommandsScrollPosition)) {
           highlightBorders(selectedCommands.getRowElement(currentIndex),false,true);
           dropIndex = currentIndex+1;
        } else {
           highlightBorders(selectedCommands.getRowElement(currentIndex),false,false);
           selectedCommands.getRowElement(currentIndex).getStyle().setPaddingTop(0, Unit.PX);
           selectedCommands.getRowElement(currentIndex).getStyle().setPaddingBottom(0, Unit.PX);
        }
        currentIndex += 1;
     }
    
  }

    private void createMacroTree() {
       macroTree = new DragAndDropCellTree(new MacroTreeModel(), null);
       macroTree.setHeight("100px;");
       macroTree.setWidth("100%");
       macroTree.addCellBeforeDragHandler(new BeforeDragStartEventHandler() {
         
         @Override
         public void onBeforeDragStart(BeforeDragStartEvent event) {
            ((MacroTreeModel)macroTree.getTreeViewModel()).getHelperLabel().setDraggedData(((MacroTreeModel)macroTree.getTreeViewModel()).getSelectedMacros());
            ((MacroTreeModel)macroTree.getTreeViewModel()).getHelperLabel().setText(""+((MacroTreeModel)macroTree.getTreeViewModel()).getSelectedMacros().size()+"  items selected");
            
         }
      });
       macroTree.addDragHandler(new DragEventHandler() {
          
          @Override
          public void onDrag(DragEvent event) {
            computeDropPosition(event);
          }


       });
       
       macroPanel.add(macroTree);
       macroTree.setVisible(true);
     }
    

  /**
   * Creates the right macro list.
   */
  private void createRightMacroList() {
    createRightMacroItemListToolbar();
  }

  /**
   * Creates the right macro item list toolbar.
   * @return 
   * 
   * @return the tool bar
   */
  private void createRightMacroItemListToolbar() {


    addDelayBtn = new Button(icons.addDelayIcon().getHTML());
    addDelayBtn.setTitle("Add Delay");
    addDelayBtn.getElement().getStyle().setMargin(0, Unit.PX);
    addDelayBtn.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        addDelay();
      }
    });
    
    currentCommandsButtonsFlowPanel.add(addDelayBtn);

    editDelayBtn = new Button(icons.editDelayIcon().getHTML());
    editDelayBtn.setEnabled(false);
    editDelayBtn.setTitle("Edit Delay");
    editDelayBtn.getElement().getStyle().setMargin(0, Unit.PX);
    editDelayBtn.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        editDelay();        
      }
    });
    
    currentCommandsButtonsFlowPanel.add(editDelayBtn);

    deleteBtn = new Button();
    deleteBtn.setEnabled(false);
    deleteBtn.setTitle("Delete Macro Item");
    deleteBtn.getElement().getStyle().setMargin(0, Unit.PX);
    deleteBtn.setHTML(icons.delete().getHTML());
    deleteBtn.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        onDeleteMacroItemBtnClicked();
      }
    });
    currentCommandsButtonsFlowPanel.add(deleteBtn);
  }

  /**
   * On delete macro item btn clicked.
   */
  private void onDeleteMacroItemBtnClicked() {
    for (MacroItemDetailsDTO data : selectedCommandsSelectionModel.getSelectedSet()) {
      final List<MacroItemDetailsDTO> currentList = new ArrayList<MacroItemDetailsDTO>(selectedCommands.getVisibleItems());
      currentList.remove(data);
      selectedCommands.setRowData(currentList);
      selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
      for (int i = 0; i < selectedCommands.getRowCount(); i++) {
         Element command = selectedCommands.getRowElement(i);
         selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
     }
     }
    selectedCommands.redraw();

  }

  /**
   * Adds the delay.
   */
  private void addDelay() {
    final DelayWindow delayWindow = new DelayWindow();
    delayWindow.getElement().getStyle().setZIndex(10000);
    delayWindow.show();
    delayWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
      
      @Override
      public void onClose(CloseEvent<PopupPanel> arg0) {
         if (delayWindow.isClickedSave()) {
            final List<MacroItemDetailsDTO> currentList = new ArrayList<MacroItemDetailsDTO>(selectedCommands.getVisibleItems());
            currentList.add(delayWindow.macroItem);
            selectedCommands.setRowData(currentList);
            selectedCommands.redraw();
            selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
            for (int i = 0; i < selectedCommands.getRowCount(); i++) {
               Element command = selectedCommands.getRowElement(i);
               selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
           }
         }
         
      }
   });
   
  }

  /**
   * Edits the delay.
   */
  private void editDelay() {
    editedCommand = selectedCommandsSelectionModel.getSelectedSet().iterator().next();
    final DelayWindow delayWindow = new DelayWindow(editedCommand);
    delayWindow.getElement().getStyle().setZIndex(10000);
    delayWindow.show();
    delayWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
      
      @Override
      public void onClose(CloseEvent<PopupPanel> arg0) {
         if (delayWindow.isClickedSave()) {
            final List<MacroItemDetailsDTO> currentList = new ArrayList<MacroItemDetailsDTO>(selectedCommands.getVisibleItems());
            editedCommand = delayWindow.macroItem;
            selectedCommands.setRowData(currentList);
            selectedCommands.redraw();
            selectedCommandCellsVerticalCenter = new ArrayList<Integer>();
            for (int i = 0; i < selectedCommands.getRowCount(); i++) {
               Element command = selectedCommands.getRowElement(i);
               selectedCommandCellsVerticalCenter.add(command.getAbsoluteTop()-(command.getOffsetHeight()/2));
           }
         }
         
      }
   });
    
  }
  
  @UiHandler("submitButton")
  void submitButtonClicked(ClickEvent e) {
     if (macroName.getText().length()<=0) {
       macroName.getElement().getStyle().setBorderColor("red");
     } else {
       macroName.getElement().getStyle().setBorderColor("grey");
     AsyncSuccessCallback<MacroDTO> callback = new AsyncSuccessCallback<MacroDTO>() {
        @Override
        public void onSuccess(MacroDTO result) {
          if (result != null) {
            macro.setDisplayName(result.getDisplayName());
            macro.setItems(result.getItems());
            macro.setOid(result.getOid());
            caption = new DialogBoxCaptionWithCancel();
            hide();
          }
        }
      };

      macroDetails.setName(macroName.getValue());
      ArrayList<MacroItemDetailsDTO> items = new ArrayList<MacroItemDetailsDTO>();
      for (MacroItemDetailsDTO bm : selectedCommands.getVisibleItems()) {
        items.add(bm);
      }
      macroDetails.setItems(items);
      if (edit) {
        DeviceMacroBeanModelProxy.updateMacroWithDTO(macroDetails, callback);
      } else {
        DeviceMacroBeanModelProxy.saveNewMacro(macroDetails, callback);
      }
     }
  }
  
  public native void highlightBorders(Element rowElement, boolean top, boolean bottom) /*-{
     if (top) {
       rowElement.style.borderTop = "2px dotted #0000FF";
     } else {
        rowElement.style.borderTop = "none";
     } 
  
     if (bottom) {
       rowElement.style.borderBottom = "2px dotted #0000FF";    
     } else {
       rowElement.style.borderBottom = "none"; 
     }
  
  }-*/;

@Override
public void onClick(ClickEvent event) {
   this.removeFromParent();
   caption = new DialogBoxCaptionWithCancel();
}
}