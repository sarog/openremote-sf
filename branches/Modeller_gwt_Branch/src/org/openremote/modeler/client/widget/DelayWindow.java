package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.domain.CommandDelay;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

public class DelayWindow extends Window {
   
   public static final String DELAY = "delay";
   
   private FormPanel form = new FormPanel();
   private BeanModel commandDelayModel = null;
   public DelayWindow() {
      initial("Add Delay");
      show();
   }
   
   public DelayWindow(BeanModel commandDelayModel){
      this.commandDelayModel = commandDelayModel;
      initial("Edit Delay");
      show();
   }
   
   private void initial(String Heading){
      setHeading("Add Delay");
      setLayout(new FillLayout());
      setModal(true);
      setBodyBorder(false);
      setSize(280, 120);
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      
      form.setButtonAlign(HorizontalAlignment.CENTER);

      Button addBtn = new Button("Add");
      
      addBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               form.submit();
            }
         }

      });
      
      form.addButton(addBtn);
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            String delay = list.get(0).getValue().toString();
            if(commandDelayModel == null){
               CommandDelay commandDelay = new CommandDelay(delay);
               commandDelayModel = commandDelay.getBeanModel();
            }else{
               commandDelayModel.set("delaySecond", delay);
            }
            fireEvent(SubmitEvent.Submit, new SubmitEvent(commandDelayModel));
         }
         
      });
      createField();
      add(form);
   }
   
   private void createField(){
      NumberField delayField = new NumberField();
      delayField.setName(DELAY);
      delayField.setFieldLabel("Delay(s)");
      delayField.setAllowBlank(false);
      delayField.setAutoWidth(true);
      if(commandDelayModel != null){
         delayField.setValue(Float.valueOf(commandDelayModel.get("delaySecond").toString()));
      }
      form.add(delayField);
   }
}
