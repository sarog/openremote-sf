package org.openremote.modeler.client.widget;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceProxyGWT;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.utils.DraggableHelper;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.TreeViewModel;

public class DeviceCommandTreeModel implements TreeViewModel {

   private static final Icons ICON = GWT.create(Icons.class);
   private AsyncDataProvider<DeviceDTO> deviceDTOList;
   private AsyncDataProvider<DeviceCommandDTO> deviceCommandsDTOList;
   private MultiSelectionModel<DeviceCommandDTO> selectionModel= new MultiSelectionModel<DeviceCommandDTO>();
   protected DeviceDTO currentDeviceValue;
   private Set<DeviceCommandDTO> selectedDeviceCommands = new HashSet<DeviceCommandDTO>();
   private DraggableHelper<DeviceCommandDTO> helperLabel = new DraggableHelper<DeviceCommandDTO>();
   private static class DeviceCell extends AbstractCell<DeviceDTO> {

      /**
       * The html of the image used for contacts.
       */
      private final String imageHtml;

      public DeviceCell() {
        this.imageHtml = ICON.device().getHTML();
      }

      @Override
      public void render(Context context, DeviceDTO value, SafeHtmlBuilder sb) {
        if (value != null) {
          sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
          sb.appendEscaped(value.getDisplayName());
        }
      }
    }
   
   private static class CommandCell extends AbstractCell<DeviceCommandDTO> {

      /**
       * The html of the image used for contacts.
       */
      private final String imageHtml;

      public CommandCell() {
        this.imageHtml = ICON.deviceCmd().getHTML();
      }

      @Override
      public void render(Context context, DeviceCommandDTO value, SafeHtmlBuilder sb) {
        if (value != null) {
          sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
          sb.appendEscaped(value.getDisplayName());
        }
      }

    }
   public Set<DeviceCommandDTO> getSelectedDeviceCommands() {
    return selectedDeviceCommands;
  }

  public void setSelectedDeviceCommands(Set<DeviceCommandDTO> selectedDeviceCommands) {
    this.selectedDeviceCommands = selectedDeviceCommands;
  }


  public void setHelperLabel(DraggableHelper<DeviceCommandDTO> helperLabel) {
    this.helperLabel = helperLabel;
  }
  

  public DraggableHelper<DeviceCommandDTO> getHelperLabel() {
    return this.helperLabel;
  }

  public DeviceCommandTreeModel() {  
     selectionModel.addSelectionChangeHandler(new Handler() {
      
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        selectedDeviceCommands= selectionModel.getSelectedSet();
        helperLabel.setDraggedData(selectedDeviceCommands);
        
      }
    });
   }
   
   @Override
   public <T> NodeInfo<?> getNodeInfo(final T value) {
      if (value==null){
        deviceDTOList = new AsyncDataProvider<DeviceDTO>() {
          
          @Override
          protected void onRangeChanged(HasData<DeviceDTO> display) {
             DeviceProxyGWT.loadDevice(new AsyncSuccessCallback<ArrayList<DeviceDTO>>() {
                
                @Override
                public void onSuccess(ArrayList<DeviceDTO> result) {
                   deviceDTOList.updateRowData(0, result);
                   deviceDTOList.updateRowCount(result.size(), true);
                }
             });
             
          }
       };
        DefaultNodeInfo<DeviceDTO> topNode = new DefaultNodeInfo<DeviceDTO>(deviceDTOList, new DeviceCell());
         return topNode;
      } else if (value instanceof DeviceDTO){
         currentDeviceValue = (DeviceDTO) value;
         deviceCommandsDTOList = new AsyncDataProvider<DeviceCommandDTO>() {
           
           @Override
           protected void onRangeChanged(HasData<DeviceCommandDTO> display) {
              DeviceProxyGWT.loadDevice((DeviceDTO)currentDeviceValue, new AsyncSuccessCallback<ArrayList<DeviceCommandDTO>>() {

                 @Override
                 public void onSuccess(ArrayList<DeviceCommandDTO> result) {
                    deviceCommandsDTOList.updateRowData(0, result);
                    deviceCommandsDTOList.updateRowCount(result.size(), true);
                 }
              } );
              
           }
        };
         DragAndDropNodeInfo<DeviceCommandDTO> node = new DragAndDropNodeInfo<DeviceCommandDTO>(deviceCommandsDTOList,new CommandCell(),selectionModel,null);
         DraggableOptions options = node.getDraggableOptions();
         options.setSnap(true);
         options.setHelper(helperLabel.getElement());
         options.setHelper(HelperType.ELEMENT);
         options.setAppendTo("#macroDialogBox");
         return node;
      }
      return null;
   }

   @Override
   public boolean isLeaf(Object value) {
      return (value instanceof DeviceCommandDTO);
   }

}
