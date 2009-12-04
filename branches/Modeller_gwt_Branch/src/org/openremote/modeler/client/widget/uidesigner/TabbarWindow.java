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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SelectEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SelectListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.CommonWindow;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.domain.component.UImage;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.core.client.GWT;

public class TabbarWindow extends CommonWindow {

   public static final String TABBAR_ITEM_IMAGE = "tabbarItemImage";
   private List<UITabbarItem> tabbarItems = null;
   private ListView<BeanModel> tabbarItemListView;
   private UITabbarItem selectTabbarItem = new UITabbarItem();
   public TabbarWindow(boolean isGlobal, List<UITabbarItem> tabbarItems) {
      super();
      this.tabbarItems = tabbarItems;
      init(isGlobal);
      show();
   }
   
   private void init(boolean isGlobal) {
      if (isGlobal) {
         setHeading("Global tabbar");
      } else {
         setHeading("Local tabbar");
      }
      setWidth(385);
      setAutoHeight(true);
      FormLayout formLayout = new FormLayout();
      formLayout.setLabelAlign(LabelAlign.TOP);
      formLayout.setLabelWidth(200);
      formLayout.setDefaultWidth(350);
      setLayout(formLayout);
      createFields();
      createButtons();
      setBodyStyleName("padding-top-left-10px");
   }
   private void createFields() {
      AdapterField tabbarField = new AdapterField(createTabbarContainer(tabbarItems));
      tabbarField.setFieldLabel("Tabbar item list");
      
      AdapterField tabbarItemPropertyField = new AdapterField(createTabbarItemPropertyForm());
      tabbarItemPropertyField.setFieldLabel("Selected tabbar item properties");
      
      add(tabbarField);
      add(tabbarItemPropertyField);
   }
   private LayoutContainer createTabbarContainer(List<UITabbarItem> tabbarItems) {
      LayoutContainer tabbarContainer = new LayoutContainer();
      tabbarContainer.setBorders(false);
      tabbarContainer.setSize(340, 140);
      HBoxLayout tabbarContainerLayout = new HBoxLayout();
      tabbarContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      tabbarContainer.setLayout(tabbarContainerLayout);
      
      ContentPanel tabbarItemsContainer = new ContentPanel();
      tabbarItemsContainer.setBorders(false);
      tabbarItemsContainer.setBodyBorder(false);
      tabbarItemsContainer.setHeaderVisible(false);
      tabbarItemsContainer.setWidth(260);
      tabbarItemsContainer.setHeight(130);
      tabbarItemsContainer.setLayout(new FitLayout());
      tabbarItemsContainer.setScrollMode(Scroll.AUTO);
      
      tabbarItemListView = new ListView<BeanModel>();
      
      final ListStore<BeanModel> store = new ListStore<BeanModel>();
      for (UITabbarItem tabbarItem : tabbarItems) {
         store.add(tabbarItem.getBeanModel());
      }
      tabbarItemListView.setStore(store);
      tabbarItemListView.setDisplayProperty("displayName");
      tabbarItemsContainer.add(tabbarItemListView);
      
      LayoutContainer buttonsContainer = new LayoutContainer();
      buttonsContainer.setSize(80, 130);
      buttonsContainer.setBorders(false);
      buttonsContainer.setLayout(new RowLayout(Orientation.VERTICAL));
      
      Button addItemBtn = new Button("Add");
      addItemBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final UITabbarItem tabbarItem = new UITabbarItem();
            store.add(tabbarItem.getBeanModel());
         }
      });
      
      Button deleteItemBtn = new Button("Delete");
      deleteItemBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedItem = tabbarItemListView.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
               store.remove(selectedItem);
            }
         }
      });
      
      Button itemUpBtn = new Button("Up");
      itemUpBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedItem = tabbarItemListView.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
               int index = store.indexOf(selectedItem);
               if(index > 0) {
                  store.remove(selectedItem);
                  store.insert(selectedItem, index - 1);
                  tabbarItemListView.getSelectionModel().select(selectedItem, false);
               }
            }
         }
      });
      
      Button itemDownBtn = new Button("Down");
      itemDownBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedItem = tabbarItemListView.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
               int index = store.indexOf(selectedItem);
               if (index < store.getCount() - 1) {
                  store.remove(selectedItem);
                  store.insert(selectedItem, index + 1);
                  tabbarItemListView.getSelectionModel().select(selectedItem, false);
               }
            }
         }
      });
      
      buttonsContainer.add(addItemBtn, new RowData(80, -1, new Margins(5)));
      buttonsContainer.add(deleteItemBtn, new RowData(80, -1, new Margins(5)));
      buttonsContainer.add(itemUpBtn, new RowData(80, -1, new Margins(5)));
      buttonsContainer.add(itemDownBtn, new RowData(80, -1, new Margins(5)));
      
      tabbarItemListView.addListener(Events.Select, new Listener<ListViewEvent<BeanModel>>() {
         @Override
         public void handleEvent(ListViewEvent<BeanModel> be) {
            selectTabbarItem = (UITabbarItem)be.getModel().getBean();
            fireEvent(SelectEvent.SELECT, new SelectEvent(selectTabbarItem));
         }
         
      });
      tabbarContainer.add(tabbarItemsContainer);
      tabbarContainer.add(buttonsContainer);
      return tabbarContainer;
   }
   
   private FieldSet createTabbarItemPropertyForm() {
      FieldSet tabbarItemForm = new FieldSet();
      tabbarItemForm.setBorders(true);
      tabbarItemForm.setWidth(340);
      tabbarItemForm.setStyleAttribute("paddingTop", "2px");
      tabbarItemForm.setStyleAttribute("paddingBottom", "2px");
      FormLayout lay = new FormLayout();
      lay.setLabelAlign(LabelAlign.TOP);
      lay.setLabelWidth(80);
      lay.setDefaultWidth(200);
      tabbarItemForm.setLayout(lay);
      
      // initial name field.
      final TextField<String> nameField = new TextField<String>();
      nameField.setFieldLabel("Name");
      nameField.addListener(Events.Blur, new Listener<BaseEvent>(){
         @Override
         public void handleEvent(BaseEvent be) {
            selectTabbarItem.setName(nameField.getValue());
            tabbarItemListView.getStore().update(selectTabbarItem.getBeanModel());
         }
      });
      final FormPanel imageForm = new FormPanel();
      imageForm.setHeaderVisible(false);
      imageForm.setBorders(false);imageForm.setBodyBorder(false);
      imageForm.setPadding(0);
      imageForm.setLabelAlign(LabelAlign.TOP);
      final FileUploadField imageField = new FileUploadField() {

         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);
            if (!imageForm.isValid()) {
             return;
            }
            imageForm.submit();
         }
         
      };
      imageField.setFieldLabel("Image");
      imageField.setName(TABBAR_ITEM_IMAGE);
      imageField.setRegex(".+?\\.(png|gif|jpg|PNG|GIF|JPG)");
      imageField.getMessages().setRegexText("Please select a gif, jpg or png type image.");
      imageField.setStyleAttribute("overflow", "hidden");
      imageForm.add(imageField);
      imageForm.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=uploadImage&uploadFieldName="+TABBAR_ITEM_IMAGE);
      imageForm.setEncoding(Encoding.MULTIPART);
      imageForm.setMethod(Method.POST);
      imageForm.addListener(Events.Submit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            UImage image = selectTabbarItem.getImage();
            String imageSrc =  be.getResultHtml();
            if (image == null) {
               image = new UImage(imageSrc);
            } else {
               image.setSrc(imageSrc);
            }
            selectTabbarItem.setImage(image);
            be.cancelBubble();
         }
      });
      
      // initial navigate properties
      FieldSet navigateSet = new FieldSet();
      navigateSet.setLayout(new ColumnLayout());
      navigateSet.setHeading("Navigate");
      navigateSet.setWidth(300);
      
      final LayoutContainer rightComboBoxes = new LayoutContainer();
      FormLayout layout = new FormLayout();
      layout.setHideLabels(true);
      layout.setDefaultWidth(100);
      rightComboBoxes.setLayout(layout);
      rightComboBoxes.setLayoutOnChange(true);
      rightComboBoxes.disable();
      
      final ComboBox<ModelData> screenList = new ComboBox<ModelData>();
      screenList.setEmptyText("--screen--");
      screenList.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      screenList.setValueField(ComboBoxDataModel.getDataProperty());
      ListStore<ModelData> screenStore = new ListStore<ModelData>();
      screenList.setStore(screenStore);
      screenList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            Screen selectedScreen = ((ComboBoxDataModel<Screen>)se.getSelectedItem()).getData();
            selectTabbarItem.getNavigate().setToScreen(selectedScreen.getOid());
         }
         
      });
      
      final ComboBox<ModelData> groupList = new ComboBox<ModelData>();
      groupList.setEmptyText("--group--");
      groupList.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      groupList.setValueField(ComboBoxDataModel.getDataProperty());
      ListStore<ModelData> groupStore = new ListStore<ModelData>();
      groupList.setStore(groupStore);
      final List<BeanModel> groupModels = BeanModelDataBase.groupTable.loadAll();
      groupList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            Group selectedGroup = ((ComboBoxDataModel<Group>)se.getSelectedItem()).getData();
            selectTabbarItem.getNavigate().setToGroup(selectedGroup.getOid());
            screenList.clearSelections();
            screenList.getStore().removeAll();
            for (ScreenRef screenRef : selectedGroup.getScreenRefs()) {
               ComboBoxDataModel<Screen> data = new ComboBoxDataModel<Screen>(screenRef.getDisplayName(), screenRef.getScreen());
               screenList.getStore().add(data);
               if (selectTabbarItem.getNavigate().getToScreen() == screenRef.getScreenId()) {
                  screenList.setValue(data);
               }
            }
            if (screenList.getValue() == null) {
               selectTabbarItem.getNavigate().setToScreen(-1);
            }
         }
      });
      for (BeanModel groupModel : groupModels) {
         ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(groupModel.get("name").toString(), (Group) groupModel.getBean());
         groupStore.add(data);
      }
         
      rightComboBoxes.add(groupList);
      rightComboBoxes.add(screenList);
      
      RadioGroup navigateGroup = new RadioGroup();
      navigateGroup.setOrientation(Orientation.VERTICAL);
      final Radio toGroup = new Radio();
      toGroup.setBoxLabel("ToGroup");
      toGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            Boolean value = (Boolean)be.getValue();
            if(value) {
               rightComboBoxes.enable();
            } else {
               selectTabbarItem.getNavigate().setToGroup(-1);
               selectTabbarItem.getNavigate().setToScreen(-1);
               screenList.clearSelections();
               groupList.clearSelections();
               rightComboBoxes.disable();
            }
         }
         
      });
      
      Radio toScreen = new Radio();
      toScreen.setBoxLabel("ToScreen");
      toScreen.disable();
      
      final Radio toSetting = new Radio();
      toSetting.setBoxLabel("ToSetting");
      toSetting.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            selectTabbarItem.getNavigate().setToSetting((Boolean)be.getValue());
         }
      });
      
      final Radio back = new Radio();
      back.setBoxLabel("Back");
      back.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            selectTabbarItem.getNavigate().setBack((Boolean)be.getValue());
         }
      });
      
      final Radio login = new Radio();
      login.setBoxLabel("Login");
      login.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            selectTabbarItem.getNavigate().setLogin((Boolean)be.getValue());
         }
      });
      
      final Radio logout = new Radio();
      logout.setBoxLabel("Logout");
      logout.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            selectTabbarItem.getNavigate().setLogout((Boolean)be.getValue());
         }
      });
      
      final Radio previous = new Radio();
      previous.setBoxLabel("Previous");
      previous.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            selectTabbarItem.getNavigate().setPrevious((Boolean)be.getValue());
         }
      });
      
      final Radio next = new Radio();
      next.setBoxLabel("Next");
      next.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            selectTabbarItem.getNavigate().setNext((Boolean)be.getValue());
         }
      });
      
      navigateGroup.add(toGroup);
      navigateGroup.add(toScreen);
      navigateGroup.add(toSetting);
      navigateGroup.add(back);
      navigateGroup.add(login);
      navigateGroup.add(logout);
      navigateGroup.add(previous);
      navigateGroup.add(next);
      
      navigateSet.add(navigateGroup);
      navigateSet.add(rightComboBoxes);
      
      tabbarItemForm.add(nameField);
      tabbarItemForm.add(imageForm);
      tabbarItemForm.add(navigateSet);
      
      addListener(SelectEvent.SELECT, new SelectListener() {
         @Override
         public void afterSelect(SelectEvent be) {
            UITabbarItem tabbarItem = be.getData();
            nameField.setValue(tabbarItem.getName());
            if (tabbarItem.getImage() != null) {
               String imageSrc = tabbarItem.getImage().getSrc();
               imageField.setValue(imageSrc.substring(imageSrc.lastIndexOf("/") + 1));
            } else {
               imageField.setValue("");
            }
            Navigate navigate = tabbarItem.getNavigate();
            if (navigate.getToGroup() != -1) {
               toGroup.setValue(true);
               for (BeanModel groupModel : groupModels) {
                  ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(groupModel.get("name").toString(), (Group) groupModel.getBean());
                  if(navigate.getToGroup() == ((Group) groupModel.getBean()).getOid()) {
                     groupList.setValue(data);
                }
               }
            } else if(navigate.getToGroup() == -1) {
               toGroup.setValue(false);
            }
            toSetting.setValue(navigate.isToSetting());
            back.setValue(navigate.isBack());
            login.setValue(navigate.isLogin());
            logout.setValue(navigate.isLogout());
            previous.setValue(navigate.isPrevious());
            next.setValue(navigate.isNext());
         }
         
      });
      return tabbarItemForm;
   }
   private void createButtons() {
      Button okBtn = new Button("OK");
      Button cancelBtn = new Button("Cancel");

      okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (tabbarItems == null) {
               tabbarItems = new ArrayList<UITabbarItem>();
            }
            tabbarItems.clear();
            
            for (BeanModel tabbarItemModel: tabbarItemListView.getStore().getModels()) {
               UITabbarItem tabbarItem = (UITabbarItem)tabbarItemModel.getBean();
               if(!tabbarItem.getNavigate().isSet()) {
                  MessageBox.alert("ERROR", "Tabbar item \"" + tabbarItem.getName() + "\" is required to set navigate.", null);
                  return;
               }
               tabbarItems.add(tabbarItem);
            }
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(tabbarItems));
         }
         
      });
      cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            TabbarWindow.this.hide();
         }
      });

      addButton(okBtn);
      addButton(cancelBtn);
   }
}
