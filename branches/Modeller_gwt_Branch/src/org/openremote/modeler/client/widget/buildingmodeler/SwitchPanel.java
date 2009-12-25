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
import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.SwitchBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Switch;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

/**
 * 
 * @author Javen
 *
 */
public class SwitchPanel extends ContentPanel {
   private Icons icons = GWT.create(Icons.class);
   private Button newBtn = new Button("New");
   private Button editBtn = new Button("Edit");
   private Button delBtn = new Button("Delete");
   private TreePanel <BeanModel> switchTree = null;
   
   private SelectionServiceExt<BeanModel> selectionService;
   
   public SwitchPanel(){
      this.setHeading("Switch");
      this.setIcon(icons.switchIcon());
      selectionService = new SelectionServiceExt<BeanModel>();
      

      createMenu();
      createSwitchsTree();
   }
   
   private void createMenu(){
      ToolBar switchToolBar = new ToolBar();
      newBtn.setToolTip("Create a switch");
      newBtn.setIcon(icons.switchAddIcon());
      
      editBtn.setToolTip("Edit the switch you select");
      editBtn.setIcon(icons.switchEditIcon());
      editBtn.setEnabled(false);
      
      delBtn.setToolTip("Delete the switch you select");
      delBtn.setIcon(icons.switchDeleteIcon());
      delBtn.setEnabled(false);
      
      switchToolBar.add(newBtn);
      switchToolBar.add(editBtn);
      switchToolBar.add(delBtn);
      
      makeCreateAndDeleteControlable();
      add(switchToolBar);
//      add(createSwitchsTree());
      
      newBtn.addSelectionListener(new NewSwitchListener());
      editBtn.addSelectionListener(new EditSwitchListener());
      delBtn.addSelectionListener(new DeleteSwitchListener());
   }
   
   private void makeCreateAndDeleteControlable() {
      List<Button> btns = new ArrayList<Button>();
      btns.add(this.delBtn);
      btns.add(this.editBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(btns) {
         @Override
         protected boolean isEditableAndDeletable(List<BeanModel> sels) {
            if(sels.size()>1){
               return false;
            }
            BeanModel selectModel = sels.get(0);
            if (selectModel.getBean() instanceof Switch) {
               return true;
            }
            return false;
         }
      });
   }

   private void createSwitchsTree(){
      this.switchTree = TreePanelBuilder.buildSwitchTree(this);
      selectionService.addListener(new SourceSelectionChangeListenerExt(switchTree.getSelectionModel()));
      selectionService.register(switchTree.getSelectionModel());
      add(switchTree);
   }
   

   class NewSwitchListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         final SwitchWindow switchWindow = new SwitchWindow(null);
         switchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){

            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel switchBeanModel = be.getData();
               Switch swh = switchBeanModel.getBean();
               switchTree.getStore().add(switchBeanModel, true);
               switchTree.getStore().add(switchBeanModel, swh.getOnDeviceCommandRef().getBeanModel(), false);
               switchTree.getStore().add(switchBeanModel, swh.getOffDeviceCommandRef().getBeanModel(), false);
               System.out.println("add switch success...........");
               switchWindow.hide();
            }
            
         });
         switchWindow.show();
      }
   }
   
   class EditSwitchListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         BeanModel selectedSwitchBean = switchTree.getSelectionModel().getSelectedItem();
         Switch switchToggle = selectedSwitchBean.getBean();
         
         final SwitchWindow switchWindow = new SwitchWindow(switchToggle);
         switchWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){

            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel switchBeanModel = be.getData();
               Switch swh = switchBeanModel.getBean();
               switchTree.getStore().update(switchBeanModel);
               switchTree.getStore().removeAll(switchBeanModel);
               switchTree.getStore().add(swh.getBeanModel(),swh.getOnDeviceCommandRef().getBeanModel(),false);
               switchTree.getStore().add(swh.getBeanModel(),swh.getOffDeviceCommandRef().getBeanModel(),false);
               
               switchTree.setExpanded(switchBeanModel, true);
               System.out.println("update switch success...........");
               switchWindow.hide();
            }
            
         });
         switchWindow.show();
      }
   }
   
   class DeleteSwitchListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         final BeanModel selectedSwitchBean = switchTree.getSelectionModel().getSelectedItem();
         MessageBox box = new MessageBox();
         box.setButtons(MessageBox.YESNO);
         box.setIcon(MessageBox.QUESTION);
         box.setTitle("Delete");
         box.setMessage("Are you sure you want to delete?");
         box.addCallback(new Listener<MessageBoxEvent>() {

             public void handleEvent(MessageBoxEvent be) {
                 if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                    SwitchBeanModelProxy.delete(selectedSwitchBean, new AsyncSuccessCallback<Void>(){
                     @Override
                     public void onSuccess(Void result) {
                        switchTree.getStore().remove(selectedSwitchBean);
                     }
                       
                    });
                 }
             }
         });
         box.show();
      }
   }
}
