package org.openremote.modeler.client.widget.uidesigner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.IconResources;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.IconPreviewWidget;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.shared.GraphicalAssetDTO;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class ImageAssetPicker extends Window {

  private static ImageAssetPickerUiBinder uiBinder = GWT.create(ImageAssetPickerUiBinder.class);

  interface ImageAssetPickerUiBinder extends UiBinder<Widget, ImageAssetPicker> {
  }
  
  public interface ImageAssetPickerListener {
    void imagePicked(String imageURL);
  }

  interface GraphicalAssetDTOProvider extends PropertyAccess<GraphicalAssetDTO> {    
    @Path("url")
    ModelKeyProvider<GraphicalAssetDTO> key();
    
    ValueProvider<GraphicalAssetDTO, String> name();
  }
  
  @UiFactory
  Window itself() {
    return this;
  }

  @UiField(provided = true)
  BorderLayoutData northData = new BorderLayoutData(40);
  @UiField(provided = true)
  BorderLayoutData westData = new BorderLayoutData(.40);
  
  @UiField(provided = true)
  BoxLayoutData addImageButtonLayoutData = new BoxLayoutData(new Margins(0, 0, 0, 8));
  
  private GraphicalAssetDTOProvider assets = GWT.create(GraphicalAssetDTOProvider.class);
  
  private ImageAssetPickerListener listener;
  
  private ColumnModel<GraphicalAssetDTO> cm;
  private ListStore<GraphicalAssetDTO> store;
  private GridSelectionModel<GraphicalAssetDTO> selectionModel;
  private GridView<GraphicalAssetDTO> gridView;
  
  public void setListener(ImageAssetPickerListener listener) {
    this.listener = listener;
  }

  public ImageAssetPicker(final String currentImageURL) {
    westData.setSplit(true);

    ColumnConfig<GraphicalAssetDTO, String> deleteColumn = new ColumnConfig<GraphicalAssetDTO, String>(new ValueProvider<GraphicalAssetDTO, String>() {
      @Override
      public String getValue(GraphicalAssetDTO object) {
        return "";
      }

      @Override
      public void setValue(GraphicalAssetDTO object, String value) {
      }

      @Override
      public String getPath() {
        return "";
      }
    }, 38, "");
    deleteColumn.setResizable(false);
    deleteColumn.setMenuDisabled(true);
    ButtonCell<String> button = new ButtonCell<String>();
    button.setIcon(IconResources.INSTANCE.delete());
    button.addSelectHandler(new SelectHandler() {
      @Override
      public void onSelect(SelectEvent event) {
        Context c = event.getContext();
        int row = c.getIndex();
        final GraphicalAssetDTO object = store.get(row);
        
        if (isImageInUse(object.getName())) {
          new AlertMessageBox("Delete error", "The image is used and can't be deleted.").show();
        } else {          
          UtilsProxy.deleteImage(object.getName(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
              if (selectionModel.getSelectedItem() == object) {
                selectionModel.deselect(selectionModel.getSelectedItem());
                imagePreview.setVisible(false);
              }
              store.remove(object);
            }

            @Override
            public void onFailure(Throwable caught) {
              new AlertMessageBox("Communication error", "Could not read assets library from server.").show();
            }
          });
        }
      }
    });
    deleteColumn.setCell(button);

    ColumnConfig<GraphicalAssetDTO, String> fileNameColumn = new ColumnConfig<GraphicalAssetDTO, String>(assets.name(), 150, "Name");
    
    List<ColumnConfig<GraphicalAssetDTO, ?>> l = new ArrayList<ColumnConfig<GraphicalAssetDTO, ?>>();
    l.add(deleteColumn);
    l.add(fileNameColumn);
    cm = new ColumnModel<GraphicalAssetDTO>(l);
    store = new ListStore<GraphicalAssetDTO>(assets.key());
    store.addSortInfo(new StoreSortInfo<GraphicalAssetDTO>(assets.name(), SortDir.ASC));
    
    uiBinder.createAndBindUi(this);

    gridView.setAutoExpandColumn(fileNameColumn);
    
    UtilsProxy.getUserImagesURLs(new AsyncSuccessCallback<List<GraphicalAssetDTO>>() {
      @Override
      public void onSuccess(List<GraphicalAssetDTO> result) {
        store.addAll(result);
        selectImage(currentImageURL);
        gridView.focusRow(store.getAll().indexOf(selectionModel.getSelectedItem()));
      }
    });
  }

  @UiFactory
  Grid<GraphicalAssetDTO> createGrid() {
    Grid<GraphicalAssetDTO> grid = new Grid<GraphicalAssetDTO>(store, cm);
    selectionModel = grid.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.SINGLE);
    selectionModel.addSelectionChangedHandler(new SelectionChangedHandler<GraphicalAssetDTO>() {
      @Override
      public void onSelectionChanged(SelectionChangedEvent<GraphicalAssetDTO> event) {
        okButton.setEnabled(selectionModel.getSelectedItem() != null);
        imagePreview.setVisible(selectionModel.getSelectedItem() != null);        
        if (selectionModel.getSelectedItem() != null) {
          imagePreview.setUrl(selectionModel.getSelectedItem().getUrl());
        }
      }
    });
    gridView = grid.getView();
    return grid;
  }
  
  @Override
  public void show() {
    super.show();
    // MODELER-399: Most UI elements are rendered using GXT2, this one uses GXT3.
    // Both XDOM classes in GXT2 and GXT3 maintain the top z index, but independently of each other.
    // Set our zIndex here in GXT3 "world" based on top index from GXT2 "world".
    setZIndex(XDOM.getTopZIndex() + 1);
  }

  @UiField
  Image imagePreview;
  
  @UiField
  TextButton okButton;
  
  @UiHandler("addButton")
  void onAddClick(SelectEvent e) {
    ChangeIconWindow selectImageONWindow = new ChangeIconWindow(new IconPreviewWidget(100, 100), 100);
//    getElement().getStyle().setZIndex(Integer.parseInt(selectImageONWindow.getElement().getStyle().getZIndex()) - 1);
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
         final String imageUrl = be.getData();
         store.add(new GraphicalAssetDTO(imageUrl.substring(imageUrl.lastIndexOf("/") + 1), imageUrl));
         selectImage(imageUrl);
         gridView.focusRow(store.getAll().indexOf(selectionModel.getSelectedItem()));
       }
    });

  }

  @UiHandler("cancelButton")
  public void onCancelClick(SelectEvent event) {
    hide();
  }

  @UiHandler("okButton")
  public void onOKClick(SelectEvent event) {
    if (listener != null) {
      listener.imagePicked(selectionModel.getSelectedItem().getUrl());
    }
    hide();
  }

  private void selectImage(final String currentImageURL) {
    for (GraphicalAssetDTO ga : store.getAll()) {
      if (ga.getUrl().equals(currentImageURL) || ga.getName().equals(currentImageURL)) { // TODO: testing also on image name, not nice but used for states on image widgets
        selectionModel.select(ga,  false);
        break;
      }
    }
    // Note if comparison on name is not required, then can use directly Store.findModelWithKey()    
  }
  
  private boolean isImageInUse(String imageName) {
    for (BeanModel panelBeanModel : BeanModelDataBase.panelTable.loadAll()) {
      Panel p = panelBeanModel.getBean();
      for (Group g : p.getGroups()) {
        for (Screen s : g.getLandscapeScreens()) {
          if (isImageUsedInScreen(imageName, s)) {
            return true;
          }
        }
        for (Screen s : g.getPortraitScreens()) {
          if (isImageUsedInScreen(imageName, s)) {
            return true;
          }
        }
        if (isImageUsedByTabBarItems(imageName, g.getTabbarItems())) {
          return true;
        }
      }
      if (isImageUsedByTabBarItems(imageName, p.getTabbarItems())) {
        return true;
      }
    }
    return false;
  }

  private boolean isImageUsedByTabBarItems(String imageName, Collection<UITabbarItem> items) {
    for (UITabbarItem tbi : items) {
      if (imageName.equals(tbi.getImage().getImageFileName())) {
        return true;
      }
    }
    return false;
  }

  private boolean isImageUsedInScreen(String imageName, Screen s) {
    if (imageName.equals(s.getBackground().getImageSource().getImageFileName())) {
      return true;
    }
    for (ImageSource src : s.getAllImageSources()) {
      if (imageName.equals(src.getImageFileName())) {
        return true;
      }
    }
    return false;
  }

}