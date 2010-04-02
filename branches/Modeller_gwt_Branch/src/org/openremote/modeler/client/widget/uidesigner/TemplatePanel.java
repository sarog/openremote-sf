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
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.client.widget.buildingmodeler.TemplateCreateWindow;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Template panel.
 *
 * @author Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class TemplatePanel extends ContentPanel {
   private TreePanel<BeanModel> templateTree = TreePanelBuilder.buildTemplateTree(this);
   
   private ScreenPanel templateEditPanel = null;
   
   private Template templateInEditing = null;
   
   private ScreenTab editTabItem = null;
   
   private static final int AUTO_SAVE_INTERVAL_MS = 30000;

   private Timer timer;

   private Icons icon = GWT.create(Icons.class);

   /**
    * Instantiates a new profile panel.
    */
   public TemplatePanel(ScreenPanel templateEditPanel) {
      this.templateEditPanel = templateEditPanel;
      setHeading("Template");
      setIcon(icon.templateIcon());
      setLayout(new FitLayout());
      createMenu();
      createTreeContainer();
      createAutoSaveTimer();
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      List<Button> menuButtons = new ArrayList<Button>();
      toolBar.add(createNewTemplateMenuItem());

      Button editBtn = createEditTemplateMenuItem();
      editBtn.setEnabled(true);
      
      Button deleteBtn = createDeleteBtn();
      deleteBtn.setEnabled(true);
      toolBar.add(editBtn);
      
      toolBar.add(deleteBtn);
      menuButtons.add(deleteBtn);
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
            final BeanModel templateBeanModel = templateTree.getSelectionModel().getSelectedItem();
            
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
                        templateTree.getStore().remove(templateBeanModel);
                        if (editTabItem != null) {
                           templateInEditing = null;
                           templateEditPanel.remove(editTabItem);
                           templateEditPanel.closeCurrentScreenTab();
                           editTabItem = null;
                           templateEditPanel.layout();
                        }
                        Info.display("Delete Template", "Template deleted successfully.");
                     }
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error", "Failed to delete template :\""+caught.getMessage()+"\"", null);
                  }

               });
            }
         }
      });
      return deleteBtn;
   }

   private Button createEditTemplateMenuItem() {
      Button editTempalteMenuItem = new Button("Edit");
      editTempalteMenuItem.setIcon(icon.edit());
      editTempalteMenuItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedBean= templateTree.getSelectionModel().getSelectedItem();
            if( selectedBean == null || !(selectedBean.getBean() instanceof Template)) {
               MessageBox.alert("Warn","A template must be selected!",null);
               return;
            }
            //remember the share type information before being updated. 
            final Template template = selectedBean.getBean();
            final boolean shareType = template.isShared();
            
            List<BeanModel> topNode = templateTree.getStore().getRootItems();
            TreeFolderBean tmpPublicTemplateParentNode = topNode.get(0).getBean();
            TreeFolderBean tmpPrivateTemplateParentNode = topNode.get(1).getBean();
            for(BeanModel beanModel : topNode) {
               TreeFolderBean folderBean = beanModel.getBean();
               if(folderBean.getDisplayName().contains("Public") ) {
                  tmpPublicTemplateParentNode = folderBean;
               } else {
                  tmpPrivateTemplateParentNode = folderBean;
               }
            }
            final BeanModel privateTemplateTopNode = tmpPrivateTemplateParentNode.getBeanModel();
            final BeanModel publicTemplateTopNode = tmpPublicTemplateParentNode.getBeanModel();
            final TemplateCreateWindow templateCreateWindow = new TemplateCreateWindow(template);
            
            templateCreateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  Template t = be.getData();
                  template.setContent(t.getContent());
                  template.setScreen(t.getScreen());
                  template.setKeywords(t.getKeywords());
                  template.setShared(t.isShared());
                  if (t.isShared() == shareType) {
                     templateTree.getStore().update(template.getBeanModel());
                  } else {
                     templateTree.getStore().remove(template.getBeanModel());
                     BeanModel parentNode = template.isShared()?publicTemplateTopNode:privateTemplateTopNode;
                     templateTree.getStore().add(parentNode, template.getBeanModel(),false);
                  }
//                  if ( editTabItem != null) {
//                     try{templateEditTab.remove(editTabItem);}catch(RuntimeException e){}
//                  } 
                  editTabItem = new ScreenTab(template.getScreen());
//                  editTabItem.setText("Template: "+templateInEditing.getName());
                  templateEditPanel.setScreenItem(editTabItem);
               }

            });

         }
      });
      return editTempalteMenuItem;
   }
   private Button createNewTemplateMenuItem() {
      Button newPanelItem = new Button("New");
      newPanelItem.setIcon(icon.add());
      newPanelItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            List<BeanModel> topNode = templateTree.getStore().getRootItems();
            TreeFolderBean tmpPublicTemplateParentNode = topNode.get(0).getBean();
            TreeFolderBean tmpPrivateTemplateParentNode = topNode.get(1).getBean();
            for(BeanModel beanModel : topNode) {
               TreeFolderBean folderBean = beanModel.getBean();
               if(folderBean.getDisplayName().contains("Public") ) {
                  tmpPublicTemplateParentNode = folderBean;
               } else {
                  tmpPrivateTemplateParentNode = folderBean;
               }
            }
            final BeanModel privateTemplateTopNode = tmpPrivateTemplateParentNode.getBeanModel();
            final BeanModel publicTemplateTopNode = tmpPublicTemplateParentNode.getBeanModel();
            final TemplateCreateWindow templateCreateWindow = new TemplateCreateWindow();
            templateCreateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  Template template = be.getData();
                  if (template.isShared()) {
                     templateTree.getStore().add(publicTemplateTopNode, template.getBeanModel(),false);
                  } else {
                     templateTree.getStore().add(privateTemplateTopNode, template.getBeanModel(),false);
                  }
                  layout();
               }

            });

         }
      });
      return newPanelItem;
   }

   
   private void createTreeContainer() {
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(templateTree);
         }
         
      };
      treeContainer.ensureDebugId(DebugId.DEVICE_TREE_CONTAINER);
   // overflow-auto style is for IE hack.
      treeContainer.addStyleName("overflow-auto");
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
      
   }
   
   
   private void createAutoSaveTimer() {
      timer = new Timer() {
         @Override
         public void run() {
            saveTemplateUpdates();
         }
      };
      timer.scheduleRepeating(AUTO_SAVE_INTERVAL_MS);
      
   }
   
   private void saveTemplateUpdates() {
      if (templateInEditing != null) {
         TemplateProxy.updateTemplate(templateInEditing, new AsyncCallback<Template>(){

            @Override
            public void onFailure(Throwable caught) {
               Info.display("Error","Update template: "+templateInEditing.getName()+"failed");
            }

            @Override
            public void onSuccess(Template result) {
               templateInEditing.setContent(result.getContent());
               templateInEditing.setScreen(result.getScreen());
               Info.display("Success", "auto save template" + templateInEditing.getName()+" successfully !");
            }
            
         });
      }
   }

   public Template getTemplateInEditing() {
      return templateInEditing;
   }

   public void setTemplateInEditing(final Template templateInEditing) {
      if (editTabItem != null) {
         //reopen template tab item close by user. 
         if (templateEditPanel.indexOf(editTabItem) == -1 ) {
            templateEditPanel.setScreenItem(editTabItem);
            templateInEditing.setScreen(templateInEditing.getScreen());
         }
      }
      if (templateInEditing != null &&templateInEditing.equals(this.templateInEditing)) return;
      
      if (this.templateInEditing != null) {
         //-----------------------------
         // 1, save previous template.
         //------------------------------
         TemplateProxy.updateTemplate(this.templateInEditing, new AsyncCallback<Template>() {

            @Override
            public void onFailure(Throwable caught) {
               Info.display("Error", "Update template: " + TemplatePanel.this.templateInEditing.getName() + " failed");
            }

            @Override
            public void onSuccess(Template result) {
               //--------------------------
               // 2, edit another template.
               //--------------------------
               buildScreen(templateInEditing);
               Info.display("Success", "auto save template" + templateInEditing.getName() + " successfully !");
            }

            
         });
      } else {
         buildScreen(templateInEditing);
      }
      

      //this.templateInEditing = templateInEditing;

   }
   
   private void buildScreen(final Template templateInEditing) {
      TemplateProxy.buildScreen(templateInEditing, new AsyncCallback<ScreenPair>() {

         @Override
         public void onFailure(Throwable caught) {
            MessageBox.alert("Error", "Failed to preview Template: " + templateInEditing.getName(), null);
         }

         @Override
         public void onSuccess(ScreenPair screen) {
            // try to close previous template editing tab item.
//            if (editTabItem != null) {
//               try {
//                  templateEditTab.remove(editTabItem);
//               } catch (RuntimeException e) {
//               }
//            }
            editTabItem = new ScreenTab(screen);
//            editTabItem.setText("Template: " + templateInEditing.getName());
            templateEditPanel.setScreenItem(editTabItem);
            templateInEditing.setScreen(screen);
            TemplatePanel.this.templateInEditing = templateInEditing;
         }

      });
   }

}
