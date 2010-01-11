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
package org.openremote.modeler.client.widget.propertyform;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.widget.component.ScreenSlider;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.ChangeIconWindow;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.SelectSliderWindow;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISlider;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;

public class SliderPropertyForm extends PropertyForm {

   public SliderPropertyForm(ScreenSlider screenSlider) {
      super();
      setLabelWidth(100);
      addFields(screenSlider);
   }
   
   private void addFields(final ScreenSlider screenSlider) {
      final UISlider uiSlider = screenSlider.getUiSlider();
      
      CheckBoxGroup vertical = new CheckBoxGroup();
      vertical.setFieldLabel("Vertical");
      final CheckBox check = new CheckBox();
      check.setValue(uiSlider.isVertical());
      check.addListener(Events.Blur, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            uiSlider.setVertical(check.getValue());
            screenSlider.setVertical(check.getValue());
            ComponentContainer container = (ComponentContainer)screenSlider.getParent();
            if (container instanceof AbsoluteLayoutContainer) {
               ((AbsoluteLayoutContainer)container).setSize(screenSlider.getWidth(), screenSlider.getHeight());
            } else {
               container.setSize(screenSlider.getWidth(), screenSlider.getHeight());
            }
            screenSlider.layout();
         }
      });
      vertical.add(check);
      
      final Button command = new Button("Select");
      command.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            SelectSliderWindow selectSliderWindow = new SelectSliderWindow();
            selectSliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  BeanModel dataModel = be.<BeanModel> getData();
                  Slider slider = dataModel.getBean();
                  uiSlider.setSlider(slider);
                  command.setText(slider.getName());
               }
            });
         }
      });
      if (uiSlider.getSlider() != null) {
         command.setText(uiSlider.getSlider().getName());
      }
      AdapterField adapterCommand = new AdapterField(command);
      adapterCommand.setFieldLabel("Command");
      
      Button minImageBtn = new Button("Select");
      minImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSlider, null);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String minImageUrl = be.getData();
                  ImageSource minImage = new ImageSource(minImageUrl);
                  uiSlider.setMinImage(minImage);
                  screenSlider.getSlider().setMinImageUrl(minImageUrl);
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMinImageBtn = new AdapterField(minImageBtn);
      adapterMinImageBtn.setFieldLabel("MinImage");
      
      Button minTrackImageBtn = new Button("Select");
      minTrackImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSlider, null);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String minTrackImageUrl = be.getData();
                  ImageSource minTrackImage = new ImageSource(minTrackImageUrl);
                  uiSlider.setMinTrackImage(minTrackImage);
                  screenSlider.getSlider().setMinTrackImageUrl(minTrackImageUrl);
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMinTrackImageBtn = new AdapterField(minTrackImageBtn);
      adapterMinTrackImageBtn.setFieldLabel("TrackImage(min)");
      
      Button thumbImageBtn = new Button("Select");
      thumbImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSlider, null);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String thumbImageUrl = be.getData();
                  uiSlider.setThumbImage(new ImageSource(thumbImageUrl));
                  screenSlider.getSlider().setThumbImageUrl(thumbImageUrl);
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterThumbImageBtn = new AdapterField(thumbImageBtn);
      adapterThumbImageBtn.setFieldLabel("ThumbImage");
      
      Button maxImageBtn = new Button("Select");
      maxImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSlider, null);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String maxImageUrl = be.getData();
                  uiSlider.setMaxImage(new ImageSource(maxImageUrl));
                  screenSlider.getSlider().setMaxImageUrl(maxImageUrl);
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMaxImageBtn = new AdapterField(maxImageBtn);
      adapterMaxImageBtn.setFieldLabel("MaxImage");
      
      Button maxTrackImageBtn = new Button("Select");
      maxTrackImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSlider, null);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String maxTrackImageUrl = be.getData();
                  uiSlider.setMaxTrackImage(new ImageSource(maxTrackImageUrl));
                  screenSlider.getSlider().setMaxTrackImageUrl(maxTrackImageUrl);
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMaxTrackImageBtn = new AdapterField(maxTrackImageBtn);
      adapterMaxTrackImageBtn.setFieldLabel("TrackImage(max)");
      
      add(vertical);
      add(adapterCommand);
      add(adapterMinImageBtn);
      add(adapterMinTrackImageBtn);
      add(adapterThumbImageBtn);
      add(adapterMaxImageBtn);
      add(adapterMaxTrackImageBtn);
   }
}
