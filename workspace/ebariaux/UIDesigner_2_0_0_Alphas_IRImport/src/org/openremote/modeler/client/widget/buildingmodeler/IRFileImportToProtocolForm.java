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
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;


/**
 * The window creates or updates a deviceCommand into server.
 */
public class IRFileImportToProtocolForm extends FormWindow {
   
   /** The Constant DEVICE_COMMAND_NAME. */
   public static final String DEVICE_COMMAND_NAME = "device_command_name";
   
   /** The Constant DEVICE_COMMAND_PROTOCOL. */
   public static final String DEVICE_COMMAND_PROTOCOL = "protocol";
   
   /** The device command. */
   private DeviceCommand deviceCommand = null;
   
   /** The device. */
   private Device device = null;
   private FlowPanel gCPanel;
   private TextField<String> ip;
   private TextField<String> tcpPort;
   protected boolean hideWindow = true;
   
   protected LabelField info;
   protected static final String INFO_FIELD = "infoField";
   /**
    * Instantiates a new device command window.
    * 
    * @param device the device
    */
   public IRFileImportToProtocolForm(Device device) {
      super();
      this.device = device;
      setHeading("New command");
      initial();
      show();
   }
   
  
   /**
    * Initial.
    */
   private void initial() {
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      
      
      form.setWidth(370);

      Button submitBtn = new Button("Submit");
      form.addButton(submitBtn);

      submitBtn.addSelectionListener(new FormSubmitListener(form, submitBtn));
      
      
      Button resetButton = new Button("Reset");
      resetButton.addSelectionListener(new FormResetListener(form));
      form.addButton(resetButton);
      
      
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {

		@Override
		public void handleEvent(FormEvent be) {
			// TODO Auto-generated method stub
			
		}});
      createFields();
      add(form);
   }
   
   /**
    * Creates the fields.
    * 
    * @param protocols the protocols
    */
   private void createFields() {
      final ListBox product = new ListBox();
      product.addItem("GlobalCaché	");
      product.addItem("IRTrans");
      form.add(product);
      gCPanel = new FlowPanel();
      ip = new TextField<String>();
      ip.setFieldLabel(new String("Ip address Host name"));
      tcpPort = new TextField<String>();
      tcpPort.setValue("4998");
      tcpPort.setFieldLabel(new String("TCP Port"));
      gCPanel.add(ip);
      gCPanel.add(tcpPort);
      
      gCPanel.setVisible(false);
      form.add(gCPanel);     
      
      product.addChangeHandler(new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			if(product.getSelectedIndex()==0){
			      gCPanel.setVisible(false);
				
				
			}else{
				gCPanel.setVisible(true);
			}
			
		}
	});


      form.layout();
   }
   
  
}
