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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.utils.SensorTree;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;

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

public class SelectSensorWindow extends Dialog {

   private TreePanel<BeanModel> sensorTree;
   public SelectSensorWindow() {
      setHeading("Select Sensor");
      setMinHeight(260);
      setWidth(200);
      setLayout(new FitLayout());
      setModal(true);
      initSensorTree();
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      addButtonListener();
      show();
   }

   private void initSensorTree() {
      ContentPanel sensorTreeContainer = new ContentPanel();
      sensorTreeContainer.setBorders(false);
      sensorTreeContainer.setBodyBorder(false);
      sensorTreeContainer.setHeaderVisible(false);
      if (sensorTree == null) {
         sensorTree = SensorTree.getInstance();
         sensorTreeContainer.add(sensorTree);
      }
      sensorTree.getSelectionModel().deselectAll();
      sensorTreeContainer.setScrollMode(Scroll.AUTO);
      add(sensorTreeContainer);
   }
   
   private void addButtonListener() {
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               BeanModel beanModel = sensorTree.getSelectionModel().getSelectedItem();
               if(beanModel.getBean() instanceof CustomSensor){
               }
               if (beanModel == null) {
                  MessageBox.alert("Error", "Please select a sensor.", null);
                  be.cancelBubble();
               } else {
                  if (beanModel.getBean() instanceof Sensor) {
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(beanModel));
                  } else {
                     MessageBox.alert("Error", "Please select a sensor.", null);
                     be.cancelBubble();
                  }
               }
            }
         }
      }); 
   }

}
