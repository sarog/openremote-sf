package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.IconPreviewWidget;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.shared.GraphicalAssetDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ImageAssetPicker extends DialogBox {

  private static ImageAssetPickerUiBinder uiBinder = GWT.create(ImageAssetPickerUiBinder.class);

  interface ImageAssetPickerUiBinder extends UiBinder<Widget, ImageAssetPicker> {
  }
  
  private final SingleSelectionModel<GraphicalAssetDTO> selectionModel = new SingleSelectionModel<GraphicalAssetDTO>();

  public interface ImageAssetPickerListener {
    void imagePicked(String imageURL);
  }

  @UiFactory
  DialogBox itself() {
    return this;
  }

  private ImageAssetPickerListener listener;
  
  public void setListener(ImageAssetPickerListener listener) {
    this.listener = listener;
  }

  public ImageAssetPicker(final String currentImageURL) {
    uiBinder.createAndBindUi(this);
    mainLayout.setSize("50em", "20em");
    getElement().getStyle().setZIndex(Integer.MAX_VALUE - 1); // TODO: check how we can be sure of the value to use

    TextColumn<GraphicalAssetDTO> fileNameColumn = new TextColumn<GraphicalAssetDTO>() {
      @Override
      public String getValue(GraphicalAssetDTO asset) {
        return asset.getName();
      }
    };
    table.addColumn(fileNameColumn, "Name");
    table.setSelectionModel(selectionModel);
    
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {      
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        imagePreview.setUrl(selectionModel.getSelectedObject().getUrl());
      }
    });
    
    UtilsProxy.getUserImagesURLs(new AsyncSuccessCallback<List<GraphicalAssetDTO>>() {
      @Override
      public void onSuccess(List<GraphicalAssetDTO> result) {
        table.setRowData(result);
        for (GraphicalAssetDTO ga : result) {
          if (ga.getUrl().equals(currentImageURL) || ga.getName().equals(currentImageURL)) { // TODO: testing also on image name, not nice but used for states on image widgets
            selectionModel.setSelected(ga, true);
            break;
          }
        }
      }
    });
  }

  @UiField
  DockLayoutPanel mainLayout;

  @UiField
  CellTable<GraphicalAssetDTO> table;
  
  @UiField
  Image imagePreview;
  
  @UiField
  Button cancelButton;
  
  @UiField
  Button okButton;
  
  @UiField
  Button addButton;
  
  @UiHandler("addButton")
  void handleAdd(ClickEvent e) {
    ChangeIconWindow selectImageONWindow = new ChangeIconWindow(new IconPreviewWidget(100, 100), 100);
    getElement().getStyle().setZIndex(Integer.parseInt(selectImageONWindow.getElement().getStyle().getZIndex()) - 1);
    setModal(false);
    
    selectImageONWindow.addListener(Events.Hide, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        setModal(true);
      }
    });
    
    selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
       @Override
       public void afterSubmit(SubmitEvent be) {
//          String imageUrl = be.getData();
         
         // TODO: reload table data / add added file to list
         UtilsProxy.getUserImagesURLs(new AsyncSuccessCallback<List<GraphicalAssetDTO>>() {
           @Override
           public void onSuccess(List<GraphicalAssetDTO> result) {
             table.setRowData(result);
           }
         });

       }
    });

  }

  @UiHandler("cancelButton")
  void handleCancel(ClickEvent e) {
    hide();
  }

  @UiHandler("okButton")
  void handleOK(ClickEvent e) {
    if (listener != null) {
      listener.imagePicked(selectionModel.getSelectedObject().getUrl());
    }
    hide();
  }

}
