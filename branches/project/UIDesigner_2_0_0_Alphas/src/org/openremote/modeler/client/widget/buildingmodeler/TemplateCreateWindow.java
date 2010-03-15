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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A window for creating a Template.
 *
 * @author javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class TemplateCreateWindow extends FormWindow
{
   public static final String TEMPLATE_NAME_FIELD = "name";
   public static final String TEMPLATE_CONTENT_FIELD = "content";

   private ListView<BeanModel> screenList = new ListView<BeanModel>();

   private TextField<String> templateName = new TextField<String>();
   
   private long shareInfo = Template.PRIVATE;
   
   public TemplateCreateWindow(){
      setPlain(true);  
      setSize(350, 300);  
      setHeading("New Template");
      setBodyBorder(true);
      createField();
      initScreenList();
      show();
   }
   
   private void createField(){
      templateName = new TextField<String>();
      templateName.setName(TEMPLATE_NAME_FIELD);
      templateName.setFieldLabel("Name");
      templateName.setAllowBlank(false);
      templateName.setValidateOnBlur(true);
      
      form.setBorders(false);  
      form.setBodyBorder(false);  
      form.setLabelWidth(55);  
      form.setPadding(5);  
      form.setHeaderVisible(false); 
      
      Button submitBtn = new Button();
      submitBtn.setText("Submit");
      Button cancleBtn = new Button();
      cancleBtn.setText("Close");
      submitBtn.addSelectionListener(new SubmitListener());
      cancleBtn.addSelectionListener(new CancleListener());
      
      form.add(templateName);
      form.add(createShareView());
      form.addButton(submitBtn);
      form.addButton(cancleBtn);
      add(form);
      
   }
   
   private void initScreenList() {
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      store.add(BeanModelDataBase.screenTable.loadAll());
      screenList.setStore(store);
      screenList.setDisplayProperty("displayName");
      screenList.setHeight(100);
      screenList.setStyleAttribute("overflow", "auto");
      FieldSet screenListGroup = new FieldSet();
      // overflow-auto style is for IE hack.
      screenListGroup.addStyleName("overflow-auto");
      screenListGroup.setHeading("Select a screen");
      screenListGroup.add(screenList);
      form.add(screenListGroup);
   }

   private RadioGroup createShareView(){
      RadioGroup shareRadioGroup = new RadioGroup();
      
      Radio shareNoneRadio = new Radio();
      shareNoneRadio.setName("Private");
      shareNoneRadio.setBoxLabel("Private");
      shareNoneRadio.setValue(true);
      shareNoneRadio.addListener(Events.Change, new Listener<FieldEvent>(){

         @Override
         public void handleEvent(FieldEvent be) {
            Boolean noShare = (Boolean) be.getValue();
            if(noShare) {
               shareInfo = Template.PRIVATE;
            } else {
               shareInfo = Template.PUBLIC;
            }
            
         }
         
      });
      
      Radio shareToAllRadio = new Radio();
      shareToAllRadio.setName("Public");
      shareToAllRadio.setBoxLabel("Public");
      
      shareRadioGroup.add(shareNoneRadio);
      shareRadioGroup.add(shareToAllRadio);
      shareRadioGroup.setFieldLabel("Share to");
      
      return shareRadioGroup;
   }
   class SubmitListener extends SelectionListener<ButtonEvent> {

      @Override
      public void componentSelected(ButtonEvent ce) {
         if(templateName.getValue()==null || templateName.getValue().trim().length()==0){
            return;
         }
         List<BeanModel> screenBeanModels = screenList.getSelectionModel().getSelectedItems();
         if (screenBeanModels == null || screenBeanModels.size() != 1) {
            MessageBox.alert("Error", "One (and only one) screen must be selected", null);
            return;
         }
         Screen screen = screenBeanModels.get(0).getBean();
         Template template = new Template(templateName.getValue(), screen);
         template.setShareTo(shareInfo);
         TemplateProxy.saveTemplate(template, new AsyncSuccessCallback<Template>() {

            @Override
            public void onSuccess(Template result) {
               Info.display("Success", "Template saved successfully:(id,name)(" + result.getOid()+","+result.getName()+")");
               TemplateCreateWindow.this.unmask();
               result.getBeanModel().set("id", result.getOid());
               fireEvent(SubmitEvent.SUBMIT,new SubmitEvent(result));
               hide();
            }

            @Override
            public void onFailure(Throwable caught) {
               MessageBox.alert("error","Beehive database not available at the moment.",null);
               TemplateCreateWindow.this.unmask();
            }
            
            
         });
         TemplateCreateWindow.this.mask("The template is being created... ");
      }

   }

   class CancleListener extends SelectionListener<ButtonEvent> {

      @Override
      public void componentSelected(ButtonEvent ce) {
         hide();
      }

   }
   
}
