package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.utils.SliderTree;
import org.openremote.modeler.domain.Slider;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class SelectSliderWindow extends Dialog {


   private TreePanel<BeanModel> sliderTree;
   public SelectSliderWindow() {
      setHeading("Select Slider");
      setMinHeight(260);
      setWidth(200);
      setLayout(new FitLayout());
      setModal(true);
      initSwitchTree();
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      addButtonListener();
      show();
   }

   private void initSwitchTree() {
      ContentPanel sliderTreeContainer = new ContentPanel();
      sliderTreeContainer.setBorders(false);
      sliderTreeContainer.setBodyBorder(false);
      sliderTreeContainer.setHeaderVisible(false);
      if (sliderTree == null) {
         sliderTree = SliderTree.getInstance();
         sliderTreeContainer.add(sliderTree);
      }
      sliderTree.getSelectionModel().deselectAll();
      sliderTreeContainer.setScrollMode(Scroll.AUTO);
      add(sliderTreeContainer);
   }
   
   private void addButtonListener() {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               BeanModel beanModel = sliderTree.getSelectionModel().getSelectedItem();
               if (beanModel == null) {
                  MessageBox.alert("Error", "Please select a switch.", null);
                  be.cancelBubble();
               } else {
                  if (beanModel.getBean() instanceof Slider) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(beanModel));
                  } else {
                     MessageBox.alert("Error", "Please select a switch.", null);
                     be.cancelBubble();
                  }
               }
            }
         }
      }); 
   }


}
