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
import org.openremote.modeler.client.widget.uidesigner.ChangeIconWindow;
import org.openremote.modeler.client.widget.uidesigner.SelectSliderWindow;
import org.openremote.modeler.domain.Slider;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;

public class SliderPropertyForm extends PropertyForm {
   private ScreenSlider screenSlider = null;
   public SliderPropertyForm(ScreenSlider screenSlider) {
      super();
      this.screenSlider = screenSlider;
      setLabelWidth(100);
      addFields();
   }
   
   private void addFields() {
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
                  screenSlider.setSlider(slider);
                  command.setText(slider.getName());
               }
            });
         }
      });
      if (screenSlider.getSlider() != null) {
         command.setText(screenSlider.getSlider().getName());
      }
      AdapterField adapterCommand = new AdapterField(command);
      adapterCommand.setFieldLabel("SliderCommand");

      Button minImageBtn = new Button("Select");
      minImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(screenSlider, null);
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String minImageUrl = be.getData();
                  screenSlider.setMinImage(minImageUrl);
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
                  screenSlider.setMinTrackImage(minTrackImageUrl);
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
                  screenSlider.setThumbImage(thumbImageUrl);
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
                  screenSlider.setMaxImage(maxImageUrl);
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
                  screenSlider.setMaxTrackImage(maxTrackImageUrl);
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMaxTrackImageBtn = new AdapterField(maxTrackImageBtn);
      adapterMaxTrackImageBtn.setFieldLabel("TrackImage(max)");

      add(adapterCommand);
      add(adapterMinImageBtn);
      add(adapterMinTrackImageBtn);
      add(adapterThumbImageBtn);
      add(adapterMaxTrackImageBtn);
      add(adapterMaxImageBtn);
   }
}
