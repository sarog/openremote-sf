package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.SliderTree;
import org.openremote.modeler.domain.Slider;

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

public class SliderPanel extends ContentPanel {
   private Icons icons = GWT.create(Icons.class);
   private Button newBtn = new Button("New");
   private Button editBtn = new Button("Edit");
   private Button delBtn = new Button("Delete");
   private TreePanel <BeanModel> sliderTree = null;
   
   private SelectionServiceExt<BeanModel> selectionService;
   
   public SliderPanel(){
      this.setHeading("Slider");
      this.setIcon(icons.sliderIcon());
      selectionService = new SelectionServiceExt<BeanModel>();
      

      createMenu();
      createSliderTree();
      layout();
   }
   
   private void createMenu(){
      ToolBar sliderToolBar = new ToolBar();
      newBtn.setToolTip("Create a slider");
      newBtn.setIcon(icons.sliderAddIcon());
      
      editBtn.setToolTip("Edit the slider you select");
      editBtn.setIcon(icons.sliderEditIcon());
      editBtn.setEnabled(false);
      
      delBtn.setToolTip("Delete the slider you select");
      delBtn.setIcon(icons.sliderDeleteIcon());
      delBtn.setEnabled(false);
      
      sliderToolBar.add(newBtn);
      sliderToolBar.add(editBtn);
      sliderToolBar.add(delBtn);
      
      makeCreateAndDeleteControlable();
      add(sliderToolBar);
      
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
            if (selectModel.getBean() instanceof Slider) {
               return true;
            }
            return false;
         }
      });
   }

   private void createSliderTree(){
      this.sliderTree = SliderTree.buildsliderTree();
      selectionService.addListener(new SourceSelectionChangeListenerExt(sliderTree.getSelectionModel()));
      selectionService.register(sliderTree.getSelectionModel());
      add(sliderTree);
   }
   

   class NewSwitchListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         final SliderWindow sliderWindow = new SliderWindow(null);
         sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){

            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel sliderBeanModel = be.getData();
               Slider slider = sliderBeanModel.getBean();
               sliderTree.getStore().add(sliderBeanModel, true);
               sliderTree.getStore().add(sliderBeanModel, slider.getSetValueCmd().getBeanModel(), false);
               System.out.println("add slider success...........");
               sliderWindow.hide();
            }
            
         });
         sliderWindow.show();
      }
   }
   
   class EditSwitchListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         BeanModel selectedSwitchBean = sliderTree.getSelectionModel().getSelectedItem();
         Slider slider = selectedSwitchBean.getBean();
         
         final SliderWindow sliderWindow = new SliderWindow(slider);
         sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){

            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel sliderBeanModel = be.getData();
               Slider slider = sliderBeanModel.getBean();
               sliderTree.getStore().update(sliderBeanModel);
               sliderTree.getStore().removeAll(sliderBeanModel);
               sliderTree.getStore().add(slider.getBeanModel(),slider.getSetValueCmd().getBeanModel(),false);
               
               sliderTree.setExpanded(sliderBeanModel, true);
               System.out.println("update slider success...........");
               sliderWindow.hide();
            }
            
         });
         sliderWindow.show();
      }
   }
   
   class DeleteSwitchListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         final BeanModel selectedSwitchBean = sliderTree.getSelectionModel().getSelectedItem();
         MessageBox box = new MessageBox();
         box.setButtons(MessageBox.YESNO);
         box.setIcon(MessageBox.QUESTION);
         box.setTitle("Delete");
         box.setMessage("Are you sure you want to delete?");
         box.addCallback(new Listener<MessageBoxEvent>() {

             public void handleEvent(MessageBoxEvent be) {
                 if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                    SliderBeanModelProxy.delete(selectedSwitchBean, new AsyncSuccessCallback<Void>(){
                     @Override
                     public void onSuccess(Void result) {
                        sliderTree.getStore().remove(selectedSwitchBean);
                     }
                       
                    });
                 }
             }
         });
         box.show();
      }
   }

   public TreePanel<BeanModel> getSliderTree() {
      return sliderTree;
   }
}
