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
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.widget.IconPreviewWidget;
import org.openremote.modeler.client.widget.component.ScreenSlider;
import org.openremote.modeler.client.widget.uidesigner.ChangeIconWindow;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
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
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SliderPropertyForm extends PropertyForm {
   private ScreenSlider screenSlider = null;
   public SliderPropertyForm(ScreenSlider screenSlider) {
      super(screenSlider);
      this.screenSlider = screenSlider;
      setLabelWidth(100);
      addFields();
      super.addDeleteButton();
   }
   
   private void addFields() {
      final CheckBox vertical = new CheckBox();
      vertical.setValue(false);
      vertical.setFieldLabel("Vertical");
      vertical.setValue(screenSlider.isVertical());
      vertical.setStyleName("left:0px");
      vertical.addListener(Events.Change, new Listener<FieldEvent>() {

         @Override
         public void handleEvent(FieldEvent be) {
            final boolean isVertical = vertical.getValue();
            
            if(isVertical != screenSlider.getUiSlider().isVertical()) {
               UtilsProxy.roteImages(screenSlider.getUiSlider(), new AsyncCallback<UISlider>(){

                  @Override
                  public void onFailure(Throwable caught) {
                     Info.display("Error", "falid to rotate images");
                  }

                  @Override
                  public void onSuccess(UISlider result) {
                     screenSlider.setMinImage(result.getMinImage().getSrc());
                     screenSlider.setMinTrackImage(result.getMinTrackImage().getSrc());
                     screenSlider.setThumbImage(result.getThumbImage().getSrc());
                     screenSlider.setMaxTrackImage(result.getMaxTrackImage().getSrc());
                     screenSlider.setMaxImage(result.getMaxImage().getSrc());
                     screenSlider.setVertical(isVertical);
                     screenSlider.getScreenCanvas().layout();
                  }
                  
               });
            }
         }
         
      });
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

      final Button minImageBtn = new Button("Select");
      if (screenSlider.isMinImageUploaded()) {
         minImageBtn.setText(screenSlider.getUiSlider().getMinImage().getImageFileName());
      }
      minImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(createIconPreviewWidget(screenSlider.getUiSlider().getMinImage()), screenSlider.getWidth());
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String minImageUrl = be.getData();
                  screenSlider.setMinImage(minImageUrl);
                  minImageBtn.setText(screenSlider.getUiSlider().getMinImage().getImageFileName());
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMinImageBtn = new AdapterField(minImageBtn);
      adapterMinImageBtn.setFieldLabel("MinImage");

      final Button minTrackImageBtn = new Button("Select");
      if (screenSlider.isMinTrackImageUploaded()) {
         minTrackImageBtn.setText(screenSlider.getUiSlider().getMinTrackImage().getImageFileName());
      }
      minTrackImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(createIconPreviewWidget(screenSlider.getUiSlider().getMinTrackImage()), screenSlider.getWidth());
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String minTrackImageUrl = be.getData();
                  screenSlider.setMinTrackImage(minTrackImageUrl);
                  minTrackImageBtn.setText(screenSlider.getUiSlider().getMinTrackImage().getImageFileName());
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMinTrackImageBtn = new AdapterField(minTrackImageBtn);
      adapterMinTrackImageBtn.setFieldLabel("TrackImage(min)");

      final Button thumbImageBtn = new Button("Select");
      if (screenSlider.isThumbUploaded()) {
         thumbImageBtn.setText(screenSlider.getUiSlider().getThumbImage().getImageFileName());
      }
      thumbImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(createIconPreviewWidget(screenSlider.getUiSlider().getThumbImage()), screenSlider.getWidth());
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String thumbImageUrl = be.getData();
                  screenSlider.setThumbImage(thumbImageUrl);
                  thumbImageBtn.setText(screenSlider.getUiSlider().getThumbImage().getImageFileName());
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterThumbImageBtn = new AdapterField(thumbImageBtn);
      adapterThumbImageBtn.setFieldLabel("ThumbImage");

      final Button maxImageBtn = new Button("Select");
      if (screenSlider.isMaxImageUploaded()) {
         maxImageBtn.setText(screenSlider.getUiSlider().getMaxImage().getImageFileName());
      }
      maxImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(createIconPreviewWidget(screenSlider.getUiSlider().getMaxImage()), screenSlider.getWidth());
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String maxImageUrl = be.getData();
                  screenSlider.setMaxImage(maxImageUrl);
                  maxImageBtn.setText(screenSlider.getUiSlider().getMaxImage().getImageFileName());
                  screenSlider.layout();
               }
            });
         }
      });
      AdapterField adapterMaxImageBtn = new AdapterField(maxImageBtn);
      adapterMaxImageBtn.setFieldLabel("MaxImage");

      final Button maxTrackImageBtn = new Button("Select");
      if (screenSlider.isMaxTrackImageUploaded()) {
         maxTrackImageBtn.setText(screenSlider.getUiSlider().getMaxTrackImage().getImageFileName());
      }
      maxTrackImageBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ChangeIconWindow selectImageONWindow = new ChangeIconWindow(createIconPreviewWidget(screenSlider.getUiSlider().getMaxTrackImage()), screenSlider.getWidth());
            selectImageONWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  String maxTrackImageUrl = be.getData();
                  screenSlider.setMaxTrackImage(maxTrackImageUrl);
                  maxTrackImageBtn.setText(screenSlider.getUiSlider().getMaxTrackImage().getImageFileName());
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
      add(adapterMaxTrackImageBtn);
      add(adapterMaxImageBtn);
   }

   /**
    * @param imageSource
    * @return IconPreviewWidget
    */
   private IconPreviewWidget createIconPreviewWidget(ImageSource imageSource) {
      IconPreviewWidget previewWidget = new IconPreviewWidget(screenSlider.getWidth(), screenSlider.getHeight());
      if (imageSource != null) {
         previewWidget.setIcon(imageSource.getSrc());
      } else {
         previewWidget.setIcon(null);
      }
      return previewWidget;
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Slider properties");
   }
}
