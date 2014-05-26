package org.openremote.modeler.client.widget;

import java.util.ArrayList;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceProxyGWT;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.TreeViewModel;

public class DeviceCommandTreeModel implements TreeViewModel {

   private static final Icons ICON = GWT.create(Icons.class);
   private AsyncDataProvider<DeviceDTO> deviceDTOList;
   private AsyncDataProvider<DeviceCommandDTO> deviceCommandsDTOList;
   protected DeviceDTO currentDeviceValue;
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
        super("dragstart");
        this.imageHtml = ICON.deviceCmd().getHTML();
      }

      @Override
      public void render(Context context, DeviceCommandDTO value, SafeHtmlBuilder sb) {
        if (value != null) {
          sb.appendHtmlConstant("<div draggable=\"true\">").appendHtmlConstant(imageHtml).appendEscaped(" ");
          sb.appendEscaped(value.getDisplayName()).appendHtmlConstant("</div>");
        }
      }
      
      @Override
      public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
        Element parent, DeviceCommandDTO value, NativeEvent event,
        ValueUpdater<DeviceCommandDTO> valueUpdater) {
      // TODO Auto-generated method stub
      //super.onBrowserEvent(context, parent, value, event, valueUpdater);
        
      event.getDataTransfer().setDragImage(parent, 10, 10);
      event.getDataTransfer().setData("deviceCommandId", Long.toString(value.getOid()));
      }
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
         return new DefaultNodeInfo<DeviceDTO>(deviceDTOList, new DeviceCell());
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
         DefaultNodeInfo<DeviceCommandDTO> node = new DefaultNodeInfo<DeviceCommandDTO>(deviceCommandsDTOList,new CommandCell());
         //node.getCell().onBrowserEvent(context, parent, value, event, valueUpdater);
         return node;
      }
      return null;
   }

   @Override
   public boolean isLeaf(Object value) {
      return (value instanceof DeviceCommandDTO);
   }

}
