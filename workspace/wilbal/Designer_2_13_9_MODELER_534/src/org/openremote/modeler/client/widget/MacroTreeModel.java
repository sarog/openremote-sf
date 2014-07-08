package org.openremote.modeler.client.widget;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceMacroGWTProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.utils.DraggableHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroItemDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.TreeViewModel;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class MacroTreeModel implements TreeViewModel {

   private static final Icons ICON = GWT.create(Icons.class);
   private AsyncDataProvider<MacroDTO> deviceDTOList;
   private AsyncDataProvider<MacroItemDTO> deviceCommandsDTOList;
   private MultiSelectionModel<MacroDTO> selectionModel= new MultiSelectionModel<MacroDTO>();
   protected MacroDTO currentMacroValue;
   private Set<MacroDTO> selectedMacros = new HashSet<MacroDTO>();
   private DraggableHelper<MacroDTO> helperLabel = new DraggableHelper<MacroDTO>();

   private static class MacroCell extends AbstractCell<MacroDTO> {

      /**
       * The html of the image used for contacts.
       */
      private final String imageHtml;

      public MacroCell() {
        this.imageHtml = ICON.macroIcon().getHTML();
      }

      @Override
      public void render(Context context, MacroDTO value, SafeHtmlBuilder sb) {
        if (value != null) {
          sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
          sb.appendEscaped(value.getDisplayName());
        }
      }
    }
   
   private static class MacroItemCell extends AbstractCell<MacroItemDTO> {

      public MacroItemCell() {
      }

      @Override
      public void render(Context context, MacroItemDTO value, SafeHtmlBuilder sb) {
        if (value != null) {
           String imageHtml = null;
           if (value.getType()==MacroItemType.Command) {
             imageHtml = ICON.deviceCmd().getHTML();
          } else if (value.getType()==MacroItemType.Delay) {
             imageHtml = ICON.delayIcon().getHTML();
          } else if (value.getType()==MacroItemType.Macro) {
             imageHtml = ICON.macroIcon().getHTML();
          }
          sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
          sb.appendEscaped(value.getDisplayName());
        }
      }

    }
   
   public void setHelperLabel(DraggableHelper<MacroDTO> helperLabel) {
      this.helperLabel = helperLabel;
    }
    

    public DraggableHelper<MacroDTO> getHelperLabel() {
      return this.helperLabel;
    }
   public Set<MacroDTO> getSelectedMacros() {
      return selectedMacros;
   }


   public MacroTreeModel() { 
      selectionModel.addSelectionChangeHandler(new Handler() {
         
         @Override
         public void onSelectionChange(SelectionChangeEvent event) {
           selectedMacros= selectionModel.getSelectedSet();
           helperLabel.setDraggedData(selectedMacros);
           
         }
       });
   }
   
   @Override
   public <T> NodeInfo<?> getNodeInfo(final T value) {
      if (value==null){
        deviceDTOList = new AsyncDataProvider<MacroDTO>() {
          
          @Override
          protected void onRangeChanged(HasData<MacroDTO> display) {
             DeviceMacroGWTProxy.loadDeviceMacro(new AsyncSuccessCallback<ArrayList<MacroDTO>>() {
                
                @Override
                public void onSuccess(ArrayList<MacroDTO> result) {
                   deviceDTOList.updateRowData(0, result);
                   deviceDTOList.updateRowCount(result.size(), true);
                }
             });
             
          }
       };
       DragAndDropNodeInfo<MacroDTO> node = new DragAndDropNodeInfo<MacroDTO>(deviceDTOList,new MacroCell(),selectionModel,null);
       DraggableOptions options = node.getDraggableOptions();
       options.setHelper(helperLabel.getElement());
       options.setHelper(HelperType.ELEMENT);
       options.setAppendTo("#macroDialogBox");
       return node;
      } else if (value instanceof MacroDTO){
         currentMacroValue = (MacroDTO) value;
         deviceCommandsDTOList = new AsyncDataProvider<MacroItemDTO>() {
           
           @Override
           protected void onRangeChanged(HasData<MacroItemDTO> display) {
              DeviceMacroGWTProxy.loadDeviceMacroDetails((MacroDTO)currentMacroValue, new AsyncSuccessCallback<ArrayList<MacroItemDTO>>() {

                 @Override
                 public void onSuccess(ArrayList<MacroItemDTO> result) {
                    deviceCommandsDTOList.updateRowData(0, result);
                    deviceCommandsDTOList.updateRowCount(result.size(), true);
                 }
              } );
              
           }
        };
        return new DefaultNodeInfo<MacroItemDTO>(deviceCommandsDTOList, new MacroItemCell());

      }
      return null;
   }

   @Override
   public boolean isLeaf(Object value) {
      return (value instanceof MacroItemDTO);
   }


   public void clearSelections() {
      this.selectionModel.clear();
      
   }

}
