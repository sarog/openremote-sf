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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.NestedJsonLoadResultReader;
import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.UtilsRPCService;
import org.openremote.modeler.client.rpc.UtilsRPCServiceAsync;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class ChangeIconWindow.
 */
public class ChangeIconWindow extends Dialog {

   /** The beehive rest icon url. */
   public static String beehiveRestIconUrl = null;
   
   /** The utils service. */
   private UtilsRPCServiceAsync utilsService = (UtilsRPCServiceAsync) GWT.create(UtilsRPCService.class);
   
   /** The Constant FROM_BEEHIVE. */
   private static final String FROM_BEEHIVE = "fromBeehive";
   
   /** The Constant FROM_URL. */
   private static final String FROM_URL = "fromURL";
   
   /** The Constant FROM_LOCAL. */
   private static final String FROM_LOCAL = "fromLocal";
   
   /** The radio group. */
   private RadioGroup radioGroup = new RadioGroup();
   
   /** The beehive icons view. */
   private ListView<ModelData> beehiveIconsView;
   
   /** The url panel. */
   private FormPanel urlPanel = new FormPanel();
   
   /** The upload panel. */
   private FormPanel uploadPanel = new FormPanel();
   
   /** The preview icon container. */
   private LayoutContainer previewIconContainer = new LayoutContainer();
   
   /** The image url. */
   private String imageURL;
   
   /** The upload image url. */
   private String uploadImageURL = null;
   
   /** The screen button. */
   private ScreenButton screenButton;
   
   /** The preview image. */
   private Image previewImage = ((UIDesignerImages)GWT.create(UIDesignerImages.class)).iphoneBtn().createImage();
   
   private ChangeIconWindow window;
   /**
    * Instantiates a new change icon window.
    * 
    * @param screenButton the screen button
    */
   public ChangeIconWindow(ScreenButton screenButton) {
      this.screenButton = screenButton;
      window = this;
      initial();
      show();
   }
   
   /**
    * Initial.
    */
   private void initial(){
      setHeading("Change Icon");
      setMinWidth(500);
      setMinHeight(350);
      setModal(true);
      setLayout(new BorderLayout());
      setButtons(Dialog.OKCANCEL);  
      setHideOnButtonClick(true);
      setBodyBorder(false);
      
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               setImageURL();
               if (imageURL != null) {
                  fireEvent(SubmitEvent.Submit, new SubmitEvent(imageURL));
               } else {
                  MessageBox.alert("Error", "Please select a image.", null);
               }
            }
         }
      }); 
      createRadioContainer();
      
      if(beehiveRestIconUrl == null) {
         utilsService.beehiveRestIconUrl(new AsyncSuccessCallback<String>(){
            @Override
            public void onSuccess(String result) {
               beehiveRestIconUrl = result;
               createContentCotainer();
               layout();
            }
         });
      } else {
         createContentCotainer();
      }
      
      BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 100, 100, 200);  
      eastData.setSplit(true);
      add(previewIconContainer, eastData);
      
   }
   
   /**
    * Creates the radio container.
    */
   private void createRadioContainer() {
      LayoutContainer radioContainer = new LayoutContainer();
      Radio beehiveRadio = new Radio(){
         @Override
         protected void onClick(ComponentEvent be) {
            super.onClick(be);
            if(FROM_BEEHIVE.equals(group.getValue().getValueAttribute())){
               imageURL = null;
               beehiveIconsView.show();
               beehiveIconsView.getSelectionModel().select(0, false);
               urlPanel.hide();
               uploadPanel.hide();
               previewIconContainer.hide();
               layout();
            }
         }
      };
      beehiveRadio.setBoxLabel("Select from beehive");
      beehiveRadio.setValueAttribute(FROM_BEEHIVE);
      beehiveRadio.setValue(true);
      
      Radio fromURL = new Radio(){
         @Override
         protected void onClick(ComponentEvent be) {
            super.onClick(be);
            if(FROM_URL.equals(group.getValue().getValueAttribute())){
               imageURL = null;
               urlPanel.show();
               beehiveIconsView.hide();
               uploadPanel.hide();
               if(previewIconContainer.getItemCount() == 0){
                  initPreviewContainer();
               }
               previewIconContainer.show();
               layout();
            }
         }
      };
      fromURL.setBoxLabel("From a URL");
      fromURL.setValueAttribute(FROM_URL);
      
      Radio uploadIcon = new Radio(){
         @Override
         protected void onClick(ComponentEvent be) {
            super.onClick(be);
            if(FROM_LOCAL.equals(group.getValue().getValueAttribute())){
               imageURL = null;
               uploadPanel.show();
               if(previewIconContainer.getItemCount() == 0){
                  initPreviewContainer();
               }
               previewIconContainer.show();
               beehiveIconsView.hide();
               urlPanel.hide();
               layout();
            }
         }
         
      };
      uploadIcon.setValueAttribute(FROM_LOCAL);
      uploadIcon.setBoxLabel("Upload an Image");

      radioGroup.add(beehiveRadio);
      radioGroup.add(fromURL);
      radioGroup.add(uploadIcon);
      radioContainer.add(radioGroup);
      
      BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 25);
      northData.setMargins(new Margins(5, 5, 0, 5));
      
      add(radioContainer, northData);
   }
   
   /**
    * Creates the content cotainer.
    */
   private void createContentCotainer() {
      
      ContentPanel iconContainer = new ContentPanel();
      iconContainer.setLayout(new FitLayout());
      iconContainer.setBorders(true);
      iconContainer.setBodyBorder(false);
      iconContainer.setHeaderVisible(false);
//      iconContainer.set
      
      iconContainer.add(createBeehiveIconsView());
      urlPanel.setHeaderVisible(false);
      urlPanel.setBorders(false);
      urlPanel.setBodyBorder(false);
      urlPanel.setHeight(80);
      TextField<String> urlField = new TextField<String>();
      urlField.setFieldLabel("URL");
      urlPanel.add(urlField);
      iconContainer.add(urlPanel);
      
      FileUploadField imageUpload = new FileUploadField() {
         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);
            if(!uploadPanel.isValid()){
//               uploadPanel.reset();
               return;
            }
            uploadPanel.submit();
            window.mask("Uploading image...");
         }
      };
      imageUpload.setFieldLabel("File");
      imageUpload.setName("uploadImage");
      imageUpload.setRegex(".+?\\.(png|gif|jpg)");
      imageUpload.getMessages().setRegexText("Please select a gif, jpg or png type image.");
      uploadPanel.setSize(320, 80);
      uploadPanel.setLabelWidth(45);
      uploadPanel.setHeaderVisible(false);
      uploadPanel.setBorders(false);
      uploadPanel.setBodyBorder(false);
      uploadPanel.add(imageUpload);
      uploadPanel.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=uploadImage");
      uploadPanel.setEncoding(Encoding.MULTIPART);
      uploadPanel.setMethod(Method.POST);
      uploadPanel.addListener(Events.Submit, new Listener<FormEvent>(){
         public void handleEvent(FormEvent be) {
            uploadImageURL = be.getResultHtml();
            window.unmask();
         }
      });
      iconContainer.add(uploadPanel);
      beehiveIconsView.show();
      beehiveIconsView.getSelectionModel().select(0, false);
      urlPanel.hide();
      uploadPanel.hide();
      
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
      centerData.setMargins(new Margins(0, 5, 0, 0));
      add(iconContainer, centerData);
   }

   /**
    * Inits the preview container.
    */
   private void initPreviewContainer() {
      VBoxLayout layout = new VBoxLayout();
      layout.setPadding(new Padding(5));
      layout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
      layout.setPack(BoxLayoutPack.CENTER);
      previewIconContainer.setLayout(layout);
      previewIconContainer.setBorders(true);
      previewIconContainer.setStyleAttribute("backgroundColor", "white");
      
      Button preview = new Button("Preview");
      preview.addSelectionListener(new SelectionListener<ButtonEvent>(){
         @Override
         public void componentSelected(ButtonEvent ce) {
            setImageURL(); 
            if(imageURL != null){
               previewImage.setUrl(imageURL);
               previewImage.setSize("46px", "46px");
            } else {
               MessageBox.alert("Error", "Please input a image URL.", null);
            }
         }
      });
      imageURL = screenButton.getButtonIcon();
      if(imageURL != null) {
         previewImage.setUrl(imageURL);
         previewImage.setSize("46px", "46px");
      }
      previewIconContainer.add(previewImage, new VBoxLayoutData(new Margins(0, 0, 5, 0)));
      previewIconContainer.add(preview, new VBoxLayoutData(new Margins(5, 0, 5, 0)));
   }
   
   /**
    * Creates the beehive icons view.
    * 
    * @return the list view< model data>
    */
   private ListView<ModelData> createBeehiveIconsView(){
      ModelType iconType = new ModelType();
      iconType.setRoot("icons.icon");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      iconType.addField(idField);
      iconType.addField("fileName");
      iconType.addField("name");
      
      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(beehiveRestIconUrl);
      NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
            iconType);
      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(scriptTagProxy, reader);

      ListStore<ModelData> store = new ListStore<ModelData>(loader);
      loader.load();
     
      beehiveIconsView = new ListView<ModelData>();
      beehiveIconsView.setId("img-chooser-view");
      beehiveIconsView.setTemplate(getTemplate());
      beehiveIconsView.setStore(store);
      beehiveIconsView.setBorders(false);
      beehiveIconsView.setLoadingText("Loading icons...");
      beehiveIconsView.setItemSelector("div.thumb-wrap");
      return beehiveIconsView;
   }
   
   /**
    * Gets the template.
    * 
    * @return the template
    */
   private native String getTemplate() /*-{ 
        return ['<tpl for=".">', 
        '<div class="thumb-wrap" id="{name}" style="border: 1px solid white">', 
        '<div class="thumb"><img src="{fileName}" title="{name}"></div></div>', 
        '</tpl>', 
        '<div class="x-clear"></div>'].join(""); 
         
        }-*/; 
   
   /**
    * Sets the image url.
    */
   @SuppressWarnings("unchecked")
   private void setImageURL(){
      String radioValue = radioGroup.getValue().getValueAttribute();
      if (FROM_BEEHIVE.equals(radioValue)) {
         imageURL = beehiveIconsView.getSelectionModel().getSelectedItem().get("fileName").toString();
      } else if (FROM_URL.equals(radioValue)) {
         imageURL = ((TextField<String>)urlPanel.getItem(0)).getValue();
      } else if (FROM_LOCAL.equals(radioValue)) {
         imageURL = uploadImageURL;
      }
         
   }
   
}
