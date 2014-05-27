package org.openremote.modeler.client.widget;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.DragFunction;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.events.DragContext;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;

import java.util.ArrayList;

import org.openremote.modeler.client.proxy.DeviceProxyGWT;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.utils.DeviceCommandDTOCell;
import org.openremote.modeler.client.widget.utils.DeviceDTOCell;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class DeviceCommandTreeModel implements TreeViewModel {

   private AsyncDataProvider<DeviceDTO> deviceDTOList;
   private AsyncDataProvider<DeviceCommandDTO> deviceCommandsDTOList;
   protected DeviceDTO currentDeviceValue;
   
   static interface Templates extends SafeHtmlTemplates {
      Templates INSTANCE = GWT.create(Templates.class);

      @Template("<div id='dragHelper' class='{0}'></div>")
      SafeHtml outerHelper(String cssClassName);
    }
   

   public DeviceCommandTreeModel() { 
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
       DragAndDropNodeInfo<DeviceDTO> node = new DragAndDropNodeInfo<DeviceDTO>(deviceDTOList, new DeviceDTOCell());
       
       return node;
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
        DragAndDropNodeInfo<DeviceCommandDTO> node = new DragAndDropNodeInfo<DeviceCommandDTO>(deviceCommandsDTOList,new DeviceCommandDTOCell(), new SingleSelectionModel<DeviceCommandDTO>(),null);
        DraggableOptions options = new DraggableOptions();
        options.setHelper(HelperType.CLONE);
        options.setOpacity((float) 0.9);
        options.setCursor(Cursor.MOVE);
        options.setCursorAt(new CursorAt(10, 10, null, null));
        options.setAppendTo("body");
        options.setRevert(RevertOption.ON_INVALID_DROP);
        options.setRevertDuration(100);
        options.setOnDragStart(new DragFunction() {
          public void f(DragContext context) {
            DeviceCommandDTO deviceCommandDTO = context.getDraggableData();
            context.getHelper().setInnerHTML(deviceCommandDTO.getDisplayName());
          }
        }); 
        node.setDraggableOptions(options);
        return node;
      }
      return null;
   }

   @Override
   public boolean isLeaf(Object value) {
      return (value instanceof DeviceCommandDTO);
   }

}
