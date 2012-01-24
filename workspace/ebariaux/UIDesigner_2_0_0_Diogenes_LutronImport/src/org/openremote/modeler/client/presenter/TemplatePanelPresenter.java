/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.client.presenter;

import java.util.List;

import org.openremote.modeler.client.event.TemplateSelectedEvent;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.client.widget.uidesigner.TemplatePanel;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TemplatePanelPresenter implements Presenter {

  private HandlerManager eventBus;
  private TemplatePanel view;
  
  public TemplatePanelPresenter(HandlerManager eventBus, TemplatePanel view) {
    super();
    this.eventBus = eventBus;
    this.view = view;
    bind();
  }

  private void bind() {
    this.view.getSelectionService().addListener(new SelectionChangedListener<BeanModel>() {
      @Override
      public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
        List<BeanModel> selection = se.getSelection();
        if (selection.size() == 1) {
          BeanModel selectModel = selection.get(0);
          if (selectModel.getBean() instanceof Template) {
            Template template = selectModel.getBean();
            TemplatePanelPresenter.this.setTemplateInEditing(template);
          }
        }
      }      
    });
    
    this.view.getDeleteButton().addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
      final TreePanel<BeanModel> templateTree = view.getTemplateTree();
      @Override
      public void onDelete(ButtonEvent ce) {
         List<BeanModel> templateBeanModels = templateTree.getSelectionModel().getSelectedItems();
         if (templateBeanModels == null || templateBeanModels.size() == 0) {
            MessageBox.alert("Error", "Please select a template.", null);
            ce.cancelBubble();
         } else {
            for (final BeanModel templateBeanModel : templateBeanModels) {
               Template template = templateBeanModel.getBean();
               Long oid = template.getOid();
               TemplateProxy.deleteTemplateById(oid, new AsyncSuccessCallback<Boolean>() {
                  @Override
                  public void onSuccess(Boolean success) {
                     if (success) {
                       view.setTemplateInEditing(null);

                        templateTree.getStore().remove(templateBeanModel);
                        if (view.getEditTabItem() != null) {
                          eventBus.fireEvent(new TemplateSelectedEvent(null));
                          view.setEditTabItem(null);
                        }
                        Info.display("Delete Template", "Template deleted successfully.");
                     }
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error", "Failed to delete template :\"" + caught.getMessage() + "\"", null);
                     super.checkTimeout(caught);
                  }

               });

            }
         }
      }
   });

    
    
    
    
  }

  // TODO EBR : method moved from TemplatePanel was synchronized. Is this required here?
  public synchronized void setTemplateInEditing(final Template templateInEditing) {
    if (templateInEditing != null && view.getTemplateInEditing() != null ) {
       if (templateInEditing.getOid() == view.getTemplateInEditing().getOid()) return;
    }
    if (view.getTemplateInEditing() != null) {
       // 1, save previous template.
       view.mask("Saving previous template.....");
       TemplateProxy.updateTemplate(view.getTemplateInEditing(), new AsyncCallback<Template>() {

          @Override
          public void onFailure(Throwable caught) {
             view.unmask();
             Info.display("Error", "Update template: " + view.getTemplateInEditing().getName() + " failed");
             buildScreen(templateInEditing);
          }

          @Override
          public void onSuccess(Template result) {
             // 2, make sure the content for the previous template be updated. 
             if (result.getOid() == view.getTemplateInEditing().getOid()){
                view.getTemplateInEditing().setContent(result.getContent());
                Info.display("Success", "Save template " + view.getTemplateInEditing().getName() + " successfully !");
             }
             view.mask("Building screen and downloading resources ...");
             // 3, edit another template.
             buildScreen(templateInEditing);
          }

          
       });
    } else {
       view.mask("Building screen and downloading resources ...");
       view.setTemplateInEditing(templateInEditing);
       buildScreen(templateInEditing);
    }
 }
 
 private void buildScreen(final Template templateInEditing) {
    if (templateInEditing.getScreen() != null) {
       view.unmask();
       view.setEditTabItem(new ScreenTab(templateInEditing.getScreen()));
       // TODO EBR : this instance of ScreenTab will not be equal to the one created in the event handler by the ScreenPanelPresenter
       // This might cause a glitch in some existing code (see TemplatePanel.saveTemplateUpdates)
       // but this should be get rid of anyway in a next iteration
       view.setTemplateInEditing(templateInEditing);
       eventBus.fireEvent(new TemplateSelectedEvent(templateInEditing));
       return;
    }
    
    TemplateProxy.buildScreen(templateInEditing, new AsyncCallback<ScreenPair>() {

       @Override
       public void onFailure(Throwable caught) {
          MessageBox.alert("Error", "Failed to preview Template: " + templateInEditing.getName(), null);
          view.unmask();
       }

       @Override
       public void onSuccess(ScreenPair screen) {
          view.unmask();
          view.setEditTabItem(new ScreenTab(screen));
          templateInEditing.setScreen(screen);
          view.setTemplateInEditing(templateInEditing);
          eventBus.fireEvent(new TemplateSelectedEvent(templateInEditing));
       }

    });
 }
 }
