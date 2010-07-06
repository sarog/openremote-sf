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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.buildingmodeler.TemplateCreateWindow;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Template panel.
 *
 * @author Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class TemplatePanel extends ContentPanel {

   private ListView<BeanModel> templateView;
   private Icons icon = GWT.create(Icons.class);

   /**
    * Instantiates a new profile panel.
    */
   public TemplatePanel() {
      setHeading("Template");
      setIcon(icon.templateIcon());
      setLayout(new FitLayout());
      createMenu();
      buildTemplateList();
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      List<Button> editDelBtns = new ArrayList<Button>();
      toolBar.add(createNewTemplateMenuItem());

      Button deleteBtn = createDeleteBtn();
      deleteBtn.setEnabled(true);
      
      toolBar.add(deleteBtn);
      editDelBtns.add(deleteBtn);
      setTopComponent(toolBar);
   }


   /**
    * Creates the delete button.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());

      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            final BeanModel templateBeanModel = templateView.getSelectionModel().getSelectedItem();
            
            if (templateBeanModel == null) {
               MessageBox.alert("Error", "Please select a template.", null);
               ce.cancelBubble();
            }

            else {
               Template template = templateBeanModel.getBean();
               Long oid = template.getOid();

               TemplateProxy.deleteTemplateById(oid, new AsyncSuccessCallback<Boolean>() {
                  @Override
                  public void onSuccess(Boolean success) {
                     if (success) {
                        templateView.getStore().remove(templateBeanModel);
                        Info.display("Delete Template", "Template deleted successfully.");
                     }
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error", "Failed to delete template. Beehive not currently available. Error message :"+caught.getMessage(), null);
                  }

               });
            }
         }
      });
      return deleteBtn;
   }

   private Button createNewTemplateMenuItem() {
      Button newPanelItem = new Button("New");
      newPanelItem.setIcon(icon.add());
      newPanelItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            final TemplateCreateWindow templateCreateWindow = new TemplateCreateWindow();
            templateCreateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  Template template = be.getData();
                  if(template.getShareTo() != Template.PUBLIC){
                     templateView.getStore().add(template.getBeanModel());
                  }
               }

            });

         }
      });
      return newPanelItem;
   }

   private void buildTemplateList() {
      this.templateView = new ListView<BeanModel>();
      templateView.setStateful(true);
      templateView.setBorders(false);
      templateView.setHeight("100%");      
      templateView.setDisplayProperty("name");
      TemplateProxy.getTemplates(true, new AsyncCallback<List<Template>>(){

         @Override
         public void onFailure(Throwable caught) {
            Info.display("Error", "Failed to get your templates, error message: "+caught.getMessage());
         }

         @Override
         public void onSuccess(List<Template> result) {
            if (result.size() > 0) {
               ListStore<BeanModel> store = new ListStore<BeanModel> ();
               store.add(Template.createModels(result));
               templateView.setStore(store);
               layout();
            }
         }
         
      });
      add(templateView);
   }
}
